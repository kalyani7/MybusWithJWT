package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Job;
import com.mybus.service.JobManager;
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

@RestController
@RequestMapping(value = "/api/v1/jobs/")

public class JobController {
    @Autowired
    private JobManager jobManager;
/*
    @RequestMapping(value = "addJob", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add job")
    public Job addJob(HttpServletRequest request,
                      @ApiParam(value = "JSON for job to be created") @RequestBody final Job job) {
        return jobManager.addJob(job);
    }*/

    @PostMapping("addJob")
    public Job addJob(@RequestBody Job data){
        return jobManager.addJob(data);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getPendingJobs", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get pending jobs ", response = JSONObject.class)
    public Page<Job> getPendingJobs(HttpServletRequest request,
                                    final Pageable pageable) throws ParseException {
        return jobManager.getJobs(pageable, false);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getCompletedJobs", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get pending jobs ", response = JSONObject.class)
    public Page<Job> getCompletedJobs(HttpServletRequest request,
                                      final Pageable pageable) throws ParseException {
        return jobManager.getJobs(pageable, true);
    }

    @RequestMapping(value = "updateJob", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update job")
    public Job updateJob(HttpServletRequest request,
                         @ApiParam(value = "JSON for job to be updated") @RequestBody final Job job) {
        return jobManager.updateJob(job);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete job")
    public boolean deleteInventory(HttpServletRequest request,
                                   @ApiParam(value = "Id of the job to be deleted") @PathVariable final String id) {
        return jobManager.delete(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAJob/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get a job ", response = JSONObject.class)
    public Job getJob(HttpServletRequest request, @ApiParam(value = "Id of the job") @PathVariable final String id) {
        return jobManager.getJob(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getCount", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public long getCount(HttpServletRequest request,
                         @RequestBody final JSONObject query, final Pageable pageable) throws ParseException {
        return jobManager.getCount(query);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "searchJobs", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    public Page<Job> search(HttpServletRequest request,
                            @RequestBody final JSONObject query) throws ParseException {
        return jobManager.search(query);
    }

}