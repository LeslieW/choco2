package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.constraints.IntLinComb;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import i_want_to_use_this_old_version_of_choco.util.UtilAlgo;
import junit.framework.TestCase;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 31 oct. 2006
 * Time: 15:07:46
 * To change this template use File | Settings | File Templates.
 */
public class BoolLinCombTest extends TestCase {

    public void test0() {
        Problem p = new Problem();
        IntDomainVar[] vars = p.makeEnumIntVarArray("x", 2, 0, 1);
        p.post(p.leq(p.sum(new IntDomainVar[]{vars[0],vars[1]}),1));
        p.post(p.eq(vars[1],1));
        try {
            p.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

    public void test0bis() {
        Problem p = new Problem();
        IntDomainVar[] vars = p.makeEnumIntVarArray("x", 2, 0, 1);
        p.post(p.eq(p.sum(new IntDomainVar[]{vars[0],vars[1]}),1));
        p.post(p.eq(vars[1],1));
        try {
            p.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

	public void test00() {
        Problem p = new Problem();
        IntDomainVar[] vars = p.makeEnumIntVarArray("x", 2, 0, 1);
		IntDomainVar sum = p.makeEnumIntVar("s", 0, 5);
        Constraint prop = p.leq(p.scalar(new int[]{3,1}, new IntDomainVar[]{vars[0],vars[1]}),sum);
		p.post(prop);
        try {
            p.propagate();
            vars[0].setVal(1);
	        ((Propagator) prop).constAwake(false);
	        p.propagate();	        
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

	public void test01() {
        Problem p = new Problem();
        IntDomainVar[] vars = p.makeEnumIntVarArray("x", 2, 0, 1);
		IntDomainVar sum = p.makeEnumIntVar("s", 0, 5);
        Constraint prop = p.leq(p.scalar(new int[]{3,1}, new IntDomainVar[]{vars[0],vars[1]}),sum);
        try {
	        p.post(prop);            
	        vars[0].setVal(1);
	        p.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

    public void test1() {
        System.out.println("Test EQ  **************************** ");
        for (int i = 1; i < 16; i++) {
            testBothLinCombVer(i, IntLinComb.EQ, 100);
        }
    }

    public void test2() {
        System.out.println("Test GT  **************************** ");
        for (int i = 1; i < 10; i++) {
            testBothLinCombVer(i, IntLinComb.GEQ, 101);
        }
    }

    public void test3() {
        System.out.println("Test LEQ  **************************** ");
        for (int i = 1; i < 13; i++) {
            testBothLinCombVer(i, IntLinComb.LEQ, 102);
        }
    }

    public void testBothLinCombVer(int n, int op, int seed) {
        testLinComb(n, op, false, seed);
        int nbSol1 = nbSol;
        int nbNodes1 = nbNodes;
        testLinComb(n, op, true, seed);
        assertEquals(nbSol1, nbSol);
        assertEquals(nbNodes1, nbNodes);
    }

    public int nbSol;
    public int nbNodes;

    public void testLinComb(int n, int op, boolean optimized, int seed) {
        Problem pb = new Problem();
        IntDomainVar[] vars = new IntDomainVar[n + 1];
        for (int i = 0; i < n; i++) {
            vars[i] = pb.makeEnumIntVar("v" + i, 0, 1);
        }
        vars[n] = pb.makeBoundIntVar("b", -n - 1, n + 1);
        int[] randCoefs = getRandomPackingPb(n + 1, 100, seed + 1);
        Random rand = new Random(seed + 2);
        int k = rand.nextInt(2 * n + 5) - n - 5;
        if (optimized) {
            if (op == IntLinComb.GEQ)
                pb.post(pb.geq(pb.scalar(vars, randCoefs), k));
            else if (op == IntLinComb.EQ) {
                pb.post(pb.eq(pb.scalar(vars, randCoefs), k));
            } else if (op == IntLinComb.LEQ) {
                pb.post(pb.leq(pb.scalar(vars, randCoefs), k));
            }
        } else {
            if (op == IntLinComb.GEQ)
                pb.post(makeIntLinComb(vars,randCoefs,-k,IntLinComb.GEQ));
            else if (op == IntLinComb.EQ) {
                pb.post(makeIntLinComb(vars,randCoefs,-k,IntLinComb.EQ));
            } else if (op == IntLinComb.LEQ) {
                UtilAlgo.inverseSign(randCoefs);
                pb.post(makeIntLinComb(vars,randCoefs,k,IntLinComb.GEQ));
            }
        }
        pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, vars, seed + 3));
        pb.getSolver().setValSelector(new RandomIntValSelector(seed + 4));
        pb.solveAll();
        nbNodes = pb.getSolver().getSearchSolver().getNodeCount();
        nbSol = pb.getSolver().getNbSolutions();
        System.out.println("n:" + n + " op:" + op + " ver:" + optimized + " nbSol " + nbSol + " nbNode " + nbNodes + " tps " + pb.getSolver().getSearchSolver().getTimeCount());
    }

    public static int[] getRandomPackingPb(int nbObj, int capa, int seed) {
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
    protected Constraint makeIntLinComb(IntVar[] lvars, int[] lcoeffs, int c, int linOperator) {
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

    public static int countNonNullCoeffs(int[] lcoeffs) {
        int nbNonNull = 0;
        for (int i = 0; i < lcoeffs.length; i++) {
            if (lcoeffs[i] != 0)
                nbNonNull++;
        }
        return nbNonNull;
    }
}
