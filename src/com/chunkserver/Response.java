package com.chunkserver;

import java.io.Serializable;

public class Response implements Serializable{
	public static final long serialVersionUID = 1;
	private String ChunkHandle;
	private boolean success;
	private byte[] payload;
	
	public Response(String handle, boolean success, byte[] payload) {
		this.ChunkHandle = handle;
		this.success = success;
		this.payload = payload;
	}
	
	public String getHandle() {
		return this.ChunkHandle;
	}
	
	public boolean isSuccess() {
		return this.success;
	}
	
	public byte[] getPayload() {
		return this.payload;
	}
	
}
