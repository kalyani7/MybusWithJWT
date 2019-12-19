package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Reminder;
import com.mybus.service.ReminderManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/reminders/")

public class ReminderController{
    @Autowired
    private ReminderManager reminderManager;


    //Add Reminder
    @RequestMapping(value = "addReminder", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add reminder")
    public Reminder addReminder(HttpServletRequest request,
                                @ApiParam(value = "JSON for reminder to be created") @RequestBody final Reminder reminder){
        return reminderManager.addReminder(reminder);
    }

    //Update Reminder
    @RequestMapping(value = "updateReminder", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update reminder")
    public Reminder updateReminder(HttpServletRequest request,
                                   @ApiParam(value = "JSON for reminder to be updated") @RequestBody final Reminder reminder){
        return reminderManager.updateReminder(reminder);
    }

    //Delete Reminder
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete reminder")
    public boolean deleteReminder(HttpServletRequest request,
                                  @ApiParam(value = "Id of the reminder to be deleted") @PathVariable final String id) {
        return reminderManager.delete(id);
    }

    //Get a reminder
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get a reminder ", response = JSONObject.class)
    public Reminder getReminder(HttpServletRequest request, @ApiParam(value = "Id of the reminder") @PathVariable final String id) {
        return reminderManager.getReminder(id);
    }

    //Get all Reminders
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAll", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the reminders")
    public Page<Reminder> getReminders(HttpServletRequest request, @RequestBody final JSONObject query) throws ParseException {
        return reminderManager.getReminders(query);
    }

    //Get upcoming Reminders upto 10-Days
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getUpcoming", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the upcoming reminders")
    public List<Reminder> getUpcomingReminders(HttpServletRequest request) {
        return reminderManager.getUpcomingReminders();
    }

    //Count
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getCount", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public long remindersCount(HttpServletRequest request,
                               @RequestBody final JSONObject query) throws ParseException {
        return reminderManager.reminderCount(query);
    }

}