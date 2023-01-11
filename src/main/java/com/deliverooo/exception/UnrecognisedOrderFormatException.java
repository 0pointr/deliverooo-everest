package com.deliverooo.exception;

public class UnrecognisedOrderFormatException extends Exception {

	public UnrecognisedOrderFormatException(String message) {
        super(message);
    }
	
	public UnrecognisedOrderFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
