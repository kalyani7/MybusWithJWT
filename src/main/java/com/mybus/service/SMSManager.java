package com.mybus.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.SystemProperties;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.SMSNotificationDAO;
import com.mybus.model.OperatorAccount;
import com.mybus.model.SMSNotification;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@Service
public class SMSManager {
    private static final Logger logger = LoggerFactory.getLogger(SMSManager.class);

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SMSNotificationDAO smsNotificationDAO;

    @Autowired
    private Environment environment;

    public void sendSMS(String to, String message, String refType, String refId) throws UnirestException {

        String[] numbers = to.split(",");
        for(String number: numbers){
            if(sessionManager.getOperatorId() == null) {
                return;
            }
            OperatorAccount operatorAccount = operatorAccountDAO.findById(sessionManager.getOperatorId()).get();
            if(operatorAccount.getSmsSenderName() == null) {
                logger.error("SMS sender name is missing");
                throw new IllegalArgumentException("SMS sender name is missing in Operator Account");
            }
            //http://alerts.skycel.in/api/v4/?api_key=Ae6794a10ade2e286bd3
            String baseUrl = String.format("%s?api_key=%s",systemProperties.getProperty(SystemProperties.SysProps.SMS_GATEWAY_URL),
                    systemProperties.getProperty(SystemProperties.SysProps.SMS_GATEWAY_API_KEY));
            String sendMessage = String.format("%s&method=sms&message=%s&to=%s&sender=%s",baseUrl, URLEncoder.encode(message), number,operatorAccount.getSmsSenderName());
            List<String> profiles = Arrays.asList(this.environment.getActiveProfiles());
            JSONObject status = new JSONObject();
            if(!profiles.contains("test")) {
                HttpResponse<String> response = Unirest.post(sendMessage).asString();
                status.put("status", response.getStatus());
                status.put("body", response.getBody());
                status.put("statusText", response.getStatusText());
            } else {
                logger.info("ignoring SMS notification");
                return;
            }
                SMSNotification smsNotification = new SMSNotification();
                smsNotification.setNumber(number);
                smsNotification.setMessage(message);
                smsNotification.setStatus(status);
                smsNotification.setRefId(refId);
                smsNotification.setRefType(refType);
                smsNotificationDAO.save(smsNotification);
                logger.info("sent SMS {}, result{}", sendMessage, smsNotification);
        }

    }

}
