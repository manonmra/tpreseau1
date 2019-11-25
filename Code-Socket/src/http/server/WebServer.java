///A Simple Web Server (WebServer.java)

package http.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;



/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */


public class WebServer{

	//List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();

	/**
	 * WebServer constructor.
	 */
	protected void start() {
		ServerSocket s;

		System.out.println("Webserver starting up on port 80");
		System.out.println("(press ctrl-c to exit)");
		try {
			// create the main server socket
			s = new ServerSocket(3000);
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return;
		}

		System.out.println("Waiting for connection");
		for (;;) {
			try {
				// wait for a connection
				Socket remote = s.accept();

				// remote is now the connected socket

				System.out.println("Connection, sending data.");
				BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
				PrintWriter out = new PrintWriter(remote.getOutputStream());

				//Lire le type de requête qui est envoyé
				String str = ".";
				str = in.readLine();

				StringTokenizer st = new StringTokenizer(str);

				String method = st.nextToken();
				System.out.println(method);
				switch (method){
				case "GET" : 
					String fileName = st.nextToken();
					if(fileName.equals("/")) {
						// Send the headers

						out.println("HTTP/1.0 200 OK");
						fillHeader(out);

						// Send the HTML page
						out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
						out.flush();
					}
					else if(!fileName.contains("/favicon") && fileName.endsWith(".html")){
						//Headers
						out.println("HTTP/1.0 200 OK");
						fillHeader(out);
			
						sendPage(out, fileName.substring(1));
						out.flush();
					}
					else if(!fileName.contains("/favicon") && (fileName.endsWith(".jpg")|| fileName.endsWith(".png")|| fileName.endsWith(".gif"))){
						//Headers
						out.println("HTTP/1.0 200 OK");
						out.println("Content-Type: image/jpeg");
						out.println("Server: Bot");
						out.println("");		
						out.flush();
						displayImage(remote, out, fileName.substring(1));
					}
					else {
						fillHeader(out);
						out.flush();
					}
					break;

				case "POST" :
					out.println("HTTP/1.0 200 OK");
					fillHeader(out);
					//*****************************************Traitement du commentaire
					int cL = 0;
					String content = "";
					while((content = in.readLine()) != null){
						if (content.equals("")) break;
						if (content.contains("Content-Length")){
							cL = Integer.parseInt(content.split(": ")[1]);
						}
					}
					char[]  buffer = new char[cL];
					String  postData = "";
					in.read(buffer, 0, cL);
					postData = new String(buffer, 0, buffer.length);
					String comment = postData.split("=")[1];
					System.out.println(comment);
					//******************************


					out.println("Commentaire envoyé :");
					out.println("<p>"+comment+"</p>");
					out.println("<button href=\"index.html\">Retournez à la page d'accueil</button>");
					out.flush();
					break;
				}
				remote.close();
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
	}

	/**
	 * Sert à afficher une page Web avec la méthode get
	 * @param out
	 * @param fileName
	 * @throws IOException
	 */
	public void sendPage(PrintWriter out, String fileName) throws IOException {
		try {
			File file = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			while((st = br.readLine()) != null) {
				out.println(st);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void displayImage(Socket remote, PrintWriter out,String filename) {
		BufferedImage img = null;
		try {
			img=ImageIO.read(new File(filename));
			ImageIO.write(img,"jpg", remote.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fillHeader(PrintWriter out) {
		out.println("Content-Type: text/html");
		out.println("<meta charset=\"UTF-8\">");
		out.println("Server: Bot");
		out.println("");
	}

	/**
	 * Start the application.
	 * 
	 * @param args
	 * Command line parameters are not used.
	 */
	public static void main(String args[]) {
		WebServer ws = new WebServer();
		ws.start();
	}
}
