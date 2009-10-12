package choco.cp.solver.constraints.integer;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;

interface ILinCombBounds {
	

	/**
	 * Computes an upper bound estimate of a linear combination of variables.
	 * @return the new upper bound value
	 */
	int computeUpperBound();

	/**
	 * Computes a lower bound estimate of a linear combination of variables.
	 * @return the new lower bound value
	 */
	int computeLowerBound();



}

public interface ILinCombSConstraint extends ILinCombBounds {
	
	/**
	 * Propagates the constraint sigma(ai Xi) + c <= 0
	 * where mylb = sigma(ai inf(Xi)) + c.
	 * Note: this does not reach saturation (fix point),
	 * but returns a boolean indicating whether
	 * it infered new information or not.
	 * @param mylb the computed lower bound
	 * @return true if filtering has been infered
	 * @throws ContradictionException if a domain empties or a contradiction
	 * is infered
	 */
	boolean propagateNewLowerBound(final int mylb) throws ContradictionException;


	
	/**
	 * Propagates the constraint sigma(ai Xi) + c <= 0
	 * where myub = sigma(ai sup(Xi)) + c.
	 * Note: this does not reach saturation (fix point),
	 * but returns a boolean indicating whether
	 * it infered new information or not.
	 * @param myub the computed upper bound
	 * @return true if filtering has been infered
	 * @throws ContradictionException if a domain empties or a contradiction
	 * is infered
	 */
	boolean propagateNewUpperBound(final int myub) throws ContradictionException;
		
	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 * @return true iff the constraint is bound consistent
	 * (weaker than arc consistent)
	 */
	boolean hasConsistentLowerBound();
	
	
	/**
	 * Tests if the constraint is consistent
	 * with respect to the current state of domains.
	 * @return true iff the constraint is bound consistent
	 * (weaker than arc consistent)
	 */
	boolean hasConsistentUpperBound();
	
}
