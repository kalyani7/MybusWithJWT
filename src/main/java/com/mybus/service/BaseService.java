package com.mybus.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Agent;
import com.mybus.model.Booking;
import com.mybus.model.OperatorAccount;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.net.URL;

@Service
@SessionScope
public class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    public static XmlRpcClient xmlRpcClient;

    public void initAbhibus(OperatorAccount operatorAccount) {
        if(operatorAccount.getApiURL() == null){
            new RuntimeException("Invalid API URL");
        }
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(operatorAccount.getApiURL()));
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
            xmlRpcClient.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String loginBitlaBus(OperatorAccount operatorAccount) throws UnirestException {

        /*HttpResponse<JsonNode> postResponse = Unirest.post("http://jagan.jagantravels.com/api/login.json").field("login","jagan.srini")
                .field("password","1234qwer").asJson(); */
        HttpResponse<JsonNode> postResponse = Unirest.post(operatorAccount.getApiURL()+"/api/login.json").field("login",operatorAccount.getUserName())
                .field("password",operatorAccount.getPassword()).asJson();
        String key = postResponse.getBody().getObject().getString("key");
        if(key == null){
            throw new BadRequestException("Login failed");
        }
        return key;
    }


    /**
     * Adjust the net income of booking based on agent commission
     * @param booking
     * @param bookingAgent
     */

    private void adjustAgentBookingCommission(Booking booking, Agent bookingAgent) {
        if(bookingAgent != null) {
            if(bookingAgent.getCommission() > 0) {
                double netShare = (double)(100 - bookingAgent.getCommission()) / 100;
                booking.setNetAmt(booking.getNetAmt() * netShare);
            }
        }
    }
}
