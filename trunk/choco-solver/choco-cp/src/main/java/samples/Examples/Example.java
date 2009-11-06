package samples.Examples;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;

public interface Example {

	public final static Logger LOGGER = ChocoLogging.getSamplesLogger();
	
	void execute();
	
	void execute(Object parameters);
		
}
