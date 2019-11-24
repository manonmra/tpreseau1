package stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextArea;

public class ListeningThread extends Thread {

	Socket client;
	JTextArea jta;
	AtomicBoolean running = new AtomicBoolean(true);

	ListeningThread(Socket s, JTextArea jta){
		client = s;
		this.jta = jta;
	}


	public void setRunning(boolean b) {
		running = new AtomicBoolean(b);
	}

	public boolean isRunning() {
		return running.get();
	}


	public void run() {

		BufferedReader socIn = null;
		try {
			socIn = new BufferedReader(
					new InputStreamReader(client.getInputStream()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		while(isRunning()) {
			String line = null;
			try {
				line = socIn.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*if(line.startsWith("#WELCOME#") && line!=null){
				jta.append(line.substring(9));
				jta.append("\r\n");
			}
			else {*/
			jta.append(line);
			jta.append("\r\n");
		}
	}
}
