package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 2 f�vr. 2007
 * Time: 10:25:57
 * To change this template use File | Settings | File Templates.
 */
public class AbsTest extends TestCase {

    public static void testPropagAbs1() {
        Problem pb = new Problem();

        IntDomainVar x = pb.makeEnumIntVar("x", 2, 5);
        IntDomainVar y = pb.makeEnumIntVar("y", -5, 5);
        pb.post(pb.abs(x , y));
        try {
            pb.propagate();
            y.remVal(3);
            y.remVal(-3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("x " + x.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println(!x.canBeInstantiatedTo(3));
        assertTrue(!x.canBeInstantiatedTo(3));
        assertTrue(!y.canBeInstantiatedTo(0));
        assertTrue(!y.canBeInstantiatedTo(1));
        assertTrue(!y.canBeInstantiatedTo(-1));
    }

	@Test
	public void testEasy() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeEnumIntVar("v0", 0, 10);
		IntDomainVar v1 = pb.makeEnumIntVar("v1", 0, 10);
		IntDomainVar w0 = pb.makeEnumIntVar("w0", -100, 100);
		IntDomainVar absw0 = pb.makeEnumIntVar("absw0", -100, 100);

		pb.post(pb.eq(pb.minus(v0, v1), w0));
		pb.post(pb.abs(absw0, w0));
		pb.post(pb.neq(absw0,0));
		pb.solveAll();
		System.out.println("YO isFeas: " + pb.getSolver().getNbSolutions());
		assertEquals(pb.getSolver().getNbSolutions(), 110);
	}


    public static void testPropagAbs2() {
        Problem pb = new Problem();

        IntDomainVar x = pb.makeEnumIntVar("x", 4, 7);
        IntDomainVar y = pb.makeEnumIntVar("y", -5, 5);
        pb.post(pb.abs(x , y));
        try {
            pb.propagate();
            y.remVal(3);
            y.remVal(-3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("x " + x.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println(!x.canBeInstantiatedTo(3));
        assertTrue(!x.canBeInstantiatedTo(3));
        assertTrue(!y.canBeInstantiatedTo(0));
        assertTrue(!y.canBeInstantiatedTo(1));
        assertTrue(!y.canBeInstantiatedTo(-1));
    }

    public static void testPropagAbs3() {
        Problem pb = new Problem();
        IntDomainVar x = pb.makeEnumIntVar("x", 0, 5);
        IntDomainVar y = pb.makeEnumIntVar("y", -5, 5);
        pb.post(pb.abs(x , y));
        try {
            pb.propagate();
            x.updateSup(2,-1);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("x " + x.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println(!x.canBeInstantiatedTo(3));
        assertTrue(!y.canBeInstantiatedTo(3));
        assertTrue(!y.canBeInstantiatedTo(-3));
        assertTrue(!y.canBeInstantiatedTo(4));
        assertTrue(!y.canBeInstantiatedTo(-4));
        assertTrue(!y.canBeInstantiatedTo(5));
        assertTrue(!y.canBeInstantiatedTo(-5));        
    }

    public static void testPropagAbs4() {
        Problem pb = new Problem();
        IntDomainVar x = pb.makeEnumIntVar("x", 1, 10);
        IntDomainVar y = pb.makeEnumIntVar("y", -10, 10);
        pb.post(pb.abs(x , y));
        try {
            pb.propagate();
            x.updateInf(7,-1);
            //y.updateSup(2,-1);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("x " + x.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        for (int i = 0; i < 6; i++) {
            assertTrue(!y.canBeInstantiatedTo(-i));
            assertTrue(!y.canBeInstantiatedTo(i));
        }
    }

    public void test1() {
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x = pb.makeEnumIntVar("x", 1, 5);
            IntDomainVar y = pb.makeEnumIntVar("y", -5, 5);
            pb.post(pb.abs(x,y));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solve();
            do {
                System.out.println("" + x.getVal() + "=abs(" + y.getVal() + ")");
            } while (pb.nextSolution() == Boolean.TRUE);
            System.out.println("" + pb.getSolver().getSearchSolver().getNodeCount());
            assertEquals(10, pb.getSolver().getNbSolutions());
            //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }

    public void test2() {
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x = pb.makeBoundIntVar("x", 1, 5);
            IntDomainVar y = pb.makeBoundIntVar("y", -5, 5);
            pb.post(pb.abs(x,y));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solve();
            do {
                System.out.println("" + x.getVal() + "=abs(" + y.getVal() + ")");
            } while (pb.nextSolution() == Boolean.TRUE);
            System.out.println("" + pb.getSolver().getSearchSolver().getNodeCount());
            assertEquals(10, pb.getSolver().getNbSolutions());
            //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }

    public void test3() {
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x = pb.makeBoundIntVar("x", 1, 10);
            IntDomainVar y = pb.makeBoundIntVar("y", -2, 10);
            pb.post(pb.abs(x,y));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solve();
            do {
                System.out.println("" + x.getVal() + "=abs(" + y.getVal() + ")");
            } while (pb.nextSolution() == Boolean.TRUE);
            //System.out.println("" + pb.getSolver().getSearchSolver().getNodeCount());
            assertEquals(12, pb.getSolver().getNbSolutions());
            //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }
}
