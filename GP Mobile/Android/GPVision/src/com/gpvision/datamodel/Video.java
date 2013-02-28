package com.gpvision.datamodel;

public class Video {
	private long id;
	private String name;
	private Status status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		Uploading, Paused, Indexing, Indexed, Failed, Deleted
	}
}
