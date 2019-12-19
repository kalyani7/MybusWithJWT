package com.mybus.controller;

import com.mybus.service.FileUploadManager;
import io.swagger.annotations.Api;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="FileIOController", description="AWSS3IOController management APIs")
public class FileIOController extends MyBusBaseController {

    @Autowired
    private FileUploadManager fileUploadManager;

    @RequestMapping(value = "fileUpload", method = RequestMethod.POST)
    public List<String> continueFileUpload(HttpServletRequest request, HttpServletResponse response,
                                           @RequestParam(required = false) String id,
                                           @RequestParam(required = false) String type){
        return fileUploadManager.uploadFile(request, id, type);
    }

    @RequestMapping(value = "deleteUpload", method = RequestMethod.POST)
    public ResponseEntity<String> deleteUploadFromS3(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestBody(required = true) JSONObject body){
        if(!body.containsKey("fileName")){
            return new ResponseEntity<String>("Fail ->File name is missing",
                    HttpStatus.BAD_REQUEST);
        }
        if(!body.containsKey("refId")){
            return new ResponseEntity<String>("Fail ->Reference Id is missing",
                    HttpStatus.BAD_REQUEST);
        }

        fileUploadManager.deleteFileFromS3(body.get("refId").toString(), body.get("fileName").toString());

        return new ResponseEntity<String>("File is deleted",
                HttpStatus.OK);


    }

    @RequestMapping(value = "getUploads", method = RequestMethod.GET)
    public List<JSONObject> continueFileUpload(HttpServletRequest request, HttpServletResponse response,
                                               @RequestParam String id){
        return fileUploadManager.findUploads(id);
    }

}
