package stockGenie;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelOutput {
	private Workbook workbook;
	private Map<Integer, Sheet> sheets;
	private CreationHelper createHelp;
	private Map<String, Integer> sheetLookupTable;
	private Row row;
	
	public ExcelOutput () {
		workbook = new HSSFWorkbook();
		sheets = new Hashtable<Integer, Sheet>();
		sheetLookupTable = new Hashtable<String, Integer>();
		createHelp = workbook.getCreationHelper();
	}
	
	public void createSheet(String sheetName) {
		String safeSheetName = WorkbookUtil.createSafeSheetName(sheetName);
		Sheet sheet = workbook.createSheet(safeSheetName);
		Integer sheetNumber = sheets.size();
		sheets.put(sheetNumber, sheet);
	}
	
	public void addRow(String sheetName) {
		Integer sheetNumber = sheetLookupTable.get(sheetName);
		if (sheetNumber == null)
			return;
		addRow(sheetNumber);
	}
	
	public void addRow(Integer sheetNumber) {
		Sheet sheet = sheets.get(sheetNumber);
		if (sheet == null)
			return;
		int rowNumber = sheet.getPhysicalNumberOfRows();
		row = sheet.createRow(rowNumber);
	}
	
	public void addCell(double value) {
		createCell().setCellValue(value);
	}
	
	public void addCell(String s) {
		createCell().setCellValue(createHelp.createRichTextString(s));
	}
	
	private Cell createCell() {
		int cellNumber = row.getPhysicalNumberOfCells();
		return row.createCell(cellNumber);
	}
	
	
	public void writeToFile(String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			workbook.write(fos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		ExcelOutput test = new ExcelOutput();
		test.createSheet("testSheet");
		test.addRow(0);
		test.addCell(1.0);
		test.addCell(2);
		test.addRow(0);
		test.addCell("Hello");
		test.addCell("DOES THIS WORK?");
		test.writeToFile("test.xls");
	}
}