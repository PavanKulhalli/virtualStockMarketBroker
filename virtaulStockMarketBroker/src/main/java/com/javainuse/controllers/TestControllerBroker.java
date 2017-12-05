package com.javainuse.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
	@RequestMapping(value = "/sellerStock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseUpgrade> getSellerStock(@RequestBody ResponseUpgrade response) {
		if (response != null) {
			System.out.println("In sellerStock");
			jdbcConnection jdbc = new jdbcConnection();
			Connection conn = jdbc.startConnection();

			PreparedStatement stmt;
			try {
				
				//Seller Shares Updation
				stmt = conn.prepareStatement("SELECT * FROM sellerStock WHERE sellerStock.sellerName='" + response.getSellerName() + "' AND sellerStock.companyName='"+response.getCompanyName()+ "'");
				ResultSet rs1  = stmt.executeQuery();
				System.out.println(rs1 + " records present");
				if((!rs1.isBeforeFirst())) {
					String query = "INSERT INTO sellerStock VALUES ('"+response.getSellerName()+"','"+response.getCompanyName()+"',"+response.getStockPrice()+","+response.getNumberOfShares()+")";
					System.out.println(query);
					stmt = conn.prepareStatement(query);
					int i = stmt.executeUpdate();
					System.out.println(i + " records inserted");
				} else {
					String query = "UPDATE sellerStock SET stockPrice=" + response.getStockPrice()
					+", numberOfShares=" + response.getNumberOfShares()
					+ " WHERE companyName='" + response.getCompanyName() + "' AND sellerName = '" + response.getSellerName()
					+"'";
					System.out.println(query);
					stmt = conn.prepareStatement(query);

					int i = stmt.executeUpdate();
					System.out.println(i + " records updated");
				}
			} catch (Exception e) {
				System.out.println("Error"+e);
			}

		}
		return new ResponseEntity<ResponseUpgrade>(response, HttpStatus.OK);
	}

	@SuppressWarnings("resource")
	@RequestMapping(value = "/buyerStock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseUpgrade> getBuyerStock(@RequestBody ResponseUpgrade response) {
		if (response != null) {
			
			jdbcConnection jdbc = new jdbcConnection();
			Connection conn = jdbc.startConnection();

			PreparedStatement stmt;
			try {
				//Buyer Shares Updation
				stmt = conn.prepareStatement("SELECT * FROM buyerStock WHERE buyerStock.buyerName='" + response.getBuyerName() + "' AND buyerStock.companyName='"+response.getCompanyName()+ "'");
				ResultSet rs1  = stmt.executeQuery();
//				System.out.println(rs1.getString(1) + " records present");
				
				if((!rs1.isBeforeFirst())) {
					String query = "INSERT INTO buyerStock VALUES ('"+response.getBuyerName()+"','"+response.getCompanyName()+"',"+response.getNumberOfShares()+")";
					System.out.println(query);
					stmt = conn.prepareStatement(query);
					int i = stmt.executeUpdate();
					System.out.println(i + " records inserted");
				} else {
					String query = "UPDATE buyerStock SET numberOfShares=" + response.getNumberOfShares()
					+ " WHERE companyName='" + response.getCompanyName() + "' AND buyerName='" + response.getBuyerName()
					+"'";
					System.out.println(query);
					stmt = conn.prepareStatement(query);
					int i = stmt.executeUpdate();
					System.out.println(i + " records updated");
				}
			} catch (Exception e) {
				System.out.println("Error"+e);
			}
		}
		return new ResponseEntity<ResponseUpgrade>(response, HttpStatus.OK);
	}
	
	
	private DiscoveryClient discoveryClient;

	

	private void postToStockMarket(JSONArray list) {
		System.out.println("postToStockMarket");
		List<ServiceInstance> instances = discoveryClient.getInstances("stockUpdates");
		System.out.println("instances"+instances);
		ServiceInstance serviceInstance = instances.get(0);
		String baseUrl = serviceInstance.getUri().toString();
		System.out.println("baseUrl" + baseUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<JSONArray> entity = new HttpEntity<JSONArray>(list, headers);
		ResponseEntity<String> response = new RestTemplate().postForEntity(baseUrl, entity, String.class);
		System.out.println(response.getStatusCodeValue());

	}

	
	public void scheduledJob() {

		new java.util.Timer().schedule(new java.util.TimerTask() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				
				System.out.println(" Scheduler Called");
				jdbcConnection jdbc = new jdbcConnection();
				Connection conn = jdbc.startConnection();

				PreparedStatement stmt;
//				List<Object> finalListOfStock = new ArrayList<Object>();
				List<String> finalListOfStock = new ArrayList<String>();
				
				try {
					System.out.println(" scheduledJob running");
					//Buyer Shares Updation
					stmt = conn.prepareStatement("SELECT * FROM buyerStock");
					ResultSet rs  = stmt.executeQuery();
					System.out.println(rs + " records present");
					double stockPrice;
					String buyerName, companyName, sellerName;
					int numberOfSharesToBuy, numberOfSharesToSell, numberOfSharesBought;
					while (rs.next()) {
						
						buyerName = rs.getString(1);
						companyName = rs.getString(2);
						numberOfSharesToBuy = rs.getInt(3);
						
						stmt = conn.prepareStatement("SELECT * FROM sellerStock WHERE sellerStock.companyName='"+companyName+ "'");
						ResultSet rs1  = stmt.executeQuery();
						System.out.println(rs1 + " records present");
						while (rs1.next() && numberOfSharesToBuy > 0) {
							sellerName = rs1.getString(1);
							stockPrice = rs1.getDouble(3);
							numberOfSharesToSell = rs1.getInt(4);
							System.out.println("sellerName:"+ sellerName + " stockPrice" + stockPrice +" numberOfSharesToSell" + numberOfSharesToSell);
							if(numberOfSharesToBuy >= numberOfSharesToSell) {
								numberOfSharesBought = numberOfSharesToBuy - (numberOfSharesToBuy - numberOfSharesToSell);
								numberOfSharesToBuy = numberOfSharesToBuy - numberOfSharesBought;
								numberOfSharesToSell = 0;
							} else {
								numberOfSharesBought = numberOfSharesToSell - (numberOfSharesToSell - numberOfSharesToBuy);
								numberOfSharesToBuy = 0;
								numberOfSharesToSell = numberOfSharesToSell - numberOfSharesToBuy;
							}
							System.out.println("numberOfSharesBought" + numberOfSharesBought +"numberOfSharesToBuy"+numberOfSharesToBuy +"numberOfSharesToSell"+numberOfSharesToSell);
							
							JSONObject json = new JSONObject();
							
							json.put("companyName",companyName);
							json.put("sellerName", sellerName);
							json.put("buyerName",buyerName);
							json.put("stockPrice", stockPrice);
							json.put("numberOfShares",numberOfSharesBought);
							System.out.println("Array List companyName:"+ companyName + " sellerName:"+sellerName+ " buyerName"+buyerName+" stockPrice"+stockPrice+" numberOfShares"+numberOfSharesBought);
							finalListOfStock.add(json.toString());
							
							if(numberOfSharesToSell != 0) {
								String query = "UPDATE sellerStock SET numberOfShares=" + numberOfSharesToSell
										+ " WHERE companyName='" + companyName + "' AND sellerName='" + sellerName
										+"'";
								System.out.println(query);
								stmt = conn.prepareStatement(query);
								int i = stmt.executeUpdate();
								System.out.println(i + " records updated for seller: "+ sellerName + "Company: "+ companyName);
							} else {
								String query = "DELETE from sellerStock "
										+ " WHERE companyName='" + companyName + "' AND sellerName= '" + sellerName
										+"'";
								System.out.println(query);
								stmt = conn.prepareStatement(query);
								int i = stmt.executeUpdate();
								System.out.println(i + " records deleted for seller: "+ sellerName + "Company: "+ companyName);
							}
						}
						if(numberOfSharesToBuy != 0) {
							String query = "UPDATE buyerStock SET"
									+" numberOfShares= " + numberOfSharesToBuy
									+ " WHERE companyName='" + companyName + "' AND buyerName='" + buyerName
									+"'";
							System.out.println(query);
							stmt = conn.prepareStatement(query);
							int i = stmt.executeUpdate();
							System.out.println(i + " records updated for Buyer: "+ buyerName + "Company: "+ companyName);
						} else {
							String query = "DELETE from buyerStock"
									+ " WHERE companyName='" + companyName + "' AND buyerName='" + buyerName
									+"'";
							System.out.println(query);
							stmt = conn.prepareStatement(query);
							int i = stmt.executeUpdate();
							System.out.println(i + " records deleted for Buyer: "+ buyerName + "Company: "+ companyName);
						}
					}
					
				} catch (Exception e) {
					System.out.println("Error:" + e);
				}
				
				try {
					System.out.println("Posting it to Stock Market" +finalListOfStock);
					postToStockMarket(new JSONArray(finalListOfStock));

				} catch (Exception e) {
					System.out.println("Error"+e);
				}
			}
		}, 10000);

	}
	


}
