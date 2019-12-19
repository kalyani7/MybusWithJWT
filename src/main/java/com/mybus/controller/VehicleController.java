package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.User;
import com.mybus.model.Vehicle;
import com.mybus.service.VehicleManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/")
public class VehicleController extends MyBusBaseController {
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleManager vehicleManager;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicles", method = RequestMethod.POST)
    @ApiOperation(value = "Get all the vehicles available")
    public Page<Vehicle> getVehicles(HttpServletRequest request, @ApiParam(value = "query JSON") @RequestBody(required = false) final JSONObject query) {
        logger.info("geting all vehicles...");
        Page<Vehicle> vs= vehicleManager.findAll(query);
        return  vs;
    }
    
    @RequestMapping(value = "vehicle/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the Vehicle JSON", response = Vehicle.class)
    public Vehicle getVehicle(HttpServletRequest request,
                              @ApiParam(value = "Id of the Vehicle to be found") @PathVariable final String id) {
        logger.debug("get vehicle called");
        return vehicleManager.getVehicle(id);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new vehicle")
    public ResponseEntity createVehicle(HttpServletRequest request,
                                        @ApiParam(value = "JSON for Vehicle to be created") @RequestBody final Vehicle vehicle){
        logger.debug("create vehicle called");
        return new ResponseEntity<>(vehicleManager.saveVehicle(vehicle), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle/{id}", method = RequestMethod.PUT)
    @ApiOperation(value ="Update vehicle", response = User.class)
    public ResponseEntity updateVehicle(HttpServletRequest request,
                                        @ApiParam(value = "Id of the vehicle to be found") @PathVariable final String id,
                                        @ApiParam(value = "Vehicle JSON") @RequestBody final Vehicle vehicle) {
        logger.debug("update vehicle called");
        return new ResponseEntity<>(vehicleManager.updateVehicle(vehicle), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value ="Delete a vehicle")
    public JSONObject deleteVehicle(HttpServletRequest request,
                                    @ApiParam(value = "Id of the vehicle to be deleted") @PathVariable final String id) {
        logger.debug("delete vehicle called");
        JSONObject response = new JSONObject();
        response.put("deleted", vehicleManager.deleteVehicle(id));
        return response;
    }

    @RequestMapping(value = "vehicle/count", method = RequestMethod.POST)
    @ApiOperation(value ="Get vehicle count", response = Long.class)
    public long getCount(HttpServletRequest request, @ApiParam(value = "query JSON") @RequestBody final JSONObject query) {
        return vehicleManager.count(query);
    }


    @RequestMapping(value = "vehicles/expiring", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get expiring vehicles", response = Long.class)
    public Map<String, List<Vehicle>> findExpiring(HttpServletRequest request){
        return vehicleManager.findExpiring();
    }

    @RequestMapping(value = "rcUpload/{vehicleId}", method = RequestMethod.POST)
    public void rcUpload(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable(name = "vehicleId") String vehicleId){
        vehicleManager.uploadRCCopy(request, vehicleId);
    }

    @RequestMapping(value = "fcUpload/{vehicleId}", method = RequestMethod.POST)
    public void fcUpload(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable(name = "vehicleId") String vehicleId){
        vehicleManager.uploadFCCopy(request, vehicleId);
    }

    @RequestMapping(value = "permitUpload/{vehicleId}", method = RequestMethod.POST)
    public void permitUpload(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable(name = "vehicleId") String vehicleId){
        vehicleManager.uploadPermitCopy(request, vehicleId);
    }

    @RequestMapping(value = "authUpload/{vehicleId}", method = RequestMethod.POST)
    public void authUpload(HttpServletRequest request, HttpServletResponse response,
                           @PathVariable(name = "vehicleId") String vehicleId){
        vehicleManager.uploadAuthCopy(request, vehicleId);
    }

    @RequestMapping(value = "insuranceUpload/{vehicleId}", method = RequestMethod.POST)
    public void insuranceUpload(HttpServletRequest request, HttpServletResponse response,
                                @PathVariable(name = "vehicleId") String vehicleId){
        vehicleManager.uploadInsuranceCopy(request, vehicleId);
    }

    @RequestMapping(value = "pollutionUpload/{vehicleId}", method = RequestMethod.POST)
    public void pollutionUpload(HttpServletRequest request, HttpServletResponse response,
                                @PathVariable(name = "vehicleId") String vehicleId){
        vehicleManager.uploadPollutionCopy(request, vehicleId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "vehicle/removeFile/{id}", method = RequestMethod.PUT)
    @ApiOperation(value ="remove file", response = User.class)
    public ResponseEntity removeFile(HttpServletRequest request,
                                     @ApiParam(value = "Id of the vehicle") @PathVariable final String id,
                                     @ApiParam(value = "Vehicle JSON") @RequestBody final JSONObject data) {
        return new ResponseEntity<>(vehicleManager.remove(data,id), HttpStatus.OK);
    }


}