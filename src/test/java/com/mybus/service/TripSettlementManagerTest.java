package com.mybus.service;


import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.*;
import com.mybus.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class TripSettlementManagerTest {

    @Autowired
    private VehicleDAO vehicleDAO;

    @Autowired
    private StaffDAO staffDAO;

    @Autowired
    private TripSettlementDAO tripSettlementDAO;

    @Autowired
    private TripSettlementsReceiptsDAO tripSettlementsReceiptsDAO;

    @Autowired
    private TripSettlementManager tripSettlementManager;

    @Autowired
    private TripSettlementsReceiptsManager tripSettlementsReceiptsManager;

    @Autowired
    private BankTransfersDAO bankTransfersDAO;

    @Autowired
    private BankTransfersManager bankTransfersManager;

    @Autowired
    private TripPaymentsDAO tripPaymentsDAO;

    @Autowired
    private TripPaymentsManager tripPaymentsManager;

    @Autowired
    private TSOtherExpensesDAO tsOtherExpensesDAO;

    @Autowired
    private TSOtherExpensesManager tsOtherExpensesManager;

    @Before
    @After
    public void cleanup(){
        bankTransfersDAO.deleteAll();
        vehicleDAO.deleteAll();
        staffDAO.deleteAll();
        tripSettlementDAO.deleteAll();
        tripSettlementsReceiptsDAO.deleteAll();
        tripPaymentsDAO.deleteAll();
        tsOtherExpensesDAO.deleteAll();
    }

    @Test
    public void testUpdateTripSettlementBalances(){
        Vehicle vehicle = new Vehicle();
        vehicle.setRegNo("APKY890u");
        vehicle = vehicleDAO.save(vehicle);
        Staff staff = new Staff();
        staff.setName("kalyani");
        staff = staffDAO.save(staff);
        TripSettlement tripSettlement = new TripSettlement();
        tripSettlement.setVehicleId(vehicle.getId());
        tripSettlement.setDriverId(staff.getId());
        Calendar calendar = Calendar.getInstance();
        tripSettlement.setStartDate(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH,2);
        tripSettlement.setEndDate(calendar.getTime());
        tripSettlement = tripSettlementDAO.save(tripSettlement);
        double totalReceipts = 0;
        for(int i=0;i<10;i++){
            TSReceipt tsReceipt = new TSReceipt();
            if(i%2 == 0 && i< 6){//0 2 4
                tsReceipt.setTripSettlementId(tripSettlement.getId());
                tsReceipt.setAdvanceReceived(10+i);
                totalReceipts += tsReceipt.getAdvanceReceived();
            }
            tripSettlementsReceiptsDAO.save(tsReceipt);
        }
        TSBankTransfer tsBankTransfer1 = new TSBankTransfer();
        tsBankTransfer1.setTripSettlementId(tripSettlement.getId());
        tsBankTransfer1.setAmount(10);
        bankTransfersManager.saveBankTransferData(tsBankTransfer1);
        TSBankTransfer tsBankTransfer2 = new TSBankTransfer();
        tsBankTransfer2.setTripSettlementId(tripSettlement.getId());
        tsBankTransfer2.setAmount(20);
        bankTransfersManager.saveBankTransferData(tsBankTransfer2);
        tripSettlementManager.updateTripSettlementTotals(tripSettlement.getId());
        TripSettlement updatedTripSettlement = tripSettlementDAO.findById(tripSettlement.getId()).get();
        assertEquals(totalReceipts,updatedTripSettlement.getTotalCollection(),0.0);
        assertEquals(30.0,updatedTripSettlement.getBankTransfersTotal(),0.0);
        assertEquals(66.0,updatedTripSettlement.getTotalReceipts(),0.0);
        //test update when saving TSReceipt
        TSReceipt tsReceipt = new TSReceipt();
        tsReceipt.setTripSettlementId(tripSettlement.getId());
        tsReceipt.setAdvanceReceived(25);
        tripSettlementsReceiptsManager.saveTripSettlementReceipts(tsReceipt);
        updatedTripSettlement = tripSettlementDAO.findById(tripSettlement.getId()).get();
        assertEquals(61.0,updatedTripSettlement.getTotalCollection(),0.0);
        assertEquals(91.0,updatedTripSettlement.getTotalReceipts(),0.0);
        //when adding trip payment
        TSPayment tsPayment1 = new TSPayment();
        tsPayment1.setAmount(20.0);
        tsPayment1.setTripSettlementId(tripSettlement.getId());
        tripPaymentsManager.saveTripPayment(tsPayment1);
        TSPayment tsPayment2 = new TSPayment();
        tsPayment2.setAmount(10.0);
        tsPayment2.setTripSettlementId(tripSettlement.getId());
        tripPaymentsManager.saveTripPayment(tsPayment2);
        updatedTripSettlement = tripSettlementDAO.findById(tripSettlement.getId()).get();
        assertEquals(30.0,updatedTripSettlement.getDieselExpenses(),0.0);
        //when adding other trip expenses
        TSOtherExpenses tsOtherExpense = new TSOtherExpenses();
        tsOtherExpense.setAmount(15);
        tsOtherExpense.setTripSettlementId(tripSettlement.getId());
        tsOtherExpensesManager.addTSExpense(tsOtherExpense);
        updatedTripSettlement = tripSettlementDAO.findById(tripSettlement.getId()).get();
        assertEquals(15.0,updatedTripSettlement.getTripOtherExpenses(),0.0);
        // test update balance when delete
        assertEquals(30.0,updatedTripSettlement.getDieselExpenses(),0.0);
        tripPaymentsManager.deleteTripPayment(tsPayment1.getId());
        updatedTripSettlement = tripSettlementDAO.findById(tripSettlement.getId()).get();
        assertEquals(10.0,updatedTripSettlement.getDieselExpenses(),0.0);

    }
}