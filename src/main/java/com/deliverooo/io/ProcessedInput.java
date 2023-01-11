package com.deliverooo.io;

import java.util.List;

import com.deliverooo.domain.Order;
import com.deliverooo.domain.VehicleInfo;

public class ProcessedInput {
	Double baseCost;
	List<Order> orders;
	VehicleInfo vehicleInfo;
	
	public ProcessedInput(Double baseCost, List<Order> orders, VehicleInfo vehicleInfo) {
		super();
		this.baseCost = baseCost;
		this.orders = orders;
		this.vehicleInfo = vehicleInfo;
	}
	
	public Double getBaseCost() {
		return baseCost;
	}
	public void setBaseCost(Double baseCost) {
		this.baseCost = baseCost;
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	public VehicleInfo getVehicleInfo() {
		return vehicleInfo;
	}
	public void setVehicleInfo(VehicleInfo vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}
	 
}
