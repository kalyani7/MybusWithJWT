package com.mybus.service;

import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.exception.BadRequestException;
import com.mybus.model.OperatorAccount;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OperatorAccountManager {

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    /**
     * Check if the account name or the domain name are duplicate
     * @param operatorAccount
     * @return
     */
    public OperatorAccount saveAccount(OperatorAccount operatorAccount){
       List<String> errors = RequiredFieldValidator.validateModel(operatorAccount, OperatorAccount.class);
       OperatorAccount duplicateAccount = operatorAccountDAO.findByName(operatorAccount.getName());
       if(duplicateAccount != null){
           if(!duplicateAccount.getId().equalsIgnoreCase(operatorAccount.getId())){
               errors.add("Duplicate operator name is used:" + operatorAccount.getName());
           }
       }

       duplicateAccount = operatorAccountDAO.findByDomainName(operatorAccount.getDomainName());
       if(duplicateAccount != null){
           if(!duplicateAccount.getId().equalsIgnoreCase(operatorAccount.getId())){
               errors.add("Duplicate domain name is used:" + operatorAccount.getDomainName());
           }
       }
       if(errors.isEmpty()) {
           return operatorAccountDAO.save(operatorAccount);
       } else {
           throw new BadRequestException("Required data missing : "+  StringUtils.join(errors.toArray()));
       }
   }

   public OperatorAccount findOne(String id){
        if(id == null){
            throw new BadRequestException("Invalid ID used for search");
        }
        return operatorAccountDAO.findById(id).get();
   }

   public void deleteAccount(String id){
       if(id == null){
           throw new BadRequestException("Invalid ID used for delete");
       }
       operatorAccountDAO.deleteById(id);
   }

   public Iterable<OperatorAccount> findAccounts(){
        return operatorAccountDAO.findAll();
   }

    public OperatorAccount findByServerName(String serverName) {
        return operatorAccountDAO.findByDomainName(serverName);
    }
}