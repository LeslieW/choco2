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
package choco.cp.model.managers.constraints.global;

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.Geost_Constraint;
import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.HashSet;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 19:38:51
 */
public class GeostManager extends IntConstraintManager {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
        if (solver instanceof CPSolver) {
            if(parameters instanceof Object[]){
                Object[] params = (Object[])parameters;
                int dim = (Integer)params[0];
                Vector<ShiftedBox> shiftedBoxes = (Vector<ShiftedBox>)params[1];
                Vector<ExternalConstraint> ectr = (Vector<ExternalConstraint>)params[2];
                Vector<GeostObject> vgo = (Vector<GeostObject>)params[3];
                Vector<int[]> ctrlVs = (Vector<int[]>)params[4];
                Vector<Obj> vo = new Vector<Obj>(vgo.size());
                for (int i = 0; i < vgo.size(); i++) {
                    GeostObject g = vgo.elementAt(i);
                    vo.add(i, new Obj(g.getDim(),
                            g.getObjectId(),
                            solver.getVar(g.getShapeId()),
                            solver.getVar(g.getCoordinates()),
                            solver.getVar(g.getStartTime()),
                            solver.getVar(g.getDurationTime()),
                            solver.getVar(g.getEndTime())));
                }
                if (ctrlVs == null) {
                    return new Geost_Constraint(solver.getVar((IntegerVariable[])variables), dim, vo, shiftedBoxes, ectr);
                } else {
                    return new Geost_Constraint(solver.getVar((IntegerVariable[])variables), dim, vo, shiftedBoxes, ectr, ctrlVs);
                }
            }
        }
        if (Choco.DEBUG) {
            System.err.println("Could not found implementation for Geost !");
        }
        return null;
    }

}
