package org.hwx.online.sme.security.demo;

import java.util.HashMap;
import java.util.Map;

public class POSRecord {
	private String transactionDate;
	private String product;
	private int price;
	private String paymentType;
	private String consumerName;
	private String city;
	private String state;
	private String country;
	private String accountCreatedDate;
	private String lastLoginDate;
	private String latitude;
	private String longitude;
	private String key;
	
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getConsumerName() {
		return consumerName;
	}
	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAccountCreatedDate() {
		return accountCreatedDate;
	}
	public void setAccountCreatedDate(String accountCreatedDate) {
		this.accountCreatedDate = accountCreatedDate;
	}
	public String getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public Map<String, String> convertToMap(){
		Map<String, String> recordMap = new HashMap<String, String>();
		recordMap.put("city", getCity());
		recordMap.put("consumerName", getConsumerName());
		recordMap.put("country", getCountry());
		recordMap.put("lastLoginDate", getLastLoginDate());
		recordMap.put("latitude", getLatitude());
		recordMap.put("longitude", getLongitude());
		recordMap.put("paymentType", getPaymentType());
		recordMap.put("product", getProduct());
		recordMap.put("state", getState());
		recordMap.put("transactionDate", getTransactionDate());
		recordMap.put("accountCreatedDate", getAccountCreatedDate());
		
		return recordMap;
		
	}
}
