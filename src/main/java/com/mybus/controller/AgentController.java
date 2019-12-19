package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dto.AgentNameDTO;
import com.mybus.model.Agent;
import com.mybus.service.AgentManager;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1/")
public class AgentController {
	private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

	@Autowired
	private AgentManager agentManager;

	@RequestMapping(value = "agent/download", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Download the agents from Abhibus", response = JSONObject.class)
	public JSONObject getDownloadStatus(HttpServletRequest request, HttpServletResponse resp) throws Exception {
		return agentManager.downloadAgents();
	}

	@RequestMapping(value = "agent/count", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get Agents count", response = Long.class)
	public long getCount(HttpServletRequest request, @RequestParam(required = false, value = "query") String query,
                         @RequestParam(required = false, value = "showInvalid") boolean showInvalid,
                         final Pageable pageable) {
		return agentManager.count(query,showInvalid);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
					value = "Results page you want to retrieve (0..N)"),
			@ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
					value = "Number of records per page."),
			@ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
					value = "Sorting criteria in the format: property(,asc|desc). " +
							"Default sort order is ascending. " +
							"Multiple sort criteria are supported.")
	})
	@RequestMapping(value = "agents", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get the agents ", response = JSONObject.class)
	public Page<Agent> getAgents(HttpServletRequest request,
                                 @RequestParam(required = false, value = "query") String query,
                                 @RequestParam(required = false, value = "showInvalid") boolean showInvalid,
                                 final Pageable pageable) {
		return agentManager.findAgents(query, showInvalid, pageable);
	}

	@RequestMapping(value = "agentNames", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get the agents ", response = JSONObject.class)
	public Iterable<AgentNameDTO> getAgentNames(HttpServletRequest request) {
		return agentManager.getAgentNames();
	}

	@RequestMapping(value = "agent/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
	@ApiOperation(value ="Get agent ", response = JSONObject.class)
	public Agent getAgent(HttpServletRequest request,
                          @ApiParam(value = "Id of the agent to be found") @PathVariable final String id) {
		return agentManager.getAgent(id);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "agent/update", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save agent")
	public Agent update(HttpServletRequest request,
                        @ApiParam(value = "JSON for Agent to be updated") @RequestBody final Agent agent) throws Exception {
		logger.debug("update agent called");
		return agentManager.update(agent);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "agent/updateBranchOffice", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save agent")
	public void updateBranchOffice(HttpServletRequest request,
                                   @ApiParam(value = "JSON for Agent to be updated") @RequestBody final Agent agent) {
		logger.debug("update agent called");
		agentManager.updateBranchOffice(agent);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "agent/addAgent", method = RequestMethod.POST, produces = ControllerUtils.JSON_UTF8,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create agent")
	public Agent create (HttpServletRequest request,
                         @ApiParam(value = "JSON for Agent to be created") @RequestBody final Agent agent) throws Exception {
		logger.debug("add agent called");
		return agentManager.save(agent);
	}


}
