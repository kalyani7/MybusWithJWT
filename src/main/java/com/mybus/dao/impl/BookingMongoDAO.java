package com.mybus.dao.impl;

import com.google.common.base.Preconditions;
import com.mongodb.client.result.UpdateResult;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.InvoiceBookingDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.service.BookingTypeManager;
import com.mybus.service.SessionManager;
import com.mybus.util.ExcelParser;
import com.mybus.util.ServiceUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by skandula on 5/7/16.
 */
@Service
public class BookingMongoDAO {

    private static final Logger logger = LoggerFactory.getLogger(BookingMongoDAO.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AgentMongoDAO agentMongoDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ExcelParser excelParser;

    @Autowired
    private InvoiceBookingDAO invoiceBookingDAO;

    @Autowired
    private InvoiceBookingsMongoDAO invoiceBookingsMongoDAO;

    /**
     * Find due bookings by agent names and Journey Date
     * @param agentNames
     * @param JDate
     * @return
     */
    public List<Booking> findDueBookings(List<String> agentNames, String JDate) {
        final Query query = new Query();
        //query.fields().include("name");
        query.addCriteria(where("bookedBy").in(agentNames));
        if(JDate != null) {
            query.addCriteria(where("jDate").is(JDate));
        }
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        addIsBookingDueConditions(query);
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }

    /**
     * Find due bookings by agent names and Journey Date
     * @param agentNames
     * @param JDate
     * @return
     */
    public double findDueBookingTotal(List<String> agentNames, String JDate) {
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(where("bookedBy").in(agentNames));
        match.add(where("due").is(true));
        match.add(where("formId").exists(true));
        match.add(where("serviceReportId").exists(false));
        if(JDate != null) {
            match.add(where("jDate").is(JDate));
        }
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group().sum("netAmt").as("totalDue"),
                sort(Sort.Direction.DESC, "totalDue"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, Document.class);
        List<Document> result = groupResults.getMappedResults();
        if(result.size() > 0){
            return result.get(0).getDouble("totalDue");
        } else {
            return 0;
        }

    }

    /**
     * For a given agent name find the branchOffice and find the due bookings whose source of the journey doesn't match
     * branchoffice city name.
     * @param agent
     * @return
     */
    public List<Booking> findReturnTicketDuesForAgent(Agent agent ) {
        long start = System.currentTimeMillis();
        Preconditions.checkNotNull(agent, "Agent not found");
        BranchOffice branchOffice = branchOfficeDAO.findById(agent.getBranchOfficeId()).get();
        Preconditions.checkNotNull(branchOffice, "Branchoffice not found");
        final Query query = new Query();
        query.addCriteria(where("bookedBy").is(agent.getUsername()));

        addIsBookingDueConditions(query);
        query.addCriteria(where("source").ne(branchOffice.getName()));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        long end = System.currentTimeMillis();
        logger.info(String.format("Finding return tickets for agent %s took " + (end-start), agent.getUsername()));
        return bookings;
    }

    /**
     * Add the query conditions to check if the booking is due
     * @param query
     */
    private void addIsBookingDueConditions(Query query) {
        query.addCriteria(where("due").is(true));
        query.addCriteria(where("formId").exists(true));
        query.addCriteria(where("serviceId").exists(false));
        query.addCriteria(where("serviceReportId").exists(false));
    }

    public List<Booking> findAgentDues(String agentName) {
        final Query query = new Query();
        //query.fields().include("name");
        query.addCriteria(where("bookedBy").is(agentName));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        addIsBookingDueConditions(query);
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }
    public boolean markBookingPaid(String bookingId) {
        Update updateOp = new Update();
        updateOp.set(Booking.DUE, false);
        if(sessionManager.getCurrentUser() != null) {
            updateOp.set(Booking.PAID_BY, sessionManager.getCurrentUser().getId());
        }
        updateOp.set(Booking.PAID_ON, new Date());
        final Query query = new Query();
        query.addCriteria(where("_id").is(bookingId));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, Booking.class);
        if(writeResult.getModifiedCount() != 1) {
            return false;
        }
        return true;
    }
    public List<Document> getBookingDueTotalsByService(String branchOfficeId){
        //db.booking.aggregate([{ $match: { 'due': true } },{$group:{_id:"$serviceNumber",total:{$sum:"$netAmt"}}}])
        /*
        Aggregation agg = newAggregation(
                match(Criteria.where("due").is(true)),
                group("hosting").count().as("total"),
                project("total").and("hosting").previousOperation(),
                sort(Sort.Direction.DESC, "total");*/
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(branchOfficeId != null) {
            List<String> agentNames = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
            if (agentNames != null && !agentNames.isEmpty()) {
                match.add(where("bookedBy").in(agentNames));
            }
        }
        match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        match.add(where("due").is(true));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("serviceNumber").sum("netAmt").as("totalDue"),
                sort(Sort.Direction.DESC, "totalDue"));

        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, Document.class);
        List<Document> result = groupResults.getMappedResults();
        return result;
    }

    public Page<Document> getBookingCountsByPhone(Pageable pageable){
        /**
         * db.booking.aggregate(
         [
         {
         $group:
         {
         _id: { phoneNo:  "$phoneNo"},
         count: { $sum: 1 }
         }
         },{
         $sort:{count:1}
         }
         ]
         )
         */

        long total = getTotalDistinctPhoneNumbers();
        Aggregation agg = newAggregation(
                match(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId())),
                group("phoneNo").count().as("totalBookings"),
                sort(Sort.Direction.DESC, "totalBookings"),
                skip((long)pageable.getPageNumber() * pageable.getPageSize()),
                limit(pageable.getPageSize()));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, Document.class);
        List<Document> result = groupResults.getMappedResults();
        return new PageImpl<>(result, pageable, total);
    }

    public long getTotalDistinctPhoneNumbers() {
        Aggregation agg = newAggregation(
                group("phoneNo").count().as("total"));

        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, Document.class);
        return 0;
    }
    public List<Document> getDueBookingByAgents(String branchOfficeId){
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(branchOfficeId != null) {
            List<String> agentNames = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
            if (agentNames != null && !agentNames.isEmpty()) {
                match.add(where("bookedBy").in(agentNames));
            }
        }
        match.add(where("due").is(true));
        match.add(where("formId").exists(true));
        match.add(where("serviceReportId").exists(false));
        match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        Aggregation agg = newAggregation(
                match(criteria),
                group("bookedBy").sum("netAmt").as("totalDue"),
                sort(Sort.Direction.DESC, "totalDue"));

        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, Document.class);
        List<Document> result = groupResults.getMappedResults();
        return result;
    }

    public List<Booking> getDueBookingByServiceNumber(String branchOfficeId, String serviceNumber){
        final Query query = new Query();
        if(branchOfficeId != null) {
            List<String> agentNames = agentMongoDAO.findAgentNamesByOfficeId(branchOfficeId);
            if (agentNames != null && !agentNames.isEmpty()) {
                query.addCriteria(where("bookedBy").in(agentNames));
            }
        }
        addIsBookingDueConditions(query);
        query.addCriteria(where("serviceNumber").is(serviceNumber));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }

    public Invoice findBookingsInvoice(Date start, Date end, List<String> bookedBy) {
        Invoice invoice = new Invoice();
        final Query query = new Query();
        List<Criteria> match = new ArrayList<>();
        Criteria criteria = new Criteria();
        if(bookedBy != null && bookedBy.size() != 0 && bookedBy.size() !=3){
            List<String> channels = new ArrayList<>();
            for(String name: bookedBy) {
                if(name.equals("ABHIBUS")) {
                    channels.addAll(BookingTypeManager.ABHIBUS_BOOKING_CHANNELS);
                }
            }
            match.add(where("bookedBy").in(channels));
        }
        match.add(where("journeyDate").gte(start).lte(end));
        match.add(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));

        criteria.andOperator(match.toArray(new Criteria[match.size()]));
        query.addCriteria(criteria);
        invoice.setBookings(mongoTemplate.find(query, Booking.class));
         Aggregation agg = newAggregation(
                match(criteria),
                group().sum("netAmt").as("bookingTotal").sum("serviceTax").as("totalTax"));
        AggregationResults<Document> groupResults
                = mongoTemplate.aggregate(agg, Booking.class, Document.class);
        List<Document> result = groupResults.getMappedResults();
        if(!result.isEmpty()) {
            invoice.setTotalTax(result.get(0).getDouble("totalTax"));
            invoice.setTotalSale(result.get(0).getDouble("bookingTotal"));
        }
        return invoice;
    }



    public List<Booking> findDueBookings(Date start, Date end, List<String> bookedBy) {
        final Query query = new Query();
        if(bookedBy != null && bookedBy.size() != 0){
            query.addCriteria(where("bookedBy").in(bookedBy));
        }
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.addCriteria(where("due").is(true));
        query.addCriteria(where("journeyDate").gte(start).lte(end));
        query.addCriteria(where("formId").exists(true));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }

    /**
     * Find a booking that is submitted along with a form.
     * @param ticketNumber
     * @return
     */
    public Booking findFormBooking(String ticketNumber) {
        final Query query = new Query();
        if(ticketNumber == null ){
            throw new BadRequestException("ticketNumber is required");
        }
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.addCriteria(where("ticketNo").is(ticketNumber));
        query.addCriteria(where("formId").exists(true));
        Booking booking = mongoTemplate.findOne(query, Booking.class);
        return booking;
    }

    /**
     * Find bookings by list of _ids
     * @param ids
     * @return
     */
    public List<Booking> findBookingsByIds(List<String> ids) {
        final Query query = new Query();
        query.addCriteria(where("_id").in(ids));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        return bookings;
    }


    /**
     * Find agent names for which no agent is created
     * @param existingNames
     * @return
     */
    public Set<String> findNewBitlaAgentNames(Collection<String> existingNames) {
        /*
        BRANCH_BOOKING = 1 {Office bookings}
        B_ONLINE_AGENT = 2 {Your Local agents to whom you gave a login to book}
        B_OFFLINE_AGENT = 3 {Same as you agents who doesn't have login to access the system}
        B_API_AGENT = 4 {OTA's like Redbus, Abhibus, Ticket Simply etc., }
        E_BOOKING = 5 {Website Bookings - Through Desktop}
        MOBILE_BOOKING = 6 {Website Bookings - Through Mobile browser}
        TRABOL_BOOKING = 7 {Ignore it as of now as we are not active on this mode of bookings}
        TRABOL_MOBILE_BOOKING = 8 {Ignore it as of now as we are not active on this mode of bookings}
        CHART_SHARED_BOOKING = 12 {Ignore it as of now as we are not active on this mode of bookings}
         */
        List<String> agentTypes = new ArrayList<>();
        agentTypes.add("1");
        agentTypes.add("2");
        agentTypes.add("3");

        final Query query = new Query();

        query.addCriteria(where("bookedBy").nin(existingNames));
        query.addCriteria(where("bookingType").in(agentTypes));
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.fields().include("bookedBy");
        List<Booking> bookings = mongoTemplate.find(query, Booking.class);
        Set<String> names = bookings.stream().map(Booking::getBookedBy).map(String::trim).collect(Collectors.toSet());
        return names;
    }

    public List<Booking> findBookingsForTaxInvoice(String Id) {
        Query query = new Query();
        query.addCriteria(where("operatorId").is(Id));

        query.addCriteria(where("bookedBy").is("REDBUS-API"));
        query.addCriteria(where("emailID").exists(true)
                .orOperator(where("emailedTaxInvoice").exists(false), where("emailedTaxInvoice").is(false)));
        query.limit(10);
        return mongoTemplate.find(query,Booking.class);
    }

    private Query createQueryToFindBookings(String startDate,String endDate) throws ParseException {
        final Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        query.addCriteria(where("bookedBy").is("REDBUS-API"));
        query.addCriteria(Criteria.where("journeyDate").gte(ServiceUtils.parseDate(startDate, false))
                .lte(ServiceUtils.parseDate(endDate, true)));
        return query;
    }

    //to compare bookings from xls sheet
    public JSONObject findInvoiceBookings(String startDate, String endDate, String verificationId) throws ParseException {
        Query query = createQueryToFindBookings(startDate,endDate);
        query.fields().include("ticketNo");
        List<Booking> bookings = mongoTemplate.find(query,Booking.class);

        Set<String> ticketNos = bookings.parallelStream().map(Booking::getTicketNo)
                .collect(Collectors.toSet());


        List<InvoiceBooking> invoiceBookingNotInBookings = findInvoiceBookingsNotInBookings(ticketNos,verificationId);

        /*List<String> invoiceBookingsIds = invoiceBookingNotInBookings.parallelStream().map(InvoiceBooking::getId)
                .collect(Collectors.toList());*/

        List<InvoiceBooking> invoiceBookingList = invoiceBookingsMongoDAO.findAllInvoiceBookings(verificationId);

        Set<String> invoiceTicketNos = invoiceBookingList.parallelStream().map(InvoiceBooking::getTicketNo)
                .collect(Collectors.toSet());

        List<Booking> bookingsNotInInvoice = findBookingsNotInInvoice(startDate,endDate,invoiceTicketNos);

        JSONObject output = new JSONObject();
        output.put("invoiceBookingNotInBookings", invoiceBookingNotInBookings);
        output.put("bookingsNotInInvoice",bookingsNotInInvoice);
        output.put("cancelledBookings",findCancelledInvoiceBookingsInBookings(startDate,endDate,verificationId));
        return output;
    }

    public List<Booking> findCancelledInvoiceBookingsInBookings(String startDate, String endDate, String verificationId) throws ParseException {
        final Query query = new Query();
        query.addCriteria(where("apiCancellation").is(true));
        query.addCriteria(where("verificationId").is(verificationId));
        query.fields().include("ticketNo");
        List<InvoiceBooking> invoiceBookings = mongoTemplate.find(query,InvoiceBooking.class);
        Set<String> invoiceTicketNos = invoiceBookings.parallelStream().map(InvoiceBooking::getTicketNo)
                .collect(Collectors.toSet());

        Query query1 = createQueryToFindBookings(startDate,endDate);
        query1.addCriteria(where("ticketNo").in(invoiceTicketNos));
        return mongoTemplate.find(query1,Booking.class);
    }

    private List<Booking> findBookingsNotInInvoice(String startDate, String endDate, Set<String> ticketNos) throws ParseException {
        Query query = createQueryToFindBookings(startDate,endDate);
        query.addCriteria(where("ticketNo").nin(ticketNos));
        List<Booking> bookingList = mongoTemplate.find(query,Booking.class);
        return bookingList;
    }

    private List<InvoiceBooking> findInvoiceBookingsNotInBookings(Set<String> ticketNos, String verificationId) {
        final Query query = new Query();
        query.addCriteria(where("verificationId").is(verificationId));
        query.addCriteria(where("ticketNo").nin(ticketNos));
        query.addCriteria(where("apiCancellation").is(false));
        List<InvoiceBooking> invoiceBookings = mongoTemplate.find(query,InvoiceBooking.class);
        return invoiceBookings;
    }

    public boolean updateEmailedTaxInvoice(String id){
        Query query=new Query();
        query.addCriteria(where("_id").is(id));
        Update update = new Update();
        update.set("emailedTaxInvoice",true);
        UpdateResult ur=mongoTemplate.updateMulti(query, update, Booking.class);
        return (ur.getModifiedCount() > 0);
    }

    public void updateEmailedTaxInvoice(List<String> ids){
        Query query=new Query();
        query.addCriteria(where("_id").in(ids));
        Update update = new Update();
        update.set("emailedTaxInvoice",true);
        UpdateResult ur = mongoTemplate.updateMulti(query, update, Booking.class);
    }
    public List<VerifyInvoice> getVerifyInvoiceEntries() {
        Query query = new Query();
        query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        return mongoTemplate.find(query,VerifyInvoice.class);
    }

    public void excelParser(InputStream inputStream, String verificationId) throws IOException, InvalidFormatException {
        excelParser.parseInvoiceExcel(inputStream,verificationId);
    }
}
