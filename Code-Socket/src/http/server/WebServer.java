///A Simple Web Server (WebServer.java)

package http.server;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

		System.out.println("Webserver starting up on port 3000");
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
				BufferedOutputStream audioStream = new BufferedOutputStream(remote.getOutputStream());
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
						//out.println("Link: <magic.css>;rel=stylesheet");
						out.println("Content-Type: text/html");
						fillHeader(out);

						// Send the HTML page
						sendPage(out, "index.html");
						out.flush();
					}
					else if(!fileName.contains("/favicon") && fileName.endsWith(".html")){
						//Headers

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
						displayImage(remote, out, fileName);
					}
					/*else if (fileName.endsWith(".css")) {
						out.println("HTTP/1.0 200 OK");
						out.println("Content-Type: text/css");
						out.println("Server: Bot");
						out.println("");
						out.flush();
						playData(audioStream, fileName.substring(1));
					}*/else if (fileName.endsWith(".mp3")) {
						out.println("HTTP/1.0 200 OK");
						out.println("Content-type: audio/mpeg");
						out.println("Server: Bot");
						out.println("");
						out.flush();
						playData(audioStream,fileName.substring(1));
					} else if (fileName.endsWith(".mp4")) {
						out.println("HTTP/1.0 200 OK");
						out.println("Content-type: video/mp4");
						out.println("Server: Bot");
						out.println("");
						out.flush();
						playData(audioStream,fileName.substring(1));
					}
					else {
						fillHeader(out);
						out.flush();
					}
					break;

				case "POST" :
					out.println("HTTP/1.0 201 CREATED");
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

					comment = comment.replace('+', ' ');
					out.println("Commentaire envoyé :");
					out.println("<p>"+comment+"</p>");
					out.println("<button href=\"resources/index.html\">Retournez à la page d'accueil</button>");
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
		File file = new File("resources/"+fileName);
		if(file.exists()) {
			out.println("HTTP/1.0 200 OK");
			fillHeader(out);
			try {
				System.out.println(file.getPath());
				BufferedReader br = new BufferedReader(new FileReader(file));
				String st;
				while((st = br.readLine()) != null) {
					System.out.println("passé par là");
					System.out.println(st);
					out.println(st);
				}
			} catch (FileNotFoundException e) {
				out.println("HTTP/1.0 500 INTERNAL ERROR");
				e.printStackTrace();
			}
		}
		else {
			out.println("HTTP/1.0 404 FILE NOT FOUND");
			fillHeader(out);
		}

	}

	/**
	 * Sert à afficher une image à l'écran depuis un fichier
	 * @param remote
	 * @param out
	 * @param filename
	 */
	public void displayImage(Socket remote, PrintWriter out,String filename) {
		BufferedImage img = null;
		File file = new File("resources/"+filename);
		if(file.exists()) {
			try {
				img=ImageIO.read(new File("resources/"+filename));
				ImageIO.write(img,"jpg", remote.getOutputStream());
			} catch (IOException e) {
				out.println("HTTP/1.0 500 INTERNAL ERROR");
				e.printStackTrace();
			}
		}
		else {
			System.out.println("NOT FOUND");
		}

	}

	/**
	 * Sert à jouer de la musique depuis un fichier
	 * @param out
	 * @param filename
	 * @throws IOException
	 */
	public void playData(BufferedOutputStream out, String filename) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
		byte[] buffer = new byte[256];
		int bytes;
		while((bytes = bis.read(buffer)) != -1) {
			out.write(buffer, 0, bytes);
		}
		bis.close();
	}

	/**
	 * Sert à remplir les entêtes du fichier
	 * @param out
	 */
	public void fillHeader(PrintWriter out) {
		//out.println("Content-Type: text/html");
		out.println("Server: Bot");
		out.println("");
		out.println("<meta charset=\"UTF-8\">");
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
