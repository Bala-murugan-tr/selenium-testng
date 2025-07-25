package com.trgan.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.trgan.exceptions.ExcelException;
import com.trgan.framework.config.FrameworkProperties;
import com.trgan.framework.context.TestContextManager;
import com.trgan.framework.enums.DataStrategy;

/**
 * Utiliy class to handle excel
 */
public class ExcelReader {
	private Workbook workbook = null;

	/**
	 * Creating ExcelReader instance will read the excel file for the current
	 * testcase based on {@link DataStrategy}.
	 */
	public ExcelReader() {
		String filePath = resolvedExcelFilePath();
		try (FileInputStream fis = new FileInputStream(filePath)) {
			this.workbook = WorkbookFactory.create(fis);
		} catch (IOException e) {
			throw new ExcelException("Failed to load Excel file: " + filePath);
		}
		TestContextManager.getContext().getReportContext().getLogger().log("EXCEL DATA PATH : " + filePath);
	}

	/**
	 * method to refine the excel file path based on provided {@link DataStrategy}.
	 *
	 * @return excel file full path
	 */
	private static String resolvedExcelFilePath() {
		DataStrategy type = FrameworkProperties.getDataStrategy();
		String base = FrameworkProperties.getDataDir();
		var testName = TestContextManager.getContext().getMetaData().getTestClassName();
		var groupName = TestContextManager.getContext().getMetaData().getTestGroup();
		switch (type) {
		case DataStrategy.INDIVIDUAL:
			return Paths.get(base, testName + ".xlsx").toString();

		case DataStrategy.MODULAR:
			return Paths.get(base, groupName + ".xlsx").toString();

		case DataStrategy.SINGLE:
			return Paths.get(base, "data.xlsx").toString();

		case DataStrategy.MODULAR_INDIVIDUAL:
			return Paths.get(base, groupName, testName + ".xlsx").toString();

		default:
			throw new ExcelException("Unsupported DataStrategy: " + type);
		}
	}

	public Map<String, String> getFirstSheetData(String keyColumn, String valueColumn) {
		String firstSheetName = workbook.getSheetAt(0).getSheetName();
		return getSheetData(workbook, firstSheetName, keyColumn, valueColumn);
	}

	public static Map<String, String> getSheetData(Workbook wb, String sh, String keyColumn, String valueColumn) {
		final int referenceColNum = 0; // first column
		int testcaseColNum = -1;
		Sheet sheet = wb.getSheet(sh);
		Map<String, String> testCaseDataMap = new HashMap<>();
		DataFormatter formatter = new DataFormatter();

		if (sheet == null) {
			throw new ExcelException("ExcelSheet [" + sh + "] not found.");
		}

		Row headerRow = sheet.getRow(0);
		if (headerRow == null) {
			throw new ExcelException("Header row not found in sheet: [" + sh + "]");
		}

		for (int i = 0; i < headerRow.getLastCellNum(); i++) {
			Cell headerCell = headerRow.getCell(i);
			String header = formatter.formatCellValue(headerCell).trim();
			if (headerCell != null && header.equalsIgnoreCase(valueColumn.trim())) {
				testcaseColNum = i;
				break;
			}
		}

		if (testcaseColNum == -1) {
			throw new ExcelException("Given reference column [" + valueColumn + "] not present");
		}

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				String referenceName = formatter.formatCellValue(row.getCell(referenceColNum)).trim();
				String cellData = formatter.formatCellValue(row.getCell(testcaseColNum)).trim();

				if (!referenceName.isEmpty()) {
					testCaseDataMap.put(referenceName, cellData);
				}
			}
		}

		return testCaseDataMap;

	}

	public Workbook getWorkBook() {
		return workbook;
	}

}
