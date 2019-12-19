package com.mybus.controller;


import com.mybus.controller.util.ControllerUtils;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Booking;
import com.mybus.model.BranchOfficeDue;
import com.mybus.service.BookingManager;
import com.mybus.service.DueReportManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 12/11/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="DueReportController", description="DueReportController management APIs")
public class DueReportController extends MyBusBaseController {

    private static final Logger logger = LoggerFactory.getLogger(DueReportController.class);

    @Autowired
    private DueReportManager dueReportManager;

    @Autowired
    private BookingManager bookingManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReports", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the due reports for branch offices", response = BranchOfficeDue.class, responseContainer = "List")
    public Iterable<BranchOfficeDue> getAllDueReports(HttpServletRequest request,
                                                      final Pageable pageable) {
        return dueReportManager.getBranchOfficesDueReports();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/office/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get branch office due report", response = BranchOfficeDue.class )
    public BranchOfficeDue getBranchDueReport(HttpServletRequest request,
                                              @ApiParam(value = "Id of the BranchOffice") @PathVariable final String id,
                                              final Pageable pageable) {
        return dueReportManager.findOfficeDuesGroupByDate(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/office/all/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get branch office due report", response = List.class )
    public List<Booking> getBranchDueReport(HttpServletRequest request,
                                            @ApiParam(value = "Id of the BranchOffice") @PathVariable final String id) {
        return dueReportManager.getBranchOfficeDues(id);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/office/{id}/{date}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get branch office due report", response = BranchOfficeDue.class )
    public BranchOfficeDue getBranchDueReportByDate(HttpServletRequest request,
                                                    @ApiParam(value = "Id of the BranchOffice") @PathVariable final String id,
                                                    @PathVariable(value = "date", required = true) String date,
                                                    final Pageable pageable) {
        return dueReportManager.getOfficeDuesByDate(id, date);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/search", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Search DueReport", response = BranchOfficeDue.class )
    public List<Booking> searchDues(HttpServletRequest request,
                                    @ApiParam(value = "Start date in yyyy/mm/dd format")
                                         @RequestParam(value = "startDate", required = true) final String startDate,
                                    @ApiParam(value = "End date in yyyy/mm/dd format")
                                         @RequestParam(value = "endDate", required = true) final String endDate,
                                    @RequestParam(value = "branchOfficeId", required = false) final String branchOfficeId)
            throws ParseException {
        return dueReportManager.searchDues(startDate, endDate, branchOfficeId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/searchByPNR", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Search DueReport", response = BranchOfficeDue.class )
    public Iterable<Booking> searchDuesByPNR(HttpServletRequest request,
                                             @RequestParam(value = "pnr", required = true) final String pnr){
        return dueReportManager.searchDuesByPNR(pnr);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/payBookingDue/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Record due payment")
    public boolean recordDuePayment(HttpServletRequest request,
                                    @ApiParam(value = "Id of the booking") @PathVariable final String id) {
        if(bookingManager.payBookingDue(id)){
            return true;
        } else {
            throw new BadRequestException("Booking payment failed");
        }
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/payBookingDues", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Record due payment")
    public List<Booking> recordDuePayments(HttpServletRequest request,
                                           @ApiParam(value = "Ids of the bookings") @RequestBody final List<String> ids) {
       return bookingManager.payBookingDues(ids);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/officeDuesByService", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Record due payment", response = BranchOfficeDue.class )
    public List<Document> getOfficeDuesByService(HttpServletRequest request) {
        return dueReportManager.getOfficeDuesByService();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/dueBookingByService/{serviceNumber}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Record due payment", response = BranchOfficeDue.class )
    public List<Booking> getDueBookingByService(HttpServletRequest request, @PathVariable final String serviceNumber) {
        return dueReportManager.getDueBookingsByService(serviceNumber);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/officeDuesByAgent", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Agent due totals", response = BranchOfficeDue.class )
    public List<Document> getOfficeDuesByAgents(HttpServletRequest request) {
        return dueReportManager.getBookingDuesGroupByAgent();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/officeDuesByAgent/{agentName}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Find Agent dues", response = BranchOfficeDue.class )
    public List<Booking> getDueBookingByAgent(HttpServletRequest request, @PathVariable final String agentName) {
        return dueReportManager.getDueBookingsByAgent(agentName);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/returnTickets", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Find return ticket Agent dues", response = BranchOfficeDue.class )
    public JSONObject getReturnTicketDues(HttpServletRequest request,
                                          @RequestParam(name = "branchOfficeId", required = false) String branchOfficeId) {
        JSONObject response = new JSONObject();
        long start = System.currentTimeMillis();
        List<Booking> dueBookings = dueReportManager.getReturnTicketDues(branchOfficeId);
        long end = System.currentTimeMillis();
        logger.info("found dues in " + (end - start));
        response.put("allDues", dueBookings);
        Map<Long, List<Booking>> byDate = new HashMap<>();
        Map<String, List<Booking>> byAgentName = new HashMap<>();
        start = System.currentTimeMillis();
        dueReportManager.groupReturnTicketDues(dueBookings, byDate, byAgentName);
        end = System.currentTimeMillis();
        logger.info("mapping done in " + (end - start));
        response.put("allDuesMappedByDate", byDate);
        response.put("allDuesMappedByAgent", byAgentName);
        return response;
    }
}
