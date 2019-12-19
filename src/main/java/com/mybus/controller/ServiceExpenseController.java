package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.ServiceExpense;
import com.mybus.service.ServiceExpenseManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/serviceExpense")
public class ServiceExpenseController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceExpenseController.class);

	@Autowired
	private ServiceExpenseManager serviceExpenseManager;

	@RequestMapping(value = "byDate", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get expenses for services", response = JSONObject.class)
	public List<ServiceExpense> getAll(HttpServletRequest request,
                                       @ApiParam(value = "Date of travel") @RequestParam final String travelDate) throws ParseException {
		return serviceExpenseManager.getServiceExpenses(travelDate);
	}


	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "search", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
	public List<ServiceExpense> search(HttpServletRequest request,
                                       @RequestBody final JSONObject query, final Pageable pageable) throws Exception {
		return serviceExpenseManager.findServiceExpenses(query, pageable);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get expenses for services", response = JSONObject.class)
	public ServiceExpense get(HttpServletRequest request,
                              @ApiParam(value = "ExpenseType id") @PathVariable final String id) throws ParseException {
		return serviceExpenseManager.getServiceExpense(id);
	}


	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Delete expense", response = JSONObject.class)
	public JSONObject delete(HttpServletRequest request,
                             @ApiParam(value = "ExpenseType id") @PathVariable final String id) throws ParseException {
		serviceExpenseManager.deleteServiceExpense(id);
		JSONObject result = new JSONObject();
		result.put("result", "done");
		return result;
	}
	@RequestMapping(value = "/", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get expenses for services", response = JSONObject.class)
	public ServiceExpense getAll(HttpServletRequest request,
                                 @RequestBody final ServiceExpense serviceExpense) throws ParseException {
		return serviceExpenseManager.save(serviceExpense);
	}

}
