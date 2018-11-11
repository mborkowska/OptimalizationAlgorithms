package packets;

public class ConnectionPacket extends Packet{
	public String username;
	public ConnectionPacket(String username) {
		this.username = username;
	}
}
