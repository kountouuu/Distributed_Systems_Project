package Interactive_People;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Broker.Broker;


public class Broker1 {

	// Get system's IP
	// ---------------------------------------------------------------------
	private static String getSystemIP() {
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost(); // Get localhost
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (inetAddress != null) {
			return (inetAddress.getHostAddress()); // Ping localhost to get HostAddress and return it
		}
		return null;
	}

	// ---------------------------------------------------------------------
	private static final String IP = getSystemIP();// If this doesn't work, put your IP manually.
	public static final int serverPort = 5001;

	public static void main(String[] args) throws ClassNotFoundException, UnknownHostException, IOException {
		// Create broker 1
		Broker b1 = new Broker(1, args,serverPort,IP,serverPort - 5000);

		// Start the server
		b1.openServer(serverPort);
	}
}
