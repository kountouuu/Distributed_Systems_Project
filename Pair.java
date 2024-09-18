package RawGetersAndOurAddOns;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//class PAir
public class Pair implements Serializable {
	private PersonType TheBroker;
	private PersonType The_CC;
	private VideoFile Videofile;
	private String HashIport;
	private String ChanelNameHash;
	private String Path;

	//constructor
	public Pair(PersonType theBroker, VideoFile Videofile, PersonType theCC, String thePath) {
		this.The_CC =theCC;
		this.Videofile = Videofile;
		this.TheBroker =theBroker;
		this.HashIport = hashGenerator(theBroker.toString());
		this.ChanelNameHash = hashGenerator(Videofile.getChannelName());
		this.Path = thePath;
	}

	private String hashGenerator(String str) { //method that generates a hash given a String
		String generatedHash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1)); //MD5 method of hashing a String
			}
			generatedHash = sb.toString(); //Builds the hash
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedHash;
	}

	//GETTERS-SETTERS
	public PersonType getThe_CC() {
		return The_CC;
	}

	public PersonType getTheBroker() {
		return TheBroker;
	}

	public VideoFile getVideofile() {
		return Videofile;
	}

	public void setVideofile(VideoFile videofile) {
		Videofile = videofile;
	}

	public String getHashIport() {
		return HashIport;
	}

	public String getChanelNameHash() {
		return ChanelNameHash;
	}

	public String getPath() {
		return Path;
	}

	@Override
	public String toString() {
		return "Pair [TheBroker=" + TheBroker + TheBroker.address+"   "+TheBroker.port+ ", The_CC=" + The_CC +The_CC.address+The_CC.port+ ", Videofile=" + Videofile + ", HashIport="
				+ HashIport + ", ChanelNameHash=" + ChanelNameHash + Path +"]";
	}
}
