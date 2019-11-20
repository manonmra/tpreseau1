package multicast;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextArea;

public class ListeningThread extends Thread {

	MulticastSocket s;
	InetAddress groupAddress;
	int groupPort;
	AtomicBoolean running;
	JTextArea jta;
	
	ListeningThread(MulticastSocket s, JTextArea jta){
		this.s = s;
		this.groupAddress = s.getInetAddress();
		this.groupPort = s.getPort();
		this.running = new AtomicBoolean(true);
		this.jta = jta;
	}
	
	public boolean isRunning() {
		return running.get();
	}
	
	public void setRunning(boolean b) {
		running.set(b);
	}
	
	public void run() {
		
		while(isRunning()) {
			byte[] buf = new byte[1000];
			DatagramPacket recv = new  DatagramPacket(buf, buf.length);  // Receive a datagram packet response 
			try {
				s.receive(recv);
				jta.append(new String(buf));
				jta.append("\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}		
	}
}
