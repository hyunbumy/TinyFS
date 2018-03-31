package com.chunkserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	// Instantiate a ChunkServer
	public static ChunkServer cs = new ChunkServer();
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ServerSocket ss;
	private Socket socket;
	
	public Server() {
		// Create a socket to accept connections
		int port = 4444;
		while(true) {
			try {
				ss = new ServerSocket(port);
				System.out.println("Server started on 127.0.0.1:"+ Integer.toString(port));
				break;
				
			} catch (BindException be) {
				System.out.println("Port "+Integer.toString(port)+ " is occupied");
				port += 1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Server Initialization failed");
			}
		}
		
		// TODO: Output to a file for the client to reference
	}
	
	private Socket establishConnection() {
		socket = null;
		try {
			System.out.println("Waiting for a connection...");
			socket = ss.accept();
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch(IOException ioe) {
			System.out.println("Connection failed");
		}
		
		System.out.println("A client is connected");
		return socket;
	}
	
	private void run() {
		while(true) {
			socket = establishConnection();
			
			while(socket != null) {
				
			}
		}
	}
	
	public static void main(String[] args) {		
		Server server = new Server();
		server.run();
	}
}
