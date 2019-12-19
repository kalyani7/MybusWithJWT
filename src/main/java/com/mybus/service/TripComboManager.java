package com.mybus.service;

import com.mybus.dao.TripComboDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.TripCombo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by srinikandula on 3/10/17.
 */
@Service
public class TripComboManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TripComboManager.class);

    @Autowired
    private TripComboDAO tripComboDAO;


    public TripCombo update(TripCombo tripCombo) {
        if(tripCombo.getId() == null) {
            throw new BadRequestException("ComboId can not be null");
        }
        TripCombo savedCombo = tripComboDAO.findById(tripCombo.getId()).get();
        try {
            savedCombo.merge(tripCombo);
            return tripComboDAO.save(savedCombo);
        } catch (Exception e) {
            LOGGER.error("Error updating the tripcombo ", e);
            throw new RuntimeException(e);
        }
    }
}
