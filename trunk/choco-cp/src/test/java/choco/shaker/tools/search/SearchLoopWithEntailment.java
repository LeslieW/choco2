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

import choco.cp.solver.search.SearchLoop;
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
public class SearchLoopWithEntailment extends SearchLoop {

        Propagator p;
        public Boolean entail = null;

    public SearchLoopWithEntailment() {
        super(null);
    }

    public SearchLoopWithEntailment(AbstractGlobalSearchStrategy searchStrategy, Propagator p) {
            super(searchStrategy);
            this.p = p;
        }

    @Override
        public void init(){
            super.init();
            checkEntailment();
        }

        @Override
        public void upBranch() {
            super.upBranch();
            checkEntailment();
        }

        @Override
        public void downBranch() {
            super.downBranch();
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