package com.deliverooo.domain;

public class VehicleInfo {

	private int count;
	private int maxSpeed;
	private int maxWeight;
	
	

	public VehicleInfo(int count, int maxSpeed, int maxWeight) {
		super();
		this.count = count;
		this.maxSpeed = maxSpeed;
		this.maxWeight = maxWeight;
	}

	
	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}
	
	
}
