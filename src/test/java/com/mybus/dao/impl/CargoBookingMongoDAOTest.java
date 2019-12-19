package com.mybus.dao.impl;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.CargoBookingDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.model.CargoBooking;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.util.CargoBookingTestService;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CargoBookingMongoDAOTest{

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private CargoBookingDAO cargoBookingDAO;

    @Autowired
    private ShipmentSequenceDAO shipmentSequenceDAO;
    @Before
    @After
    public void cleanup() {
        shipmentSequenceDAO.deleteAll();
        cargoBookingDAO.deleteAll();
    }

    @Autowired
    private CargoBookingTestService cargoBookingTestService;


    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Test
    public void testFindShipments() throws Exception {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.save(new ShipmentSequence("F", "Free"));
        String fromBranchId = null;
        String toBranchId = null;

        for(int i=0; i<5; i++) {
            CargoBooking shipment = cargoBookingTestService.createNew(shipmentSequence);
            fromBranchId = shipment.getFromBranchId();
            toBranchId = shipment.getToBranchId();
            shipment = cargoBookingDAO.save(shipment);
            System.out.println(shipment.getId());
        }
        JSONObject query = new JSONObject();
        query.put("fromBranchId", fromBranchId);
        List<CargoBooking> shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(1, shipments.size());
        query.put("fromBranchId", 1234);
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(0, shipments.size());

        //test with toCityId
        query.remove("fromBranchId");
        query.put("toBranchId", toBranchId);
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(1, shipments.size());
        query.put("toBranchId", "12345");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(0, shipments.size());

        //test my email
        query.remove("toBranchId");
        query.put("fromEmail", "email@e.com");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(5, shipments.size());

        //change email and test
        CargoBooking shipment = shipments.get(0);
        shipment.setFromEmail("srini@email.com");
        cargoBookingDAO.save(shipment);
        query.put("fromEmail", "srini@email.com");
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, null, query, null).iterator());
        assertEquals(1, shipments.size());

        //test querying only email field
        String fields[] = {"fromEmail"};
        shipments = IteratorUtils.toList(
                mongoQueryDAO.getDocuments(CargoBooking.class, CargoBooking.COLLECTION_NAME, fields, query, null).iterator());
        assertEquals(1, shipments.size());
        shipment = shipments.get(0);
        assertEquals("srini@email.com", shipment.getFromEmail());
        assertNull(shipment.getFromBranchId());
        assertNull(shipment.getToBranchId());
    }

}