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
package choco.shaker.tools.search;

import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.AbstractSearchLoop;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 11 mars 2009
 * Since : Choco 2.0.1
 * Update : Choco 2.0.1
 * 
 * SearchLoop with entailment checker
 */
public class SearchLoopWithEntailment extends AbstractSearchLoop {

	Propagator p;
	
	public Boolean entail = null;

	public final AbstractSearchLoop searchLoop;

    private int previousNbSolutions;

	public SearchLoopWithEntailment(AbstractGlobalSearchStrategy searchStrategy, Propagator propagator) {
		super(searchStrategy);
		this.searchLoop = (AbstractSearchLoop) searchStrategy.searchLoop;
		this.p = propagator;
	}

	
	
	@Override
	public void downBranch() {
		searchLoop.downBranch();
		checkEntailment();
		
	}

	@Override
	public Boolean endLoop() {
		return searchLoop.endLoop();
	}

	@Override
	public void initLoop() {
		searchLoop.initLoop();
        checkEntailment();
	}

	@Override
	public void initSearch() {
		searchLoop.initSearch();
	}

	@Override
	public void openNode() {
		searchLoop.openNode();
        if(searchStrategy.getSolutionCount() > previousNbSolutions) {
            previousNbSolutions++;
            stop = true;
		}
	}

	@Override
	public void restart() {
		searchLoop.restart();
//		checkEntailment();
	}

	@Override
	public void upBranch() {
		searchLoop.upBranch();
        if(searchStrategy.isTraceEmpty()){
            stop = true;
        }
		checkEntailment();
		
	}

	private void checkEntailment(){
		if(p.isActive()){
			entail = p.isEntailed();
			if(entail!=null){
				p.setPassive();
			}
		}
	}
}