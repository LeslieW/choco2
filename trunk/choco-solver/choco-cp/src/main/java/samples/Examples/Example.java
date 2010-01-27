package samples.Examples;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;

public interface Example {

	public final static Logger LOGGER = ChocoLogging.getMainLogger();
	
	void execute();
	
	void execute(Object parameters);
		
}
