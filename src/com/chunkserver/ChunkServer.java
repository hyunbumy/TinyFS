package com.chunkserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.interfaces.ChunkServerInterface;

/**
 * implementation of interfaces at the chunkserver side
 * 
 * @author Shahram Ghandeharizadeh
 *
 */

public class ChunkServer implements ChunkServerInterface {
	final static String filePath = "C:\\Users\\didgu\\TinyFS-disk\\"; // or C:\\newfile.txt
	// For the filenames of each chunks
	public static long counter;

	/**
	 * Initialize the chunk server
	 */
	public ChunkServer() {
		System.out.println(
				"Constructor of ChunkServer is invoked:  Part 1 of TinyFS must implement the body of this method.");
		//System.out.println("It does nothing for now.\n");
		
		// Read the metadata file
		try
		{
			FileReader fr = new FileReader(filePath+"metadata.txt");
			BufferedReader br = new BufferedReader(fr);
			
			// Initialize the counter to the latest count value
			counter = Long.parseLong(br.readLine());
			br.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not found");
			// No metadata means no existing chunks
			counter = 0;
		}
		catch(IOException ex)
		{
			System.out.println("Error reading the file");
			counter = 0;
		}
	}

	/**
	 * Each chunk corresponds to a file. Return the chunk handle of the last chunk
	 * in the file.
	 */
	public String initializeChunk() {
		System.out.println("createChunk invoked:  Part 1 of TinyFS must implement the body of this method.");
		//System.out.println("Returns null for now.\n");
		
		// Allocates (Creates) a new chunk in the chunk server of size ChunkSize
		// Returns the chunk handle of this new chunk
		String handle = Long.toString(counter)+".bin";
		try
		{
			// Create a new file of size ChunkSize
			FileOutputStream ops = new FileOutputStream(filePath+handle);
			byte[] buffer = new byte[ChunkSize];
			ops.write(buffer);
			ops.close();
			
			// Increment the counter
			//TODO Should I update the metadata here or when the server is destroyed?
			counter++;
			
			// Update the metadata
			// Create file
			FileWriter fw = new FileWriter(filePath+"metadata.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			// Update the file to reflect the current value of the counter
			bw.write(Long.toString(counter));
			bw.close();
			
			System.out.println("Initialization Successful");
			return handle;
		}
		catch(IOException ioe)
		{
			System.out.println("Chunk Initialization failed");
		}
		return null;
	}

	/**
	 * Write the byte array to the chunk at the specified byte offset (within the chunk) The byte array size
	 * should be no greater than 4KB
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		System.out.println("writeChunk invoked:  Part 1 of TinyFS must implement the body of this method.");
		// System.out.println("Returns false for now.\n");
		
		// Read the specified chunk
		byte[] chunk = getChunk(ChunkHandle, 0, ChunkSize);
		
		// Update the chunk
		for (int i = 0; i<payload.length;i++)
			chunk[i+offset] = payload[i];
		
		// Write the chunk back to the chunk file
		try
		{
			FileOutputStream fos = new FileOutputStream(filePath+ChunkHandle);
			fos.write(chunk);
			fos.close();
			
			System.out.println("putChunk Successful");
			return true;
		}
		catch(IOException ioe)
		{
			System.out.println("putChunk failed");
		}
		return false;
	}

	/**
	 * read the chunk at the specific byte offset
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		System.out.println("readChunk invoked:  Part 1 of TinyFS must implement the body of this method.");
		//System.out.println("Returns null for now.\n");
		
		// Read the chunk specified by the chunk handle
		try
		{
			// Read the file
			FileInputStream fis = new FileInputStream(filePath+ChunkHandle);
			
			// Get to the correct start location to read the file
			if (fis.skip(offset) != offset)
				System.out.println("Skip unsuccessful");
			
			// Read the specified number of bytes
			byte[] res = new byte[NumberOfBytes];
			if (fis.read(res) != NumberOfBytes)
				System.out.println("Read unsuccesful");
			else
				return res;
		}
		catch(IOException ioe)
		{
			System.out.println("getChunk failed");
		}
		return null;
	}

}
