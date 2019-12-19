package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.dao.*;
import com.mybus.dao.impl.CargoBookingMongoDAO;
import com.mybus.dto.AllBranchBookingSummary;
import com.mybus.dto.BranchCargoBookingsSummary;
import com.mybus.dto.BranchDeliverySummary;
import com.mybus.dto.UserCargoBookingsSummary;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.model.cargo.ShipmentSequence;
import com.mybus.util.ServiceUtils;
import org.bson.Document;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by srinikandula on 12/10/16.
 */
@Service
public class CargoBookingManager {
    private static final Logger logger = LoggerFactory.getLogger(CargoBookingManager.class);

    @Autowired
    private CargoBookingDAO cargoBookingDAO;

//    @Autowired
//    private CargoBookingDailyTotalsDAO cargoBookingDailyTotals;
    @Autowired
    private CargoBookingMongoDAO cargoBookingMongoDAO;

    @Autowired
    private ShipmentSequenceManager shipmentSequenceManager;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private SMSManager smsManager;

    @Autowired
    private SupplierDAO supplierDAO;

    private Map<String, String> lrTypes;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private VehicleManager vehicleManager;

    private Map<String, BranchOffice> branchOffices = null;

    private Map<String, String> userNames = null;

    public CargoBooking findOne(String shipmentId) {
        Preconditions.checkNotNull(shipmentId, "shipmentId is required");
        CargoBooking shipment = cargoBookingDAO.findByIdAndOperatorId(shipmentId, sessionManager.getOperatorId());
        Preconditions.checkNotNull(shipment, "No CargoBooking found with id");
        branchOffices = branchOfficeManager.getAllMap();
        loadShipmentDetails(shipment);
        return shipment;
    }

    public List<CargoBooking> findByLRNumber(String LRNumber) {
        Preconditions.checkNotNull(LRNumber, "LRNumber is required");
        List<CargoBooking> shipments = cargoBookingMongoDAO.findShipments(LRNumber);
        Preconditions.checkNotNull(shipments, "No CargoBooking found with LRNumebr",LRNumber);
        return shipments;
    }

    /**
     * save cargo booking
     * @param cargoBooking
     * @return
     */
    public CargoBooking saveWithValidations(CargoBooking cargoBooking) {
        if(cargoBooking.getPaymentType() == null) {
            throw new BadRequestException("getPaymentType missing ");
        }
        if(cargoBooking.getDispatchDate() == null) {
            throw new BadRequestException("dispatch date is missing ");
        }
        shipmentSequenceManager.preProcess(cargoBooking);
        cargoBooking.setOperatorId(sessionManager.getOperatorId());
        List<String> errors = RequiredFieldValidator.validateModel(cargoBooking, CargoBooking.class);
        if( cargoBooking.getItems() != null) {
            for(CargoBookingItem cargoBookingItem: cargoBooking.getItems()){
                //calculate the total articles
                cargoBooking.setTotalArticles(cargoBooking.getTotalArticles()+ cargoBookingItem.getQuantity());
                if(cargoBookingItem.getDescription() == null){
                    errors.add("Missing description for item ");
                }
            }
        }
        if(errors.isEmpty()) {
            cargoBooking.setCreatedAt(new DateTime());
            sendBookingConfirmationSMS(cargoBooking);
            try {
                if (cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.PAID.toString())) {
                    updateUserCashBalance(cargoBooking);
                } else if (cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                    cargoBooking.setDue(true);
                } else if (cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                    cargoBooking.setDue(true);
                    updateOnAccountBalance(cargoBooking.getSupplierId(), cargoBooking.getTotalCharge(), false);
                }
                cargoBooking = cargoBookingDAO.save(cargoBooking);
            }catch (Exception e){
                //cargoBookingDAO.deleteById(cargoBooking.getId());
                throw e;
            }
        } else {
            throw new BadRequestException("Required data missing "+ String.join("<br> ", errors));
        }
        return cargoBooking;
    }

    /**
     * Pay cargobookings of types ToPay or OnAccount
     *
     * @param cargoBooking
     */
    private CargoBooking payCargoBooking(CargoBooking cargoBooking, String deliveryNotes) {
        if(cargoBooking == null || !cargoBooking.isDue()) {
            throw new BadRequestException("Invalid CargoBooking Id");
        } else {
            cargoBooking.setDue(false);
            cargoBooking.setPaidBy(sessionManager.getCurrentUser().getFullName());
            if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                updateUserCashBalance(cargoBooking);
                return cargoBookingDAO.save(cargoBooking);
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                if(cargoBooking.getSupplierId() == null) {
                    throw new BadRequestException("Invalid supplierId on OnAccount shipment");
                }
                updateOnAccountBalance(cargoBooking.getSupplierId(), cargoBooking.getTotalCharge(), true);
                updateUserCashBalance(cargoBooking);
                return cargoBookingDAO.save(cargoBooking);
            } else {
                throw new BadRequestException("Invalid Type for CargoBooking, Only ToPay and OnAccount types can be paid");
            }
        }
    }

    /**
     *
     * @param supplierId
     * @param balance
     * @param isPaymentTransaction true when called from payBooking call, false when called from save booking call
     */

    private void updateOnAccountBalance(String supplierId, double balance, boolean isPaymentTransaction) {
        Supplier supplier = supplierDAO.findById(supplierId).get();
        if(supplier == null) {
            throw new BadRequestException("Invalid supplier on OnAccount shipment");
        }
        if(isPaymentTransaction){
            supplier.setToBeCollected(supplier.getToBeCollected() - balance);
        } else {
            supplier.setToBeCollected(supplier.getToBeCollected() + balance);
        }
        supplierDAO.save(supplier);
    }


    /**
     * Update user cash balance for cargo booking
     * @param cargoBooking
     */
    private void updateUserCashBalance(CargoBooking cargoBooking) {
        Payment payment = paymentManager.createPayment(cargoBooking);
        if(payment == null){
            throw new BadRequestException("Failed to create payment for Cargo Booking");
        }
    }

    public CargoBooking updateShipment(String shipmentId, CargoBooking shipment) {
        Preconditions.checkNotNull(shipmentId, "ShipmentId can not be null");
        CargoBooking shipmentCopy = cargoBookingDAO.findById(shipmentId).get();
        Preconditions.checkNotNull(shipmentCopy, "No CargoBooking found with id");
        try {
            shipmentCopy.merge(shipment, false);
        } catch (Exception e) {
            throw new BadRequestException("Error updating shipment");
        }
        return cargoBookingDAO.save(shipmentCopy);
    }


    public Iterable<CargoBooking> findShipments(JSONObject query, final Pageable pageable) throws ParseException {
        if(logger.isDebugEnabled()) {
            logger.debug("Looking up shipments with {0}", query);
        }
        List<CargoBooking> shipments = cargoBookingMongoDAO.findShipments(query, pageable);
        branchOffices = branchOfficeManager.getAllMap();
        shipments.stream().forEach(shipment -> {
            loadShipmentDetails(shipment);
        });
        return shipments;
    }

    /**
     * Find ToPay booking delivery
     *
     * Note: for now this is used in unit tests
     * @param branchId
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public BranchDeliverySummary findDeliveryShipmentsTotalByBranchUsers(String branchId, PaymentStatus paymentStatus, Date start, Date end) throws ParseException {
        if(logger.isDebugEnabled()) {
            logger.debug("Finding delivered shipments between {} and {}", start, end);
        }
        BranchDeliverySummary summary = cargoBookingMongoDAO.findDeliveryTotalByBranchUsers(branchId,  paymentStatus, start, end);
        return summary;
    }

    public void delete(String shipmentId) {
        Preconditions.checkNotNull(shipmentId, "ShipmentId can not be null");
        CargoBooking shipment = cargoBookingDAO.findById(shipmentId).get();
        Preconditions.checkNotNull(shipment, "No CargoBooking found with id");
        cargoBookingDAO.delete(shipment);
    }

    public Iterable<ShipmentSequence> getShipmentTypes() {
        return shipmentSequenceManager.getShipmentTypes();
    }

    /**
     * Module to populate details in to Cargo Shipment. The details would be like branchOfficeNames, createdBy etc..
     */
    private void loadShipmentDetails(CargoBooking cargoBooking){
        BranchOffice fromBranchOffice = branchOffices.get(cargoBooking.getFromBranchId());
        BranchOffice toBranchOffice =  branchOffices.get(cargoBooking.getToBranchId());
        if(fromBranchOffice != null){
            cargoBooking.getAttributes().put("fromBranchOfficeAddress",String.format("%s, %s",fromBranchOffice.getAddress() , fromBranchOffice.getContact()));
            cargoBooking.getAttributes().put("fromBranchOfficeName",fromBranchOffice.getName());
        }
        if(toBranchOffice != null) {
            cargoBooking.getAttributes().put("toBranchOfficeAddress",String.format("%s, %s",toBranchOffice.getAddress() , toBranchOffice.getContact()));
            cargoBooking.getAttributes().put("toBranchOfficeName",toBranchOffice.getName());
        }
        if(lrTypes != null){
            cargoBooking.getAttributes().put("LRType",lrTypes.get(cargoBooking.getPaymentType()));
        }
        if(userNames != null) {
            if(cargoBooking.getCreatedBy() != null){
                cargoBooking.getAttributes().put("bookedBy",userNames.get(cargoBooking.getCreatedBy()));
            }
            if(cargoBooking.getForUser() != null) {
                cargoBooking.getAttributes().put("forUser",userNames.get(cargoBooking.getForUser()));
            }
        }
    }

    public long count(JSONObject query) throws ParseException {
        return cargoBookingMongoDAO.countShipments(query);
    }

    /**
     * Send SMS for cargobooking
     * @param cargoBooking
     */
    private void sendBookingConfirmationSMS(CargoBooking cargoBooking){
        BranchOffice fromBranchOffice = branchOfficeDAO.findById(cargoBooking.getFromBranchId()).get();
        BranchOffice toBranchOffice = branchOfficeDAO.findById(cargoBooking.getToBranchId()).get();
        String cargoServiceName = "Cargo Services";
        if(sessionManager.getOperatorId() != null) {
            OperatorAccount operatorAccount = operatorAccountDAO.findById(sessionManager.getOperatorId()).get();
            cargoServiceName = operatorAccount.getCargoServiceName();
        }
        String message ="A parcel is booked with LR# "+cargoBooking.getShipmentNumber()+"" +
                " From:"+cargoBooking.getFromName()+"(Ph:"+cargoBooking.getFromContact()+") " +
                "To:"+cargoBooking.getToName()+" At "+fromBranchOffice.getName()+", " +
                "LRType:"+cargoBooking.getPaymentType()+" Amt:"+cargoBooking.getTotalCharge()+"," +
                " Date:"+ ServiceUtils.formatDate(cargoBooking.getCreatedAt().toDate())+" To:"+toBranchOffice.getName()+"" +
                " Contact "+toBranchOffice.getContact() +" "+ toBranchOffice.getAddress()+" for collecting. "+cargoServiceName;
        try {
            smsManager.sendSMS(cargoBooking.getFromContact()+","+cargoBooking.getToContact(), message, "CargoBooking", cargoBooking.getId());
        } catch (UnirestException e) {
            e.printStackTrace();
            logger.error("Error sending SMS notification for cargo booking:" + cargoBooking.getId());
        }
    }

    private void sendBookingArrivalNotification(CargoBooking cargoBooking){
        BranchOffice toBranchOffice = branchOfficeDAO.findById(cargoBooking.getToBranchId()).get();
        String cargoServiceName = "Cargo Services";
        if(sessionManager.getOperatorId() != null) {
            OperatorAccount operatorAccount = operatorAccountDAO.findById(sessionManager.getOperatorId()).get();
            cargoServiceName = operatorAccount.getCargoServiceName();
        }
        String message = String.format("A parcel with LR# %s From:%s(Ph:%s) " +
                        "has been arrived. Please collect it from %s. %s", cargoBooking.getShipmentNumber(), cargoBooking.getFromName(),
                String.valueOf(cargoBooking.getFromContact()), toBranchOffice.getAddress(), cargoServiceName);
        try {
            smsManager.sendSMS(cargoBooking.getToContact().toString(), message, "CargoBookingArrived", cargoBooking.getId());
        } catch (UnirestException e) {
            e.printStackTrace();
            logger.error("Error sending SMS notification for cargo booking:" + cargoBooking.getId());
        }
    }
    /**
     * Cancel cargo booking
     * @param id
     * @return
     */
    public boolean cancelCargoBooking(String id) {
        CargoBooking cargoBooking = cargoBookingDAO.findById(id).get();
        if(cargoBooking == null) {
            throw new BadRequestException("Invalid CargoBooking Id");
        } else {
           return cargoBookingMongoDAO.cancelCargoBookingStatus(id);
        }
    }

    public JSONObject findContactInfo(String contactType, Long contact) {
        JSONObject jsonObject = new JSONObject();
        List<CargoBooking> cargoBookings = null;
        if(contactType.equalsIgnoreCase("from")){
            cargoBookings = cargoBookingDAO.findOneByFromContactAndOperatorId(contact, sessionManager.getOperatorId());
            if(cargoBookings != null && cargoBookings.size() > 0) {
                jsonObject.put("name", cargoBookings.get(0).getFromName());
                jsonObject.put("email", cargoBookings.get(0).getFromEmail());
            }
        }else if(contactType.equalsIgnoreCase("to")){
            cargoBookings = cargoBookingDAO.findOneByToContactAndOperatorId(contact, sessionManager.getOperatorId());
            if(cargoBookings != null && cargoBookings.size() > 0) {
                jsonObject.put("name", cargoBookings.get(0).getToName());
                jsonObject.put("email", cargoBookings.get(0).getToEmail());
            }
        }
        return jsonObject;
    }

    /**
     * Module to re-send SMS for cargobooking
     * @param id
     * @return
     */
    public boolean sendSMSForCargoBooking(String id) {
        CargoBooking cargoBooking = cargoBookingDAO.findById(id).get();
        if(cargoBooking != null) {
            sendBookingConfirmationSMS(cargoBooking);
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @param vehicleId
     * @param ids
     * @return
     */
    public boolean assignVehicle(String vehicleId, List<String> ids) {
        boolean assignmentSuccess = cargoBookingMongoDAO.assignVehicles(ids, vehicleId, sessionManager.getOperatorId());
        return assignmentSuccess;
    }

    /**
     * Deliver a cargobooking
     * @param id
     * @return
     */
    public CargoBooking deliverCargoBooking(String id, String deliveryNotes) {
        CargoBooking cargoBooking = cargoBookingDAO.findById(id).get();

        if(cargoBooking == null){
            throw new IllegalArgumentException("Invalid cargo booking being delivered.");
        }
        if(cargoBooking.getCargoTransitStatus().toString().equalsIgnoreCase(CargoTransitStatus.DELIVERED.toString())){
            throw new BadRequestException("Cargo booking already delivered");
        }
        cargoBooking.setDeliveredBy(sessionManager.getCurrentUser().getFullName());
        cargoBooking.setDeliveryNotes(deliveryNotes);
        cargoBooking.setDeliveredOn(new Date());
        cargoBooking.setCargoTransitStatus(CargoTransitStatus.DELIVERED);
        cargoBooking.setDeliveredByUserId(sessionManager.getCurrentUser().getId());

        if(cargoBooking.isDue()){ //ToPay or OnAccount booking
            return payCargoBooking(cargoBooking, deliveryNotes);
        } else {
            return cargoBookingDAO.save(cargoBooking);
        }
    }

    /** This is very complicated module. I need to revisit this.
     * Get branch office summary for given date range
     * @param query
     * @return
     * @throws ParseException
     */
    public AllBranchBookingSummary getBranchSummary(JSONObject query) throws Exception {
        AllBranchBookingSummary allSummary = new AllBranchBookingSummary();
        Date start = ServiceUtils.parseDate(query.get("startDate").toString(), false);;
        Date end = ServiceUtils.parseDate(query.get("endDate").toString(), true);
        if(query.containsKey("fromBranchId")) { //if a branch is selected
            String branchId = query.get("fromBranchId").toString();
            BranchOffice branchOffice = branchOfficeManager.findOne(branchId);
            getBranchOfficeSummary(allSummary, start, end, branchId, branchOffice.getName());
        } else{ // for all branches
            List<BranchOffice> branchOffices = branchOfficeManager.getNames();
            branchOffices.stream().forEach(branchOffice -> {
                try {
                    getBranchOfficeSummary(allSummary, start, end, branchOffice.getId(), branchOffice.getName());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }
        return allSummary;
    }

    private void getBranchOfficeSummary(AllBranchBookingSummary allSummary, Date start, Date end,
                                        String branchId, String branchOfficeName) throws ParseException {
        BranchCargoBookingsSummary branchBookingsSummary = new BranchCargoBookingsSummary();
        branchBookingsSummary.setBranchOfficeName(branchOfficeName);
        List<UserCargoBookingsSummary> userBookingSummaries =
                cargoBookingMongoDAO.findBranchUserBookingSummary(branchId, start, end);
        if(userBookingSummaries != null && userBookingSummaries.size() >0) {
            branchBookingsSummary.addUserSummaries(userBookingSummaries);
            allSummary.addBranchBookingSummary(branchBookingsSummary);
        }
    }

    /**
     * Find bookings for unloading
     * @param query
     * @return
     * @throws ParseException
     */
    public List<CargoBooking> findShipmentsForUnloading(JSONObject query) throws ParseException {
        List<CargoBooking> cargoBookings = cargoBookingMongoDAO.findShipmentsForUnloading(query);
        branchOffices= branchOfficeManager.getAllMap();
        cargoBookings.stream().forEach(shipment -> {
            loadShipmentDetails(shipment);
        });
        return cargoBookings;
    }


    /**
     * Find bookings for unloading
     * @param query
     * @return
     * @throws ParseException
     */
    public List<CargoBooking> findShipmentsForLoading(JSONObject query) throws ParseException {
        List<CargoBooking> cargoBookings = cargoBookingMongoDAO.findShipmentsForLoading(query);
        branchOffices= branchOfficeManager.getAllMap();
        cargoBookings.stream().forEach(shipment -> {
            loadShipmentDetails(shipment);
        });
        return cargoBookings;
    }

    /**
     * Unload bookings and send arrival SMS notifications
     * @param bookingIds
     * @return
     */
    public boolean unloadBookings(List<String> bookingIds){
        if(cargoBookingMongoDAO.unloadBookings(bookingIds)){
            bookingIds.stream().forEach(id -> {
                sendBookingArrivalNotification(cargoBookingDAO.findByIdAndOperatorId(id, sessionManager.getOperatorId()));
            });
            return true;
        };
        return false;
    }

    public List<CargoBooking> findUndeliveredShipments(JSONObject query) throws ParseException {
        List<CargoBooking> cargoBookings = cargoBookingMongoDAO.findUndeliveredShipments(query);
        branchOffices = branchOfficeManager.getAllMap();
        userNames = userManager.getUserNames(true);
        if(lrTypes == null){
            lrTypes = shipmentSequenceManager.getShipmentNamesMap();
        }
        cargoBookings.stream().forEach(shipment -> {
            loadShipmentDetails(shipment);
        });
        return cargoBookings;
    }

    public List<Document> getBranchBookingSummaryByDay(String branchId, JSONObject query) throws ParseException {
        return cargoBookingMongoDAO.getBranchBookingSummaryByDay(branchId, query);
    }

    public CargoBooking addReviewComment(String id, String deliveryComment) {
        Optional<CargoBooking> optional =  cargoBookingDAO.findById(id);
        if(optional.isPresent()) {
            CargoBooking cargoBooking = optional.get();
            cargoBooking.setReviewComment(deliveryComment);
            return cargoBookingDAO.save(cargoBooking);
        } else {
            throw new BadRequestException(("Invalid cargo booking"));
        }
    }

    public Page<CargoBooking> getDeliveredCargoBookings(JSONObject query) throws ParseException {
        long totalCount = countDeliveredBookings(query);
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        if(query.get("page") != null && query.get("size") != null && query.get("sort") != null){
            pageable = PageRequest.of((int)query.get("page"),(int)query.get("size"));
        }
        List<CargoBooking> deliveredBookings = cargoBookingMongoDAO.getDeliveredCargoBookings(query,pageable);
        Map<String,String> vehicleNamesMap = vehicleManager.findVehicleNumbers();
        Map<String,String> getNamesMap = branchOfficeManager.getNamesMap();
        deliveredBookings.stream().forEach(booking -> {
            booking.getAttributes().put("fromBranch",getNamesMap.get(booking.getFromBranchId()));
            booking.getAttributes().put("toBranch",getNamesMap.get(booking.getToBranchId()));
            booking.getAttributes().put("RegNo",vehicleNamesMap.get(booking.getVehicleId()));
        });
        Page<CargoBooking> page = new PageImpl<>(deliveredBookings, pageable, totalCount);
        return page;
    }


    public long countDeliveredBookings(JSONObject query) throws ParseException {
        return cargoBookingMongoDAO.countDelivered(query);
    }

//    public void insertIntoCargoBookingDailyTotals() throws ParseException{
//        SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
//       List<Document> results= cargoBookingMongoDAO.groupByOffice();
//       for(int i=0;i<results.size();i++){
//           CargoBookingDailyTotals cb=new CargoBookingDailyTotals();
//           cb.setBookingsTotal(results.get(i).getLong("bookingsTotal"));
//           cb.setDate(formatter.parse(results.get(i).getString("date")));
//           cb.setOfficeId(results.get(i).getString("officeId"));
//           cargoBookingDailyTotals.save(cb);
//        };
//
//    }


    public List<Document> groupCargoBookings() {
        List<Document> queryResults = cargoBookingMongoDAO.groupCargoBookings();
        Map<String,String> getNamesMap=branchOfficeManager.getNamesMap();
        queryResults.stream().forEach(booking -> {
            booking.put("branchName",getNamesMap.get(booking.get("_id")));
        });
        return queryResults;

    }

    public long countCancelledShipments(JSONObject query) throws ParseException {
        return cargoBookingMongoDAO.countCancelledShipments(   query);
    }

    public Iterable<CargoBooking> findCancelledShipments(JSONObject query) throws ParseException {
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        if(query.get("page") != null && query.get("size") != null && query.get("sort") != null){
            pageable = PageRequest.of((int)query.get("page"),(int)query.get("size"));
        }
        List<CargoBooking> cargoBookings = cargoBookingMongoDAO.findCancelledShipments(query, pageable);
        Map<String,String> getNamesMap = branchOfficeManager.getNamesMap();
        userNames = userManager.getUserNames(true);
        cargoBookings.stream().forEach(booking -> {
            booking.getAttributes().put("fromBranch",getNamesMap.get(booking.getFromBranchId()));
            booking.getAttributes().put("toBranch",getNamesMap.get(booking.getToBranchId()));
            booking.getAttributes().put("bookedBy",userNames.get(booking.getCreatedBy()));
        });
        return cargoBookings;
    }

    public long countCancellationPendingShipments(JSONObject query) throws ParseException {
        return cargoBookingMongoDAO.countCancellationPendingShipments(query);
    }

    public Page<CargoBooking> getPendingShipments(JSONObject query) throws ParseException {
        long totalCount = countCancellationPendingShipments(query);
        PageRequest pageable = PageRequest.of(0,Integer.MAX_VALUE);
        if(query.get("page") != null && query.get("size") != null && query.get("sort") != null){
            pageable = PageRequest.of((int)query.get("page"),(int)query.get("size"));
        }
        List<CargoBooking> cargoBookings = cargoBookingMongoDAO.getPendingShipments(query,pageable);
        Map<String,String> getNamesMap = branchOfficeManager.getNamesMap();
        userNames = userManager.getUserNames(true);
        cargoBookings.stream().forEach(booking -> {
            booking.getAttributes().put("fromBranch",getNamesMap.get(booking.getFromBranchId()));
            booking.getAttributes().put("toBranch",getNamesMap.get(booking.getToBranchId()));
            booking.getAttributes().put("bookedBy",userNames.get(booking.getCreatedBy()));
            booking.getAttributes().put("cancelledBy",userNames.get(booking.getCancelledBy()));
        });
        Page<CargoBooking> page = new PageImpl<>(cargoBookings, pageable, totalCount);
        return page;
    }

    public boolean approveCancellation(JSONObject data) {
        CargoBooking cargoBooking = cargoBookingDAO.findById(data.get("shipmentId").toString()).get();
        if(cargoBooking == null) {
            throw new BadRequestException("Invalid CargoBooking Id");
        } else {
            User cancelledBy = userManager.findOne(cargoBooking.getCancelledBy());
            if(cancelledBy != null) {
                cargoBooking.getMessages().add(String.format("Cancelled by %s on %s", cancelledBy.getFullName(), new DateTime()));
            }
            cargoBooking.setCanceled(true);
            cargoBooking.setCanceledOn(new Date());
            cargoBooking.setCancellationReason(data.get("reason").toString());
            cargoBooking.setCargoTransitStatus(CargoTransitStatus.CANCELLED);
            if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.PAID.toString())) {
                paymentManager.cancelCargoBooking(cargoBooking);
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.TOPAY.toString())) {
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else if(cargoBooking.getPaymentType().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())) {
                if(cargoBooking.getSupplierId() == null) {
                    throw new BadRequestException("Invalid supplierId on OnAccount shipment");
                }
                //deduct the balance
                updateOnAccountBalance(cargoBooking.getSupplierId(), -cargoBooking.getTotalCharge(), false);
                cargoBookingDAO.save(cargoBooking);
                return true;
            } else {
                throw new BadRequestException("Invalid Type for CargoBooking, Only ToPay and OnAccount types can be paid");
            }
        }
    }
}
