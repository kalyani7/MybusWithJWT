package com.mybus.controller;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/api/v1/")
@Api(value="ApplicationConfigController", description="Application configuration info")

public class AppConfigController {

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = { "/appconfig" }, method = RequestMethod.GET)
    @ResponseBody
	public String appconfig() {
        return "";
	}
}
