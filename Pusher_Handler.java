package ThreadHandlers;


import RawGetersAndOurAddOns.PersonType;
import RawGetersAndOurAddOns.VideoFile;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//TALKS WITH Content_Creator_Thread_Handler
public class Pusher_Handler implements Runnable {
	// Pusher Handler Class

	private final Socket clientSocket;
	private final PersonType cc;
	public Pusher_Handler(Socket clientSocket, PersonType cc) {
		this.cc =cc;
		this.clientSocket = clientSocket;
	}
	//Constructor
	
	//Invalid Path checker
	public static boolean isPathValid(String path) {

		File tempFile = new File(path);
		boolean exists = tempFile.exists();
		return exists;
    }
	public void VideoInitializer(VideoFile videoFile, Socket connection,String Path)
			throws IOException {
		//Create an array to pass the hastags to VideoFile hastags field
		ArrayList<String> hashtags = new ArrayList();

		//Create a stream from the Content Creator to the server

		ObjectOutputStream ToServerObject = new ObjectOutputStream(connection.getOutputStream());
		String line;
		System.out.println("Please give hashtags for your video: Type Exit to exit the procedure .");
		Scanner input = new Scanner(System.in);
		line = input.nextLine();
		//tests input
		while (!line.equalsIgnoreCase("Exit")) {
			hashtags.add(line);
			System.out.println("Please give more associated hashtags for your video: Type Exit to exit the procedure . ");
			line = input.nextLine();
		}
		System.out.println("Thank you, to continue, please give your Channel Name .");

		videoFile.setAssociatedHastags(hashtags);
		line = input.nextLine();
		videoFile.setChannelName(line);

		System.out.println("Thank you, your Video is uploaded!\n");
		//Send off data to broker .
		ToServerObject.writeObject(videoFile);
		ToServerObject.writeObject(cc);
		ToServerObject.writeUTF(Path);
		ToServerObject.flush();
	}
	public void run() {
		//Initialize in and out streams for server
		PrintWriter out = null;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner scanner = new Scanner(System.in);
		String line = null;
		//checks input
		while (!"exit".equalsIgnoreCase(line)) {
			System.out.println("Please give the video path of the file that you want to upload or type exit if you are done uploading . ");
			//Reading the song's path CC wants to upload from keyboard
			line = scanner.nextLine();
			String Path= line;
			while(!isPathValid(Path)) {
				System.out.println(isPathValid(Path));
				System.out.println("This path is wrong! Please give a valid Path ");
				line = scanner.nextLine();
				Path= line;
			}
			//sending the path to Broker's Thread
			out.println(line);
			out.flush();
			// if user gave a path and he didn't exit then initialize the Video LINE 64
			if (!"exit".equalsIgnoreCase(line)) {
				try { //Writes the video through VideoInitializer
					VideoInitializer(new VideoFile(line), clientSocket,Path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Thank you for uploading ! You will now exit the uploading procedure .");
		scanner.close();
	}
}
