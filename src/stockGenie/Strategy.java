package stockGenie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import TBD.ClientGUI;

/**
 * Abstract class used for implementing a simple strategy to filter stocks.
 */
abstract public class Strategy {

	//Member variables that can be utilized by any inheriting class
	protected String stratName;
	protected StockUniverse universe;
	protected BloombergAPICommunicator bloomberg;
	
	/**
	 * Constructor
	 * @param stratName String
	 */
	public Strategy(String stratName){
		this.stratName = stratName;
	}
	
	/**
	 * Outputs results to a specific file based on the contents of the stock universe.
	 * @param filename String
	 * @param universe StockUniverse
	 */
	public void outputResults(String filename, StockUniverse universe) {
		try {
			PrintWriter pw = new PrintWriter(new File(filename));
			Timestamp t = new Timestamp(System.currentTimeMillis());
			//First line will show the strategy name and current time
			pw.println("Results for " + stratName + " at " + t);
			pw.println();
			//Buy list
			pw.println("BUY LIST:");
			for (Stock s: universe.getStocks()) {
				if(s.status == Stock.Status.BUY)
					pw.println(s.ticker);
			}
			pw.println();
			//Sell list
			pw.println("SELL LIST:");
			for (Stock s: universe.getStocks()) {
				if (s.status == Stock.Status.SELL)
					pw.println(s.ticker);
			}
			//print a final line to show that execution completed as expected
			pw.println("----output complete----");
			pw.close();
		} catch (FileNotFoundException e) {
			return;
		}
	}

	/**
	 * Executest the strategy.
	 * @param clientGUI ClientGUI
	 */
	abstract public void execute(ClientGUI clientGUI);
}

