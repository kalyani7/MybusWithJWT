package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.VehicleDAO;
import com.mybus.model.Vehicle;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class VehicleManagerTest{

    @Autowired
    private VehicleDAO vehicleDAO;

    @Autowired
    private VehicleManager vehicleManager;

    @Before
    @After
    public void cleanup() {
        vehicleDAO.deleteAll();
    }

    @Ignore
    @Test
    public void testFindExpiring() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+11);
        DateTime dateTime = new DateTime(calendar.getTime());
        for(int i =0; i<5; i++){
            Vehicle v = new Vehicle();
            v.setRegNo("12");
            v.setAuthExpiry(dateTime);
            v.setPermitExpiry(dateTime);
            v.setPollutionExpiry(dateTime);
            v.setFitnessExpiry(dateTime);
            v.setInsuranceExpiry(dateTime);
            vehicleDAO.save(v);
        }
        Map<String, List<Vehicle>> vehicles = vehicleManager.findExpiring();
        assertEquals(0, vehicles.size());
        Vehicle v = new Vehicle();
        v.setRegNo("12345");
        v.setAuthExpiry(new DateTime());
        v.setActive(true);
        vehicleDAO.save(v);
        vehicles = vehicleManager.findExpiring();
        assertEquals(1, vehicles.get("authExpiring").size());
        assertEquals(1, vehicles.get("permitExpiring").size());
        assertEquals(1, vehicles.get("fitnessExpiring").size());
        assertEquals(1, vehicles.get("pollutionExpiring").size());
        assertEquals(1, vehicles.get("insuranceExpiring").size());
        v.setAuthExpiry(dateTime);
        vehicleDAO.save(v);
        vehicles = vehicleManager.findExpiring();
        assertEquals(0, vehicles.get("authExpiring").size());
        assertEquals(1, vehicles.get("permitExpiring").size());
        assertEquals(1, vehicles.get("fitnessExpiring").size());
        assertEquals(1, vehicles.get("pollutionExpiring").size());
        assertEquals(1, vehicles.get("insuranceExpiring").size());
    }
    @Test
    public void testVehicleCount(){
        Vehicle v1 = new Vehicle();
        v1.setRegNo("AP01T112");
        v1.setActive(true);
        vehicleDAO.save(v1);
        Vehicle v2 = new Vehicle();
        v2.setActive(true);
        v2.setRegNo("TR0Y777");
        vehicleDAO.save(v2);
        Vehicle v3 = new Vehicle();
        v3.setRegNo("AR7D999");
        v3.setActive(true);
        vehicleDAO.save(v3);
        Vehicle v4 = new Vehicle();
        v4.setActive(true);
        v4.setRegNo("PYOH567");
        vehicleDAO.save(v4);
        JSONObject q = new JSONObject();
        q.put("searchText", "p");
        long count = vehicleManager.count(q);
        assertEquals(2,count);
    }

    @Test
    public void testGetAllVehicles(){
        Vehicle v1 = new Vehicle();
        v1.setRegNo("AP01T112");
        vehicleDAO.save(v1);
        Vehicle v2 = new Vehicle();
        v2.setRegNo("TR0Y777");
        vehicleDAO.save(v2);
        Vehicle v3 = new Vehicle();
        v3.setRegNo("AR7D999");
        vehicleDAO.save(v3);
        Vehicle v4 = new Vehicle();
        v4.setRegNo("PYOH567");
        vehicleDAO.save(v4);
        PageRequest page = new PageRequest(0,10);
        JSONObject q = new JSONObject();
        q.put("searchText", "ap");
        Page<Vehicle> vehicles = vehicleManager.findAll(q);
        assertEquals(1,vehicles.getTotalElements());
    }
    @Test
    public void testGetAllVehiclesInAscOrder(){
        Vehicle v1 = new Vehicle();
        v1.setRegNo("AP01T112");
        vehicleDAO.save(v1);
        Vehicle v2 = new Vehicle();
        v2.setRegNo("TR0Y777");
        vehicleDAO.save(v2);
        Vehicle v3 = new Vehicle();
        v3.setRegNo("AR7D999");
        vehicleDAO.save(v3);
        Vehicle v4 = new Vehicle();
        v4.setRegNo("PYOH567");
        vehicleDAO.save(v4);
        PageRequest page = new PageRequest(0,10);
        Page<Vehicle> vehicles = vehicleManager.findAll(new JSONObject());
        List<Vehicle> sortedVehicles = vehicles.getContent();
        assertEquals("AR7D999",sortedVehicles.get(1).getRegNo());
        assertEquals("TR0Y777",sortedVehicles.get(3).getRegNo());
    }
}