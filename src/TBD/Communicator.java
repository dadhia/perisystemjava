package TBD;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import guiWidgets.TextFieldWithGhostText;

public class Communicator extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton connectButton;
	private TextFieldWithGhostText ipAddressField, portField;
	
	public Communicator() {
		initializeVariables();
		createGUI();
		addEvents();
	}
	
	private void initializeVariables() {
		connectButton = new JButton("Connect via Desktop Communicator");
		ipAddressField = new TextFieldWithGhostText("IP Address", 20);
		portField = new TextFieldWithGhostText("Port", 20);
	}
	
	private void createGUI() {
		this.setLayout(new GridLayout(3, 1, 0, 10));
		this.setBorder(BorderFactory.createTitledBorder("  Desktop Communicator  "));
		this.add(ipAddressField);
		ipAddressField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, this.getBackground()));
		this.add(portField);
		portField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, this.getBackground()));
		this.add(connectButton);
		connectButton.setBorder(new EmptyBorder(2, 2, 2, 2));
		//connectButton.setBorder(BorderFactory.createMatteBorder(1,1,1,1,this.getBackground()));
	}
	
	private void addEvents() {
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO add connection to the desktop listener
			}
		});
	}
	
	public static void main(String [] args) {
		new Communicator();
	}

}
