package com.mybus.service;

import com.mybus.model.ServiceReport;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by srinikandula on 8/30/16.
 */
@Service
public class ServiceReportTestService {
    public static ServiceReport createNew(){
        ServiceReport serviceReport = new ServiceReport();
        serviceReport.setVehicleId("1234");
        Set<String> staffIds = new HashSet<>();
        staffIds.add("1234");
        serviceReport.setStaff(staffIds);
        return serviceReport;
    }

}
