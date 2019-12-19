package com.mybus.controller;

import com.mybus.dao.BranchOfficeDAO;
import com.mybus.dao.CashTransferDAO;
import com.mybus.dao.PaymentDAO;
import com.mybus.dao.impl.CashTransferMongoDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by srinikandula on 3/22/17.
 */
public class CashTransferControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private CashTransferDAO cashTransferDAO;

    @Autowired
    private CashTransferMongoDAO cashTransferMongoDAO;

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private BranchOfficeDAO branchOfficeDAO;

    @Before
    @After
    public void cleanup() {
        super.setup();
        cashTransferDAO.deleteAll();
    }

    @Test
    public void testGet() throws Exception {

    }

    public void testGetCount() throws Exception {

    }

    public void testCreate() throws Exception {

    }

    public void testUpdate() throws Exception {

    }

    public void testDelete() throws Exception {

    }
}