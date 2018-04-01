package com.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.chunkserver.ChunkServer;
import com.interfaces.ClientInterface;
import com.chunkserver.Response;

/**
 * implementation of interfaces at the client side
 * @author Shahram Ghandeharizadeh
 *
 */
public class Client implements ClientInterface {
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	/**
	 * Initialize the client
	 */
	public Client(){
		// TODO: Read the port from the file
		
	}
	
	private void connect() {
		// Establish Connection
		try {
			//System.out.println("Trying to connect to server");
			socket = new Socket("localhost", 4444);
			//System.out.println("Connected");
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch(IOException ioe) {
			System.out.println(ioe.getMessage());
			System.out.println("Connection failed");
		}
	}
	
	/**
	 * Create a chunk at the chunk server from the client side.
	 */
	public String initializeChunk() {
		connect();
		if (socket == null)
			return null;
		try {
			// Send request
			//System.out.println("Sending request");
			oos.writeObject(new Request(0, null, null, -1, -1));
			oos.flush();
			
			// Receive response
			String handle = ((Response) ois.readObject()).getHandle();
			//System.out.println(handle);
			socket.close();
			return handle;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
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
		
		connect();
		if (socket == null)
			return false;

		try {
			// Send request
			//System.out.println("Sending request");
			oos.writeObject(new Request(1, ChunkHandle, payload, offset, -1));
			oos.flush();
			
			// Receive response
			boolean success = ((Response) ois.readObject()).isSuccess();
			//System.out.println(success);
			socket.close();
			
			return success;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
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
		
		connect();
		if (socket == null)
			return null;

		try {
			// Send request
			//System.out.println("Sending request");
			oos.writeObject(new Request(2, ChunkHandle, null, offset, NumberOfBytes));
			oos.flush();
			
			// Receive response
			byte[] payload = ((Response) ois.readObject()).getPayload();
			socket.close();
			
			return payload;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	


}
