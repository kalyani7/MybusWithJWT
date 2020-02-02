package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.StaffCodeSequenceDAO;
import com.mybus.dao.StaffDAO;
import com.mybus.dao.SupplierDAO;
import com.mybus.model.PartyType;
import com.mybus.model.Staff;
import com.mybus.model.StaffCodeSequence;
import com.mybus.model.Supplier;
import org.apache.commons.collections.IteratorUtils;
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

    @Autowired
    private StaffCodeSequenceDAO staffCodeSequenceDAO;

    @Before
    @After
    public void clear(){
        staffDAO.deleteAll();
        supplierDAO.deleteAll();
        staffCodeSequenceDAO.deleteAll();
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

    @Test
    public void testAddStaff(){
        Staff staff1 = new Staff();
        staff1.setName("Staff1");
        staff1.setAge(30);
        staff1 = staffManager.saveStaff(staff1);
        assertEquals("EMP-100",staff1.getUniqueId());
        Staff staff2 = new Staff();
        staff2.setName("Staff2");
        staff2.setAge(40);
        staff2 = staffManager.saveStaff(staff2);
        assertEquals("EMP-101",staff2.getUniqueId());
        List<StaffCodeSequence> staffCodeSequences = IteratorUtils.toList(staffCodeSequenceDAO.findAll().iterator());
        assertEquals(102,staffCodeSequences.get(0).getValue());
    }


}