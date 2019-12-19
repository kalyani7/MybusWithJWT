package com.mybus.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.mybus.SystemProperties;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class AmazonClient {
    private static final Logger logger = LoggerFactory.getLogger(AmazonClient.class);

    @Autowired
    private SystemProperties systemProperties;
    @Setter
    private AmazonS3 s3Client;

    @PostConstruct
    private void initializeAmazon() {
        String awsAccessKey = systemProperties.getProperty("aws.accesskey");
        String awsSecretKey = systemProperties.getProperty("aws.secretkey");
        if(StringUtils.isEmpty(awsAccessKey)|| StringUtils.isEmpty(awsSecretKey)){
            throw new IllegalStateException("Please check configuration for aws crendentials");
        }
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }
    public JSONObject uploadFile(String bucketName, String fileName, MultipartFile mFile) throws IOException {
        byte[] contents = IOUtils.toByteArray(mFile.getInputStream());
        InputStream stream = new ByteArrayInputStream(contents);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(contents.length);
        meta.setContentType(mFile.getContentType());
        PutObjectResult result = s3Client.putObject(new PutObjectRequest(bucketName, fileName, stream, meta));
        return new JSONObject();
    }

    public String getSignedURL(String bucketName, String filePath){
        // Set the presigned URL to expire after one hour.
      /*  java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, filePath)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();*/
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, filePath);
        ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
        overrides.setContentDisposition("attachment; filename="+filePath);
        req.setResponseHeaders(overrides);
        URL url = s3Client.generatePresignedUrl(req);
        return url.toString();
    }

    public InputStream downloadFile(String bucketName, String key) throws IOException {
        S3Object result = s3Client.getObject(bucketName,key);
        S3ObjectInputStream data = result.getObjectContent();
        byte[] contents = IOUtils.toByteArray(data);
        InputStream stream = new ByteArrayInputStream(contents);
        return stream;
    }


    public void deleteFile(String bucketName, String key){
        s3Client.deleteObject(bucketName,key);
    }
}