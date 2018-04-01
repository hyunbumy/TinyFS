package com.client;

import java.io.Serializable;

public class Request implements Serializable {
	public static final long serialVersionUID = 1;
	/*
	 * cmdType:
	 *   0 : Initialize Chunk
	 *   1 : Put Chunk
	 *   2 : Get Chunk
	 */
	private int cmdType;
	private String ChunkHandle;
	private byte[] payload;
	private int offset;
	private int NumberOfBytes;
	
	public Request(int cmd, String handle, byte[] payload, int offset, int bytes) {
		this.cmdType = cmd;
		this.ChunkHandle = handle;
		this.payload = payload;
		this.offset = offset;
		this.NumberOfBytes = bytes;
	}
	
	public int getCommand() {
		return this.cmdType;
	}
	
	public String getHandle() {
		return this.ChunkHandle;
	}
	
	public byte[] getPayload() {
		return this.payload;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public int getBytes() {
		return this.NumberOfBytes;
	}
}
