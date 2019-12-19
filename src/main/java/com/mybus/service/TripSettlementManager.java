package com.mybus.service;

import com.mybus.dao.StaffDAO;
import com.mybus.dao.TripSettlementDAO;
import com.mybus.dao.impl.*;
import com.mybus.dto.TripSettlementDTO;
import com.mybus.model.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TripSettlementManager {

    @Autowired
    private TripSettlementDAO tripSettlementDAO;

    @Autowired
    private TripSettlementMongoDAO tripSettlementMongoDAO;

    @Autowired
    private TSReceiptsMongoDAO tsReceiptsMongoDAO;

    @Autowired
    private TSBankTransfersMongoDAO tsBankTransfersMongoDAO;

    @Autowired
    private TripSettlementsReceiptsManager tripSettlementsReceiptsManager;

    @Autowired
    private TripPaymentsManager tripPaymentsManager;

    @Autowired
    private BankTransfersManager bankTransfersManager;

    @Autowired
    private VehicleManager vehicleManager;

    @Autowired
    private  StaffManager staffManager;

    @Autowired
    private TSPaymentsMongoDAO tsPaymentsMongoDAO;

    @Autowired
    private TSOtherExpensesMongoDAO tsOtherExpensesMongoDAO;

    @Autowired
    private TSOtherExpensesManager tsOtherExpensesManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TripExpensesManager tripExpensesManager;

    @Autowired
    private StaffDAO staffDAO;


    public long count(JSONObject query) throws ParseException {
        return tripSettlementMongoDAO.getTripSettlementsCount(query);
    }

    public Page<TripSettlement> search(JSONObject query, Pageable pageable) throws ParseException {
        long total = count(query);
        List<TripSettlement> tripSettlements = tripSettlementMongoDAO.getAllTripSettlements(query,pageable);
        Map<String,String> vehicleNamesMap = vehicleManager.findVehicleNumbers();
        Map<String,String> driverNamesMap = staffManager.findStaffNames();
        tripSettlements.stream().forEach(tripSettlement -> {
            tripSettlement.getAttributes().put("RegNo",vehicleNamesMap.get(tripSettlement.getVehicleId()));
            tripSettlement.getAttributes().put("driver",driverNamesMap.get(tripSettlement.getDriverId()));
        });
        return new PageImpl<>(tripSettlements, pageable, total);
    }

    public boolean update(TripSettlement tripSettlement, String tripSettlementId) {
        return tripSettlementMongoDAO.updateTripSettlement(tripSettlement,tripSettlementId);
    }

    public TripSettlement saveTripSettlement(TripSettlement tripSettlement) {
        tripSettlement.setOperatorId(sessionManager.getOperatorId());
        return tripSettlementDAO.save(tripSettlement);
    }

    public boolean deleteTripSettlement(String tripSettlementId) {
        tripSettlementDAO.deleteById(tripSettlementId);
        return true;
    }

    public TripSettlementDTO getTripSettlement(String tripSettlementId, JSONObject data) throws ParseException {
        data.put("tripSettlementId",tripSettlementId);
        TripSettlementDTO tripSettlementDTO = new TripSettlementDTO();
        List<TSReceipt> tsReceiptList = tripSettlementsReceiptsManager.getAllTripSettlementsReceipts(data, PageRequest.of(0,Integer.MAX_VALUE)).getContent();
        List<TSPayment> tsPaymentList = tripPaymentsManager.getTripPayments(data, PageRequest.of(0,Integer.MAX_VALUE)).getContent();
        List<TSBankTransfer> tsBankTransfers = bankTransfersManager.getBankTransfers(data, PageRequest.of(0,Integer.MAX_VALUE)).getContent();
        List<TSOtherExpenses> tsOtherExpenses = tsOtherExpensesManager.getTSOtherExpenses(data, PageRequest.of(0,Integer.MAX_VALUE)).getContent();
        List<TripExpenses> tripExpenses = tripExpensesManager.findAllByTripSettlementId(tripSettlementId);
        tripSettlementDTO.setTsBankTransfer(tsBankTransfers);
        tripSettlementDTO.setTripSettlement(tripSettlementDAO.findById(tripSettlementId).get());
        tripSettlementDTO.setTsPayment(tsPaymentList);
        tripSettlementDTO.setTsReceipt(tsReceiptList);
        tripSettlementDTO.setTsOtherExpenses(tsOtherExpenses);
        tripSettlementDTO.setTripExpenses(tripExpenses);
        Optional<Staff> driver = staffDAO.findById(tripSettlementDTO.getTripSettlement().getDriverId());
        if(driver.isPresent()) {
            tripSettlementDTO.setDriverName(driver.get().getDisplayName());
        }

        return tripSettlementDTO;
    }
    /**
     * Update the total numbers of trip settlement
     *
     * @param tripsettlementId
     */
    public void updateTripSettlementTotals(String tripsettlementId) {
        TripSettlement tripSettlement = tripSettlementDAO.findById(tripsettlementId).get();
        if(tripSettlement != null) {
            //get total for tripSettlementReceipts
            double totalCollection = tsReceiptsMongoDAO.getReceiptsTotal(tripsettlementId);
            //get total for TSBankTransfer
            double bankTransfersTotal = tsBankTransfersMongoDAO.getBankTransfersTotal(tripsettlementId);
            //get total for TripSettlementPayments
            double dieselExpenses = tsPaymentsMongoDAO.getDieselExpenses(tripsettlementId);
            //get total for trip other expenses
            double tripOtherExpenses = tsOtherExpensesMongoDAO.getTripOtherExpensesTotal(tripsettlementId);
            double tripExpenses = tripSettlement.getTripExpenses();
            tripSettlementMongoDAO.updateBalances(tripsettlementId,totalCollection, bankTransfersTotal,dieselExpenses,tripOtherExpenses,tripSettlement.getBalanceReceived(),tripExpenses,tripSettlement.getBalancePaid());
        }
    }
}
