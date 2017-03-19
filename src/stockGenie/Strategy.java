package stockGenie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import com.ib.controller.ApiController.IFundamentalsHandler;

abstract public class Strategy {

	protected String stratName;
	protected StockUniverse universe;
	protected BloombergAPICommunicator bloomberg;
	
	public Strategy(String stratName){
		this.stratName = stratName;
	}
	
	public void outputResults(String filename, StockUniverse universe) {
		try {
			PrintWriter pw = new PrintWriter(new File(filename));
			Timestamp t = new Timestamp(System.currentTimeMillis());
			pw.println("Results for " + stratName + " at " + t);
			pw.println();
			pw.println("BUY LIST:");
			for (Stock s: universe.getStocks()) {
				if(s.status == Stock.Status.BUY)
					pw.println(s.ticker);
			}
			pw.println();
			pw.println("SELL LIST:");
			for (Stock s: universe.getStocks()) {
				if (s.status == Stock.Status.SELL)
					pw.println(s.ticker);
			}
			pw.println("----output complete----");
			pw.close();
		} catch (FileNotFoundException e) {
			return;
		}
	}

	abstract public void execute(ClientGUI clientGUI);
}

