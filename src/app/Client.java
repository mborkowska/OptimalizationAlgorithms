package app;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import packets.ConnectionPacket;
import packets.DisconnectionPacket;
import packets.MessagePacket;
import packets.Packet;


public class Client implements ActionListener {
	private static JFrame frame;
	private static final JTextArea textArea = new JTextArea();
	private static final JTextField textField = new JTextField(25);
	private static final JButton PSOButton = new JButton("use PSO");
	//private static final JButton DEButton = new JButton("use DE");
	
	Socket s;
	ObjectInputStream oin;
	ObjectOutputStream oout;
	public String username;
	Boolean shouldRun = true;

	public static void main(String[] args) {
		new Client();
	}
	public Client() {
		String username = JOptionPane.showInputDialog("Input the username:");
		this.username = username;

		setUpWindow();
		connect();
	}
	private void setUpWindow() {
		frame = new JFrame(username);
		frame.setSize(500, 450);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				DisconnectionPacket dp = new DisconnectionPacket(username);
				try {
					oout.writeObject(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		Panel p = new Panel();
		PSOButton.addActionListener(this);
		//DEButton.addActionListener(this);
		textArea.setEditable(false);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(430, 275));
		p.add(areaScrollPane);
		p.add(textField);
		p.add(PSOButton);
		//p.add(DEButton);
		frame.add(p);
		frame.setVisible(true);
	}

	private void connect() {
		try {
			s = new Socket("localhost", 3333);
			oout = new ObjectOutputStream(s.getOutputStream());
			oin = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to server");
			return;
		}
		ConnectionPacket cp = new ConnectionPacket(username);
		try {
			oout.writeObject(cp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new listenForInput().start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String input = textField.getText();
			MessagePacket mp = new MessagePacket();
			mp.message = input;
			try {
				oout.writeObject(mp);
				oout.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			textField.setText("");
	}
	public class listenForInput extends Thread {
		public void run() {
			while (shouldRun) {
				try {
					Packet p = (Packet) oin.readObject();
					if(p instanceof MessagePacket) {
						MessagePacket mp = (MessagePacket) p;
						textArea.append(mp.message + "\n");
					}
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
