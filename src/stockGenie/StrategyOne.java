package stockGenie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

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
			PrintWriter pw = new PrintWriter(new File("ErrorLogStratOne.txt"));
			ExcelOutput excel = new ExcelOutput();
			excel.createSheet("OBV and A/D");
			bloomberg.requestStockDetails(BloombergAPICommunicator.Strategies.FUNDAMENTALS_ONE);
			//get all the technical data
			bloomberg.requestHistoricalPriceData(
					BloombergAPICommunicator.HistoricalRequest.ALL,
					"20170206","20160205");
			Core c = new Core();
			//Price/Tang. Book Value
			pw.println("Reached checkpoint 1"); pw.flush();
			for (Stock s: clientGUI.getStockUniverse().getStocks()) {
				if (s.status == Stock.Status.NO_DATA)
					continue;
				
				if ((s.pTangBV <= 5.0 && s.pTangBV >= 0.0) && 
				   (s.pEbitda <= 10.0 && s.pEbitda >= 0.0)) 
				{
					MInteger outBegin65 = new MInteger();
					MInteger outLength65 = new MInteger();
					double [] sma65 = new double[s.px_close.length];
					RetCode rc65 = c.sma(0, s.px_close.length-1, s.px_close, 65, outBegin65, outLength65, sma65);
					s.svOne = sma65[outLength65.value-1];				
					MInteger outBegin20 = new MInteger();
					MInteger outLength20 = new MInteger();
					double [] sma20 = new double[s.px_close.length];
					RetCode rc25 = c.sma(0, s.px_close.length-1, s.px_close, 20, outBegin20, outLength20, sma20);
					//price is less than 20 AND 65 SMAs
					s.svTwo = sma20[outLength20.value-1];
					if ((s.price <= s.svTwo) && (s.price <= s.svOne)) {
							
						double [] ad = new double [s.px_close.length];
						MInteger outBeginAD = new MInteger();
						MInteger outLengthAD = new MInteger();
						RetCode rcad = c.ad(0, s.px_close.length-1, s.px_high, s.px_low, s.px_close, s.volume, outBeginAD, outLengthAD, ad);
						s.svThree = ad[outLengthAD.value-1];
						MInteger outBeginOBV = new MInteger();
						MInteger outLengthOBV = new MInteger();
						double [] obv = new double[s.px_close.length];
						RetCode rcobv = c.obv(0, s.px_close.length-1, s.px_close, s.volume, outBeginOBV, outLengthOBV, obv);
						s.svFour = obv[outLengthOBV.value-1];
						boolean obvBullish = false;
						boolean adBullish = false;
						if ((s.svThree >= s.svOne) && (s.svThree >= s.svTwo)) {
							adBullish = true;
						}
						
						if ((s.svFour >= s.svOne) && (s.svFour >= s.svTwo)) {
								obvBullish = true;		
						}
						if (obvBullish && adBullish) {
							s.status = Stock.Status.BUY;
						}
					}
				}
				else if (s.pEbitda > 10.0 || s.pEbitda < 0) {
						
					//calculate the 65 Day Simple Moving Average (SMA)
					MInteger outBegin65 = new MInteger();
					MInteger outLength65 = new MInteger();
					double [] sma65 = new double[s.px_close.length];
					RetCode rc65 = c.sma(0, s.px_close.length-1, s.px_close, 65, outBegin65, outLength65, sma65);
					s.svOne = sma65[outLength65.value-1];					
					//calculate the 20 Day Simple Moving Average (SMA)
					MInteger outBegin20 = new MInteger();
					MInteger outLength20 = new MInteger();
					double [] sma20 = new double[s.px_close.length];
					RetCode rc25 = c.sma(0, s.px_close.length-1, s.px_close, 20, outBegin20, outLength20, sma20);
					s.svTwo = sma20[outLength20.value-1];
					//check the price
					if ((s.price >= sma20[outLength20.value-1]) &&
						(s.price >= sma65[outLength65.value-1])) {
						
						//Calculate the A/D line
						double [] ad = new double [s.px_close.length];
						MInteger outBeginAD = new MInteger();
						MInteger outLengthAD = new MInteger();
						RetCode rcad = c.ad(0, s.px_close.length-1, s.px_high, s.px_low, s.px_close, s.volume, outBeginAD, outLengthAD, ad);
						s.svThree = ad[outLengthAD.value-1];
						//Calculate the OBV -- On-Balance Volume
						MInteger outBeginOBV = new MInteger();
						MInteger outLengthOBV = new MInteger();
						double [] obv = new double[s.px_close.length];
						RetCode rcobv = c.obv(0, s.px_close.length-1, s.px_close, s.volume, outBeginOBV, outLengthOBV, obv);
						s.svFour = obv[outLengthOBV.value-1];
						boolean obvBearish = false;
						boolean adBearish = false;
						if ((ad[outLengthAD.value-1] < sma65[outLength65.value-1]) &&
							(ad[outLengthAD.value-1] < sma20[outLength20.value-1])) {
							adBearish = true;
						}
						if ((obv[outLengthOBV.value-1] < sma65[outLength65.value-1]) && 
							(obv[outLengthOBV.value-1] < sma20[outLength65.value-1])) {
							obvBearish = true;
						}
						if (obvBearish && adBearish) {
							s.status = Stock.Status.SELL;
						}
					}
				}
			}
			
			pw.println("Reached checkpoint 2"); pw.flush();
			excel.addRow(0);
			excel.addCell("Buy List:");
			buildColumnHeadings(excel);
			
			for (Stock s: clientGUI.getStockUniverse().getStocks())
				if(s.status == Stock.Status.BUY)
					printStockDetails(excel, s);

			excel.addRow(0);
			excel.addRow(0);
			excel.addCell("Sell List");			
			
			pw.println("Reached checkpoint 3"); pw.flush();
			for (Stock s: clientGUI.getStockUniverse().getStocks())
				if(s.status == Stock.Status.SELL)
					printStockDetails(excel, s);
			excel.addRow(0);
			excel.addRow(0);
			excel.addCell("No Data:");
			pw.println("Reached Checkpoint 4"); pw.flush();
			for (Stock s: clientGUI.getStockUniverse().getStocks())
				if (s.status == Stock.Status.NO_DATA)
				{
					excel.addRow(0);
					excel.addCell(s.ticker);
				}
			pw.println("Reached checkpoint 5"); pw.flush();
			excel.writeToFile("StrategyOne.xls");
			
			
		} catch (FileNotFoundException e1) {
			clientGUI.makeUpdate("FNFE", 3, "Strategy 1");
		} catch (IOException e) {
			clientGUI.makeUpdate("IOE", 3, "Strategy 1");
		}
		

	}

	private void buildColumnHeadings(ExcelOutput excel) {
		excel.addRow(0);
		excel.addCell("Ticker");
		excel.addCell("Price");
		excel.addCell("P/Tang. BV");
		excel.addCell("P/EBITDA");
		excel.addCell("65 SMA");
		excel.addCell("20 SMA");
		excel.addCell("A/D Value");
		excel.addCell("OBV Value");
	}
	
	private void printStockDetails(ExcelOutput excel, Stock s) {
		excel.addRow(0);
		excel.addCell(s.ticker);
		excel.addCell(s.price);
		excel.addCell(s.pTangBV);
		excel.addCell(s.pEbitda);
		excel.addCell(s.svOne);
		excel.addCell(s.svTwo);
		excel.addCell(s.svThree);
		excel.addCell(s.svFour);
	}
}
