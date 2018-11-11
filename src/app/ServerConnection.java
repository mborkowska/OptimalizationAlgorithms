package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream.GetField;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTextArea;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;

import de.Candidate;
import de.DifferentialEvolution;
import packets.ConnectionPacket;
import packets.DisconnectionPacket;
import packets.MessagePacket;
import packets.Packet;
import pso.Particle;
import pso.ParticleSwarmOptimalization;

public class ServerConnection extends Thread {

	private final JTextArea textArea;
	Socket socket;
	Server server;
	ObjectInputStream oin;
	ObjectOutputStream oout;
	boolean shouldRun = true;
	static Jep jep = new Jep();
	List<double[]> dimensionList = new LinkedList<>();

	public ServerConnection(Socket socket, Server server, JTextArea textArea) {
		super("ServerConnectionThred");
		this.socket = socket;
		this.server = server;
		this.textArea = textArea;
	}

	public void sendPacketToClient(Packet packet) {
		try {
			oout.writeObject(packet);
			oout.flush();
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacketToOtherClients(Packet packet) {
		for (int index = 0; index < server.connections.size(); index++) {
			ServerConnection sc = server.connections.get(index);
			if (sc.shouldRun == true && sc != this) {
				sc.sendPacketToClient(packet);
			}
		}
	}

	// opening ObjectInputstram, ObjectOutputStream for clients
	public void run() {
		try {
			oout = new ObjectOutputStream(socket.getOutputStream());
			oin = new ObjectInputStream(socket.getInputStream());
			addBounds();
			while (shouldRun) {
				try {
					Packet p = (Packet) oin.readObject();
					MessagePacket mp = new MessagePacket();
					if (p instanceof ConnectionPacket) {
						ConnectionPacket cp = (ConnectionPacket) p;
						mp.message = cp.username + " connected.\n";
						textArea.append(mp.message);
						sendPacketToClient(mp);
						sendPacketToOtherClients(mp);
					}
					if (p instanceof DisconnectionPacket) {
						DisconnectionPacket dp = (DisconnectionPacket) p;
						mp.message = dp.username + " disconnected.\n";
						textArea.append(mp.message);
						sendPacketToOtherClients(mp);
					}
					if (p instanceof MessagePacket) {
						mp = (MessagePacket) p;
						String[] subString = mp.message.split(" ");
						if (subString.length == 2 && (subString[0].equals("PSO") || subString[0].equals("DE"))) {
							parseFormula(subString[1]);
							if (subString[0].equals("PSO")) {
								ParticleSwarmOptimalization pso = new ParticleSwarmOptimalization(
										(Particle particle) -> fitFunction(particle), dimensionList);
								Particle particle = pso.optimize();
								mp.message = "Result of optimalization by PSO: bBest found: " + fitFunction(particle)
										+ " at x = " + particle.toString();
							} else if (subString[0].equals("DE")) {
								DifferentialEvolution de = new DifferentialEvolution(
										(Candidate candidate) -> fitFunction(candidate), dimensionList);
								Candidate candidate = de.optimize();
								mp.message = "Result of optimalization by DE: bBest found: " + fitFunction(candidate)
										+ " at x = " + candidate.toString();
							}
						} else
							mp.message = "invalid input";
						textArea.append(mp.message + "\n");
						sendPacketToClient(mp);
						sendPacketToOtherClients(mp);
					}
				} catch (SocketException se) {
					shouldRun = false;
				}
			}
			oin.close();
			oout.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addBounds() {
		double[] dimension1Bounds = new double[2];
		dimension1Bounds[0] = -50;
		dimension1Bounds[1] = 50;

		double[] dimension2Bounds = new double[2];
		dimension2Bounds[0] = -50;
		dimension2Bounds[1] = 50;

		dimensionList.add(dimension1Bounds);
		dimensionList.add(dimension2Bounds);
	}

	public void parseFormula(String formula) {
		try {
			jep.addVariable("x", 0);
			jep.addVariable("y", 0);
			jep.parse(formula);
		} catch (JepException e) {
			e.printStackTrace();
		}
	}

	public static double fitFunction(Solution solution) {
		double x;
		double y;
		Object f;
		if (solution instanceof Candidate) {
			Candidate candidate = (Candidate) solution;
			x = candidate.variables[0];
			y = candidate.variables[1];
		} else if (solution instanceof Particle) {
			Particle particle = (Particle) solution;
			x = particle.position[0];
			y = particle.position[1];
		} else {
			x = 0;
			y = 0;
			System.out.println("Sht went wrong");
		}
		try {
			jep.getVariableTable().getVariable("x").setValue(x);
			jep.getVariableTable().getVariable("y").setValue(y);
			f = jep.evaluate();
		} catch (JepException e) {
			System.out.println("An error occurred: " + e.getMessage());
			return 0;
		}
		return (double) f;
	}
}
