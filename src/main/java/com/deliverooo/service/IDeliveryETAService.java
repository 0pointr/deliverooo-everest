package com.deliverooo.service;

import java.util.List;

import com.deliverooo.domain.Order;

public interface IDeliveryETAService {

	List<Order> getEstimates(int vehicleCount, int maxWeightPerVehicle,
									int vehicleMaxSpeed, List<Order> orders);
}
