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

import static choco.Choco.geq;
import static choco.Choco.makeIntVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Level;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 1 mai 2008
 * Since : Choco 2.0.0
 *
 */
public class PrecedenceReified extends AbstractTernIntSConstraint {

	// duration of the task
	protected int k1;

	/**
	 * b0 <=> x0 + k1 <= x1
	 */
	public PrecedenceReified(IntDomainVar x0, int k1, IntDomainVar x1, IntDomainVar b) {
		super(b, x0, x1);
		this.k1 = k1;
	}

    @Override
    public int getFilteredEventMask(int idx) {
        if(idx == 0){
            if(v0.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }else if(idx == 1){
            if(v1.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }else{
            if(v2.hasEnumeratedDomain()){
                return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
            }
        }
    }


	public void propagateP1() throws ContradictionException {
		v2.updateInf(v1.getInf() + k1, cIdx2);
		v1.updateSup(v2.getSup() - k1, cIdx1);
	}

	public void propagateP2() throws ContradictionException {
		v2.updateSup(v1.getSup() + k1 - 1, cIdx2);
		v1.updateInf(v2.getInf() - k1 + 1, cIdx1);
	}

	public Boolean isP1Entailed() {
		if (v1.getSup() + k1 <= v2.getInf())
			return Boolean.TRUE;
		if (v1.getInf() + k1 > v2.getSup())
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
			}
		}
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
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

	public void propagate() throws ContradictionException {
		if (v0.isInstantiatedTo(0))
			propagateP2();
		else if (v0.isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
	}

	public boolean isSatisfied() {
		if (v0.isInstantiatedTo(0))
			return v1.getVal() + k1 <= v2.getVal();
		else return v1.getVal() + k1 > v2.getVal();
	}

	public String toString() {
		return "Precedence Reified" + v1+","+ k1+ " - " + v2;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
		  CPModel m = new CPModel();
		  LOGGER.finer("test2");
	      int k1 = 5;
		  IntegerVariable x = makeIntVar("x", 1, 10);
		  IntegerVariable y = makeIntVar("y", 1, 10);
            m.addVariables("cp:bound", x, y);
          IntegerVariable z = makeIntVar("z", 0, 1);

		  m.addConstraint(geq(x,0));
		  m.addConstraint(geq(y,0));
		  m.addConstraint(geq(z,0));


		  CPSolver s = new CPSolver();
		  s.read(m);

		  s.post(new PrecedenceReified(s.getVar(x),k1,s.getVar(y),s.getVar(z)));

		  s.setVarIntSelector(new RandomIntVarSelector(s, i));
		  s.setValIntSelector(new RandomIntValSelector(i + 1));

		  s.solve();
		  do {
		  } while (s.nextSolution() == Boolean.TRUE);
		  if (s.getNbSolutions() != 100) {
			  throw new Error("wrong number of solutions " + s.getNbSolutions());
		  }
		  LOGGER.log(Level.INFO, "Nb solution : {0}" ,  s.getNbSolutions());

		  //assertEquals( s.getNbSolutions(), 14);
		}

	}
}
