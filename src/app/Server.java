package app;

import java.awt.Dimension;
import java.awt.Panel;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server {

	private static final JFrame frame = new JFrame("Server");
	private static final JTextArea textArea = new JTextArea();

	ServerSocket ss;
	ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
	boolean shouldRun = true;
	boolean acceptsRequests = false;

	public static void main(String[] args) {
		new Server();
	}
	public Server() {
		setUpWindow();
		connect();
		initiateConnections();
	}

	private void setUpWindow() {
		frame.setSize(450, 375);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Panel p = new Panel();
		textArea.setEditable(false);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(430, 275));
		p.add(areaScrollPane);
		frame.add(p);
		frame.setVisible(true);
	}
	private void connect() {
		try {
			ss = new ServerSocket(3333);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initiateConnections() {
		try {
			while (shouldRun) {
				Socket s = ss.accept();
				ServerConnection sc = new ServerConnection(s, this, textArea);
				sc.start();
				connections.add(sc);
				Thread.sleep(5);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
