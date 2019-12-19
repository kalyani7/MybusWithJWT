package com.mybus.controller;

import com.google.common.base.Preconditions;
import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.BoardingPoint;
import com.mybus.model.City;
import com.mybus.service.CityManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="CityController", description="City and Boarding points management")
public class CityController extends MyBusBaseController {
    private static final Logger logger = LoggerFactory.getLogger(CityController.class);

    @Autowired
    private CityManager cityManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "cities", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the cities available", response = City.class, responseContainer = "List")
    public List<City> getCities(HttpServletRequest request, final Pageable pageable) {
        return cityManager.findAll(pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "cities/count", method = RequestMethod.GET)
    @ApiOperation(value = "Get Count")
    public long getCount(HttpServletRequest request, final Pageable pageable) {
        return cityManager.count();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "activeCityNames", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get names of the active cities as key value pair", response = Map.class, responseContainer = "Map")
    public Iterable<City> getActiveCityNames(HttpServletRequest request) {
        return cityManager.getCityNames(true);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "allCityNames", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get names of the active cities as key value pair", response = Map.class, responseContainer = "Map")
    public Map<String, String> getAllCityNames(HttpServletRequest request) {
       return cityManager.getCityNamesMap();
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a city")
    public ResponseEntity createCity(HttpServletRequest request,
                                     @ApiParam(value = "JSON for City to be created") @RequestBody final City city){
        logger.debug("post city called");
        return new ResponseEntity<>(cityManager.saveCity(city), HttpStatus.OK);
    }

    @RequestMapping(value = "city/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the City JSON", response = City.class)
    public City getCity(HttpServletRequest request,
                        @ApiParam(value = "Id of the City to be found") @PathVariable final String id) {
        logger.debug("get city called");
        return cityManager.findOne(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{id}", method = RequestMethod.PUT)
    @ApiOperation(value ="Update city", response = City.class)
    public ResponseEntity updateCity(HttpServletRequest request,
                                     @ApiParam(value = "Id of the City to be found") @PathVariable final String id,
                                     @ApiParam(value = "City JSON") @RequestBody final City city) {
        logger.debug("get city called");
        return new ResponseEntity<>(cityManager.updateCity(city), HttpStatus.OK);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete a city")
    public JSONObject deleteCity(HttpServletRequest request,
                                 @ApiParam(value = "Id of the city to be deleted") @PathVariable final String id) {
        logger.debug("get city called");
        JSONObject response = new JSONObject();
        response.put("deleted", cityManager.deleteCity(id));
        return response;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint", method = RequestMethod.POST,
            produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value ="Create a new city boarding point", response = City.class)
    public City createCityBoardingpoint(HttpServletRequest request,
                                        @ApiParam(value = "Id of the city to which boardingpoint to be added") @PathVariable final String cityId,
                                        @ApiParam(value = "JSON for boardingpoint") @RequestBody final BoardingPoint bp) {
        logger.debug("create boardingpoint called");
        return cityManager.addBoardingPointToCity(cityId, bp);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint/{id}", method = RequestMethod.GET,
            produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get JSON for a city boardingpoint", response = BoardingPoint.class)
    public BoardingPoint getgCityBoardingpoint(HttpServletRequest request,
                                               @ApiParam(value = "cityId") @PathVariable final String cityId,
                                               @ApiParam(value = "BoardingpointId") @PathVariable final String id) {
        logger.debug("create boardingpoint called");
        BoardingPoint bp = cityManager.getBoardingPoint(cityId, id);
        Preconditions.checkNotNull(bp, "No boardingpoint found");
        return bp;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint", method = RequestMethod.PUT,
            produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value ="Update a city boarding point", response = City.class)
    public City updateCityBoardingpoint(HttpServletRequest request,
                                        @ApiParam(value = "Id of the city to which contains boardingpoint ")@PathVariable final String cityId,
                                        @ApiParam(value = "JSON for boardingpoint") @RequestBody final BoardingPoint bp) {
        logger.debug("create boardingpoint called");
        return cityManager.updateBoardingPoint(cityId, bp);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint/{id}", method = RequestMethod.DELETE,
            produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Delete a city boarding point", response = City.class)
    public City deleteCityBoardingpoint(HttpServletRequest request,
                                        @ApiParam(value = "City Id")  @PathVariable final String cityId,
                                        @ApiParam(value ="Boardingpoint Id")@PathVariable final String id) {
        logger.debug("create boardingpoint called");
        return cityManager.deleteBoardingPoint(cityId, id);
    }
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "city/{cityId}/boardingpoint/", method = RequestMethod.GET,
            produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Gets all boarding points for a given city", response = BoardingPoint.class, responseContainer= "List")
    public Iterable<BoardingPoint> getBoardingPoints(HttpServletRequest request,
                                                     @ApiParam(value = "City Id")  @PathVariable final String cityId) {
        logger.debug("create boardingpoint called");
        return cityManager.getBoardingPoints(cityId);
    }

}
