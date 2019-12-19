package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.StaffDAO;
import com.mybus.model.Staff;
import com.mybus.service.StaffManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/")
public class StaffController extends MyBusBaseController {
    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);

    @Autowired
    private StaffDAO staffDAO;

    @Autowired
    private StaffManager staffManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "staff", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the staff available")
    public Page<Staff> getVehicles(HttpServletRequest request, final Pageable pageable) {
        Page<Staff> vs= staffManager.findStaff(null, null);
        return  vs;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "staff/count", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the staff available")
    public long count(HttpServletRequest request, @RequestParam(value = "filter", required = false) String filter, final Pageable pageable) {
        long count = staffManager.count(filter, pageable);
        return  count;
    }

    @RequestMapping(value = "staff/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the Staff JSON", response = Staff.class)
    public Staff getVehicle(HttpServletRequest request,
                            @ApiParam(value = "Id of the Vehicle Staff to be found") @PathVariable final String id) {
        logger.debug("get staff called");
        return staffDAO.findById(id).get();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "staff/create", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new staff")
    public ResponseEntity createStaff(HttpServletRequest request,
                                      @ApiParam(value = "JSON for Vehicle to be created") @RequestBody final Staff staff){
        logger.debug("create vehicle called");
        return new ResponseEntity<>(staffManager.saveStaff(staff), HttpStatus.OK);
    }

    @RequestMapping(value = "staff/aadharCopyUpload/{staffId}", method = RequestMethod.POST)
    public void aadharCopyUpload(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable(name = "staffId") String staffId){
        staffManager.aadharCopyUpload(request, staffId);
    }

    @RequestMapping(value = "staff/panCardCopyUpload/{staffId}", method = RequestMethod.POST)
    public void panCardCopyUpload(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable(name = "staffId") String staffId){
        staffManager.panCardCopyUpload(request, staffId);
    }

    @RequestMapping(value = "staff/profilePhotoCopyUpload/{staffId}", method = RequestMethod.POST)
    public void profilePhotoCopyUpload(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable(name = "staffId") String staffId){
        staffManager.profilePhotoCopyUpload(request, staffId);
    }

    @RequestMapping(value = "staff/drivingLicenseCopyUpload/{staffId}", method = RequestMethod.POST)
    public void drivingLicenseCopyUpload(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable(name = "staffId") String staffId){
        staffManager.drivingLicenseCopyUpload(request, staffId);
    }

    @RequestMapping(value = "staff/update/{staffId}", method = RequestMethod.POST)
    public void update(@RequestBody final Staff staff, @PathVariable(name = "staffId") String staffId){
        staffManager.updateStaffData(staff, staffId);
    }



}