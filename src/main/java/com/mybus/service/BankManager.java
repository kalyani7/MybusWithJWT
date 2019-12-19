package com.mybus.service;

import com.google.common.base.Strings;
import com.mybus.dao.BankDAO;
import com.mybus.dao.impl.BankMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Bank;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BankManager {

    @Autowired
    private BankDAO bankDAO;

    @Autowired
    private BankMongoDAO bankMongoDAO;

    public Bank addBankDetails(Bank bankInfo) {
        if (Strings.isNullOrEmpty(bankInfo.getAccountNumber())){
            throw new BadRequestException("Enter Account Number");
        }
        if (Strings.isNullOrEmpty(bankInfo.getAccountName())){
            throw new BadRequestException("Enter Account Name");
        }
        if (Strings.isNullOrEmpty(bankInfo.getBankName())){
            throw new BadRequestException("Enter Bank Name");
        }
        return bankDAO.save(bankInfo);
    }

    public List<Bank> getAllBankDetails() {
        return IteratorUtils.toList(bankDAO.findAll().iterator());
    }

    public Bank getBankInfoById(String bankId) {
        return bankDAO.findById(bankId).get();
    }

    public boolean updateBankInfo(Bank bankInfo, String bankId) {
        if (Strings.isNullOrEmpty(bankInfo.getAccountNumber())){
            throw new BadRequestException("Enter Account Number");
        }
        if (Strings.isNullOrEmpty(bankInfo.getAccountName())){
            throw new BadRequestException("Enter Account Name");
        }
        if (Strings.isNullOrEmpty(bankInfo.getBankName())){
            throw new BadRequestException("Enter Bank Name");
        }
        return bankMongoDAO.updateBankInfo(bankInfo,bankId);
    }

    public void deleteBankInfo(String bankId) {
        bankDAO.deleteById(bankId);
    }

    public Map<String,String> findBankNames(){
        Map<String,String> bankNamesMap = new HashMap<>();
        List<Bank> bankList = IteratorUtils.toList(bankDAO.findAll().iterator());
        bankList.stream().forEach(bank -> {
            bankNamesMap.put(bank.getId(),bank.getBankName());
        });
        return bankNamesMap;
    }
}
