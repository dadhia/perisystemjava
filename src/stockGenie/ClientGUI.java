package stockGenie;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ClientGUI extends JPanel{

	public enum Strategy {
		STRAT_ONE
	}

	private final int sourceIdentifierID = 1;
	private final String sourceName = "Peri System";
	
	private static final long serialVersionUID = 1L;
	private Communicator communicatorPanel;
	
	private JTable stocksPanel;
	private DefaultTableModel stocksTableModel;
	private JScrollPane stocksScrollPane;
	private ClientGUI thisFrame;
	
	private JComboBox<String> indexChooser;
	private JPanel indexPanel;
	private JButton selectIndexButton;
	
	private JTextArea updateCenter;
	private JScrollPane updateScrollPane;
	private JPanel updateCenterPanel;
	
	private SQLConnector databaseConnection;
	private BloombergAPICommunicator bloomberg;
	private StockUniverse stockUniverse;
	
	private JButton connectBloomberg, connectDatabase, selectStrategyButton;
	private JComboBox<String> strategiesComboBox;
	
	
	private JButton runHistoricalTest;
	
	public ClientGUI() {
		thisFrame = this;
		initializeVariables();
		createGUI();
		addEvents();
	}
	
	
	private void initializeVariables() {
		communicatorPanel = new Communicator();
		createStocksTable();
		createAdditionalButtons();
		createUpdateCenter();	
	}
	
	private void createGUI(){
		
		//GUI LAYOUT (GRID BAG)...more to be added as we add more functionality
		// 			X = 0		X = 1		X = 2		X = 3
		//	Y = 0	Comm.		Comm.		Stocks		Stocks
		//  Y = 1	Updates		Updates		Stocks		Stocks
		//  Y = 2							Index		Index
		//  Y = 3
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(5, 5, 5, 5);
		this.add(communicatorPanel, constraints);
		
		stocksScrollPane.setSize(500, 300);
		stocksScrollPane.setPreferredSize(new Dimension(500, 300));
		stocksScrollPane.setMinimumSize(new Dimension(500, 300));
		stocksScrollPane.setBorder(BorderFactory.createTitledBorder("  Stocks  "));
		constraints.gridy = 0;
		constraints.gridx = 2;
		constraints.gridheight = 2;
		constraints.gridwidth = 3;
		this.add(stocksScrollPane, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		this.add(updateCenterPanel, constraints);
		
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		constraints.gridwidth = 3;
		this.add(indexPanel, constraints);
	}
	
	private void addEvents() {
		
		selectIndexButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					int indexChosen = indexChooser.getSelectedIndex();
					BloombergAPICommunicator.Index actualIndex = null;
					switch (indexChosen) {
						case 0: actualIndex = BloombergAPICommunicator.Index.DOWJONES; break;
						case 1: actualIndex = BloombergAPICommunicator.Index.SP500; break;
						case 2: actualIndex = BloombergAPICommunicator.Index.NASDAQ; break;
						case 3: actualIndex = BloombergAPICommunicator.Index.SPTSX; break;
						case 4: actualIndex = BloombergAPICommunicator.Index.MEXIPC; break;
						case 5: actualIndex = BloombergAPICommunicator.Index.IBOVESPA; break;
						case 6: actualIndex = BloombergAPICommunicator.Index.EUROSTOXX; break;
						case 7: actualIndex = BloombergAPICommunicator.Index.FTSE100; break;
						case 8: actualIndex = BloombergAPICommunicator.Index.CAC40; break;
						case 9: actualIndex = BloombergAPICommunicator.Index.DAX; break;
						case 10: actualIndex = BloombergAPICommunicator.Index.IBEX35; break;
						case 11: actualIndex = BloombergAPICommunicator.Index.FTSEMIB; break;
						case 12: actualIndex = BloombergAPICommunicator.Index.OMXSTKH30; break;
						case 13: actualIndex = BloombergAPICommunicator.Index.SWISSMKT; break;
						case 14: actualIndex = BloombergAPICommunicator.Index.NIKKEI; break;
						case 15: actualIndex = BloombergAPICommunicator.Index.HANGSENG; break;
						case 16: actualIndex = BloombergAPICommunicator.Index.CSI300; break;
						case 17: actualIndex = BloombergAPICommunicator.Index.SPASX200; break;
					}
					makeUpdate("Requesting members of: " + indexChosen, sourceIdentifierID, sourceName);
					bloomberg.getIndexMembers(actualIndex);
				} catch (IOException e) {
					makeUpdate("IOException has occurred at Location 0", sourceIdentifierID, sourceName);
				}
			}
		});
		
		connectBloomberg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					bloomberg = new BloombergAPICommunicator("localhost", 8194, thisFrame);
					makeUpdate("Established connection to Bloomberg.", sourceIdentifierID, "Peri System");
				} catch (InterruptedException | IOException e) {
					makeUpdate("Unable to establish connection to the Bloomberg servers.", 0, "Peri System");
				}
			}
		});
		connectDatabase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					databaseConnection = new SQLConnector();
					makeUpdate("Established connection to the database.", sourceIdentifierID, "Peri System");
				} catch (ClassNotFoundException | SQLException e) {
					databaseConnection = null;
					makeUpdate("Unable to establish connection to the database. Logs will not be stored", 0, "Peri System");
				}
			}
		});
		
		runHistoricalTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StrategyOne strat = new StrategyOne("Strategy 1", bloomberg);
				strat.execute(thisFrame);
			}
		});
		
		selectStrategyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
	}
	
	/**
	 * Creates the stocks table with columns for ticker, company name,
	 * current price, p/e.  Of course, stocks will have more data than 
	 * this, but that extra data will not be shown.
	 */
	private void createStocksTable() {
		Object [] columnNames = {
			"Ticker", "Company Name", "Current Price", "P/E"	
		};
		stocksTableModel = new DefaultTableModel(columnNames, 0);
		stocksPanel = new JTable(stocksTableModel);
		stocksScrollPane = new JScrollPane(stocksPanel);
		stocksPanel.setRowHeight(16);
	}
	
	private void createAdditionalButtons() {
		//Buttons: Connect To BB and DB
		connectBloomberg = new JButton("Connect To Bloomberg");
		connectBloomberg.setBorder(new EmptyBorder(2, 2, 2, 2));
		connectDatabase = new JButton("Connect To Database");
		connectDatabase.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		//Choose which index to apply the index on
		String [] indexOptions = {
				"DOWJONES", "SP500", "NASDAQ", "SPTSX", "MEXIPC", "IBOVESPA",
				"EUROSTOXX", "FTSE100", "CAC40", "DAX", "IBEX35", "FTSEMIB",
				"OMXSTKH30", "SWISSMKT", "NIKKEI", "HANGSENG", "CSI300", "SPASX200"
		};
		indexChooser = new JComboBox<String>(indexOptions);
		selectIndexButton = new JButton("Select Index");
		selectIndexButton.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		String [] strategies = {
				"EBITDA-Tang. BVal-A/D//OBV"
		};
		strategiesComboBox = new JComboBox<String>(strategies);
		selectStrategyButton = new JButton("Apply Strategy");
		selectStrategyButton.setBorder(new EmptyBorder(2, 2, 2, 2));

		runHistoricalTest = new JButton("Test Run");
		runHistoricalTest.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		//add the buttons onto the index panel which will be added to 
		//the overall layout
		//LEVEL ONE: Bloomberg	Index	Strategy
		//LEVEL TWO: Database	Select	Select
		indexPanel = new JPanel(new GridLayout(2, 4, 10, 10));
		indexPanel.setBorder(BorderFactory.createTitledBorder("  Connections & Strategies  "));
		indexPanel.add(connectBloomberg);
		indexPanel.add(indexChooser);
		indexPanel.add(strategiesComboBox);
		indexPanel.add(runHistoricalTest);
		indexPanel.add(connectDatabase);
		indexPanel.add(selectIndexButton);
		indexPanel.add(selectStrategyButton);
		
	}
	
	private void createUpdateCenter() {
		updateCenter = new JTextArea(20, 20);
		updateScrollPane = new JScrollPane(updateCenter);
		//text formatting
		updateCenter.setEditable(false);
		updateCenter.setLineWrap(true);
		updateCenter.setWrapStyleWord(true);
		
		//sizing
		updateScrollPane.setPreferredSize(new Dimension(300, 200));
		updateScrollPane.setMinimumSize(new Dimension(300, 200));
		
		updateCenterPanel = new JPanel(new BorderLayout());
		JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		innerPanel.add(updateScrollPane);
		updateCenterPanel.add(innerPanel);
		updateCenterPanel.setBorder(BorderFactory.createTitledBorder("  Updates  "));
	}
	
	public void makeUpdate(String updateMessage, int sourceIdentifierID, String sourceName) {
		Timestamp t = new Timestamp(System.currentTimeMillis());
		updateCenter.append(t + " :: " + sourceName + " :: " + updateMessage);
		updateCenter.append("\n");
		
		if (databaseConnection != null) {
			if(sourceIdentifierID != 0) {
				try {
					databaseConnection.logUpdateMessage(sourceIdentifierID, updateMessage, t);
				} catch (SQLException e) {
					makeUpdate("Failed to write latest message to database.", 0, this.sourceName);
				}
			}
		}
	}
	
	public StockUniverse getStockUniverse() {
		return stockUniverse;
	}
	
	public void createNewStockUniverse(int numberOfStocks) {
		stockUniverse = new StockUniverse(numberOfStocks);
	}
	
	public void updateTable() {
		 stockUniverse.refreshTable(stocksTableModel);
	}
}
