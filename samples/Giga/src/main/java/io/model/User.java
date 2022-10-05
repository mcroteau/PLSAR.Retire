package io.model;

import java.math.BigDecimal;
import java.util.List;

public class User {
	Long id;
	Long businessId;
	String name;
	String phone;
	String username;
	String password;
	Long dateJoined;

	String paymentStreet;
	String paymentStreetDos;
	String paymentCity;
	String paymentState;
	String paymentZip;
	String paymentCountry;//HELP! MICROSOFT IS GOING TO TAKE OVER STRIPE!

	String shipStreet;
	String shipStreetDos;
	String shipCity;
	String shipState;
	String shipZip;
	String shipCountry;

	String notes;
	Boolean mining;

	String uuid;

	String freshPassword;

	Long salesCount;
	BigDecimal salesTotal;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public String getName() {
		if(this.name != null &&
				!this.name.equals("")){
			return this.name;
		}
		return "";
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		if(this.phone != null &&
				!this.phone.equals("")){
			return this.phone;
		}
		return "";
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Long dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String getPaymentStreet() {
		return paymentStreet;
	}

	public void setPaymentStreet(String paymentStreet) {
		this.paymentStreet = paymentStreet;
	}

	public String getPaymentStreetDos() {
		return paymentStreetDos;
	}

	public void setPaymentStreetDos(String paymentStreetDos) {
		this.paymentStreetDos = paymentStreetDos;
	}

	public String getPaymentCity() {
		return paymentCity;
	}

	public void setPaymentCity(String paymentCity) {
		this.paymentCity = paymentCity;
	}

	public String getPaymentState() {
		return paymentState;
	}

	public void setPaymentState(String paymentState) {
		this.paymentState = paymentState;
	}

	public String getPaymentZip() {
		return paymentZip;
	}

	public void setPaymentZip(String paymentZip) {
		this.paymentZip = paymentZip;
	}

	public String getPaymentCountry() {
		return paymentCountry;
	}

	public void setPaymentCountry(String paymentCountry) {
		this.paymentCountry = paymentCountry;
	}

	public String getShipStreet() {
		return shipStreet;
	}

	public void setShipStreet(String shipStreet) {
		this.shipStreet = shipStreet;
	}

	public String getShipStreetDos() {
		return shipStreetDos;
	}

	public void setShipStreetDos(String shipStreetDos) {
		this.shipStreetDos = shipStreetDos;
	}

	public String getShipCity() {
		return shipCity;
	}

	public void setShipCity(String shipCity) {
		this.shipCity = shipCity;
	}

	public String getShipState() {
		return shipState;
	}

	public void setShipState(String shipState) {
		this.shipState = shipState;
	}

	public String getShipZip() {
		return shipZip;
	}

	public void setShipZip(String shipZip) {
		this.shipZip = shipZip;
	}

	public String getShipCountry() {
		return shipCountry;
	}

	public void setShipCountry(String shipCountry) {
		this.shipCountry = shipCountry;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getMining() {
		return mining;
	}

	public void setMining(Boolean mining) {
		this.mining = mining;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFreshPassword() {
		return freshPassword;
	}

	public void setFreshPassword(String freshPassword) {
		this.freshPassword = freshPassword;
	}

	public Long getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Long salesCount) {
		this.salesCount = salesCount;
	}

	public BigDecimal getSalesTotal() {
		return salesTotal;
	}

	public void setSalesTotal(BigDecimal salesTotal) {
		this.salesTotal = salesTotal;
	}

	public boolean valid(String value){
		return value != null && !value.equals("null") && !value.equals("");
	}
}

