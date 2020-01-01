package com.mybus.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.mybus.model.Booking;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

@Service
public class SendTaxInvoice {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

//    private VelocityContext context;
    private XMLWorkerHelper xmlWorkerHelper;

    @PostConstruct
    public void init(){
        Velocity.init();
//        context = new VelocityContext();
        xmlWorkerHelper = XMLWorkerHelper.getInstance();
    }

    @Autowired
    private AmazonSESService sesService;

    public void emailTaxInvoice(Booking booking, String fileName) {
        try {
            String folderPath = "emailTemplates/srikrishna";
            InputStream invoiceStream = getClass().getClassLoader().getResourceAsStream(folderPath+"/" +fileName);
            InputStream messageBody = getClass().getClassLoader().getResourceAsStream(folderPath+"/thankYouForTraveling.html");
            String htmlContent = IOUtils.toString(invoiceStream, "UTF-8");
            String emailBody = IOUtils.toString(messageBody, "UTF-8");
            VelocityContext context =  new VelocityContext();
            context.put("ticketNo",booking.getTicketNo());
            context.put("bookedBy",booking.getBookedBy());
            context.put("source",booking.getSource());
            context.put("destination",booking.getSource());
            context.put("seats",booking.getSeats());
            context.put("boardingTime",booking.getBoardingTime());
            context.put("landmark",booking.getLandmark());
//            context.put("gst",booking.getGrossCollection());
            context.put("boardingPoint",booking.getBoardingPoint());
//            context.put("passengerAddress");
            context.put("journeyDate",booking.getJDate());
            context.put("serviceNumber",booking.getServiceNumber());
            context.put("NoOfPassengers",booking.getSeatsCount());
            context.put("Bus Type",booking.getServiceName());
            context.put("basicAmount",booking.getBasicAmount());
            context.put("tax", booking.getServiceTax());
            context.put("grossCollection",booking.getGrossCollection());
            context.put("TypeOfBooking",booking.getPaymentType());
            context.put("passengerName", booking.getName());

            StringWriter str = new StringWriter();
            Velocity.evaluate(context, str, fileName, htmlContent);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            InputStream is = new ByteArrayInputStream(str.toString().getBytes());
            xmlWorkerHelper.parseXHtml(writer, document, is);
            document.close();
            byte[] contents = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(contents);
            sesService.sendEmailWithAttachment("kalyanikandula0@gmail.com", emailBody, "Srikrishna Travels - Your tax invoice","Tax Invoice", inputStream);
            byteArrayOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void emailGreeting(Booking booking, String fileName) {
        try {
            String folderPath = "emailTemplates/srikrishna";
            InputStream messageBody = getClass().getClassLoader().getResourceAsStream(folderPath+"/"+ fileName);
            String emailBody = IOUtils.toString(messageBody, "UTF-8");
            sesService.sendEmail(booking.getEmailID(), emailBody, booking.getTicketNo() + "- Srikrishna Travels wishing you Happy New Year 2020");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}


