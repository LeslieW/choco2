package samples.tutorials;

import choco.kernel.common.logging.ChocoLogging;

import java.util.logging.Logger;

public interface Example {

	public final static Logger LOGGER = ChocoLogging.getMainLogger();
	
	void execute();
	
	void execute(Object parameters);
		
}
