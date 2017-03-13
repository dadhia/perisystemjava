package strategiesNoGUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import stockGenie.BloombergAPICommunicator;
import stockGenie.StockUniverse;

/**
 * Class to run all strategies and deliver one combined excel report.
 * We will build strategies and each one will have its own sheet in
 * the excel report.
 * @author devan
 */
public class AllStrategies {

	public static void main() {
		try {
			BloombergAPICommunicator bloomberg = new BloombergAPICommunicator();
			bloomberg.getIndexMembers(BloombergAPICommunicator.Index.SP500);
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			Date dateobj = new Date();
			
			PrintWriter pw = new PrintWriter(new File(df.format(dateobj))); 
			pw.println("This run was started on: " + df.format(dateobj));
		
			StrategyA strategyA = new StrategyA(bloomberg);
			strategyA.run(bloomberg.getStockUniverse(), pw);
		} catch (InterruptedException | IOException e) {
			return;
		}
	}
}
