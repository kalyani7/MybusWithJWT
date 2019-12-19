package com.mybus.util;


import com.mybus.dao.InvoiceBookingDAO;
import com.mybus.model.InvoiceBooking;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ExcelParser {

    private static final Logger logger = LoggerFactory.getLogger(ExcelParser.class);

    @Autowired
    private InvoiceBookingDAO invoiceBookingDAO;


    public void parseInvoiceExcel(InputStream stream, String verificationId) throws IOException, InvalidFormatException {
        logger.info("Start excel parsing...");
        List<InvoiceBooking> invoiceBookings = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);
        int rowNo = 0;
        double value = 0.0;
        DataFormatter dataFormatter = new DataFormatter();
        String cellValue;
        Iterator<Row> rowIterator = sheet.rowIterator();
        boolean busCancellation = false;
        boolean apiCancellation = false;
        boolean exceptionalRefund = false;
        while (rowIterator.hasNext()) {
            InvoiceBooking invoiceBooking = new InvoiceBooking();
            int cellNo=0;
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if(cellNo == 0 && cell.getStringCellValue().equals("TIN")){
                    break;
                }
                if(cellNo == 0){
                    String string = cell.getStringCellValue();
                    if (string.endsWith("-Bus Cancelled")){
                        busCancellation = true;
                        apiCancellation = false;
                        exceptionalRefund = false;
                        break;
                    }else if(string.endsWith("-Exceptional Refund")){
                        busCancellation = false;
                        apiCancellation = false;
                        exceptionalRefund = true;
                        break;
                    }else if(string.endsWith("-Cancellation")){
                        busCancellation = false;
                        apiCancellation = true;
                        exceptionalRefund = false;
                        break;
                    }
                }if(cellNo >= 6){
                    cellValue = dataFormatter.formatCellValue(cell);
                    if (cellValue != null && !cellValue.isEmpty() && Pattern.compile( "[0-9]" ).matcher( cellValue ).find()) {
                        cellValue = cellValue.replace(",","");
                        value = Double.parseDouble(cellValue);
                    }
                }
                if(rowNo >= 2 && cellNo!=0){
                    switch(cellNo) {
                            case 1:
                                String string = cell.getStringCellValue();
                                if (string.contains("-")) {
                                    String[] parts = string.split("-");
                                    invoiceBooking.setTicketNo(parts[0]);
                                } else {
                                    invoiceBooking.setTicketNo(cell.getStringCellValue());
                                }
                                break;


                            case 2:
                                invoiceBooking.setDOE(cell.getStringCellValue());
                                break;
                            case 3:
                                invoiceBooking.setDOM(cell.getStringCellValue());
                                break;
                            case 4:
                                invoiceBooking.setRoute(cell.getStringCellValue());
                                break;
                            case 5:
                                invoiceBooking.setSeats(cell.getStringCellValue());
                                break;
                            case 6:
                                invoiceBooking.setTicketFare(value);
                                break;
                            case 7:
                                invoiceBooking.setAcBusGST(value);
                                break;
                            case 8:
                                invoiceBooking.setCommissionWithOutGst(value);
                                break;
                            case 9:
                                invoiceBooking.setCgstOnCommission(value);
                                break;
                            case 10:
                                invoiceBooking.setSgstOnCommission(value);
                                break;
                            case 11:
                                invoiceBooking.setIgstOnCommission(value);
                                break;
                            case 12:
                                invoiceBooking.setTotalCommissionIncludingGst(value);
                                break;
                            case 13:
                                invoiceBooking.setNetPayable(value);
                                break;
                            case 14:
                                invoiceBooking.setOperatorOffer(value);
                                break;
                            case 15:
                                invoiceBooking.setGdsCommision(value);
                                break;

                    }

                }
                cellNo = cellNo+1;
            }
            invoiceBooking.setBusCancellation(busCancellation);
            invoiceBooking.setApiCancellation(apiCancellation);
            invoiceBooking.setExceptionalRefund(exceptionalRefund);
            if(invoiceBooking.getTicketNo() != null && !invoiceBooking.getTicketNo().isEmpty() && !invoiceBooking.getTicketNo().equals("PNR")){
                invoiceBooking.setVerificationId(verificationId);
                invoiceBookings.add(invoiceBooking);
            }
            rowNo = rowNo+1;
        }
        invoiceBookingDAO.saveAll(invoiceBookings);
        workbook.close();
        logger.info("End excel parsing...");
    }

    /*public static void main(String[] args) throws IOException, InvalidFormatException {
        ExcelParser obj = new ExcelParser();
        long busCancellation = 0;
        long apiCancellation = 0 ;
        long exceptionalRefund= 0;
        List<InvoiceBooking> list = obj.parseInvoiceExcel("/home/kalyani/MyBusProject/mybus/src/main/resources/047671901-000082.xls");
        for (InvoiceBooking booking:list){
            if(booking.isBusCancellation()){
                busCancellation = busCancellation+1;
            }else if(booking.isApiCancellation()){
                apiCancellation = apiCancellation+1;
            }else if(booking.isExceptionalRefund()){
                exceptionalRefund = exceptionalRefund+1;
            }
        }
        *//*System.out.println("size......"+list.size());
        System.out.println("bus cancel......."+busCancellation+"api cancel....."+apiCancellation+"exceptional refund..."+exceptionalRefund);*//*
    }*/
}