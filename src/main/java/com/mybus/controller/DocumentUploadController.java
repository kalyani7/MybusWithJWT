package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.DocumentUpload;
import com.mybus.service.DocumentUploadManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/documentUpload")
@Api(value="DocumentUploadController")
public class DocumentUploadController {

    @Autowired
    private DocumentUploadManager documentUploadManager;

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public List<String> continueFileUpload(HttpServletRequest request, HttpServletResponse response,
                                           @RequestParam(required = false) String fileName,
                                           @RequestParam(required = false) String description){
        return documentUploadManager.uploadFile(request,fileName,description);
    }

    @RequestMapping(value = "getAllUploads", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all uploads")
    public Page<DocumentUpload> getAllUploads(HttpServletRequest request,
                                              @ApiParam(value = "getAllUploads") @RequestBody final JSONObject data){
        return documentUploadManager.getAllUploads(data);
    }

    @RequestMapping(value = "deleteUpload", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "delete upload")
    public void deleteUpload(HttpServletRequest request,
                             @ApiParam(value = "delete upload") @RequestBody final JSONObject data){
        documentUploadManager.deleteUpload(data);
    }

    @RequestMapping(value = "count", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "count uploads")
    public long count(HttpServletRequest request,
                      @ApiParam(value = "count") @RequestBody final JSONObject data){
        return documentUploadManager.count(data);
    }
}
