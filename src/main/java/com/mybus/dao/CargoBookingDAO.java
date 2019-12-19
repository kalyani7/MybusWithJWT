package com.mybus.dao;

import com.mybus.model.CargoBooking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by srinikandula on 12/10/16.
 */
@Repository
public interface CargoBookingDAO extends PagingAndSortingRepository<CargoBooking, String> {
    CargoBooking findByIdAndOperatorId(String id, String operatorId);
    List<CargoBooking> findByPaymentTypeAndOperatorId(String paymentType, String operatorId);
    List<CargoBooking> findByFromBranchId(String fromBranchId, String operatorId);
    List<CargoBooking> findByToBranchId(String toBranchId, String operatorId);
    List<CargoBooking> findOneByFromContactAndOperatorId(long contact, String operatorId);
    List<CargoBooking> findOneByToContactAndOperatorId(long contact, String operatorId);
}
