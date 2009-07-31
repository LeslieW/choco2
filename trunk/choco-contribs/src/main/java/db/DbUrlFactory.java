package db;

import java.io.File;

public final class DbUrlFactory {

	private DbUrlFactory() {}
	
	
	public final static String makeLocalhostURL(String dbName) {
		return makeNetworkURL("localhost", dbName);
	}
	
	public final static String makeEmbeddedURL(File dbDir, String dbName) {
		return "jdbc:hsqldb:file:" + dbDir.getAbsolutePath()+"/"+dbName;
	}
	
	public final static String makeNetworkURL(String host, String dbName) {
		return "jdbc:hsqldb:hsql://"+host+"/"+dbName;
	}
	
	public final static String makeNetworkURL(String host, int port, String dbName) {
		return "jdbc:hsqldb:hsql://"+host+":"+port+"/"+dbName;
	}

}
