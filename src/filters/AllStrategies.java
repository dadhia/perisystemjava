package filters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import data.BloombergAPICommunicator;
import data.StockUniverse;
import stockGenie.ExcelOutput;

/**
 * Class to run all strategies and deliver one combined excel report.
 * We will build strategies and each one will have its own sheet in
 * the excel report.
 * @author devan
 */
public class AllStrategies {

	public static void main(String [] args) {
		try {
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			Date dateobj = new Date();
			
			PrintWriter pw = new PrintWriter(new File("devanLog"+ ".txt")); 
			pw.println("This run was started on: " + df.format(dateobj));
			pw.flush();
			pw.println("Trying to connect to bloomberg");
			pw.flush();
			BloombergAPICommunicator bloomberg = new BloombergAPICommunicator(pw, BloombergAPICommunicator.Index.SP500);
			pw.println("Connected to bloomberg");
			pw.flush();
			pw.println("Received index members");
			pw.flush();
			
			ExcelOutput excel = new ExcelOutput();
			FilterA strategyA = new FilterA(bloomberg);
			FilterB strategyB = new FilterB(bloomberg);
			strategyA.run(bloomberg.getStockUniverse(), pw, excel);
			strategyB.run(bloomberg.getStockUniverse(), pw, excel);
			excel.writeToFile("SP500-test.xls");
			pw.close();
		} catch (InterruptedException | IOException e) {
			return;
		}
	}
}
