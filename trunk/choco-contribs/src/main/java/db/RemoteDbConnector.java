package db;

public class RemoteDbConnector implements IDbConnector {

	private String databaseURL;
	
	private DbManager dbManager;
	
	public RemoteDbConnector(String url) {
		super();
		this.databaseURL = url;
	}

	
	public final String getDatabaseURL() {
		return databaseURL;
	}


	public final void setDatabaseURL(String url) {
		this.databaseURL = url;
	}


	@Override
	public DbManager getDatabaseManager() {
		return dbManager;
	}

	@Override
	public void setUp() {
		dbManager = new DbManager(databaseURL);
	}

	@Override
	public void tearDown() {
		dbManager.commit();
		dbManager = null;
		databaseURL = null;
	}


	@Override
	public String toString() {
		return databaseURL;
	}
	
	
}
