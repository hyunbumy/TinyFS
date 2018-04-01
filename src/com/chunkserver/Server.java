package com.chunkserver;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import com.client.Request;

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
			//System.out.println("Waiting for a connection...");
			socket = ss.accept();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Connection failed\r\n");
			return null;
		}
		
		//System.out.println("A client is connected\r\n");
		return socket;
	}
	
	private void run() {
		while(true) {
			socket = establishConnection();
			
			try {			
				if (socket != null) {
					oos = new ObjectOutputStream(socket.getOutputStream());
					ois = new ObjectInputStream(socket.getInputStream());
				}
				while(true) {
					Request cmd = (Request) ois.readObject();
					//System.out.println(cmd.getCommand());

					// Decode the command
					decode(cmd);
				}
			} catch (EOFException eofe) {
				// Nothing to read - continue
			} catch (IOException e) {
				System.out.println(e.getMessage() + "\r\n");
			} catch (ClassNotFoundException cnfe) {
				System.out.println(cnfe.getMessage() + "\r\n");
			}
		}
	}
	
	private void decode(Request cmd) {
		try {
			switch(cmd.getCommand()) {
			// Initialize Chunk
			case 0:
				String handle = cs.initializeChunk();
				oos.writeObject(new Response(handle, true, null));
				oos.flush();
				break;
				
			// Put Chunk
			case 1:
				boolean success = cs.putChunk(cmd.getHandle(), cmd.getPayload(), cmd.getOffset());
				oos.writeObject(new Response(null, success, null));
				oos.flush();
				break;
				
			// Get Chunk
			case 2:
				byte[] payload = cs.getChunk(cmd.getHandle(), cmd.getOffset(), cmd.getBytes());
				oos.writeObject(new Response(null, true, payload));
				oos.flush();
			}
		} catch (IOException ioe) {
			System.out.println("Request failed!");
		}
	}
	
	public static void main(String[] args) {		
		Server server = new Server();
		server.run();
	}
}
