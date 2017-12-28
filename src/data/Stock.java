package data;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Stock.
 * A container for various data requests. Other classes can inherit from this class and add
 * more data fields specific to their strategy.
 * @author devan
 */
public class Stock {

	public enum Status {
		NO_STATUS, BUY, SELL, NO_DATA
	}
	
	//Every stock has a ticker and a status
	public String ticker;
	public Status status;
	
	//holds key value pairs for the various data requested through Bloomberg
	public Map<data.BloombergAPICommunicator.DataRequest, Object> referenceData = 
			new HashMap<data.BloombergAPICommunicator.DataRequest, Object>();
	
	//holds data requested through Historical Data Requests
	public double [] px_high;
	public double [] px_low;
	public double [] px_open;
	public double [] px_close;
	public double [] volume;
	
	/**
	 * Constructor
	 */
	public Stock() {
		ticker = "";
		status = Stock.Status.NO_STATUS;
	}
}
