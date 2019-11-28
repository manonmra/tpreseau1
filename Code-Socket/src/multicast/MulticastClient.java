package multicast;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

import javax.swing.*;

public class MulticastClient {

	public static void main(String[]args) {

		InetAddress groupAddress;
		int groupPort;

		try {
			groupAddress = InetAddress.getByName("228.5.6.7");
			groupPort = 6789;

			MulticastSocket s = new MulticastSocket(groupPort);
			s.joinGroup(groupAddress);

			//Choix d'un pseudo
			JFrame framePseudo = new JFrame("Pseudo");
			Container containerPseudo = new Container();
			JTextField pseudo = new JTextField(20);
			JTextField info = new JTextField("Choissisez un pseudo");
			info.setEditable(false);
			info.setHorizontalAlignment(JTextField.CENTER);
			JButton valider = new JButton("Valider");


			containerPseudo.setLayout(new BorderLayout());

			JPanel subPanel = new JPanel();
			subPanel.add(pseudo);
			subPanel.add(valider);


			containerPseudo.add(subPanel, BorderLayout.CENTER);
			containerPseudo.add(info, BorderLayout.NORTH);

			framePseudo.setContentPane(containerPseudo);

			framePseudo.pack();
			framePseudo.setVisible(true);

			valider.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					framePseudo.dispose();
					afficherChat(s, groupAddress, groupPort, pseudo.getText());

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void afficherChat(MulticastSocket s, InetAddress groupAddress, int groupPort, String pseudo){
		JFrame frame = new JFrame("Chat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextField message = new JTextField(50);
		JTextArea chat = new JTextArea(16,50);
		JButton deconnexion = new JButton("Se déconnecter");
		chat.setEditable(false);


		WritingThread wt = new WritingThread(s, groupAddress, groupPort, message, pseudo);
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
	}
}
