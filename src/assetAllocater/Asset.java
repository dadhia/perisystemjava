package assetAllocater;


import data.BloombergAPICommunicator;
import data.BloombergAPICommunicator.Sector;

public class Asset {
	
	private double price;
	private int amount;
	private String ticker;
	private Sector sector; 
	private double origValue;
	
	//Rule 1 fields 
	private double lossLimitCutoff;
		
	//Rule 2 fields
	private double growthLimitCutoff; 
		
	//Rule 3 fields
	private double portPortionCutoff;
	private double portPortionAfterReduction;
	
	
	public Asset(double price, int amount, String ticker, Sector sector) {
		super();
		this.price = price;
		this.amount = amount;
		this.ticker = ticker;
		this.origValue = amount * price;
		this.sector = sector;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public double getOrigValue() {
		return origValue;
	}
	
	public double valueNow() {
		return amount * price;
	}
	
	public BloombergAPICommunicator.Sector getSector() {
		return sector;
	}

	public double getLossLimitCutoff() {
		return lossLimitCutoff;
	}

	public void setLossLimitCutoff(double lossLimitCutoff) {
		this.lossLimitCutoff = lossLimitCutoff;
	}

	public double getPortPortionCutoff() {
		return portPortionCutoff;
	}

	public void setPortPortionCutoff(double portPortionCutoff) {
		this.portPortionCutoff = portPortionCutoff;
	}

	public double getPortPortionAfterReduction() {
		return portPortionAfterReduction;
	}

	public void setPortPortionAfterReduction(double portPortionAfterReduction) {
		this.portPortionAfterReduction = portPortionAfterReduction;
	}

	public double getGrowthLimitCutoff() {
		return growthLimitCutoff;
	}

	public void setGrowthLimitCutoff(double growthLimitCutoff) {
		this.growthLimitCutoff = growthLimitCutoff;
	}

}
