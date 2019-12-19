package com.mybus.controller;

import com.mybus.dao.impl.MongoQueryDAO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by skandula on 2/13/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value = "MongoQueryController", description = "Generic Query APIs")
public class MongoQueryController extends MyBusBaseController {

    @Autowired
    private MongoQueryDAO mongoQueryDAO;
    /*
    @RequestMapping(value = "documents/{collectionName}", method = RequestMethod.GET,
            produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get documents from a colletion with specific fields queried.",
            response = Layout.class, responseContainer = "List")
    public Iterable<BasicDBObject> getDocuments(HttpServletRequest request,
                       @ApiParam(value = "Name of the collection") @PathVariable final String collectionName,
                       @ApiParam(value = "Names of the fields to query")@RequestParam(value = "fields", required = false) String fields,
                       @ApiParam(value = "Query") @RequestParam(value = "query", required = false) JSONObject query){
        String[] f = null;
        if(fields != null) {
            f = fields.split(",");
        }
        return mongoQueryDAO.getDocuments(collectionName, f, query, null);
    }
*/
}
