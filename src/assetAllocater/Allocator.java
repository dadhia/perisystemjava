package assetAllocater;

import java.util.ArrayList;

import filters.Order;
import data.BloombergAPICommunicator;

public abstract class Allocator {
	
	protected double currentBalance;
	protected ArrayList<Asset> assets;
	protected ArrayList<Order> orderList;
	
	public abstract void dailyRun(BloombergAPICommunicator bloomberg);
	 
	public void getPortfolio() {
		boolean stocksLeft = true; 
		// TODO check in TWS if there are any stocks left, delete line above
		while (stocksLeft) {
			assets.add(new Asset(currentBalance, 0, null, null));
			//TODO create new Asset based on data from TWS
			}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
