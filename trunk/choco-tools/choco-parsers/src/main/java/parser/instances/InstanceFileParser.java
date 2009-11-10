package parser.instances;

import java.io.File;
import java.util.logging.Logger;

import parser.absconparseur.tools.UnsupportedConstraintException;
import choco.kernel.common.logging.ChocoLogging;

public interface InstanceFileParser {
	
	public final static Logger LOGGER = ChocoLogging.getParserLogger();
	
	File getInstanceFile();
	
	void loadInstance(File file);

	void parse(boolean displayInstance) throws UnsupportedConstraintException;

	void cleanup();

}