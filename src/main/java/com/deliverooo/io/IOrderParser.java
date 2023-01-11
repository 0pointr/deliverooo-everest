package com.deliverooo.io;

import com.deliverooo.domain.Order;
import com.deliverooo.exception.UnrecognisedOrderFormatException;

public interface IOrderParser {

	public Order parse(String line, String delimiter) throws UnrecognisedOrderFormatException;
	
}
