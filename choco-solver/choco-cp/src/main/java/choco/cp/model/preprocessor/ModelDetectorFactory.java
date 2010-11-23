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
import choco.cp.common.util.preprocessor.detector.scheduling.DisjFromCumulModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.DisjointFromCumulModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.DisjointFromDisjModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.DisjointModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.DisjunctiveModel;
import choco.cp.common.util.preprocessor.detector.scheduling.PrecFromDisjointModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.PrecFromImpliedModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.PrecFromReifiedModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.PrecFromTimeWindowModelDetector;
import choco.cp.common.util.preprocessor.detector.scheduling.RmDisjModelDetector;
import choco.cp.model.CPModel;
import choco.kernel.common.util.tools.ArrayUtils;
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

	/**
	 * Detect disjunctive from cumulative (redundant constraint).
	 * @param m model to analyze
	 */
	public static AbstractDetector disjFromCumulDetector(final CPModel m){
		return new DisjFromCumulModelDetector(m);
	}

	/**
	 * 
	 * @param m model to analyze
	 */
	public static AbstractDetector precFromImpliedDetector(final CPModel m, DisjunctiveModel disjMod){
		return new PrecFromImpliedModelDetector(m, disjMod);
	}

	/**
	 * 
	 * @param m model to analyze
	 */
	public static AbstractDetector precFromReifiedDetector(final CPModel m, DisjunctiveModel disjMod){
		return new PrecFromReifiedModelDetector(m, disjMod);
	}

	/**
	 * 
	 * @param m model to analyze
	 */
	public static AbstractDetector precFromTimeWindowDetector(final CPModel m, DisjunctiveModel disjMod){
		return new PrecFromTimeWindowModelDetector(m, disjMod);
	}

	/**
	 * 
	 * @param m model to analyze
	 */
	public static AbstractDetector precFromDisjointDetector(final CPModel m, DisjunctiveModel disjMod){
		return new PrecFromDisjointModelDetector(m, disjMod);
	}


	/**
	 * 
	 * @param m model to analyze
	 */
	public static AbstractDetector disjointDetector(final CPModel m, DisjunctiveModel disjMod){
		return new DisjointModelDetector(m, disjMod);
	}

	public static AbstractDetector disjointFromDisjDetector(final CPModel m, DisjunctiveModel disjMod){
		return new DisjointFromDisjModelDetector(m, disjMod);
	}

	public static AbstractDetector disjointFromCumulDetector(final CPModel m, DisjunctiveModel disjMod){
		return new DisjointFromCumulModelDetector(m, disjMod);
	}
	
	/**
	 * 
	 * @param m model to analyze
	 */
	public static AbstractDetector rmDisjDetector(final CPModel m){
		return new RmDisjModelDetector(m);
	}


	public static AbstractDetector[] disjunctiveModelDetectors(final CPModel m, final DisjunctiveModel disjMod) {
		return new AbstractDetector[] {
				precFromImpliedDetector(m, disjMod),
				precFromReifiedDetector(m, disjMod),
				precFromDisjointDetector(m, disjMod),
				disjointDetector(m, disjMod),
				disjointFromDisjDetector(m, disjMod),
				disjointFromCumulDetector(m, disjMod)
		};
	}

	public static AbstractDetector[] schedulingModelDetectors(final CPModel m, final DisjunctiveModel disjMod) {
		return ArrayUtils.append( new AbstractDetector[] {
				precFromTimeWindowDetector(m, disjMod),
				disjFromCumulDetector(m)},
				disjunctiveModelDetectors(m, disjMod),
				new AbstractDetector[] {rmDisjDetector(m)}) ;
	}
}
