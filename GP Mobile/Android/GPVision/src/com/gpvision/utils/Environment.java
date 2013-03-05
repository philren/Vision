package com.gpvision.utils;

public class Environment {

	public static final Environment E0 = new Environment("192.12.213.1:8080",
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
