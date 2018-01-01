package filters;

import java.io.PrintWriter;
import java.util.Date;
import stockGenie.BloombergAPICommunicator;
import stockGenie.ExcelOutput;
import stockGenie.StockUniverse;

/**
 * Abstract class for all strategies.
 * @author akash
 */
abstract public class StrategyAbstractClass {

	protected BloombergAPICommunicator bloomberg;
	protected Order [] orderList;
	
	/**
	 * Constructor
	 * @param bloomberg BloombergAPICommunicator
	 */
	public StrategyAbstractClass(BloombergAPICommunicator bloomberg) {
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
	public static void runMultiple(StrategyAbstractClass[] strategyArray, StockUniverse stocks, PrintWriter pw, ExcelOutput excel) {
		for (StrategyAbstractClass strat: strategyArray) {
			strat.run(stocks, pw, excel);
			strat.printOrders(pw);
		}
	}
	
	/**
	 * Testbench.
	 * @param args
	 */
	public static void main(String [] args) {
		StrategyAbstractClass testStrategy = new StrategyAbstractClass(null) {
			@Override
			public void run(StockUniverse stocks, PrintWriter pw, ExcelOutput excel) {
				this.orderList = new Order[4];
				orderList[0] = new Order(Order.Action.BUY, 100, "AAPL", 99.99, new Date(System.currentTimeMillis()));
				orderList[1] = new Order(Order.Action.BUY, 200, "XXX", 99.99, new Date(System.currentTimeMillis()));
				orderList[2] = new Order(Order.Action.SELL, 300, "YYY", 99.99, new Date(System.currentTimeMillis()));
				orderList[3] = new Order(Order.Action.SELL, 400, "ZZZ", 99.99, new Date(System.currentTimeMillis()));
			}
		};
		testStrategy.run(null, null, null);
		PrintWriter pw = new PrintWriter(System.out);
		testStrategy.printOrders(pw);
		pw.close();
	}
}
