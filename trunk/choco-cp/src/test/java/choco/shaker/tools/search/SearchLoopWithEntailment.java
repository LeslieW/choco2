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

import choco.cp.solver.search.AbstractSearchLoop;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

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
		final Boolean res = searchLoop.endLoop();
		checkEntailment();
		return res;
	}

	@Override
	public void initLoop() {
		searchLoop.initLoop();
	}

	@Override
	public void initSearch() {
		searchLoop.initSearch();
		checkEntailment();
		
	}

	@Override
	public void openNode() {
		searchLoop.openNode();
		checkEntailment();
		
	}

	@Override
	public void restart() {
		searchLoop.restart();
		checkEntailment();
		
	}

	@Override
	public void upBranch() {
		searchLoop.upBranch();
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