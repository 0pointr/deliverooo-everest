package com.deliverooo.service;

import com.deliverooo.domain.Order;

public interface IDeliveryCostCalculator {

	public double calculateCost(Order order, double baseCost);
	public double calculateDiscount(Order order, double baseCost);
	public double calculateDiscountedCost(Order order, double baseCost);
	
}
