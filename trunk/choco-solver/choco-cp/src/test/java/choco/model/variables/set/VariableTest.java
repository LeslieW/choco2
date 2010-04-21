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
package choco.model.variables.set;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.set.SetVar;
import junit.framework.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.logging.Logger;

public class VariableTest {
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    @Test
    public void test1() {
		LOGGER.finer("test1");
		CPSolver s = new CPSolver();
		SetVar x = s.createBoundSetVar("X", 1, 5);
		try {
			x.addToKernel(2, null, true);
			x.addToKernel(4, null, true);
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		DisposableIntIterator it = x.getDomain().getOpenDomainIterator();
		while (it.hasNext()) {
			int val = it.next();
			LOGGER.info("" + val);
			assertTrue(val != 2);
			assertTrue(val != 4);
		}
	}

    @Test
    public void test2() {
		CPSolver s = new CPSolver();
		SetVar set = s.createBoundSetVar("X", 1, 5);
		boolean bool = true;
		LOGGER.info("" + set.pretty());
		for (DisposableIntIterator it0 = set.getDomain().getEnveloppeIterator();
		     it0.hasNext();) {
			int x = it0.next();
			bool = !bool;
			try {
				if (bool) {
					set.remFromEnveloppe(x, null, false);
				}
			} catch (ContradictionException e) {

			}
		}
		LOGGER.info("" + set.pretty());
		assertTrue(!set.isInDomainKernel(2));
		assertTrue(!set.isInDomainKernel(4));
	}

    @Test
    public void test3(){
        Model m = new CPModel();
        CPSolver s = new CPSolver();

        SetVariable sv1 = makeSetVar("s1", 0, 1);
        IntegerVariable card = sv1.getCard();
        m.addConstraint(eq(card, 1));
        m.addConstraint(eqCard(sv1, 1));
        s.read(m);
        s.solve();
        do{
            LOGGER.info(s.getVar(sv1).pretty());
            LOGGER.info("---------------------");
        }while(s.nextSolution());
        Assert.assertEquals("nb solution", 2, s.getNbSolutions());
    }


    @Test
    public void testEmptySet(){
        Model m1 = new CPModel();
        Solver so1 = new CPSolver();
        Model m2 = new CPModel();
        Solver so2 = new CPSolver();

        SetVariable s1 = makeSetVar("s1", 0, 2);
        SetVariable s2 = makeSetVar("s2", 0, 2);
        SetVariable empty = constant(new int[0]);

        m1.addConstraint(setInter(s1, s2, empty));

        m2.addConstraint(setDisjoint(s1, s2));

        so1.read(m1);
        so1.solveAll();
        so2.read(m2);
        so2.solveAll();
        Assert.assertEquals("Not same number of solutions", so1.getNbSolutions(), so2.getNbSolutions());
    }

    @Test
    public void testConstantSet(){
        Model m1 = new CPModel();
        Solver so1 = new CPSolver();
        Model m2 = new CPModel();
        Solver so2 = new CPSolver();

        SetVariable s1 = makeSetVar("s1", 0, 2);
        SetVariable s2 = makeSetVar("s2", 1, 1);
        SetVariable one = constant(new int[1]);

        m1.addConstraint(isIncluded(one, s1));

        m2.addConstraint(isIncluded(s2, s1));
        m2.addConstraint(eqCard(s2, 1));


        so1.read(m1);
        so1.solveAll();
        so2.read(m2);
        so2.solveAll();
        Assert.assertEquals("Not same number of solutions", so1.getNbSolutions(), so2.getNbSolutions());
    }

    @Test
    public void test4(){
        Solver so1 = new CPSolver();
        Model m1 = new CPModel();
        SetVariable s1 = Choco.emptySet();
        SetVariable s2 = Choco.emptySet();

        m1.addConstraint(eq(s1, s2));

        so1.read(m1);
        so1.solveAll();
    }

    @Test
    public void testDESSORT() {
        Model m = new CPModel();
        Solver s = new CPSolver();
        int n = 20;
        int nMatch = (n * (n - 1)) / 2;

        IntegerVariable[] r = new IntegerVariable[nMatch];
        IntegerVariable[] i = new IntegerVariable[nMatch];
        IntegerVariable[] occR = new IntegerVariable[n];
        IntegerVariable[] occI = new IntegerVariable[n];
        SetVariable[] match = new SetVariable[nMatch];

        for (int k = 0; k < nMatch; k++) {
            r[k] = makeIntVar("r" + k, 1, n, "cp:enum");
            i[k] = makeIntVar("i" + k, 1, n, "cp:enum");
            match[k] = makeSetVar("match" + k, 1, n);
        }
        for (int k = 0; k < n; k++) {
            occR[k] = makeIntVar("occR" + k, (n - 1) / 2, n / 2, "cp:bound");
            occI[k] = makeIntVar("occI" + k, (n - 1) / 2, n / 2, "cp:bound");
        }

        for (int k = 0; k < nMatch; k++) {
            m.addConstraint(neq(r[k], i[k]));
            m.addConstraint(eqCard(match[k], 2));
            m.addConstraint(member(r[k], match[k]));
            m.addConstraint(member(i[k], match[k]));
        }
        for (int k1 = 0; k1 < nMatch - 1; k1++) {
            for (int k2 = k1 + 1; k2 < nMatch; k2++) {
                m.addConstraint(isNotIncluded(match[k1], match[k2]));
            }
        }

        for (int j = 0; j < n; j++) {
            m.addConstraint(occurrence(j+1, occR[j], r));
            m.addConstraint(occurrence(j+1, occI[j], i));
            m.addConstraint(eq(sum(occR[j], occI[j]), n - 1));
        }
        s.read(m);
//        CPSolver.setVerbosity(CPSolver.SOLUTION);
//        ChocoLogging.setVerbosity(Verbosity.VERBOSE);
        s.solve();
//        CPSolver.flushLogs();
        System.out.println(s.isFeasible());
        for (int k = 0; k < nMatch; k++) {
            System.out.println(s.getVar(match[k]));
        }
        for (int k = 0; k < nMatch; k++) {
            System.out.println(s.getVar(r[k]).toString() + "-" + s.getVar(i[k]).toString());
        }


    }

    

}

