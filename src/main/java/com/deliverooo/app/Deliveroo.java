package com.deliverooo.app;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.deliverooo.domain.Coupon;
import com.deliverooo.domain.Order;
import com.deliverooo.domain.VehicleInfo;
import com.deliverooo.io.IInputProcessor;
import com.deliverooo.io.ProcessedInput;
import com.deliverooo.service.CouponService;
import com.deliverooo.service.IDeliveryCostCalculator;
import com.deliverooo.service.IDeliveryETAService;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@ComponentScan(basePackages = "com.deliverooo")
@PropertySource("classpath:/application.properties")
public class Deliveroo implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(Deliveroo.class);

	@Value("${test:false}")
	private boolean test;
	
	@Value("${couponDataFile:/coupons.json}")
	private String couponDataFile;
	
	@Autowired
	private IInputProcessor inputProcessor;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	@Qualifier("SimpleDeliveryCostCalculatorService")
	private IDeliveryCostCalculator costService;

	@Autowired
	private IDeliveryETAService etaService;

	@Override
	public void run(String... args) {
		try {
			setup();
		} catch (Exception ex) {
			logger.error("Error whie setup", ex);
			return;
		}
		
		if (test) return;
		
		logger.info("waiting for input");
		try {
			
			ProcessedInput input = inputProcessor.process(System.in, StandardCharsets.UTF_8); 
			
			Double baseCost = input.getBaseCost();
			List<Order> orders = input.getOrders();
			VehicleInfo vehicle = input.getVehicleInfo();
			
			orders.forEach(order -> {
				order.setCoupon(couponService.getCoupon(order.getCouponCode()));
				order.setCost(costService.calculateCost(order, baseCost));
				order.setDiscount(costService.calculateDiscount(order, baseCost));
			});
			
			List<Order> ordersWithETA = etaService.getEstimates(vehicle.getCount(), vehicle.getMaxWeight(), vehicle.getMaxSpeed(), orders);

			
			ordersWithETA
			.stream()
			.sorted(Comparator.comparing(Order::getPackageName))
			.forEach(order -> {
				System.out.println(order.getPkg().getPkgName()
									+ " " + order.getDiscount()
									+ " " + String.format("%.2f", (order.getCost() - order.getDiscount())) 
									+ " " + String.format("%.2f", order.getEtaHours()));
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Deliveroo.class, args);
	}

	private void setup() throws StreamReadException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<Coupon> coupons = mapper.readValue(this.getClass().getResourceAsStream(couponDataFile), new TypeReference<List<Coupon>>(){});
		coupons.stream().forEach(c -> couponService.registerCoupon(c));
		
//		couponService.registerCoupon( new Coupon("OFR001", 10, 70, 200, 0, 199) )	// code, discount, minW, maxW, minD, maxD
//					 .registerCoupon( new Coupon("OFR002", 7, 100, 250, 50, 150) )
//					 .registerCoupon( new Coupon("OFR003", 5, 10, 150, 50, 250) );
	}

	
}
