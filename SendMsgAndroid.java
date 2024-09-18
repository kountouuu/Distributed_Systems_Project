package Interactive_People;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SendMsgAndroid {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Scanner 	scanner = new Scanner(System.in);
		String line;
		
		System.out.println("Socket almost created");
		Socket  x = new Socket();
		
		x.connect(new InetSocketAddress("10.0.2.16", 10002),100000000);
		line=scanner.nextLine();
		System.out.println("Socket created");
		PrintWriter s = new PrintWriter(x.getOutputStream());
	    s.write(line);
	    s.flush();
	    s.close();
	}

}
