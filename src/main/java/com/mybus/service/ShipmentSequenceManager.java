package com.mybus.service;

import com.mongodb.client.result.UpdateResult;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.CargoBooking;
import com.mybus.model.PaymentStatus;
import com.mybus.model.cargo.ShipmentSequence;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by busda001 on 7/8/17.
 */
@Service
public class ShipmentSequenceManager {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ShipmentSequenceDAO shipmentSequenceDAO;

    /**
     * Set the shipment sequence on the cargoshipment object
     * @param shipmentSequence
     * @return
     */
    private ShipmentSequence nextSequeceNumber(ShipmentSequence shipmentSequence) {
        Update updateOp = new Update();
        updateOp.inc("nextNumber", 1);
        final Query query = new Query();
        query.addCriteria(where("shipmentCode").is(shipmentSequence.getShipmentCode()));
        //query.addCriteria(where("shipmentCode").is(ShipmentSequence.FREE_TYPE));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, ShipmentSequence.class);
        if(writeResult.getModifiedCount() == 1){
            shipmentSequence = shipmentSequenceDAO.findByShipmentCode(shipmentSequence.getShipmentCode());
        } else {
            throw new IllegalStateException("next number failed");
        }
        return shipmentSequence;
    }

    public Iterable<ShipmentSequence> getShipmentTypes(){
        return shipmentSequenceDAO.findAll(new Sort(Sort.Direction.DESC,"shipmentType"));
    }

    /**
     * Get shipment names as Map<ID,NAME>
     * @return
     */
    public Map<String, String> getShipmentNamesMap(){
        List<ShipmentSequence> shipmentSequences =  IteratorUtils.toList(getShipmentTypes().iterator());
        return shipmentSequences.stream().collect(Collectors.toMap(ShipmentSequence::getId,
                ShipmentSequence::getShipmentType));
    }

    /**
     * Set the shipment number and the payment status
     * @param shipment
     */
    public void preProcess(CargoBooking shipment) {
        ShipmentSequence shipmentSequence = shipmentSequenceDAO.findByShipmentType(shipment.getPaymentType());
        if(shipmentSequence == null) {
            throw new BadRequestException("Invalid shipment code");
        }
        if(shipment.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString()) && shipment.getSupplierId() == null){
            throw new BadRequestException("Invalid supplier ID");
        }
        shipmentSequence = nextSequeceNumber(shipmentSequence);
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(shipment.getDispatchDate());
        String shipmentNumber = String.format("%s-%d-%d-%d-%d", shipmentSequence.getShipmentCode(),
                currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH)+1, currentDate.get(Calendar.DATE),shipmentSequence.nextNumber);
        shipment.setShipmentNumber(shipmentNumber);
    }
}
