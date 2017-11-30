package com.javainuse.controllers;

public class ResponseUpgrade {
private double stockPrice;
private String companyName;
private String buyerName;
private String sellerName;
private int numberOfShares;

public double getStockPrice() {
	return stockPrice;
}
public void setStockPrice(double stockPrice) {
	this.stockPrice = stockPrice;
}
public String getCompanyName() {
	return companyName;
}
public void setCompanyName(String company_Name) {
	companyName = company_Name;
}
public String getBuyerName() {
	return buyerName;
}
public void setBuyerName(String buyer) {
	buyerName = buyer;
}
public String getSellerName() {
	return sellerName;
}
public void setSellerName(String seller) {
	sellerName = seller;
}
public int getNumberOfShares() {
	return numberOfShares;
}
public void setNumberOfShares(int number_of_shares) {
	numberOfShares = number_of_shares;
}
}
