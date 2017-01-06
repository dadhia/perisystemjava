package stockGenie;

public class Stock {

	public enum Status {
		NO_STATUS, BUY, SELL
	}

	public String ticker;
	public String companyName;
	public double pe;
	public double price;
	public double [] px_high;
	public double [] px_low;
	public double [] px_open;
	public double [] px_close;
	public double [] volume;
	public double [] ad;
	public Status status;
	
	public double pEbitda, pTangBV;
	
	public Stock() {
		ticker = "";
		companyName = "";
		pe = 0;
		price = 0;
		status = Stock.Status.NO_STATUS;
	}
	
	public Object [] retrieveRowData() {
		Object [] rowData = new Object[4];
		rowData[0] = ticker;
		rowData[1] = companyName;
		rowData[3] = price;
		rowData[2] = pe;
		return rowData;
	}
}
