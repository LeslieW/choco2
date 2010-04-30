/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver;

import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.cp.solver.preprocessor.detectors.ExpressionDetector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.set.SetConstantVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.BoolNode;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.kernel.solver.variables.set.SetVar;
import gnu.trove.THashSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * User:    charles
 * Date: 31 mars 2008
 * <p/>
 * Main and unique class for constraint programming transposition from Model to Solver.
 * (Well, it creates CPSolver objects from CPModel objects declare by the users).
 * It is separeted into 2 big parts: variables transposition and constraints transposition.
 */
public class CPModelToCPSolver {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

	protected final CPSolver cpsolver;

	private final THashSet<IntDomainVar> intNoDecisionVar = new THashSet<IntDomainVar>();

	private final THashSet<SetVar> setNoDecisionVar = new THashSet<SetVar>();

	private final THashSet<RealVar> realNoDecisionVar = new THashSet<RealVar>();

	private final THashSet<TaskVar> taskNoDecisionVar = new THashSet<TaskVar>();

    private final List<SConstraint> postponedConstraint = new ArrayList<SConstraint>(8);

	public CPModelToCPSolver(final CPSolver cpsolver) {
		this.cpsolver = cpsolver;
	}

    /**
     * Clear datastructures for safe reuses
     */
    public void clear(){
        this.intNoDecisionVar.clear();
        this.setNoDecisionVar.clear();
        this.realNoDecisionVar.clear();
        this.taskNoDecisionVar.clear();
    }
	//************************************************* CONCERNING VARIABLES ***********************************************

	/**
	 * Read variable from the model, transpose it into Solver variables,
	 * adding it to the Solver directly.
	 *
	 * @param model to read
	 */
	public void readVariables(final CPModel model) {
		readIntegerVariables(model);
        readRealVariables(model);
		readSetVariables(model);
		readConstants(model);
        readMultipleVariables(model);
    }

    public void readIntegerVariables(final CPModel model){
        IntegerVariable i;

		final Iterator it = model.getIntVarIterator();
		while (it.hasNext()) {
			i = (IntegerVariable) it.next();
			if (!cpsolver.mapvariables.containsKey(i.getIndex())) {
				cpsolver.mapvariables.put(i.getIndex(), readModelVariable(i));
			}
		}
    }

    public void readRealVariables(final CPModel model){
        RealVariable r;
		final Iterator it = model.getRealVarIterator();
		while (it.hasNext()) {
			r = (RealVariable) it.next();
			if (!cpsolver.mapvariables.containsKey(r.getIndex())) {
				cpsolver.mapvariables.put(r.getIndex(), readModelVariable(r));
			}
		}
    }

    public void readSetVariables(final CPModel model){
        SetVariable s;
        final Iterator it = model.getSetVarIterator();
		while (it.hasNext()) {
			s = (SetVariable) it.next();
			if (!cpsolver.mapvariables.containsKey(s.getIndex())) {
				final SetVar setVar = (SetVar) readModelVariable(s);
				cpsolver.mapvariables.put(s.getIndex(), setVar);
				cpsolver.mapvariables.put(s.getCard().getIndex(), setVar.getCard());
				checkOptions(s.getCard(), setVar.getCard());
			}
		}
    }

    public void readConstants(final CPModel model){
        Variable v;
		IntegerConstantVariable ci;
		RealConstantVariable cr;
		SetConstantVariable cs;
		final Iterator it = model.getConstVarIterator();
		while (it.hasNext()) {
			v = (Variable) it.next();
			if (!cpsolver.mapvariables.containsKey(v.getIndex())) {
				switch (v.getVariableType()) {
				case CONSTANT_INTEGER:
					ci = (IntegerConstantVariable) v;
					if (!cpsolver.mapvariables.containsKey(ci.getIndex())) {
						cpsolver.mapvariables.put(ci.getIndex(), readModelVariable(ci));
					}
					break;
				case CONSTANT_DOUBLE:
					cr = (RealConstantVariable) v;
					if (!cpsolver.mapvariables.containsKey(cr.getIndex())) {
						cpsolver.mapvariables.put(cr.getIndex(), readModelVariable(cr));
					}
					break;
				case CONSTANT_SET:
					cs = (SetConstantVariable) v;
					if (!cpsolver.mapvariables.containsKey(cs.getIndex())) {
						cpsolver.mapvariables.put(cs.getIndex(), readModelVariable(cs));
					}
					break;
				}
			}
		}
    }

    public void readMultipleVariables(final CPModel model){
        MultipleVariables mv;
        final Iterator it = model.getMultipleVarIterator();
		while (it.hasNext()) {
			mv = (MultipleVariables) it.next();
			if (!cpsolver.mapvariables.containsKey(mv.getIndex())) {
				cpsolver.mapvariables.put(mv.getIndex(), readModelVariable(mv));
			}
		}
    }


    @SuppressWarnings({"unchecked"})
    public Var readModelVariable(final Variable v) {
		final VariableManager vm = v.getVariableManager();
    	if (vm != null) {
			final Var var = vm.makeVariable(cpsolver, v);
			checkOptions(v, var);
			return var;
		}
		return null;
	}

	/**
	 * handle options associated to a given var.
	 * @param v the model variable to check
     * @param var the solver variable
	 */
	private void checkOptions(final Variable v, final Var var) {
		if (v.getOptions().contains(Options.V_DECISION)) {
			LOGGER.warning("CPOptions.V_DECISION or \"cp:decision\" option are deprecated and have no longer effect on decision variables pool!");
		}else
        if (v.getOptions().contains(Options.V_NO_DECISION)) {
			removeFromDecisionPool(var);
		}
		if(v.getOptions().contains(Options.V_OBJECTIVE)){
			cpsolver.setObjective(var);
		}
		if(v.getOptions().contains(Options.V_MAKESPAN)){
			cpsolver.setMakespan(var);
		}
	}
	/**
	 * Add decision or non decision variable to the correct list
	 *
	 * @param v        the variable to add
     */
	private void removeFromDecisionPool(final Var v) {
		if (v instanceof IntDomainVar) {
			intNoDecisionVar.add((IntDomainVar) v);
		} else if (v instanceof SetVar) {
			setNoDecisionVar.add((SetVar) v);
		} else if (v instanceof RealVar) {
			realNoDecisionVar.add((RealVar) v);
		} else if (v instanceof TaskVar) {
			taskNoDecisionVar.add((TaskVar) v);
		}

	}
	/**
	 * Read the decision variables declared in the model
	 */
	protected void readDecisionVariables() {
		// Integer decision variables
        cpsolver.intDecisionVars.addAll(cpsolver.intVars.toList());
        if (!intNoDecisionVar.isEmpty()) {
			cpsolver.intDecisionVars.removeAll(intNoDecisionVar);
			cpsolver.intDecisionVars.removeAll(cpsolver.getIntConstantSet());
		}
		// Set decision variables
        cpsolver.setDecisionVars.addAll(cpsolver.setVars.toList());
        if (!setNoDecisionVar.isEmpty()) {
            cpsolver.setDecisionVars.removeAll(setNoDecisionVar);
        }
        // Real decision variables
        cpsolver.floatDecisionVars.addAll(cpsolver.floatVars.toList());
        if (!realNoDecisionVar.isEmpty()) {
			cpsolver.floatDecisionVars.removeAll(realNoDecisionVar);
			cpsolver.floatDecisionVars.removeAll(cpsolver.getRealConstantSet());
		}
		// Task decision variables
        cpsolver.taskDecisionVars.addAll(cpsolver.taskVars.toList());
        if (!taskNoDecisionVar.isEmpty()) {
			cpsolver.taskDecisionVars.removeAll(taskNoDecisionVar);
		}
	}

	//************************************************* CONCERNING CONSTRAINTS *********************************************


	public void readConstraints(final CPModel model) {
		Constraint ic;
		SConstraint c;
		Boolean decomp = model.getDefaultExpressionDecomposition();
		final Iterator<Constraint> it = model.getConstraintIterator();
		while (it.hasNext()) {
			ic = it.next();
			if (!cpsolver.mapconstraints.containsKey(ic.getIndex())) {
				if (ic.getOptions().contains(Options.E_DECOMP)) {
					decomp = true;
				}
				c = readModelConstraint(ic, decomp);
				if (ic.getOptions().contains(Options.C_POST_PONED)) {
                    postponedConstraint.add(c);
                }else{
                    cpsolver.post(c);
                }
				cpsolver.mapconstraints.put(ic.getIndex(), c);
			}
		}
        for (final SConstraint ppc : postponedConstraint) {
            cpsolver.post(ppc);
        }
        if( cpsolver.isUniqueReading() ){
		    cpsolver.postTaskConsistencyConstraints();
		    cpsolver.postMakespanConstraint();
        }
	}

	public void readConstraint(final Constraint ic, final Boolean decomp) {
		if (!cpsolver.mapconstraints.containsKey(ic.getIndex())) {
			final SConstraint c = readModelConstraint(ic, decomp);
			cpsolver.mapconstraints.put(ic.getIndex(), c);
			cpsolver.post(c);
		}
	}

	public SConstraint makeSConstraint(final Constraint ic, final Boolean decomp) {
		return readModelConstraint(ic, decomp);
	}

	public SConstraint makeSConstraint(final Constraint ic) {
		return readModelConstraint(ic, false);
	}

    public SConstraint[] makeSConstraintAndOpposite(final Constraint ic, final Boolean decomp) {
		final SConstraint[] cs = new SConstraint[2];
        if (ic instanceof MetaConstraint) {
			cs[0] =  createMetaConstraint(ic, decomp);
            cs[1] = cs[0].opposite(cpsolver);
            return cs;
		}

		if (ic instanceof ComponentConstraint) {
			if (ic.getConstraintType().canContainExpression && containExpression(ic.getVariables())) {
				cs[0] =  createMetaConstraint(ic, decomp);
                cs[1] = cs[0].opposite(cpsolver);
                return cs;
			}
			final ComponentConstraint cc = (ComponentConstraint) ic;
			final ConstraintManager cm = cc.getConstraintManager();
			return cm.makeConstraintAndOpposite(cpsolver, cc.getVariables(), cc.getParameters(),  cc.getOptions());
		}
		return null;
	}

	public SConstraint[] makeSConstraintAndOpposite(final Constraint ic) {
		return makeSConstraintAndOpposite(ic, false);
	}

	SConstraint readModelConstraint(final Constraint ic, final Boolean decomp) {

		if (ic instanceof MetaConstraint) {
			return createMetaConstraint(ic, decomp);
		}

		if (ic instanceof ComponentConstraint) {
			if (ic.getConstraintType().canContainExpression && containExpression(ic.getVariables())) {
				return createMetaConstraint(ic, decomp);
			}
			final ComponentConstraint cc = (ComponentConstraint) ic;
			final ConstraintManager cm = cc.getConstraintManager();
			return cm.makeConstraint(cpsolver, cc.getVariables(), cc.getParameters(),  cc.getOptions());
		}
		return null;
	}

	/**
	 * Check wether a pool of variables is composed of simple variables or not
	 * @param vars pool of variables
	 * @return true if only simple variable,
	 */
	private static boolean containExpression(final Variable[] vars){
		if (vars == null) {
			return false;
		}
        for (final Variable v : vars) {
            final VariableType type = v.getVariableType();
            if (type == VariableType.INTEGER_EXPRESSION) {
                return true;
            }
        }
		return false;
	}

	private IntDomainVar[] integerVariableToIntDomainVar(final Variable[] tab) {
		return integerVariableToIntDomainVar(tab, tab.length);
	}

	private IntDomainVar[] integerVariableToIntDomainVar(final Variable[] tab, final int n) {
		final IntDomainVar[] newTab = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			newTab[i] = (IntDomainVar) cpsolver.mapvariables.get(tab[i].getIndex());
		}
		return newTab;
	}

	private IntDomainVar[][] integerVariableToIntDomainVar(final Variable[][] tab, final int n) {
		final IntDomainVar[][] newTab = new IntDomainVar[n][];
		for (int i = 0; i < n; i++) {
			newTab[i] = integerVariableToIntDomainVar(tab[i]);
		}
		return newTab;
	}


	protected IntDomainVar[][] integerVariableToIntDomainVar(final Variable[][] tab) {
		return integerVariableToIntDomainVar(tab, tab.length);
	}


	protected SConstraint createMetaConstraint(final Constraint ic, final Boolean decomp) {
		final ExpressionSConstraint c = new ExpressionSConstraint(buildBoolNode(ic));
		c.setDecomposeExp(decomp);
		c.setScope(cpsolver);
        if (ic.getOptions().contains("cp:ac")) {
            c.setLevelAc(0);
        } else if (ic.getOptions().contains(Options.C_EXT_FC)) {
            c.setLevelAc(1);
        }
        //important step to deal properly with linear equation
		final SConstraint intensional = ExpressionDetector.getScalarConstraint(c, cpsolver);
		if (intensional != null) {
			return intensional;
		} else {
			return c;
		}
	}

	protected BoolNode buildBoolNode(final Constraint ic) {
		IntegerExpressionVariable[] vars = null;
		if(ic.getNbVars()>0){
			vars = new IntegerExpressionVariable[ic.getNbVars()];
			for (int i = 0; i < ic.getVariables().length; i++) {
				vars[i] = (IntegerExpressionVariable)ic.getVariables()[i];
			}
		}
		return (BoolNode)ic.getExpressionManager().makeNode(cpsolver, new Constraint[]{ic}, vars);
	}

}
