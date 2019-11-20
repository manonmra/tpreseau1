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
	private String pseudo = null;
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
			

			boolean pseudoAccepte = false;
			
    		while (!pseudoAccepte) {
    		  String line = socIn.readLine();
    		  System.out.println("Message recu du clientThread " + this + " : " + line );
    		  if(line.startsWith("#PSEUDO#")) {
    			  System.out.println("Ligne pseudo reçue");
    			  String pseudo = line.substring(8);
    			  if(EchoServerMultiThreaded.pseudos.contains(pseudo) == false) {
    				  System.out.println("pseudo ok");
    				  socOut.println("PSEUDOOK");
    				  this.pseudo = pseudo;
    				  EchoServerMultiThreaded.pseudos.add(pseudo);
    				  pseudoAccepte = true;
    			  } else {
    				  socOut.println("PSEUDONONOK");
    			  }
    		  }
    		}
    		
    		File file = new File("historique.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String st;
			while((st = br.readLine()) != null) {
				socOut.println(st);
			}
    		
    		while(true) {
      		  String line = socIn.readLine();
    		  String message = (pseudo + " : " + line);
    		  EchoServerMultiThreaded.sendAll(message);    		  
    		}
    		
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
       }
  
  }

  
