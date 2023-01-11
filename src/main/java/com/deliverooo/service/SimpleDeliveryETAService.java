package com.deliverooo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.deliverooo.domain.Order;
import com.deliverooo.util.Util;

@Service("SimpleDeliveryETAService")
public class SimpleDeliveryETAService implements IDeliveryETAService{

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
			List<OrderGroup> combinations = createCombinations(orders, maxWeightPerVehicle);
			OrderGroup consignment = combinations.stream()
									.sorted(Comparator.comparing(OrderGroup::getSize)
													  .thenComparing(OrderGroup::getTotalWeight)
													  .thenComparing(OrderGroup::getMaxDistance).reversed())
									.findFirst()
									.get();
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
	
	private List<OrderGroup> createCombinations(List<Order> orders, int maxWeightPerVehicle) {
		
		List<OrderGroup> groups = new ArrayList<>();
		groups.add(new OrderGroup(Collections.emptyList(), 0));
		
		for (Order order : orders) {
			List<OrderGroup> newGroups = new ArrayList<>();
			
			for (OrderGroup group : groups) {
				if (group.totalWeight + order.getPkg().getWeight() <= maxWeightPerVehicle) {
					List<Order> newList = new ArrayList<>(group.orders);
					newList.add(order);
					newGroups.add(new OrderGroup(newList, group.totalWeight + order.getPkg().getWeight()));
				}
			}
			
			groups.addAll(newGroups);
		}
		
		return groups;
	}

}
