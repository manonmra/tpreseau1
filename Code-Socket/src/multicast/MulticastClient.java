package multicast;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MulticastClient {



	public static void main(String[]args) {

		InetAddress groupAddress;
		int groupPort;

		try {
			groupAddress = InetAddress.getByName("228.5.6.7");
			groupPort = 6789;

			MulticastSocket s = new MulticastSocket(groupPort);
			s.joinGroup(groupAddress);
			
			System.out.println(s.getPort());

			JFrame frame = new JFrame("Chat");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JTextField message = new JTextField(50);
			JTextArea chat = new JTextArea(16,50);
			JButton deconnexion = new JButton("Se déconnecter");
			chat.setEditable(false);


			WritingThread wt = new WritingThread(s, groupAddress, groupPort, message);
			ListeningThread lt = new ListeningThread(s, chat);
			wt.start();
			lt.start();
			
			deconnexion.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					int input = JOptionPane.showConfirmDialog(frame, "Etes-vous sur de vouloir quitter le chat?", "Déconnexion", JOptionPane.YES_NO_OPTION);

					// TODO Auto-generated method stub
					if(input == 0) {
						wt.setRunning(false);
						lt.setRunning(false);
						try {
							s.leaveGroup(groupAddress);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						frame.setVisible(false);
						frame.dispose();
					}
					
				}
				
			});
			
			frame.getContentPane().add(message, BorderLayout.SOUTH);
			frame.getContentPane().add(new JScrollPane(chat), BorderLayout.CENTER);
			frame.getContentPane().add(deconnexion, BorderLayout.NORTH);
			
			frame.pack();
			frame.setVisible(true);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
