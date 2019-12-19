package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.cargo.ShipmentSequenceDAO;
import com.mybus.model.cargo.ShipmentSequence;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by busda001 on 7/8/17.
 */
@RestController
@RequestMapping(value = "/api/v1/shipmentSequence")
@Api(value="ShipmentSequenceController", description="ShipmentSequenceController management APIs")
public class ShipmentSequenceController {
    private static final Logger logger = LoggerFactory.getLogger(ShipmentSequenceController.class);

    @Autowired
    private ShipmentSequenceDAO shipmentSequenceDAO;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the ShipmentSequences available", response = ShipmentSequence.class, responseContainer = "List")
    public Iterable<ShipmentSequence> getAll(HttpServletRequest request) {
        return shipmentSequenceDAO.findAll();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/byType/{sequenceType}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the ShipmentSequences available", response = ShipmentSequence.class)
    public ShipmentSequence getShipmentSequence(HttpServletRequest request, @PathVariable String sequenceType) {
        return shipmentSequenceDAO.findByShipmentCode(sequenceType);
    }

}
