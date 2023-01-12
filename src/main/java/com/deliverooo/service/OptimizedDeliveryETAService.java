package com.deliverooo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.deliverooo.domain.Order;
import com.deliverooo.domain.Package;
import com.deliverooo.util.Util;

@Service("OptimizedDeliveryETAService")
public class OptimizedDeliveryETAService implements IDeliveryETAService {

	private final Logger logger = LoggerFactory.getLogger(SimpleDeliveryETAService.class);
	
	@Override
	public List<Order> getEstimates(int vehicleCount, int maxWeightPerVehicle,
										   int vehicleMaxSpeed, List<Order> ords) {

		List<Order> orders = new ArrayList<>(ords);
		
		Map<Integer, Double> leadTimes = new HashMap<>();
		for (int i=0; i<vehicleCount; i++) {
			leadTimes.put(i, 0d);
		}
		List<Order> processedOrders = new ArrayList<>();
		int consignNumber = 1;
		
		while (orders.size() > 0) {
			
			OrderGroup consignment = createGroup(orders, 
												 maxWeightPerVehicle);
			logger.info("Consignment number:" + consignNumber++
					  + (" weight:" + consignment.totalWeight)
					  + (" items:" + consignment.getOrders().stream().map(o -> o.getPackageName()).collect(Collectors.joining(",")))
					  );
			
			Map.Entry<Integer, Double> minLeadTimeVehicle = leadTimes.entrySet().stream().min((e1,e2) -> e1.getValue().compareTo(e2.getValue())).get();
			double deliveryEndETA = 0d;
			for (Order order : consignment.getOrders()) {
				Double etaHours = minLeadTimeVehicle.getValue() + ((double)order.getPkg().getDistance() / vehicleMaxSpeed);
				order.setEtaHours(Util.round(2, etaHours));
				deliveryEndETA = Math.max(deliveryEndETA, order.getEtaHours());
			}
			
			leadTimes.put(minLeadTimeVehicle.getKey(), deliveryEndETA*2);
			for (Order o : consignment.getOrders()) {
				processedOrders.add(o);
				orders.remove(o);
			}
		}
		
		return processedOrders;
	}

	public static class OrderGroup {
		List<Order> orders;
		int totalWeight;
		int maxDistance;
		public OrderGroup(List<Order> orders, int totalWeight) {
			super();
			this.orders = orders;
			this.totalWeight = totalWeight;
			this.maxDistance = 0;
			if (orders.size() > 0) {
				this.maxDistance = orders.stream()
										.max((o1, o2) -> o1.getPkg().getDistance().compareTo(o2.getPkg().getDistance()))
										.get().getPkg().getDistance();
			}
		}
		public List<Order> getOrders() {
			return orders;
		}
		public int getTotalWeight() {
			return totalWeight;
		}
		public int getSize() {
			return orders.size();
		}
		public int getMaxDistance() {
			return maxDistance;
		}
		
	}
	
	/**
	 * Dynamic programming solution to the problem
	 * of fitting k items in a group with the constraints that:
	 * 
	 * 1. group size has to be maximum within a weight class
	 * 2. for groups of same size, break tie by higher weight
	 * 3. for same size and weight, break tie by lower delivery time
	 * 
	 * Time O(order.size() * maxWeight), 
	 * Space O(order.size() * maxWeight)
	 *
	 * @param orders
	 * @param maxWeight
	 * @return
	 */
	private OrderGroup createGroup(List<Order> orders, int maxWeight) {
		List<ArrayList<OrderGroup>> dp = new ArrayList<>();
		IntStream.range(0, orders.size()+1).forEach(i -> {
			dp.add(new ArrayList<>());
		});
		
		Order blankOrder = new Order(new Package("", 0, 0), "NA");
		List<Order> initOrderList = new ArrayList<>(1);
		initOrderList.add(blankOrder);
		OrderGroup blankGroup = new OrderGroup(initOrderList, 0);
		
		// initialize the first row and
		// the first column with a blank order 
		IntStream.range(0, maxWeight+1)
		.forEach(i -> {
			dp.get(0).add(blankGroup);
		});
		
		IntStream.range(1, orders.size()+1)
		.forEach(i -> {
			dp.get(i).add(blankGroup);
		});
		
		for (int i=1; i<=orders.size(); i++) {
			for (int j=1; j<=maxWeight; j++) {
	
				int itemWeight = orders.get(i-1).getPkg().getWeight();
				
				if (itemWeight <= j) {
					int weightWithoutItem = dp.get(i-1).get(j).getTotalWeight();
					int weightWithItem = dp.get(i-1).get(j-itemWeight).getTotalWeight() + itemWeight;
					
					int countWithoutItem = dp.get(i-1).get(j).getSize();
					int countWithItem = dp.get(i-1).get(j-itemWeight).getSize() + 1;
					
					int distanceWithoutItem = dp.get(i-1).get(j).getMaxDistance();
					int distanceWithItem = Math.max(
												dp.get(i-1).get(j-itemWeight).getMaxDistance(),
												orders.get(i-1).getPkg().getDistance()
												);
					if 
					(
						(countWithItem > countWithoutItem) ||
						(countWithItem == countWithoutItem && weightWithItem > weightWithoutItem) ||
						(countWithItem == countWithoutItem && weightWithItem == weightWithoutItem && distanceWithItem < distanceWithoutItem)
					)
					{
						List<Order> newList = new ArrayList<>(dp.get(i-1).get(j-itemWeight).getOrders());
						newList.add(orders.get(i-1));
						dp.get(i).add(new OrderGroup(newList, weightWithItem));
					}
					else {
						dp.get(i).add(dp.get(i-1).get(j));
					}
					
				} else {
					dp.get(i).add(dp.get(i-1).get(j));
				}
			}
		}
		
		OrderGroup result = dp.get(orders.size()).get(maxWeight);
		result.getOrders().remove(blankOrder);
		
		return result;
	}

}


