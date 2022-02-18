package com.smk.example.spring.boot.rest.controller;

import java.net.InetSocketAddress;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/")
@Slf4j
public class HomeController {

	@GetMapping("/listHeaders")
	public ResponseEntity<String> listHeaders(@RequestHeader MultiValueMap<String, String> headers) {
		log.info("HTTP GET values of all HTTP Headers");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		headers.forEach((key, value) -> {
			node.put(key, value.stream().collect(Collectors.joining("|")));			
		});
	    return new ResponseEntity<String>(node.toPrettyString(), HttpStatus.OK);
	}
	
	@GetMapping("/getBaseUrl")
	public ResponseEntity<String> getBaseUrl(@RequestHeader HttpHeaders headers) {
		log.info("HTTP GET Base URL");
	    InetSocketAddress host = headers.getHost();
	    String url = "http://" + host.getHostName() + ":" + host.getPort();
	    return new ResponseEntity<String>(String.format("Base URL = %s", url), HttpStatus.OK);
	}
	
	@GetMapping("/getUserAgent")
	public ResponseEntity<String> getUserAgent(@RequestHeader("user-agent") String userAgent) {
		log.info("HTTP GET User Agent");
	    return new ResponseEntity<String>(userAgent, HttpStatus.OK);
	}

}
