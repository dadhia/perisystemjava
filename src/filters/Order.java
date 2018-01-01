package filters;

import java.util.Date;

/**
 * Class Order.
 * @author akash
 */
public class Order {
	
	public enum Action {
		BUY, SELL
	}
	
	//fields that are input//
	private Action action;
	private int amount;
	private String symbol;
	private String exchange;
	private double price;
	private Date executionDate;
	
	//fields that are not input//
	private Date orderCreationDate;
	 
	/**
	 * Constructor.
	 * @param action Order.Action
	 * @param amount int
	 * @param symbol String
	 * @param price double
	 * @param executionDate Date
	 */
	public Order(Action action, int amount, String symbol, double price, Date executionDate) {
		this.orderCreationDate = new Date(System.currentTimeMillis());
		this.amount = amount;
		this.action = action;
		this.symbol = symbol; 
		this.price = price;
		this.executionDate = executionDate;
	}
	
	public Date getOrderCreationDate() {
		return orderCreationDate;
	}

	/**
	 * Provides string representation of order details.
	 * @return String
	 */
	@Override
	public String toString() {
		String stringOutput = null;
		switch (this.action) {
			case BUY: 	stringOutput = "BUY"; break;
			case SELL: 	stringOutput = "SELL"; break;
			default:	stringOutput = "";
		}
		return stringOutput + " " + amount + " " + symbol + " " + exchange + " " + price + " " + executionDate;
	}
	
	public static void main(String[] args) {
		Order ord = new Order(Order.Action.BUY, 100, "AAPL", 99.99, new Date(System.currentTimeMillis()));
		System.out.println(ord);
	}
}
