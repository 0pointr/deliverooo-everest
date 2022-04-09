package com.deliverooo.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.deliverooo.app.Deliveroo;
import com.deliverooo.domain.Coupon;
import com.deliverooo.domain.Order;
import com.deliverooo.domain.Package;
import com.deliverooo.service.CouponService;
import com.deliverooo.service.IDeliveryCostCalculator;

@SpringBootTest(classes = Deliveroo.class, properties = { "test = true" })
@TestInstance(Lifecycle.PER_CLASS)
public class CostTest {

	@Autowired
	CouponService couponService;
	
	@Autowired
	IDeliveryCostCalculator costCalculator;
	
	@BeforeAll
	void setup() {
		couponService
		 .registerCoupon( new Coupon("OFR001", 10, 70, 200, 0, 199) )	// code, discount, minW, maxW, minD, maxD
		 .registerCoupon( new Coupon("OFR002", 7, 100, 250, 50, 150) )
		 .registerCoupon( new Coupon("OFR003", 5, 10, 150, 50, 250) );
	}
	
	@Test
	void costTest() {
		List<Order> orders = new ArrayList<>();
		orders.add( new Order(new Package("PKG1", 5, 5), "OFR001") );
		orders.add( new Order(new Package("PKG1", 15, 5), "OFR002") );
		orders.add( new Order(new Package("PKG1", 10, 100), "OFR003") );
		
		Double[] costs = new Double[] { 175d, 275d, 665d };
		
		IntStream.range(0, orders.size())
		.forEach(i -> {
			assertEquals(costs[i], costCalculator.calculateDiscountedCost(orders.get(i), 100));
		});
	}
	
	@Test
	void discountTest() {
		List<Order> orders = new ArrayList<>();
		orders.add( new Order(new Package("PKG1", 5, 5), "NA") );
		orders.add( new Order(new Package("PKG1", 15, 5), "OFR002") );
		orders.add( new Order(new Package("PKG1", 10, 100), "OFR003") );
		
		Double[] discounts = new Double[] { 0d, 0d, 35d};
		
		IntStream.range(0, orders.size())
		.forEach(i -> {
			assertEquals(discounts[i], costCalculator.calculateDiscount(orders.get(i), 100));
		});
	}
}
