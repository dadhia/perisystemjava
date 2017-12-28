package TBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLConnector {
	
	private Connection connection;
	private static final String makeLog = "INSERT INTO log (sourceID, time, message) VALUES (?,?,?)";
	public SQLConnector() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://localhost/Peri?useSSL=false", "root", "root");
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void logUpdateMessage(int sourceIdentifierID, String message, Timestamp t)
			throws SQLException {
		PreparedStatement ps = connection.prepareStatement(makeLog);
		ps.setInt(1, sourceIdentifierID);
		ps.setTimestamp(2, t);
		if(message.length() > 150) {
			message = message.substring(0, 150);
		}
		ps.setString(3, message);
		ps.executeUpdate();
	}
}