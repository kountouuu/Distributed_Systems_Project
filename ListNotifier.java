package Broker;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import RawGetersAndOurAddOns.Pair;
import RawGetersAndOurAddOns.PersonType;

//List Notifier class ---------------------------------------------------------------<
public class ListNotifier {
	//This is an arraylist that writes the connection .
	public static ArrayList<Socket> brokersCreatedConnections = new ArrayList<>();
	//This is an arraylist of all the brokers created .
	public static ArrayList<PersonType> Brokers = new ArrayList<>();
	//This is an arraylist that tracks the brokers that are in charge of some pairs .
	public static ArrayList<Pair> BrokersInChargePairs = new ArrayList<>();
	
	private static ServerSocket server;
	// Open the ListNotifier Server .
	public static void NotifyServer() {
		
		try {
			System.out.println("Notifier Started");
			server = new ServerSocket(10000); // Opens the notifier server .
			server.setReuseAddress(true);
			// Waits for connection .
			
			while (true) {
				//accepts connection and sends the messageType to Broker after handling the requests .
				Socket connection = server.accept();
				ObjectInputStream FromServerObject = new ObjectInputStream(connection.getInputStream());
				int messageType = FromServerObject.readInt();

				if (messageType ==13) { //Messagetype that notifies brokers .
					
					System.out.println("Notifying Brokers..");
					String IP = FromServerObject.readUTF();
					int port = FromServerObject.readInt();
					brokersCreatedConnections.add(connection); //Adds the created connection to the array list .
					Brokers.add(new PersonType(IP,port)); //adds to the broker list the equivalent broker .

					for(PersonType connected: Brokers) { //for every broker send out the details to the Broker class .
						Socket eachBroker = new Socket(connected.getAddress(),connected.getPort());
						DataOutputStream OutToClient =new DataOutputStream(eachBroker.getOutputStream());
						OutToClient.writeByte(12); //Writes back to the broker class the according message Type
						for(PersonType sendIpPort: Brokers) {							
							OutToClient.writeUTF(sendIpPort.getAddress());
							OutToClient.writeInt(sendIpPort.getPort());
							System.out.println("IP: " + sendIpPort.getAddress() +", Port: " +sendIpPort.getPort());
						}
						OutToClient.writeUTF("End"); //notifies that this is the end .
						OutToClient.writeInt(312); //messagetype to exit the while loop in Broker class
						OutToClient.flush();

						OutToClient.writeByte(123); //messagetype to exit the while loop in Broker class
						OutToClient.flush();
					}
				}else if(messageType ==14) { //If block that updates  the pairs in the Broker List .
					System.out.println("Notifying-Updating Brokers Pairs..");
					ArrayList<Pair> BrInChargeTemp; //temp broker value
					BrInChargeTemp = (ArrayList<Pair>) FromServerObject.readObject();
					System.out.println("Assigning the broker value to the broker in charge pairs array list . ");
					for(Pair p: BrInChargeTemp) {
						BrokersInChargePairs.add(p);
						System.out.println("...Broker assigned ! ");
					}
					ObjectOutputStream UpdateObject; //initialize output stream
					UpdateObject =new ObjectOutputStream(connection.getOutputStream());
					UpdateObject.writeObject(BrokersInChargePairs); //Writes to broker class the arraylist as an object .
					UpdateObject.flush();
						//Adds the IP + Port of each Broker to the Brokers Arraylist .
					for(PersonType sendIpPort: Brokers) {
						Socket eachBroker = new Socket(sendIpPort.getAddress(),sendIpPort.getPort());
						DataOutputStream OutToClient =new DataOutputStream(eachBroker.getOutputStream());
						OutToClient.writeByte(16); //Sending the appropriate message Type .
						OutToClient.flush();
						System.out.println("Brokers renewed . ");
						UpdateObject =new ObjectOutputStream(eachBroker.getOutputStream());
						UpdateObject.writeObject(BrokersInChargePairs);
						UpdateObject.flush();
						//sends the exit Message Type
						OutToClient.writeByte(123);
						OutToClient.flush();
							}
						BrokersInChargePairs.clear(); //Renews the templist for the next broker .
				}
			}
		//Exceptions Handler .
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally { // finally block .
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
