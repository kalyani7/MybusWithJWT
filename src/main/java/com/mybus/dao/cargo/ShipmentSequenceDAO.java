package com.mybus.dao.cargo;

import com.mybus.model.cargo.ShipmentSequence;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by busda001 on 7/8/17.
 */
public interface ShipmentSequenceDAO extends PagingAndSortingRepository<ShipmentSequence, String> {
    ShipmentSequence findByShipmentCode(String shipmentCode);
    ShipmentSequence findByShipmentType(String shipmentType);
}
