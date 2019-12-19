package com.mybus.controller;

import com.mybus.service.FileUploadManager;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/deprecated")
@Api(value="DueReportController", description="DueReportController management APIs")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(DueReportController.class);

    @Autowired
    private FileUploadManager fileUploadManager;

    /*@Autowired
    private AWSClient awsClient;

    @ApiOperation(value = "Upload a file")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public JSONObject handleFormUpload(@RequestParam(value = "file", required = true) final MultipartFile file,
                                       @RequestParam(value = "docId", required = true) final String documentId,
                                       @RequestParam(value = "s3BucketName", required = true) final String s3BucketName,
                                       final HttpServletRequest httpServletRequest)
            throws InterruptedException, IOException {
       // ServiceUtils.logRequestPath(logger, httpServletRequest);

        if (file.isEmpty()) {
            throw new FileUploadException("The uploaded file was empty.");
        }
        Path path = fileUploadManager.saveMultipartFile(file);
        try {
            String[] result  = awsClient.uploadFileToS3(s3BucketName, path, documentId);
        } catch (IOException e) {
            throw new BadRequestException("error saving multipart file", "Error saving file.", e);
        }
        return null;
    }
*/
}
