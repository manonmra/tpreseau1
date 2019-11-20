/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class EchoServerMultiThreaded  {

	public static ArrayList<String> historique = new ArrayList<String>();
	public static ArrayList<PrintStream> outStreams = new ArrayList<PrintStream>();
	public static FileWriter writer;
	public static int appel = 2; //0 : sans historique, 1: historique non persistent, 2: historique persistent
	/**
	 * main method
	 * @param EchoServer port
	 * 
	 **/
	public static void main(String args[]){ 
		ServerSocket listenSocket;
		try {
			writer = new FileWriter("historique.txt", true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (args.length != 1) {
			System.out.println("Usage: java EchoServer <EchoServer port>");
			System.exit(1);
		}
		try {
			listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
			System.out.println("Server ready123..."); 
			while (true) {
				Socket clientSocket = listenSocket.accept();
				PrintStream out = new PrintStream(clientSocket.getOutputStream());
				System.out.println("Connexion from:" + clientSocket.getInetAddress());
				ClientThread ct = new ClientThread(clientSocket);
				ct.start();
			}
		} catch (Exception e) {
			System.err.println("Error in EchoServer:" + e);
		}
	}
	
	public synchronized static void sendAll(String message) {
		//System.out.println("sendAll" + outStreams.size());
		for(PrintStream out : outStreams) {
			out.println(message);
		}
		
	}
	
	public synchronized static void sendAllHistorique(String message) {
		historique.add(message);
		for(PrintStream out : outStreams) {
			out.println(message);
		}
		
	}
	
	public synchronized static void sendAllHistoriquePersistent(String message) {
		try {
			writer.write(message);
			writer.write("\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(PrintStream out : outStreams) {
			out.println(message);
		}
	}
	
	public static ArrayList<String> getHistorique(){
		return historique;
	}
}


