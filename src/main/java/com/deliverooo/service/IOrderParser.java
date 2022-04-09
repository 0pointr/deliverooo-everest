package com.deliverooo.service;

import com.deliverooo.domain.Order;
import com.deliverooo.exception.UnrecognisedOrderFormatException;

public interface IOrderParser {

	public Order parse(String line) throws UnrecognisedOrderFormatException;
	
}
