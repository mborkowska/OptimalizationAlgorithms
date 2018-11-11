package packets;

public class DisconnectionPacket extends Packet {
	public String username;
	public DisconnectionPacket(String username) {
		this.username = username;
	}
}
