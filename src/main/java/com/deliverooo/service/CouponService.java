package com.deliverooo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.deliverooo.domain.Coupon;
import com.deliverooo.domain.Order;

@Service
public class CouponService {

	private final Map<String, Coupon> coupons = new HashMap<>();

    public CouponService registerCoupon(Coupon coupon) {
    	coupons.put(coupon.getCode(), coupon);
    	return this;
    }
    
    public double getCouponDiscount(Order order) {
    	if (order == null || order.getPkg() == null)
    		throw new IllegalArgumentException("Package cannot be null");
    	
    	if ( checkCouponValidity(order) == CouponValidity.VALID ) {
    		return coupons.get(order.getCouponCode()).getDiscount();
    	} else {
    		return 0d;
    	}
    }
    
    public CouponValidity checkCouponValidity(Order order) {
    	
    	if (order == null || order.getPkg() == null)
    		throw new IllegalArgumentException("Package cannot be null");
    	
    	if (order.getCouponCode() == null)
    		return CouponValidity.INVALID;
    	
    	String orderCouponCode = order.getCouponCode();
    	if (!coupons.containsKey(orderCouponCode))
    		return CouponValidity.UNKNOWN;
    	
    	Coupon orderCoupon = coupons.get(orderCouponCode);
    	
    	boolean valid = orderCoupon.getMinDistance() <= order.getPkg().getDistance()
	    			&&  orderCoupon.getMaxDistance() >= order.getPkg().getDistance()
	    			&&  orderCoupon.getMinWeight() <= order.getPkg().getWeight()
					&&  orderCoupon.getMaxWeight() >= order.getPkg().getWeight();
	    			
	    if (valid)
	    	return CouponValidity.VALID;
	    
	    return CouponValidity.INVALID;
    }

	public Coupon getCoupon(String couponCode) {
		return coupons.get(couponCode);
	}
}
