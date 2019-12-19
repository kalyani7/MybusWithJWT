package com.mybus.service;

import com.mybus.dao.DocumentUploadDAO;
import com.mybus.dao.DocumentUploadMongoDAO;
import com.mybus.model.DocumentUpload;
import com.mybus.util.AmazonClient;
import com.mybus.util.ServiceUtils;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class DocumentUploadManager {

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private DocumentUploadDAO documentUploadDAO;

    @Autowired
    private UserManager userManager;

    @Autowired
    private DocumentUploadMongoDAO documentUploadMongoDAO;


    public List<String> uploadFile(HttpServletRequest request, String nameOfFile, String description) {
        try {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            mRequest.getParameterMap();
            Iterator<String> itr = mRequest.getFileNames();
            while (itr.hasNext()) {
                MultipartFile mFile = mRequest.getFile(itr.next());
                String displayName = mFile.getOriginalFilename();
                String newId = new ObjectId().toString();
                String type = "Documents";
                amazonClient.uploadFile(ServiceUtils.BUCKET_NAME, type+"/"+newId, mFile);
                saveUploadData(nameOfFile,type+"/"+newId,description,displayName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveUploadData(String nameOfFile, String filePath, String description, String displayName) {
        DocumentUpload documentUpload = new DocumentUpload();
        documentUpload.setFileName(nameOfFile);
        documentUpload.setDescription(description);
        documentUpload.setFilePath(filePath);
        documentUpload.setDisplayName(displayName);
        documentUploadDAO.save(documentUpload);
    }

    public Page<DocumentUpload> getAllUploads(JSONObject data) {
        Map<String,String> userNamesMap = userManager.getUserNames(true);
        Page<DocumentUpload> documentUploads = documentUploadMongoDAO.getAllUploads(data);
        documentUploads.getContent().stream().forEach(documentUpload -> {
          documentUpload.getAttributes().put("url",amazonClient.getSignedURL(ServiceUtils.BUCKET_NAME, documentUpload.getFilePath()));
          documentUpload.getAttributes().put("uploadedBy",userNamesMap.get(documentUpload.getUpdatedBy()));
          documentUpload.getAttributes().put("uploadedAt", ServiceUtils.formatDate(documentUpload.getUpdatedAt().toDate()));
        });
        return documentUploads;
    }

    public void deleteUpload(JSONObject data) {
        amazonClient.deleteFile(ServiceUtils.BUCKET_NAME,data.get("key").toString());
        // delete document upload entry
        documentUploadDAO.deleteById(data.get("id").toString());
    }

    public long count(JSONObject data) {
        return documentUploadMongoDAO.count(data);
    }
}
