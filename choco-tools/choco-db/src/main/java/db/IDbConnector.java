package db;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;

public interface IDbConnector {

	public final static Logger LOGGER= ChocoLogging.getParserLogger();
	
	DbManager getDatabaseManager();
	
	void setUp();

	void tearDown();

}