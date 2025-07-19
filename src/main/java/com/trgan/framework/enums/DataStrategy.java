package com.trgan.framework.enums;

/**
 * Defines how test data (Excel files) are organized and resolved for each test.
 */
public enum DataStrategy {
	/**
	 * Each @Test method has its own dedicated Excel file under testdata/ Example:
	 * TC01.xlsx, TC02.xlsx Ideal for small suites with isolated test logic.
	 */
	INDIVIDUAL,
	/**
	 * One Excel file per module or group. All @Tests within a group share the same
	 * data sheet. Example: ABC.xlsx for group ABC containing TC01, TC02, TC03 Best
	 * suited for modular design with grouped logic.
	 */
	MODULAR,
	/**
	 * Each @Test has its own Excel file, but files are grouped into folders by
	 * module. Example: testdata/ABC/TC01.xlsx, testdata/ABC/TC02.xlsx Ideal for
	 * large frameworks with both isolation and modular classification.
	 */
	MODULAR_INDIVIDUAL,
	/**
	 * A single, central Excel file for all test cases. Example: data.xlsx contains
	 * data for TC01, TC02, TC03 Works well for small projects or unified datasets.
	 */
	SINGLE;
}
