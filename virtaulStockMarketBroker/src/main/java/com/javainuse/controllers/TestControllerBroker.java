package com.javainuse.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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
	@RequestMapping(value = "/sellerStock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseUpgrade> getSellorStock(@RequestBody ResponseUpgrade response) {
		if (response != null) {
			
			jdbcConnection jdbc = new jdbcConnection();
			Connection conn = jdbc.startConnection();

			PreparedStatement stmt;
			try {
				
				//Sellor Shares Updation
				stmt = conn.prepareStatement("SELECT * FROM sellerStock WHERE sellerStock.sellerName='" + response.getSellerName() + "' AND sellerStock.companyName='"+response.getCompanyName()+ "'");
				ResultSet rs1  = stmt.executeQuery();
				System.out.println(rs1 + " records present");
				if(rs1.wasNull()) {
					stmt = conn.prepareStatement("INSERT INTO sellerStock VALUES ('"+response.getSellerName()+"','"+response.getCompanyName()+"',"+response.getStockPrice()+"',"+response.getNumberOfShares()+"'");;
					int i = stmt.executeUpdate();
					System.out.println(i + " records inserted");
				} else {
					stmt = conn.prepareStatement("UPDATE sellerStock SET stockPrice=" + response.getStockPrice()
					+"numberOfShares=" + response.getNumberOfShares()
					+ " WHERE companyName='" + response.getCompanyName() + "sellerName=" + response.getSellerName()
					+"'");

					int i = stmt.executeUpdate();
					System.out.println(i + " records updated");
				}
			} catch (Exception e) {
				System.out.println("No Such Company Found");
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
				System.out.println(rs1 + " records present");
				if(rs1.wasNull()) {
					stmt = conn.prepareStatement("INSERT INTO buyerStock VALUES ('"+response.getBuyerName()+"','"+response.getCompanyName()+"',"+response.getStockPrice()+"',"+response.getNumberOfShares()+"'");;
					int i = stmt.executeUpdate();
					System.out.println(i + " records inserted");
				} else {
					stmt = conn.prepareStatement("UPDATE buyerStock SET stockPrice=" + response.getStockPrice()
					+"numberOfShares=" + response.getNumberOfShares()
					+ " WHERE companyName='" + response.getCompanyName() + "buyerName=" + response.getBuyerName()
					+"'");

					int i = stmt.executeUpdate();
					System.out.println(i + " records updated");
				}
			} catch (Exception e) {
				System.out.println("No Such Company Found");
			}
		}
		return new ResponseEntity<ResponseUpgrade>(response, HttpStatus.OK);
	}
	
	
	private DiscoveryClient discoveryClient;

	

	private void postToStockMarket(List<String> request) {
		List<ServiceInstance> instances = discoveryClient.getInstances("StockMarket");
		ServiceInstance serviceInstance = instances.get(0);
		String baseUrl = serviceInstance.getUri().toString();
		ResponseEntity<Object[]> response = new RestTemplate().postForEntity(baseUrl, request, Object[].class);
		System.out.println(response.getStatusCodeValue());

	}

	
	public void scheduledJob() {

		new java.util.Timer().schedule(new java.util.TimerTask() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				

				jdbcConnection jdbc = new jdbcConnection();
				Connection conn = jdbc.startConnection();

				PreparedStatement stmt;
//				List<Object> finalListOfStock = new ArrayList<Object>();
				List<String> finalListOfStock = new ArrayList<String>();
				
				try {
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
							if(numberOfSharesToBuy >= numberOfSharesToSell) {
								numberOfSharesBought = numberOfSharesToBuy - (numberOfSharesToBuy - numberOfSharesToSell);
								numberOfSharesToBuy = numberOfSharesToBuy - numberOfSharesBought;
								numberOfSharesToSell = 0;
							} else {
								numberOfSharesBought = numberOfSharesToSell - (numberOfSharesToSell - numberOfSharesToBuy);
								numberOfSharesToBuy = 0;
								numberOfSharesToSell = numberOfSharesToSell - numberOfSharesToBuy;
							}
							
//							finalListOfStock.add(companyName);
//							finalListOfStock.add(sellerName);
//							finalListOfStock.add(buyerName);
//							finalListOfStock.add(String.valueOf(stockPrice));
							JSONObject json = new JSONObject();
							
							json.put("companyName",companyName);
							json.put("sellerName", sellerName);
							json.put("buyerName",buyerName);
							json.put("stockPrice", stockPrice);
							json.put("numberOfShares",numberOfSharesBought);
							
							finalListOfStock.add(json.toString());
							
							if(numberOfSharesToSell != 0) {
								stmt = conn.prepareStatement("UPDATE sellerStock SET numberOfShares=" + numberOfSharesToSell
								+ " WHERE companyName='" + companyName + "sellerName=" + sellerName
								+"'");
								int i = stmt.executeUpdate();
								System.out.println(i + " records updated");
							} else {
								stmt = conn.prepareStatement("Delete sellerStock "
								+ " WHERE companyName='" + companyName + "sellerName=" + sellerName
								+"'");
								int i = stmt.executeUpdate();
								System.out.println(i + " records deleted");
							}
						}
						if(numberOfSharesToBuy != 0) {
							stmt = conn.prepareStatement("UPDATE buyerStock"
							+"numberOfShares=" + numberOfSharesToBuy
							+ " WHERE companyName='" + companyName + "buyerName=" + buyerName
							+"'");
							int i = stmt.executeUpdate();
							System.out.println(i + " records updated");
						} else {
							stmt = conn.prepareStatement("Delete buyerStock"
							+ " WHERE companyName='" + companyName + "buyerName=" + buyerName
							+"'");
							int i = stmt.executeUpdate();
							System.out.println(i + " records deleted");
						}
					}
					
				} catch (Exception e) {
					System.out.println("No Such Company Found");
				}
				
				try {
					postToStockMarket(finalListOfStock);

				} catch (Exception e) {
					System.out.println("Error in connection with Stock Market");
				}
			}
		}, 5000);

	}
	


}
