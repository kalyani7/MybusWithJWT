package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.service.ServiceReportsManager;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/")
public class ServiceReportController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceReportController.class);

	@Autowired
	private ServiceReportsManager serviceReportsManager;

	@Autowired
	private ServiceReportMongoDAO serviceReportMongoDAO;

	@Autowired
	private SessionManager sessionManager;

	@RequestMapping(value = "serviceReport/downloadStatus", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get status of reports download", response = JSONObject.class)
	public JSONObject getDownloadStatus(HttpServletRequest request,
                                        @ApiParam(value = "Date of travel") @RequestParam final String travelDate) throws ParseException {
		return serviceReportsManager.getDownloadStatus(travelDate);
	}

	@RequestMapping(value = "serviceReport/download", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Download reports for a given date", response = JSONObject.class)
	public JSONObject downloadReports(HttpServletRequest request,
                                      @ApiParam(value = "Date of travel") @RequestParam final String travelDate) {
		try{
			return serviceReportsManager.downloadReports(travelDate);
		}catch (Exception e) {
			throw new BadRequestException("Error downloading reports "+ e);
		}
	}
	
	@RequestMapping(value = "serviceListings/activeListings", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get active service list for a given date", response = JSONObject.class)
	public JSONObject getServicesByDate(HttpServletRequest request,
                                        @ApiParam(value = "Date of travel") @RequestParam final String travelDate) {
		try{
			return serviceReportsManager.getServicesByDate(travelDate);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error downloading services");
		}
	}
	
	@RequestMapping(value = "serviceReport/downloadServices", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Download service details a given service number and date", response = JSONObject.class)
	public JSONObject getServiceDetailsByNumberAndDate(HttpServletRequest request,
                                                       @ApiParam(value = "Date of travel") @RequestParam final String travelDate,
                                                       @ApiParam(value = "Service Number") @RequestParam final String serviceNum) {
		try{
			return serviceReportsManager.downloadServiceDetailsByNumberAndDate(serviceNum, travelDate);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error downloading service details");
		}
	}

	@RequestMapping(value = "serviceReport/loadReports", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load reports for a given date", response = JSONObject.class)
	public Iterable<ServiceReport> loadReports(HttpServletRequest request,
											   @ApiParam(value = "Date of travel") @RequestParam final String travelDate) {
		try{
			return serviceReportsManager.getReports(travelDate);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error loading reports", e);
		}
	}

	@RequestMapping(value = "serviceReport/refresh", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load reports for a given date", response = JSONObject.class)
	public Iterable<ServiceReport> refreshReports(HttpServletRequest request,
                                                  @ApiParam(value = "Date of travel") @RequestParam final String travelDate) {
		try{
			return serviceReportsManager.refreshReport(ServiceUtils.parseDate(travelDate));
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error refreshing reports", e);
		}
	}

	@RequestMapping(value = "serviceReport/pending", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load pending reports", response = JSONObject.class)
	public Iterable<ServiceReport> findPendingReports(HttpServletRequest request) {
		try{
			return serviceReportMongoDAO.findPendingReports(null, null);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error :Load pending reports ", e);
		}
	}
	@RequestMapping(value = "serviceReport/toBeReviewed", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load pending reports", response = JSONObject.class)
	public Iterable<ServiceReport> findReportsReportsToBeViewed(HttpServletRequest request) {
		try{
			return serviceReportMongoDAO.findReportsToBeReviewed(null, null);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Error :Load pending reports ", e);
		}
	}
	@RequestMapping(value = "serviceReport/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load one service report", response = JSONObject.class)
	public ServiceReport getServiceReport(HttpServletRequest request,
                                          @ApiParam(value = "id")@PathVariable final String id) {
		try{
			ServiceReport report = serviceReportsManager.getReport(id);
			return report;
		}catch (Exception e) {
			throw new BadRequestException("Error loading report" + e);
		}
	}

	@RequestMapping(value = "serviceReport/booking/{bookingId}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Find Booking by ID", response = JSONObject.class)
	public Booking getServiceReportBooking(HttpServletRequest request,
										   @ApiParam(value = "bookingId")@PathVariable final String bookingId) {
			Booking booking = serviceReportsManager.getBooking(bookingId);
			return booking;
	}

	@RequestMapping(value = "serviceForm/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load one service form", response = JSONObject.class)
	public ServiceForm getServiceForm(HttpServletRequest request,
									  @ApiParam(value = "id")@PathVariable final String id) {
		try{
			ServiceForm report = serviceReportsManager.getForm(id);
			return report;
		}catch (Exception e) {
			throw new BadRequestException("Error loading report");
		}
	}


	@RequestMapping(value = "serviceReport/{status}", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Submit service report", response = JSONObject.class)
	public void submitReport(HttpServletRequest request,
                             @ApiParam(value = "JSON for ServiceReort to be submmitted")
				@RequestBody final ServiceReport serviceReport,
                             @PathVariable String status) throws Exception {
		User currentUser = sessionManager.getCurrentUser();
		if(serviceReport.isRequiresVerification()) {
			if(currentUser.isCanVerifyRates()){
				serviceReport.setVerifiedOn(new Date());
				serviceReport.setVerifiedBy(currentUser.getId());
				serviceReportsManager.submitReport(serviceReport, ServiceStatus.SUBMITTED.toString());
			} else {
				serviceReport.setSubmittedOn(new Date());
				serviceReport.setSubmittedBy(currentUser.getId());
				serviceReportsManager.saveServiceReportForVerification(serviceReport);
			}
		} else {
			serviceReport.setSubmittedOn(new Date());
			serviceReport.setSubmittedBy(currentUser.getId());
			serviceReportsManager.submitReport(serviceReport, status);
		}
	}

	@RequestMapping(value = "serviceReport/incomeReport", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load one service form", response = JSONObject.class)
	public List<Document> getIncomeReport(HttpServletRequest request, @RequestBody final JSONObject query) {
		try{
			return serviceReportMongoDAO.findServiceIncomeReport(query);
		}catch (Exception e) {
			throw new BadRequestException("Error finding income report");
		}
	}

	@RequestMapping(value = "serviceReport/haltedServices", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get halted services", response = JSONObject.class)
	public List<ServiceReport> getHaltedServices(HttpServletRequest request,
                                                 @ApiParam(example = "2018-11-25") @RequestParam final String date) {
		try{
			return serviceReportsManager.findHaltedReports(date);
		}catch (Exception e) {
			throw new BadRequestException("Error finding halted reports");
		}
	}
	@RequestMapping(value = "serviceReport/filterHaltedServices", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="filter halted services", response = JSONObject.class)
	public List<ServiceReport> getHaltedServices(HttpServletRequest request, @RequestBody final JSONObject query) {
		try{
			return serviceReportsManager.filterHaltedReports(query);
		}catch (Exception e) {
			throw new BadRequestException("Error finding halted reports");
		}
	}
	@RequestMapping(value = "serviceReport/serviceIncomeReportDaily", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Find the daily stats for service reports. Arguments serviceNumber, startDate, endDate",
			response = JSONObject.class)
	public List<ServiceReport> getServiceIncomeReport(HttpServletRequest request, @RequestBody final JSONObject query) {
		try{
			return serviceReportsManager.findServiceIncomeReports(query);
		}catch (Exception e) {
			throw new BadRequestException("Error finding income report");
		}
	}
	@RequestMapping(value = "serviceReport/getCities", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Load one service form", response = JSONObject.class)
	public List<String> getCities(HttpServletRequest request) {
		List<String> cities = serviceReportsManager.getAllCities();
		cities.sort(String::compareTo);
		return cities;
	}

}
