package com.javainuse.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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


	@Autowired 
	private DiscoveryClient discoveryClient;
	public boolean startFlag;
	public static String baseUrl;
	
	@SuppressWarnings("resource")
	@RequestMapping(value = "/sellerStock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseUpgrade> getSellerStock(@RequestBody ResponseUpgrade response) {
		if (response != null) {
			
			setBaseUrl();
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
		System.out.println("HERE");
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
					String query = "INSERT INTO buyerStock VALUES ('"+response.getBuyerName()+"','"+response.getCompanyName()+"',"+response.getStockPrice()+","+response.getNumberOfShares()+")";
					System.out.println(query);
					stmt = conn.prepareStatement(query);
					int i = stmt.executeUpdate();
					System.out.println(i + " records inserted");
				} else {
					String query = "UPDATE buyerStock SET stockPrice=" + response.getStockPrice() 
					+", numberOfShares=" + response.getNumberOfShares()
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

	private void setBaseUrl() {
		List<ServiceInstance> instances = discoveryClient.getInstances("stockUpdates");
		System.out.println("instances"+instances);
		ServiceInstance serviceInstance = instances.get(0);
		baseUrl = serviceInstance.getUri().toString();
		System.out.println("baseUrl: " + baseUrl);
		baseUrl += "/stockMarket";
	}
	private void postToStockMarket(List<ResponseUpgrade> list) {
		System.out.println("postToStockMarket");
		
		if(baseUrl != null) {
			System.out.println("BASE URL "+baseUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			System.out.println(list.get(0).getClass().getName());
			HttpEntity<List<ResponseUpgrade>> entity = new HttpEntity<List<ResponseUpgrade>>(list, headers);
			ResponseEntity<ResponseUpgrade> response = new RestTemplate().postForEntity(baseUrl, entity, ResponseUpgrade.class);
			System.out.println(response);
		}else {
			System.out.println("BASE URL "+baseUrl);
		}
//		String baseUrl = "http://localhost:8093/stockMarket";
	}

	
	public void scheduledJob() {
		Timer timer = new Timer(10000, new ActionListener() {
			  @Override
			  public void actionPerformed(ActionEvent arg0) {
			    // Code to be executed
				  System.out.println(" Scheduler Called");
				  jdbcConnection jdbc = new jdbcConnection();
					Connection conn = jdbc.startConnection();

					PreparedStatement stmt;
//					List<Object> finalListOfStock = new ArrayList<Object>();
					List<ResponseUpgrade> finalListOfStock = new ArrayList<ResponseUpgrade>();
					
					try {
						System.out.println(" scheduledJob running");
						//Buyer Shares Updation
						stmt = conn.prepareStatement("SELECT * FROM buyerStock");
						ResultSet rs  = stmt.executeQuery();
						System.out.println(rs + " records present");
						double buyerStockPrice, sellerStockPrice;
						String buyerName, sellerName, companyName;
						int numberOfSharesToBuy, numberOfSharesToSell, numberOfSharesBought;
						while (rs.next()) {
							
							buyerName = rs.getString(1);
							companyName = rs.getString(2);
							buyerStockPrice = rs.getDouble(3);
							numberOfSharesToBuy = rs.getInt(4);
							
							stmt = conn.prepareStatement("SELECT * FROM sellerStock WHERE sellerStock.companyName='"+companyName+ "'");
							ResultSet rs1  = stmt.executeQuery();
							System.out.println(rs1 + " records present");
							while (rs1.next() && numberOfSharesToBuy > 0) {
								sellerName = rs1.getString(1);
								sellerStockPrice = rs1.getDouble(3);
								numberOfSharesToSell = rs1.getInt(4);
								System.out.println("sellerName:"+ sellerName + " stockPrice" + sellerStockPrice +" numberOfSharesToSell" + numberOfSharesToSell);
								
								if (buyerStockPrice >= sellerStockPrice) 
									{
									if(numberOfSharesToBuy >= numberOfSharesToSell) {
										numberOfSharesBought = numberOfSharesToBuy - (numberOfSharesToBuy - numberOfSharesToSell);
										numberOfSharesToBuy = numberOfSharesToBuy - numberOfSharesBought;
										numberOfSharesToSell = 0;
									} else {
										numberOfSharesBought = numberOfSharesToSell - (numberOfSharesToSell - numberOfSharesToBuy);
										numberOfSharesToSell = numberOfSharesToSell - numberOfSharesToBuy;
										numberOfSharesToBuy = 0;
									}
									System.out.println("numberOfSharesBought" + numberOfSharesBought +"numberOfSharesToBuy"+numberOfSharesToBuy +"numberOfSharesToSell"+numberOfSharesToSell);
									
									ResponseUpgrade responseUpgrade = new ResponseUpgrade();
									responseUpgrade.setCompanyName(companyName);
									responseUpgrade.setSellerName(sellerName);
									responseUpgrade.setBuyerName(buyerName);
									responseUpgrade.setStockPrice(sellerStockPrice);
									responseUpgrade.setNumberOfShares(numberOfSharesBought);
									
									System.out.println("Array List companyName:"+ companyName + " sellerName:"+sellerName+ " buyerName"+buyerName+" stockPrice"+sellerStockPrice+" numberOfShares"+numberOfSharesBought);
									finalListOfStock.add(responseUpgrade);
									
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
						if(finalListOfStock.size() > 0) {
							postToStockMarket((List<ResponseUpgrade>) finalListOfStock);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			  }
			});
			timer.setRepeats(true); // Only execute once
			timer.start(); 
	}
}
