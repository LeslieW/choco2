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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.bool.sat.ClauseStore;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 16 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ClausesManager extends MixedConstraintManager {

    public SConstraint makeConstraint(Solver solver, Variable[] vars, Object parameters, HashSet<String> options) {
        if (solver instanceof CPSolver) {
            IntDomainVar[] vs = ((CPSolver)solver).getVar(IntDomainVar.class, vars);
            ClauseStore cs =  new ClauseStore(vs);
            if (options.contains("cp:entail")) {
                cs.setEfficientEntailmentTest();
            }
            Constraint[] constraints = (Constraint[])((Object[])parameters)[1];
            for(int c = 0; c < constraints.length; c++){
                ComponentConstraint clause = (ComponentConstraint)constraints[c];
                int offset = (Integer)clause.getParameters();
                IntegerVariable[] posLits = new IntegerVariable[offset];
                IntegerVariable[] negLits = new IntegerVariable[clause.getNbVars() - offset];
                for(int v = 0; v < clause.getNbVars(); v++){
                    if(v < offset){
                        posLits[v] = (IntegerVariable)clause.getVariables()[v];
                    }else{
                        negLits[v-offset] = (IntegerVariable) clause.getVariables()[v];
                    }
                }
                cs.addClause(solver.getVar(posLits), solver.getVar(negLits));
            }
            return cs;
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }

}
