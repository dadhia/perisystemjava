package stockGenie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.table.DefaultTableModel;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class StockUniverse {
	private PrintWriter pw; 
	private Stock [] universe;
	private Core c;
	
	public StockUniverse(int numberOfStocks) {
		universe = new Stock[numberOfStocks];
		for (int i = 0; i < universe.length; i++) {
			universe[i] = new Stock();
		}
		c = new Core();	//make a new Core---this is necessary to use ta-lib
		try {
			pw = new PrintWriter(new File("TA-OUTPUT.txt"));
			pw.println("IS THIS WRITING?");
			pw.flush();
		} catch (FileNotFoundException e) {

		}
	}
	
	public void setTickers(String [] tickers) {
		for (int i = 0; i < universe.length; i++)
			universe[i].ticker = tickers[i];
	}
	
	public void refreshTable(DefaultTableModel model) {
		//remove all the rows
		while(model.getRowCount() > 0)
			model.removeRow(0);
		for (int i = 0; i < universe.length; i++)
			model.addRow(universe[i].retrieveRowData());
	}
	
	public Stock [] getStocks() {
		return universe;
	}
	
	/**
	 * Accumulation-Distribution function.  This will calculate
	 * the AD line based on the historical data requested.
	 */
	/**
	public void ad(ClientGUI clientGUI) {
			for (Stock s: universe) {
				MInteger outBeginIndex = new MInteger();
				MInteger outLength = new MInteger();
				s.ad = new double[s.px_close.length];
				pw.println("Allocated space for the output data");
				clientGUI.makeUpdate("Reached checkpoint 1", 0, "TA-LIB");
				//use the ta library to form the A/D line
				pw.println(s.px_close);
				pw.println("PX CLOSE ARRAY");
				for (int i = 0; i < s.px_close.length; i++) {
					pw.println(i + " " + s.px_close[i]);
				}
				pw.println(s.px_high);
				pw.println("PX HIGH ARRAY");
				for (int i = 0; i < s.px_close.length; i++) {
					pw.println(i + " " + s.px_high[i]);
				}
				pw.println(s.px_low);
				pw.println("PX LOW ARRAY");
				for (int i = 0; i < s.px_close.length; i++) {
					pw.println(i + " " + s.px_low[i]);
				}
				pw.println(s.volume);
				pw.println("VOLUME ARRAY");
				for (int i = 0; i < s.volume.length; i++) {
					pw.println(i + " " + s.volume[i]);
				}
				pw.println(s.ad);
				pw.flush();
				RetCode rc = c.ad(0, s.px_close.length-1, s.px_high, s.px_low, s.px_close, s.volume, outBeginIndex, outLength, s.ad);
				//RetCode rc = c.sma(0, 100, s.px_close, 30, outBeginIndex, outLength, s.ad);
				clientGUI.makeUpdate("Reached checkpoint 2", 0, "TA-LIB");
				if (rc == RetCode.Success) {
					pw.println("Ret Code success");
					pw.println("Stock: " + s.companyName);
					for (int i = 0; i < s.ad.length; i++) {
						pw.println(i + " " + s.ad[i]);
					}
					pw.flush();
				}
			}
	}*/
}
