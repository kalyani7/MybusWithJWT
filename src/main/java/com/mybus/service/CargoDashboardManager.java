package com.mybus.service;

import com.mybus.model.BranchOffice;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoDashboardManager {

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private UserManager userManager;

    public JSONObject getDashboardContent() {
        List<BranchOffice> branchOffices = branchOfficeManager.getNames();
        return null;
    }
}
