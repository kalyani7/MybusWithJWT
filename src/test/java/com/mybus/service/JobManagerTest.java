package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.InventoryDAO;
import com.mybus.dao.JobDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.ReminderDAO;
import com.mybus.dao.impl.ReminderMongoDAO;
import com.mybus.dto.InventoryDTO;
import com.mybus.model.Inventory;
import com.mybus.model.Job;
import com.mybus.model.OperatorAccount;
import com.mybus.model.Reminder;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;


@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class JobManagerTest{
    @Autowired
    private JobManager jobManager;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private InventoryDAO inventoryDAO;
    @Autowired
    private ReminderDAO reminderDAO;
    @Autowired
    private ReminderMongoDAO reminderMongoDAO;

    @Before
    @After
    public void cleanup(){
        jobDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        inventoryDAO.deleteAll();
        reminderDAO.deleteAll();;
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
    }

    @Test
    public void testAddJob(){
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        Inventory i = inventoryDAO.save(new Inventory());
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(i.getId());
        inventoryDTO.setQuantity(2);
        inventoryDTOS.add(inventoryDTO);
        Job job = new Job();
        job.setVehicleId("111");
        job.setOperatorId(sessionManager.getOperatorId());
        job.setInventories(inventoryDTOS);
        job = jobManager.addJob(job);
        assertEquals("111",job.getVehicleId());
    }

    @Test
    public void testGetAllJobs() throws ParseException {
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        Inventory i = inventoryDAO.save(new Inventory());
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(i.getId());
        inventoryDTO.setQuantity(2);
        inventoryDTOS.add(inventoryDTO);
        Job j1 = new Job();
        j1.setVehicleId("111");
        j1.setOperatorId(sessionManager.getOperatorId());
        jobDAO.save(j1);
        Job j2 = new Job();
        j2.setVehicleId("111");
        j2.setInventories(inventoryDTOS);
        j2.setOperatorId(sessionManager.getOperatorId());
        jobDAO.save(j2);
        Job j3 = new Job();
        j3.setVehicleId("555");
        j3.setInventories(inventoryDTOS);
        j3.setOperatorId(sessionManager.getOperatorId());
        jobDAO.save(j3);
        Job j4 = new Job();
        j4.setVehicleId("980");
        j4.setInventories(inventoryDTOS);
        j4.setOperatorId(sessionManager.getOperatorId());
        jobDAO.save(j4);
        PageRequest page = new PageRequest(0,10);
        Page<Job> jobs = jobManager.getJobs(page, false);
        assertEquals(4,jobs.getTotalElements());
    }

    @Test
    public void testUpdate(){
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId("123");
        inventoryDTO.setQuantity(2);
        inventoryDTOS.add(inventoryDTO);
        Job job = new Job();
        job.setVehicleId("111");
        job.setInventories(inventoryDTOS);
        job.setOperatorId(sessionManager.getOperatorId());
        job = jobDAO.save(job);
        job.setVehicleId("132");
        job.setInventories(inventoryDTOS);
        job = jobManager.updateJob(job);
        assertEquals("132",job.getVehicleId());
    }

    @Test
    public void testAddJobWithReminder(){
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        Inventory i = new Inventory();
        i = inventoryDAO.save(i);
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(i.getId());
        inventoryDTO.setQuantity(2);
        inventoryDTOS.add(inventoryDTO);
        Job job = new Job();
        job.setVehicleId("111");
        job.setId("123");
        job.setReminder(true);
        job.setExpectedMileage(5000);
        job.setOperatorId(sessionManager.getOperatorId());
        job.setInventories(inventoryDTOS);
        job = jobManager.addJob(job);
         Reminder r1=reminderMongoDAO.getReminderJobRefId("123");
        assertEquals(5000,r1.getExpectedMileage());
        assertEquals(1,reminderDAO.count());
    }
    @Test
    public void testUpdateJobWithReminder(){
        Inventory i = new Inventory();
        i = inventoryDAO.save(i);
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(i.getId());
        inventoryDTO.setQuantity(2);
        inventoryDTOS.add(inventoryDTO);
        Job job = new Job();
        job.setVehicleId("111");
        job.setId("123");
        job.setInventories(inventoryDTOS);
        job.setInventories(inventoryDTOS);
        job.setOperatorId(sessionManager.getOperatorId());
        job = jobManager.addJob(job);
        job.setReminder(true);
        job.setExpectedMileage(5000);
        job.setInventories(inventoryDTOS);
        job=jobManager.addJob(job);
        Reminder r1=reminderMongoDAO.getReminderJobRefId("123");
        assertEquals(5000,r1.getExpectedMileage());
        assertEquals(1,reminderDAO.count());
    }

    @Test
    public void testSearchJobs() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        for(int i=0;i<5;i++){
            Job j1 = new Job();
            j1.setVehicleId("111");
           j1.setOperatorId(sessionManager.getOperatorId());
            j1.setJobDate(calendar.getTime());
            jobDAO.save(j1);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        JSONObject query = new JSONObject();
        query.put("vehicleId","111");
        query.put("size",10);
        query.put("page",0);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        query.put("startDate", ServiceUtils.formatDate(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        query.put("endDate",  ServiceUtils.formatDate(calendar.getTime()));
        Page data = jobManager.search(query);
        List<Job> jobs = data.getContent();
        assertEquals(1,jobs.size());
    }
}



