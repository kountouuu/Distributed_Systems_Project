package ThreadHandlers;

import RawGetersAndOurAddOns.Chunk;

import java.io.*;
import java.net.Socket;

// Content Creator Connector Thread Handler class
public class ContentCreatorConnector_Thread_Hanlder implements Runnable {


	private final Socket clientSocket;
	private String videoChoice;
	private String streamOrDowChoice;
	private String subAddress;
	private String subPort;
	private String typeofRequest;
	private String thePath;
	private Socket socket;

	// Constructor
	public ContentCreatorConnector_Thread_Hanlder(Socket clientSocket, String videoChoice, String streamOrDowChoice,
												  String subAddress, String subPort, String typeofRequest, String thePath, Socket socket) {

		this.clientSocket = clientSocket;
		this.videoChoice = videoChoice;
		this.streamOrDowChoice = streamOrDowChoice;
		this.subAddress = subAddress;
		this.subPort = subPort;
		this.typeofRequest = typeofRequest;
		this.thePath = thePath;
		this.socket = socket;
	}



	//run method
	public void run() {

		System.out.println("Broker Content Creator Connector_Thread_Hanlder says:  ");

		try {
			DataInputStream InFromClientToGo;
			DataOutputStream OutToClientToGo;

			//Writes the appropriate Byte for the messageType .
			OutToClientToGo = new DataOutputStream(clientSocket.getOutputStream());
			OutToClientToGo.writeByte(8);
			OutToClientToGo.writeUTF(thePath);
			OutToClientToGo.flush();

			OutToClientToGo.writeByte(100);
			OutToClientToGo.flush();

			//Receives the number of bytes in a fyle through dIn.
			ObjectInputStream dIn = new ObjectInputStream(clientSocket.getInputStream());
			int TotalData =dIn.readInt();
			int TotalDataFinal =TotalData ;

			
			//edw prepei na parei to connection
			//Socket connect_To_Subscriber = this.socket; //Establishes connection with subscriber.

			//Initialize the Output stream .
			ObjectOutputStream OutD = new ObjectOutputStream(this.socket.getOutputStream());
			OutD.writeInt(TotalDataFinal);
			OutD.writeUTF(this.videoChoice);
			//Break the bytes in pieces and send it .
			while (TotalData >0) {
				int chunksize;
				Chunk ThisChunk =(Chunk) dIn.readObject();
				chunksize = ThisChunk.getBytechunk().length;
				TotalData = TotalData - chunksize;

				OutD.writeObject(ThisChunk.getBytechunk()); //Writes data that is an instance of Chunk class .
				OutD.flush();
			}
			
			OutD.writeUTF(this.streamOrDowChoice);
			OutD.flush();
			
		}// Exception Handler
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
