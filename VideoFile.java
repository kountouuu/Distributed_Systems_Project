package RawGetersAndOurAddOns;

import com.sun.istack.SAXException2;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

//VideoFile class
public class VideoFile implements Serializable {
	private String videoName;
	private String channelName;
	private String dateCreated;
	private String lenght;
	private String framerate;
	private String frameWidth;
	private String frameHeight;
	private ArrayList<String> associatedHastags = new ArrayList<>();
	private byte[] videoFileChunk;
	public byte[] getVideoFileChunk() {
		return videoFileChunk;
	}
	public void setVideoFileChunk(byte[] videoFileChunk) {
		this.videoFileChunk = videoFileChunk;
	}
	// VideoFile Constructor
	//default constructor
	public VideoFile() {}
	public VideoFile(String VideoPath) throws IOException {
		//Detecting the file type .
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(new File(VideoPath));
		ParseContext pcontext = new ParseContext();

		// HTML Parser
		MP4Parser MP4Parser = new MP4Parser();
		try {
			MP4Parser.parse(inputstream, handler, metadata, pcontext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//initialize the String array of metadata .
		String[] metadataNames = metadata.names();

		for (String name : metadataNames) { //for loop that initializes metadata .

			switch (name) {
				case "title" -> this.videoName = metadata.get(name);
				case "Creation-Date" -> this.dateCreated = metadata.get(name);
				case "xmpDM:duration" -> this.lenght = metadata.get(name);
				case "xmpDM:audioSampleRate" -> this.framerate = metadata.get(name);
				case "tiff:ImageWidth" -> this.frameWidth = metadata.get(name);
				case "tiff:ImageLength" -> this.frameHeight = metadata.get(name);
			}
		}
	}

	//Getters-Setters
	public String getVideoName() {
		return videoName;
	}

	public ArrayList<String> getAssociatedHastags() {
		return associatedHastags;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public void setAssociatedHastags(ArrayList<String> associatedHastags) {
		this.associatedHastags = associatedHastags;
	}

	//Generated toString
	@Override
	public String toString() {
		return "VideoFile [videoName=" + videoName + ", channelName=" + channelName + ", dateCreated=" + dateCreated
				+ ", lenght=" + lenght + ", framerate=" + framerate + ", frameWidth=" + frameWidth + ", frameHeight="
				+ frameHeight + ", associatedHastags=" + associatedHastags + ", videoFileChunk="
				+ Arrays.toString(videoFileChunk) + "]";
	}

}
