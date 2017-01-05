package stockGenie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientCommunicator extends Thread {

	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
//this secomnd 
	public ClientCommunicator(String ipAddress, int port) {
		try {
			socket = new Socket(ipAddress, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {System.out.println(e.getMessage());} 
		catch (IOException e) {System.out.println(e.getMessage());}
	}
	
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
	
	public void sendMessage(String s) throws IOException {
		oos.writeObject(s);
		oos.flush();
	}
}
