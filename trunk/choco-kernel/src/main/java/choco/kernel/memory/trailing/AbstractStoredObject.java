package choco.kernel.memory.trailing;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;

public class AbstractStoredObject {

	/**
	 * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
	 */

	protected final static Logger LOGGER = ChocoLogging.getMemoryLogger();

	/**
	 * The current {@link EnvironmentTrailing}.
	 */
	protected final EnvironmentTrailing environment;



	/**
	 * The last world the search was moidified in.
	 */
	protected int worldStamp;



	public AbstractStoredObject(EnvironmentTrailing environment) {
		super();
		this.environment = environment;
		this.worldStamp = environment.getWorldIndex();
	}



	public final int getWorldStamp() {
		return worldStamp;
	}



	public final void setWorldStamp(int worldStamp) {
		this.worldStamp = worldStamp;
	}



	public final EnvironmentTrailing getEnvironment() {
		return environment;
	}
	
	
	
}
