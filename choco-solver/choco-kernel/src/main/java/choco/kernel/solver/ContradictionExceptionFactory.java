package choco.kernel.solver;

/**
 * Factory for contradiction exception.
 * Ensure only one contradiction is created by execution.
 */
public final class ContradictionExceptionFactory{

    private static ContradictionException reuseException =
            new ContradictionException(null);

    
    private ContradictionExceptionFactory() {
		super();
    }

	/**
     * Return the reusable contradiction exception.
     * @return {@link ContradictionException}
     */
    public static ContradictionException getSingloton() {
        return reuseException;
    }

}