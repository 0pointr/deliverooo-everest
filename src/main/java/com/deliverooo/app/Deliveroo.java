package com.deliverooo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.deliverooo.domain.Coupon;
import com.deliverooo.domain.Order;
import com.deliverooo.domain.VehicleInfo;
import com.deliverooo.service.CouponService;
import com.deliverooo.service.IDeliveryCostCalculator;
import com.deliverooo.service.IDeliveryETAService;
import com.deliverooo.service.IOrderParser;
import com.deliverooo.service.SimpleOrderParser;

@SpringBootApplication
@ComponentScan(basePackages = "com.deliverooo")
public class Deliveroo implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(Deliveroo.class);

	@Value("${test}")
	boolean test;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	@Qualifier("SimpleDeliveryCostCalculatorService")
	private IDeliveryCostCalculator costService;

	@Autowired
	private IDeliveryETAService etaService;
	
	private final IOrderParser orderParser = new SimpleOrderParser();

	private String DELIMITER = "[ ]+";
	
	@Override
	public void run(String... args) {
		
		setup();
		
		if (test) return;
		
		logger.info("waiting for input");
		try {
			
			List<String> lines = getInput();
			if (lines.size() <= 1) {
				System.out.println("Nothing to do");
			}
			
			Double baseCost = getBaseCostInput(lines.get(0));
			List<Order> orders = getOrdersInput(lines.subList(1, lines.size()-1));
			VehicleInfo vehicle = getVehicleInfoInput(lines.get(lines.size()-1));
			
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
									+ " " + String.format("%.2f", order.getEtaMinutes()));
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Deliveroo.class, args);
	}

	private void setup() {
		
		couponService.registerCoupon( new Coupon("OFR001", 10, 70, 200, 0, 199) )	// code, discount, minW, maxW, minD, maxD
					 .registerCoupon( new Coupon("OFR002", 7, 100, 250, 50, 150) )
					 .registerCoupon( new Coupon("OFR003", 5, 10, 150, 50, 250) );
	}
	
	private List<String> getInput() throws IOException {
		
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
			String nextLine;
			do {
				nextLine = reader.readLine();
				if (nextLine != null && nextLine.trim().length() > 0)
					lines.add(nextLine);
			} while (nextLine != null && nextLine.trim().length() > 0);
		}
		
		return lines;
	}

	private Double getBaseCostInput(String line) {
		return Double.valueOf( line.trim().split(DELIMITER )[0] );
	}
	
	private List<Order> getOrdersInput(List<String> lines) {
		return
		lines.stream().map(line -> {
			try {
				return orderParser.parse(line);
			} catch (Exception ex) {
				logger.error("Error parsing order", ex);
				return null;
			}
		})
		.filter(Objects::nonNull)
		.collect(Collectors.toList());
	}
	
	private VehicleInfo getVehicleInfoInput(String line) {
		String[] tokens = line.split(DELIMITER);
		return new VehicleInfo(Integer.valueOf(tokens[0]), Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2]));
	}
	
}
