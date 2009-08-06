package choco.kernel.model.variables;

/**
 * a hook is an integer field to attach temporary information about a given variable or constraint.
 * @author Arnaud Malapert</br> 
 * @since 5 août 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public interface IHook {

	public final static int NO_HOOK = Integer.MIN_VALUE;
	
	/**
	 * get the current hook value
	 */
	int getHook();
	
	/**
	 * you should be sure to reset the hook before setting a new hook value
	 * @param hook the new hook value
	 */
	void setHook(int hook);
	
	/**
	 * reset the hook, it allows to set a new value.
	 */
	void resetHook();
}
