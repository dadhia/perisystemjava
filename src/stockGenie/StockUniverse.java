package stockGenie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.table.DefaultTableModel;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class StockUniverse {

	private Stock [] universe;
	private Core c;
	
	public StockUniverse(int numberOfStocks) {
		universe = new Stock[numberOfStocks];
		for (int i = 0; i < universe.length; i++) {
			universe[i] = new Stock();
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
	
	public void ad() {
		try {
			PrintWriter pw = new PrintWriter(new File("ad.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Stock s: universe) {
			MInteger outBeginIndex = new MInteger();
			MInteger outLength = new MInteger();
			s.ad = new double[s.px_close.length];
			RetCode rc = c.ad(0, s.px_close.length-1, s.px_high, s.px_low, s.px_close, s.volume, outBeginIndex, outLength, s.ad);
			if (rc == RetCode.Success) {
			}
		}
	}
}
