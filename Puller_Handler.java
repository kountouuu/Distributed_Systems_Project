package ThreadHandlers;

import RawGetersAndOurAddOns.Chunk;
import org.apache.commons.compress.utils.IOUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Puller Handler class
public class Puller_Handler implements Runnable {
    // vars
    private final Socket clientSocket;
    private String path;
    // Constructor
    public Puller_Handler(Socket clientSocket, String path) {
        this.path = path;
        this.clientSocket = clientSocket;
    }
    // run method
    public void run() {
        System.out.println(
                "Entering Puller Handler, a Content Creator's Thread Connected to Broker's  Content Creator Connector Thread  ");
        try {
            // Initialize the output stream so we can send the byte array of the file .
            ObjectOutputStream ToServerObject = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Video is being uploaded ....");
            FileInputStream getVid;
            try {
                getVid = new FileInputStream(path); // Creating a new File with the dedicated video path
                byte[] VideoBytes = IOUtils.toByteArray(getVid);

                int AllBytes = VideoBytes.length; // length of bytes in video.
                ToServerObject.writeInt(AllBytes);
                ToServerObject.flush();

                System.out.println(".....done ! # of bytes sent : " + AllBytes);
                int chunktobesent;

                int counter = 0;
                // for loop that breaks the byte array into buffer sized chunks and sends the
                // data off to the Content Creator Connector Thread Handler
                for(chunktobesent = 0; chunktobesent < AllBytes-2048; chunktobesent+= 2048){
                    byte[] data = new byte[2048];
                    counter =counter+data.length;
                    for(int j = 0; j < 2048; j++){
                        data[j] = VideoBytes[chunktobesent + j];

                    }
                    ToServerObject.writeObject(new Chunk(data));
                }
                // Send the remaining data .
                if (AllBytes - counter == 0) {


                }
                else if(AllBytes-counter>0) {
                    byte[] data = new byte[AllBytes-counter];
                    for(int j = 0; j < AllBytes-counter; j++){
                        data[j] = VideoBytes[chunktobesent + j];

                    }
                    ToServerObject.writeObject(new Chunk(data));
                }
                // Exception Handler
            } catch (IOException ioe) {
            } finally {
                // close things
            }
            // Exception Handler second try
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // finally block .
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}