/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.common.util.preprocessor.detector;

import choco.Choco;
import static choco.Choco.allDifferent;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.preprocessor.SymetryBreakingModelDetector;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.Iterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 2 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 * <p/>
 * A class dedicated to detect clique of differences
 * and state the corresponding global constraints
 */
public class CliquesModelDetector extends AbstractGraphBasedDetector {

    private final SymetryBreakingModelDetector symbreakD;

    public CliquesModelDetector(final CPModel model, final boolean breakSymetries) {
        super(model);
        if(breakSymetries){
            symbreakD = new SymetryBreakingModelDetector(model);
        }else{
            symbreakD = new SymetryBreakingModelDetector.EmptySymetryBreakingModelDetector(model);
        }
    }

    /**
     * Apply the detection defined within the detector.
     */
    @Override
    public void apply() {
        if (addAllNeqEdges()) {
            final CliqueIterator it = cliqueIterator();
            while (it.hasNext()) {
                final IntegerVariable[] cl = it.next();
                if (cl.length > 2) {
                    add(allDifferent(Options.C_ALLDIFFERENT_BC, cl));
                    symbreakD.setMaxClique(cl);
                    it.remove();
                } else {
                    add(Choco.neq(cl[0], cl[1]));
                }
            }
            symbreakD.applyThenCommit();
        }
    }

    /**
     * Build the constraint graph of differences
     *
     * @return boolean
     */
    public boolean addAllNeqEdges() {
        Iterator<Constraint> itneq = model.getConstraintByType(ConstraintType.NEQ);
        while (itneq.hasNext()) {
            Constraint neq = itneq.next();
            Variable[] vars = neq.getVariables();
            if (isRealBinaryNeq(vars)) {
                //the NEQ can take a constant...
                addEdge(neq.getVariables()[0], neq.getVariables()[1], neq);
            }
        }
        return diffs.nbEdges > 0;
    }

    public static boolean isRealBinaryNeq(Variable[] vars) {
        if (vars.length != 2) return false;
        for (Variable var : vars) {
            if (var.getVariableType() != VariableType.INTEGER)
                return false;
        }
        return true;
    }

}
