package com.mybus.controller;


import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Layout;
import com.mybus.model.ServiceConfig;
import com.mybus.service.ServiceConfigManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/serviceConfig/")

public class ServiceConfigController{
    @Autowired
    private ServiceConfigManager serviceConfigManager;

    @RequestMapping(value = "addService", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add service")
    public ServiceConfig addService(HttpServletRequest request,
                                    @ApiParam(value = "JSON for service to be created") @RequestBody final ServiceConfig service) {
        return serviceConfigManager.addService(service);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAllServices", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get the service configs ", response = JSONObject.class)
    public Page<ServiceConfig> getAllServices(HttpServletRequest request,
                                              final Pageable pageable) throws ParseException {
        return serviceConfigManager.getAllServices(pageable);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getServicesCount", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get services count")
    public long getServicesCount(HttpServletRequest request) {
        return serviceConfigManager.getServicesCount();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAServiceConfig/{serviceConfigId}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get a service ", response = JSONObject.class)
    public ServiceConfig getServiceConfig(HttpServletRequest request, @ApiParam(value = "Id of the service configuration") @PathVariable final String serviceConfigId) {
        return serviceConfigManager.getServiceConfig(serviceConfigId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "deleteServiceConfig/{serviceConfigId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete service configuration")
    public boolean deleteServiceConfig(HttpServletRequest request,
                                       @ApiParam(value = "Id of the service to be deleted") @PathVariable final String serviceConfigId) {
        return serviceConfigManager.deleteServiceConfig(serviceConfigId);
    }

    @RequestMapping(value = "updateServiceConfig", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update service")
    public ServiceConfig updateJob(HttpServletRequest request,
                                   @ApiParam(value = "JSON for service configuration to be updated") @RequestBody final ServiceConfig serviceConfig) {
        return serviceConfigManager.updateServiceConfig(serviceConfig);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getLayoutsForService", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get layouts for service")
    public List<Layout> getLayoutsForService(HttpServletRequest request) {
        return serviceConfigManager.getLayoutsForService();
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getRoute/{routeId}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get route for service")
    public JSONObject getRouteForService(HttpServletRequest request, @ApiParam(value = "Id of the route") @PathVariable final String routeId) {
        return serviceConfigManager.getRouteForService(routeId);
    }



}