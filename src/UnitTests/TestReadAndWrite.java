package UnitTests;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.chunkserver.ChunkServer;
import com.client.Client;

/**
 * A utility used by the UnitTests
 * @author Shahram Ghandeharizaden
 *
 */

public class TestReadAndWrite {
	
	public static Client client = null;
	
	public TestReadAndWrite(Client client) {
		this.client = client;
	}
	
	/**
	 * Create and write chunk(s) of a physical file.
	 * The default full chunk size is 4K. Note that the last chunk of the file may not have the size 4K.
	 * The sequence of chunk handles are returned, which are stored in a static map.
	 */
	public String[] createFile(File f) {
		try {
			RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "rw");
			raf.seek(0);
			long size = f.length();
			int num = (int)Math.ceil((double)size / ChunkServer.ChunkSize);
			String[] ChunkHandles = new String[num];
			String handle = null;
			byte[] chunkArr = new byte[ChunkServer.ChunkSize];
			for(int i = 0; i < num; i++){
				handle = client.initializeChunk();
				ChunkHandles[i] = handle;
				raf.read(chunkArr, 0, ChunkServer.ChunkSize);
				boolean isWritten = client.putChunk(handle, chunkArr, 0);
				if(isWritten == false){
					throw new IOException("Cannot write a chunk to the chunk server!");
				}
			}
			raf.close();
			return ChunkHandles;
		} catch (IOException ie){
			return null;
		}
	}

}
