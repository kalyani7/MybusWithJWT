package com.mybus.dao;

import com.mybus.model.Booking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by skandula on 5/7/16.
 */
@Repository
public interface BookingDAO extends PagingAndSortingRepository<Booking, String> {
    Iterable<Booking> findByServiceReportId(String serviceId);
    Iterable<Booking> findByServiceReportIdAndHasValidAgent(String id, boolean hasValidAgent);
    Iterable<Booking> findByFormId(String formId);
    Iterable<Booking> findByDue(boolean due);
    List<Booking> findByPhoneNo(String phoneNumber);
    Iterable<Booking> findByBookedByAndDue(String bookedBy, boolean due);
    Iterable<Booking> findByBookedByAndHasValidAgent(String bookedBy, boolean hasValidAgent);
    Booking findByTicketNoAndOperatorIdAndServiceReportId(String ticketNo, String operatorId, String serviceReportId);
    Booking findByTicketNoAndDue(String ticketNo, boolean due);
    void deleteByServiceReportId(String serviceId);
    void deleteByFormId(String formId);

    List<Booking> findByPhoneNoAndOperatorId(String phoneNumber, String operatorId);
}
