package com.mybus.controller;

import org.json.simple.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skandula on 2/10/16.
 */

public class MyBusBaseController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleAppException(HttpServletResponse resp, Exception ex) throws IOException {
        ex.printStackTrace();
        //LOG.error("EXCEPTION", mpe);
        resp.setStatus(500); // for now treating all membership profile exceptions the same
        resp.setContentType("application/json");
        Map<String, Object> error = new HashMap<>();
        error.put("code", ex.getMessage());
        resp.getWriter().println( getJsonObject(ex).toJSONString());
    }

    private JSONObject getJsonObject(Exception ex) {
        JSONObject error = new JSONObject();
        error.put("message", ex.getMessage());
        return error;
    }
    protected Pageable getPageable(JSONObject query) {
        int page = 0;
        int count = 100;
        Pageable pageable = null;
        if(query != null) {
            if(query.containsKey("page")){
                page = Integer.parseInt(query.get("page").toString());
                page--;
            }
            if(query.containsKey("size")){
                count = Integer.parseInt(query.get("size").toString());
            }
            String sortOn = "createdAt";
            Sort.Direction sortDirection = Sort.Direction.DESC;

            if(query.containsKey("sort")){
                String[] sortParams = query.get("sort").toString().split(",");
                sortOn = sortParams[0];
                if(sortParams[1].equalsIgnoreCase("DESC")){
                    sortDirection = Sort.Direction.DESC;
                } else {
                    sortDirection = Sort.Direction.ASC;
                }
            }
            pageable = new PageRequest(page, count, sortDirection, sortOn);
        }
        return pageable;
    }
}

