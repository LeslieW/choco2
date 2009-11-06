package choco.kernel.memory.trailing;

import choco.kernel.common.logging.ChocoLogging;

import java.util.logging.Logger;

public class AbstractStoredObject {

	/**
	 * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
	 */
	protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

	/**
	 * The current {@link EnvironmentTrailing}.
	 */
	protected final EnvironmentTrailing environment;



	/**
	 * The last world the search was moidified in.
	 */
    public int worldStamp;



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
