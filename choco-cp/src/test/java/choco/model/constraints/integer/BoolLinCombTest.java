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
package choco.model.constraints.integer;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.IntLinComb;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 31 oct. 2006
 * Time: 15:07:46
 */
public class BoolLinCombTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    CPModel m;
    CPSolver s;

    @Before
    public void before(){
        m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        s = new CPSolver();
    }

    @Test
    public void test0() {
        IntegerVariable[] vars = makeIntVarArray("x", 2, 0, 1);
        m.addConstraint(leq(sum(new IntegerVariable[]{vars[0],vars[1]}),1));
        m.addConstraint(eq(vars[1],1));
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

    @Test
    public void test0bis() {
        IntegerVariable[] vars = makeIntVarArray("x", 2, 0, 1);
        m.addConstraint(eq(sum(new IntegerVariable[]{vars[0],vars[1]}),1));
        m.addConstraint(eq(vars[1],1));
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

    @Test
    public void test00() {
        IntegerVariable[] vars = makeIntVarArray("x", 2, 0, 1);
		IntegerVariable sum = makeIntVar("s", 0, 5);
        Constraint prop = leq(scalar(new int[]{3,1}, new IntegerVariable[]{vars[0],vars[1]}),sum);
		m.addConstraint(prop);
        s.read(m);
        try {
            s.propagate();
            s.getVar(vars[0]).setVal(1);
	        //((Propagator) s.getCstr(prop)).constAwake(false);
          for(Iterator<SConstraint> iter = s.getIntConstraintIterator(); iter.hasNext(); ) {
            ((Propagator)iter.next()).constAwake(false);
          }
          s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

    @Test
    public void test01() {
        IntegerVariable[] vars = makeIntVarArray("x", 2, 0, 1);
		IntegerVariable sum = makeIntVar("s", 0, 5);
        Constraint prop = leq(scalar(new int[]{3,1}, new IntegerVariable[]{vars[0],vars[1]}),sum);
        try {
	        m.addConstraint(prop);
            s.read(m);
            s.getVar(vars[0]).setVal(1);
	        s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

    @Test
    public void test1() {
        LOGGER.info("Test EQ  **************************** ");
        for (int i = 1; i < 16; i++) {
            testBothLinCombVer(i, IntLinComb.EQ, 100);
        }
    }

    @Test
    public void test2() {
        LOGGER.info("Test GT  **************************** ");
        for (int i = 1; i < 10; i++) {
            testBothLinCombVer(i, IntLinComb.GEQ, 101);
        }
    }

    @Test
    public void test3() {
        LOGGER.info("Test LEQ  **************************** ");
        for (int i = 1; i < 13; i++) {
            testBothLinCombVer(i, IntLinComb.LEQ, 102);
        }
    }

	@Test
	public void test1S() {
	    LOGGER.info("Test EQ  **************************** ");
	    for (int i = 1; i < 16; i++) {
	        testBothSumCombVer(i, IntLinComb.EQ, 100);
	    }
	}

	@Test
	public void test2S() {
	    LOGGER.info("Test GT  **************************** ");
	    for (int i = 1; i < 10; i++) {
	        testBothSumCombVer(i, IntLinComb.GEQ, 101);
	    }
	}

	@Test
	public void test3S() {
	    LOGGER.info("Test LEQ  **************************** ");
	    for (int i = 1; i < 13; i++) {
	        testBothSumCombVer(i, IntLinComb.LEQ, 102);
	    }
	}


    private void testBothLinCombVer(int n, int op, int seed) {
        testLinComb(n, op, false, seed);
        int nbSol1 = nbSol;
        int nbNodes1 = nbNodes;
        testLinComb(n, op, true, seed);
        assertEquals(nbSol1, nbSol);
        assertEquals(nbNodes1, nbNodes);
    }

	private void testBothSumCombVer(int n, int op, int seed) {
	    testSumComb(n, op, false, seed);
	    int nbSol1 = nbSol;
	    int nbNodes1 = nbNodes;
	    testSumComb(n, op, true, seed);
	    assertEquals(nbSol1, nbSol);
	    assertEquals(nbNodes1, nbNodes);
	}


    public int nbSol;
    public int nbNodes;

    private void testLinComb(int n, int op, boolean optimized, int seed) {
	    m = new CPModel();
	    s = new CPSolver();
	    IntegerVariable[] vars = new IntegerVariable[n + 1];
        for (int i = 0; i < n; i++) {
            vars[i] = makeIntVar("v" + i, 0, 1);
        }
        vars[n] = makeIntVar("b", -n - 1, n + 1);
        m.addVariables("cp:bound",vars);
        m.addVariables(vars);

        int[] randCoefs = getRandomPackingPb(n + 1, 100, seed + 1);
        Random rand = new Random(seed + 2);
        int k = rand.nextInt(2 * n + 5) - n - 5;
        if (optimized) {
            if (op == IntLinComb.GEQ) {
				m.addConstraint("cp:decomp", geq(scalar(vars, randCoefs), k));
			} else if (op == IntLinComb.EQ) {
                m.addConstraint("cp:decomp", eq(scalar(vars, randCoefs), k));
            } else if (op == IntLinComb.LEQ) {
                m.addConstraint("cp:decomp", leq(scalar(vars, randCoefs), k));
            }
          s.read(m);
        } else {
          s.read(m);
            if (op == IntLinComb.GEQ) {
				s.post(makeIntLinComb(s.getVar(vars),randCoefs,-k,IntLinComb.GEQ));
			} else if (op == IntLinComb.EQ) {
                s.post(makeIntLinComb(s.getVar(vars),randCoefs,-k,IntLinComb.EQ));
            } else if (op == IntLinComb.LEQ) {
                ArrayUtils.inverseSign(randCoefs);
                s.post(makeIntLinComb(s.getVar(vars),randCoefs,k,IntLinComb.GEQ));
            }
        }
        s.setVarIntSelector(new RandomIntVarSelector(s, s.getVar(vars), seed + 3));
        s.setValIntSelector(new RandomIntValSelector(seed + 4));
        s.solveAll();
        nbNodes = s.getSearchStrategy().getNodeCount();
        nbSol = s.getNbSolutions();
        LOGGER.info("n:" + n + " op:" + op + " ver:" + optimized + " nbSol " + nbSol + " nbNode " + nbNodes + " tps " + s.getSearchStrategy().getTimeCount());
    }

	private void testSumComb(int n, int op, boolean optimized, int seed) {
		  m = new CPModel();
		  s = new CPSolver();
		  IntegerVariable[] vars = new IntegerVariable[n];
	      for (int i = 0; i < n; i++) {
	          vars[i] = makeIntVar("v" + i, 0, 1);
	      }
	      m.addVariables(vars);
	      Random rand = new Random(seed + 2);
	      int k = rand.nextInt(n);
	      if (optimized) {
	          if (op == IntLinComb.GEQ) {
				m.addConstraint("cp:decomp", geq(sum(vars), k));
			} else if (op == IntLinComb.EQ) {
	              m.addConstraint("cp:decomp", eq(sum(vars), k));
	          } else if (op == IntLinComb.LEQ) {
	              m.addConstraint("cp:decomp", leq(sum(vars), k));
	          }
          s.read(m);
        } else {
          s.read(m);
          int[] sumcoef = new int[n];
		      for (int i = 0; i < sumcoef.length; i++) {
			      sumcoef[i] = 1;
		      }
	          if (op == IntLinComb.GEQ) {
				s.post(makeIntLinComb(s.getVar(vars),sumcoef,-k,IntLinComb.GEQ));
			} else if (op == IntLinComb.EQ) {
	              s.post(makeIntLinComb(s.getVar(vars),sumcoef,-k,IntLinComb.EQ));
	          } else if (op == IntLinComb.LEQ) {
	              ArrayUtils.inverseSign(sumcoef);
	              s.post(makeIntLinComb(s.getVar(vars),sumcoef,k,IntLinComb.GEQ));
	          }
	      }
	      s.setVarIntSelector(new RandomIntVarSelector(s, s.getVar(vars), seed + 3));
	      s.setValIntSelector(new RandomIntValSelector(seed + 4));
	      s.solveAll();
	      nbNodes = s.getSearchStrategy().getNodeCount();
	      nbSol = s.getNbSolutions();
	      LOGGER.info("n:" + n + " op:" + op + " ver:" + optimized + " nbSol " + nbSol + " nbNode " + nbNodes + " tps " + s.getSearchStrategy().getTimeCount());
	  }



    private static int[] getRandomPackingPb(int nbObj, int capa, int seed) {
        Random rand = new Random(seed);
        int[] instance = new int[nbObj];
        for (int i = 0; i < nbObj; i++) {
            int val = rand.nextInt(2 * capa) - capa;
            instance[i] = val;
        }
        return instance;
    }

    /**
     * Copy of api to state the old non optimized linear constraint for comparison
     */
    private SConstraint makeIntLinComb(IntVar[] lvars, int[] lcoeffs, int c, int linOperator) {
        int nbNonNullCoeffs = countNonNullCoeffs(lcoeffs);
        int nbPositiveCoeffs = 0;
        int[] sortedCoeffs = new int[nbNonNullCoeffs];
        IntVar[] sortedVars = new IntVar[nbNonNullCoeffs];

        int j = 0;
        // fill it up with the coefficients and variables in the right order
        for (int i = 0; i < lvars.length; i++) {
            if (lcoeffs[i] > 0) {
                sortedVars[j] = lvars[i];
                sortedCoeffs[j] = lcoeffs[i];
                j++;
            }
        }
        nbPositiveCoeffs = j;

        for (int i = 0; i < lvars.length; i++) {
            if (lcoeffs[i] < 0) {
                sortedVars[j] = lvars[i];
                sortedCoeffs[j] = lcoeffs[i];
                j++;
            }
        }
        IntDomainVar[] tmpVars = new IntDomainVar[sortedVars.length];
        System.arraycopy(sortedVars, 0, tmpVars, 0, sortedVars.length);

        return new IntLinComb(tmpVars, sortedCoeffs, nbPositiveCoeffs, c, linOperator);
    }

    private static int countNonNullCoeffs(int[] lcoeffs) {
        int nbNonNull = 0;
        for (int i = 0; i < lcoeffs.length; i++) {
            if (lcoeffs[i] != 0) {
				nbNonNull++;
			}
        }
        return nbNonNull;
    }
}
