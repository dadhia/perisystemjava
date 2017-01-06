package stockGenie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class StrategyOne extends Strategy {

	/**
	 * Strategy One:
	 * Start with any index and filter out stocks that meet the following
	 * criterion:
	 * P/Tangible Book Value < 5
	 * P/EBITDA < 10
	 */
	BloombergAPICommunicator bloomberg;
	
	public StrategyOne(String stratName, BloombergAPICommunicator bloomberg){
		super(stratName);
		this.bloomberg = bloomberg;
	}
	
	
	@Override
	public void execute(ClientGUI clientGUI) {
		try {
			PrintWriter pw = new PrintWriter(new File("StrategyOneResult.txt"));
			bloomberg.requestStockDetails(BloombergAPICommunicator.Strategies.FUNDAMENTALS_ONE);
			//get all the technical data
			bloomberg.requestHistoricalPriceData(
					BloombergAPICommunicator.HistoricalRequest.ALL,
					"20160105","20170105");
			Core c = new Core();
			
			pw.println("****Beginning Strategy One*****");
			//Price/Tang. Book Value
			for (Stock s: clientGUI.getStockUniverse().getStocks()) {
				pw.println("Stock: " + s.companyName + " with ticker " + s.ticker);
				pw.flush();
				if (s.pTangBV <= 5.0) {
					pw.println("Passed the book value filter");
					pw.flush();
					if (s.pEbitda <= 10.0) {
						pw.println("Passed the EBITDA filter");
						pw.flush();
						MInteger outBegin65 = new MInteger();
						MInteger outLength65 = new MInteger();
						double [] sma65 = new double[s.px_close.length];
						RetCode rc65 = c.sma(0, s.px_close.length-1, s.px_close, 65, outBegin65, outLength65, sma65);
						pw.println("65 SMA Calculated: " + sma65[outLength65.value-1] + " with out length of " + outLength65.value);
						pw.flush();
						MInteger outBegin20 = new MInteger();
						MInteger outLength20 = new MInteger();
						double [] sma20 = new double[s.px_close.length];
						RetCode rc25 = c.sma(0, s.px_close.length-1, s.px_close, 20, outBegin20, outLength20, sma20);
						pw.println("65 SMA Calculated: " + sma20[outLength20.value-1] + " with out length of " + outLength20.value);
						pw.flush();
						//price is less than 20 AND 65 SMAs
						
						if ((s.price <= sma20[outLength20.value-1]) && (s.price <= sma65[outLength65.value-1])) {
							pw.println("Price (" + s.price + ") is lower than sma 20 and sma 65");
							pw.flush();
							double [] ad = new double [s.px_close.length];
							MInteger outBeginAD = new MInteger();
							MInteger outLengthAD = new MInteger();
							RetCode rcad = c.ad(0, s.px_close.length-1, s.px_high, s.px_low, s.px_close, s.volume, outBeginAD, outLengthAD, ad);
							pw.println("Calculated A/D with array length of " + outLengthAD.value);
							pw.flush();
							MInteger outBeginOBV = new MInteger();
							MInteger outLengthOBV = new MInteger();
							double [] obv = new double[s.px_close.length];
							RetCode rcobv = c.obv(0, s.px_close.length-1, s.px_close, s.volume, outBeginOBV, outLengthOBV, obv);
							pw.println("Calculated A/D with array length of " + outLengthOBV.value);
							pw.flush();
							boolean obvBullish = false;
							boolean adBullish = false;
							if ((ad[outLengthAD.value-1] >= sma65[outLength65.value-1]) && 
								(ad[outLengthAD.value-1] >= sma20[outLength20.value-1])) {
								adBullish = true;
								pw.println("A/D Line indicates bullish divergence.");
							}
							else {
								pw.println("A/D Line does not show bullish divergence.");
							}
							pw.flush();
							if ((obv[outLengthOBV.value-1] >= sma65[outLength65.value-1]) &&
								(obv[outLengthOBV.value-1] >= sma20[outLength20.value-1])) {
								obvBullish = true;
								pw.println("OBV Line indicates bullish divergence.");
							}
							else {
								pw.println("OBV Line does not show bullish divergence");
							}
							pw.flush();
							if (obvBullish && adBullish) {
								s.status = Stock.Status.BUY;
								pw.println("You should buy this stock!");
								pw.flush();
							}
						}
						else {
							pw.println("Price (" + s.price + ") is higher than SMA 20 and SMA 65");
							pw.flush();
						}
					}
					else {
						pw.println("Had P/EBITDA > 10.0");
						pw.flush();
					}
				}
				else {
					pw.println("Had P/Tang. BVal > 5.0");
					pw.flush();
				}
			}
			
			pw.println("-------------");
			pw.println("FINAL RESULTS");
			pw.println("BUY: ");
			for (Stock s: clientGUI.getStockUniverse().getStocks()) {
				if(s.status == Stock.Status.BUY) {
					pw.print(s.ticker + " ");
				}
			}
			pw.println("Strategy Complete");
			pw.flush();
			pw.close();
			
		} catch (FileNotFoundException e1) {
			clientGUI.makeUpdate("FNFE", 3, "Strategy 1");
		} catch (IOException e) {
			clientGUI.makeUpdate("IOE", 3, "Strategy 1");
		}

	}

}
