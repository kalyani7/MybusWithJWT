package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.BranchOffice;
import com.mybus.model.City;
import com.mybus.model.User;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by srinikandula on 12/12/16.
 */
@Service
public class BranchOfficeManager {
    private static final Logger logger = LoggerFactory.getLogger(BranchOfficeManager.class);
    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private SessionManager sessionManager;


    @Autowired
    private MongoQueryDAO mongoQueryDAO;
    public BranchOffice save(BranchOffice branchOffice) {
        branchOffice.setOperatorId(sessionManager.getOperatorId());
        List<String> errors = RequiredFieldValidator.validateModel(branchOffice, BranchOffice.class);
        if(errors.isEmpty()) {
            return branchOfficeDAO.save(branchOffice);
        } else {
            throw new BadRequestException("Required data missing ", String.join(",", errors));
        }
    }
    public BranchOffice findOne(String branchOfficeId) {
        Preconditions.checkNotNull(branchOfficeId, "branchOfficeId is required");
        BranchOffice branchOffice = branchOfficeDAO.findByIdAndOperatorId(branchOfficeId, sessionManager.getOperatorId());
        Preconditions.checkNotNull(branchOffice, "No BranchOffice found with id");
        if(branchOffice.getCityId() != null) {
            City city = cityManager.findOne(branchOffice.getCityId());
            if(city != null) {
                branchOffice.getAttributes().put(BranchOffice.CITY_NAME, city.getName());
            }
        }
        if(branchOffice.getManagerId() != null) {
            User user = userManager.findOne(branchOffice.getManagerId());
            if(user != null) {
                branchOffice.getAttributes().put(BranchOffice.MANAGER_NAME, user.getFullName());
            }
        }
        return branchOffice;
    }

    public BranchOffice update(String branchOfficeId, BranchOffice branchOffice) {
        Preconditions.checkNotNull(branchOfficeId, "branchOfficeId can not be null");
        BranchOffice branchOfficeCopy = branchOfficeDAO.findById(branchOfficeId).get();
        Preconditions.checkNotNull(branchOfficeCopy, "No branchOffice found with id");
        try {
            branchOfficeCopy.merge(branchOffice, false);
        } catch (Exception e) {
            throw new BadRequestException("Error updating branchOffice");
        }
        return branchOfficeDAO.save(branchOfficeCopy);
    }

    public Page<BranchOffice> find(JSONObject query, final Pageable pageable) {
        if(logger.isDebugEnabled()) {
            logger.debug("Looking up shipments with {0}", query);
        }
        if(query == null) {
            query = new JSONObject();
        }
        List<BranchOffice> branchOffices = IteratorUtils.toList(mongoQueryDAO.
                getDocuments(BranchOffice.class, BranchOffice.COLLECTION_NAME, null, query, pageable).iterator());
        Map<String, String> cityNames = cityManager.getCityNamesMap();
        Map<String, String> userNames = userManager.getUserNames(false);
        branchOffices.parallelStream().forEach(office -> {
            office.getAttributes().put(BranchOffice.CITY_NAME, cityNames.get(office.getCityId()));
            office.getAttributes().put(BranchOffice.MANAGER_NAME, userNames.get(office.getManagerId()));
        });
        Page<BranchOffice> page = new PageImpl<>(branchOffices);
        return page;
    }

    public long count(JSONObject query) {
        if(query == null) {
            query = new JSONObject();
        }
        return mongoQueryDAO.count(BranchOffice.class, BranchOffice.COLLECTION_NAME, null, query);
    }
    public void delete(String branchOfficeId) {
        Preconditions.checkNotNull(branchOfficeId, "branchOfficeId can not be null");
        BranchOffice branchOffice = branchOfficeDAO.findById(branchOfficeId).get();
        Preconditions.checkNotNull(branchOffice, "No branchOffice found with id");
        branchOfficeDAO.delete(branchOffice);
    }

    /**
     * Get the list of branch office names( by operatorId is always implicit)
     * @return
     */
    public List<BranchOffice> getNames() {
        String[] fields = {"name"};
        JSONObject query = new JSONObject();
        List<BranchOffice> offices = IteratorUtils.toList(mongoQueryDAO
                .getDocuments(BranchOffice.class, BranchOffice.COLLECTION_NAME, fields, query, null).iterator());
        offices.sort(new BranchOfficeComparator());
        return offices;
    }

    /**
     * Get names Map
     * @return
     */
    public Map<String, String> getNamesMap() {
        List<BranchOffice> offices = getNames();
        return offices.stream().collect(
                Collectors.toMap(BranchOffice::getId, BranchOffice::getName));
    }

    public Map<String, BranchOffice> getAllMap(){
        List<BranchOffice> allOffices = branchOfficeDAO.findByOperatorId(sessionManager.getOperatorId());
        Map<String, BranchOffice> officesMap = new HashMap<>();
        allOffices.stream().forEach(o -> {
            officesMap.put(o.getId(), o);
        });
        return officesMap;
    }
    class BranchOfficeComparator implements Comparator<BranchOffice> {
        @Override
        public int compare(BranchOffice o1, BranchOffice o2) {
            if(o1.getName() == null && o2.getName() != null) {
                return 1;
            }
            if(o1.getName() != null && o2.getName() == null) {
                return -1;
            }
            if(o1.getName() == null && o2.getName() == null) {
                return 0;
            }
            return o1.getName().compareTo(o2.getName());
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

}
