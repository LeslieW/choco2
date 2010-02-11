package choco.cp.solver.constraints.integer.channeling;


import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Constraints that map the boolean assignments variables (bvars) with the standard assignment variables (var).
 * var = i -> bvars[i] = 1
 * @author Xavier Lorca
 * @author Hadrien Cambazard
 * @author Fabien Hermenier
 *
 */
public final class DomainChanneling extends AbstractLargeIntSConstraint {


	/**
	 * Number of possible assignments. 
	 * ie, the number of boolean vars
	 */
	private final int dsize;

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
     * @param environment
     */
	public DomainChanneling(IntDomainVar[] yij, IntDomainVar xi, IEnvironment environment) {
		super(ArrayUtils.append(yij, new IntDomainVar[]{xi}));    	
		this.dsize = yij.length;        
		oldinf = environment.makeInt();
		oldsup = environment.makeInt();
	}


	@Override
	/**
	 * For all the binary variables, we catch only awakeOnInst. Otherwise, we catch awakeOnInst, bounds and awakeOnRem.
	 */
	public int getFilteredEventMask(int idx) {
		return idx < dsize ? IntVarEvent.INSTINTbitvector : IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void awake() throws ContradictionException {
		vars[dsize].updateInf(0, cIndices[dsize]);
		vars[dsize].updateSup(dsize - 1, cIndices[dsize]);
		super.awake();
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
				clearBooleanExcept(vars[i].getVal());
			} else if (! vars[dsize].canBeInstantiatedTo(i)) {
				clearBoolean(i);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void awakeOnInf(int i) throws ContradictionException {
		clearBoolean(oldinf.get(), vars[i].getInf());
		oldinf.set(vars[i].getInf());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void awakeOnSup(int i) throws ContradictionException {
		clearBoolean(vars[i].getSup()+1, oldsup.get()+1);
		oldsup.set(vars[i].getSup());
	}

	private final void clearBoolean(int val) throws ContradictionException {
		vars[val].instantiate(0, cIndices[val]);
	}
	

	private final void clearBoolean(int begin, int end) throws ContradictionException {
		for (int i = begin; i < end; i++) {
			clearBoolean(i);
		}
	}
	
	/**
	 * Instantiate all the boolean variable to 1 except one.
	 * @param val The index of the variable to keep
	 * @throws ContradictionException if an error occured
	 */
	private final void clearBooleanExcept(int val) throws ContradictionException {
		clearBoolean(oldinf.get(), val);
		clearBoolean(val+1, oldsup.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void awakeOnRem(int idx, int val) throws ContradictionException {    	
		clearBoolean(val);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void awakeOnInst(int idx) throws ContradictionException {    	

		//val = the current value 
		final int val = vars[idx].getVal();

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
	public String pretty() {
		StringBuilder b = new StringBuilder();
		b.append("DomainChanneling ").append(vars[dsize].pretty()).append(" <=> ");
		b.append(StringUtils.pretty(vars,0 , dsize));
		return b.toString();
	}    
}
