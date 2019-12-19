package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.service.CargoBookingManager;
import com.mybus.service.CargoDashboardManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by srinikandula on 12/11/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="CargoDashBoardController", description="CargoDashBoardController management APIs")
public class CargoDashboardController extends MyBusBaseController {

    private static final Logger logger = LoggerFactory.getLogger(CargoDashboardController.class);

    @Autowired
    private CargoBookingManager cargoBookingManager;

    @Autowired
    private CargoDashboardManager cargoDashboardManager;


    @RequestMapping(value = "cargoDashboard/content", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the cargoDashboard content", response = JSONObject.class)
    public JSONObject get(HttpServletRequest request) {
        logger.debug("Get the cargoDashboard content called");
        return null;
    }


}
