package Broker;

import RawGetersAndOurAddOns.Chunk;
import RawGetersAndOurAddOns.Pair;
import RawGetersAndOurAddOns.PersonType;
import RawGetersAndOurAddOns.Value;
import RawGetersAndOurAddOns.VideoFile;
import ThreadHandlers.ContentCreatorConnector_Thread_Hanlder;
import ThreadHandlers.Content_Creator_Thread_Handler;
import ThreadHandlers.Subscriber_Thread_Handler;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class Broker {
	// fields----------------------------------------------------------------------------<

	private ServerSocket server;
	private final int id;
	private final int port;
	private String ip;

	@Override
	public String toString() {
		return "Broker [id=" + id + ", port=" + port + "]";
	}

	// This is an Arraylist with paired values, Broker-Value In charge
	public static ArrayList<Pair> BrokersTopicInCharge = new ArrayList<>();
	// This is an Arraylist with all Brokers Available
	public static ArrayList<PersonType> BrokersAvailable = new ArrayList<>();
	// This is an Arraylist that stores all VideoFiles that Content Creators upload.
	public static ArrayList<VideoFile> AllVideoObjects = new ArrayList<>();
	// This is an ArrayList that stores all Values of the Videofiles .
	public static ArrayList<Value> AllVideoValues = new ArrayList<>();
	// ArrayList with all the registered Subscribers
	ArrayList<PersonType> registeredSubscribers = new ArrayList<>();
	// ArrayList with all the registered Content Creators
	ArrayList<PersonType> registeredContentCreators = new ArrayList<>();

	// METHODS--------------------------------------------------------------------<

	// Adds the Content Creator to ArrayList
	public void acceptContent_CreatorConection(String address, int id, int port) {
		registeredContentCreators.add(new PersonType(address, id, port));
		System.out.println("Content creator with id = " + id + " just registered");
	}

	// Adds the Subscriber to ArrayList
	public void acceptSubscriberConection(String address, int id, int port) {
		registeredSubscribers.add(new PersonType(address, id, port));
		System.out.println("Subscriber  with id = " + id + " just registered!");
	}

	// Content_Creator_Thread_Handler sends every video which is uploaded here and
	// checks for every broker if it's in charge
	public static void CreatePairings(VideoFile v, PersonType theCC, String thePath) throws UnknownHostException, IOException, ClassNotFoundException {
		boolean iTfoundPair =false; //boolean value that checks if the video has a broker in charge .
		for (PersonType br : BrokersAvailable) {
			 iTfoundPair = createpair(new Pair(br, v, theCC, thePath)); //creates the pair through the create pair method
			 if(iTfoundPair) {
			 break; //if it has found a pair then exit the loop .
			 }
		}
		if(!iTfoundPair) { //if it hasn't found a pair then add to a random broker .
			try{
			Random r = new Random();
			int high = BrokersAvailable.size()-1; // high = size of brokers available array list .
			int result = r.nextInt(high); //random number between 0-2
			InetAddress ip; //instantiate an ip object to get the local host IP
			if(result==0) {
				//create a connection with the ListNotifier class .
				ip = InetAddress.getLocalHost();
		        BrokersTopicInCharge.add(new Pair(BrokersAvailable.get(0), v, theCC, thePath)); //Add a new Pair object to the Brokers Topic in Charge ArrayList .
		        Socket notify = new Socket(ip, 10000); //creates a connection with the ListNotifier Class .
				ObjectOutputStream UpdateObject = new ObjectOutputStream(notify.getOutputStream()); //instantiates the OutputStream Object .
				UpdateObject.writeInt(14); //Writes to the (messageByte == 14) block in ListNotifier to update the Broker pairs .
				
				UpdateObject.writeObject(BrokersTopicInCharge); //Writes the object to the Output Stream .
				UpdateObject.flush(); //Sends off the data .
				ObjectInputStream GetArrayPairObject = new ObjectInputStream(notify.getInputStream()); //Initializes and Creates a new Input stream to accept the
				BrokersTopicInCharge= (ArrayList<Pair>) GetArrayPairObject.readObject();			   // Arraylist and modify the value of BrokersTopicInCharge .
		        }	//same for the rest random cases , 1 and 2
		        else if(result == 1) {
				ip = InetAddress.getLocalHost();
		        BrokersTopicInCharge.add(new Pair(BrokersAvailable.get(1), v, theCC, thePath));
		        Socket notify = new Socket(ip, 10000);
				ObjectOutputStream UpdateObject = new ObjectOutputStream(notify.getOutputStream());
				UpdateObject.writeInt(14);
					
				UpdateObject.writeObject(BrokersTopicInCharge);
				UpdateObject.flush();
				ObjectInputStream GetArrayPairObject = new ObjectInputStream(notify.getInputStream());
				BrokersTopicInCharge= (ArrayList<Pair>) GetArrayPairObject.readObject();
		        }
		        else if(result ==2) {
				ip = InetAddress.getLocalHost();
		        BrokersTopicInCharge.add(new Pair(BrokersAvailable.get(2), v, theCC, thePath));
		        Socket notify = new Socket(ip, 10000);
				ObjectOutputStream UpdateObject = new ObjectOutputStream(notify.getOutputStream());
				UpdateObject.writeInt(14);
					
				UpdateObject.writeObject(BrokersTopicInCharge);
				UpdateObject.flush();
				ObjectInputStream GetArrayPairObject = new ObjectInputStream(notify.getInputStream());
				BrokersTopicInCharge= (ArrayList<Pair>) GetArrayPairObject.readObject();
		        }
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// Adds the pair to the Arraylist of brokers that are in charge of files
	private static boolean createpair(Pair pair) throws UnknownHostException, IOException, ClassNotFoundException {
		//checks the IP + port hash with the channel name Hash and adds the pair to the equivalent broker .
		//Same with the cases above .
		if (pair.getHashIport().compareTo(pair.getChanelNameHash()) > 0) {
			InetAddress ip;
			ip = InetAddress.getLocalHost();
			Broker.BrokersTopicInCharge.add(pair);
			Socket notify = new Socket(ip, 10000);
			ObjectOutputStream UpdateObject = new ObjectOutputStream(notify.getOutputStream());
			
			UpdateObject.writeInt(14); //Sends off the data to List Notifier .
			
			UpdateObject.writeObject(BrokersTopicInCharge);
			UpdateObject.flush();

			ObjectInputStream GetArrayPairObject = new ObjectInputStream(notify.getInputStream());
			BrokersTopicInCharge= (ArrayList<Pair>) GetArrayPairObject.readObject();
			System.out.println("Hashed and added the pair to the responsible Broker . ");
			return true; //returns to the caller method
		}
		return false; //returns to the caller method
		
	}
	//This is a method that notifies the list of every broker .
	public void NotifyBrokers() throws UnknownHostException, IOException, ClassNotFoundException {
		InetAddress ip;
		ip = InetAddress.getLocalHost();
		Socket notify = new Socket(ip, 10000);
		ObjectOutputStream ToNotifier = new ObjectOutputStream(notify.getOutputStream()); //Initializing and creating an object output stream to ListNotifier class.
		ToNotifier.writeInt(13); //Updating Brokers in ListNotifier class ,
		ToNotifier.writeUTF(this.ip); // with IP + PORT of equivalent broker .
		ToNotifier.writeInt(port); 
		ToNotifier.flush(); //Send off the data .
	}   
	
	
	
	// Broker Constructor
	public Broker(int id, String[] info, int port, String ip, int key)
			throws UnknownHostException, ClassNotFoundException, IOException {
		this.port = port;
		this.id = id;
		this.ip = ip;
		System.out.println("Broker " + this.id + " created with key " + key + "\n");
	}

	// This is Broker's multithreaded server, it opens threads that serve connection
	// according to their type .
	public void openServer(int port) {
		try {
			System.out.println("Server Started");
			server = new ServerSocket(port); // Opens new Server that belongs to the dedicated port of the broker .
			server.setReuseAddress(true);
			NotifyBrokers();
			DataInputStream InFromClient; // Input Stream
			DataOutputStream OutToClient; // OutputStream
			// Wait for connection
			while (true) {
				// Wait for Content Creators or Subscribers to connect.
				System.out.println("Awaiting requests from Content Creators or Subscribers... \n");
				System.out.println(server.getInetAddress());
                System.out.println(server.getLocalPort());
                System.out.println(server.getLocalSocketAddress());
				Socket connection = server.accept(); // Establishes Connection with either CC or Subscriber depending on
														// the messageType.
				
				System.out.println(connection.getLocalAddress());
     
				System.out.println("A new request!,");

				InFromClient = new DataInputStream(connection.getInputStream());
				OutToClient = new DataOutputStream(connection.getOutputStream());

				while (true) {
					byte messageType = InFromClient.readByte(); // Message that defines who wants to connect .

					if (messageType == 0) {
						// Starts retrieving info about subscriber .
						System.out.println("It was  a request from a subscriber with identifier :"
								+ InFromClient.readUTF() + "\n");
						int Sub_ID = InFromClient.readInt();
						int Sub_Port = InFromClient.readInt();
						String Sub_address = InFromClient.readUTF();

						acceptSubscriberConection(Sub_address, Sub_ID, Sub_Port); // Establishes socket connection with
																					// subscriber .
						OutToClient
								.writeUTF("Register request to Server with id = " + (port - 5000) + " is Accepted! \n"); // Writes
																															// to
						System.out.println(Sub_address+ "  "+Sub_ID+ "  "+ Sub_Port);																						// Sub

						
						
																															// client
																															// .
						//Socket  x = new Socket("10.0.2.16", 4001);
						
						Subscriber_Thread_Handler subcriberSock = new Subscriber_Thread_Handler(
								connection);
						
						
						// This thread will handle the client separately .
						new Thread(subcriberSock).start();

					} else if (messageType == 1) {
						// Retrieves info from content creator .
						System.out.println(
								"It was a request from a content creator with identifier: " + InFromClient.readUTF());

						int CC_ID = InFromClient.readInt();
						int CC_Port = InFromClient.readInt();
						String CC_address = InFromClient.readUTF();

						// Establishes connection with content creator .
						acceptContent_CreatorConection(CC_address, CC_ID, CC_Port);
						OutToClient
								.writeUTF("Register request to Server with id = " + (port - 5000) + " is Accepted! \n");

						// create a new thread object
						Content_Creator_Thread_Handler contentCreatorSock = new Content_Creator_Thread_Handler(
								new Socket(CC_address, CC_Port));

						// This thread will handle the client separately
						new Thread(contentCreatorSock).start();
					}
					
					//THIS IS FOR SUBSCRIBER SECOND PHASE 2-- ANDROID STUDIO
					else if (messageType == 33) {
						// Retrieves info that is needed to fulfill the download request .
						System.out.print(" A download/stream request from a subscriber !\n");
						// IP + PORT OF CC
						String ContentCreatorInChargeAddress = InFromClient.readUTF();
						int ContentCreatorInChargePort = InFromClient.readInt();
						// Choices of Subscriber and the video path .
						String VideoChoice = InFromClient.readUTF();
						String StreamOrDowChoice = InFromClient.readUTF();
						String SubAddress = InFromClient.readUTF();
						String SubPort = InFromClient.readUTF();
						String TypeofRequest = InFromClient.readUTF();
						String ThePath = InFromClient.readUTF();
						// Prints the data .
						System.out.println(
								" IP and Port of Content Creator in charge : " + ContentCreatorInChargeAddress + ", "
										+ ContentCreatorInChargePort + "\n Video that was chosen by the subscriber :  "
										+ VideoChoice + " --- Stream or Download : " + StreamOrDowChoice
										+ "\n Subscriber Address + Port : " + SubAddress + ", " + SubPort
										+ "\n Type of request : " + TypeofRequest + "\n Path of the File : " + ThePath);
						System.out.println("Connecting to the Consumer in charge....");
						
						ContentCreatorConnector_Thread_Hanlder ContentCreatorConnector = new ContentCreatorConnector_Thread_Hanlder(
								new Socket(ContentCreatorInChargeAddress, ContentCreatorInChargePort), VideoChoice,
								StreamOrDowChoice, SubAddress, SubPort, TypeofRequest, ThePath, connection);
						// This thread will handle the broker thread separately
						new Thread(ContentCreatorConnector).start();

					} 
					//END OF  SUBSCRIBER FROM PHASE 2- ANDROID STUDIO
					
					//THIS IS FOR CONTENT CREATOR SECOND PHASE 2-- ANDROID STUDIO
					else if (messageType == 43) {
						   //ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
						   // Retrieves info from content creator .
						   System.out.println(
						         "It was a request from a content creator from Android Studio with identifier : " + InFromClient.readUTF());
						   int CC_ID = InFromClient.readInt();
						   int CC_Port = InFromClient.readInt();
						   String CC_address = InFromClient.readUTF();
						   //Receiving all Data from android in order to create a pair
						   //String TheVideoPath = InFromClient.readUTF();
						   String TheChanelName = InFromClient.readUTF();
						   String VideoName = InFromClient.readUTF();
						   String hashtag1 = InFromClient.readUTF();
						   String hashtag2 = InFromClient.readUTF();
						   String hashtag3 = InFromClient.readUTF();
						   String hashtag4 = InFromClient.readUTF();
						   int TotalData = InFromClient.readInt();
						   int chunksize= 0;
						   byte[] bytes = new byte[TotalData];						   
						   InFromClient.readFully(bytes, 0, bytes.length); // read the message
						   System.out.println(bytes.length);
						   //put hashtags to Array
						   ArrayList<String> hashToList = new ArrayList<>();
						   hashToList.add(hashtag1);
						   hashToList.add(hashtag2);
						   hashToList.add(hashtag3);
						   hashToList.add(hashtag4);
						  
						//   CreatePairings(VideoFile v, PersonType theCC, String thePath)
						   //create VideoFile
						   VideoFile vidFileFromPhase2 = new VideoFile();
						   vidFileFromPhase2.setAssociatedHastags(hashToList);
						   vidFileFromPhase2.setChannelName(TheChanelName);
						   vidFileFromPhase2.setVideoName(VideoName);
						   vidFileFromPhase2.setVideoFileChunk(bytes);
						   
						CreatePairings( vidFileFromPhase2,new PersonType(CC_address, CC_ID, CC_Port),"notPathNeededForPhase2") ;  
						   // Establishes connection with content creator .
						   acceptContent_CreatorConection(CC_address, CC_ID, CC_Port);
						   OutToClient
						         .writeUTF("Register request to Server with id = " + (port - 5000) + " is Accepted! \n");

						}
					//END OF CONTENT CREATOR FROM PHASE 2- ANDROID STUDIO
					
					
					
					else if (messageType == 2) {
						// Retrieves info that is needed to fulfill the download request .
						System.out.print(" A download/stream request from a subscriber !\n");
						// IP + PORT OF CC
						String ContentCreatorInChargeAddress = InFromClient.readUTF();
						int ContentCreatorInChargePort = InFromClient.readInt();
						// Choices of Subscriber and the video path .
						String VideoChoice = InFromClient.readUTF();
						String StreamOrDowChoice = InFromClient.readUTF();
						String SubAddress = InFromClient.readUTF();
						String SubPort = InFromClient.readUTF();
						String TypeofRequest = InFromClient.readUTF();
						String ThePath = InFromClient.readUTF();
						// Prints the data .
						System.out.println(
								" IP and Port of Content Creator in charge : " + ContentCreatorInChargeAddress + ", "
										+ ContentCreatorInChargePort + "\n Video that was chosen by the subscriber :  "
										+ VideoChoice + " --- Stream or Download : " + StreamOrDowChoice
										+ "\n Subscriber Address + Port : " + SubAddress + ", " + SubPort
										+ "\n Type of request : " + TypeofRequest + "\n Path of the File : " + ThePath);
						System.out.println("Connecting to the Consumer in charge....");
						
						ContentCreatorConnector_Thread_Hanlder ContentCreatorConnector = new ContentCreatorConnector_Thread_Hanlder(
								new Socket(ContentCreatorInChargeAddress, ContentCreatorInChargePort), VideoChoice,
								StreamOrDowChoice, SubAddress, SubPort, TypeofRequest, ThePath, connection);
						// This thread will handle the broker thread separately
						new Thread(ContentCreatorConnector).start();

					} else if (messageType == 12) { //Adds each broker to temp array list then modifies the original BrokersAvailable array list .
						ArrayList<PersonType> brokerTemp = new ArrayList<>();
						String NotifierIP = InFromClient.readUTF();
						int NotifierPort = InFromClient.readInt();

						while (!NotifierIP.equalsIgnoreCase("End")) { //while the string End from ListNotifier isn't send , add to the arraylist .
							brokerTemp.add(new PersonType(NotifierIP, NotifierPort));

							NotifierIP = InFromClient.readUTF();
							NotifierPort = InFromClient.readInt();

						}

						BrokersAvailable = brokerTemp; //modifies original arraylist
						System.out.println("A new Broker connected! New Brokers that are available are : ");
						for (PersonType p : BrokersAvailable) {
							System.out.println(p.toString());
						}

					}else if (messageType == 16) {
						ObjectInputStream GetArrayPairObject = new ObjectInputStream(connection.getInputStream());
						BrokersTopicInCharge= (ArrayList<Pair>) GetArrayPairObject.readObject();
						System.out.println("List was renewed !");
					}
					else {
						break;
					}
				} // endofwhile
			} // exceptions handler
		} catch (IOException | ClassNotFoundException e) {
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
