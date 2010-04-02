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
package choco.cp.model.preprocessor;

import choco.cp.common.util.preprocessor.AbstractDetector;
import choco.cp.common.util.preprocessor.DetectorFactory;
import choco.cp.common.util.preprocessor.detector.AbstractIntegerVariableEqualitiesDetector;
import choco.cp.common.util.preprocessor.detector.AbstractTaskVariableEqualitiesDetector;
import choco.cp.common.util.preprocessor.detector.CliquesModelDetector;
import choco.cp.model.CPModel;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 *
 * A factory to apply detectors on a model.
 */
public final class ModelDetectorFactory extends DetectorFactory {

    /**
     * Run {@link choco.cp.common.util.preprocessor.AbstractDetector#apply()} and {@link choco.cp.common.util.preprocessor.AbstractDetector#commit()} for each {@code detectors}.
     * @param detectors list of detectors to run
     */
    public static void run(final CPModel model, final AbstractDetector... detectors){
        associateIndexes(model);
        for(AbstractDetector detector : detectors){
            detector.applyThenCommit();
        }
        resetIndexes(model);
    }

    /**
     * Detect equalities between {@link IntegerVariable} within a model
     * @param m model
     * @return new instance of {@link choco.cp.common.util.preprocessor.detector.AbstractIntegerVariableEqualitiesDetector}
     */
    public static AbstractDetector intVarEqDet(final CPModel m){
        return new AbstractIntegerVariableEqualitiesDetector.IntegerVariableEqualitiesModelDetector(m);
    }

    /**
     * Detect equalities between {@link TaskVariable} within a model
     * @param m model
     * @return new instance of {@link choco.cp.common.util.preprocessor.detector.AbstractTaskVariableEqualitiesDetector}
     */
    public static AbstractDetector taskVarEqDet(final CPModel m){
        return new AbstractTaskVariableEqualitiesDetector.TaskVariableEqualitiesModelDetector(m);
    }

    /**
     * Detect cliques.
     * @param m model to analyze
     * @param breakSymetries
     * @return new instance of {@link choco.cp.common.util.preprocessor.detector.CliquesModelDetector.CliqueModelDetector}
     */
    public static AbstractDetector cliqueDetector(final CPModel m, final boolean breakSymetries){
        return new CliquesModelDetector(m, breakSymetries);
    }
}
