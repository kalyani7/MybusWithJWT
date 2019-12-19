package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.BookingDAO;
import com.mybus.dao.ServiceReportDAO;
import com.mybus.dao.ServiceReportStatusDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by srinikandula on 2/20/17.
 */

public class AbhiBusPassengerReportServiceTest extends AbstractControllerIntegrationTest {
    @Autowired
    private AbhiBusPassengerReportService abhiBusPassengerReportService;

    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportStatusDAO serviceReportStatusDAO;

    @Autowired
    private BookingDAO bookingDAO;
    
    @Before
    @After
    public void cleanup() {
        serviceReportDAO.deleteAll();
        serviceReportStatusDAO.deleteAll();
        bookingDAO.deleteAll();
    }

    @Test
    public void testMethod() {

    }
    
    


    /*
    @Test
    public void testDownloadReport() throws Exception {
        String dt = "2017-04-29";  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        c.setTime(sdf.parse(dt));
        while(c.before(today)){
            System.out.println("*****************  Downloading the report for " + c.getTime());
            c.setTime(sdf.parse(dt));
            abhiBusPassengerReportService.downloadReports(dt);
            c.add(Calendar.DATE, 1);  // number of days to add
            dt = sdf.format(c.getTime());
        }

    }
    */

   /* @Test
    public void testDownloadSingleReport() throws Exception {
        abhiBusPassengerReportService.downloadReports("2017-02-20");
    }*/


}