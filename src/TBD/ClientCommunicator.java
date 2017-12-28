package TBD;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class ClientCommunicator.
 * Sits on a client communicator and sends messages to a server.
 * ---This class is currently not needed but may be needed for future expansion of this project.--
 * @author devan
 */
public class ClientCommunicator extends Thread {

	//member fields
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;

	/**
	 * Constructor
	 * @param ipAddress String
	 * @param port int
	 */
	public ClientCommunicator(String ipAddress, int port) {
		try {
			socket = new Socket(ipAddress, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		}
		catch (UnknownHostException e) {System.out.println(e.getMessage());} 
		catch (IOException e) {System.out.println(e.getMessage());}
	}
	
	/**
	 * Runs the client communicator thread to constantly receive messages.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				String s = (String)ois.readObject();
				System.out.println("String received: " + s);
			}
			
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	/**
	 * Sends messages using the socket built in our constructor.
	 * @param s String
	 * @throws IOException
	 */
	public void sendMessage(String s) throws IOException {
		oos.writeObject(s);
		oos.flush();
	}
}
