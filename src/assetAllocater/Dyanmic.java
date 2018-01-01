package assetAllocater;


import java.util.Date;
import filters.Order;
import stockGenie.BloombergAPICommunicator;

public class Dyanmic extends Allocator {
	
	public void dailyRun(BloombergAPICommunicator bloomberg) {
		//get asset names and amounts from trader's workstation
		this.getPortfolio();
		//compare values analyze them for price increase and decrease sell rules and “trimming” sell rule 
		for (Asset a: assets) {
			//Rule 1: Sell stock if it drops 8% its original value//
			if (a.valueNow() < (a.getOrigValue() * (1 - getLossLimitCutoff(a)))) {
				orderList.add(new Order(Order.Action.SELL, a.getAmount(), a.getTicker(), a.getPrice(), new Date(System.currentTimeMillis())));
			}
			//Rule 2: Sell stock if it grows greater than its 20% of its original value//
			if (a.valueNow() > (a.getOrigValue() * (1 + getGrowthLimitCutoff(a)))) {
				orderList.add(new Order(Order.Action.SELL, a.getAmount(), a.getTicker(), a.getPrice(), new Date(System.currentTimeMillis())));
			}
			//Rule 3: If a stock represents more than 20% of the total portfolio value, sell enough of that stock so that it represents only 15% //
			if ((a.valueNow() / currentBalance) > getPortPortionCutoff(a)) {
				int sellAmount = (int) ((currentBalance * getPortPortionAfterReduction(a)) / ((1 + getPortPortionAfterReduction(a)) * a.getPrice()));
				orderList.add(new Order(Order.Action.SELL, sellAmount, a.getTicker(), a.getPrice(), new Date(System.currentTimeMillis())));
			}
		}
	}
		
	public double getLossLimitCutoff(Asset a) {
		return 0; 
		// TODO use sql call to get the value, remove 0
	}
	
	public double getGrowthLimitCutoff(Asset a) {
		return 0; // TODO use sql call to get the value, remove 0
	}
	
	public double getPortPortionCutoff(Asset a) {
		return 0; // TODO use sql call to get the value, remove 0
	}
	
	public double getPortPortionAfterReduction(Asset a) {
		return 0; // TODO use sql call to get the value, remove 0
	}
		
	public static void main(String[] args) {
	}
	
}
