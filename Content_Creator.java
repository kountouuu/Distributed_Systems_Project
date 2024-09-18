package Broker;

import RawGetersAndOurAddOns.PersonType;
import RawGetersAndOurAddOns.Value;
import RawGetersAndOurAddOns.VideoFile;
import ThreadHandlers.Puller_Handler;
import ThreadHandlers.Pusher_Handler;
import com.sun.istack.SAXException2;
import org.apache.tika.exception.TikaException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//Content Creator Class
public class Content_Creator {

	// fields-------------------------------------------------------------------------------------------<
	private int key;
	private PersonType cc;
	private String address;
	private ServerSocket server;
	//GETTERS-SETTERS
	public String getAddress() { return address; }

	public void setAddress(String address) { this.address = address; }

	public int getPort() { return port; }

	public void setPort(int port) { this.port = port; }


	private int id;
	private int port;
	// This is an Arraylist that stores all VideoFiles this Content Creator uploads.
	private ArrayList<VideoFile> CCVideoObjects = new ArrayList<>();
	//This is an Arraylist that stores all VideoFiles this Content Creator uploads
	private  ArrayList<Value> CCVideoValues = new ArrayList<>();
	//This is an ArrayList that defines the person type of the instance in contact .
	public static ArrayList<PersonType> persontypes = new ArrayList<>();

	// METHODS

//CREATE CONTENT CREATOR WITH ID AND PORT Constructor
	public Content_Creator(int id, int port,String address,int key) {
		this.port = port;
		this.id = id;
		 this.address = address;
		// CREATE THE PERSON TYPE OBJECT IN ORDER TO MAKE THE ARRAY WITH PERSON
		PersonType cc = new PersonType(address,port);
		this.cc =cc;
		persontypes.add(new PersonType("Content Creator", port, this.id));

		ArrayList<PersonType> Types = new ArrayList<>();

		System.out.println("	Content Creator with id " + this.id + " created with key " + key);
	}

	//Register to the given broker
	public void register(String address, int port) throws SAXException2, TikaException {
		System.out.println(" Sending Registering Request to broker " + (port - 5000) + "... \n");

		System.out.println("Registering to broker " + (port - 5000) + "... \n");
		try {
			//Establishing Connection and initializing streams
			Socket socket = new Socket(address, port);
			DataInputStream FromServer = new DataInputStream(socket.getInputStream());
			DataOutputStream ToServer = new DataOutputStream(socket.getOutputStream());
			Scanner input = new Scanner(System.in);
			String line = null;

			// Send first message
			ToServer.writeByte(1);
			ToServer.writeUTF("Content Creator " + " " + this.id);
			ToServer.writeInt(this.id);
			ToServer.writeInt(this.port);
			ToServer.writeUTF(address);

			// TO ACCEPT

			System.out.println(FromServer.readUTF());

			ToServer.flush(); // Send off the data

			// Send second message
			ToServer.writeByte(9);
			ToServer.writeUTF("This is the second type of message.");
			ToServer.flush(); // Send off the data

			// Send third message
			ToServer.writeByte(3);
			ToServer.writeUTF("This is the CLOSE message.");
			ToServer.flush(); // Send off the data



		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	//opening Content Creator Server
	public void openCCServer( int port) throws ClassNotFoundException {

		try {
			System.out.println("Server Started");
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			PrintWriter out = null;
			BufferedReader in = null;

			// while loop that awaits requests
			while (true) {

				// Wait for Broker Threads to connect.
				System.out.println("Awaiting requests from Broker Threads... \n");
				Socket connection = server.accept();
				System.out.println("A new request!");
				//initializing streams
				out = null;
				in = null;
				out = new PrintWriter(connection.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				DataInputStream InFromClient = new DataInputStream(connection.getInputStream());

				boolean done = true;

				while (done) {//While loop that specifies type of requests .

					byte messageType = InFromClient.readByte();

					if (messageType == 7) {
						System.out.println("It was  a request from a Broker Thread to upload (push) a Video");
						Pusher_Handler uploadThread= new Pusher_Handler(connection,this.cc);
						//This thread will handle the broker thread separately
						new Thread(uploadThread).start();

					} else if (messageType == 8) {
						String Path =InFromClient.readUTF();
						System.out.println("It was a request from a  broker thread to Pull a Video");
						Puller_Handler uploadThread= new Puller_Handler(connection,Path);
						//This thread will handle the broker thread separately .
						new Thread(uploadThread).start();
					}
					else {
						break;
					}
				}// End of while loop that specifies type of requests .

			}//End of While loop that awaits requests .
		} catch (IOException e) {//Exception Handler .
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}



}
