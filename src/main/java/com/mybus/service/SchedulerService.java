package com.mybus.service;

import com.mybus.SystemProperties;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.CargoBookingDailyTotalsDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.impl.*;
import com.mybus.model.*;
import com.mybus.util.EmailSender;
import com.mybus.util.SendTaxInvoice;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private VehicleMongoDAO vehicleMongoDAO;

    @Autowired
    private CargoBookingDailyTotalsDAO cargoBookingDailyTotals;

    @Autowired
    private CargoBookingMongoDAO cargoBookingMongoDAO;


    @Autowired
    private EmailSender emailSender;

    @Autowired
    private VelocityEngineService velocityEngineService;

    @Autowired
    private ServiceReportMongoDAO serviceReportMongoDAO;

    @Autowired
    private ServiceReportsManager serviceReportsManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    @Autowired
    private OperatorAccountMongoDAO operatorAccountMongoDAO;
    @Autowired
    private BookingMongoDAO bookingMongoDAO;
    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    private SendTaxInvoice sendTaxInvoice;

    @Scheduled(cron = "0 0 3 * * *")
    //@Scheduled(fixedDelay = 50000)
    public void checkExpiryDates () {
        logger.info("checking expiry date..." + systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        int buffer = Integer.parseInt(systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - buffer));
        List<OperatorAccount> operatorAccounts = IteratorUtils.toList(operatorAccountDAO.findAll().iterator());
        for(OperatorAccount operatorAccount:operatorAccounts) {
            if (operatorAccount.getEmail() != null) {
                List<Vehicle> vehicles = IteratorUtils.toList(vehicleMongoDAO.findExpiring(calendar.getTime()).iterator());
                if (!vehicles.isEmpty()) {
                    Map<String, Object> context = new HashMap<>();
                    context.put("permitExpiring", vehicles.stream().filter(v -> v.getPermitExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("fitnessExpiring", vehicles.stream().filter(v -> v.getFitnessExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("authExpiring", vehicles.stream().filter(v -> v.getAuthExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("pollutionExpiring", vehicles.stream().filter(v -> v.getPollutionExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    context.put("insuranceExpiring", vehicles.stream().filter(v -> v.getInsuranceExpiry().isBefore(calendar.getTime().getTime())).collect(Collectors.toList()));
                    String content = velocityEngineService.trasnform(context, VelocityEngineService.EXPIRING_DOCUMENTS_TEMPLATE);
                    logger.info("Sending email for notifying expiring documents ...");
                    emailSender.sendExpiringNotifications(content, operatorAccount.getEmail());
                }
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    //@Scheduled(fixedDelay = 50000)
    public void checkServiceReportsReview () throws ParseException {
        List<OperatorAccount> operatorAccounts = IteratorUtils.toList(operatorAccountDAO.findAll().iterator());
        for(OperatorAccount operatorAccount:operatorAccounts){
            if(operatorAccount.getEmail() != null) {
                Map<String, Object> context = new HashMap<>();
                List<ServiceReport> reports = IteratorUtils.toList(serviceReportMongoDAO.findPendingReports(null, operatorAccount.getId()).iterator());
                if(!reports.isEmpty()) {
                    context.put("pendingReports", reports);
                }
                reports = IteratorUtils.toList(serviceReportMongoDAO.findReportsToBeReviewed(null, operatorAccount.getId()).iterator());
                if(!reports.isEmpty()) {
                    context.put("reportsToBeReviewed", reports);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -3);
                reports = IteratorUtils.toList(serviceReportMongoDAO.findHaltedReports(calendar.getTime(), operatorAccount.getId()).iterator());
                if(!reports.isEmpty()) {
                    context.put("haltedReports", reports);
                }
                if(!context.isEmpty()) {
                    String content = velocityEngineService.trasnform(context, VelocityEngineService.PENDING_SERVICEREPORTS_TEMPLATE);
                    emailSender.sendServiceReportsToBeReviewed(content, operatorAccount.getEmail());
                }
            }
        }
    }

   // @Scheduled(cron = "0 0 1 * * *")
    //@Scheduled(fixedDelay = 50000)
    public void downloadServiceReports () throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
        serviceReportsManager.downloadReports(ServiceUtils.formatDate(calendar.getTime()));
    }


    @Scheduled(cron = "*/10 * * * * *")
//    @Scheduled(fixedDelay = 5000)
    public void sendTaxInvoice() {
        if(systemProperties.getBooleanProperty(SystemProperties.SysProps.SEND_EMAIL_ENABLED)){
            List<OperatorAccount> operatorAccounts = operatorAccountMongoDAO.getOperatorAccountsSendEmailTrue();
            operatorAccounts.stream().forEach(operatorAccount -> {
                logger.info("Sending tax invoice to bookings in account ", operatorAccount.getDomainName());
                List<Booking> bookings = bookingMongoDAO.findBookingsForTaxInvoice(operatorAccount.getId());
                List<String> ids = new ArrayList<>();
                bookings.parallelStream().forEach(booking -> {
                    //if(booking.getServiceTax() > 0) {
                        //send email to
                        sendTaxInvoice.emailTaxInvoice(booking, "bookingTaxInvoice.html");
                        ids.add(booking.getId());
                    //}
                });
                bookingMongoDAO.updateEmailedTaxInvoice(ids);
            });
        }
    }


    @Scheduled(cron = "*/10 * * * * *")
    public void sendNewYearGreeting() {
        if(systemProperties.getBooleanProperty(SystemProperties.SysProps.SEND_EMAIL_ENABLED)){
            List<OperatorAccount> operatorAccounts = operatorAccountMongoDAO.getOperatorAccountsSendEmailTrue();
            operatorAccounts.stream().forEach(operatorAccount -> {
                List<Booking> bookings = bookingMongoDAO.findBookingsForGreeting(operatorAccount.getId());List<String> ids = new ArrayList<>();
                bookings.parallelStream().forEach(booking -> {
                    sendTaxInvoice.emailGreeting(booking, "happyNewYear.html");
                    ids.add(booking.getId());
                });
                bookingMongoDAO.updateGreetingSent(ids);
            });
        }
    }
    @Scheduled(cron = "0 1 1 * * *")
    public void saveCargoBookingDailyTotals()throws ParseException{
            SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
        List<Document> results= cargoBookingMongoDAO.groupByOffice();
        for(int i=0;i<results.size();i++){
            CargoBookingDailyTotals cb=new CargoBookingDailyTotals();
            cb.setBookingsTotal(results.get(i).getLong("bookingsTotal"));
            cb.setDate(formatter.parse(results.get(i).getString("date")));
            cb.setOfficeId(results.get(i).getString("officeId"));
            cargoBookingDailyTotals.save(cb);
        };

    }

}
