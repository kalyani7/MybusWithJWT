package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.*;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.joda.time.DateTime;
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
import java.util.*;

import static org.junit.Assert.*;


@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class FuelExpenseManagerTest {

    @Autowired
    private FuelExpenseManager fuelExpenseManager;
    @Autowired
    private FuelExpenseDAO fuelExpenseDAO;
    @Autowired
    private SuppliersManager suppliersManager;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private SupplierDAO supplierDAO;

    private OperatorAccount operatorAccount;
    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    private Page<FuelExpense> fuelExpenses;
    @Autowired
    private ServiceReportDAO serviceReportDAO;

    @Autowired
    private ServiceReportMongoDAO serviceReportMongoDAO;
    @Autowired
    private VehicleDAO vehicleDAO;

    @Before
    @After
    public void cleanup(){
        fuelExpenseDAO.deleteAll();
        vehicleDAO.deleteAll();
        supplierDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
    }
    //addTestCase
    @Test
    public void testAddExpense()throws ParseException{
        Calendar calendar = Calendar.getInstance();
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setDateString("2019-5-05");
        fuelExpense.setId("234");
        fuelExpense.setVehicleId("1234");
        fuelExpense.setOdometer(50);
        fuelExpense.setPaid(true);
         fuelExpenseManager.addFuelExpense(fuelExpense);
         fuelExpense=fuelExpenseManager.getFuelExpense("234");
        assertEquals(50,fuelExpense.getOdometer());
        v1 = vehicleDAO.findById(v1.getId()).get();
        assertEquals(v1.getOdometerReading(),fuelExpense.getOdometer());
    }
    //getALLExpensesTestCase
    @Test
    public void testGetAllExpenses() throws ParseException {
        for(int i=0;i<2;i++){
            FuelExpense fuelExpense = new FuelExpense();
            fuelExpense.setOdometer(5);
            fuelExpense.setCost(500);
            fuelExpense.setDateString("2018-05-05");
            fuelExpense.setOperatorId(operatorAccount.getId());
            fuelExpenseDAO.save(fuelExpense);
        }
        PageRequest page = new PageRequest(0, 20);
        Iterable<FuelExpense> fuelExpenses = fuelExpenseManager.getAllFuelExpenses("2018-05-05",page);
        List it = IteratorUtils.toList(fuelExpenses.iterator());//converting iterator to array list
        assertEquals(2,it.size());
    }
    //getOneExpensebasedOnId
    @Test
    public void testGetFuelExpense(){
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setOdometer(10);
        fuelExpense.setSupplierId("121");
        fuelExpense = fuelExpenseDAO.save(fuelExpense);
        FuelExpense obj = fuelExpenseManager.getFuelExpense(fuelExpense.getId());
        assertEquals("121",obj.getSupplierId());
    }
    //updateOneExpensebasedOnId
    @Test
    public void testUpdateFuelExpense()throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setDate(calendar.getTime());
        fuelExpense.setOdometer(15);
        fuelExpense.setId("456");
        fuelExpense.setVehicleId("1234");
        fuelExpense.setDateString("2019-02-04");
        fuelExpense.setSupplierId("999");
        fuelExpense = fuelExpenseDAO.save(fuelExpense);
        fuelExpense.setOdometer(45);
        fuelExpenseManager.updateFuelExpense(fuelExpense);
        assertEquals(45,fuelExpense.getOdometer());
        v1 = vehicleDAO.findById(v1.getId()).get();
        assertEquals(v1.getOdometerReading(),fuelExpense.getOdometer());
    }
    //deleteOneExpense
    @Test
    public void testDeleteFuelExpense()throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setQuantity(20);
        fuelExpense.setId("456");
        fuelExpense.setSupplierId("999");
        fuelExpense.setVehicleId("1234");
        fuelExpense.setDate(new Date());
         fuelExpenseManager.addFuelExpense(fuelExpense);
         fuelExpense=fuelExpenseDAO.findById("456").get();
        fuelExpenseManager.delete(fuelExpense.getId());
        Optional obj = fuelExpenseDAO.findById(fuelExpense.getId());
        assertFalse(obj.isPresent());
    }
    //searchQuery
    @Test
    public void testWithQuery() throws  ParseException{
        JSONObject query = new JSONObject();
        Calendar calendar = Calendar.getInstance();
        Supplier supplier = new Supplier();
        supplier.setName("Volvo");
        supplier = supplierDAO.save(supplier);
        FuelExpense f1 = new FuelExpense();
        f1.setOdometer(44);
        f1.setDateString("2018-05-05");
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        f1.setDate(calendar.getTime());
        f1.setSupplierId("23456");
        f1.setVehicleId("12345");
        f1.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseDAO.save(f1);
        FuelExpense f2 = new FuelExpense();
        f2.setOdometer(55);
        f2.setDateString(ServiceUtils.formatDate( calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        f2.setDate(calendar.getTime());
        f2.setSupplierId("23456");
        f2.setOperatorId(sessionManager.getOperatorId());
        f2.setVehicleId("12345");
        fuelExpenseDAO.save(f2);
        FuelExpense f3 = new FuelExpense();
        f3.setOdometer(55);
        f3.setDateString(ServiceUtils.formatDate( calendar.getTime()));
        f3.setDate(calendar.getTime());
        f3.setSupplierId("23456");
        f3.setOperatorId(sessionManager.getOperatorId());
        f3.setVehicleId("12345");
        fuelExpenseDAO.save(f3);
        FuelExpense f4 = new FuelExpense();
        f4.setOdometer(65);
        f4.setDateString(ServiceUtils.formatDate( calendar.getTime()));
        f4.setDate(calendar.getTime());
        f4.setSupplierId("23456");
        f4.setOperatorId(sessionManager.getOperatorId());
        f4.setVehicleId("12345");
        fuelExpenseDAO.save(f4);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        query.put("startDate", ServiceUtils.formatDate( calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 4);
        query.put("endDate", ServiceUtils.formatDate( calendar.getTime()));
        query.put("supplierId","23456");
        query.put("vehicleId","12345");
        PageRequest page = new PageRequest(0, 10);
        Page<FuelExpense> fuelExpenses = fuelExpenseManager.searchFuelExpenses(query);
        assertEquals(3,fuelExpenses.getTotalElements());
    }
    //count
    @Test
    public void getCountTest() throws ParseException{
        Supplier supplier = new Supplier();
        for(int i=0;i<2;i++) {
            FuelExpense fuelExpense = new FuelExpense();
            fuelExpense.setOdometer(15);
            fuelExpense.setSupplierId(supplier.getId());
            fuelExpense.setDateString("2018-05-05");
            fuelExpense.setOperatorId(sessionManager.getOperatorId());
            fuelExpense = fuelExpenseDAO.save(fuelExpense);
        }
        PageRequest page = new PageRequest(0, 20);
        long count=fuelExpenseManager.count("2018-05-05");
        assertEquals(2,count);
    }
    //getBasedOnDate
    @Test
    public void  getBasedOnDate()throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        JSONObject query = new JSONObject();
        Supplier supplier = new Supplier();
        FuelExpense f1 = new FuelExpense();
        f1.setOdometer(99);
        calendar.add(Calendar.DATE, -5);
        f1.setDate(calendar.getTime());
        f1.setSupplierId(supplier.getId());
        f1.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseDAO.save(f1);
        PageRequest page = new PageRequest(0, 20);
        query.put("date",calendar.getTime());
        Page<FuelExpense> fuelExpenses=fuelExpenseManager.searchFuelExpenses(query);
        List it = IteratorUtils.toList(fuelExpenses.iterator());//converting iterator to array list
        FuelExpense f3= new FuelExpense();
        f3=(FuelExpense) it.get(0);
        Iterator<FuelExpense> iterator = fuelExpenses.iterator();
        assertEquals(99,f3.getOdometer());

    }

    @Test
    public void  addFuelExpenseWithEconomy()throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
       Supplier supplier = new Supplier();
        FuelExpense f0 = new FuelExpense();
        f0.setOdometer(500);
        f0.setFillup(true);
        calendar.add(Calendar.DATE, -6);
        f0.setDate(calendar.getTime());
        f0.setVehicleId("1234");
        f0.setQuantity(100);
        f0.setSupplierId(supplier.getId());
        f0.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f0);
        FuelExpense f1 = new FuelExpense();
        f1.setOdometer(1000);
        f1.setFillup(false);
        calendar.add(Calendar.DATE, 1);
        f1.setDate(calendar.getTime());
        f1.setVehicleId("1234");
        f1.setQuantity(10);
        f1.setSupplierId(supplier.getId());
        f1.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f1);
        FuelExpense f2 = new FuelExpense();
        f2.setOdometer(1500);
        f2.setFillup(true);
        calendar.add(Calendar.DATE, 1);
        f2.setDate(calendar.getTime());
        f2.setVehicleId("1234");
        f2.setQuantity(10);
        f2.setSupplierId(supplier.getId());
        f2.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f2);
        FuelExpense f3 = new FuelExpense();
        f3.setOdometer(2000);
        f3.setFillup(true);
        calendar.add(Calendar.DATE, 1);
        f3.setDate(calendar.getTime());
        f3.setVehicleId("1234");
        f3.setQuantity(50);
        f3.setId("123");
        f3.setSupplierId(supplier.getId());
        f3.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f3);
        f3=fuelExpenseDAO.findById("123").get();
        assertEquals(10.0,f3.getEconomy(),0.0);
        assertEquals(50,f2.getEconomy(),0.0);
    }
    @Test
    public void  updateAddFuelExpenseWithEconomy()throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        Supplier supplier = new Supplier();
        FuelExpense f0 = new FuelExpense();
        f0.setOdometer(500);
        f0.setFillup(true);
        calendar.add(Calendar.DATE, -3);
        f0.setDate(calendar.getTime());
        f0.setVehicleId("1234");
        f0.setQuantity(100);
        f0.setSupplierId(supplier.getId());
        f0.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f0);
        FuelExpense f1 = new FuelExpense();
        f1.setOdometer(1000);
        f1.setFillup(false);
        calendar.add(Calendar.DATE, 1);
        f1.setDate(calendar.getTime());
        f1.setVehicleId("1234");
        f1.setQuantity(10);
        f1.setId("234");
        f1.setSupplierId(supplier.getId());
        f1.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f1);
        f1.setFillup(true);
        fuelExpenseManager.updateFuelExpense(f1);
        f1=fuelExpenseDAO.findById("234").get();
        assertEquals(50.0,f1.getEconomy(),0.0);

    }
    @Test
    public void  oldFuelExpenseIsfillupMakeItTrue()throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        Supplier supplier = new Supplier();
        FuelExpense f0 = new FuelExpense();
        f0.setOdometer(500);
        f0.setFillup(false);
        calendar.add(Calendar.DATE, -3);
        f0.setDate(calendar.getTime());
        f0.setVehicleId("1234");
        f0.setQuantity(100);
        f0.setSupplierId(supplier.getId());
        f0.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f0);
        FuelExpense f1 = new FuelExpense();
        f1.setOdometer(1000);
        f1.setFillup(false);
        calendar.add(Calendar.DATE, 1);
        f1.setDate(calendar.getTime());
        f1.setVehicleId("1234");
        f1.setId("234");
        f1.setQuantity(20);
        f1.setSupplierId(supplier.getId());
        f1.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f1);
        FuelExpense f2 = new FuelExpense();
        f2.setOdometer(1500);
        f2.setFillup(true);
        calendar.add(Calendar.DATE, 1);
        f2.setDate(calendar.getTime());
        f2.setVehicleId("1234");
        f2.setQuantity(50);
        f2.setSupplierId(supplier.getId());
        f2.setOperatorId(sessionManager.getOperatorId());
        fuelExpenseManager.addFuelExpense(f2);
        f1.setFillup(true);
        fuelExpenseManager.updateFuelExpense(f1);
        f1=fuelExpenseDAO.findById("234").get();
        assertEquals(4.166666666666667,f1.getEconomy(),0.0);
        assertEquals(5.882352941176471,f2.getEconomy(),0.0);

    }
    @Test
    public void  getServiceName()throws  ParseException{
        Calendar calendar = Calendar.getInstance();
        ServiceReport sr=new ServiceReport();
        ServiceReport sr1=new ServiceReport();
       String sr2;
        sr.setVehicleRegNumber("ap07bk9999");
        sr.setJDate("2019-01-25");
        sr.setSource("hyderabad");
        sr.setDestination("guntur");
        sr.setServiceName("skthyd-gunt");
        sr.setBusType("semiSleeper");
        sr.setOperatorId(sessionManager.getOperatorId());
         sr1=serviceReportDAO.save(sr);
         sr2=serviceReportMongoDAO.findServiceName(sr1.jDate,sr1.getVehicleRegNumber());
        assertEquals("skthyd-gunt",sr1.getServiceName());
        assertEquals("ap07bk9999",sr1.getVehicleRegNumber());
        assertEquals("semiSleeper",sr1.getBusType());
        assertEquals("skthyd-gunt",sr2);


    }
    @Test
    public void testAddServiceName()throws ParseException{
        Calendar calendar = Calendar.getInstance();
        ServiceReport sr=new ServiceReport();
        ServiceReport sr1=new ServiceReport();
        String sr2;
        sr.setVehicleRegNumber("ap07bk9999");
        sr.setJDate("2019-01-25");
        sr.setSource("hyderabad");
        sr.setDestination("guntur");
        sr.setServiceName("skthyd-gunt");
        sr.setBusType("semiSleeper");
        sr.setOperatorId(sessionManager.getOperatorId());
        sr1=serviceReportDAO.save(sr);
        Vehicle v1=new Vehicle();
        Vehicle v2=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        System.out.println(v1);
        FuelExpense fuelExpense = new FuelExpense();
        FuelExpense f1 = new FuelExpense();
        fuelExpense.setDateString("2019-01-25");
        fuelExpense.setOdometer(50);
        fuelExpense.setVehicleId("1234");
        fuelExpense.setPaid(true);
        fuelExpense.setId("4567");
        fuelExpenseManager.addFuelExpense(fuelExpense);
        f1=fuelExpenseManager.getFuelExpense("4567");
        assertEquals(50,fuelExpense.getOdometer());
        assertEquals("skthyd-gunt",f1.getServiceName());
    }
    @Test
    public void testUpdateServiceNameWhenVehileIdChanged()throws ParseException{
        Calendar calendar = Calendar.getInstance();
        ServiceReport sr=new ServiceReport();
        sr.setVehicleRegNumber("ap07bk9999");
        sr.setJDate("2019-01-25");
        sr.setSource("hyderabad");
        sr.setDestination("guntur");
        sr.setServiceName("skthyd-gunt");
        sr.setBusType("semiSleeper");
        sr.setOperatorId(sessionManager.getOperatorId());
        sr=serviceReportDAO.save(sr);
        ServiceReport sr1=new ServiceReport();
        sr1.setVehicleRegNumber("ap07bk5555");
        sr1.setJDate("2019-01-25");
        sr1.setSource("hyderabad");
        sr1.setDestination("guntur");
        sr1.setServiceName("skthyd-chennai");
        sr1.setBusType("semiSleeper");
        sr1.setOperatorId(sessionManager.getOperatorId());
        sr1=serviceReportDAO.save(sr1);
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        Vehicle v2=new Vehicle();
        calendar.add(Calendar.DATE, 1);
        v2.setPermitExpiry(dateTime);
        v2.setInsuranceExpiry(dateTime);
        v2.setFitnessExpiry(dateTime);
        v2.setPollutionExpiry(dateTime);
        v2.setAuthExpiry(dateTime);
        v2.setRegNo("ap07bk5555");
        v2.setId("234");
        v2.setVehicleType("AC");
        v2= vehicleDAO.save(v2);
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setDateString("2019-01-25");
        fuelExpense.setDate(calendar.getTime());
        fuelExpense.setOdometer(50);
        fuelExpense.setVehicleId("1234");
        fuelExpense.setPaid(true);
        fuelExpense.setId("4567");
        fuelExpenseManager.addFuelExpense(fuelExpense);
        fuelExpense=fuelExpenseDAO.findById("4567").get();
        fuelExpense.setVehicleId("234");
        ServiceReport sr2=new ServiceReport();
        sr2.setVehicleRegNumber("ap07bk4444");
        sr2.setJDate("2019-01-25");
        sr2.setSource("hyderabad");
        sr2.setDestination("guntur");
        sr2.setServiceName("skthyd-banglore");
        sr2.setBusType("semiSleeper");
        sr2.setOperatorId(sessionManager.getOperatorId());
        sr2=serviceReportDAO.save(sr2);
        fuelExpenseManager.updateFuelExpense(fuelExpense);
        fuelExpense=fuelExpenseManager.getFuelExpense("4567");
        assertEquals(50,fuelExpense.getOdometer());
        assertEquals("skthyd-chennai",fuelExpense.getServiceName());
    }
    @Test
    public void testUpdateServiceName()throws ParseException{
        Calendar calendar = Calendar.getInstance();
        Vehicle v1=new Vehicle();
        calendar.add(Calendar.DATE, 3);
        DateTime dateTime = new DateTime(calendar.getTime());
        v1.setPermitExpiry(dateTime);
        v1.setInsuranceExpiry(dateTime);
        v1.setFitnessExpiry(dateTime);
        v1.setPollutionExpiry(dateTime);
        v1.setAuthExpiry(dateTime);
        v1.setRegNo("ap07bk9999");
        v1.setId("1234");
        v1.setVehicleType("AC");
        v1= vehicleDAO.save(v1);
        FuelExpense fuelExpense = new FuelExpense();
        fuelExpense.setDateString("2019-01-25");
        fuelExpense.setDate(calendar.getTime());
        fuelExpense.setOdometer(50);
        fuelExpense.setVehicleId("1234");
        fuelExpense.setPaid(true);
        fuelExpense.setId("4567");
        fuelExpenseManager.addFuelExpense(fuelExpense);
        fuelExpense=fuelExpenseManager.getFuelExpense("4567");
        assertNotEquals("skthyd-banglore",fuelExpense.getServiceName());
        fuelExpense.setVehicleId("1234");
        ServiceReport sr2=new ServiceReport();
        sr2.setVehicleRegNumber("ap07bk9999");
        sr2.setJDate("2019-01-25");
        sr2.setSource("hyderabad");
        sr2.setDestination("guntur");
        sr2.setServiceName("skthyd-banglore");
        sr2.setBusType("semiSleeper");
        sr2.setOperatorId(sessionManager.getOperatorId());
        sr2=serviceReportDAO.save(sr2);
        fuelExpenseManager.updateFuelExpense(fuelExpense);
        fuelExpense=fuelExpenseManager.getFuelExpense("4567");
        assertEquals(50,fuelExpense.getOdometer());
        assertEquals("skthyd-banglore",fuelExpense.getServiceName());
    }
}
