package com.mybus.exception;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionController {

	private JSONObject getJsonObject(Exception ex) {
		JSONObject error = new JSONObject();
		error.put("message", ex.getMessage());
		return error;
	}

	@ExceptionHandler(Exception.class)
	public void handleAllException(HttpServletResponse resp, Exception ex) throws IOException {
		ex.printStackTrace();
		//LOG.error("EXCEPTION", mpe);
		resp.setStatus(500); // for now treating all membership profile exceptions the same
		resp.setContentType("application/json");
		Map<String, Object> error = new HashMap<>();
		error.put("code", ex.getMessage());
		resp.getWriter().println( getJsonObject(ex).toJSONString());
	}

}