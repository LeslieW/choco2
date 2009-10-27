/* ************************************************
*           _       _                            *
*          |  °(..)  |                           *
*          |_  J||L _|        CHOCO solver       *
*                                                *
*     Choco is a java library for constraint     *
*     satisfaction problems (CSP), constraint    *
*     programming (CP) and explanation-based     *
*     constraint solving (e-CP). It is built     *
*     on a event-based propagation mechanism     *
*     with backtrackable structures.             *
*                                                *
*     Choco is an open-source software,          *
*     distributed under a BSD licence            *
*     and hosted by sourceforge.net              *
*                                                *
*     + website : http://choco.emn.fr            *
*     + support : choco@emn.fr                   *
*                                                *
*     Copyright (C) F. Laburthe,                 *
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.cp.solver.constraints.integer.channeling;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Constraints that map the boolean assignments variables (bvars) with the standard assignment variables (var).
 * var = 1 -> bvars[1] = 1
 * @author Xavier Lorca
 * @author Hadrien Cambazard
 * @author Fabien Hermenier
 *
 */
public class DomainConstraint extends AbstractLargeIntSConstraint {

	/**
	 * Number of possible assignments.
	 * ie, the number of boolean vars
	 */
    private int dsize;

    /**
     * The last lower bounds of the assignment var.
     */
    private IStateInt oldinf;

    /**
     * The last upper bounds of the assignment var.
     */
    private IStateInt oldsup;

    /**
     * Make a new Channeling.
     * Warning : no offset ! the lower bound of x_i should be O !!!!!
     * @param yij The boolean assignment var for a virtual machine
     * @param xi the associated assignment var
     */
    public DomainConstraint(Solver solver, IntDomainVar[] yij, IntDomainVar xi) {
    	super(ArrayUtils.append(yij, new IntDomainVar[]{xi}));
        this.dsize = yij.length;
        oldinf = solver.getEnvironment().makeInt();
        oldsup = solver.getEnvironment().makeInt();
    }

    @Override
	public String pretty() {
		StringBuffer buffer = new StringBuffer("domainConstraint " + vars[vars.length - 1].pretty() + " <-> (");
		for (int i = 0; i < vars.length - 1; i++) {
			buffer.append(" " + vars[i].pretty());
		}
		buffer.append(")");
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void awake() throws ContradictionException {
    	//Set oldinf & oldsup equals to the current bounds of the assignment var
        oldinf.set(vars[dsize].getInf());
        oldsup.set(vars[dsize].getSup());
    }

	/**
	 * {@inheritDoc}
	 */
    public void propagate() throws ContradictionException {
        for (int i = 0; i < dsize; i++) {
            if (vars[i].isInstantiated()) {
                awakeOnInst(i);
            } else if (!vars[dsize].canBeInstantiatedTo(i)) {
                awakeOnRem(dsize, i);
            }
        }
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void awakeOnInf(int i) throws ContradictionException {
    	//clear all the boolean vars before i
        for (int j = oldinf.get(); j < vars[i].getInf(); j++) {
            awakeOnRem(i, j);
        }
        oldinf.set(vars[i].getInf());

    }

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void awakeOnSup(int i) throws ContradictionException {
    	//clear all the boolean vars after i
    	for (int j = oldsup.get(); j > vars[i].getSup(); j--) {
        	awakeOnRem(i, j);
        }
        oldsup.set(vars[i].getSup());

    }

    /**
     * Instantiate all the boolean variable to 1 except one.
     * @param val The index of the variable to keep
     * @throws ContradictionException if an error occured
     */
    public void clearBooleanExcept(int val) throws ContradictionException {
        for (int i = oldinf.get(); i <= oldsup.get(); i++) {
            if (i != val/* && i != dsize*/) {
                vars[i].instantiate(0, cIndices[i]);
            }
        }
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void awakeOnRem(int idx, int val) throws ContradictionException {
    	vars[val].instantiate(0, cIndices[val]);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void awakeOnInst(int idx) throws ContradictionException {

    	//val = the current value
        int val = vars[idx].getVal();

        if (idx == dsize) {
        	//We instantiate the assignment var
        	//val = index to keep
            vars[val].instantiate(1, cIndices[val]);
            clearBooleanExcept(val);
        } else {
        	//We instantiate a boolean var
            if (val == 1) {
            	//We report the instantiation to the associated assignment var
                vars[dsize].instantiate(idx, cIndices[dsize]);
                //Next line should be useless ?
                clearBooleanExcept(idx);
            } else {
                vars[dsize].removeVal(idx, cIndices[dsize]);
            }
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean isSatisfied(int [] tuple) {
    	int val = tuple[tuple.length - 1];
    	for (int i = 0; i < tuple.length - 1; i++) {
    		if (i != val && tuple[i] != 0) {
    			return false;
    		} else if (i == val && tuple[i] != 1) {
    			return false;
    		}
    	}
    	if (val < 0 || val > tuple.length - 1) {
			return false;
		}
    	return true;
    }

	@Override
	/**
	 * For all the binary variables, we catch only awakeOnInst. Otherwise, we catch awakeOnInst, bounds and awakeOnRem.
	 */
	public int getFilteredEventMask(int idx) {
		return idx < dsize ? IntVarEvent.INSTINTbitvector : IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector;
		// TODO Auto-generated method stub
		//return super.getFilteredEventMask(idx);
	}
}
