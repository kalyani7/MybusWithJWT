package com.mybus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReportsController extends MyBusBaseController {

	@RequestMapping(value = "/uploadReport", method = RequestMethod.GET)
    public ModelAndView getUploadForm() {
        ModelAndView model = new ModelAndView();
        model.setViewName("uploadReport");
        return model;
    }

    @RequestMapping(value = "/uploadReport", method = RequestMethod.POST)
    public ModelAndView postUploadForm() {
        ModelAndView model = new ModelAndView();
        model.setViewName("uploadReport");
        return model;
    }

}
