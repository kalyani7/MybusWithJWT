package com.mybus.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.exception.BadRequestException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleSQLException(HttpServletRequest request, Exception ex){
        logger.info("Badrequest exception Occured:: URL= "+request.getRequestURL());
        return new ResponseEntity<Object>(ex.getMessage() , HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleAppException(HttpServletResponse resp, Exception ex) throws IOException {
        //LOG.error("EXCEPTION", mpe);
        resp.setStatus(500); // for now treating all membership profile exceptions the same
        resp.setContentType("application/json");
        Map<String, Object> error = new HashMap<>();
        error.put("code", ex.getMessage());
        String exceptionJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(error);
        resp.getWriter().println(exceptionJson);
    }

    private JSONObject getJsonObject(Exception ex) {
        JSONObject error = new JSONObject();
        error.put("message", ex.getMessage());
        return error;
    }
}
