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
package choco.kernel.model.detector;

import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 *
 * A factory to apply detectors on a model.
 */
public final class DetectorFactory {


    /**
     * Logger
     */
    protected static final Logger LOGGER = ChocoLogging.getEngineLogger();


    /**
     * Add an index to the variables to be able to map them easily
     * to nodes of the constraint graph
     * @param m model
     */
    private static void associateIndexes(final CPModel m) {
        Iterator it = m.getIntVarIterator();
        int cpt = 0;
        while (it.hasNext()) {
            final IntegerVariable iv = (IntegerVariable) it.next();
            iv.setHook(cpt);
            cpt++;
        }
        it = m.getMultipleVarIterator();
        cpt = 0;
        while (it.hasNext()) {
            final MultipleVariables iv = (MultipleVariables) it.next();
            if(iv instanceof TaskVariable){
                iv.setHook(cpt);
                cpt++;
            }
        }
    }

    /**
     * Add an index to the variables to be able to map them easily
     * to nodes of the constraint graph
     * @param m model
     */
    private static void resetIndexes(final CPModel m) {
        Iterator it = m.getIntVarIterator();
        while (it.hasNext()) {
            final IntegerVariable iv = (IntegerVariable) it.next();
            iv.resetHook();
        }
        it = m.getMultipleVarIterator();
        while (it.hasNext()) {
            final MultipleVariables iv = (MultipleVariables) it.next();
            if(iv instanceof TaskVariable){
                iv.resetHook();
            }
        }
    }

    /**
     * Run {@link AbstractDetector#apply()} and {@link AbstractDetector#commit()} for each {@code detectors}.
     * @param detectors list of detectors to run
     */
    public static void run(final CPModel model, final AbstractDetector ... detectors){
        associateIndexes(model);
        for(AbstractDetector detector : detectors){
            detector.apply();
            detector.commit();
        }
        resetIndexes(model);
    }

    /**
     * Detect equalities between {@link IntegerVariable} within a model
     * @param m model
     * @return new instance of {@link IntegerVariableEqualitiesDetector}
     */
    public static IntegerVariableEqualitiesDetector intVarEqDet(final CPModel m){
        return new IntegerVariableEqualitiesDetector(m);
    }

    /**
     * Detect equalities between {@link TaskVariable} within a model
     * @param m model
     * @return new instance of {@link TaskVariableEqualitiesDetector}
     */
    public static TaskVariableEqualitiesDetector taskVarEqDet(final CPModel m){
        return new TaskVariableEqualitiesDetector(m);
    }
}
