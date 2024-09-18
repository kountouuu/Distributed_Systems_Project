package Interactive_People;

import Broker.Content_Creator;
import com.sun.istack.SAXException2;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Content_Creator3 {
	//Get system's IP
	//---------------------------------------------------------------------
	private static String getSystemIP(){
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost(); //Get localhost
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (inetAddress != null) {
			return(inetAddress.getHostAddress()); //Ping localhost to get HostAddress and return it
		}
		return null;
	}
	//---------------------------------------------------------------------
	//fields
	public static final int  Creator_id =3;
	public static final int    Creator_port = 3000;

	public static final int    portToBroker1  = 5001;
	public static final int    portToBroker2  = 5002;
	public static final int    portToBroker3  = 5003;
	private static final String IP = getSystemIP();
	public static void main(String[] args) throws SAXException2, TikaException, ClassNotFoundException {

		Content_Creator c3 =new Content_Creator(Creator_id,Creator_port +Creator_id,IP ,Creator_id);
		c3.register(IP, portToBroker1);
		c3.openCCServer(Creator_port+Creator_id);
	}
}
