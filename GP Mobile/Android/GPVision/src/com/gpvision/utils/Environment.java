package com.gpvision.utils;

public class Environment {

	public static final Environment E9 = new Environment("172.20.230.9:3008",
			null);
	public static final Environment E9S = new Environment("172.20.230.9:3088",
			null);
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
