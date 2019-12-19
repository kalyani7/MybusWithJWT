package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.PreferredStaff;
import com.mybus.model.Staff;
import com.mybus.service.PreferredStaffManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/preferredStaff/")

public class PreferredStaffController{
    @Autowired
    private PreferredStaffManager preferredStaffManager;

    @RequestMapping(value = "addPreferredStaff", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add staff")
    public PreferredStaff addPreferredStaff(HttpServletRequest request,
                                            @ApiParam(value = "JSON for staff to be created") @RequestBody final PreferredStaff preferredStaff){
        return preferredStaffManager.addPreferredStaff(preferredStaff);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAllStaff", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the staff")
    public Page<PreferredStaff> getAllStaff(HttpServletRequest request, @RequestBody final JSONObject query) {
        return preferredStaffManager.getAllPreferredStaff(query);
    }

    @RequestMapping(value = "updatePreferredStaff", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update staff")
    public PreferredStaff updatePreferredStaff(HttpServletRequest request,
                                               @ApiParam(value = "JSON for staff to be updated") @RequestBody final PreferredStaff preferredStaff){
        return preferredStaffManager.updatePreferredStaff(preferredStaff);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete staff")
    public boolean deleteReminder(HttpServletRequest request,
                                  @ApiParam(value = "Id of the staff to be deleted") @PathVariable final String id) {
        return preferredStaffManager.delete(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get a staff ", response = JSONObject.class)
    public PreferredStaff getStaffById(HttpServletRequest request, @ApiParam(value = "Id of the staff") @PathVariable final String id) {
        return preferredStaffManager.getStaffById(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getCount", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get count")
    public long getPreferredStaffCount(HttpServletRequest request, @RequestBody final JSONObject query) {
        return preferredStaffManager.count(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getStaffForDailyTrips", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the staff available")
    public List<Staff> getStaffForTrips(HttpServletRequest request, @RequestBody final JSONObject query) {
        return preferredStaffManager.getStaffForDailyTrips(query);
    }

}