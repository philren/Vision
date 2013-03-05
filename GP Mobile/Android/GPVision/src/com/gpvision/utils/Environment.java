package com.gpvision.utils;

public class Environment {

	public static final Environment E9 = new Environment("172.20.230.9:3011",
			null);
	private String host;
	private String basePath;

	public String getHost() {
		return host;
	}

	public String getBasePath() {
		return basePath;
	}

	private Environment(String host, String basePath) {
		super();
		this.host = host;
		this.basePath = basePath;
	}

}
