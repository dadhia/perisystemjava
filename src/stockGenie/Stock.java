package stockGenie;

public class Stock {

	public enum Status {
		NO_STATUS, BUY, SELL, NO_DATA
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
	public double svOne, svTwo, svThree, svFour;
	
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
		rowData[2] = price;
		rowData[3] = pe;
		return rowData;
	}
}
