package Interactive_People;

import java.net.InetAddress;
import java.net.UnknownHostException;

import Broker.Subscriber;

public class Subscriber2 {

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
	// fields

	//
	public static final int Subscriber_id = 2;
	public static final int Subscriber_port = 4000;

	public static final int portToBroker = 5000;

	private static final String IP = getSystemIP();

	public static void main(String[] args) {
		  int min = 1;
	      int max = 3;
	      int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
		Subscriber s2 = new Subscriber(Subscriber_id, Subscriber_port + Subscriber_id);
		
		s2.register(IP, portToBroker+2);
		
		s2.openSubServer(IP, Subscriber_port + Subscriber_id);

	}

}
