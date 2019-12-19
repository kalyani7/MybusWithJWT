package com.mybus.controller;

import com.mybus.dao.CityDAO;
import com.mybus.dao.UserDAO;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.model.User;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by skandula on 12/9/15.
 */
public class CityControllerTest extends AbstractControllerIntegrationTest{

    private MockMvc mockMvc;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private UserDAO userDAO;

    private User currentUser;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        super.setup();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(getWac()).build();
        currentUser = new User("test", "test", "test", "test", true, true);
        cleanup();
        currentUser = userDAO.save(currentUser);
    }

    private void cleanup() {
        cityDAO.deleteAll();
        userDAO.deleteAll();
    }
    @After
    public void teardown() {
        cleanup();
    }

    @Test
    public void testGetActiveCityNames() throws Exception {
    	String[] ids = {"123", "234", "345", "456"};
        for(int i=1; i<= 4; i++ ) {
        	City city = new City("Name"+i, "state", i%2==0, new ArrayList<BoardingPoint>());
        	city.setId(ids[i-1]);
            cityDAO.save(city);
        }
        
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/allCityNames"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(
                jsonPath("$.234").value("Name2"));
        actions.andExpect(
                jsonPath("$.456").value("Name4"));
    }


    @Test
    public void testPagination()  throws Exception{
        for(int i=1; i<= 40; i++ ) {
            City city = new City("Name"+i, "state", i%2==0, new ArrayList<BoardingPoint>());
            cityDAO.save(city);
        }
        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/cities?page=3&size=5&sort=name"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpect(jsonPath("$", Matchers.hasSize(5)));
    }
    @Test
    public void testGetAllCityNames() throws Exception {
    	String[] ids = {"123", "234", "345", "456"};
        for(int i=1; i<=4; i++ ) {
        	City city = new City("Name"+i, "state", i%2==0, new ArrayList<BoardingPoint>());
        	city.setId(ids[i-1]);
        	city.setActive(true);
            cityDAO.save(city);
        }

        ResultActions actions = mockMvc.perform(asUser(get("/api/v1/allCityNames"), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(
                jsonPath("$.123").value("Name1"));
        actions.andExpect(
                jsonPath("$.234").value("Name2"));
        actions.andExpect(
                jsonPath("$.345").value("Name3"));
        actions.andExpect(
                jsonPath("$.456").value("Name4"));
    }

    @Test
    public void testCreateCitySuccess() throws Exception {
        JSONObject city = new JSONObject();
        city.put("name", "city");
        city.put("state", "CA");
        String str = city.toJSONString();
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value("city"));
        actions.andExpect(jsonPath("$.state").value("CA"));
        actions.andExpect(jsonPath("$.bp").doesNotExist());
    }

    @Test
    public void testCreateCityDuplicateName() throws Exception {
        cityDAO.save(new City("city", "CA", true, new ArrayList<>()));
        JSONObject city = new JSONObject();
        city.put("name", "city");
        city.put("state", "CA");
        String str = city.toJSONString();
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.message").value("A city already exists with same name and state"));
    }

    @Test
    public void testCreateCityFail() throws Exception {
        JSONObject city = new JSONObject();
        city.put("name", "city");
        String str = city.toJSONString();
        ResultActions actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.message").value("The city State can not be null"));
        //send only state
        city = new JSONObject();
        city.put("state", "city");
        str = city.toJSONString();
        actions = mockMvc.perform(asUser(post("/api/v1/city")
                .content(str).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.message").value("The city name can not be null"));

    }

    @Test
    public void testDeleteCity() throws Exception {
        City city = new City("city", "CA", true, null);
        city = cityDAO.save(city);
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/city/%s", city.getId())), currentUser));
        actions.andExpect(status().isOk());
        Assert.assertFalse(cityDAO.findById(city.getId()).isPresent());
    }

    @Test
    public void testDeleteCityUnknownId() throws Exception {
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/city/%s", "123")), currentUser));
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void testAddBoardingPoint() throws Exception {
        City city = new City("city", "CA", true, null);
        city = cityDAO.save(city);
        JSONObject bp = new JSONObject();
        bp.put("name", "BPName");
        bp.put("landmark", "landmark");
        bp.put("contact", "123");
        bp.put("active", true);
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").exists());
        actions.andExpect(jsonPath("$.boardingPoints").isArray());
        actions.andExpect(jsonPath("$.boardingPoints", Matchers.hasSize(1)));
        actions.andExpect(jsonPath("$.boardingPoints[0].name").value("BPName"));
        actions.andExpect(jsonPath("$.boardingPoints[0].landmark").value("landmark"));
        actions.andExpect(jsonPath("$.boardingPoints[0].contact").value("123"));
        bp.put("name", "BPNameNew");
        actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").exists());
        actions.andExpect(jsonPath("$.boardingPoints").isArray());
        City savedCity = cityDAO.findById(city.getId()).get();
        Assert.assertEquals(2, savedCity.getBoardingPoints().size());
        for(BoardingPoint b : savedCity.getBoardingPoints()) {
            Assert.assertNotNull(b.getId());
        }
    }

    @Test
    public void testAddDroppingPoint() throws Exception {
        City city = new City("city", "CA", true, null);
        city = cityDAO.save(city);
        JSONObject bp = new JSONObject();
        bp.put("name", "BPName");
        bp.put("landmark", "landmark");
        bp.put("contact", "123");
        bp.put("active", true);
        bp.put("droppingPoint", true);

        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").exists());
        actions.andExpect(jsonPath("$.boardingPoints").isArray());
        actions.andExpect(jsonPath("$.boardingPoints", Matchers.hasSize(1)));
        actions.andExpect(jsonPath("$.boardingPoints[0].name").value("BPName"));
        actions.andExpect(jsonPath("$.boardingPoints[0].landmark").value("landmark"));
        actions.andExpect(jsonPath("$.boardingPoints[0].contact").value("123"));
        actions.andExpect(jsonPath("$.boardingPoints[0].droppingPoint").value(true));
    }

    @Test
    public void testAddBoardingPointDuplicateName() throws Exception {
        City city = new City("city", "CA", true, new ArrayList<>());
        BoardingPoint boardingPoint = new BoardingPoint("BPName", "landmark", "1234", true);
        city.getBoardingPoints().add(boardingPoint);
        city = cityDAO.save(city);

        JSONObject bp = new JSONObject();
        bp.put("name", "BPName");
        bp.put("landmark", "landmark");
        bp.put("contact", "123");
        bp.put("active", true);
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp))
                .contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
    }


    @Test
    public void testAddBoardingPointNoName() throws Exception {
        City city = new City("city", "CA", true, null);
        city = cityDAO.save(city);
        BoardingPoint bp = new BoardingPoint(null, "landmark", "123", true);
        ResultActions actions = mockMvc.perform(asUser(post(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateBoardingPoint() throws Exception {
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        bp.setName("NewName");
        ResultActions actions = mockMvc.perform(asUser(put(format("/api/v1/city/%s/boardingpoint", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(bp)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").exists());
        actions.andExpect(jsonPath("$.boardingPoints").isArray());
        actions.andExpect(jsonPath("$.boardingPoints[0].name").value(bp.getName()));
        Assert.assertNotNull(cityDAO.findById(city.getId()).get());
    }

    @Test
    public void testGetBoardingPoint() throws Exception {
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/city/%s/boardingpoint/%s", city.getId(),
                bp.getId())), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.name").value(bp.getName()));
        Assert.assertNotNull(cityDAO.findById(city.getId()).get());
        Assert.assertEquals(1, cityDAO.findById(city.getId()).get().getBoardingPoints().size());
        actions = mockMvc.perform(asUser(get(format("/api/v1/city/%s/boardingpoint/%s", city.getId(),
                "1234")), currentUser));
        actions.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testGetAllBoardingPoints() throws Exception {
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
//        bp = city.getBoardingPoints().iterator().next();
        ResultActions actions = mockMvc.perform(asUser(get(format("/api/v1/city/%s/boardingpoint/", city.getId())), currentUser));
        actions.andExpect(status().isOk());
    }

    @Test
    public void testDeleteBoardingPoint() throws Exception{
        City city = new City("TextCity", "TestState", true, new ArrayList<>());
        BoardingPoint bp = new BoardingPoint("name", "landmark", "123", true);
        city.getBoardingPoints().add(bp);
        city = cityDAO.save(city);
        bp = city.getBoardingPoints().iterator().next();
        ResultActions actions = mockMvc.perform(asUser(delete(format("/api/v1/city/%s/boardingpoint/%s", city.getId()
                , bp.getId())), currentUser));
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.boardingPoints").isEmpty());
        Assert.assertNotNull(cityDAO.findById(city.getId()).get());
    }

    @Test
    public void testUpdateCity() throws Exception{
        City city = new City("TestCity", "TestState", true, new ArrayList<>());
        city = cityDAO.save(city);
        city.setName("NewName");
        city.setActive(false);
        ResultActions actions = mockMvc.perform(asUser(put(format("/api/v1/city/%s", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(city)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isOk());
        City savedCity = cityDAO.findById(city.getId()).get();
        Assert.assertNotNull(savedCity);
        Assert.assertFalse(savedCity.isActive());
        Assert.assertEquals(city.getName(), savedCity.getName());
    }

    @Test
    public void testUpdateCityWithDuplicateName() throws Exception {
        City city = new City("TestCity", "TestState", true, new ArrayList<>());
        city = cityDAO.save(city);
        cityDAO.save(new City("NewName", "TestState", true, new ArrayList<>()));
        city.setName("NewName");
        city.setActive(false);
        ResultActions actions = mockMvc.perform(asUser(put(format("/api/v1/city/%s", city.getId()))
                .content(getObjectMapper().writeValueAsBytes(city)).contentType(MediaType.APPLICATION_JSON), currentUser));
        actions.andExpect(status().isBadRequest());
//        actions.andExpect(jsonPath("$.message").value("A city already exists with same name and state"));
    }

}