package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.table.DefaultTableModel;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

/**
 * Class StockUniverse.
 * Used for maintaining the stocks in a filter through the filter life cycle.
 * Required by BloombergAPICommunicator to collect stock ticker names and data.
 * Inherit from this class if you are using a child class of Stock in your strategy.
 * @author devan
 */
public class StockUniverse {
	protected Stock [] universe;
	
	public void buildUniverse(String [] tickers, int numberOfStocks) {
		universe = new Stock[numberOfStocks];
		for (int i = 0; i < universe.length; i++) {
			universe[i] = new Stock();
			universe[i].ticker = tickers[i];
		}
			
	}
	
	/**
	 * Gets the stocks in this universe.
	 * @return
	 */
	public Stock [] getStocks() {
		return universe;
	}
}
