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
package choco.cp.solver.constraints.global.scheduling;
import junit.framework.Assert;
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class AltDisjunctive extends Disjunctive {

	public AltDisjunctive(final String name, final TaskVar[] taskvars, final IntDomainVar[] usages, final IntDomainVar makespan, CPSolver solver) {
		super(solver, name, taskvars, usages.length, solver.getSchedulerConfiguration().isUsingHypotheticalDomain(), ArrayUtils.append(usages, new IntDomainVar[]{makespan}));
		rules = new AltDisjRules(rtasks, this.makespan, solver.getEnvironment());
	}

	@Override
	public void fireTaskRemoval(IRTask rtask) {
		rules.remove(rtask);
	}
	@Override
	public int getFilteredEventMask(int idx) {
		return idx < taskIntVarOffset || idx >= taskIntVarOffset + getNbOptionalTasks() ? 
				EVENT_MASK : IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
	}
	
	@Override
	public void awakeOnRem(int varIdx, int val) throws ContradictionException {
		//applying second, and first rule
		//To enable updating time window over a resource, if value removed from
		//main domain affects hypothetical domain time window (enables handling domains with gaps).
		if(varIdx < taskIntVarOffset){
			if(varIdx < startOffset){
				//start
				if(rtasks[varIdx].isOptional() && val == rtasks[varIdx].getHTask().getEST()){
					//Hypothetical boundary need to be updated
					if(val < vars[varIdx].getSup()){
						//Intersection exists, apply rule two
						final int newEST = vars[varIdx].getNextDomainValue(val);
						assert newEST > val;
						rtasks[varIdx].setEST(newEST);
					}else{
						//applying rule 1 as no intersection exists.
						rtasks[varIdx].remove();
						rtasks[varIdx].fireRemoval();
					}
				}
			}else if(varIdx < endOffset){
				//end
				if(rtasks[varIdx - startOffset].isOptional() && val == rtasks[varIdx - startOffset].getHTask().getLCT()){
					//Hypothetical boundary need to be updated
					if(val > vars[varIdx].getInf()){
						final int newLCT = vars[varIdx].getPrevDomainValue(val);
						assert newLCT < val;
						rtasks[varIdx - startOffset].setLCT(newLCT);
					}else{
						//applying rule 1 as no intersection exists.
						rtasks[varIdx - startOffset].remove();
						rtasks[varIdx - startOffset].fireRemoval();
					}
				}
			}
		}
		constAwake(false);
	}

	@Override
	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain)
			throws ContradictionException {
		 if (deltaDomain != null) {
	            try {
	                for (; deltaDomain.hasNext();) {
	                    int val = deltaDomain.next();
	                    awakeOnRem(idx, val);
	                }
	            } finally {
	                deltaDomain.dispose();
	            }
	        }
	}

	@Override
	public void propagate() throws ContradictionException {
		//checkHypotheticalDomains();
		super.propagate();
	}
}
