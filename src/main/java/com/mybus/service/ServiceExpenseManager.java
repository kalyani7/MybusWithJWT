package com.mybus.service;

import com.mybus.dao.ServiceExpenseDAO;
import com.mybus.dao.ServiceListingDAO;
import com.mybus.dao.SupplierDAO;
import com.mybus.dao.impl.ServiceExpenseMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.ServiceExpense;
import com.mybus.model.ServiceListing;
import com.mybus.model.Supplier;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceExpenseManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExpenseManager.class);

    @Autowired
    private ServiceExpenseDAO serviceExpenseDAO;

    @Autowired
    private ServiceExpenseMongoDAO serviceExpenseMongoDAO;

    @Autowired
    private SupplierDAO fillingStationDAO;

    @Autowired
    private ServiceListingDAO serviceListingDAO;

    @Autowired
    private SessionManager sessionManager;
    private Map<String, Supplier> fillingStationMap = new HashMap<>();

    @PostConstruct
    public void init(){
        Iterable<Supplier> fillingStations = fillingStationDAO.findAll();
        fillingStations.forEach(fillingStation -> {
            fillingStationMap.put(fillingStation.getId(), fillingStation);
        });
    }

    public ServiceExpense save(ServiceExpense serviceExpense) {
        serviceExpense.validate();
        /*serviceExpense.setNetRealization(
                - parseFloat($scope.serviceExpense.fuelCost)
                + parseFloat($scope.serviceExpense.paidLuggage)
                + parseFloat($scope.serviceExpense.toPayLuggage)
                - parseFloat($scope.serviceExpense.driverSalary1)
                - parseFloat($scope.serviceExpense.driverSalary2)
                - parseFloat($scope.serviceExpense.cleanerSalary);
                */
        serviceExpense.setOperatorId(sessionManager.getOperatorId());
        return serviceExpenseDAO.save(serviceExpense);
    }

    public ServiceExpense getServiceExpense(String id) {
        return loadServiceInfo(serviceExpenseDAO.findById(id).get());
    }
    /**
     * Load the information from servicereport
     * @param serviceExpense
     * @return
     */
    private ServiceExpense loadServiceInfo(ServiceExpense serviceExpense) {
        if(serviceExpense == null) {
            return null;
        }
        serviceExpense.validate();
        ServiceListing serviceListing = serviceListingDAO.findByIdAndOperatorId
                (serviceExpense.getServiceListingId(), sessionManager.getOperatorId());
        if(serviceListing != null) {
            serviceExpense.getAttributes().put("to", serviceListing.getDestination());
            serviceExpense.getAttributes().put("from", serviceListing.getSource());
            serviceExpense.getAttributes().put("busType", serviceListing.getBusType());
            serviceExpense.getAttributes().put("VehicleNumber", serviceListing.getVehicleRegNumber());
        }
        if(serviceExpense.getFillingStationId() != null) {
            Supplier fillingStation = fillingStationDAO.findById(serviceExpense.getFillingStationId()).get();
            serviceExpense.getAttributes().put("fillingStation", fillingStation.getName());
        }
        return serviceExpense;
    }


    public List<ServiceExpense> getServiceExpenses(String journeyDate) {
        List<ServiceExpense> serviceExpenses = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("startDate", journeyDate);
            jsonObject.put("endDate", journeyDate);
            List<ServiceExpense> expenses = serviceExpenseMongoDAO.searchServiceExpenses(jsonObject, null);
            if(!expenses.isEmpty()) {
                for(ServiceExpense expense: expenses) {
                    ServiceExpense loadDataExpense = loadServiceInfo(expense);
                    loadFillingStationInfo(loadDataExpense);
                    serviceExpenses.add(loadDataExpense);
                }
            }
        } catch (ParseException e) {
            logger.error("Error finding service expenses ", e);
        }
        return serviceExpenses;
    }


    private void loadFillingStationInfo(ServiceExpense serviceExpense) {
        if(serviceExpense.getFillingStationId() != null) {
            serviceExpense.getAttributes().put("fillingStationName",
                    fillingStationMap.get(serviceExpense.getFillingStationId()).getName());
        }
    }
    /**
     * Update the data with
     * @param serviceExpense
     */
    public void updateFromServiceReport(ServiceExpense serviceExpense) {
        if(serviceExpense != null) {
            ServiceExpense savedExpense = serviceExpenseDAO.findById(serviceExpense.getId()).get();
            savedExpense.setFuelQuantity(serviceExpense.getFuelQuantity());
            savedExpense.setFuelRate(serviceExpense.getFuelRate());
            savedExpense.setFuelCost(serviceExpense.getFuelCost());
            savedExpense.setDriverSalary1(serviceExpense.getDriverSalary1());
            savedExpense.setDriverSalary2(serviceExpense.getDriverSalary2());
            savedExpense.setCleanerSalary(serviceExpense.getCleanerSalary());
            savedExpense.setPaidLuggage(serviceExpense.getPaidLuggage());
            savedExpense.setToPayLuggage(serviceExpense.getToPayLuggage());
            savedExpense.setNetRealization(serviceExpense.getNetRealization());
            serviceExpenseDAO.save(savedExpense);
        }

    }

    public List<ServiceExpense> findServiceExpenses(JSONObject query, Pageable pageable) throws ParseException {
        query = ServiceUtils.addOperatorId(query, sessionManager);
        List<ServiceExpense>  serviceExpenses = serviceExpenseMongoDAO.searchServiceExpenses(query, pageable);
        for(ServiceExpense expense: serviceExpenses) {
            loadServiceInfo(expense);
            loadFillingStationInfo(expense);
        }
        return serviceExpenses;
    }

    public void deleteServiceExpense(String id) {
        if(id == null){
            throw new BadRequestException("Invalid id");
        }
        serviceExpenseDAO.deleteById(id);
    }
}
