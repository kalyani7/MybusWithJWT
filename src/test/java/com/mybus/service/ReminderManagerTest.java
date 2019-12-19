package com.mybus.service;

import com.mybus.dao.*;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

public class ReminderManagerTest{

    @Autowired
    private ReminderDAO reminderDAO;
    @Autowired
    private VehicleDAO vehicleDAO;
    @Autowired
    private FuelExpenseDAO fuelExpenseDAO;
    @Autowired
    private ReminderManager reminderManager;
    @Autowired
    private FuelExpenseManager fuelExpenseManager;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    @Autowired
    private SessionManager sessionManager;

    @Before
    @After
    public void cleanUp(){
        reminderDAO.deleteAll();
        fuelExpenseDAO.deleteAll();
        vehicleDAO.deleteAll();
        userDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
    }
    @Test
    public void testGetAll() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        JSONObject query = new JSONObject();
        User user = new User();
        user.setFirstName("Kalyani");
        user.setLastName("Kandula");
        user = userDAO.save(user);
        Reminder r1 = new Reminder();
        r1.setDescription("Demo");
        r1.setUserId(user.getId());
        r1.setOperatorId(sessionManager.getOperatorId());
        reminderDAO.save(r1);
        Reminder r2 = new Reminder();
        r2.setDescription("Tour");
        r2.setUserId(user.getId());
        r2.setOperatorId(sessionManager.getOperatorId());
        reminderDAO.save(r2);
        Reminder r3 = new Reminder();
        r3.setDescription("Movie");
        r3.setUserId(user.getId());
        r3.setCompleted(true);
        r3.setOperatorId(sessionManager.getOperatorId());
        reminderDAO.save(r3);
        query.put("isCompleted",true);
        query.put("page",0);
        query.put("size",10);
        query.put("sort","desc");
        PageRequest pageable = null;
        int page = (int) query.get("page");
        int size = (int) query.get("size");
        pageable = new PageRequest(page,size);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        query.put("fromDate", ServiceUtils.formatDate(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        query.put("toDate",  ServiceUtils.formatDate(calendar.getTime()));
        Page<Reminder> reminderPage = reminderManager.getReminders(query);
        assertEquals(1,reminderPage.getTotalElements());
    }

   @Test
   public void testCount() throws ParseException {
       User user = new User();
       user.setFirstName("Kalyani");
       user.setLastName("Kandula");
       user = userDAO.save(user);
       Reminder r1 = new Reminder();
       r1.setDescription("Demo");
       r1.setUserId(user.getId());
       r1.setCompleted(true);
       r1.setOperatorId(sessionManager.getOperatorId());
       reminderDAO.save(r1);
       Reminder r2 = new Reminder();
       r2.setCompleted(true);
       r2.setDescription("Tour");
       r2.setUserId(user.getId());
       r2.setOperatorId(sessionManager.getOperatorId());
       reminderDAO.save(r2);
       JSONObject query = new JSONObject();
       query.put("isCompleted",true);
       long count = reminderManager.reminderCount(query);
       assertEquals(2,count);
   }

    @Ignore
    @Test
    public void testUpcomingReminders() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+10);
        DateTime dateTime = new DateTime(calendar.getTime());
        User user = new User();
        user.setFirstName("harish");
        user.setLastName("K");
        user = userDAO.save(user);
        User user1 = new User();
        user.setFirstName("saradhi");
        user.setLastName("M");
        user1 = userDAO.save(user1);
        User user2 = new User();
        user.setFirstName("Dp");
        user.setLastName("K");
        user2 = userDAO.save(user2);
        User user3 = new User();
        user.setFirstName("kalyani");
        user.setLastName("K");
        user3 = userDAO.save(user3);
        for (int i=0;i<5;i++) {
            Reminder r1 = new Reminder();
            r1.setDescription("Demo");
            r1.setReminderDate(dateTime);
            r1.setCompleted(false);
            r1.setUserId(user.getId());
            r1.setOperatorId(sessionManager.getOperatorId());
            reminderDAO.save(r1);
        }
        for (int i=0;i<3;i++) {
            Reminder r2 = new Reminder();
            r2.setDescription("Demo");
            r2.setReminderDate(dateTime);
            r2.setCompleted(false);
            r2.setUserId(user1.getId());
            r2.setOperatorId(sessionManager.getOperatorId());
            reminderDAO.save(r2);
        }
        for (int i=0;i<4;i++) {
            Reminder r3 = new Reminder();
            r3.setDescription("Demo");
            r3.setReminderDate(dateTime);
            r3.setCompleted(false);
            r3.setUserId(user2.getId());
            r3.setOperatorId(sessionManager.getOperatorId());
            reminderDAO.save(r3);
        }
        for (int i=0;i<2;i++) {
            Reminder r4 = new Reminder();
            r4.setDescription("Demo");
            r4.setReminderDate(dateTime);
            r4.setCompleted(false);
            r4.setUserId(user3.getId());
            r4.setOperatorId(sessionManager.getOperatorId());
            reminderDAO.save(r4);
        }
//        sessionManager.setCurrentUser(user3);
//        String userId = sessionManager.getCurrentUser().getId();
        List<Reminder> reminders = reminderManager.getUpcomingReminders( );
        assertEquals(14,reminders.size());
    }
    @Test
    public void setReminderDateToCurrentDate() throws ParseException {
        User user = new User();
        user.setFirstName("saradhi");
        user.setLastName("makineni");
        user = userDAO.save(user);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+20);
        DateTime dateTime = new DateTime(calendar.getTime());
        Reminder r1 = new Reminder();
        r1.setDescription("Demo");
        r1.setId("456");
        r1.setUserId(user.getId());
        r1.setReminderDate(dateTime);
        r1.setExpectedMileage(3000);
        r1.setCompleted(false);
        r1.setVehicleId("5678");
        r1.setOperatorId(sessionManager.getOperatorId());
        reminderDAO.save(r1);
        Reminder r2 = new Reminder();
        r2.setCompleted(true);
        r2.setDescription("Tour");
        r2.setUserId(user.getId());
        r2.setOperatorId(sessionManager.getOperatorId());
        reminderDAO.save(r2);

        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk8888");
        v1.setId("5678");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setDateString("2019-5-05");
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-20);

        fuelExpense.setDate(calendar.getTime());
        fuelExpense.setId("789");
        fuelExpense.setVehicleId("5678");
        fuelExpense.setOdometer(3100);
        fuelExpense.setPaid(true);
        fuelExpenseManager.addFuelExpense(fuelExpense);
        fuelExpense=fuelExpenseDAO.findById("789").get();
        r1=reminderDAO.findById("456").get();
        Calendar calendar1 = Calendar.getInstance();
        DateTime dateTime1 = new DateTime(calendar.getTime());

        assertEquals(dateTime1,r1.getReminderDate());
    }

    @Test
    public void updateReminderDateToCurrentDate() throws ParseException {
        User user = new User();
        user.setFirstName("saradhi");
        user.setLastName("makineni");
        user = userDAO.save(user);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+20);
        DateTime dateTime = new DateTime(calendar.getTime());
        Reminder r1 = new Reminder();
        r1.setDescription("Demo");
        r1.setId("456");
        r1.setUserId(user.getId());
        r1.setReminderDate(dateTime);
        r1.setExpectedMileage(3000);
        r1.setCompleted(false);
        r1.setVehicleId("5678");
        r1.setOperatorId(sessionManager.getOperatorId());
        reminderDAO.save(r1);
        Reminder r2 = new Reminder();
        r2.setCompleted(true);
        r2.setDescription("Tour");
        r2.setUserId(user.getId());
        r2.setOperatorId(sessionManager.getOperatorId());
        reminderDAO.save(r2);

        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk8888");
        v1.setId("5678");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setDateString("2019-5-05");
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-20);

        fuelExpense.setDate(calendar.getTime());
        fuelExpense.setId("789");
        fuelExpense.setVehicleId("5678");
        fuelExpense.setOdometer(2900);
        fuelExpense.setPaid(true);
        fuelExpenseManager.addFuelExpense(fuelExpense);
        fuelExpense.setOdometer(3200);
        fuelExpenseManager.updateFuelExpense(fuelExpense);
        fuelExpense=fuelExpenseDAO.findById("789").get();
        r1=reminderDAO.findById("456").get();
        Calendar calendar1 = Calendar.getInstance();
        DateTime dateTime1 = new DateTime(calendar.getTime());

        assertEquals(dateTime1,r1.getReminderDate());
    }



}