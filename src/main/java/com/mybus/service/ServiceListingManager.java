package com.mybus.service;

import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.ServiceListingDAO;
import com.mybus.dao.impl.ServiceListingMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OperatorAccount;
import com.mybus.model.ServiceListing;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ServiceListingManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceListingManager.class);

    @Autowired
    private ServiceListingMongoDAO serviceListingMongoDAO;

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private AbhiBusPassengerReportService abhiBusPassengerReportService;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private ServiceListingDAO serviceListingDAO;
    /**
     * If the listing are not found and if the operator is Abhibus download them now
     * @param date
     * @return
     * @throws Exception
     */
    public Iterable<ServiceListing> getServiceListings(String date) throws Exception {
         OperatorAccount operatorAccount = operatorAccountDAO.findById(sessionManager.getOperatorId()).get();
        if(operatorAccount == null){
            throw new BadRequestException("No Operator found");
        }
        Iterable<ServiceListing> serviceListings = serviceListingMongoDAO
                .getServiceListing(date, sessionManager.getOperatorId());

        if(!serviceListings.iterator().hasNext()) {
            if (operatorAccount.getProviderType().equalsIgnoreCase(OperatorAccount.ABHIBUS)) {
                serviceListings = abhiBusPassengerReportService.getActiveServicesByDate(date);
            }
        }
        return serviceListings;
    }
    public ServiceListing saveServiceListing(ServiceListing serviceListing) {
        if(serviceListingDAO.findByJDateAndServiceNumberAndOperatorId(serviceListing.getJDate(), serviceListing.getServiceNumber(), serviceListing.getOperatorId()) == null){
            return serviceListingDAO.save(serviceListing);
        }
        return null;
    }

    public List<ServiceListing> getServiceListingsForDailyTrip(String date, Set<String> serviceNames) throws Exception {
//        serviceNumber
        OperatorAccount operatorAccount = operatorAccountDAO.findById(sessionManager.getOperatorId()).get();
        if(operatorAccount == null){
            throw new BadRequestException("No Operator found");
        }
        Iterable<ServiceListing> serviceListings = serviceListingMongoDAO
                .getServiceListingForDailyTrips(date,serviceNames, sessionManager.getOperatorId());

        if(!serviceListings.iterator().hasNext()) {
            if (operatorAccount.getProviderType().equalsIgnoreCase(OperatorAccount.ABHIBUS)) {
                serviceListings = abhiBusPassengerReportService.getActiveServicesByDate(date);
            }
        }
        return IteratorUtils.toList(serviceListings.iterator());
    }

}
