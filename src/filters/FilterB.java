package filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import data.BloombergAPICommunicator;
import data.Stock;
import data.StockUniverse;
import stockGenie.ExcelOutput;

//Strategy B is the moving average cross:
//if the 10 day average is higher than 20 day average, but less than 50 day
//that is the bullish indicator
//bearish indicator is the IBD price below 50 day moving average
//Bloomberg Fields requested:
//MOV_AVG_10D
//MOV_AVG_20D
//MOV_AVG_50D
//We will also want price/EBITDA and price/Tang BV
//These fields will constitute FUNDAMENTALS_TWO in the BloombergAPICommunicator
//enumeration so look to that class for more information
public class FilterB extends Filter {

	public FilterB(BloombergAPICommunicator bloomberg) {
		super(bloomberg);
	}

	
	@Override
	public void run(StockUniverse stocks, PrintWriter pw, ExcelOutput excel) {
		try {
			for (Stock s: stocks.getStocks()) {
				s.status = Stock.Status.NO_STATUS;
			}
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.LAST_PX);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.MOV_AVG_50D);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.MOV_AVG_20D);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.MOV_AVG_10D);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.PX_TBV_RATIO);
			bloomberg.requestStockDetails(BloombergAPICommunicator.DataRequest.PX_EBITDA_RATIO);
			excel.createSheet("Moving Averages");
			ArrayList<Stock> buyList = new ArrayList<Stock>();
			ArrayList<Stock> sellList = new ArrayList<Stock>();
			ArrayList<Stock> noData = new ArrayList<Stock>();
			for (Stock s: stocks.getStocks()) {
				double price, sma50, sma20, sma10, tangBV, ebitda;
				price = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.LAST_PX);
				sma50 = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.MOV_AVG_50D);
				sma20 = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.MOV_AVG_20D);
				sma10 = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.MOV_AVG_10D);
				tangBV = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.PX_TBV_RATIO);
				ebitda = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.PX_EBITDA_RATIO);
				if (s.status == Stock.Status.NO_DATA) {
					noData.add(s);
				}
				else if (price < sma50 && sma20 < sma50) {
					if (tangBV > 5.0 && ebitda > 10.0)
						sellList.add(s);
				}
				else if (sma10 > sma20 && sma20 < sma50) {
					if (tangBV <= 5.0 && ebitda <= 10.0)
						buyList.add(s);
				}
			}
		
			excel.addRow(0);
			excel.addCell("Buy List:");
			buildColumnHeadings(excel);	//make column headings in excel
			
			for (Stock s: buyList)
				printStockDetails(excel, s);
			
			excel.addRow(0);
			excel.addRow(0);
			excel.addCell("Sell List:");
			
			for (Stock s: sellList)
				printStockDetails(excel, s);
			
			excel.addRow(0);
			excel.addRow(0);
			excel.addCell("No Data:");
			
			for (Stock s: noData) {
				excel.addRow(0);
				excel.addCell(s.ticker);
			}
			orderList = new Order[buyList.size() + sellList.size()];
			int i = 0;
			for (Stock s: buyList)
				orderList[i++] = new Order(Order.Action.BUY, 0 , s.ticker, 0.0, new Date());
			for (Stock s: sellList)
				orderList[i++] = new Order(Order.Action.SELL, 0 , s.ticker, 0.0, new Date());
		}
		catch (IOException e) {}
	}
	
	private void buildColumnHeadings(ExcelOutput excel) {
		excel.addRow(0);
		excel.addCell("Ticker");
		excel.addCell("Price");
		excel.addCell("P/Tang. BV");
		excel.addCell("P/EBITDA");
		excel.addCell("10 SMA");
		excel.addCell("20 SMA");
		excel.addCell("50 SMA");
	}
	
	private void printStockDetails(ExcelOutput excel, Stock s) {
		excel.addRow(0);
		excel.addCell(s.ticker);
		double price, tangBV, ebitda, sma10, sma20, sma50;
		price = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.LAST_PX);
		tangBV = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.PX_TBV_RATIO);
		ebitda = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.PX_EBITDA_RATIO);
		sma10 = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.MOV_AVG_10D);
		sma20 = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.MOV_AVG_20D);
		sma50 = (double)s.referenceData.get(BloombergAPICommunicator.DataRequest.MOV_AVG_50D);
		excel.addCell(price);
		excel.addCell(tangBV);
		excel.addCell(ebitda);
		excel.addCell(sma10);
		excel.addCell(sma20);
		excel.addCell(sma50);
	}
}
