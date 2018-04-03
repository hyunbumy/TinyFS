package com.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.chunkserver.ChunkServer;
import com.interfaces.ClientInterface;
import com.chunkserver.Response;

/**
 * implementation of interfaces at the client side
 * @author Shahram Ghandeharizadeh
 *
 */
public class Client implements ClientInterface {
	
	private static Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private int port;
	
	/**
	 * Initialize the client
	 */
	public Client(){
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Read the port from a local file
		try {
			FileReader fr = new FileReader("port.txt");
			BufferedReader br = new BufferedReader(fr);
			
			port = Integer.parseInt(br.readLine());
			br.close();
		} catch(IOException ioe) {
			System.out.println("Error reading the port config");
		}
		
		// Establish Connection
		try {
			//System.out.println("Trying to connect to server");
			socket = new Socket("localhost", port);
			//System.out.println("Connected");
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch(IOException ioe) {
			System.out.println(ioe.getMessage());
			System.out.println("Connection failed");
		}
		
	}
	
	/**
	 * Create a chunk at the chunk server from the client side.
	 */
	public String initializeChunk() {
		
		try {
			// Send request
			//System.out.println("Sending request");
			dos.writeInt(0);
			dos.flush();
			
			// Receive response
			int expectedSize = dis.readInt();
			byte[] response = new byte[expectedSize];
			
			// Read byte by byte
			for (int i = 0; i<expectedSize; i++) {
				response[i] = dis.readByte();
			}
			String handle = new String(response);
			//System.out.println(handle);
			
			return handle;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Write a chunk at the chunk server from the client side.
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		if(offset + payload.length > ChunkServer.ChunkSize){
			System.out.println("The chunk write should be within the range of the file, invalide chunk write!");
			return false;
		}

		try {
			// Send request
			
			// Format the request
			byte[] command = ByteBuffer.allocate(4).putInt(1).array();
			byte[] handle = ChunkHandle.getBytes();
			byte[] off = ByteBuffer.allocate(4).putInt(offset).array();
			
			// Send command
			dos.writeInt(1);
			
			// Send size of the handle
			dos.writeInt(handle.length);
			// Send handle
			dos.write(handle);
			
			// Send size of the payload
			dos.writeInt(payload.length);
			// Send payload
			dos.write(payload);
			
			// Send size of the offset
			dos.writeInt(off.length);
			// Send offset
			dos.writeInt(offset);			
			
			dos.flush();
			
			// Receive response
			int response = dis.readInt();
			if (response == 1)
				return true;
			
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Read a chunk at the chunk server from the client side.
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		if(NumberOfBytes + offset > ChunkServer.ChunkSize){
			System.out.println("The chunk read should be within the range of the file, invalide chunk read!");
			return null;
		}

		try {
			// Send request

			// Format the request
			byte[] handle = ChunkHandle.getBytes();
			
			// Send command
			dos.writeInt(2);
			
			// Send size of the handle
			dos.writeInt(handle.length);
			// Send handle
			dos.write(handle);
			
			// Send size of the offset
			dos.writeInt(4);
			// Send offset
			dos.writeInt(offset);		

			// Send size of the number of bytes
			dos.writeInt(4);
			// Send byte num
			dos.writeInt(NumberOfBytes);
			
			dos.flush();
			
			// Receive response
			int expectedSize = dis.readInt();
			byte[] response = new byte[expectedSize];
			
			// Read byte by byte
			for (int i = 0; i<expectedSize; i++) {
				response[i] = dis.readByte();
			}
			
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	


}
