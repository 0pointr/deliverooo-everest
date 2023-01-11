package com.deliverooo.io;

import com.deliverooo.domain.Order;
import com.deliverooo.domain.Package;
import com.deliverooo.exception.UnrecognisedOrderFormatException;

public class SimpleOrderParser implements IOrderParser {

	@Override
	public Order parse(String line, String delimiter) throws UnrecognisedOrderFormatException {
		
		String[] tokens = line.split(delimiter);
		try {
			return new Order(
						new Package(tokens[0], Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2])),
						tokens[3]
					);
		} catch (Exception e) {
			throw new UnrecognisedOrderFormatException(line, e);
		}
		
	}

}
