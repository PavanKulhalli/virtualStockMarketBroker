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
public void setCompanyName(String companyName) {
	this.companyName = companyName;
}
public String getBuyerName() {
	return buyerName;
}
public void setBuyerName(String buyerName) {
	this.buyerName = buyerName;
}
public String getSellerName() {
	return sellerName;
}
public void setSellerName(String sellerName) {
	this.sellerName = sellerName;
}
public int getNumberOfShares() {
	return numberOfShares;
}
public void setNumberOfShares(int numberOfShares) {
	this.numberOfShares = numberOfShares;
}
}
