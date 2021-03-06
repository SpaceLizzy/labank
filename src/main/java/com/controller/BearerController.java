package com.controller;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.exceptions.AlreadyExistsException;
import com.exceptions.InvalidJsonException;
import com.exceptions.InvalidOperationException;
import com.exceptions.NotFoundException;
import com.model.Bearer;
import com.service.BearerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Bearer")
@RequestMapping("/api/v1/bearer")
public class BearerController {

	final static Logger log = Logger.getLogger(BearerController.class);

	@Autowired
	private BearerService service;
	private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");


	@GetMapping("/allBearers")
	@ApiOperation(value = "Get all default bearers")
	@ResponseBody
	List<Bearer> getAllBearers() throws NotFoundException {
		log.info("GET /api/v1/bearer/allBearers");
		List<Bearer> reqReturn = service.getAllBearers();		
		return reqReturn;
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "Get a bearer")
	@ResponseBody
	Bearer getBearerByDocument(@PathVariable (value = "id", required = true) String document) throws NotFoundException, InvalidJsonException {
		log.info("GET /api/v1/bearer/");
		validateQueryParams(document);
		Bearer reqReturn = service.getBearerByDocument(document);
		return reqReturn;
	}

	@PostMapping("/")
	@ResponseBody
	@ApiOperation(value = "Create a bearer")
	Bearer createBearer(@RequestBody Bearer newBearer) throws InvalidOperationException, AlreadyExistsException, InvalidJsonException {
		log.info("POST /api/v1/bearer/");
		validateRequestBody(newBearer);
		Bearer reqReturn = service.createBearer(newBearer);
		return reqReturn;
	}

	@PutMapping("/{id}")
	@ResponseBody
	@ApiOperation(value = "Update a bearer")
	Bearer updateBearer(@PathVariable (value = "id", required = true) String document, @RequestBody Bearer bearer) throws NotFoundException, InvalidOperationException, InvalidJsonException {
		log.info("PUT /api/v1/bearer/");
		validateQueryParams(document);
		validateUpdateDocument(bearer, document);
		validateRequestBody(bearer);
		Bearer reqReturn = service.updateBearer(bearer, document);
		return reqReturn;
	}

	private void validateQueryParams(String document) throws InvalidJsonException {
		if(document == null || !pattern.matcher(document).matches()) {
			throw new InvalidJsonException("Missing or invalid arguments");	
		}
	}

	private void validateRequestBody(Bearer bearer) throws InvalidJsonException{
		if(bearer.getBearerDocument() == null || bearer.getBearerName() == null || bearer.getBearerType() == null){
			throw new InvalidJsonException("Missing or invalid arguments");
		}
	}
	
	private void validateUpdateDocument(Bearer bearer, String document) throws InvalidOperationException {
		if(!(bearer.getBearerDocument().equals(document))){
			throw new InvalidOperationException("Bearer documents doesn't match");
		}
	}

}
