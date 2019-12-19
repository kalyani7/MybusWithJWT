package com.mybus.service;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class VelocityEngineService {
    public static final String EXPIRING_DOCUMENTS_TEMPLATE = "expiringDocuments.vm";
    public static final String PENDING_SERVICEREPORTS_TEMPLATE = "pendingServiceReports.vm";


    Map<String, Template> templates = new HashMap<>();
    VelocityEngine ve = null;

    @PostConstruct
    public void init(){
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
    }
    public String trasnform(Map<String,Object> contextParams, String templateName) {
        Template t = templates.get(templateName);
        if (t == null) {
            t = ve.getTemplate("velocity/"+templateName);
        }
        if(t == null){
            throw new RuntimeException("Template not found with name "+ templateName);
        }
        VelocityContext vc = new VelocityContext();
        vc.put("date", new DateTool());
        contextParams.entrySet().stream().forEach(entry -> {vc.put(entry.getKey(), entry.getValue());});
        StringWriter sw = new StringWriter();
        t.merge(vc, sw);
        return sw.toString();
    }
}
