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

	static JFrame frame = new JFrame("Chat");
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
			final PrintStream socOut = new PrintStream(echoSocket.getOutputStream());
			final BufferedReader socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			
			boolean pseudoAccepte = false;


			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container containerPseudo = new Container();
			
			JTextField pseudo = new JTextField(20);
			JButton valider = new JButton("Valider");
			
			containerPseudo.setLayout(new BoxLayout(containerPseudo, BoxLayout.X_AXIS));
			containerPseudo.add(pseudo);
			containerPseudo.add(valider);
			
			frame.setContentPane(containerPseudo);
			
			valider.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(pseudo.getText().length()>0) {
						socOut.println("#PSEUDO#"+pseudo.getText());
						String line = null;
						try {
							line = socIn.readLine();
							System.out.println(line);
							if(line.contains("PSEUDOOK")) {
								System.out.println("pseudo ok client");
								accepterPseudo(pseudo.getText(), echoSocket);
							}
							else {
								valider.setEnabled(true);
								pseudo.setText("");
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				
			});	
			frame.pack();			
			frame.setVisible(true);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + args[0]);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to:"+ args[0]);
			System.exit(1);
		}
	}
	
	public static void accepterPseudo(String pseudo, Socket s) {
		JTextField message = new JTextField(50);
		JTextArea chat = new JTextArea(16,50);
		JButton deconnexion = new JButton("Se déconnecter");
		chat.setEditable(false);
		
		
		final ListeningThread lt = new ListeningThread(s, chat);
		final WritingThread wt = new WritingThread(s, message);
		
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
		
		Container containerPrincipal = new Container();
		containerPrincipal.setLayout(new BorderLayout());
		
		frame.setContentPane(containerPrincipal);
		frame.getContentPane().add(message, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(chat), BorderLayout.CENTER);
		frame.getContentPane().add(deconnexion, BorderLayout.NORTH);
		
		frame.pack();
		frame.setVisible(true);
		
		lt.start();
		wt.start();	
	}
}


