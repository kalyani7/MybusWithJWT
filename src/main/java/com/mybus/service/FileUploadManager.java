package com.mybus.service;

import com.mybus.dao.FileUploadDAO;
import com.mybus.model.FileUpload;
import com.mybus.util.AmazonClient;
import com.mybus.SystemProperties;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class FileUploadManager {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadManager.class);

    @Autowired
    private SystemProperties props;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private FileUploadDAO fileUploadDAO;

    private String bucketName = "mybus-prod-uploads";

    /**
     * Saves a multipart file to a temp file, and returns it.
     *
     * @return the newly saved temp file stored on the local disk
     *//*
    public Path saveMultipartFile(final MultipartFile file) throws IOException {
        final Path tempFile = Files.createTempFile(null, null);
        if (logger.isDebugEnabled()) {
            logger.debug(format("Temp file for '%s' is: '%s'",
                    file.getOriginalFilename(), tempFile.toAbsolutePath().toString()));
        }
        file.transferTo(tempFile.toFile());
        return tempFile;
    }

    public Path downloadFileToTempDir(String fileUrl, String filenameSuffix) throws IOException {
        URL website = new URL(fileUrl);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        Path tempFile = Files.createTempFile(null, filenameSuffix);
        FileOutputStream fos = new FileOutputStream(tempFile.toString());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        return tempFile;
    }*/

    public List<String> uploadFile(HttpServletRequest request, String id, String type){
        try {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            mRequest.getParameterMap();
            Iterator<String> itr = mRequest.getFileNames();
            while (itr.hasNext()) {
                MultipartFile mFile = mRequest.getFile(itr.next());
                String fileName = mFile.getOriginalFilename();
                String newId = new ObjectId().toString();
                amazonClient.uploadFile(bucketName, type+"/"+newId, mFile);
                fileUploadDAO.save(new FileUpload(id, type, bucketName, newId, fileName));
            }
            return findUploadNames(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param request
     * @param path
     * @return
     */
    public String uploadDocument(HttpServletRequest request, String path){
        try {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            mRequest.getParameterMap();
            Iterator<String> itr = mRequest.getFileNames();
            if (itr.hasNext()) {
                MultipartFile mFile = mRequest.getFile(itr.next());
                String fileName = mFile.getOriginalFilename();
                amazonClient.uploadFile(bucketName, path+"/"+fileName, mFile);
                return path+"/"+fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> findUploadNames(String id) {
        Iterable<FileUpload> files = fileUploadDAO.findByRefId(id);
        List<String> urls = new ArrayList<>();
        files.forEach( f -> {
            urls.add(amazonClient.getSignedURL(bucketName, f.getFileName()));
        });
        return urls;
    }
    public List<JSONObject> findUploads(String id) {
        Iterable<FileUpload> files = fileUploadDAO.findByRefId(id);
        List<JSONObject> fileNames = new ArrayList<>();
        files.forEach( f -> {
            JSONObject file = new JSONObject();
            file.put("displayName", f.getDisplayName());
            file.put("fileName", f.getFileName());
            file.put("url",  amazonClient.getSignedURL(bucketName, f.getRefType()+"/"+f.getFileName()));
            fileNames.add(file);
        });
        return fileNames;
    }

    public boolean deleteFileFromS3(String refId, String fileName) {
        try {
            amazonClient.deleteFile(bucketName, fileName);
            fileUploadDAO.deleteByRefIdAndFileName(refId, fileName);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
