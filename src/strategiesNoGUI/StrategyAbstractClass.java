package strategiesNoGUI;

import stockGenie.BloombergAPICommunicator;
import stockGenie.StockUniverse;

abstract public class StrategyAbstractClass {

	protected BloombergAPICommunicator bloomberg;
	
	public StrategyAbstractClass(BloombergAPICommunicator bloomberg) {
		this.bloomberg = bloomberg;
	}
	
	abstract public void run(StockUniverse stocks, PrintWriter pw);
}
