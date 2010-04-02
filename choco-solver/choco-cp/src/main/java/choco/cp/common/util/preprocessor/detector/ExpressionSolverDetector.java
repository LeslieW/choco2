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
import choco.cp.common.util.preprocessor.AbstractDetector;
import choco.cp.common.util.preprocessor.ExpressionTools;
import choco.cp.model.CPModel;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.model.constraints.*;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 2 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class ExpressionSolverDetector extends AbstractDetector{

    private final PreProcessCPSolver ppsolver;

    public ExpressionSolverDetector(final CPModel model, final PreProcessCPSolver solver) {
        super(model);
        ppsolver = solver;
    }

    /**
     * Apply the detection defined within the detector.
     */
    @Override
    public void apply() {
        final Iterator<Constraint> it = model.getConstraintIterator();
        final List<Constraint> neqToAdd = new LinkedList<Constraint>();
        while (it.hasNext()) {
            final Constraint ic = it.next();
            if (!ppsolver.contains(ic) && isAValidExpression(ic)) {
                final ExpressionSConstraint c = new ExpressionSConstraint(ppsolver.getMod2Sol().buildNode(ic));
                c.setScope(ppsolver);
                ppsolver.getMod2Sol().storeExpressionSConstraint(ic, c);
                final SConstraint intensional = ExpressionTools.getIntentionalConstraint(c, ppsolver);
                if (intensional != null) {
                    c.setKnownIntensionalConstraint(intensional);
                } else {
                    if (ExpressionTools.encompassDiff(c)) {
                       final IntegerVariable[] vars = ((AbstractConstraint) ic).getIntVariableScope();
                       neqToAdd.add(Choco.neq(vars[0],vars[1]));
                    }
                }
            }
        }
        for (final Constraint aNeqToAdd : neqToAdd) {
            model.addConstraint(aNeqToAdd);
        }
    }

    private static boolean isAValidExpression(final Constraint ic) {
        return ic instanceof MetaConstraint ||
                (ic instanceof ComponentConstraint &&
                 (ic.getConstraintType() == ConstraintType.EQ ||
                 ic.getConstraintType() == ConstraintType.NEQ ||
                 ic.getConstraintType() == ConstraintType.LEQ ||
                 ic.getConstraintType() == ConstraintType.GEQ ||
                 ic.getConstraintType() == ConstraintType.GT ||
                 ic.getConstraintType() == ConstraintType.LT));
    }
}
