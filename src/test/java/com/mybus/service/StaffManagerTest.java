package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.StaffDAO;
import com.mybus.dao.SupplierDAO;
import com.mybus.model.PartyType;
import com.mybus.model.Staff;
import com.mybus.model.Supplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class StaffManagerTest {

    @Autowired
    private StaffManager staffManager;

    @Autowired
    private StaffDAO staffDAO;

    @Autowired
    private SupplierDAO supplierDAO;

    @Autowired
    private SuppliersManager suppliersManager;

    @Before
    @After
    public void clear(){
        staffDAO.deleteAll();
        supplierDAO.deleteAll();
    }

    @Test
    public void testCount(){
        for(int i=0;i<5;i++){
            staffDAO.save(new Staff("One"+i, "9906"+i, "aadhar"+i));
        }
        long count = staffManager.count("One1", null);
        assertEquals(count, 1);
        count = staffManager.count("99061", null);
        assertEquals(count, 1);
        count = staffManager.count("aadhar1", null);
        assertEquals(count, 1);
    }

    @Test
    public void testSearch(){
        for(int i=0;i<5;i++){
            staffDAO.save(new Staff("One"+i, "9906"+i, "aadhar"+i));
        }
        Page<Staff> staff= staffManager.findStaff("One1", null);
        assertEquals(staff.getContent().size(), 1);
        staff= staffManager.findStaff(null, null);
        assertEquals(staff.getContent().size(), 5);
    }

    @Test
    public void testGetPartiesByType(){
        for(int i=0;i<5;i++){
            Supplier supplier = new Supplier();
            if(i%2 == 0){
                supplier.setPartyType(PartyType.CREDITORS);
            }else{
                supplier.setPartyType(PartyType.DEBTORS);
            }
            supplierDAO.save(supplier);
        }
        List<Supplier> suppliers = suppliersManager.getAll(PartyType.DEBTORS.toString());
        assertEquals(2,suppliers.size());
    }


}