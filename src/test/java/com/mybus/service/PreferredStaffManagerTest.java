package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.*;
import com.mybus.model.*;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class PreferredStaffManagerTest{
    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private PreferredStaffDAO preferredStaffDAO;
    @Autowired
    private VehicleDAO vehicleDAO;
    @Autowired
    private PreferredStaffManager preferredStaffManager;
    @Autowired
    private StaffDAO staffDAO;
    @Autowired
    private CityDAO cityDAO;

    @Before
    @After
    public void cleanUp(){
        operatorAccountDAO.deleteAll();
        preferredStaffDAO.deleteAll();
        vehicleDAO.deleteAll();
        staffDAO.deleteAll();
        cityDAO.deleteAll();
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
    }

    @Test
    public void testGetAllStaff(){
        City c1 = new City();
        c1.setName("Ongole");
        c1=cityDAO.save(c1);
        City c2 = new City();
        c2.setName("Hyderabad");
        c2=cityDAO.save(c2);
        Vehicle v1 = new Vehicle();
        v1.setRegNo("AP123");
        v1 = vehicleDAO.save(v1);
        Vehicle v2 = new Vehicle();
        v2.setRegNo("KA111");
        v2 = vehicleDAO.save(v2);
        Staff s1 = new Staff();
        s1=staffDAO.save(s1);
        Staff s2 = new Staff();
        s2=staffDAO.save(s2);
        for(int i=0;i<3;i++){
            Set<String> staffIds = new HashSet<>();
            PreferredStaff p = new PreferredStaff();
            p.setOperatorId(sessionManager.getOperatorId());
            if(i == 2){
                staffIds.add(s1.getId());
                staffIds.add(s2.getId());
                p.setVehicleId(v2.getId());
                p.setStaffIds(staffIds);
            }else{
                staffIds.add(s1.getId());
                p.setVehicleId(v1.getId());
                p.setStaffIds(staffIds);
                p.setSourceId(c1.getId());
                p.setDestinationId(c2.getId());
            }
            preferredStaffDAO.save(p);
        }
        JSONObject query = new JSONObject();
        query.put("size",3);
        query.put("page",0);
        query.put("vehicleId",v1.getId());
        Page<PreferredStaff> page = preferredStaffManager.getAllPreferredStaff(query);
        List<PreferredStaff> staffList = page.getContent();
        assertEquals(2,staffList.size());
        JSONObject query1 = new JSONObject();
        query1.put("vehicleId",v2.getId());
        Page<PreferredStaff> page1 = preferredStaffManager.getAllPreferredStaff(query1);
        assertEquals(1,page1.getTotalElements());
        JSONObject query2 = new JSONObject();
        query2.put("sourceId",c1.getId());
        query2.put("destinationId",c2.getId());
        Page<PreferredStaff> page2 = preferredStaffManager.getAllPreferredStaff(query2);
        assertEquals(2,page2.getTotalElements());
    }

}