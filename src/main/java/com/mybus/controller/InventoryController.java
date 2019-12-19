package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.Inventory;
import com.mybus.service.InventoryManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/inventory/")

public class InventoryController {

    @Autowired
    private InventoryManager inventoryManager;


    // add inventory
    @RequestMapping(value = "addInventory", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add inventory")
    public Inventory addInventory(HttpServletRequest request,
                                  @ApiParam(value = "JSON for Inventory to be created") @RequestBody final Inventory inventory){
        return inventoryManager.addInventory(inventory);
    }
    //to get all inventories
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getAllInventories", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value ="Get the inventories ", response = JSONObject.class)
    public Page<Inventory> getAllInventories(HttpServletRequest request,
                                             @RequestParam(required = false, value = "query") String query,
                                             final Pageable pageable) {
        return inventoryManager.getAllInventories(query, pageable);
    }
    //get an inventory
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    @ApiOperation(value ="get inventory by id")
    public Inventory getById(HttpServletRequest request,
                             @ApiParam(value = "Id of the inventory to get") @PathVariable final String id) {
        return inventoryManager.getAnInventory(id);
    }
    //update an inventory
    @RequestMapping(value = "updateInventory", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update inventory")
    public Inventory updateInventory(HttpServletRequest request,
                                     @ApiParam(value = "JSON for Inventory to be updated") @RequestBody final Inventory inventory){
        return inventoryManager.updateInventory(inventory);
    }
    //delete an inventory
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value ="Delete inventory")
    public boolean deleteInventory(HttpServletRequest request,
                                   @ApiParam(value = "Id of the inventory to be deleted") @PathVariable final String id) {
       return inventoryManager.delete(id);
    }
    //count
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "getCount", method = RequestMethod.GET)
    @ApiOperation(value = "Get Count")
    public long getCount(HttpServletRequest request, @RequestParam(required = false, value = "query") String query) {
        return inventoryManager.count(query);
    }



}