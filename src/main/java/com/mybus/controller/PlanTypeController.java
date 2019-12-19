package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.PlanTypeDAO;
import com.mybus.model.PlanType;
import com.mybus.service.PlanTypeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by svanik on 2/23/2016.
 */

@Controller
@RequestMapping(value = "/api/v1/")
@Api(value="PlanTypeController", description="PlanTypeController management APIs")
public class PlanTypeController extends MyBusBaseController {
    private static final Logger logger = LoggerFactory.getLogger(PlanTypeController.class);

    @Autowired
    private PlanTypeDAO planTypeDAO;

    @Autowired
    private PlanTypeManager planTypeManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "plans", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    @ApiOperation(value = "Get all the plans available", response = PlanType.class, responseContainer = "List")
    public Iterable<PlanType> getAll(HttpServletRequest request) {
        return planTypeDAO.findAll();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "plan", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Create a PlanType")
    public PlanType create(HttpServletRequest request,
                           @ApiParam(value = "JSON for Plan Type to be created") @RequestBody final JSONObject json) {
        logger.debug("save plan type called");
        return planTypeManager.savePlanType(new PlanType(json));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "plan", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Update a PlanType")
    public PlanType update(HttpServletRequest request,
                           @ApiParam(value = "JSON for Plan Type to be created") @RequestBody final JSONObject json) {
        logger.debug("update plan type called");
        return planTypeManager.updatePlanType(new PlanType(json));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "plan/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete a PlanType")
    public boolean delete(HttpServletRequest request,
                          @ApiParam(value = "PlanType Id")  @PathVariable final String id) {
        logger.debug("delete plan type called");
        return planTypeManager.deletePlanType(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "plan/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get a PlanType")
    public PlanType get(HttpServletRequest request,
                        @ApiParam(value = "PlanType Id")  @PathVariable final String id) {
        logger.debug("get plan type called");
        return planTypeDAO.findOneById(id);
    }

}
