package com.mybus.service;

import com.mybus.dao.OfficeExpenseDAO;
import com.mybus.dao.VehicleDAO;
import com.mybus.dao.impl.OfficeExpenseMongoDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

/**
 * Created by srinikandula on 12/12/16.
 */

@Service
public class OfficeExpenseManager {
    private static final Logger logger = LoggerFactory.getLogger(OfficeExpenseManager.class);

    @Autowired
    private OfficeExpenseDAO officeExpenseDAO;

    @Autowired
    private OfficeExpenseMongoDAO officeExpenseMongoDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private VehicleDAO vehicleDAO;

    Map<String, Vehicle> vehicleMap = new HashMap<>();

    @PostConstruct
    public void init() {
        List<Vehicle> vehicles = IteratorUtils.toList(vehicleDAO.findAll().iterator());
        vehicles.stream().forEach(vehicle -> {
            vehicleMap.put(vehicle.getId(), vehicle);
        });
    }
    public OfficeExpense save(OfficeExpense officeExpense) {
        officeExpense.setOperatorId(sessionManager.getOperatorId());
        return officeExpenseMongoDAO.save(officeExpense);
    }

    public OfficeExpense updateOfficeExpense(OfficeExpense officeExpense) {
        logger.debug("updating balance for office:" + officeExpense.getBranchOfficeId());
        if(officeExpense.getStatus() != null && (officeExpense.getStatus().equals(Payment.STATUS_APPROVED))){
            userMongoDAO.updateCashBalance(officeExpense.getCreatedBy(), (0-officeExpense.getAmount()));
        }
        return officeExpenseDAO.save(officeExpense);
    }

    /**
     * Approve or reject expenses
     * @param ids
     * @param approve
     * @return
     */
    public List<OfficeExpense> approveOrRejectExpenses(List<String> ids, boolean approve) {
        List<OfficeExpense> officeExpenses = new ArrayList<>();
        User currentUser = sessionManager.getCurrentUser();
        ids.stream().forEach(id -> {
            OfficeExpense officeExpense = officeExpenseDAO.findById(id).get();
            if(officeExpense.getStatus() != null){
                throw new BadRequestException("OfficeExpense has invalid status");
            }
            if(approve){
                if(userMongoDAO.updateCashBalance(officeExpense.getCreatedBy(), (0-officeExpense.getAmount()))){
                    officeExpense.setStatus(ApproveStatus.Approved);
                    officeExpense.setReviewedBy(currentUser.getId());
                    officeExpense.setApprovedOn(new Date());
                    officeExpenseDAO.save(officeExpense);
                } else {
                    throw new BadRequestException("Failed to approve Office expense");
                }
            } else {
                officeExpense.setStatus(ApproveStatus.Rejected);
                officeExpense.setReviewedBy(currentUser.getId());
                officeExpense.setApprovedOn(new Date());
                officeExpenseDAO.save(officeExpense);
            }
            officeExpenses.add(officeExpense);
        });
        return officeExpenses;
    }

    public void delete(String id) {
        OfficeExpense officeExpense = officeExpenseDAO.findById(id).get();
        if(officeExpense != null && officeExpense.getStatus() != null) {
            throw new BadRequestException("officeExpense can not be deleted");
        }
        officeExpenseDAO.delete(officeExpense);
    }

    /**
     * Load office expenses with user names
     * @param officeExpenses
     */
    private void loadUserNames(List<OfficeExpense> officeExpenses) {
        officeExpenses.forEach(officeExpense -> {
            serviceUtils.fillInUserNames(officeExpense);
            fillInVehicleNumber(officeExpense);
        });
    }


    public long findCount(boolean pending) {
        return officeExpenseMongoDAO.count(pending);
    }
    public Page<OfficeExpense> findPendingOfficeExpenses(Pageable pageable) {
        Page<OfficeExpense> page = officeExpenseMongoDAO.findPendingExpenses(pageable);
        loadUserNames(page.getContent());
        return page;
    }
    public Page<OfficeExpense> findNonPendingOfficeExpenses(Pageable pageable) {
        Page<OfficeExpense> page = officeExpenseMongoDAO.findNonPendingExpenses(pageable);
        loadUserNames(page.getContent());
        return page;
    }

    public OfficeExpense findOne(String id) {
        OfficeExpense payment = officeExpenseDAO.findById(id).get();
        if(payment == null) {
            throw new BadRequestException("No Payment found");
        }
        serviceUtils.fillInUserNames( payment);
        return payment;
    }

    public Page<OfficeExpense> findOfficeExpenseByDate(String date, Pageable pageable) {
        Page<OfficeExpense> officeExpenses = officeExpenseMongoDAO.findOfficeExpensesByDate(date, pageable);
        serviceUtils.fillInUserNames(officeExpenses.getContent());
        return officeExpenses;
    }

    public List<OfficeExpense> findOfficeExpenses(JSONObject query, Pageable pageable) throws ParseException {
        List<OfficeExpense> expenses = officeExpenseMongoDAO.searchOfficeExpenses(query,pageable);
        serviceUtils.fillInUserNames(expenses);
        expenses.stream().forEach(expense -> {
            fillInVehicleNumber(expense);
        });
        return expenses;
    }

    private void fillInVehicleNumber(OfficeExpense officeExpense) {
        if(officeExpense.getVehicleId() != null) {
            Vehicle vehicle = vehicleMap.get(officeExpense.getVehicleId());
            if(vehicle != null) {
                officeExpense.getAttributes().put("vehicle", vehicle.getRegNo());
            }
        }
    }
}
