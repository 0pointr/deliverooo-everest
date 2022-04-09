package com.deliverooo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deliverooo.domain.Order;

@Service("SimpleDeliveryCostCalculatorService")
public class SimpleDeliveryCostCalculatorService implements IDeliveryCostCalculator {

	@Autowired
	private CouponService couponService;
	
	
	public double calculateCost(Order order, double baseCost) {
		return baseCost + order.getPkg().getWeight() * 10 + order.getPkg().getDistance() * 5;
	}
	
	@Override
	public double calculateDiscountedCost(Order order, double baseCost) {
		return calculateCost(order, baseCost) - calculateDiscount(order, baseCost);
	}

	@Override
	public double calculateDiscount(Order order, double baseCost) {
		return calculateCost(order, baseCost) * (couponService.getCouponDiscount(order)/100d);
	}

}
