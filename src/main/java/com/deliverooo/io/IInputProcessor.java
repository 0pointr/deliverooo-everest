package com.deliverooo.io;

import java.io.InputStream;
import java.nio.charset.Charset;

import com.deliverooo.exception.InvalidInputException;

public interface IInputProcessor {
	public ProcessedInput process(InputStream stream, Charset charset) throws InvalidInputException;
}
