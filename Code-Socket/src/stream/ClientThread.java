/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.UUID;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	private String uniqueID = UUID.randomUUID().toString();
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try {
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
			EchoServerMultiThreaded.outStreams.add(socOut);
			/*for(String s : EchoServerMultiThreaded.historique) {
				socOut.println(s);
			}*/
			
			File file = new File("historique.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String st;
			while((st = br.readLine()) != null) {
				socOut.println(st);
			}

    		while (true) {
    		  String line = socIn.readLine();
    		  System.out.println("Message recu du clientThread " + this + " : " + line );
    		  String message = (uniqueID.substring(0, 8) + " : " + line);
    		  EchoServerMultiThreaded.sendAll(message);
    		  //EchoServerMultiThreaded.writer.write(message);
    		  //EchoServerMultiThreaded.historique.add(message);
    		  
    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
       }
  
  }

  
