package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.impl.PaymentMongoDAO;
import com.mybus.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by srinikandula on 3/26/17.
 */
@RestController
@RequestMapping(value = "/api/v1/")
public class VehicleExpensesController extends MyBusBaseController {
    @Autowired
    private PaymentMongoDAO paymentMongoDAO;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicleExpenses/count", method = RequestMethod.GET)
    public long getCount(HttpServletRequest request) {
        return paymentMongoDAO.getVehicleExpensesCount();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicleExpenses", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    public Page<Payment> getApprovedPayments(HttpServletRequest request, final Pageable pageable) {
        return paymentMongoDAO.getVehicleExpenses(pageable);
    }
}
