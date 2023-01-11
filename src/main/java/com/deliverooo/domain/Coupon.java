package com.deliverooo.domain;

public class Coupon {

	private String code;
	private double discount;
	private int minWeight;
	private int maxWeight;
	private int minDistance;
	private int maxDistance;
	
	public Coupon() {}
	public Coupon(String code, double discount, int minWeight, int maxWeight, int minDistance, int maxDistance) {
		super();
		this.code = code;
		this.discount = discount;
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getMinWeight() {
		return minWeight;
	}
	public void setMinWeight(int minWeight) {
		this.minWeight = minWeight;
	}
	public int getMaxWeight() {
		return maxWeight;
	}
	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}
	public int getMinDistance() {
		return minDistance;
	}
	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}
	public int getMaxDistance() {
		return maxDistance;
	}
	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coupon other = (Coupon) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Coupon [code=" + code + ", discount=" + discount + ", minWeight=" + minWeight + ", maxWeight="
				+ maxWeight + ", minDistance=" + minDistance + ", maxDistance=" + maxDistance + "]";
	}
	
	
}
