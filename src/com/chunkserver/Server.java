package com.chunkserver;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
	private DataOutputStream dos;
	private DataInputStream dis;
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
		
		// Update the port file for the client to reference
		try {
			FileWriter fw = new FileWriter("port.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(Integer.toString(port));
			bw.close();
		} catch(IOException ioe) {
			System.out.println("Port Reference update failed");
		}
		
	}
	
	private Socket establishConnection() {
		socket = null;
		try {
			System.out.println("Waiting for a connection...");
			socket = ss.accept();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Connection failed\r\n");
			return null;
		}
		
		System.out.println("A client is connected\r\n");
		return socket;
	}
	
	private void run() {
		while(true) {
			socket = establishConnection();
			
			try {			
				if (socket != null) {
					dos = new DataOutputStream(socket.getOutputStream());
					dis = new DataInputStream(socket.getInputStream());
				}
				while(true) {
					int cmd = dis.readInt();
					//System.out.println(cmd.getCommand());

					// Decode the command
					decode(cmd);
				}
			} catch (EOFException eofe) {
				// Nothing to read - continue
			} catch (IOException e) {
				System.out.println(e.getMessage() + "\r\n");
			}
		}
	}
	
	private void decode(int cmd) {
		try {
			switch(cmd) {
			// Initialize Chunk
			case 0:
				String handle = cs.initializeChunk();
				byte[] chunkHandle = handle.getBytes();
				dos.writeInt(chunkHandle.length);
				dos.write(chunkHandle);
				dos.flush();
				break;
				
			// Put Chunk
			case 1:
				// Receive parameters
				// ChunkHandle
				int len = dis.readInt();
				byte[] byteHandle = new byte[len];
				for (int i=0; i<len; i++) {
					byteHandle[i] = dis.readByte();
				}
				String strHandle = new String(byteHandle);
				
				// payload
				len = dis.readInt();
				byte[] pay = new byte[len];
				for (int i = 0; i < len; i++) {
					pay[i] = dis.readByte();
				}
				
				// offset
				int off = dis.readInt();
				
				boolean success = cs.putChunk(strHandle, pay, off);
				if (success)
					dos.writeInt(1);
				else
					dos.writeInt(0);
				dos.flush();
				break;
				
			// Get Chunk
			case 2:
				// ChunkHandle
				len = dis.readInt();
				byteHandle = new byte[len];
				for (int i=0; i<len; i++) {
					byteHandle[i] = dis.readByte();
				}
				strHandle = new String(byteHandle);
				
				// Offset
				off = dis.readInt();
				
				// Numofbytes
				int numBytes = dis.readInt();
				
				byte[] payload = cs.getChunk(strHandle, off, numBytes);
				
				// Send payload
				dos.writeInt(payload.length);
				dos.write(payload);
				dos.flush();
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
