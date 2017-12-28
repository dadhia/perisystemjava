package data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;
import com.bloomberglp.blpapi.MessageIterator;


/**
 * Class BloombergAPICommunicator.
 * This class is the core framework used by filters to communicate with Bloomberg market data.
 */
public class BloombergAPICommunicator {
	
	//Various indices that can be called from Bloomberg
	public enum Index {
		DOWJONES,SP500, NASDAQ, SPTSX, MEXIPC, IBOVESPA, 
		EUROSTOXX, FTSE100, CAC40, DAX, IBEX35, FTSEMIB, 
		OMXSTKH30, SWISSMKT, NIKKEI, HANGSENG, CSI300, SPASX200
	}
	
	//Various fields that can be called for a Bloomberg "Historical Request"
	public enum HistoricalRequest {
		ALL,		//All five fields below 
		PX_OPEN,	//open price
		PX_HIGH,	//high price
		PX_LOW,		//low price
		PX_CLOSE,	//close price
		VOLUME		//volume
	}
	
	//Various fields that can be called for a Bloomberg "Reference Data Request"
	public enum DataRequest {
		NAME,			// Company Name
		PE_RATIO, 		// P/E Ratio
		LAST_PX,  		// Last price
		PX_TBV_RATIO,	// Price to Tangible Book Value Per Share
		PX_EBITDA_RATIO,// Price to EBITDA
		MOV_AVG_20D,	// 20-day moving average
		MOV_AVG_50D, 	// 50-day moving average
		MOV_AVG_10D		// 10-day moving average
	}
	
	//private member variables to maintain communication with Bloomberg
	private Session session;
	private Service refDataService;
	private Service histStudyService;		//TODO
	
	//maintains the stocks that we should collect data for
	protected StockUniverse stockUniverse;	
	
	/**
	 * Constructor.
	 * Establishes various connections to Bloomberg and logs that these have started.
	 * @param pw PrintWriter
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public BloombergAPICommunicator(PrintWriter pw, Index index) 
			throws InterruptedException, IOException
	{
		startSession();
		pw.println("Session started"); pw.flush();
		establishRefDataService();
		pw.println("Ref Data Svc started"); pw.flush();
		establishHistoricalStudyService();
		pw.println("Hist study svc started"); pw.flush();
		getIndexMembers(index);
	}
	
	/**
	 * Constructs the stock universe.  Can be overriden by inheriting classes.
	 */
	protected void constructUniverse() {
		stockUniverse = new StockUniverse();
	}
	
	/**
	 * Starts a session with bloomberg using "localhost" and 8194 as the 
	 * default IP address and port, respectively.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void startSession() throws IOException, InterruptedException {
		//Basic connection to Bloomberg
		SessionOptions sessionOptions = new SessionOptions();
		sessionOptions.setServerHost("localhost");
		sessionOptions.setServerPort(8194);
		session = new Session(sessionOptions);
		session.start();
	}
	
	
	/**
	 * Creates a Reference Data Service.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void establishRefDataService() throws InterruptedException, IOException 
	{
		session.openService("//blp/refdata");
		refDataService = session.getService("//blp/refdata");
	}

	
	/**
	 * Creates a Historical Study Service.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void establishHistoricalStudyService() 
			throws InterruptedException, IOException 
	{
		session.openService("//blp/tasvc");
		histStudyService = session.getService("//blp/tasvc");
	}
	
	
	/**
	 * Builds the internal StockUniverse from an index.
	 * We force the user to provide an index upon construction so that a stock universe always exists.
	 * @param index Index
	 * @return StockUniverse
	 * @throws IOException
	 */
	private void getIndexMembers(Index index) throws IOException {
		//make a request, add the index, and ask for all members and a count of members
		Request request = refDataService.createRequest("ReferenceDataRequest");
		addIndexAsSecurity(index, request);
		request.getElement("fields").appendValue("INDX_MEMBERS");
		request.getElement("fields").appendValue("COUNT_INDEX_MEMBERS");
		session.sendRequest(request, null);

		//wait for a response
		//can be split into multiple PARTIAL RESPONSE's followed by a final RESPONSE
		//TODO: handle unlikely case with multiple responses for this request
		while (true) {
			Event event = null;
			try {
				event = session.nextEvent();	//keep pulling events
			} catch (InterruptedException e) {}
			
			if(event.eventType() == Event.EventType.RESPONSE) {
				readIndexResponse(event);
				break;
			}
			else if (event.eventType() == Event.EventType.PARTIAL_RESPONSE) {
				readIndexResponse(event);
			}
		}
	}
	
	
	/**
	 * Add an index to the securities element of the request.
	 * @param index Index
	 * @param request Request
	 */
	private void addIndexAsSecurity(Index index, Request request) {
		switch (index)
		{
			case DOWJONES: request.getElement("securities").appendValue("INDU INDEX");break;
			case SP500: request.getElement("securities").appendValue("SPX INDEX");break;
			case NASDAQ: request.getElement("securities").appendValue("NDX INDEX");break;
			case SPTSX: request.getElement("securities").appendValue("SPTSX INDEX");break;
			case MEXIPC:request.getElement("securities").appendValue("MEXBOL INDEX");break;
			case IBOVESPA: request.getElement("securities").appendValue("IBOV INDEX"); break;
			case EUROSTOXX:request.getElement("securities").appendValue("SX5E INDEX");break;
			case FTSE100: request.getElement("securities").appendValue("UKX INDEX");break;
			case CAC40: request.getElement("securities").appendValue("CAC INDEX"); break;
			case DAX: request.getElement("securities").appendValue("DAX INDEX");break;
			case IBEX35: request.getElement("securities").appendValue("IBEX INDEX");break;
			case FTSEMIB: request.getElement("securities").appendValue("FTSEMIB INDEX");break;
			case OMXSTKH30: request.getElement("securities").appendValue("OMX INDEX");break;
			case SWISSMKT:request.getElement("securities").appendValue("SMI INDEX");break;
			case NIKKEI:request.getElement("securities").appendValue("NKY INDEX"); break;
			case HANGSENG:request.getElement("securities").appendValue("HSI INDEX");break;
			case CSI300:request.getElement("securities").appendValue("SHSZ300 INDEX");break;
			case SPASX200:request.getElement("securities").appendValue("AS51 INDEX");break;
		}
	}
	
	
	
	/**
	 * Used by getIndexMembers() to read the full response and populate the stock universe.
	 * @param indexResponseEvent Event
	 */
	private void readIndexResponse(Event indexResponseEvent) 
			throws IOException, FileNotFoundException 
	{
		MessageIterator it = indexResponseEvent.messageIterator();
		while (it.hasNext()) {
			Message message = it.next();
			//Field Data has two elements: COUNT_INDEX_MEMBERS and INDX_MEMBERS
			Element messageAsElement = message.asElement();
			Element securityDataArray = messageAsElement.getElement("securityData");
			Element securityData = securityDataArray.getValueAsElement();
			Element fieldData = securityData.getElement("fieldData");
			
			//get the number of securities in this specific index
			int memberCount = fieldData.getElementAsInt32("COUNT_INDEX_MEMBERS");
						
			//iterate through the list and get all the tickers
			Element members = fieldData.getElement("INDX_MEMBERS");
			String [] tickers = new String[memberCount];
			for (int i = 0; i < memberCount; i++) {
				Element securityName = members.getValueAsElement(i);
				tickers[i] = securityName.getElementAsString("Member Ticker and Exchange Code");
			}
			
			stockUniverse.buildUniverse(tickers, memberCount);
		}
	}
	
	
	/**
	 * Creates a ReferenceDataRequest for all stocks in our stock universe.
	 * @return Request
	 */
	private Request buildRefDataRequest() {
		Request request = refDataService.createRequest("ReferenceDataRequest");
		Stock [] stocks = stockUniverse.getStocks();
		Element securitiesElement = request.getElement("securities");
		for (Stock s: stocks)
			securitiesElement.appendValue(s.ticker + " EQUITY");
		return request;
	}
	
	/**
	 * Method used to request specific details about every stock in our StockUniverse.
	 * Call this method after calling getIndexMembers().
	 * @param field DataRequest
	 * @throws IOException
	 */
	public void requestStockDetails(DataRequest field) throws IOException 
	{
		Request request = buildRefDataRequest();
		Element fields = request.getElement("fields");
		switch (field) {
			case NAME: fields.appendValue("NAME"); break;
			case PE_RATIO: fields.appendValue("PE_RATIO"); break;
			case LAST_PX: fields.appendValue("LAST_PRICE"); break;
			case PX_TBV_RATIO: fields.appendValue("PX_TO_TANG_BV_PER_SH"); break;
			case PX_EBITDA_RATIO: fields.appendValue("PX_TO_EBITDA"); break;
			case MOV_AVG_20D: fields.appendValue("MOV_AVG_20D"); break;
			case MOV_AVG_50D: fields.appendValue("MOV_AVG_50D"); break;
			case MOV_AVG_10D: fields.appendValue("MOV_AVG_10D"); break;
		}
		session.sendRequest(request, null);	//send request
		
		//wait for a response
		//like before, PARTIAL_RESPONSE will come prior to RESPONSE
		while (true) {
			Event event = null;
			try {
				event = session.nextEvent();
			} catch (InterruptedException e) {}
			
			if(event.eventType() == Event.EventType.RESPONSE) {
				readStocksResponse(event, field, stockUniverse.getStocks());
				break;
			}
			else if (event.eventType() == Event.EventType.PARTIAL_RESPONSE) {
				readStocksResponse(event, field, stockUniverse.getStocks());
			}
		}
	}
	
	
	/**
	 * Helper method used by requestStockDetails() to read the response;
	 * @param event
	 * @param field
	 * @param stocks
	 */
	public void readStocksResponse(Event event, DataRequest field, Stock [] stocks) 
	{
		MessageIterator iter = event.messageIterator();
		while(iter.hasNext()) {
			Message message = iter.next();
			//find the security data array which holds details per stock
			Element referenceDataRequest = message.asElement();
			Element securityDataArray = referenceDataRequest.getElement("securityData");
			
			//count of number of stocks that are in this message
			int numberOfStocksInMessage = securityDataArray.numValues();
			
			//iterate through all stocks in this message
			for (int i = 0; i < numberOfStocksInMessage; i++) {
				//get the stock and its sequence number
				Element singleStock = securityDataArray.getValueAsElement(i);
				int sequenceNumber = singleStock.getElementAsInt32("sequenceNumber");
				
				//the field data holds the vital information that was requested
				Element fieldData = singleStock.getElement("fieldData");
				Stock stock = stocks[sequenceNumber];
				switch (field) {
				case NAME:
					getStringFromBB(field, "NAME", "", fieldData, stock); break;
				case LAST_PX:
					getDoubleFromBB(field, "LAST_PRICE", 0, fieldData, stock); break;
				case PE_RATIO: 
					getDoubleFromBB(field, "PE_RATIO", Double.MAX_VALUE, fieldData, stock); break;
				case PX_TBV_RATIO:
					getDoubleFromBB(field, "PX_TO_TANG_BV_PER_SH", Double.MAX_VALUE, fieldData, stock); break;
				case PX_EBITDA_RATIO:
					getDoubleFromBB(field, "PX_TO_EBITDA", Double.MAX_VALUE, fieldData, stock); break;
				case MOV_AVG_20D:
					getDoubleFromBB(field, "MOV_AVG_20D", Double.MAX_VALUE, fieldData, stock); break;
				case MOV_AVG_50D:
					getDoubleFromBB(field, "MOV_AVG_50D", Double.MAX_VALUE, fieldData, stock); break;
				case MOV_AVG_10D:
					getDoubleFromBB(field, "MOV_AVG_10D", Double.MAX_VALUE, fieldData, stock); break;
				}
			}
		}
	}
	
	
	/**
	 * Helper method used to populate stock information for Float64.
	 * @param field DataRequest
	 * @param elementName String
	 * @param defaultValue double
	 * @param parentElement Element
	 * @param stock Stock
	 */
	private void getDoubleFromBB(DataRequest field, String elementName, 
			double defaultValue, Element parentElement, Stock stock) {
		if (parentElement.hasElement(elementName))
			stock.referenceData.put(field, parentElement.getElementAsFloat64(elementName));
		else
			stock.referenceData.put(field, defaultValue);
	}
	
	
	/**
	 * Helper method used to populate stock information for String.
	 * @param field DataRequest
	 * @param elementName String
	 * @param defaultValue double
	 * @param parentElement Element
	 * @param stock Stock
	 */
	private void getStringFromBB(DataRequest field, String elementName,
			String defaultValue, Element parentElement, Stock stock) {
		if (parentElement.hasElement(elementName))
			stock.referenceData.put(field, parentElement.getElementAsString(elementName));
		else
			stock.referenceData.put(field, defaultValue);
	}
	
	
	/**
	 * Historical Price Data request through Bloomberg API.
	 * @param requestType BloombergAPICommunicator.HistoricalRequest
	 * @param startDate String
	 * @param endDate String
	 * @param pw PrintWriter (for logging)
	 * @throws IOException
	 */
	public void requestHistoricalPriceData(BloombergAPICommunicator.HistoricalRequest requestType,
			String startDate, String endDate, PrintWriter pw) throws IOException 
	{
		//get the stock names
		Stock [] stocks;
		stocks = stockUniverse.getStocks();
		
		/* To send a historical price data request, we first need to check that Bloomberg
		 * has enough data to fulfill our request.  We can create a historical check message
		 * which compares our desired interval start date to the start date available for
		 * Bloomberg data.
		 * We use the field "INTERVAL_START_VALUE_DATE" and override the "START_DATE_OVERRIDE"
		 * and "END_DATE_OVERRIDE"
		 */
		Request histCheck = buildRefDataRequest();
		histCheck.getElement("fields").appendValue("INTERVAL_START_VALUE_DATE");			
		Element overrides = histCheck.getElement("overrides");
		Element override1 = overrides.appendElement();
		override1.setElement("fieldId", "START_DATE_OVERRIDE");
		override1.setElement("value", startDate);
		Element override2 = overrides.appendElement();
		override2.setElement("fieldId", "END_DATE_OVERRIDE");
		override2.setElement("value", endDate);
		session.sendRequest(histCheck, null);
		
		//logging
		pw.println("Sent histCheck");
		pw.flush();
		
		while (true) {
			Event event = null;
			try {
				event = session.nextEvent();
				readHistoricalCheckMessage(event, startDate, pw);
				if (event.eventType() == Event.EventType.RESPONSE) {
					break;	//only break when the full RESPONSE has been received
				}	
			} catch (InterruptedException e) { 
				
			}
		}
		
		pw.println("Histcheck complete");
		pw.flush();
		
		/* At this point, all equities that do not have the appropriate amount of data will have
		 * the status NO_DATA.  We move forward with our request from Bloomberg and only request
		 * information on those stocks without the NO_DATA status.
		 */
		
		int stocksLeft = stocks.length;		//number of stocks left
		int startIndex = 0;				//current index
		
		while (stocksLeft > 0) {
			Request request = refDataService.createRequest("HistoricalDataRequest");
			Element securitiesElement = request.getElement("securities");
			int nextStart = 0;
			//logging
			pw.println("The start index is: " + startIndex);
			pw.flush();
			
			for (int i = startIndex; i < stocks.length; i++) {
				stocksLeft -= 1;	//move to the next stock
				if (stocks[i].status != Stock.Status.NO_DATA) {
					securitiesElement.appendValue(stocks[i].ticker + " EQUITY");
				}
				else {
					nextStart = i + 1;	//skip over that stock
					break;
				}
			}
			
			pw.println("The last index inserted was " + (nextStart-2) + ", and we skipped over index " + (nextStart-1));
			pw.flush();
			//now move on to the fields we want to request
			Element fieldsElement = request.getElement("fields");
			//different kinds of requests so we don't need all the data every time
			switch (requestType) {
				case PX_OPEN:
					fieldsElement.appendValue("PX_OPEN"); break;
				case PX_HIGH:
					fieldsElement.appendValue("PX_HIGH"); break;
				case PX_LOW:
					fieldsElement.appendValue("PX_LOW"); break;
				case PX_CLOSE:
					fieldsElement.appendValue("PX_CLOSE_1D"); break;
				case VOLUME:
					fieldsElement.appendValue("PX_VOLUME"); break;
				case ALL:
					fieldsElement.appendValue("PX_OPEN");
					fieldsElement.appendValue("PX_HIGH");
					fieldsElement.appendValue("PX_LOW");
					fieldsElement.appendValue("PX_CLOSE_1D");
					fieldsElement.appendValue("PX_VOLUME");
					break;
			}
			//some basic settings for getting daily data
			//not sure exactly what these do, but they were in the example
			request.set("periodicityAdjustment", "ACTUAL");
			request.set("periodicitySelection", "DAILY");
			request.set("startDate", startDate);
			request.set("endDate", endDate);
			request.set("maxDataPoints", 10000);
			request.set("returnEids", true);
	
			session.sendRequest(request, null);
			//logging
			pw.println("Sent request");
			pw.flush();
			
			//wait for the request
			while (true) {
				Event event = null;
				try {
					event = session.nextEvent();
					readHistoricalResponse(event, requestType, startIndex, pw);
					if (event.eventType() == Event.EventType.RESPONSE) {
						break;
					}
				} 
				catch (InterruptedException e) {}
				catch (FileNotFoundException e) {}
			}
			pw.println("Request completed");
			pw.flush();
			startIndex = nextStart;	//shift the start index
		}
	}
	
	
	/**
	 * Helper method to read HistoricalCheck messages crafted by requestHistoricalPriceData()
	 * @param event Event
	 * @param stocks Stock []
	 * @param startDateString String
	 * @param pw PrintWriter
	 */
	private void readHistoricalCheckMessage(Event event, String startDateString, PrintWriter pw) 
	{
		MessageIterator iter = event.messageIterator();
		while (iter.hasNext()) {
			Message message = iter.next();

			//move to the securityData array
			Element referenceDataRequest = message.asElement();
			Element securityDataArray = referenceDataRequest.getElement("securityData");
			
			//determine number of stocks in the security data array
			int numberOfStocksInMessage = securityDataArray.numValues();
			
			//for every stock in this message
			for (int i = 0; i < numberOfStocksInMessage; i++) {
				//get the stock and its sequence number
				Element singleStock = securityDataArray.getValueAsElement(i);
				int sequenceNumber = singleStock.getElementAsInt32("sequenceNumber");
				
				//the field data element has the information we need -- whether the start date is valid
				Element fieldData = singleStock.getElement("fieldData");
				//convert the start date into a string
				String startDate = fieldData.getElementAsDate("INTERVAL_START_VALUE_DATE").toString();
				String [] splitted = startDate.split("-");	//split based on "-"
				//convert our start date into an array as well
				String [] ourDates = new String[3];
				ourDates[0] = startDateString.substring(0, 4);
				ourDates[1] = startDateString.substring(4, 6);
				ourDates[2] = startDateString.substring(6);
				
				//compare the two arrays, if there is not equality for each date, than
				//this stock does not have enough data in the bloomberg system
				for (int j = 0; j < splitted.length; j++) {
					if (!ourDates[j].contentEquals(splitted[j])) {
						pw.println(stockUniverse.getStocks()[sequenceNumber].ticker + " does not have enough data.");
						pw.flush();
						stockUniverse.getStocks()[sequenceNumber].status = Stock.Status.NO_DATA;
						break;
					}
				}
			}
		}
	}
	
	private void printMessage(Event event, PrintWriter pw) {
		MessageIterator msgIter = event.messageIterator();
		while(msgIter.hasNext()) {
			Message message = msgIter.next();
			pw.println(message);
			pw.flush();
		}
	}
	
	/**
	 * Helper method to read numerous Historical Responses that are created by requestHistoricalPriceData().
	 * @param event Event
	 * @param stocks Stocks
	 * @param request BloombergAPICommunicator.HistoricalRequest
	 * @param startIndex int
	 * @param pw PrintWriter
	 * @throws FileNotFoundException
	 */
	private void readHistoricalResponse(Event event, HistoricalRequest request,
			int startIndex, PrintWriter pw) throws FileNotFoundException {
		
		/* HistoricalDataResponse
		 * ---> securityData
		 * ---> ---> sequenceNumber
		 * ---> ---> fieldData []
		 * ---> ---> ---> fieldData values
		 */
		MessageIterator msgIter = event.messageIterator();
		while (msgIter.hasNext()) {
			Message message = msgIter.next();
			pw.print(message);
			pw.flush();
			
			Element messageAsElement = message.asElement();
			Element securityDataElement = messageAsElement.getElement("securityData");
			int sequenceNumber = securityDataElement.getElementAsInt32("sequenceNumber");
			
			sequenceNumber += startIndex;
			Stock stock = stockUniverse.getStocks()[sequenceNumber];
			Element fieldDataArray = securityDataElement.getElement("fieldData");
			int numberOfValues = fieldDataArray.numValues();
			switch (request) {
				case PX_OPEN: {
					stock.px_open = new double[numberOfValues];
					//fill in all the values
					for (int i = 0; i < numberOfValues; i++)
						stock.px_open[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_OPEN");
					break;
				}
				case PX_HIGH: {
					stock.px_high = new double[numberOfValues];
					for (int i = 0; i < numberOfValues; i++)
						stock.px_high[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_HIGH");
					break;
				}
				case PX_LOW: {
					stock.px_low = new double[numberOfValues];
					for (int i = 0; i < numberOfValues; i++)
						stock.px_low[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_LOW");
					break;
				}
				case PX_CLOSE:{
					stock.px_close = new double[numberOfValues];
					for (int i = 0; i < numberOfValues; i++)
						stock.px_close[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_CLOSE_1D");
					break;
				}
				case VOLUME: {
					stock.volume = new double[numberOfValues];
					for (int i = 0; i < numberOfValues; i++)
						stock.volume[i] = fieldDataArray.getValueAsElement(i).getElementAsInt32("PX_VOLUME");
					break;
				}
				case ALL: {
					stock.px_open = new double[numberOfValues];
					stock.px_close = new double[numberOfValues];
					stock.px_high = new double[numberOfValues];
					stock.px_low = new double[numberOfValues];
					stock.volume = new double[numberOfValues];
					for (int i = 0; i < numberOfValues; i++) {
						stock.px_open[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_OPEN");
						stock.px_high[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_HIGH");
						stock.px_low[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_LOW");
						stock.px_close[i] = fieldDataArray.getValueAsElement(i).getElementAsFloat64("PX_CLOSE_1D");
						stock.volume[i] = fieldDataArray.getValueAsElement(i).getElementAsInt32("PX_VOLUME");
					}
					break;
				}
			}
		}
	}
	
	/**
	 * Gets the internal stock universe.
	 * @return StockUniverse
	 */
	public StockUniverse getStockUniverse(){
		return this.stockUniverse;
	}
}
		

	
