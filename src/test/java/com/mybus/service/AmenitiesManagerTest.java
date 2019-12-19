package com.mybus.service;

import com.mybus.dao.AmenityDAO;
import com.mybus.model.Amenity;
import com.mybus.util.AmenityTestService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 
 * @author yks-Srinivas
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AmenitiesManagerTest {

	@Autowired
	public AmenityDAO amenityDAO;

	@Autowired
	private AmenityTestService amenityTestService;

	@Autowired
	public AmenitiesManager amenitiesManager;

	private void cleanup() {
		amenityDAO.deleteAll();
	}

	@After
	@Before
	public void teardown() {
		cleanup();
	}

	@Test
	public void amenitTest(){
		Amenity a = amenityTestService.createTestAmenity();
		Assert.assertNotNull(a);
		Assert.assertNotNull(a.getId());
		
		Assert.assertEquals(true, a.isActive());
		Assert.assertEquals("bottle", a.getName());
		
		a.setActive(false);
		Amenity a1 = amenitiesManager.upateAmenity(a);
		
		Assert.assertEquals(a1.getId(), a.getId());
		Assert.assertEquals(false, a.isActive());
		
		Assert.assertEquals(a1, amenitiesManager.getAmenityById(a1.getId()));
	}
}
