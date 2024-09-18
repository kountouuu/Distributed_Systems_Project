package ThreadHandlers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import Broker.Broker;
import RawGetersAndOurAddOns.Pair;
import RawGetersAndOurAddOns.PersonType;
import RawGetersAndOurAddOns.Value;
import RawGetersAndOurAddOns.VideoFile;

// Content Creator Thread Handler
public class Content_Creator_Thread_Handler implements Runnable {
	private final Socket clientSocket;
	// Constructor
	public Content_Creator_Thread_Handler(Socket clientSocket) {

		this.clientSocket = clientSocket;
	}
	//run method
	public void run() {
		//Broker Thread
		System.out.println("Broker's Thread says:  ");
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			// Initialize Stream to appropriate socket .
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String line;
			DataOutputStream OutToClientToGo;

			//Writes out to Content Creator
			OutToClientToGo = new DataOutputStream(clientSocket.getOutputStream());
			OutToClientToGo.writeByte(7);
			OutToClientToGo.writeByte(100);
			OutToClientToGo.flush(); // Send off the data
			while ((line = in.readLine()) != null) {
				System.out.println("Input from The creator was given!");
				if (!"Exit".equalsIgnoreCase(line)) {
					DataOutputStream OutToClient;
					DataInputStream InFromClient;

					OutToClient = new DataOutputStream(clientSocket.getOutputStream());

					ObjectInputStream FromClientObject = new ObjectInputStream(clientSocket.getInputStream());
					//Get the VideoFile from the Content Creator and adding it to AllVideoObjects and AllVideoValues
					VideoFile temp = (VideoFile) FromClientObject.readObject();
					PersonType theCC = (PersonType) FromClientObject.readObject();
					String ThePath = FromClientObject.readUTF();

					Broker.CreatePairings(temp,theCC,ThePath);
					Broker.AllVideoObjects.add(temp);
					Broker.AllVideoValues.add(new Value(temp));
					//Printing the Video files
					for (Pair d : Broker.BrokersTopicInCharge) {
						System.out.println(d.toString());
					}
					InFromClient = new DataInputStream(clientSocket.getInputStream());

				} else {
					System.out.println("Out of the while");
				}
			}
			//exceptions handler
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} // TODO Auto-generated catch block
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
