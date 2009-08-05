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

import choco.cp.model.CPModel;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.cp.solver.preprocessor.detectors.ExpressionDetector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.*;
import choco.kernel.model.variables.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * ***********************************************
 * _       _                            *
 * |  °(..)  |                           *
 * |_  J||L _|        ChocoSolver.net    *
 * *
 * Choco is a java library for constraint     *
 * satisfaction problems (CSP), constraint    *
 * programming (CP) and explanation-based     *
 * constraint solving (e-CP). It is built     *
 * on a event-based propagation mechanism     *
 * with backtrackable structures.             *
 * *
 * Choco is an open-source software,          *
 * distributed under a BSD licence            *
 * and hosted by sourceforge.net              *
 * *
 * + website : http://choco.emn.fr            *
 * + support : choco@emn.fr                   *
 * *
 * Copyright (C) F. Laburthe,                 *
 * N. Jussien    1999-2008      *
 * *************************************************
 * User:    charles
 * Date: 31 mars 2008
 * <p/>
 * Main and unique class for constraint programming transposition from Model to Solver.
 * (Well, it creates CPSolver objects from CPModel objects declare by the users).
 * It is separeted into 2 big parts: variables transposition and constraints transposition.
 */
public class CPModelToCPSolver {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

	protected CPSolver cpsolver;

	protected ExpressionDetector expDetect;

	public final static ArrayList<Class> CLASS_LIST = new ArrayList<Class>();

	protected HashMap<String, VariableManager> variableManagers = new HashMap<String, VariableManager>();

	private HashSet<IntDomainVar> intDecisionVar = new HashSet<IntDomainVar>();
	private HashSet<IntDomainVar> intNoDecisionVar = new HashSet<IntDomainVar>();

	private HashSet<SetVar> setDecisionVar = new HashSet<SetVar>();
	private HashSet<SetVar> setNoDecisionVar = new HashSet<SetVar>();

	private HashSet<RealVar> realDecisionVar = new HashSet<RealVar>();
	private HashSet<RealVar> realNoDecisionVar = new HashSet<RealVar>();

	private HashSet<TaskVar> taskDecisionVar = new HashSet<TaskVar>();
	private HashSet<TaskVar> taskNoDecisionVar = new HashSet<TaskVar>();


	public CPModelToCPSolver(CPSolver cpsolver) {
		this.cpsolver = cpsolver;
		this.expDetect = new ExpressionDetector();
	}

	//************************************************* CONCERNING VARIABLES ***********************************************

	/**
	 * Read variable from the model, transpose it into Solver variables,
	 * adding it to the Solver directly.
	 *
	 * @param model to read
	 */
	public void readVariables(CPModel model) {
		readIntegerVariables(model);
        readRealVariables(model);
		readSetVariables(model);
		readConstants(model);
        readMultipleVariables(model);

		readParameters(model);
	}

    public void readIntegerVariables(CPModel model){
        IntegerVariable i;

		Iterator it = model.getIntVarIterator();
		while (it.hasNext()) {
			i = (IntegerVariable) it.next();
			if (!cpsolver.mapvariables.containsKey(i.getIndex())) {
				cpsolver.mapvariables.put(i.getIndex(), readModelVariable(i));
			}
		}
    }

    public void readRealVariables(CPModel model){
        RealVariable r;
		Iterator it = model.getRealVarIterator();
		while (it.hasNext()) {
			r = (RealVariable) it.next();
			if (!cpsolver.mapvariables.containsKey(r.getIndex())) {
				cpsolver.mapvariables.put(r.getIndex(), readModelVariable(r));
			}
		}
    }

    public void readSetVariables(CPModel model){
        SetVariable s;
        Iterator it = model.getSetVarIterator();
		while (it.hasNext()) {
			s = (SetVariable) it.next();
			if (!cpsolver.mapvariables.containsKey(s.getIndex())) {
				SetVar setVar = (SetVar) readModelVariable(s);
				cpsolver.mapvariables.put(s.getIndex(), setVar);
				cpsolver.mapvariables.put(s.getCard().getIndex(), setVar.getCard());
				checkOptions(s.getCard(), setVar.getCard());
			}
		}
    }

    public void readConstants(CPModel model){
        Variable v;
		IntegerConstantVariable ci;
		RealConstantVariable cr;
		SetConstantVariable cs;
		Iterator it = model.getConstVarIterator();
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

    public void readMultipleVariables(CPModel model){
        MultipleVariables mv;
        Iterator it = model.getMultipleVarIterator();
		while (it.hasNext()) {
			mv = (MultipleVariables) it.next();
			if (!cpsolver.mapvariables.containsKey(mv.getIndex())) {
				cpsolver.mapvariables.put(mv.getIndex(), readModelVariable(mv));
			}
		}
    }

    public void readParameters(CPModel model) {
        cpsolver.setPrecision(model.getPrecision());
        cpsolver.setReduction(model.getReduction());
    }


    public Var readModelVariable(Variable v) {
		if (v instanceof IComponentVariable) {
			IComponentVariable vv = (IComponentVariable) v;

			VariableManager vm = variableManagers.get(vv.getComponentClass());

			if (vm == null) {
				//We get it by reflection !
				Class componentClass = null;
				try {
					componentClass = Class.forName(vv.getComponentClass());
				} catch (ClassNotFoundException e) {
					LOGGER.severe("Component class could not be found: " + vv.getComponentClass());
					System.exit(-1);
				}
				try {
					vm = (VariableManager) componentClass.newInstance();
				} catch (InstantiationException e) {
					LOGGER.severe("Component class could not be instantiated: " + vv.getComponentClass());
					System.exit(-1);
				} catch (IllegalAccessException e) {
					LOGGER.severe("Component class could not be accessed: " + vv.getComponentClass());
					System.exit(-1);
				}
				variableManagers.put(vv.getComponentClass(), vm);
			}

			Var var = vm.makeVariable(cpsolver, v);
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
	private void checkOptions(Variable v,Var var) {
		if (v.getOptions().contains("cp:decision")) {
			checkDecision(var, true);
		}else if (v.getOptions().contains("cp:no_decision")) {
			checkDecision(var, false);
		}
		if(v.getOptions().contains("cp:objective")){
			cpsolver.setObjective(var);
		}
		if(v.getOptions().contains("cp:makespan")){
			cpsolver.getScheduler().setMakespan( (IntDomainVar) var);
		}
	}
	/**
	 * Add decision or non decision variable to the correct list
	 *
	 * @param v        the variable to add
	 * @param decision wether it is a decisionnal variable or not
	 */
	private void checkDecision(Var v, boolean decision) {
		if (v instanceof IntDomainVar) {
			(decision ? intDecisionVar : intNoDecisionVar).add((IntDomainVar) v);
		} else if (v instanceof SetVar) {
			(decision ? setDecisionVar : setNoDecisionVar).add((SetVar) v);
		} else if (v instanceof RealVar) {
			(decision ? realDecisionVar : realNoDecisionVar).add((RealVar) v);
		} else if (v instanceof TaskVar) {
			(decision ? taskDecisionVar : taskNoDecisionVar).add((TaskVar) v);
		}

	}
	/**
	 * Read the decision variables declared in the model
	 */
	protected void readDecisionVariables() {
		// Integer decision variables
		if (!intDecisionVar.isEmpty()) {
			cpsolver.intDecisionVars.addAll(intDecisionVar);
		} else if (!intNoDecisionVar.isEmpty()) {
			cpsolver.intDecisionVars.addAll(cpsolver.intVars.toList());
			cpsolver.intDecisionVars.removeAll(intNoDecisionVar);
			cpsolver.intDecisionVars.removeAll(cpsolver.getIntConstantSet());
		}
		// Set decision variables
		if (!setDecisionVar.isEmpty()) {
			cpsolver.setDecisionVars.addAll(setDecisionVar);
		} else if (!setNoDecisionVar.isEmpty()) {
			cpsolver.setDecisionVars.addAll(cpsolver.setVars.toList());
			cpsolver.setDecisionVars.removeAll(setNoDecisionVar);
		}
		// Real decision variables
		if (!realDecisionVar.isEmpty()) {
			cpsolver.floatDecisionVars.addAll(realDecisionVar);
		} else if (!realNoDecisionVar.isEmpty()) {
			cpsolver.floatDecisionVars.addAll(cpsolver.floatVars.toList());
			cpsolver.floatDecisionVars.removeAll(realNoDecisionVar);
			cpsolver.intDecisionVars.removeAll(cpsolver.getRealConstantSet());
		}
		// Task decision variables
		if (!taskDecisionVar.isEmpty()) {
			cpsolver.taskDecisionVars.addAll(taskDecisionVar);
		} else if (!taskNoDecisionVar.isEmpty()) {
			cpsolver.taskDecisionVars.addAll(cpsolver.taskVars.toList());
			cpsolver.taskDecisionVars.removeAll(taskNoDecisionVar);
		}
	}

	//************************************************* CONCERNING CONSTRAINTS *********************************************


	public void readConstraints(CPModel model) {
		Constraint ic;
		SConstraint c;
		Boolean decomp = model.getDefaultExpressionDecomposition();
		Iterator<Constraint> it = model.getConstraintIterator();
		while (it.hasNext()) {
			ic = it.next();
			if (!cpsolver.mapconstraints.containsKey(ic.getIndex())) {
				if (ic.getOptions().contains("cp:decomp")) {
					decomp = true;
				}
				c = readModelConstraint(ic, decomp);
				cpsolver.post(c);
				cpsolver.mapconstraints.put(ic.getIndex(), c);
			}
		}
		cpsolver.postRedundantTaskConstraints();

	}

	public void readConstraint(Constraint ic, Boolean decomp) {
		if (!cpsolver.mapconstraints.containsKey(ic.getIndex())) {
			SConstraint c = readModelConstraint(ic, decomp);
			cpsolver.mapconstraints.put(ic.getIndex(), c);
			cpsolver.post(c);
		}
	}

	public SConstraint makeSConstraint(Constraint ic, Boolean decomp) {
		return readModelConstraint(ic, decomp);
	}

	public SConstraint makeSConstraint(Constraint ic) {
		return readModelConstraint(ic, false);
	}

    public SConstraint[] makeSConstraintAndOpposite(Constraint ic, Boolean decomp) {
		SConstraint[] cs = new SConstraint[2];
        if (ic instanceof MetaConstraint) {
			cs[0] =  createMetaConstraint(ic, decomp);
            cs[1] = cs[0].opposite();
            return cs;
		}

		if (ic instanceof ComponentConstraint) {
			if (!ic.getConstraintType().equals(ConstraintType.REIFIEDINTCONSTRAINT) &&
					!allSimpleVariable(ic.getVariables())) {
				cs[0] =  createMetaConstraint(ic, decomp);
                cs[1] = cs[0].opposite();
                return cs;
			}
			ComponentConstraint cc = (ComponentConstraint) ic;
			ConstraintManager cm = cc.getCm();
			return cm.makeConstraintAndOpposite(cpsolver, cc.getVariables(), cc.getParameters(), cc.getOptions());
		}
		return null;
	}

	public SConstraint[] makeSConstraintAndOpposite(Constraint ic) {
		return makeSConstraintAndOpposite(ic, false);
	}

	protected SConstraint readModelConstraint(Constraint ic, Boolean decomp) {

		if (ic instanceof MetaConstraint) {
			return createMetaConstraint(ic, decomp);
		}

		if (ic instanceof ComponentConstraint) {
			if (!ic.getConstraintType().equals(ConstraintType.REIFIEDINTCONSTRAINT) &&
					!allSimpleVariable(ic.getVariables())) {
				return createMetaConstraint(ic, decomp);
			}
			ComponentConstraint cc = (ComponentConstraint) ic;
			ConstraintManager cm = cc.getCm();
			return cm.makeConstraint(cpsolver, cc.getVariables(), cc.getParameters(), cc.getOptions());
		}
		return null;
	}

	/**
	 * Check wether a pool of variables is composed of simple variables or not
	 * @param vars pool of variables
	 * @return true if only simple variable,
	 */
	private boolean allSimpleVariable(Variable[] vars){
		if (vars == null) {
			return true;
		}
        for (Variable v : vars) {
            VariableType type = v.getVariableType();
            if (type == VariableType.INTEGER_EXPRESSION
                //                    || type == VariableType.SET_EXPRESSION
                //                    || type == VariableType.REAL_EXPRESSION
                //                    || type == VariableType.REAL
                //                    || type == VariableType.SET
                    ) {
                return false;
            }
        }
		return true;
	}

	private IntDomainVar[] integerVariableToIntDomainVar(Variable[] tab) {
		return integerVariableToIntDomainVar(tab, tab.length);
	}

	private IntDomainVar[] integerVariableToIntDomainVar(Variable[] tab, int n) {
		IntDomainVar[] newTab = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			newTab[i] = (IntDomainVar) cpsolver.mapvariables.get(tab[i].getIndex());
		}
		return newTab;
	}

	private IntDomainVar[][] integerVariableToIntDomainVar(Variable[][] tab, int n) {
		IntDomainVar[][] newTab = new IntDomainVar[n][];
		for (int i = 0; i < n; i++) {
			newTab[i] = integerVariableToIntDomainVar(tab[i]);
		}
		return newTab;
	}


	protected IntDomainVar[][] integerVariableToIntDomainVar(Variable[][] tab) {
		return integerVariableToIntDomainVar(tab, tab.length);
	}


	protected SConstraint createMetaConstraint(Constraint ic, Boolean decomp) {
		ExpressionSConstraint c = new ExpressionSConstraint(buildBoolNode(ic));
		c.setDecomposeExp(decomp);
		c.setScope(cpsolver);
        if (ic.getOptions().contains("cp:ac")) {
            c.setLevelAc(0);
        } else if (ic.getOptions().contains("cp:fc")) {
            c.setLevelAc(1);
        }
        //important step to deal properly with linear equation
		SConstraint intensional = expDetect.getScalarConstraint(c, cpsolver);
		if (intensional != null) {
			return intensional;
		} else {
			return c;
		}
	}

	protected BoolNode buildBoolNode(Constraint ic) {
		IntegerExpressionVariable[] vars = null;
		if(ic.getNbVars()>0){
			vars = new IntegerExpressionVariable[ic.getNbVars()];
			for (int i = 0; i < ic.getVariables().length; i++) {
				vars[i] = (IntegerExpressionVariable)ic.getVariables()[i];
			}
		}
		return (BoolNode)ic.getEm().makeNode(cpsolver, new Constraint[]{ic}, vars);
	}

}
