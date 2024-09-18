package RawGetersAndOurAddOns;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


//Value Class
public class Value {
	private VideoFile videofile;
	private final String hashkey;
	//Constructor
	public Value(VideoFile videofile ) {
		this.videofile = videofile;
		this.hashkey=generateHash(videofile.toString());
	}
	//METHODS
	public VideoFile getVideofile() {
		return videofile;
	}

	public void setVideofile(VideoFile videofile) {
		this.videofile = videofile;
	}

	private  String generateHash(String str) { //Hash Method that generates Strings
		String generatedHash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
			}
			generatedHash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedHash;
	}

	@Override
	public String toString() {
		return "Value [videofile=" + videofile + ", hashkey=" + hashkey + "]";
	}

}


