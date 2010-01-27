package parser.instances;

import choco.kernel.common.logging.ChocoLogging;
import parser.absconparseur.tools.UnsupportedConstraintException;

import java.io.File;
import java.util.logging.Logger;

public interface InstanceFileParser {
	
	public final static Logger LOGGER = ChocoLogging.getMainLogger();
	
	File getInstanceFile();
	
	void loadInstance(File file);

	void parse(boolean displayInstance) throws UnsupportedConstraintException;

	void cleanup();

}