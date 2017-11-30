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

	
	@SuppressWarnings("resource")
	@RequestMapping(value = "/sellorStock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseUpgrade> getSellorStock(@RequestBody ResponseUpgrade response) {
		if (response != null) {
			System.out.println("Inhere");
			jdbcConnection jdbc = new jdbcConnection();
			Connection conn = jdbc.startConnection();

			PreparedStatement stmt;
			try {
				
				//Sellor Shares Updation
				stmt = conn.prepareStatement("SELECT * FROM sellorStock WHERE sellorStock.sellerName='" + response.getSellerName() + "' AND sellorStock.companyName='"+response.getCompanyName()+ "'");
				ResultSet rs1  = stmt.executeQuery();
				System.out.println(rs1 + " records present");
				if(rs1.wasNull()) {
					stmt = conn.prepareStatement("INSERT INTO sellorStock VALUES ('"+response.getSellerName()+"','"+response.getCompanyName()+"',"+response.getStockPrice()+"',"+response.getNumberOfShares()+"'");;
					int i = stmt.executeUpdate();
					System.out.println(i + " records inserted");
				} else {
					stmt = conn.prepareStatement("UPDATE sellorStock SET stockPrice=" + response.getStockPrice()
					+"numberOfShares=" + response.getNumberOfShares()
					+ " WHERE companyName='" + response.getCompanyName() + "sellerName=" + response.getSellerName()
					+"'");

					int i = stmt.executeUpdate();
					System.out.println(i + " records updated");
				}
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

	@SuppressWarnings("resource")
	@RequestMapping(value = "/buyerStock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseUpgrade> getBuyerStock(@RequestBody ResponseUpgrade response) {
		if (response != null) {
			System.out.println("Inhere");
			jdbcConnection jdbc = new jdbcConnection();
			Connection conn = jdbc.startConnection();

			PreparedStatement stmt;
			try {
				//Buyer Shares Updation
				stmt = conn.prepareStatement("SELECT * FROM buyerStock WHERE buyerStock.buyerName='" + response.getSellerName() + "' AND buyerStock.companyName='"+response.getCompanyName()+ "'");
				ResultSet rs1  = stmt.executeQuery();
				System.out.println(rs1 + " records present");
				if(rs1.wasNull()) {
					stmt = conn.prepareStatement("INSERT INTO buyerStock VALUES ('"+response.getSellerName()+"','"+response.getCompanyName()+"',"+response.getStockPrice()+"',"+response.getNumberOfShares()+"'");;
					int i = stmt.executeUpdate();
					System.out.println(i + " records inserted");
				} else {
					stmt = conn.prepareStatement("UPDATE buyerStock SET stockPrice=" + response.getStockPrice()
					+"numberOfShares=" + response.getNumberOfShares()
					+ " WHERE companyName='" + response.getCompanyName() + "buyerName=" + response.getSellerName()
					+"'");

					int i = stmt.executeUpdate();
					System.out.println(i + " records updated");
				}
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
