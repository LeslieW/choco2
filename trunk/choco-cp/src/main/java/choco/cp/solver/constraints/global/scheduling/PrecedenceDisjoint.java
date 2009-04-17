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

import choco.Choco;
import static choco.Choco.geq;
import static choco.Choco.makeIntVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.IntIterator;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Level;

/**
 *
 * Let b be a boolean variables; x0, x1 be two integer variables and k1, k2 two integers.
 * This constraint enforce x0 before x1 if b is true or x1 before x0 if b is false.
 * b0 = 1 <=> x0 + k1 <= x1
 * b0 = 0 <=> x1 + k2 <= x0
 **/
public class PrecedenceDisjoint extends AbstractTernIntSConstraint {

	// duration of the two tasks
	protected int k1, k2;

	/**
	 * b0 = 1 <=> x0 + k1 <= x1
	 * b0 = 0 <=> x1 + k2 <= x0
	 */
	public PrecedenceDisjoint(IntDomainVar x0, int k1, IntDomainVar x1, int k2, IntDomainVar b) {
		super(b, x0, x1);
		this.k1 = k1;
		this.k2 = k2;
	}

    @Override
    public int getFilteredEventMask(int idx) {
        if(idx == 0){
            return IntVarEvent.INSTINTbitvector;
        }else return IntVarEvent.BOUNDSbitvector;
    }


	public void propagateP1() throws ContradictionException {
		v2.updateInf(v1.getInf() + k1, cIdx2);
		v1.updateSup(v2.getSup() - k1, cIdx1);
	}

	public void propagateP2() throws ContradictionException {
		v1.updateInf(v2.getInf() + k2, cIdx1);
		v2.updateSup(v1.getSup() - k2, cIdx2);
	}

	public Boolean isP1Entailed() {
		if (v1.getSup() + k1 <= v2.getInf())
			return Boolean.TRUE;
		if (v1.getInf() + k1 > v2.getSup())
			return Boolean.FALSE;
		return null;
	}

	public Boolean isP2Entailed() {
		if (v2.getSup() + k2 <= v1.getInf())
			return Boolean.TRUE;
		if (v2.getInf() + k2 > v1.getSup())
			return Boolean.FALSE;
		return null;
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) {        // booleen de decision
			int val = v0.getVal();
			if (val == 1) {
				propagateP1();
			} else {
				propagateP2();
			}
		} else {
			filterOnP1P2TowardsB();
		}
	}

	//idx = 1 ou idx = 2
	public void filterOnP1P2TowardsB() throws ContradictionException {
		Boolean b = isP1Entailed();
		if (b != null) {
			if (b) {
				v0.instantiate(1, cIdx0);
			} else {
				v0.instantiate(0, cIdx0);
                propagateP2();
			}
		}
		b = isP2Entailed();
		if (b != null) {
			if (b) {
				v0.instantiate(0, cIdx0);
			} else {
				v0.instantiate(1, cIdx0);
                propagateP1();
			}
        }
	}

    public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
        if (v0.isInstantiatedTo(0))
            propagateP2();
        else if (v0.isInstantiatedTo(1))
            propagateP1();
        else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
    }


	public void awakeOnSup(int idx) throws ContradictionException {
		if (v0.isInstantiatedTo(0))
			propagateP2();
		else if (v0.isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		if (v0.isInstantiatedTo(0))
			propagateP2();
		else if (v0.isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
	}

    public void awakeOnBounds(int idx) throws ContradictionException {
        if (v0.isInstantiatedTo(0))
			propagateP2();
		else if (v0.isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
    }

    public void propagate() throws ContradictionException {
		if (v0.isInstantiatedTo(0))
			propagateP2();
		else if (v0.isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici		
	}

	public boolean isSatisfied() {
		if (v0.isInstantiatedTo(1))
			return v1.getVal() + k1 <= v2.getVal();
		else return v2.getVal() + k2 <= v1.getVal();
	}

	public String toString() {
		return "Precedence " + v1+","+ k1+ " - " + v2 + "," + k2;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1; i++) {
		  CPModel m = new CPModel();
		  LOGGER.finer("test2");
	      int k1 = 5, k2 = 5;
		  IntegerVariable x = makeIntVar("x", 1, 10);
		  IntegerVariable y = makeIntVar("y", 1, 10);
          m.addVariables("cp:bound", x, y);
          IntegerVariable z = makeIntVar("z", 0, 1);

		  m.addConstraint(geq(x,0));
		  m.addConstraint(geq(y,0));
		  m.addConstraint(geq(z,0));	

          m.addConstraint(Choco.preceding(x,k1,y,k2,z));
		  CPSolver s = new CPSolver();
		  s.read(m);

		  //s.post(new PrecedenceDisjoint(s.getVar(x),k1,s.getVar(y),k2,s.getVar(z)));

		  s.setVarIntSelector(new RandomIntVarSelector(s, i));
		  s.setValIntSelector(new RandomIntValSelector(i + 1));

		  s.solve();
		  do {
		  } while (s.nextSolution() == Boolean.TRUE);
		  if (s.getNbSolutions() != 30) {
			  throw new Error("wrong number of solutions " + s.getNbSolutions());
		  }
		  LOGGER.log(Level.INFO, "Nb solution : {0} {1}", new Object[]{s.getNbSolutions(), s.getNodeCount()});

		  //assertEquals( s.getNbSolutions(), 14);
		}

	}
}
