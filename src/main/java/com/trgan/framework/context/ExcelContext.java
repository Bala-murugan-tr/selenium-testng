package com.trgan.framework.context;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

public class ExcelContext {
	private Map<String, String> allData;
	private Workbook workbook;

	public ExcelContext(Map<String, String> allData, Workbook workbook) {
		this.allData = allData;
		this.workbook = workbook;
	}

	public Map<String, String> getAllData() {
		return allData;
	}

	public void setAllData(Map<String, String> allData) {
		this.allData = allData;
	}

	public void setWorkBook(Workbook workbook) {
		this.workbook = workbook;
	}

	public Workbook getWorkBook() {
		return this.workbook;
	}

}
