package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.StaffComplaints;
import com.mybus.service.StaffComplaintManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/staffComplaints/")
public class StaffComplaintController {

    @Autowired
    private StaffComplaintManager complaintManager;

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get complaint ", response = JSONObject.class)
    public StaffComplaints getComplaint(HttpServletRequest request,
                                        @ApiParam(value = "Id of the staff to be found") @PathVariable final String id) {
        return complaintManager.getComplaint(id);
    }

    @RequestMapping(value = "addComplaint", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add complaint")
    public StaffComplaints addComplaint(HttpServletRequest request,
                                        @ApiParam(value = "JSON for complaint to be created") @RequestBody final StaffComplaints complaint){
        return complaintManager.addComplaint(complaint);
    }

    @RequestMapping(value = "updateComplaint", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update complaint")
    public StaffComplaints updateComplaint(HttpServletRequest request,
                                           @ApiParam(value = "JSON for complaint to be updated") @RequestBody final StaffComplaints complaint){
        return complaintManager.updateComplaint(complaint);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAll", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the complaints")
    public Page<StaffComplaints> getAllComplaints(HttpServletRequest request, @RequestParam(required = false, value = "query") String query,
                                                  final Pageable pageable) throws ParseException {
        return complaintManager.getAllComplaints(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete complaint")
    public boolean deleteComplaint(HttpServletRequest request,
                                   @ApiParam(value = "Id of the complaint to be deleted") @PathVariable final String id) {
        return complaintManager.delete(id);
    }

    @RequestMapping(value = "count", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get Complaints count", response = Long.class)
    public long getCount(HttpServletRequest request, @RequestParam(required = false, value = "query")String query, final Pageable pageable) throws ParseException {
        return complaintManager.count(query,pageable);
    }

}
