/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.UUID;

import javax.swing.*;



public class EchoClient {

	/**
	 *  main method
	 *  accepts a connection, receives a message from client then sends an echo to the client
	 **/
	public static void main(String[] args) throws IOException {

		/*PrintStream socOut = null;
		BufferedReader stdIn = null;
		BufferedReader socIn = null;*/

		if (args.length != 2) {
			System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
			System.exit(1);
		}

		try {
			// creation socket ==> connexion
			final Socket echoSocket = new Socket(args[0],new Integer(args[1]).intValue());

			JFrame frame = new JFrame("Chat");
			Container containerPseudo = new Container();
			JTextField pseudo = new JTextField(20);
			JButton valider = new JButton("Valider");


			JTextField message = new JTextField(50);
			JTextArea chat = new JTextArea(16,50);
			JButton deconnexion = new JButton("Se déconnecter");
			JButton historique = new JButton("Supprimer l'historique");
			chat.setEditable(false);
			chat.setLineWrap(true);


			final ListeningThread lt = new ListeningThread(echoSocket, chat);
			final WritingThread wt = new WritingThread(echoSocket, message);

			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					wt.setRunning(false);
					lt.setRunning(false);
					frame.setVisible(false);
					frame.dispose();
				}
			});

			deconnexion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int input = JOptionPane.showConfirmDialog(frame, "Etes-vous sur de vouloir quitter le chat?", "Déconnexion", JOptionPane.YES_NO_OPTION);
					if(input == 0) {
						wt.setRunning(false);
						lt.setRunning(false);
						frame.setVisible(false);
						frame.dispose();
					}
				}
			});

			historique.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					switch (EchoServerMultiThreaded.appel) {
					case 2:
						PrintWriter pw = null;
						try {
							pw = new PrintWriter("historique.txt");
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						pw.close();
						chat.setText(null);
						break;
					}
				}
			});

			if(EchoServerMultiThreaded.appel==2) {
				JPanel subPanel = new JPanel();
				subPanel.add(deconnexion);
				subPanel.add(historique);
				frame.getContentPane().add(subPanel, BorderLayout.NORTH);
			}
			else {
				frame.getContentPane().add(deconnexion, BorderLayout.NORTH);
			}
			
			frame.getContentPane().add(message, BorderLayout.SOUTH);
			frame.getContentPane().add(new JScrollPane(chat), BorderLayout.CENTER);
			

			frame.pack();
			frame.setVisible(true);
			lt.start();
			wt.start();

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + args[0]);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to:"+ args[0]);
			System.exit(1);
		}
	}
}


