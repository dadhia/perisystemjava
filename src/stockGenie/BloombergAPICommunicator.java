package stockGenie;

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
 * This class is the core framework used by strategies to communicate with
 * Bloomberg market data.
 */
public class BloombergAPICommunicator {
	
	//private member variables used for logging functionality
	private final int sourceIdentifierID = 2;
	private final String sourceName = "Bloomberg API";
	//private member variables to maintain communication with Bloomberg
	private Session session;
	private Service refDataService;
	private Service histStudyService;
	
	private ClientGUI clientGUI;
	private StockUniverse stockUniverse;
	
	//Various indices that can be called from Bloomberg
	public enum Index {
		DOWJONES,SP500, NASDAQ, SPTSX, MEXIPC, IBOVESPA, 
		EUROSTOXX, FTSE100, CAC40, DAX, IBEX35, FTSEMIB, 
		OMXSTKH30, SWISSMKT, NIKKEI, HANGSENG, CSI300, SPASX200
	}
	
	//Various fields that can be called for a Bloomberg "Historical Request#
	public enum HistoricalRequest {
		ALL,		//All five fields below 
		PX_OPEN,	//open price
		PX_HIGH,	//high price
		PX_LOW,		//low price
		PX_CLOSE,	//close price
		VOLUME		//volume
	}
	
	/**
	 * Strategies
	 * These are different than the "Strategy" class
	 * -->perhaps the names are a bit too confusing and should be changed
	 * The goal of Strategies in BloombergAPICommunicator is to provide an easy way to collect multiple
	 * data fields for all members of a StockUniverse.
	 * Proper practice is to add details of what each enumeration will provide so that we can reuse them
	 * in various strategies that we build.
	 */
	public enum Strategies {
		BASIC_INFORMATION,	//Company Name, PE Ratio, and LAST PRICE
		FUNDAMENTALS_ONE,
		FUNDAMENTALS_TWO
	}
	

	/**
	 * Constructor
	 * @param clientGUI ClientGUI
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public BloombergAPICommunicator(ClientGUI clientGUI)
			throws IOException, InterruptedException 
	{
		this.clientGUI = clientGUI;
		startSession();
		establishRefDataService();
		establishHistoricalStudyService();
	}

	
	/**
	 * Constructor.
	 * @param pw PrintWriter
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public BloombergAPICommunicator(PrintWriter pw) 
			throws InterruptedException, IOException
	{
		//Start a session
		startSession();
		pw.println("Session started");
		pw.flush();
		//Start Reference Data Service
		establishRefDataService();
		pw.println("Ref Data Svc started");
		pw.flush();
		//Start Historical Study Service
		establishHistoricalStudyService();
		pw.println("Hist study svc started");
		pw.flush();
	}
	
	
	/**
	 * Create a Reference Data Service and store it in the refDataService
	 * member variable.
	 */
	private void establishRefDataService() 
			throws InterruptedException, IOException 
	{
		session.openService("//blp/refdata");
		refDataService = session.getService("//blp/refdata");
	}
	
	
	/**
	 * Create a Historical Study Service and store it in the histStudyService
	 * member variable.
	 */
	private void establishHistoricalStudyService() 
			throws InterruptedException, IOException 
	{
		session.openService("//blp/tasvc");
		histStudyService = session.getService("//blp/tasvc");
	}
	
	
	/**
	 * Starts a session with bloomberg using "localhost" and 8194
	 * as the default IP address and port, respectively.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void startSession() throws IOException, InterruptedException {
		//Basic connection to bloomberg
		SessionOptions sessionOptions = new SessionOptions();
		sessionOptions.setServerHost("localhost");
		sessionOptions.setServerPort(8194);
		session = new Session(sessionOptions);	//start session
		session.start();
	}

	
	/**
	 * Provides a StockUniverse with all members of an index.
	 * @param index Index
	 * @return StockUniverse
	 * @throws IOException
	 */
	public StockUniverse getIndexMembers(Index index) throws IOException {
		//Create a request -- just basic bloomberg syntax
		Request request = refDataService.createRequest("ReferenceDataRequest");
		//add the index
		addIndexAsSecurity(index, request);
		//we ask for two things: all of the members of the index, and a count
		request.getElement("fields").appendValue("INDX_MEMBERS");
		request.getElement("fields").appendValue("COUNT_INDEX_MEMBERS");
		//send the request
		session.sendRequest(request, null);

		//wait for a response
		while (true) {
			Event event = null;
			try {
				event = session.nextEvent();	//keep pulling events
			} catch (InterruptedException e) {}
			
			//the response can potentially be split into multiple events
			//final event is indicated by the Event Type RESPONSE
			if(event.eventType() == Event.EventType.RESPONSE) {
				readIndexResponse(event);	//build the stock universe
				break;
			}
			
			//if we had multiple events than the earlier ones would be
			//called PARTIAL_RESPONSE by Bloomberg
			else if (event.eventType() == Event.EventType.PARTIAL_RESPONSE) {
				readIndexResponse(event);	//build the stock universe
			}
		}
		return stockUniverse;
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
			//clientGUI.makeUpdate("There are " + memberCount + " members.", sourceIdentifierID, sourceName);
			
			//iterate through the list and get all the tickers
			Element members = fieldData.getElement("INDX_MEMBERS");
			String [] tickers = new String[memberCount];
			for (int i = 0; i < memberCount; i++) {
				Element securityName = members.getValueAsElement(i);
				//populate each stock with a string of its name
				//Example: Apple Inc will be AAPL US
				//later on we append with the "EQUITY" tag, but there is no point in storing that in memory
				tickers[i] = securityName.getElementAsString("Member Ticker and Exchange Code");
			}
			
			stockUniverse = new StockUniverse(memberCount);
			stockUniverse.setTickers(tickers);
		}
	}
	
	
	/**
	 * Method used to request specific details about every stock in our StockUniverse.
	 * Call this method after calling getIndexMembers().
	 * @param strategy BloombergAPICommunicator.Strategies (enum)
	 * @throws IOException
	 */
	public void requestStockDetails(BloombergAPICommunicator.Strategies strategy) 
			throws IOException 
	{
		//make a new reference data request -- standard bloomberg syntax
		Request request = refDataService.createRequest("ReferenceDataRequest");
		
		//populate all members of our stock universe
		Stock [] stocks;
		stocks = stockUniverse.getStocks();
		Element securitiesElement = request.getElement("securities");
		for (int i = 0; i < stocks.length; i++)
			securitiesElement.appendValue(stocks[i].ticker + " EQUITY");
		
		//Based on the strategy that was selected, we simply add the fields we want
		//ADD MORE STRATEGIES HERE
		switch (strategy) {
			case BASIC_INFORMATION: {
				request.getElement("fields").appendValue("NAME");
				request.getElement("fields").appendValue("PE_RATIO");
				request.getElement("fields").appendValue("LAST_PRICE");
				break;
			}
			case FUNDAMENTALS_ONE: {
				request.getElement("fields").appendValue("PX_TO_TANG_BV_PER_SH");
				request.getElement("fields").appendValue("PX_TO_EBITDA");
				break;
			}
			case FUNDAMENTALS_TWO: {
				request.getElement("fields").appendValue("PX_TO_TANG_BV_PER_SH");
				request.getElement("fields").appendValue("PX_TO_EBITDA");
				request.getElement("fields").appendValue("MOV_AVG_20D");
				request.getElement("fields").appendValue("MOV_AVG_50D");
				request.getElement("fields").appendValue("MOV_AVG_10D");	
			}
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
				readStocksResponse(event, strategy, stocks);
				break;	//leave the loop
			}
			else if (event.eventType() == Event.EventType.PARTIAL_RESPONSE) {
				readStocksResponse(event, strategy, stocks);
			}
		}
	}
	
	
	/**
	 * Used by request stock details to read the stocks response.
	 */
	public void readStocksResponse(Event event, BloombergAPICommunicator.Strategies strategy, 
									Stock [] stocks) 
	{
		MessageIterator iter = event.messageIterator();
		while(iter.hasNext()) {
			Message message = iter.next();
			//find the security data array which holds details per stock
			Element referenceDataRequest = message.asElement();
			Element securityDataArray = referenceDataRequest.getElement("securityData");
			//count of number of stocks that are in this message
			int numberOfStocksInMessage = securityDataArray.numValues();
			
			//FOR LOOP WILL ITERATE THROUGH ALL THE STOCKS
			//for every stock in THIS message
			for (int i = 0; i < numberOfStocksInMessage; i++) {
				//get the stock
				Element singleStock = securityDataArray.getValueAsElement(i);
				//the sequence number matches the stock to the number we have assigned
				//it in our stock universe
				int sequenceNumber = singleStock.getElementAsInt32("sequenceNumber");
				
				//the field data holds the vital information that was requested for
				//that particular stock
				Element fieldData = singleStock.getElement("fieldData");
				switch (strategy){
					case BASIC_INFORMATION: {
						if (fieldData.hasElement("NAME"))
							stocks[sequenceNumber].companyName = fieldData.getElementAsString("NAME");
						if (fieldData.hasElement("PE_RATIO"))
							stocks[sequenceNumber].pe = fieldData.getElementAsFloat64("PE_RATIO");
						else 
							stocks[sequenceNumber].pe = Integer.MAX_VALUE; 	// No P/E so put MAX VALUE
						if (fieldData.hasElement("LAST_PRICE"))
							stocks[sequenceNumber].price = fieldData.getElementAsFloat64("LAST_PRICE");
						break;
					} 
					case FUNDAMENTALS_ONE: {
						if (fieldData.hasElement("PX_TO_TANG_BV_PER_SH"))
							stocks[sequenceNumber].pTangBV = fieldData.getElementAsFloat64("PX_TO_TANG_BV_PER_SH");
						else
							stocks[sequenceNumber].pTangBV = Integer.MAX_VALUE;
						if (fieldData.hasElement("PX_TO_EBITDA"))
							stocks[sequenceNumber].pEbitda = fieldData.getElementAsFloat64("PX_TO_EBITDA");
						else
							stocks[sequenceNumber].pEbitda = Integer.MAX_VALUE;
						break;
					}
					case FUNDAMENTALS_TWO: {
						if (fieldData.hasElement("PX_TO_TANG_BV_PER_SH"))
							stocks[sequenceNumber].pTangBV = fieldData.getElementAsFloat64("PX_TO_TANG_BV_PER_SH");
						else
							stocks[sequenceNumber].pTangBV = Integer.MAX_VALUE;
						if (fieldData.hasElement("PX_TO_EBITDA"))
							stocks[sequenceNumber].pEbitda = fieldData.getElementAsFloat64("PX_TO_EBITDA");
						else
							stocks[sequenceNumber].pEbitda = Integer.MAX_VALUE;
						if (fieldData.hasElement("MOV_AVG_10D"))
							stocks[sequenceNumber].sma10 = fieldData.getElementAsFloat64("MOV_AVG_10D");
						if (fieldData.hasElement("MOV_AVG_20D"))
							stocks[sequenceNumber].sma20 = fieldData.getElementAsFloat64("MOV_AVG_20D");
						if (fieldData.hasElement("MOV_AVG_50D"))
							stocks[sequenceNumber].sma50 = fieldData.getElementAsFloat64("MOV_AVG_50D");
					}
				}
			}
		}
	}
	
	/**
	 * Historical Price Data request.
	 */
	public void requestHistoricalPriceData(
			BloombergAPICommunicator.HistoricalRequest requestType,
			String startDate,
			String endDate, PrintWriter pw) throws IOException 
	{
		//get the stock names
		Stock [] stocks;
		stocks = stockUniverse.getStocks();
		
		//To send a historical price data request, we first need to check
		//that bloomberg has enough data to fulfill our request
		
		//For that, we wend a histCheck, which compares our desired interval start
		//date to the start date available for bloomberg data
		Request histCheck = refDataService.createRequest("ReferenceDataRequest");
		
		//fill the securities element with all stock tickers, appending
		//with the "EQUITY" tag
		Element securities = histCheck.getElement("securities");
		for (int i = 0; i < stocks.length; i++)
			securities.appendValue(stocks[i].ticker + " EQUITY");
		
		//use the field INTERVAL_START_VALUE_DATE
		Element fields = histCheck.getElement("fields");
		fields.appendValue("INTERVAL_START_VALUE_DATE");
			
		//get the overrides element
		Element overrides = histCheck.getElement("overrides");
		
		//start date override
		Element override1 = overrides.appendElement();
		override1.setElement("fieldId", "START_DATE_OVERRIDE");
		override1.setElement("value", startDate);
		
		//end date override
		Element override2 = overrides.appendElement();
		override2.setElement("fieldId", "END_DATE_OVERRIDE");
		override2.setElement("value", endDate);
				
		session.sendRequest(histCheck, null);	//send the request
		pw.println("Sent histCheck");
		pw.flush();
		while (true) {
			Event event = null;
			try {
				event = session.nextEvent();
				readHistoricalCheckMessage(event, stocks, startDate, pw);
				if (event.eventType() == Event.EventType.RESPONSE) {
					break;	//only break when the full RESPONSE has been received
				}	
			} catch (InterruptedException e) { 
				
			}
		}
		pw.println("Histcheck complete");
		pw.flush();
		//At this point, all equities that do not have date will have the
		//status indicator: NO_DATA
		
		//We can move forward and request data from bloomberg for all tickers
		//in our stock universe, excluding those with the NO_DATA status
		
		//Again, we will build a request, but this time a HistoricalDataRequest
		
		//rather than remove certain stocks from the universe, we will skip over
		//them when they occur and note that index so the data matches correctly
		
		//total number of remaining stocks that we have not requested data for
		int stocksLeft = stocks.length;	
		//stock number that we are currently on
		int startIndex = 0;
		

		while (stocksLeft > 0) {	//while there are stocks left
			Request request = refDataService.createRequest("HistoricalDataRequest");
			Element securitiesElement = request.getElement("securities");
			int nextStart = 0;
			pw.println("The start index is: " + startIndex);
			pw.flush();
			
			for (int i = startIndex; i < stocks.length; i++) {
				stocksLeft--;	//move to the next stock
				if (stocks[i].status != Stock.Status.NO_DATA) {
					securitiesElement.appendValue(stocks[i].ticker + " EQUITY");
				}
				else {
					nextStart = i + 1;	//skip over that stock
					break;
				}
			}
			pw.println("The last index inserted was " + (nextStart-2) + 
					", and we skipped over index " + (nextStart-1));
			pw.flush();
			//now move on to the fields we want to request
			Element fieldsElement = request.getElement("fields");
			//different kinds of requests so we don't need all the data everytime
			switch (requestType) {
				case PX_OPEN: {
					fieldsElement.appendValue("PX_OPEN"); 
					break;
				}
				case PX_HIGH: {
					fieldsElement.appendValue("PX_HIGH");
					break;
				}
				case PX_LOW: {
					fieldsElement.appendValue("PX_LOW");
					break;
				}
				case PX_CLOSE: {
					fieldsElement.appendValue("PX_CLOSE_1D");
					break;
				}
				case VOLUME: {
					fieldsElement.appendValue("PX_VOLUME");
					break;
				}
				case ALL: {
					fieldsElement.appendValue("PX_OPEN");
					fieldsElement.appendValue("PX_HIGH");
					fieldsElement.appendValue("PX_LOW");
					fieldsElement.appendValue("PX_CLOSE_1D");
					fieldsElement.appendValue("PX_VOLUME");
					break;
				}
			}
			//some basic settings for getting daily data
			//not sure exactly what these do, but they were in the example
			request.set("periodicityAdjustment", "ACTUAL");
			request.set("periodicitySelection", "DAILY");
			request.set("startDate", startDate);
			request.set("endDate", endDate);
			request.set("maxDataPoints", 10000);
			request.set("returnEids", true);
	
			session.sendRequest(request, null);	//send the request
			pw.println("Sent request");
			pw.flush();
			//wait for the request
			while (true) {
				Event event = null;
				try {
					event = session.nextEvent();
					readHistoricalResponse(event, stocks, requestType, startIndex, pw);
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
	 * Method used to read a histCheck message. This determines if a
	 * stock has enough data based on the start and end dates provided
	 * by the user.
	 */
	private void readHistoricalCheckMessage(Event event, Stock [] stocks, 
											String startDateString, PrintWriter pw) 
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
				//get the stock
				Element singleStock = securityDataArray.getValueAsElement(i);
				//get the sequence number which is the number we associate with that stock in the sequence
				int sequenceNumber = singleStock.getElementAsInt32("sequenceNumber");
				//the field data element has the information we need--whether the start date is valid
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
						pw.println(stocks[sequenceNumber].ticker + " does not have enough data.");
						pw.flush();
						stocks[sequenceNumber].status = Stock.Status.NO_DATA;
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
	
	private void readHistoricalResponse(Event event, Stock [] stocks, BloombergAPICommunicator.HistoricalRequest request,
			int startIndex, PrintWriter pw) throws FileNotFoundException {
		//OVERALL MESSAGE : HistoricalDataResponse
		//----> securityData
		//----> ----> sequenceNumber
		//----> ----> fieldData []
		//----> ----> ----> fieldData values

		MessageIterator msgIter = event.messageIterator();
		
		while (msgIter.hasNext()) {
			
			Message message = msgIter.next();
		//pw.print(message);
		//	pw.flush();
			Element messageAsElement = message.asElement();
			Element securityDataElement = messageAsElement.getElement("securityData");
			int sequenceNumber = securityDataElement.getElementAsInt32("sequenceNumber");
			sequenceNumber += startIndex;
			Stock stock = stocks[sequenceNumber];
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
	
	public StockUniverse getStockUniverse(){
		return this.stockUniverse;
	}
}
		

	
