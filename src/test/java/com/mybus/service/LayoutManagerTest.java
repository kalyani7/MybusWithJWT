package com.mybus.service;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.LayoutDAO;
import com.mybus.model.Layout;
import com.mybus.model.LayoutType;
import com.mybus.model.Row;
import org.json.simple.JSONObject;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by skandula on 5/2/16.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class LayoutManagerTest {
    @Autowired
    private LayoutDAO layoutDAO;
    @Autowired
    private LayoutManager layoutManager;

    @Before
    @After
    public void cleanup(){
        layoutDAO.deleteAll();
    }


    @Test
    public void testDeleteLayout() throws Exception {

    }

    @Test
    public void testUpdateLayout() throws Exception {

    }

    @Test
    public void testSaveLayout() throws Exception {

    }

    @Test
    public void testGetLayoutNames() throws Exception {

    }

    @Test
    public void testGetDefaultLayout() throws Exception {

    }

    @Test
    public void testGetAllLayouts(){
        List<Row> rows  = new ArrayList<>();
        Layout l1 = new Layout();
        l1.setName("AC Semi Sleeper");
        l1.setType(LayoutType.AC_SEMI_SLEEPER);
        l1.setRows(rows);
        l1.setTotalSeats(20);
        l1.setTotalRows(10);
        l1.setSeatsPerRow(4);
        l1.setMiddleRowPosition(6);
        layoutDAO.save(l1);
        Layout l2 = new Layout();
        l2.setName("AC");
        l2.setType(LayoutType.SEMI_SLEEPER);
        l2.setRows(rows);
        l2.setTotalSeats(40);
        l2.setTotalRows(8);
        l2.setSeatsPerRow(5);
        l2.setMiddleRowPosition(4);
        layoutDAO.save(l2);
        JSONObject query = new JSONObject();
        Page<Layout> page = layoutManager.getAllLayouts(query);
        assertEquals(2,page.getTotalElements());
    }
}