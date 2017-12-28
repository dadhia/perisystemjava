package filters;

import java.io.PrintWriter;
import java.util.Date;
import data.BloombergAPICommunicator;
import data.StockUniverse;
import stockGenie.ExcelOutput;

/**
 * Abstract class for all strategies.
 * @author akash
 */
abstract public class Filter {

	protected BloombergAPICommunicator bloomberg;
	protected Order [] orderList;
	
	/**
	 * Constructor
	 * @param bloomberg BloombergAPICommunicator
	 */
	public Filter(BloombergAPICommunicator bloomberg) {
		this.bloomberg = bloomberg;
	}
	
	/**
	 * Runs strategy.  Implemented by inheriting classes.
	 * @param stocks StockUniverse
	 * @param pw PrinteWriter
	 * @param excel ExcelOutput
	 */
	public abstract void run(StockUniverse stocks, PrintWriter pw, ExcelOutput excel);
	
	/**
	 * Prints all orders to the PrintWriter provided.
	 * @param pw PrintWriter
	 */
	public void printOrders(PrintWriter pw) {
		for (Order ord: orderList) {
			pw.println(ord);
		}
		pw.flush();
	}
	
	/**
	 * Gets order list.
	 * @return Order []
	 */
	public Order [] getOrderList() {
		return orderList;
	}
	
	/**
	 * Runs multiple strategies.
	 * @param strategyArray StrategyAbstractClass []
	 * @param stocks StockUniverse
	 * @param pw PrintWriter
	 * @param excel ExcelOutput
	 */
	public static void runMultiple(Filter[] strategyArray, StockUniverse stocks, PrintWriter pw, ExcelOutput excel) {
		for (Filter strat: strategyArray) {
			strat.run(stocks, pw, excel);
			strat.printOrders(pw);
		}
	}
	
	/**
	 * Testbench.
	 * @param args
	 */
	public static void main(String [] args) {
		Filter testFilter = new Filter(null) {
			@Override
			public void run(StockUniverse stocks, PrintWriter pw, ExcelOutput excel) {
				this.orderList = new Order[4];
				orderList[0] = new Order(Order.Action.BUY, 100, "AAPL", "NYSE", 99.99, new Date(System.currentTimeMillis()));
				orderList[1] = new Order(Order.Action.BUY, 200, "XXX", "NYSE", 99.99, new Date(System.currentTimeMillis()));
				orderList[2] = new Order(Order.Action.SELL, 300, "YYY", "NYSE", 99.99, new Date(System.currentTimeMillis()));
				orderList[3] = new Order(Order.Action.SELL, 400, "ZZZ", "NYSE", 99.99, new Date(System.currentTimeMillis()));
			}
		};
		testFilter.run(null, null, null);
		PrintWriter pw = new PrintWriter(System.out);
		testFilter.printOrders(pw);
		pw.close();
	}
}
