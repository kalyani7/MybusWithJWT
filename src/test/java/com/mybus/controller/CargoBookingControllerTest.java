package com.mybus.controller;

import com.mybus.dao.*;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.model.*;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.service.BranchOfficeManager;
import com.mybus.service.SessionManager;
import com.mybus.util.CargoBookingTestService;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by srinikandula on 12/11/16.
 */

public class CargoBookingControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private CargoBookingDAO cargoBookingDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    private User currentUser;

    private MockMvc mockMvc;

    @Autowired
    private ShipmentSequenceDAO shipmentSequenceDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private CargoBookingTestService cargoBookingTestService;

    @Autowired
    private SupplierDAO supplierDAO;

    private ShipmentSequence freeBookingSequence;
    private ShipmentSequence paidBookingSequence;
    private ShipmentSequence toPaySequence;
    private ShipmentSequence onAccountSequence;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Before
    @After
    public void cleanup() {
        super.setup();
        cargoBookingDAO.deleteAll();
        shipmentSequenceDAO.deleteAll();
        userDAO.deleteAll();
        supplierDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        //currentUser.setAmountToBePaid(500);
        currentUser = userDAO.save(currentUser);
        sessionManager.setCurrentUser(currentUser);
        freeBookingSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        paidBookingSequence = shipmentSequenceDAO.save(new ShipmentSequence("P", "Paid"));
        toPaySequence =  shipmentSequenceDAO.save(new ShipmentSequence("TP", "ToPay"));
        onAccountSequence = shipmentSequenceDAO.save(new ShipmentSequence(ShipmentSequence.ON_ACCOUNT, "OnAccount"));
        OperatorAccount operatorAccount = operatorAccountDAO.save(new OperatorAccount());
        sessionManager.setOperatorId(operatorAccount.getId());
    }

    @Test
    public void testGetAll() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        for(int i=0; i<5; i++) {
            cargoBookingDAO.save(cargoBookingTestService.createNew(shipmentSequence));
        }
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/shipments")), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void testGetAllFromCityId() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        String fromBranchId = null;
        for(int i=0; i<5; i++) {
            fromBranchId = cargoBookingDAO.save(cargoBookingTestService.createNew(shipmentSequence)).getFromBranchId();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fromBranchId", fromBranchId);
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void testGetAllByDispathDate() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        for(int i=0; i<5; i++) {
            CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
            if(i == 1) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE, cal.get(Calendar.DATE) -2);
                shipment.setDispatchDate(cal.getTime());
            }
            cargoBookingDAO.save(shipment);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void testGetAllByDispathDateRange() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) -2);
        for(int i=0; i<5; i++) {
            CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
            if(i == 1) {
                shipment.setDispatchDate(cal.getTime());
            }
            cargoBookingDAO.save(shipment);
        }
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) +1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDate", ServiceUtils.formatDate(cal.getTime()));
        jsonObject.put("endDate", ServiceUtils.formatDate(new Date()));
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void testGetAllByBookingDate() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE)-3);
        for(int i=0; i<5; i++) {
            CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
            if(i == 1) {
                shipment.setDispatchDate(cal.getTime());
            }
            cargoBookingDAO.save(shipment);
        }
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) +1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDate", ServiceUtils.formatDate(cal.getTime()));
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/shipments")).content(getObjectMapper()
                .writeValueAsBytes(jsonObject)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void testUpdateShipment() throws Exception {
       CargoBooking shipment = cargoBookingTestService.createNew(freeBookingSequence);
        shipment = cargoBookingDAO.save(shipment);
        shipment.setFromEmail("newemail@email.com");
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/shipment/"+shipment.getId())
                .content(getObjectMapper().writeValueAsBytes(shipment))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromEmail").value(shipment.getFromEmail()));
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        assertEquals(1, shipments.size());
        assertEquals(shipment.getFromEmail(), shipments.get(0).getFromEmail());
    }

    @Test
    public void testUpdateShipmentUnSetFields() throws Exception {
        CargoBooking shipment = cargoBookingTestService.createNew(freeBookingSequence);
        shipment = cargoBookingDAO.save(shipment);
        ResultActions actions = mockMvc.perform(asUser(put("/api/v1/shipment/"+shipment.getId())
                .content(getObjectMapper().writeValueAsBytes(shipment))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        assertEquals(1, shipments.size());
    }
    @Test
    public void testGetShipment() throws Exception {
        BranchOffice b1 = branchOfficeDAO.save(new BranchOffice());
        BranchOffice b2 = branchOfficeDAO.save(new BranchOffice());
        CargoBooking shipment = cargoBookingTestService.createNew(freeBookingSequence);
        shipment.setFromBranchId(b1.getId());
        shipment.setToBranchId(b2.getId());
        shipment = cargoBookingDAO.save(shipment);
        shipment.setCreatedBy(currentUser.getId());
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/shipment/" + shipment.getId()), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromEmail").value(shipment.getFromEmail()));
    }
    @Test
    public void testDeleteShipment() throws Exception {
        CargoBooking shipment = cargoBookingTestService.createNew(freeBookingSequence);
        shipment = cargoBookingDAO.save(shipment);
        ResultActions actions = mockMvc.perform(asUser(delete("/api/v1/shipment/" + shipment.getId()), currentUser));
        actions.andExpect(status().isOk());
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        assertEquals(0, shipments.size());
    }

    @Test
    public void testSaveFreeShipment() throws Exception {
        OperatorAccount operatorAccount = new OperatorAccount();
        sessionManager.setOperatorId(operatorAccount.getId());
        BranchOffice fromBranch = new BranchOffice("FromBranch", "1234");
        BranchOffice toBranch = new BranchOffice("ToBranch", "1234");
        fromBranch.setOperatorId(operatorAccount.getId());
        toBranch.setOperatorId(operatorAccount.getId());
        fromBranch = branchOfficeManager.save(fromBranch);
        toBranch = branchOfficeManager.save(toBranch);
        CargoBooking shipment = cargoBookingTestService.createNew(freeBookingSequence);
        shipment.setFromBranchId(fromBranch.getId());
        shipment.setToBranchId(toBranch.getId());

        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/shipment").content(getObjectMapper()
                .writeValueAsBytes(shipment)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromBranchId").value(shipment.getFromBranchId()));
        actions.andExpect(jsonPath("$.toBranchId").value(shipment.getToBranchId()));
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        assertEquals(1, shipments.size());
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(0, currentUser.getAmountToBePaid(), 0.0);
    }

    @Test
    public void testSavePaidBooking() throws Exception {
        OperatorAccount operatorAccount = new OperatorAccount();
        sessionManager.setOperatorId(operatorAccount.getId());
        BranchOffice fromBranch = new BranchOffice("FromBranch", "1234");
        BranchOffice toBranch = new BranchOffice("ToBranch", "1234");
        fromBranch.setOperatorId(operatorAccount.getId());
        toBranch.setOperatorId(operatorAccount.getId());
        fromBranch = branchOfficeManager.save(fromBranch);
        toBranch = branchOfficeManager.save(toBranch);
        CargoBooking shipment = cargoBookingTestService.createNew(paidBookingSequence);
        shipment.setFromBranchId(fromBranch.getId());
        shipment.setToBranchId(toBranch.getId());
        shipment.setTotalCharge(150);
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/shipment").content(getObjectMapper()
                .writeValueAsBytes(shipment)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromBranchId").value(shipment.getFromBranchId()));
        actions.andExpect(jsonPath("$.toBranchId").value(shipment.getToBranchId()));
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        assertEquals(1, shipments.size());
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(150, currentUser.getAmountToBePaid(), 0.0);

        //test cancel
        actions = mockMvc.perform(asUser(put("/api/v1/shipment/cancel/"+shipments.get(0).getId()), currentUser));
        actions.andExpect(status().isOk());
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(150.0, currentUser.getAmountToBePaid(), 0.0);
    }

    @Test
    public void testPayToPayBooking() throws Exception {
        Supplier supplier = supplierDAO.save(new Supplier());
        OperatorAccount operatorAccount = new OperatorAccount();
        sessionManager.setOperatorId(operatorAccount.getId());
        BranchOffice fromBranch = new BranchOffice("FromBranch", "1234");
        BranchOffice toBranch = new BranchOffice("ToBranch", "1234");
        fromBranch.setOperatorId(operatorAccount.getId());
        toBranch.setOperatorId(operatorAccount.getId());
        fromBranch = branchOfficeManager.save(fromBranch);
        toBranch = branchOfficeManager.save(toBranch);
        CargoBooking shipment = cargoBookingTestService.createNew(toPaySequence);
        shipment.setFromBranchId(fromBranch.getId());
        shipment.setToBranchId(toBranch.getId());
        shipment.setTotalCharge(150);
        shipment.setSupplierId(supplier.getId());
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/shipment").content(getObjectMapper()
                .writeValueAsBytes(shipment)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.fromBranchId").value(shipment.getFromBranchId()));
        actions.andExpect(jsonPath("$.toBranchId").value(shipment.getToBranchId()));
        List<CargoBooking> shipments = IteratorUtils.toList(cargoBookingDAO.findAll().iterator());
        assertEquals(1, shipments.size());
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(0, currentUser.getAmountToBePaid(), 0.0);
        //pay ToPay booking
        actions = mockMvc.perform(asUser(put("/api/v1/shipment/deliver/"+shipments.get(0).getId()).content(
                "Delivered by Srini".getBytes()).contentType(MediaType.TEXT_PLAIN_VALUE), currentUser));
        actions.andExpect(status().isOk());
        currentUser = userDAO.findById(currentUser.getId()).get();
        assertEquals(150, currentUser.getAmountToBePaid(), 0.0);

        //deliver booking which is already delivered
        actions = mockMvc.perform(asUser(put("/api/v1/shipment/deliver/"+shipments.get(0).getId())
                .content("Delivered by Srini".getBytes()).contentType(MediaType.TEXT_PLAIN_VALUE), currentUser));
        actions.andExpect(status().isBadRequest());
    }

    /**
     * Canceling a paid booking should deduct the user balance
     */
    @Test
    public void testCancelPaidBooking() throws Exception {
        CargoBooking cargoBooking = new CargoBooking();
        cargoBooking.setShipmentNumber("8800");
        cargoBooking.setPaymentType("Paid");
        cargoBooking.setTotalCharge(100);
        cargoBooking.setCreatedBy(currentUser.getId());
        cargoBooking.setForUser(currentUser.getId());
        cargoBooking = cargoBookingDAO.save(cargoBooking);
        assertNull(cargoBooking.getCancelledBy());
        User canceledBy = userDAO.save(new User("testCancel"));
        ResultActions resultActions = mockMvc.perform(asUser(put("/api/v1/shipment/cancel/"+cargoBooking.getId()), canceledBy));
        resultActions.andExpect(status().isOk());
        CargoBooking updatedShipment = cargoBookingDAO.findById(cargoBooking.getId()).get();
        assertEquals(CargoTransitStatus.CANCELLATION_PENDING,updatedShipment.getCargoTransitStatus());
        assertNotNull(updatedShipment.getCancelledBy());

        //Approving cancellation
        JSONObject data = new JSONObject();
        data.put("shipmentId",cargoBooking.getId());
        data.put("reason","Address incorrect");

        User approvedBy = userDAO.save(new User("approvedBy"));
        resultActions = mockMvc.perform(asUser(put("/api/v1/shipment/approveCancellation").content(getObjectMapper()
                .writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON), approvedBy));
        resultActions.andExpect(status().isOk());
        updatedShipment = cargoBookingDAO.findById(cargoBooking.getId()).get();
        assertEquals(CargoTransitStatus.CANCELLED,updatedShipment.getCargoTransitStatus());
        assertNotNull(updatedShipment.getCancellationReason());
        User user = userDAO.findById(currentUser.getId()).get();
        assertEquals(-100.0,user.getAmountToBePaid(),0.0);
        User user1 = new User();
        user1.setUserName("user 1");
        user1.setAmountToBePaid(300);
        user1 = userDAO.save(user1);
        CargoBooking cargoBooking1 = new CargoBooking();
        cargoBooking1.setShipmentNumber("cargoBooking1-8867");
        cargoBooking1.setPaymentType("Paid");
        cargoBooking1.setTotalCharge(50);
        cargoBooking1.setCargoTransitStatus(CargoTransitStatus.CANCELLATION_PENDING);
        cargoBooking1.setCancelledBy(user1.getId());
        cargoBooking1.setForUser(user1.getId());
        cargoBooking1 = cargoBookingDAO.save(cargoBooking1);
        data = new JSONObject();
        data.put("shipmentId",cargoBooking1.getId());
        data.put("reason","");
        resultActions = mockMvc.perform(asUser(put("/api/v1/shipment/approveCancellation").content(getObjectMapper()
                .writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON), currentUser));
        resultActions.andExpect(status().isOk());
        updatedShipment = cargoBookingDAO.findById(cargoBooking1.getId()).get();
        assertEquals(CargoTransitStatus.CANCELLED,updatedShipment.getCargoTransitStatus());
        user1 = userDAO.findById(cargoBooking1.getCancelledBy()).get();
        assertEquals(250.0,user1.getAmountToBePaid(),0.0);
    }

}
