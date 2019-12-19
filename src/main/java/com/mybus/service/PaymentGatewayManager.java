package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.PaymentGatewayDAO;
import com.mybus.dao.impl.PaymentGatewayMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by HARIPRASADREDDYGURAM on 5/8/2016.
 */
@Service
public class PaymentGatewayManager {
    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayManager.class);

    @Autowired
    private PaymentGatewayDAO payGWDAO;

    @Autowired
    private PaymentGatewayMongoDAO payGWMonDAO;
    public PaymentGateway savePaymentGateway(PaymentGateway payGW){
        //Preconditions.checkNotNull(payGW, "The city can not be null");
        Preconditions.checkNotNull(payGW.getName(), "The payment gateway name can not be null");
       // Preconditions.checkNotNull(payGW.getPgAccountID(), "The city State can not be null");
        List<PaymentGateway> matchingPG = payGWDAO.findOneByName(payGW.getName());
        if(matchingPG != null && payGW.getId() != null && !payGW.getId().equals(matchingPG.get(0).getId())) {
            throw new BadRequestException("A payment gateway already exists with same name");
        }
        return payGWDAO.save(payGW);

    }

    public PaymentGateway getPaymentGateWayByName(String name){
        //Preconditions.checkNotNull(payGW, "The city can not be null");
        Preconditions.checkNotNull(name, "The payment gateway name can not be null");
        // Preconditions.checkNotNull(payGW.getPgAccountID(), "The city State can not be null");
        PaymentGateway paymentGatewayInfo = payGWDAO.findByName(name);

        return paymentGatewayInfo;

    }

    public PaymentGateway getPaymentGateWayById(String Id){
        //Preconditions.checkNotNull(payGW, "The city can not be null");
        Preconditions.checkNotNull(Id, "The payment gateway id can not be null");
        // Preconditions.checkNotNull(payGW.getPgAccountID(), "The city State can not be null");
        Optional<PaymentGateway> paymentGatewayInfo = payGWDAO.findById(Id);
        if(paymentGatewayInfo.isPresent()){
            return paymentGatewayInfo.get();
        }
        return null;
    }

    public Iterable<PaymentGateway>  getAllPaymentGateways() {
            return     payGWDAO.findAll();
    }

    public boolean updatePaymentGateWay(PaymentGateway payGW) {
        Preconditions.checkNotNull(payGW.getName(), "The payment gateway name can not be null");
        PaymentGateway matchingPG = payGWDAO.findByName(payGW.getName());
        if(matchingPG != null && !payGW.getId().equals(matchingPG.getId())) {
            throw new BadRequestException("A payment gateway already exists with same name");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Updating city:[{}]" + payGW);
        }
        return payGWMonDAO.updatePaymentGateWay(payGW);
    }
    public boolean deletePaymentGateway(String id) {
        Preconditions.checkNotNull(id, "The paymentGateway id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting paymentGateway:[{}]" + id);
        }
        if (payGWDAO.findById(id).isPresent()) {
            payGWDAO.deleteById(id);
        } else {
            throw new RuntimeException("Unknown city id");
        }
        return true;
    }

}
