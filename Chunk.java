package RawGetersAndOurAddOns;

import java.io.Serializable;
//class Chunk
public class Chunk implements Serializable {
	//field
	private byte[] bytechunk;
	//Constructor
	public Chunk(byte[] Bytechunk){
		this.bytechunk = Bytechunk;
	}
	//Methods
	public byte[] getBytechunk() {
		return bytechunk;
	}

}
