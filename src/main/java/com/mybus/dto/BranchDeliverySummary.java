package com.mybus.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class BranchDeliverySummary {
    private String branchId;
    private String branchName;
    private Map<String, UserDeliverySummary> userDeliverySummaryList = new HashMap<>();
    private double total;

}
