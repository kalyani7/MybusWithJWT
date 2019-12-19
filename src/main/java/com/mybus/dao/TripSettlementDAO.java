package com.mybus.dao;

import com.mybus.model.TripSettlement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripSettlementDAO extends MongoRepository<TripSettlement, String> {
    Iterable<TripSettlement> findByVehicleId(String vehicleId);
}
