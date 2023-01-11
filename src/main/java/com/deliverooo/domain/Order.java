package com.deliverooo.domain;

public class Order {
	
	private Package pkg;
	private Coupon coupon;
	private double discount;
	private double cost;
	private double etaHours;
	private String couponCode;
	
	public Order(Package pkg, String couponCode) {
		super();
		this.pkg = pkg;
		this.couponCode = couponCode;
	}
	
	public Order(Package pkg, Coupon coupon) {
		super();
		this.pkg = pkg;
		this.coupon = coupon;
	}
	
	public Order(Package pkg, Coupon coupon, double etaMinutes) {
		super();
		this.pkg = pkg;
		this.coupon = coupon;
		this.etaHours = etaMinutes;
	}

	public Package getPkg() {
		return pkg;
	}
	public void setPkg(Package pkg) {
		this.pkg = pkg;
	}
	public String getPackageName() {
		return this.pkg.getPkgName();
	}
	public Coupon getCoupon() {
		return coupon;
	}
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
	public double getEtaHours() {
		return etaHours;
	}
	public void setEtaHours(double eta) {
		this.etaHours = eta;
	}
	
	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	
	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
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
		Order other = (Order) obj;
		if (pkg == null) {
			if (other.pkg != null)
				return false;
		} else if (!pkg.equals(other.pkg))
			return false;
		return true;
	}
	
	
}
