package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.GSTFilter;
import com.mybus.service.GSTFilterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/GSTFilters")
@Api(value="GSTFilterController")
public class GSTFilterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GSTFilterController.class);

    @Autowired
    private GSTFilterService gstFilterService;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the GSTFilters available", response = GSTFilter.class, responseContainer = "List")
    public List<GSTFilter> getAll() {
        LOGGER.debug("Get all the fillingStations available");
        return gstFilterService.getGSTFilters();
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Save filters")
    public Iterable<GSTFilter> addAmenity(HttpServletRequest request, @RequestBody List<GSTFilter> filters) {
        LOGGER.debug("Save filters");
        return gstFilterService.saveGSTFilters(filters);
    }

}
