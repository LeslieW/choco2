package choco.cp.solver.constraints.global.scheduling;

import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.common.util.IntIterator;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.model.CPModel;
import static choco.Choco.makeIntVar;
import static choco.Choco.geq;
import choco.Choco;

/**
 *  Let b be a boolean variables; x0, x1 be two integer variables and k1, k2 two integers.
 * This constraint enforce x0 before x1 if b is true or x1 before x0 if b is false.
 * b0 = 1 <=> x0 + d0 <= x1
 * b0 = 0 <=> x1 + d1 <= x0
 * */
public class VariablePrecedenceDisjoint extends AbstractLargeIntSConstraint {


    public static IntDomainVar[] makeVars(IntDomainVar b,
                                          IntDomainVar s0, IntDomainVar d0,
                                          IntDomainVar s1, IntDomainVar d1) {
        IntDomainVar[] vars = new IntDomainVar[5];
        vars[0] = b;
        vars[1] = s0;
        vars[2] = d0;
        vars[3] = s1;
        vars[4] = d1;
        return vars;
    }

    /**
     *
     * @param vars
     */
    public VariablePrecedenceDisjoint(IntDomainVar b, IntDomainVar s0, IntDomainVar d0,
                                      IntDomainVar s1, IntDomainVar d1) {
        super(makeVars(b,s0,d0,s1,d1));
    }

    @Override
    public int getFilteredEventMask(int idx) {
        if(idx == 0){
            return IntVarEvent.INSTINTbitvector;
        }else return IntVarEvent.BOUNDSbitvector;
    }

    // propagate x0 + d0 <= x1 (b0 = 1)
	public void propagateP1() throws ContradictionException {
		boolean b = true;
        while(b) {
            b = false;
            b |= vars[3].updateInf(vars[1].getInf() + vars[2].getInf(), cIndices[3]);
            b |= vars[1].updateSup(vars[3].getSup() - vars[2].getInf(), cIndices[1]);
            b |= vars[2].updateSup(vars[3].getSup() - vars[1].getInf(), cIndices[2]);
        }
    }

    // propagate x1 + d1 <= x0 (b0 = 0)
    public void propagateP2() throws ContradictionException {
		boolean b = true;
        while(b) {
            b = false;
            vars[1].updateInf(vars[3].getInf() + vars[4].getInf(), cIndices[1]);
            vars[3].updateSup(vars[1].getSup() - vars[4].getInf(), cIndices[3]);
            vars[4].updateSup(vars[1].getSup() - vars[3].getInf(), cIndices[4]);
        }
    }

	public Boolean isP1Entailed() {
		if (vars[1].getSup() + vars[2].getSup() <= vars[3].getInf())
			return Boolean.TRUE;
		if (vars[1].getInf() + vars[2].getInf() > vars[3].getSup())
			return Boolean.FALSE;
		return null;
	}

	public Boolean isP2Entailed() {
		if (vars[3].getSup() + vars[4].getSup() <= vars[1].getInf())
			return Boolean.TRUE;
		if (vars[3].getInf() + vars[4].getInf() > vars[1].getSup())
			return Boolean.FALSE;
		return null;
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) {        // booleen de decision
			int val = vars[0].getVal();
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
				vars[0].instantiate(1, cIndices[0]);
			} else {
				vars[0].instantiate(0, cIndices[0]);
                propagateP2();
			}
		}
		b = isP2Entailed();
		if (b != null) {
			if (b) {
				vars[0].instantiate(0, cIndices[0]);
			} else {
				vars[0].instantiate(1, cIndices[0]);
                propagateP1();
			}
        }
	}

    public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
        if (vars[0].isInstantiatedTo(0))
            propagateP2();
        else if (vars[0].isInstantiatedTo(1))
            propagateP1();
        else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
    }


	public void awakeOnSup(int idx) throws ContradictionException {
		if (vars[0].isInstantiatedTo(0))
			propagateP2();
		else if (vars[0].isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		if (vars[0].isInstantiatedTo(0))
			propagateP2();
		else if (vars[0].isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
	}

    public void awakeOnBounds(int idx) throws ContradictionException {
        if (vars[0].isInstantiatedTo(0))
			propagateP2();
		else if (vars[0].isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
    }

    public void propagate() throws ContradictionException {
		if (vars[0].isInstantiatedTo(0))
			propagateP2();
		else if (vars[0].isInstantiatedTo(1))
			propagateP1();
		else filterOnP1P2TowardsB(); //idx ne peut pas valoir 0 ici
	}

	public boolean isSatisfied() {
		if (vars[0].isInstantiatedTo(1))
			return vars[1].getVal() + vars[2].getVal() <= vars[3].getVal();
		else return vars[3].getVal() + vars[4].getVal() <= vars[1].getVal();
	}

	public String toString() {
		return "VDisjunction " + vars[1] +","+ vars[2]+ " - " + vars[3] + "," + vars[4];
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
		  CPModel m = new CPModel();
	      IntegerVariable k1 = makeIntVar("dx",2,7);
          IntegerVariable k2 = makeIntVar("dy",2,7);
          IntegerVariable x = makeIntVar("x", 1, 10);
		  IntegerVariable y = makeIntVar("y", 1, 10);
          IntegerVariable z = makeIntVar("z", 0, 1);
          m.addVariables("cp:bound", x, y, k1, k2, z);

//          m.addConstraints(Choco.implies(Choco.eq(z,1),Choco.leq(Choco.plus(x,k1),y)));
//          m.addConstraints(Choco.implies(Choco.eq(z,0),Choco.leq(Choco.plus(y,k2),x)));


          CPSolver s = new CPSolver();
          s.read(m);

		  s.post(new VariablePrecedenceDisjoint(s.getVar(z),s.getVar(x),s.getVar(k1),
                                                s.getVar(y),s.getVar(k2)));

		  s.setVarIntSelector(new RandomIntVarSelector(s, i));
		  s.setValIntSelector(new RandomIntValSelector(i + 1));

		  s.solve();
		  do {
		    //System.out.println(s.getVar(z).getVal() + " " + s.getVar(x).getVal() + " " + s.getVar(y).getVal());

          } while (s.nextSolution() == Boolean.TRUE);

           if (s.getNbSolutions() != 1392) {
			  throw new Error("wrong number of solutions " + s.getNbSolutions());
		  }
		  System.out.println("Nb solution : " + s.getNbSolutions() + " " + s.getNodeCount());

		  //assertEquals( s.getNbSolutions(), 14);
		}

	}
}
