package RawGetersAndOurAddOns;

import java.io.Serializable;

public class PersonType implements Serializable {
	int port;
	int id;
	String address;

	@Override
	public String toString() {
		return "PersonType [address=" + address + ", port=" + port + "]";
	}

	//Constructor
	public PersonType(String address, int id, int port) {
		this.address= address;
		this.port = port;
		this.id = id;

	}
	//Constructor with address and port
	public PersonType( String IP, int port) {

		this.port = port;
		this.address = IP;

	}
	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public void setId(int id) {
		this.id = id;
	}
}
