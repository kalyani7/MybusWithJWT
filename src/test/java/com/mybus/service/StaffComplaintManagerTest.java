package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.StaffComplaintDAO;
import com.mybus.model.StaffComplaints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class StaffComplaintManagerTest {

    @Autowired
    private StaffComplaintManager complaintManager;

    @Autowired
    private StaffComplaintDAO complaintDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Before
    @After
    public  void removeAll() {
        complaintDAO.deleteAll();
    }

    @Test
    public void testgetAll() throws ParseException {
        StaffComplaints c1 = new StaffComplaints();
        c1.setStaffid("good");
        c1.setOperatorId(sessionManager.getOperatorId());
        complaintDAO.save(c1);
        PageRequest page = new PageRequest(0,10);
        Page<StaffComplaints> complaints = complaintManager.getAllComplaints("good",page);
        assertEquals(1,complaints.getTotalElements());
    }

    @Test
    public void testSearch() throws ParseException {
        StaffComplaints s1 = new StaffComplaints();
        s1.setIncidentDate("01/01/2019");
        s1.setStaffid("333");
        s1.setRemarks("bad service");
        s1.setVehicleid("333");
        s1.setOperatorId(sessionManager.getOperatorId());
        complaintDAO.save(s1);
        String staffid="333";
        PageRequest page = new PageRequest(0,10);
        Page<StaffComplaints> complaints = complaintManager.getAllComplaints(staffid,page);
        assertEquals(1,complaints.getTotalElements());
    }

}