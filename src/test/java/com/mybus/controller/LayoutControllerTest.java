package com.mybus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.mybus.service.LayoutManager.SEMI_SLEEPER_DEFAULT_COLUMNS;
import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by schanda on 1/17/16.
 */

public class LayoutControllerTest extends AbstractControllerIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private LayoutDAO layoutDAO;

	@Autowired
	private UserDAO userDAO;

	private User currentUser;

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void setup() {
		super.setup();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
		currentUser = new User("test", "test", "test", "test", true, true);
		cleanup();
		currentUser = userDAO.save(currentUser);
	}

	private void cleanup() {
		layoutDAO.deleteAll();
		userDAO.deleteAll();
	}

	@After
	public void teardown() {
		cleanup();
	}

	@Test
	public void testGetAllLayoutsSuccess() throws Exception {
		String id1 = "Layout001";
		String layoutName1 = "AC_SEMI_SLEEPER Layout";
		int totalSeats1 = 44;
		Layout layout1 = LayoutControllerTestUtils.constructSemiSleeperLayout(id1, layoutName1,
				LayoutType.AC_SEMI_SLEEPER, totalSeats1);
		mockMvc.perform(asUser(
				post("/api/v1/layout").content(objectMapper.writeValueAsBytes(layout1)).contentType(
						MediaType.APPLICATION_JSON), currentUser));

		String id2 = "Layout0012";
		String layoutName2 = "SEMI_SLEEPER Layout";
		int totalSeats2 = 40;
		Layout layout2 = LayoutControllerTestUtils.constructSemiSleeperLayout(id2, layoutName2,
				LayoutType.SEMI_SLEEPER, totalSeats2);
		mockMvc.perform(asUser(
				post("/api/v1/layout").content(objectMapper.writeValueAsBytes(layout2)).contentType(
						MediaType.APPLICATION_JSON), currentUser));

		ResultActions actions = mockMvc.perform(asUser(get("/api/v1/layouts").contentType(MediaType.APPLICATION_JSON),
				currentUser));
		actions.andExpect(jsonPath("$[0].active").value(true));
		actions.andExpect(jsonPath("$[0].type").value("AC_SEMI_SLEEPER"));
		actions.andExpect(jsonPath("$[0].id").value("Layout001"));
		actions.andExpect(jsonPath("$[0].name").value("AC_SEMI_SLEEPER Layout"));
		actions.andExpect(jsonPath("$[0].totalSeats").value(44));

		actions.andExpect(jsonPath("$[1].active").value(true));
		actions.andExpect(jsonPath("$[1].type").value("SEMI_SLEEPER"));
		actions.andExpect(jsonPath("$[1].id").value("Layout0012"));
		actions.andExpect(jsonPath("$[1].name").value("SEMI_SLEEPER Layout"));
		actions.andExpect(jsonPath("$[1].totalSeats").value(40));
	}

	@Test
	public void testCreateLayoutSuccess() throws Exception {
		String id1 = "Layout001";
		String layoutName1 = "AC_SEMI_SLEEPER Layout";
		int totalSeats1 = 44;
		Layout layout = LayoutControllerTestUtils.constructSemiSleeperLayout(id1, layoutName1,
				LayoutType.AC_SEMI_SLEEPER, totalSeats1);
		ResultActions actions = mockMvc.perform(asUser(
				post("/api/v1/layout").content(objectMapper.writeValueAsBytes(layout)).contentType(
						MediaType.APPLICATION_JSON), currentUser));
		actions.andExpect(status().isOk());
		LayoutControllerTestUtils.validateResult(actions);
	}

	@Test
	public void testGetLayoutSuccess() throws Exception {
		String id1 = "Layout001";
		String layoutName1 = "AC_SEMI_SLEEPER Layout";
		int totalSeats1 = 44;
		Layout layout = LayoutControllerTestUtils.constructSemiSleeperLayout(id1, layoutName1,
				LayoutType.AC_SEMI_SLEEPER, totalSeats1);
		mockMvc.perform(asUser(
				post("/api/v1/layout").content(objectMapper.writeValueAsBytes(layout)).contentType(
						MediaType.APPLICATION_JSON), currentUser));

		ResultActions actions = mockMvc.perform(asUser(get(String.format("/api/v1/layout/%s", "Layout001"))
				.contentType(MediaType.APPLICATION_JSON), currentUser));
		actions.andExpect(status().isOk());
		LayoutControllerTestUtils.validateResult(actions);
	}

	@Test
	public void testDeleteLayout() throws Exception {
		String id1 = "Layout001";
		String layoutName1 = "AC_SEMI_SLEEPER Layout";
		int totalSeats1 = 44;
		Layout layout = LayoutControllerTestUtils.constructSemiSleeperLayout(id1, layoutName1,
				LayoutType.AC_SEMI_SLEEPER, totalSeats1);

		Layout layoutSaved = layoutDAO.save(layout);

		ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/layout/%s", layoutSaved.getId())),
				currentUser));
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.deleted").value(true));
		Assert.assertFalse(layoutDAO.findById(layoutSaved.getId()).isPresent());
	}

	@Test
	public void testUpdateLayout() throws Exception {
		String id1 = "Layout001";
		String layoutName1 = "AC_SEMI_SLEEPER Layout";
		int totalSeats1 = 44;
		Layout layout = LayoutControllerTestUtils.constructSemiSleeperLayout(id1, layoutName1,
				LayoutType.AC_SEMI_SLEEPER, totalSeats1);

		Layout layout4Update = layoutDAO.save(layout);
		layout4Update.setTotalSeats(15);
		;
		layout4Update.setType(LayoutType.SEMI_SLEEPER);
		Row middleRow = layout4Update.getRows().get(2);
		Seat lastSeat = middleRow.getSeats().get(SEMI_SLEEPER_DEFAULT_COLUMNS - 1);
		lastSeat.setDisplay(false);
		lastSeat.setActive(false);

		ResultActions actions = mockMvc.perform(asUser(
				put("/api/v1/layout").content(objectMapper.writeValueAsBytes(layout4Update)).contentType(
						MediaType.APPLICATION_JSON), currentUser));
		actions.andExpect(status().isOk());

		actions.andExpect(jsonPath("$.totalSeats").value(15));
		actions.andExpect(jsonPath("$.type").value("SEMI_SLEEPER"));

		actions.andExpect(jsonPath("$.rows[2].window").value(false));
		actions.andExpect(jsonPath("$.rows[2].seats[10].display").value(false));
		actions.andExpect(jsonPath("$.rows[2].seats[10].active").value(false));
	}

	@Test
	public void testGetDefaultLayoutForSemiSleeper() throws Exception {
		ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/layout/default/%s", "AC_SEMI_SLEEPER"))
				.content("").contentType(MediaType.APPLICATION_JSON), currentUser));

		actions.andExpect(status().isOk());
		LayoutControllerTestUtils.validateResult(actions);
	}
}