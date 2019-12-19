package com.mybus.controller;

import com.mybus.annotations.RequiresAuthorizedUser;
import com.mybus.service.BookingManager;
import com.mybus.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/")
public class SchedulerController {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private BookingManager bookingManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "schedule/checkExpiringDocuments", method = RequestMethod.GET)
    @RequiresAuthorizedUser(value = false)
    public void checkExpiringDocuments(HttpServletRequest request) {
        logger.info("Checking expiring documents");
        schedulerService.checkExpiryDates();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "schedule/checkPendingDocuments", method = RequestMethod.GET)
    @RequiresAuthorizedUser(value = false)
    public void checkPendingForms(HttpServletRequest request) throws ParseException {
        logger.info("Checking pending documents");
        schedulerService.checkServiceReportsReview();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "schedule/sendTaxInvoice", method = RequestMethod.GET)
    @RequiresAuthorizedUser(value = false)
    public void sendTaxInvoice(HttpServletRequest request) {
        logger.info("Sending tax invoice");
        schedulerService.sendTaxInvoice();
    }

}
