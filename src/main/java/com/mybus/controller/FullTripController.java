package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.FullTrip;
import com.mybus.service.FullTripManager;
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
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/")
public class FullTripController {
    private static final Logger logger = LoggerFactory.getLogger(FullTripController.class);

    @Autowired
    private FullTripManager fullTripManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "fullTrips", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public Page<FullTrip> getPayments(HttpServletRequest request, @RequestBody(required = false) final JSONObject query,
                                      final Pageable pageable) throws ParseException {
        return fullTripManager.search(query, pageable);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "fullTrip/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public FullTrip getTrip(HttpServletRequest request, @PathVariable final String id) {
        return fullTripManager.findOne(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "fullTrips/count", method = RequestMethod.GET)
    public long getCount(HttpServletRequest request, @RequestBody(required = false) final JSONObject query) throws ParseException {
        return fullTripManager.count(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "fullTrip", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public FullTrip create(HttpServletRequest request, @RequestBody final FullTrip fullTrip) {
        logger.debug("post fulltrip called");
        return fullTripManager.save(fullTrip);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "fullTrip", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public FullTrip updateFullTrip(HttpServletRequest request, @RequestBody final FullTrip fullTrip) {
        logger.debug("put FullTrip called");
        return fullTripManager.updateFullTrip(fullTrip);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "fullTrip/pay/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
    public boolean payFullTrip(HttpServletRequest request, @PathVariable final String id) {
        logger.debug("pay FullTrip called");
        return fullTripManager.payFullTrip(id);
    }

    @RequestMapping(value = "fullTrip/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a Payment", response = FullTrip.class)
    public JSONObject delete(HttpServletRequest request,
                             @ApiParam(value = "Id of the Payment to be removed") @PathVariable final String id) {
        logger.debug("delete FullTrip called");
        JSONObject response = new JSONObject();
        fullTripManager.delete(id);
        response.put("deleted", true);
        return response;
    }

}
