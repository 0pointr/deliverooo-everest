package com.deliverooo.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
import com.deliverooo.domain.VehicleInfo;
import com.deliverooo.exception.InvalidInputException;
import com.deliverooo.exception.UnrecognisedOrderFormatException;
import com.deliverooo.io.IInputProcessor;
import com.deliverooo.io.ProcessedInput;
import com.deliverooo.service.CouponService;
import com.deliverooo.service.IDeliveryCostCalculator;
import com.deliverooo.service.IDeliveryETAService;

@SpringBootTest(classes = Deliveroo.class, properties = { "test = true" })
@TestInstance(Lifecycle.PER_CLASS)
public class Tests {

	@Autowired
	CouponService couponService;
	
	@Autowired
	IDeliveryCostCalculator costCalculator;
	
	@Autowired
	IDeliveryETAService etaService;
	
	@Autowired
	IInputProcessor inputProcessor;
	
	@BeforeAll
	void setup() {
		couponService
		 .registerCoupon( new Coupon("OFR001", 10, 70, 200, 0, 199) )	// code, discount, minW, maxW, minD, maxD
		 .registerCoupon( new Coupon("OFR002", 7, 100, 250, 50, 150) )
		 .registerCoupon( new Coupon("OFR003", 5, 10, 150, 50, 250) );
	}

	@Test
	void invalidBaseCostInputTest() {
		String inputStr = "1.0.0 3\n" + 
				"PKG1 5 5 OFR001\n" + 
				"PKG2 15 5 OFR002\n" + 
				"PKG3 10 100 OFR003\n" + 
				"2 70 200";
		Throwable ex = assertThrows(InvalidInputException.class, () -> inputProcessor.process(new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
		assertEquals("Input cannot be processed", ex.getMessage());
		assertEquals(ex.getCause().getClass(), UnrecognisedOrderFormatException.class);
		assertTrue(ex.getCause().getMessage().contains("Could not read base delivery cost"));
	}
	
	@Test
	void invalidOrderInputTest() {
		String inputStr = "100 3\n" + 
				"PKG1 5 5\n" + 
				"PKG2 15 5 OFR002\n" + 
				"PKG3 10 100 OFR003\n" + 
				"2 70 200";
		Throwable ex = assertThrows(InvalidInputException.class, () -> inputProcessor.process(new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
		assertEquals("Input cannot be processed", ex.getMessage());
		assertEquals(ex.getCause().getClass(), UnrecognisedOrderFormatException.class);
	}
	
	@Test
	void invalidVehicleInputTest() {
		String inputStr = "100 3\n" + 
				"PKG1 5 5 OFR001\n" + 
				"PKG2 15 5 OFR002\n" + 
				"PKG3 10 100 OFR003\n" + 
				"2 70";
		Throwable ex = assertThrows(InvalidInputException.class, () -> inputProcessor.process(new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
		assertEquals("Input cannot be processed", ex.getMessage());
		assertEquals(ex.getCause().getClass(), UnrecognisedOrderFormatException.class);
		assertTrue(ex.getCause().getMessage().contains("Could not read vehicle info"));
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
	
	@Test
	void etaTest1() throws InvalidInputException {
		
		String inputStr = "100 3\n" + 
				"PKG1 5 5 OFR001\n" + 
				"PKG2 15 5 OFR002\n" + 
				"PKG3 10 100 OFR003\n" + 
				"2 70 200";
		
		List<Order> ordersWithETA = getOrderEtasFromInputString(inputStr)
									.stream()
									.sorted(Comparator.comparing(Order::getPackageName))
									.collect(Collectors.toList());
		Double[] expectedEta = new Double[] {.07, .07, 1.43};
		IntStream.range(0, ordersWithETA.size())
		.forEach(i -> {
			assertEquals(expectedEta[i], ordersWithETA.get(i).getEtaHours(), 0.01);
		});
	}
	
	@Test
	void etaTest2() throws InvalidInputException {
		
		String inputStr = "100 5\n" + 
				"PKG1 50 30 OFR001\n" + 
				"PKG2 75 125 OFR002\n" + 
				"PKG3 175 100 OFR003\n" + 
				"PKG4 110 60 OFR003\n" + 
				"PKG5 155 95 NA\n" + 
				"2 70 200";
		
		List<Order> ordersWithETA = getOrderEtasFromInputString(inputStr)
									.stream()
									.sorted(Comparator.comparing(Order::getPackageName))
									.collect(Collectors.toList());
		// notice the answers are rounded to 2 decimal places,
		// not truncated as given in the problem statement.
		Double[] expectedEta = new Double[] {4.01, 1.79, 1.43, 0.86, 4.22};
		IntStream.range(0, ordersWithETA.size())
		.forEach(i -> {
			assertEquals(expectedEta[i], ordersWithETA.get(i).getEtaHours(), 0.01);
		});
	}
	
	private List<Order> getOrderEtasFromInputString(String inputStr) throws InvalidInputException {
		ProcessedInput input = inputProcessor.process(new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8); 
		
		Double baseCost = input.getBaseCost();
		List<Order> orders = input.getOrders();
		VehicleInfo vehicle = input.getVehicleInfo();
		
		orders.forEach(order -> {
			order.setCoupon(couponService.getCoupon(order.getCouponCode()));
			order.setCost(costCalculator.calculateCost(order, baseCost));
			order.setDiscount(costCalculator.calculateDiscount(order, baseCost));
		});
		
		return etaService.getEstimates(vehicle.getCount(), vehicle.getMaxWeight(), vehicle.getMaxSpeed(), orders);
	}
}
