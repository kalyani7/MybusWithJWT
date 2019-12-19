package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.BranchOffice;
import com.mybus.service.BranchOfficeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by srinikandula on 12/11/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="BranchOfficeController", description="BranchOfficeController management APIs")
public class BranchOfficeController extends MyBusBaseController {

    private static final Logger logger = LoggerFactory.getLogger(BranchOfficeController.class);

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "branchOffices", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the branchOffices available", response = BranchOffice.class, responseContainer = "List")
    public Page<BranchOffice> getAll(HttpServletRequest request,
                                     @ApiParam(value = "JSON Query") @RequestBody(required = false) final JSONObject query,
                                     final Pageable pageable) {
        return branchOfficeManager.find(query, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "branchOffices/count", method = RequestMethod.GET)
    @ApiOperation(value = "Get all the branchOffices available")
    public long count(HttpServletRequest request,
                      @ApiParam(value = "JSON Query") @RequestBody(required = false) final JSONObject query) {
        return branchOfficeManager.count(query);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "branchOffice/names", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all names of branchOffices available", response = BranchOffice.class, responseContainer = "List")
    public Iterable<BranchOffice> getNamesMap(HttpServletRequest request) {
        return branchOfficeManager.getNames();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "branchOffice", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a BranchOffice")
    public BranchOffice create(HttpServletRequest request,
                               @ApiParam(value = "JSON for BranchOffice to be created") @RequestBody final BranchOffice branchOffice) {
        logger.debug("save BranchOffice called");
        return branchOfficeManager.save(branchOffice);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "branchOffice/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a BranchOffice")
    public BranchOffice update(HttpServletRequest request,
                               @ApiParam(value = "Id of the BranchOffice to be found") @PathVariable final String id,
                               @ApiParam(value = "JSON for BranchOffice to be updated") @RequestBody final BranchOffice branchOffice) {
        logger.debug("update BranchOffice called");
        return branchOfficeManager.update(id, branchOffice);
    }

    @RequestMapping(value = "branchOffice/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the BranchOffice", response = BranchOffice.class)
    public BranchOffice get(HttpServletRequest request,
                            @ApiParam(value = "Id of the BranchOffice to be found") @PathVariable final String id) {
        logger.debug("get BranchOffice called");
        return branchOfficeManager.findOne(id);
    }

    @RequestMapping(value = "branchOffice/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a BranchOffice", response = BranchOffice.class)
    public JSONObject delete(HttpServletRequest request,
                             @ApiParam(value = "Id of the BranchOffice to be removed") @PathVariable final String id) {
        logger.debug("delete BranchOffice called");
        JSONObject response = new JSONObject();
        branchOfficeManager.delete(id);
        response.put("deleted", true);
        return response;
    }
}
