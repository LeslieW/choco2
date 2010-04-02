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
package choco.cp.solver.preprocessor;

import choco.cp.common.util.preprocessor.AbstractDetector;
import choco.cp.common.util.preprocessor.DetectorFactory;
import choco.cp.common.util.preprocessor.detector.AbstractIntegerVariableEqualitiesDetector;
import choco.cp.common.util.preprocessor.detector.AbstractTaskVariableEqualitiesDetector;
import choco.cp.common.util.preprocessor.detector.DisjunctionsSolverDetector;
import choco.cp.common.util.preprocessor.detector.ExpressionSolverDetector;
import choco.cp.model.CPModel;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 2 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class SolverDetectorFactory extends DetectorFactory{

    /**
     * Detect equalities between {@link IntegerVariable} within a model
     * @param m model
     * @return new instance of {@link choco.cp.common.util.preprocessor.detector.AbstractIntegerVariableEqualitiesDetector}
     */
    public static AbstractDetector intVarEqDet(final CPModel m,
                                                                        final PreProcessCPSolver ppsolver){
        return new AbstractIntegerVariableEqualitiesDetector.IntegerVariableEqualitiesSolverDetector(m, ppsolver);
    }

    /**
     * Detect equalities between {@link TaskVariable} within a model
     * @param m model
     * @param ppsolver instance of {@link PreProcessCPSolver}
     * @return new instance of {@link choco.cp.common.util.preprocessor.detector.AbstractTaskVariableEqualitiesDetector}
     */
    public static AbstractDetector taskVarEqDet(final CPModel m, final PreProcessCPSolver ppsolver){
        return new AbstractTaskVariableEqualitiesDetector.TaskVariableEqualitiesSolverDetector(m, ppsolver);
    }

    /**
     * Detect disjunctions.
     * @param m model to analyze
     * @param ppsolver solver scope
     * @return new instance of {@link choco.cp.common.util.preprocessor.detector.DisjunctionsSolverDetector}
     */
    public static AbstractDetector disjunctionDetector(final CPModel m, final PreProcessCPSolver ppsolver){
        return new DisjunctionsSolverDetector(m, ppsolver);
    }

    /**
     * Detect expressions.
     * @param m model to analyze
     * @param ppsolver solver scope
     * @return new instance of {@link choco.cp.common.util.preprocessor.detector.ExpressionSolverDetector}
     */
    public static AbstractDetector expressionDetector(final CPModel m, final PreProcessCPSolver ppsolver){
        return new ExpressionSolverDetector(m, ppsolver);
    }
}
