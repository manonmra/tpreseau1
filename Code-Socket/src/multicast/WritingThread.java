package multicast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextField;

public class WritingThread extends Thread {

	InetAddress groupAddress;
	int groupPort;
	MulticastSocket s;
	AtomicBoolean running;
	JTextField jtf;
	
	String pseudo;

	WritingThread(MulticastSocket s, InetAddress ga, int gp, JTextField jtf, String pseudo){
		this.s = s;
		this.groupAddress =  ga;
		this.groupPort = gp;
		this.running = new AtomicBoolean(true);
		this.jtf = jtf;
		this.pseudo = pseudo;
	}

	public boolean isRunning() {
		return running.get();
	}

	public void setRunning(boolean b) {
		running.set(b);
	}

	public void run() {
		jtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String line = jtf.getText();
				line = pseudo + " : " + line;
				DatagramPacket dp = new DatagramPacket(line.getBytes(), line.length(), groupAddress, groupPort);
				try {
					s.send(dp);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				jtf.setText("");
			}
		});	
		
		while(isRunning()) {
			
			/*try {
				line = stdIn.readLine();
				s.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}
}
