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
package choco.cp.solver.preprocessor;

import choco.cp.common.util.preprocessor.ExpressionTools;
import choco.cp.solver.CPModelToCPSolver;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.BoolNode;
import gnu.trove.TLongObjectHashMap;

/* 
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 9, 2008
 * Since : Choco 2.0.0
 *
 */
public class PPModelToCPSolver extends CPModelToCPSolver {


    /**
     * store if an expression constraint has already be been built for
     * a model constraint.
     */
    //This could be implemented as a hook on constraint ?
    protected TLongObjectHashMap<ExpressionSConstraint> knownExpressionCts;

    public PPModelToCPSolver(CPSolver cpsolver) {
        super(cpsolver);
        knownExpressionCts = new TLongObjectHashMap<ExpressionSConstraint>();
    }


    public void storeExpressionSConstraint(Constraint c, ExpressionSConstraint ic) {
        knownExpressionCts.put(c.getIndex(),ic);
    }

    /**
     * The number of heavy extensional constraint posted
     * using an expression
     */
    protected int nbHeavyBin = 0;


    /**
     * Override the creation of Expression Constraint as in the preprocessing
     * they might have been built earlier or identified as an intensional
     * constraint. 
     * @param ic
     * @param decomp
     * @return
     */
    protected SConstraint createMetaConstraint(Constraint ic, Boolean decomp) {
        ExpressionSConstraint c = knownExpressionCts.get(ic.getIndex());
        if (c == null) {
            c = new ExpressionSConstraint(super.buildBoolNode(ic));
        }
        c.setScope(cpsolver);
        c.setDecomposeExp(false); //todo: check default value of decomp ?
        //c.setDecomposeExp(decomp);
        if (ExpressionTools.toBeDecomposed(c)) {
            c.setDecomposeExp(true);
        } else if (ExpressionTools.isVeryBinaryHeavy(c)) {
            nbHeavyBin ++;
        }
        if (nbHeavyBin > 2000) {
            c.setLevelAc(1);
        } else c.setLevelAc(0);

        SConstraint intensional = c.getKnownIntensionalConstraint();
        if (intensional == null)
            intensional = ExpressionTools.getIntentionalConstraint(c, cpsolver);
        if (intensional != null)
            return intensional;
        else return c;
    }



    // to make the builNode method accessible from the blackbox solver
    public BoolNode buildNode(Constraint ic) {
       return super.buildBoolNode(ic);
    }

   
    protected void readBBDecisionVariables() {
        super.readDecisionVariables();
    }
}
