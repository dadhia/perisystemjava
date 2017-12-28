package filters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

import data.BloombergAPICommunicator;
import data.Stock;
import data.StockUniverse;
import stockGenie.ExcelOutput;

public class StrategyA extends Filter{

	public StrategyA(BloombergAPICommunicator bloomberg) {
		super(bloomberg);
	}

	public void run(StockUniverse stocks, PrintWriter pw, ExcelOutput excel) {
		try {
			pw.println();
			pw.println("Strategy One");
			pw.println("Requesting FUNDAMENTALS_ONE from Bloomberg"); 
			pw.flush();
			
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.NAME);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.PE_RATIO);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.LAST_PX);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.PX_TBV_RATIO);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.PX_EBITDA_RATIO);
			
			//get all the technical data
			pw.println("Received FUNDAMENTALS_ONE from Bloomberg");
			pw.println("Requesting historical price details (ALL)");
			pw.flush();
			
			/**Determine start and end dates for historical price and volume data**/
			//Bloomberg uses the format yyyyMMdd
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			
			//determine the end date
			Calendar calendar = Calendar.getInstance();
			//move to the appropriate day in case this algorithm is run on the weekend
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				calendar.add(Calendar.DAY_OF_WEEK, -1);
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				calendar.add(Calendar.DAY_OF_WEEK, -2);
			String endDate = df.format(calendar.getTime());
			
			pw.println("The end date is: " + endDate + ", which is a " + calendar.get(Calendar.DAY_OF_WEEK));
			pw.flush();
			//determine start date (last year)
			calendar.add(Calendar.YEAR, -1);
			//move to a business day, as of now we are not able to determine
			//market holidays which may cause a bug in the process on certain
			//days
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				calendar.add(Calendar.DAY_OF_WEEK, -1);
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				calendar.add(Calendar.DAY_OF_WEEK, -2);
			String startDate = df.format(calendar.getTime());
			
			pw.println("The start date is: " + startDate + ", which is a " + calendar.get(Calendar.DAY_OF_WEEK));
			pw.flush();
			
			bloomberg.requestHistoricalPriceData(
					BloombergAPICommunicator.HistoricalRequest.ALL,
					startDate,endDate, pw);
			
			pw.println("Received Historical price details (ALL)");
			pw.flush();
			
			
			excel.createSheet("OBV and A/D");

			Core c = new Core();
			ArrayList<Stock> buyList = new ArrayList<Stock>();
			ArrayList<Stock> sellList = new ArrayList<Stock>();
			ArrayList<Stock> noData = new ArrayList<Stock>();
			for (Stock s: bloomberg.getStockUniverse().getStocks()) {
				//skip over ever stock that has no data
				if (s.status == Stock.Status.NO_DATA){
					noData.add(s);
					continue;
				}
				double tangBV = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.PX_TBV_RATIO);
				double ebitda = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.PX_EBITDA_RATIO);
				if ( tangBV <= 5.0 && ebitda <= 10.0) 
				{
					//calculate 65 day SMA
					MInteger outBegin65 = new MInteger();
					MInteger outLength65 = new MInteger();
					double [] sma65 = new double[s.px_close.length];
					c.sma(0, s.px_close.length-1, s.px_close, 65, outBegin65, outLength65, sma65);
					s.svOne = sma65[outLength65.value-1];		//store it into svOne--special value one		
					
					//calculate 20 day SMA
					MInteger outBegin20 = new MInteger();
					MInteger outLength20 = new MInteger();
					double [] sma20 = new double[s.px_close.length];
					c.sma(0, s.px_close.length-1, s.px_close, 20, outBegin20, outLength20, sma20);
					s.svTwo = sma20[outLength20.value-1];		//store it into svTwo--special value two
					
					//if the price is less than both SMA's
					if ((s.price <= s.svTwo) && (s.price <= s.svOne)) {
						//calculate Chaikin A/D line
						double [] ad = new double [s.px_close.length];
						MInteger outBeginAD = new MInteger();
						MInteger outLengthAD = new MInteger();
						c.ad(0, s.px_close.length-1, s.px_high, s.px_low, s.px_close, s.volume, outBeginAD, outLengthAD, ad);
						s.svThree = ad[outLengthAD.value-1];	//store in svThree
						
						boolean adBullish = false;
						if ((s.svThree >= s.svOne) && (s.svThree >= s.svTwo))
							adBullish = true;				
						
						//calculate OBV
						MInteger outBeginOBV = new MInteger();
						MInteger outLengthOBV = new MInteger();
						double [] obv = new double[s.px_close.length];
						c.obv(0, s.px_close.length-1, s.px_close, s.volume, outBeginOBV, outLengthOBV, obv);
						s.svFour = obv[outLengthOBV.value-1];	//store in svFour
						
						boolean obvBullish = false;
						if ((s.svFour >= s.svOne) && (s.svFour >= s.svTwo))
								obvBullish = true;		
						if (obvBullish && adBullish) {
							s.status = Stock.Status.BUY;
							buyList.add(s);
						}
							
					}
				}
				else if ((s.pEbitda > 10.0 || s.pEbitda < 0) && (s. > 10.0 || s. < 0))
				{
						
					//calculate the 65 Day Simple Moving Average (SMA)
					MInteger outBegin65 = new MInteger();
					MInteger outLength65 = new MInteger();
					double [] sma65 = new double[s.px_close.length];
					c.sma(0, s.px_close.length-1, s.px_close, 65, outBegin65, outLength65, sma65);
					s.svOne = sma65[outLength65.value-1];					
					
					//calculate the 20 Day Simple Moving Average (SMA)
					MInteger outBegin20 = new MInteger();
					MInteger outLength20 = new MInteger();
					double [] sma20 = new double[s.px_close.length];
					c.sma(0, s.px_close.length-1, s.px_close, 20, outBegin20, outLength20, sma20);
					s.svTwo = sma20[outLength20.value-1];
					
					//check the price
					if ((s.price >= sma20[outLength20.value-1]) && (s.price >= sma65[outLength65.value-1])) 
					{
						//Calculate the A/D line
						double [] ad = new double [s.px_close.length];
						MInteger outBeginAD = new MInteger();
						MInteger outLengthAD = new MInteger();
						c.ad(0, s.px_close.length-1, s.px_high, s.px_low, s.px_close, s.volume, outBeginAD, outLengthAD, ad);
						s.svThree = ad[outLengthAD.value-1];
						
						//Calculate the OBV -- On-Balance Volume
						MInteger outBeginOBV = new MInteger();
						MInteger outLengthOBV = new MInteger();
						double [] obv = new double[s.px_close.length];
						c.obv(0, s.px_close.length-1, s.px_close, s.volume, outBeginOBV, outLengthOBV, obv);
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
							sellList.add(s);
						}
					}
				}
			}

			excel.addRow(0);
			excel.addCell("Buy List:");
			buildColumnHeadings(excel);	//make column headings in excel
			
			for (Stock s: buyList)
				printStockDetails(excel, s);

			excel.addRow(0);	//make one empty row
			excel.addRow(0);
			excel.addCell("Sell List");
			buildColumnHeadings(excel);
			
			for (Stock s: sellList)
				printStockDetails(excel, s);
				
			excel.addRow(0);	//make an empty row
			excel.addRow(0);
			excel.addCell("No Data:");
			
			for (Stock s: noData) {
				excel.addRow(0);
				excel.addCell(s.ticker);
			}
			
			//place all orders in final order list
			orderList = new Order[buyList.size() + sellList.size()];
			int i = 0;
			for (Stock s: buyList)
				orderList[i++] = new Order(Order.Action.BUY, 0 , s.ticker, new String() , 0.0, new Date());
			for (Stock s: sellList)
				orderList[i++] = new Order(Order.Action.SELL, 0 , s.ticker, new String() , 0.0, new Date());
		}
		catch (FileNotFoundException e1) {} 
		catch (IOException e) {}
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
		excel.addCell(s.);
		excel.addCell(s.pEbitda);
		excel.addCell(s.svOne);
		excel.addCell(s.svTwo);
		excel.addCell(s.svThree);
		excel.addCell(s.svFour);
	}
}
