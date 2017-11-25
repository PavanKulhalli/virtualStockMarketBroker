package com.javainuse.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//import com.javainuse.model.Company;

@Controller
@RestController
public class TestControllerBroker {

	
	@RequestMapping(value = "/stockMarket", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseUpgrade> update(@RequestBody ResponseUpgrade response) {
		if (response != null) {
			System.out.println("Inhere");
			jdbcConnection jdbc = new jdbcConnection();
			Connection conn = jdbc.startConnection();

			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement("UPDATE stockprice SET StockPrice=" + response.getStock_price()
						+ " WHERE StockName='" + response.getCompany_Name() + "'");

				int i = stmt.executeUpdate();
				System.out.println(i + " records updated");
			} catch (Exception e) {
				System.out.println("No Such Company Found");
			}
			try {

				postToStockMarket(response);

			} catch (Exception e) {
				System.out.println("Error in connection with Stock Market");
			}

		}
		return new ResponseEntity<ResponseUpgrade>(response, HttpStatus.OK);
	}

	private DiscoveryClient discoveryClient;

	

	private void postToStockMarket(ResponseUpgrade response) {
		List<ServiceInstance> instances = discoveryClient.getInstances("StockMarket");
		ServiceInstance serviceInstance = instances.get(0);
		String baseUrl = serviceInstance.getUri().toString();
		ResponseEntity<?> response1 = new RestTemplate().postForEntity(baseUrl, response, String.class);
		System.out.println(response1.getStatusCodeValue());

	}

	/*
	private void postToClient(ResponseUpgrade response) {
		List<ServiceInstance> instances = discoveryClient.getInstances("bank");
		ServiceInstance serviceInstance = instances.get(0);
		String baseUrl = serviceInstance.getUri().toString();
		ResponseEntity<?> response1 = new RestTemplate().postForEntity(baseUrl, response, String.class);
		System.out.println(response1.getStatusCodeValue());

	}
	*/


}
