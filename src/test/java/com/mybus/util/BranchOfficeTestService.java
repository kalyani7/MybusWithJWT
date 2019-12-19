package com.mybus.util;

import com.mybus.model.BranchOffice;

/**
 * Created by srinikandula on 12/12/16.
 */
public class BranchOfficeTestService {
    public static BranchOffice createNew() {
        return new BranchOffice("OfficeName","1234", "123", true, "email@e.com", 123456, "address" , 0,true);
    }
}
