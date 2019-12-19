package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.InventoryDAO;
import com.mybus.dao.JobDAO;
import com.mybus.dao.OperatorAccountDAO;
import com.mybus.dao.SupplierDAO;
import com.mybus.dto.InventoryDTO;
import com.mybus.model.Inventory;
import com.mybus.model.Job;
import com.mybus.model.OperatorAccount;
import com.mybus.model.Supplier;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class InventoryManagerTest{
    @Autowired
    private InventoryManager inventoryManager;
    @Autowired
    private InventoryDAO inventoryDAO;

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private SupplierDAO supplierDAO;
    @Autowired
    private JobDAO jobDAO;

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    @Autowired
    private JobManager jobManager;

    @Before
    @After
    public void cleanup(){
        inventoryDAO.deleteAll();
        jobDAO.deleteAll();
        supplierDAO.deleteAll();
        operatorAccountDAO.deleteAll();
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount = operatorAccountDAO.save(operatorAccount);
        sessionManager.setOperatorId(operatorAccount.getId());
    }

    @Test
    public void testAddInventory(){
        Inventory inventory = new Inventory();
        inventory.setName("Oil");
        inventory.setUniqueId("121Jan");
        inventory.setPaid(true);
        inventory.setSupplierType("others");
        inventory.setOthersSupplierName("HP bachupally");
        inventory = inventoryManager.addInventory(inventory);
        assertEquals("Oil",inventory.getName());
        //with out others
        Supplier supplier = new Supplier();
        supplier.setName("Volvo 123");
        supplier.setOperatorId(sessionManager.getOperatorId());
        supplier = supplierDAO.save(supplier);
        Inventory inventory1 = new Inventory();
        inventory1.setName("tyres");
        inventory1.setUniqueId("feb-111");
        inventory1.setPaid(true);
        inventory1.setSupplierType(supplier.getId());
        inventory1 = inventoryManager.addInventory(inventory1);
        assertEquals("tyres",inventory1.getName());
    }

    @Test
    public void testGetAllInventories(){
        for(int i=0;i<2;i++){
            Inventory inventory = new Inventory();
            inventory.setName("diesel");
            inventory.setUniqueId(Integer.toString(i));
            inventoryDAO.save(inventory);
        }
        PageRequest page = new PageRequest(0, 20);
        Iterable<Inventory> inventories = inventoryManager.getAllInventories("",page);
        List it = IteratorUtils.toList(inventories.iterator());//converting iterator to array list
        assertEquals(0,it.size());
    }

    @Test
    public void testGetAnInventory(){
        Inventory inventory = new Inventory();
        inventory.setName("diesel");
        inventory.setUniqueId("121");
        inventory = inventoryDAO.save(inventory);
        Inventory obj = inventoryManager.findById(inventory.getId());
        assertEquals("121",obj.getUniqueId());
    }

    @Test
    public void testUpdate(){
        Supplier supplier = new Supplier();
        supplier.setName("Volvo 990");
        supplier.setOperatorId(sessionManager.getOperatorId());
        supplier = supplierDAO.save(supplier);
        Inventory inventory = new Inventory();
        inventory.setName("Diesel");
        inventory.setUniqueId("143");
        inventory.setSupplierType(supplier.getId());
        inventory = inventoryDAO.save(inventory);
        inventory.setSupplierType("others");
        inventory.setOthersSupplierName("kalyani-ongole");
        Inventory update = inventoryManager.updateInventory(inventory);
        assertEquals("others",update.getSupplierType());

        Inventory inventory1 = new Inventory();
        inventory1.setName("oil");
        inventory1.setUniqueId("888");
        inventory1.setSupplierType("123456");
        inventory1 = inventoryDAO.save(inventory1);
        inventory1.setName("oil-121");
        Inventory update1 = inventoryManager.updateInventory(inventory1);
        assertEquals("oil-121",update1.getName());
    }

    @Test
    public void testDelete(){
        Inventory inventory = new Inventory();
        inventory.setName("Diesel");
        inventory.setUniqueId("143");
        inventory = inventoryManager.addInventory(inventory);
        inventoryDAO.deleteById(inventory.getId());
        Optional obj = inventoryDAO.findById(inventory.getId());
        assertFalse(obj.isPresent());
    }
    /*
    test method for supplier name in inventory
     */
    @Test
    public void testInventory(){
        Supplier supplier = new Supplier();
        supplier.setName("Volvo 123");
        supplier.setOperatorId(sessionManager.getOperatorId());
        supplier = supplierDAO.save(supplier);
        Inventory inventory = new Inventory();
        inventory.setName("Diesel");
        inventory.setUniqueId("143");
        inventory.setOperatorId(sessionManager.getOperatorId());
        inventory.setSupplierId(supplier.getId());
        inventoryDAO.save(inventory);
        PageRequest page = new PageRequest(0, 30);
        Page<Inventory> inventories = inventoryManager.getAllInventories(null, page);
        assertEquals(1, inventories.getTotalElements());
        assertEquals(supplier.getName(), inventories.iterator().next().getAttributes().get("supplierName"));
    }

    @Test
    public void testWithQuery(){
        Supplier supplier = new Supplier();
        supplier.setName("Volvo");
        supplier = supplierDAO.save(supplier);
        Inventory i1 = new Inventory();
        i1.setName("Diesel");
        i1.setSupplierId(supplier.getId());
        i1.setOperatorId(sessionManager.getOperatorId());
        i1.setUniqueId("121-Diesel");
        inventoryDAO.save(i1);
        Inventory i2 = new Inventory();
        i2.setName("diesel");
        i2.setSupplierId(supplier.getId());
        i2.setOperatorId(sessionManager.getOperatorId());
        i2.setUniqueId("444-diesel");
        inventoryDAO.save(i2);
        Inventory i3 = new Inventory();
        i3.setName("oil");
        i3.setSupplierId(supplier.getId());
        i3.setOperatorId(sessionManager.getOperatorId());
        i3.setUniqueId("333-oil");
        inventoryDAO.save(i3);
        Inventory i4 = new Inventory();
        i4.setName("tyre");
        i4.setUniqueId("121-tyre");
        i4.setSupplierId(supplier.getId());
        i4.setOperatorId(sessionManager.getOperatorId());
        inventoryDAO.save(i4);
        PageRequest page = new PageRequest(0, 20);
        Page<Inventory> inventories = inventoryManager.getAllInventories("d", page);
        assertEquals(2,inventories.getTotalElements());
        inventories = inventoryManager.getAllInventories("121", page);
        assertEquals(2,inventories.getTotalElements());
        inventories = inventoryManager.getAllInventories("", page);
        assertEquals(4,inventories.getTotalElements());
    }
    @Test
    public void testUpdateRemainingCount(){
        Job job = new Job();
        job.setVehicleId("123");
        Inventory inventory = new Inventory();
        inventory.setName("Oil");
        inventory.setUniqueId("121");
        inventory.setQuantity(10);
        inventory.setRemainingQuantity(10);
        inventory = inventoryDAO.save(inventory);
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();
        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(inventory.getId());
        dto.setQuantity(3);
        inventoryDTOS.add(dto);
        job.setInventories(inventoryDTOS);
        job = jobManager.addJob(job);
        Inventory updatedInventory = inventoryDAO.findById(inventory.getId()).get();
        assertEquals(10,updatedInventory.getRemainingQuantity());
        job.setJobCompleted(true);
        jobManager.updateJob(job);
        updatedInventory = inventoryDAO.findById(inventory.getId()).get();
        assertEquals(7,updatedInventory.getRemainingQuantity());

    }
}