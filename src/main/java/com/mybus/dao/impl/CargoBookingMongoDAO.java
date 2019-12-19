package com.mybus.dao.impl;


import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;
import com.mybus.dao.UserDAO;
import com.mybus.dao.VehicleDAO;
import com.mybus.dto.BranchDeliverySummary;
import com.mybus.dto.UserCargoBookingsSummary;
import com.mybus.dto.UserDeliverySummary;
import com.mybus.model.*;
import com.mybus.service.CargoBookingManager;
import com.mybus.service.SessionManager;
import com.mybus.service.UserManager;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by srinikandula on 12/11/16.
 */
@Repository
public class CargoBookingMongoDAO {
    private static final Logger logger = LoggerFactory.getLogger(CargoBookingManager.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private VehicleDAO vehicleDAO;

    @Autowired
    private UserManager userManager;
    /**
     * Assign vehicle to cargo bookings
     * @param ids
     * @param vehicleId
     * @param operatorId
     * @return
     */
    public boolean assignVehicles(List<String> ids, String vehicleId, String operatorId) {
        Vehicle vehicle = vehicleDAO.findById(vehicleId).get();
        Update updateOp = new Update();
        updateOp.set("vehicleId", vehicleId);
        updateOp.set("cargoTransitStatus", CargoTransitStatus.INTRANSIT);
        if(vehicle != null) {
            updateOp.push("messages", "Loaded to vehicle "+ vehicle.getRegNo()
                    +" by "+ sessionManager.getCurrentUser().getFullName());
        }
        final Query query = new Query();
        query.addCriteria(where("_id").in(ids));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(operatorId));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, CargoBooking.class);
        return writeResult.getModifiedCount() == ids.size();
    }

    public List<CargoBooking> findShipments(JSONObject query, final Pageable pageable) throws ParseException {
        final Query q = createSearchQuery(query);
        if(pageable != null) {
            q.with(pageable);
        }else {
            q.with(new Sort(Sort.Direction.DESC,"dispatchDate"));
        }
        List<CargoBooking> cargoBookings = mongoTemplate.find(q, CargoBooking.class);
        return cargoBookings;
    }

    private Query createSearchQuery(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(query != null) {
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            if(query.get("filter") != null) {
                q.addCriteria(where(CargoBooking.SHIPMENT_NUMBER).regex(query.get("filter").toString(), "i"));
            }
            if(query.get("filter") != null) {
                q.addCriteria(where("remarks").regex(query.get("filter").toString(), "i"));
            }

            if(query.get("status") != null && query.get("status").toString().trim().length() > 0) {
                q.addCriteria(where("cargoTransitStatus").is(query.get("status").toString().trim()));
            }
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("fromBranchId") != null) {
                match.add(Criteria.where("fromBranchId").is(query.get("fromBranchId").toString()));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            if(query.get("deliveredBy") != null) {
                match.add(Criteria.where("deliveredBy").is(query.get("deliveredBy").toString()));
            }
            if(query.get("bookedBy") != null) {
                match.add(Criteria.where("createdBy").is(query.get("bookedBy").toString()));
            }
            if(query.get("paymentType") != null) {
                match.add(Criteria.where("paymentType").is(query.get("paymentType").toString()));
            }
        }
        if(match.size() > 0) {
            criteria.andOperator(match.toArray(new Criteria[match.size()]));
            q.addCriteria(criteria);
        }
        return q;
    }

    /**
     * Count the shipments
     * @param query
     * @return
     * @throws ParseException
     */
    public long countShipments(JSONObject query) throws ParseException {
        final Query q = createSearchQuery(query);
        return mongoTemplate.count(q, CargoBooking.class);
    }

    /**
     * Find cargo bookings with a given matching string
     * @param id
     * @return
     */
    public List<CargoBooking> findShipments(String id){
        Query q = new Query();
        q.addCriteria(where(CargoBooking.SHIPMENT_NUMBER).regex(id, "i"));
        q.addCriteria(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.find(q, CargoBooking.class);
    }

    /**
     * Find cargo bookings for unloading
     * @param query
     * @return
     * @throws ParseException
     */
    public List<CargoBooking> findShipmentsForUnloading(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(query != null) {
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            match.add(Criteria.where("cargoTransitStatus").nin(CargoTransitStatus.CANCELLED.toString(),
                    CargoTransitStatus.ARRIVED.toString(),
                    CargoTransitStatus.DELIVERED.toString(),
                    CargoTransitStatus.ONHOLD.toString()));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        q.with(new Sort(Sort.Direction.DESC,"dispatchDate"));
        return mongoTemplate.find(q, CargoBooking.class);
    }


    /**
     * Find cargo bookings for unloading
     * @param query
     * @return
     * @throws ParseException
     */
    public List<CargoBooking> findShipmentsForLoading(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(query != null) {
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("fromBranchId") != null) {
                match.add(Criteria.where("fromBranchId").is(query.get("fromBranchId").toString()));
            }
            match.add(Criteria.where("cargoTransitStatus").nin(CargoTransitStatus.CANCELLED.toString(),
                    CargoTransitStatus.ARRIVED.toString(),
                    CargoTransitStatus.DELIVERED.toString(),
                    CargoTransitStatus.ONHOLD.toString()));

        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        q.with(new Sort(Sort.Direction.DESC,"dispatchDate"));
        return mongoTemplate.find(q, CargoBooking.class);
    }

    public boolean unloadBookings(List<String> bookingIds) {
        User currentUser = sessionManager.getCurrentUser();
        Update updateOp = new Update();
        updateOp.set("cargoTransitStatus", CargoTransitStatus.ARRIVED.toString());
        updateOp.push("messages", "Unloaded by "+ currentUser.getFullName() + " on "+ new Date());
        final Query query = new Query();
        query.addCriteria(where("_id").in(bookingIds));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, CargoBooking.class);
        return writeResult.getModifiedCount() == bookingIds.size();
    }

    public List<CargoBooking> findUndeliveredShipments(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(query != null) {
            if(query.get("startDate") != null) {
                match.add(Criteria.where("dispatchDate").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("dispatchDate").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
            match.add(Criteria.where("cargoTransitStatus").nin(CargoTransitStatus.CANCELLED.toString(),
                    CargoTransitStatus.DELIVERED.toString(),
                    CargoTransitStatus.ONHOLD.toString()));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        q.with(new Sort(Sort.Direction.DESC,"dispatchDate"));
        return mongoTemplate.find(q, CargoBooking.class);
    }

    public List<CargoBooking> findCancelledShipments(JSONObject query, Pageable pageable) throws ParseException {
        Query q = getFindCancellationsQuery(query);
        q.addCriteria(where("cargoTransitStatus").is(CargoTransitStatus.CANCELLED.toString()));
        if(pageable != null){
            q.with(pageable);
        }
        q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        return mongoTemplate.find(q, CargoBooking.class);
    }

    /**
     * Count cancelled cargo bookings
     * @param query
     * @return
     * @throws ParseException
     */
    public long countCancelledShipments(JSONObject query) throws ParseException {
        Query q = getFindCancellationsQuery(query);
        q.addCriteria(where("cargoTransitStatus").is(CargoTransitStatus.CANCELLED.toString()));
        return mongoTemplate.count(q, CargoBooking.class);
    }
    private Query getFindCancellationsQuery(JSONObject query) throws ParseException {
        Query q = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            match.add(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(query != null) {
            if(query.get("startDate") != null) {
                match.add(Criteria.where("canceledOn").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false)));
            }
            if(query.get("endDate") != null) {
                match.add(Criteria.where("canceledOn").lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if(query.get("fromBranchId") != null) {
                match.add(Criteria.where("fromBranchId").is(query.get("fromBranchId").toString()));
            }
            if(query.get("toBranchId") != null) {
                match.add(Criteria.where("toBranchId").is(query.get("toBranchId").toString()));
            }
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        q.addCriteria(criteria);
        return q;
    }

    /**
     * For a given branchOfficeId
     *
     * 1) find all users of that branch
     * 2) find the topay deliveries total for those users
     *
     * @param branchId
     * @param start
     * @param end
     */
    public BranchDeliverySummary findDeliveryTotalByBranchUsers(String branchId, PaymentStatus paymentStatus,
                                                                Date start, Date end)
            throws ParseException {
        if(start == null || end == null){
            throw new IllegalArgumentException("Dates are required");
        }
        List<User> users = userDAO.findByBranchOfficeIdAndOperatorId(branchId, sessionManager.getOperatorId());
        List<String> userNames = users.stream().map(u -> u.getFullName()).collect(Collectors.toList());
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where("paymentType").is(paymentStatus.getKey()));
        match.add(where(CargoBooking.DELIVERED_ON).gte(ServiceUtils.parseDate(start, false)));
        match.add(where(CargoBooking.DELIVERED_ON).lte(ServiceUtils.parseDate(end, true)));
        match.add(where(CargoBooking.DELIVERED_BY).in(userNames));
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group(CargoBooking.DELIVERED_BY).sum("totalCharge").as("totalCharge"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, CargoBooking.class, Document.class);
        BranchDeliverySummary deliverySummary = new BranchDeliverySummary();
        deliverySummary.setBranchId(branchId);
        for(Document object: groupResults){
            UserDeliverySummary userDeliverySummary = new UserDeliverySummary();
            userDeliverySummary.setUserName(object.get("_id").toString());
            userDeliverySummary.setTotal(Double.parseDouble(object.get("totalCharge").toString()));
            deliverySummary.setTotal(deliverySummary.getTotal() + userDeliverySummary.getTotal());
            deliverySummary.getUserDeliverySummaryList().put(userDeliverySummary.getUserName(), userDeliverySummary);
        }
        return deliverySummary;
    }

    /**
     * Find booking details cumulative by the day
     * @param branchId
     * @param query
     * @return
     * @throws ParseException
     */
    public List<Document> getBranchBookingSummaryByDay(String branchId, JSONObject query) throws ParseException {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
       match.add(where("fromBranchId").is(branchId));
        if(query != null){
            if(query.containsKey("startDate")) {
                String startDate = query.get("startDate").toString();
                Date start = ServiceUtils.parseDate(startDate, false);
                match.add(where("createdAt").gte(start));
            }
            if(query.containsKey("endDate")) {
                String endDate = query.get("endDate").toString();
                Date end = ServiceUtils.parseDate(endDate, true);
                match.add(where("endDate").lte(end));
            }
        }

        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                //match(criteria),
               project()
                        .andExpression("year(createdAt)").as("year")
                        .andExpression("month(createdAt)").as("month")
                        .andExpression("dayOfMonth(createdAt)").as("day")
                        .and("paymentType").as("paymentType")
                        .and("totalCharge").as("totalCharge")
                        .and("id").as("_id"),
                group(fields().and("year").and("month").and("day").and("paymentType")).sum("totalCharge")
                        .as("totalCharge").count().as("totalCount"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, CargoBooking.class, Document.class);
        return groupResults.getMappedResults();
    }

    /** Module to find branch user bookings summary.
     *
     * Find Branch users, find the cargo bookings by those users within the given date range.
     *
     * @param branchId
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public List<UserCargoBookingsSummary> findBranchUserBookingSummary(String branchId, Date start, Date end)
            throws ParseException {
        if(start == null || end == null){
            throw new IllegalArgumentException("Dates are required");
        }
        List<User> users = userDAO.findByBranchOfficeIdAndOperatorId(branchId, sessionManager.getOperatorId());
        List<String> userIds = users.stream().map(u -> u.getId()).collect(Collectors.toList());
        if(userIds.size() == 0 ){
            return null;
        }
        Map<String, String> userNames = userManager.getUserNames(true);
        Map<String , UserCargoBookingsSummary> userBookingSummary = new HashMap<>();
        //find booking summary
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where(CargoBooking.KEY_CREATED_AT).gte(ServiceUtils.parseDate(start, false)));
        match.add(where(CargoBooking.KEY_CREATED_AT).lte(ServiceUtils.parseDate(end, true)));
        if(userIds.size() > 0) {
            match.add(where("createdBy").in(userIds));
        }
        match.add(Criteria.where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("createdBy", "paymentType").sum("totalCharge").as("totalCharge")
                        .count().as("totalCount"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, CargoBooking.class, Document.class);
        //find to-pay delivery totals
        match.clear();
        match.add(where("paymentType").is("ToPay"));
        match.add(where(CargoBooking.DELIVERED_ON).gte(ServiceUtils.parseDate(start, false)));
        match.add(where(CargoBooking.DELIVERED_ON).lte(ServiceUtils.parseDate(end, true)));
        if(userIds.size() > 0) {
            match.add(where("updatedBy").in(userIds));
        }
        criteria = new Criteria();
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation deliveriesAgg = newAggregation(
                match(criteria),
                group("updatedBy").sum("totalCharge").as("totalCharge")
                        .count().as("totalCount"));
        List<Document> toPayDeliveryResults
                = mongoTemplate.aggregate(deliveriesAgg, CargoBooking.class, Document.class).getMappedResults();


        //find cancellation totals. Use createdBy for checking cancellation
        match.clear();
        match.add(where("canceledOn").gte(ServiceUtils.parseDate(start, false)));
        match.add(where("canceledOn").lte(ServiceUtils.parseDate(end, true)));
        if(userIds.size() > 0) {
            match.add(where("createdBy").in(userIds));
        }
        criteria = new Criteria();
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation cancelAgg = newAggregation(
                match(criteria),
                group("createdBy").sum("totalCharge").as("totalCharge")
                        .count().as("totalCount"));
        List<Document> cancelResults
                = mongoTemplate.aggregate(cancelAgg, CargoBooking.class, Document.class).getMappedResults();

        for(Document result: groupResults){
            String userId = result.getString("createdBy");
            if(userBookingSummary.get(userId) == null) {
                userBookingSummary.put(userId, new UserCargoBookingsSummary());
            }
            UserCargoBookingsSummary userSummary = userBookingSummary.get(userId);

            //set delivery total
            Optional<Document> toPayDeliveryTotal = toPayDeliveryResults.stream()
                    .filter(d -> d.getString("_id").equals(userId)).findFirst();
            if(toPayDeliveryTotal.isPresent()){
                userSummary.setTopayBookingsDeliveredTotal(toPayDeliveryTotal.get().getLong("totalCharge"));
            }
            //set cancellation total
            Optional<Document> cancellation = cancelResults.stream()
                    .filter(d -> d.getString("_id").equals(userId)).findFirst();
            if(cancellation.isPresent()){
                userSummary.setCanceledBookingsTotal(cancellation.get().getLong("totalCharge"));
            }
            userSummary.setUserName(userNames.get(result.getString("createdBy")));
            if(result.get("paymentType").toString().equalsIgnoreCase(PaymentStatus.PAID.toString())){
                userSummary.setPaidBookingsTotal(result.getLong("totalCharge"));
                userSummary.setPaidBookingsCount(result.getInteger("totalCount"));
            } else if(result.get("paymentType").toString().equalsIgnoreCase(PaymentStatus.TOPAY.toString())){
                userSummary.setTopayBookingsTotal(result.getLong("totalCharge"));
                userSummary.setTopayBookingsCount(result.getInteger("totalCount"));
            } else if(result.get("paymentType").toString().equalsIgnoreCase(PaymentStatus.ONACCOUNT.toString())){
                userSummary.setOnAccountBookingsTotal(result.getLong("totalCharge"));
                userSummary.setOnAccountBookingsCount(result.getInteger("totalCount"));
            }
        }
        return Lists.newArrayList(userBookingSummary.values());
    }

    public List<CargoBooking> getDeliveredCargoBookings(JSONObject query, PageRequest pageable) throws ParseException {
      Query q = getDeliveredBookingsQuery(query);
        if(pageable != null){
            q.with(pageable);
        } else {
            q.with(new Sort(Sort.Direction.DESC,"dispatchDate"));
        }
        Iterable<CargoBooking> bookings = mongoTemplate.find(q,CargoBooking.class);
        List<CargoBooking> deliveredCargoBookings = IteratorUtils.toList(bookings.iterator()) ;
        return deliveredCargoBookings;
    }
    public long countDelivered(JSONObject query) throws ParseException {
        Query q = getDeliveredBookingsQuery(query);
        return mongoTemplate.count(q,CargoBooking.class);
    }

    /**
     * Module to create query for finding delivered bookings
     * @param query
     * @return
     * @throws ParseException
     */
    private Query getDeliveredBookingsQuery(JSONObject query) throws ParseException {
        Query q = new Query();
        q.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        q.addCriteria(where("cargoTransitStatus").is("DELIVERED"));
        q.with(new Sort(Sort.Direction.DESC,"deliveredOn"));
        if(userManager.isAgent(sessionManager.getCurrentUser())){
            q.addCriteria(where("createdBy").is(sessionManager.getCurrentUser().getId()));
        }
        if(query.size() > 0){
            if (query.get("vehicleId") != null){
                q.addCriteria(where("vehicleId").is(query.get("vehicleId")));
            }
            if(query.get("startDate") != null && query.get("endDate") != null){
                q.addCriteria(Criteria.where("deliveredOn").gte(ServiceUtils.parseDate(query.get("startDate").toString(), false))
                        .lte(ServiceUtils.parseDate(query.get("endDate").toString(), true)));
            }
            if (query.get("branchOffice") != null){
                q.addCriteria(where("toBranchId").is(query.get("branchOffice")));
            }
            if (query.get("deliveredBy") != null){
                q.addCriteria(where("deliveredBy").is(query.get("deliveredBy")));
            }
        }
        return q;
    }

    public List<Document> groupByOffice(){

        Date myDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date previousDate=calendar.getTime();
        Aggregation aggregation = newAggregation(
                match(Criteria.where("dispatchDate").gte(previousDate)),
//                aggregationOps.add(match(Criteria.where("timestamp").gte(from)))
//                match(Criteria.where("createdAt").gte(previousDate)),
                project().and(DateOperators.dateOf("dispatchDate").toString("%d-%m-%Y")).as("date")
                        .and("fromBranchId").as("officeId")
                        .and("totalCharge").as("total"),
                group("date","officeId").sum("total").as("bookingsTotal")
//                project("date","total").andExpression("concat( substr(total,0,-1))").as("bookings")

        );
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, CargoBooking.class, Document.class);
        List<Document> result = results.getMappedResults();
        return  result;
    }

    public List<Document> groupCargoBookings() {
        Aggregation aggregation = newAggregation(
                sort(Sort.Direction.ASC,"date"),
                group("officeId").
                        push(new BasicDBObject
                                ("BookingsTotal", "$bookingsTotal").append
                                ("date", "$date")).as("Bookings")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, CargoBookingDailyTotals.class, Document.class);
        List<Document> result = results.getMappedResults();
        return result;
    }

    public boolean cancelCargoBookingStatus(String id) {
        final Query query = new Query();
        query.addCriteria(where("id").is(id));
        Update update = new Update();
        update.set("cargoTransitStatus",CargoTransitStatus.CANCELLATION_PENDING);
        update.set("cancelledBy",sessionManager.getCurrentUser().getId());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,CargoBooking.class);
        return updateResult.getModifiedCount() == 1;
    }

    public long countCancellationPendingShipments(JSONObject query) throws ParseException {
        Query q = getFindCancellationsQuery(query);
        q.addCriteria(where("cargoTransitStatus").is(CargoTransitStatus.CANCELLATION_PENDING.toString()));
        return mongoTemplate.count(q, CargoBooking.class);
    }

    public List<CargoBooking> getPendingShipments(JSONObject query, Pageable pageable) throws ParseException {
        Query q = getFindCancellationsQuery(query);
        q.addCriteria(where("cargoTransitStatus").is(CargoTransitStatus.CANCELLATION_PENDING.toString()));
        if(pageable != null){
            q.with(pageable);
        }
        q.with(new Sort(Sort.Direction.DESC,"createdAt"));
        return mongoTemplate.find(q, CargoBooking.class);
    }
}
