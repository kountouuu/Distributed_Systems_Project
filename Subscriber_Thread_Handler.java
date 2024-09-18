																																																																													package ThreadHandlers;

import Broker.Broker;
import RawGetersAndOurAddOns.Chunk;
import RawGetersAndOurAddOns.Pair;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//This class represents a thread of Broker Class in contact with the server of the Content Creator
// Subscriber Thread Handler
public class Subscriber_Thread_Handler implements Runnable {
	
	public Socket getSocket (){
		return clientSocket;}
	
	
	private final Socket clientSocket;
	// Constructor
	public Subscriber_Thread_Handler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	//run method
	public void run() {
		System.out.println("Broker's Thread says:  ");
		try {
			// ----------------------------------------------------------------------------
			//Communication with subscriber
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			Scanner 	scanner = new Scanner(System.in);
			String line;
			out.println("Here is a list of videos that were uploaded by various Content Creators : ");

			for(Pair val: Broker.BrokersTopicInCharge) { //for loop prints all the available videos for the subscriber to see
				line = val.getVideofile().getVideoName();
				out.println(line);
				out.flush();

			}
			out.println("Here is a list of all hastags that were uploaded by various Content Creators : ");
			for(Pair val2: Broker.BrokersTopicInCharge) { //for loop prints all the available hastags for the subscriber to see
				ArrayList<String> arraytemp = val2.getVideofile().getAssociatedHastags();
				for(String hashtag: arraytemp) {
					out.println( "#" + hashtag + "/" +val2.getVideofile().getVideoName());
					out.flush();

				}


			}
			line ="EndOfList";
			out.println(line);
			out.flush();
			//subscriber chooses which video he wants to see and if he wants to download it or stream it .
			String VideoChoice = in.readLine();
			String StreamOrDowChoice = in.readLine();
			String TypeofRequest = in.readLine();
			String SubAddress = in.readLine();
			String SubPort= in.readLine();
			//Initializing the necessary variables to establish the connection .
			int brokerInChargePort=0;
			String brokerInChargeAddress=null;
			int ContentCreatorInChargePort=0;
			String ContentCreatorInChargeAddress=null;
			String PathGet = null;
			byte[] VideoBytes = null ;
			System.out.println(" " +  VideoChoice);
			//When you find The name on pair Arraylist, then get the Broker's in Charge Port.
			for(Pair p: Broker.BrokersTopicInCharge) {
				if(p.getVideofile().getVideoName().equalsIgnoreCase(VideoChoice)) {
					brokerInChargePort = p.getTheBroker().getPort();
					brokerInChargeAddress =p.getTheBroker().getAddress();
					ContentCreatorInChargePort = p.getThe_CC().getPort();
					ContentCreatorInChargeAddress = p.getThe_CC().getAddress();
					PathGet = p.getPath();
					VideoBytes = p.getVideofile().getVideoFileChunk();
				
				}
			}
			System.out.println("size : " +VideoBytes.length);
			//Printing the values of the accountable Brokers / Content Creators of the video chosen .
			System.out.println("Port of the broker in charge : " + brokerInChargePort);
			System.out.print(" " + brokerInChargeAddress);
			System.out.println("Port of the Content Creator in charge : " + ContentCreatorInChargePort);
			System.out.print(" " + ContentCreatorInChargeAddress);
			System.out.println("Subscriber address + port : "+SubAddress + " + " + SubPort);
			
			//----------------------------------------------------------------------------------------
			
			 ObjectOutputStream ToServerObject = new ObjectOutputStream(clientSocket.getOutputStream());
			 
			
			 int chunktobesent;
			 int AllBytes =VideoBytes.length;
			 ToServerObject.writeInt(AllBytes);
             int counter = 0;
             // for loop that breaks the byte array into buffer sized chunks and sends the
             // data off to the Content Creator Connector Thread Handler
             for(chunktobesent = 0; chunktobesent < AllBytes-2048; chunktobesent+= 2048){
                 byte[] data = new byte[2048];
                 counter =counter+data.length;
                 for(int j = 0; j < 2048; j++){
                     data[j] = VideoBytes[chunktobesent + j];

                 }
                 ToServerObject.writeObject(data);
             }
             // Send the remaining data .
             if (AllBytes - counter == 0) {


             }
             else if(AllBytes-counter>0) {
                 byte[] data = new byte[AllBytes-counter];
                 for(int j = 0; j < AllBytes-counter; j++){
                     data[j] = VideoBytes[chunktobesent + j];

                 }
                 ToServerObject.writeObject(data);
             }
			
			//----------------------------------------------------------------------------------------
			
			
			
			//Creating a Broker socket so we can send the Content Creator info.
			/*Socket connectToBrokerInCharge =new Socket(brokerInChargeAddress, brokerInChargePort);
			DataInputStream FromServer = new DataInputStream(connectToBrokerInCharge.getInputStream());
			DataOutputStream ToServer = new DataOutputStream(connectToBrokerInCharge.getOutputStream());
			
			
			System.out.println(
					" IP and Port of Content Creator in charge : " + ContentCreatorInChargeAddress + ", "
							+ ContentCreatorInChargePort + "\n Video that was chosen by the subscriber :  "
							+ VideoChoice + " --- Stream or Download : " + StreamOrDowChoice
							+ "\n Subscriber Address + Port : " + SubAddress + ", " + SubPort
							+ "\n Type of request : " + TypeofRequest + "\n Path of the File : " + PathGet);
			System.out.println("Connecting to the Consumer in charge....");
			
			ContentCreatorConnector_Thread_Hanlder ContentCreatorConnector = new ContentCreatorConnector_Thread_Hanlder(
					new Socket(ContentCreatorInChargeAddress, ContentCreatorInChargePort), VideoChoice,
					StreamOrDowChoice, SubAddress, SubPort, TypeofRequest,PathGet,getSocket());
			// This thread will handle the broker thread separately
			new Thread(ContentCreatorConnector).start();

			*/
	/*		
			ToServer.writeByte(2);
			//Write the values : In Charge CC Address, po rt and Subscribers VideoChoice and his decision to stream it or download it and Sub Address and Sub port
			ToServer.writeUTF(ContentCreatorInChargeAddress);
			ToServer.writeInt(ContentCreatorInChargePort);
			ToServer.writeUTF(VideoChoice);
			ToServer.writeUTF(StreamOrDowChoice);
			ToServer.writeUTF(SubAddress);
			ToServer.writeUTF(SubPort);
			ToServer.writeUTF(TypeofRequest);
			ToServer.writeUTF(PathGet);
			ToServer.flush(); //Send off the data.*/
			//Exceptions handler .
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}