package com.trgan.framework.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.trgan.framework.config.FrameworkProperties;

public class TestLogger {
	private final Path filePath;
	private final StringBuffer buffer = new StringBuffer();

	/**
	 * Create a log file for the current test
	 * 
	 * @param testName
	 */
	public TestLogger(String testName) {
		String baseDir = FrameworkProperties.getReportDir() + File.separator + testName;
		filePath = Paths.get(baseDir, testName + ".log");
		try {
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, "".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write the message to the current test's log file
	 * 
	 * @param message
	 */
	public synchronized void log(String message) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		buffer.append('[').append(timestamp).append("]- ").append(message).append(System.lineSeparator());
	}

	/** Call this once per test to write all buffered lines in one I/O. */
	public void flush() {
		try {
			Files.writeString(filePath, buffer.toString(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.err.println("Log flush failed: " + e.getMessage());
		}
	}
}
