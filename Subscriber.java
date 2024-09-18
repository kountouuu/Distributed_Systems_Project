package Broker;
import RawGetersAndOurAddOns.Chunk;
import java.awt.Desktop;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
public class Subscriber {
	// fields
	private int id;
	private int key;
	private ServerSocket server;
	private int port;
	// constructor
	public Subscriber(int id, int port) {
		this.id = id;
		this.port = port;
		System.out.println("Subscriber " + this.id + " created with key " + key);
	}
	// Register to Broker method
	public void register(String address, int port) {
		System.out.println("Registering to broker " + (port - 5000) + "...");
		try {
			Socket socket = new Socket(address, port);
			DataInputStream FromServer = new DataInputStream(socket.getInputStream());
			DataOutputStream ToServer = new DataOutputStream(socket.getOutputStream());
			// Send first message
			ToServer.writeByte(0);
			ToServer.writeUTF("Subscriber " + " " + this.id);
			ToServer.writeInt(this.id);
			ToServer.writeInt(this.port);
			ToServer.writeUTF(address);
			// TO ACCEPT
			System.out.println(FromServer.readUTF());
			// Send off the data
			ToServer.flush();
			// Send second message
			ToServer.writeByte(3);
			ToServer.writeUTF("This is the CLOSE message.");
			ToServer.flush(); // Send off the data
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Open the subscriber Server .
	public void openSubServer(String address, int port) {
		try {
			System.out.println("Server Started");
			server = new ServerSocket(port); // Opens the server
			server.setReuseAddress(true);
			// Waits for connection .
			boolean heAsked = false; // boolean that defines what the Subscriber will do .
			while (true) {
				// Wait for Content Creators or Subscribers to connect.
				System.out.println("Subscriber with id = " + (port - 4000) + " Started and waiting... ");
				Socket connection = server.accept();
				if (!heAsked) {
					// Establishes connections and initializes streams .
					System.out.println("New client connected with the IP address of : "
							+ connection.getInetAddress().getHostAddress());
					PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					Scanner scanner = new Scanner(System.in);
					// vars that are needed as an input from subscriber ( choice & DowOrStream )
					String line;
					String choice;
					String DowOrStream;
					ArrayList<String> videos = new ArrayList<String>();
					while ((line = in.readLine()) != null && !line.equals("EndOfList")) {
						System.out.println(line);
						videos.add(line);
					}
					//Handling Subscriber's input, hashtag or video request
					System.out.println("What video do you want to watch. You can also choose a hashtag to choose from all related videos . ");
					// Users Choice of video or hashtag
					choice = scanner.nextLine();
					boolean found = true;
					// checking if he choose something from the list, if not he is asked for input
					// again
					while (found) {
						for (String nameOrHastag : videos) {
							String[] arrOfStr = nameOrHastag.split("/", 2);

							String kept =arrOfStr[0];
							if (nameOrHastag.equalsIgnoreCase(choice) ||kept.equalsIgnoreCase(choice) ) {
								found = false;
							}
						}
						if (found == true) {
							System.out.println(
									"Video or hashtag is not in the list! Please choose a video or hashtag that exists in the list above . ");
							choice = scanner.nextLine();
						}
					}

					String firstLetter = String.valueOf(choice.charAt(0));
					//If the user chose a hashtag
					if(firstLetter.equals("#")) {
						ArrayList<String> Relatedvideos = new ArrayList<>();
						System.out.println("These are the videos that are related with your #hashtag. Please choose one of them . ");
						for (String nameOrHastag : videos) {
							String[] arrOfStr = nameOrHastag.split("/", 2);
							String[] kept =arrOfStr;
							if (kept[0].equalsIgnoreCase(choice)) {
								String NameOfVideos =kept[1];
								System.out.println(NameOfVideos); //prints out the specified video of the specific hashtag .
								Relatedvideos.add(NameOfVideos);
							}
						}
						choice = scanner.nextLine();
						boolean heChosetherightRelated= true;
						// checking if he choose something from the list, if not he is asked for input
						// again
						while (heChosetherightRelated) {
							for (String nameOrHastag : Relatedvideos) {
								if (nameOrHastag.equalsIgnoreCase(choice)) {
									heChosetherightRelated= false;
								}
							}
							if (heChosetherightRelated== true) {
								System.out.println(
										"Video is not in the related list! Please choose a video that exists in the related list above .");
								choice = scanner.nextLine();
							}
						}
					}//END OF Handling Subscriber's input, hashtag or video request


					// Users choice whether he wants to download it or stream it .
					System.out.println("Do you want to stream it or u want to download it locally? Type S to Stream it, or D just to Download!");
					DowOrStream = scanner.nextLine();
					//checks if the input is D or S
					while(!(DowOrStream.equalsIgnoreCase("D")|| DowOrStream.equalsIgnoreCase("S"))){
						System.out.println("Wrong Input! Type S to Stream it, or D just to Download!");
						DowOrStream = scanner.nextLine();
					}
					String ItWasAPullRequest = "pull";
					System.out.println("Connecting to the broker in charge");
					// Sending off the Data .
					out.println(choice);
					out.println(DowOrStream);
					out.println(ItWasAPullRequest);
					out.println(address);
					out.println(this.port);
					heAsked = true; // Changes the value to true , so it can access the else block .
				} else {

					ObjectInputStream dIn = new ObjectInputStream(connection.getInputStream());
					// reads Total Data from the thread handler .
					int TotalData = dIn.readInt();
					//Get the name of the video
					String VidChoice = dIn.readUTF();

					// Creates the file in the specified Path of the out stream .
					FileOutputStream out = new FileOutputStream("C:\\Users\\dimit\\Desktop\\save\\"+ VidChoice + ".mp4");
//FileOutputStream out = new FileOutputStream("your path"+ VidChoice + ".mp4");
					System.out.println("Receiving video ......");
					while (TotalData > 0) { // While bytes of the file remain , repeat .
						int chunksize = 0;
						Chunk ThisChunk = (Chunk) dIn.readObject();
						chunksize = ThisChunk.getBytechunk().length;
						TotalData = TotalData - chunksize;
						out.write(ThisChunk.getBytechunk()); // Sending the chunks of the file requested
					}
					System.out.println("Video was sent successfully !"); // success message
					String DorS = dIn.readUTF();
					//if true, we stream the video..
					if(DorS.equalsIgnoreCase("S")) {
						Desktop.getDesktop().open(new File("C:\\Users\\dimit\\Desktop\\save\\"+VidChoice+ ".mp4"));
					}
					else { //if not we download the video
						System.out.println("Thank you !");
					}
				}
			}
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