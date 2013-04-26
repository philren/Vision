package com.gpvision.utils;

public class Environment {

	public static final Environment E9 = new Environment("121.199.1.247", null);
	public static final Environment E9S = new Environment("121.199.1.247", null);
	private final String host;
	private final String basePath;

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
