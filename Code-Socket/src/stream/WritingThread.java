package stream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;

import javax.swing.JTextField;

public class WritingThread extends Thread {

	Socket client;
	JTextField jtx;
	boolean running = true;
	
	public void setRunning(boolean b) {
		running = b;
	}

	WritingThread(Socket s, JTextField jtx){
		client=s;
		this.jtx=jtx;
	}

	public void run() {


		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		try {
			final PrintStream socOut = new PrintStream(client.getOutputStream());
			jtx.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String line = jtx.getText();
					socOut.println(line);
					jtx.setText("");
				}
			});

			String line = null;
			while (running) {
				try {
					line=stdIn.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (line.equals(".")) break;
				socOut.println(line);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





	}
}
