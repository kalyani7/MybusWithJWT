package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Route;
import com.mybus.model.ServiceConfig;
import com.mybus.service.RouteManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by skandula on 1/18/16.
 */

@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="RouteController", description="RouteController management APIs")
public class RouteController extends MyBusBaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RouteManager routeManager;

    @RequestMapping(value = "routes/count", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get routes count", response = Long.class)
    public long getCount(HttpServletRequest request) {
        return routeManager.count();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "routes", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the routes available", response = Route.class, responseContainer = "List")
    public List<Route> getAll(HttpServletRequest request, Pageable pageable) {
        return routeManager.findAll(pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "route", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a Route")
    public Route create(HttpServletRequest request,
                        @ApiParam(value = "JSON for Route to be created") @RequestBody final Route route) {
        logger.debug("save route called");
        return routeManager.saveRoute(route);
    }

    @RequestMapping(value = "route/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the Route JSON", response = Route.class)
    public Route get(HttpServletRequest request,
                     @ApiParam(value = "Id of the Route to be found") @PathVariable final String id) {
        logger.debug("get city called");
        return routeManager.findOne(id);
    }

    @RequestMapping(value = "route/{id}", method = RequestMethod.PUT)
    @ApiOperation(value ="Update Route", response = Route.class)
    public boolean update(HttpServletRequest request,
                          @ApiParam(value = "Id of the Route to be found") @PathVariable final String id,
                          @ApiParam(value = "Route JSON") @RequestBody final Route route) {
        logger.debug("update route called");
        //save per
        return routeManager.update(route);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "route/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete a Route")
    public JSONObject delete(HttpServletRequest request,
                             @ApiParam(value = "Id of the Route to be deleted") @PathVariable final String id) {
        logger.debug("get city called");
        JSONObject response = new JSONObject();
        response.put("deleted", routeManager.deleteRoute(id));
        return response;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "route/searchServices", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public List<ServiceConfig> search(HttpServletRequest request,
                                      @RequestBody final JSONObject query)  {
        return routeManager.searchServices(query);
    }

}
