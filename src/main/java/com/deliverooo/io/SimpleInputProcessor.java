package com.deliverooo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deliverooo.app.Deliveroo;
import com.deliverooo.domain.Order;
import com.deliverooo.domain.VehicleInfo;
import com.deliverooo.exception.InvalidInputException;
import com.deliverooo.exception.UnrecognisedOrderFormatException;

@Component
public class SimpleInputProcessor implements IInputProcessor {

	@Value("${delimiter:[ ]+}")
	private String DELIMITER;
	
	@Override
	public ProcessedInput process(InputStream stream, Charset charset) throws InvalidInputException {
		try {
			List<String> lines = getInput(stream, charset);
			if (lines.size() <= 1) {
				throw new InvalidInputException("Insufficient Input");
			}
			
			Double baseCost = getBaseCostInput(lines.get(0));
			List<Order> orders = getOrdersInput(lines.subList(1, lines.size()-1));
			VehicleInfo vehicle = getVehicleInfoInput(lines.get(lines.size()-1));
			
			return new ProcessedInput(baseCost, orders, vehicle);
		} catch (UnrecognisedOrderFormatException ex) {
			throw new InvalidInputException("Input cannot be processed", ex);
		} catch (IOException ex) {
			throw new InvalidInputException("Error while acquiring input", ex);
		}
		
	}

	private List<String> getInput(InputStream stream, Charset charset) throws IOException {
		
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset))) {
			String nextLine;
			do {
				nextLine = reader.readLine();
				if (nextLine != null && nextLine.trim().length() > 0)
					lines.add(nextLine);
			} while (nextLine != null && nextLine.trim().length() > 0);
		}
		
		return lines;
	}

	private Double getBaseCostInput(String line) throws UnrecognisedOrderFormatException {
		try {
			return Double.valueOf( line.trim().split(DELIMITER )[0] );
		} catch (Exception ex) {
			throw new UnrecognisedOrderFormatException("Could not read base delivery cost: " + line);
		}
	}
	
	private List<Order> getOrdersInput(List<String> lines) throws UnrecognisedOrderFormatException {
		final List<Order> orders = new ArrayList<>();
		final IOrderParser orderParser = new SimpleOrderParser();
		for (String line : lines) {
			orders.add(orderParser.parse(line, DELIMITER));
		}
		return orders;
	}
	
	private VehicleInfo getVehicleInfoInput(String line) throws UnrecognisedOrderFormatException {
		try {
			String[] tokens = line.split(DELIMITER);
			return new VehicleInfo(Integer.valueOf(tokens[0]), Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2]));
		} catch (Exception ex) {
			throw new UnrecognisedOrderFormatException("Could not read vehicle info: " + line);
		}
	}
}
