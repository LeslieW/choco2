package choco.kernel.solver;

/**
 * Factory for contradiction exception.
 * Ensure only one contradiction is created by execution.
 */
public class ContradictionExceptionFactory{

    private static ContradictionException ex =
            new ContradictionException(null, ContradictionException.Type.UNKNOWN);

    /**
     * Return the reusable contradiction exception.
     * @return {@link ContradictionException}
     */
    public static ContradictionException get() {
        return ex;
    }

}