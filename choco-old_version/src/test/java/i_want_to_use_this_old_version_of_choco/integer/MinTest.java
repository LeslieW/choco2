package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 30 janv. 2007
 * Time: 10:49:34
 * To change this template use File | Settings | File Templates.
 */
public class MinTest extends TestCase {

    public void test1() {
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x = pb.makeEnumIntVar("x", 1, 5);
            IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
            IntDomainVar z = pb.makeEnumIntVar("z", 1, 5);
            IntDomainVar w = pb.makeEnumIntVar("z", 1, 5);
            pb.post(pb.min(new IntDomainVar[]{x, y, z},w));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solve();
            do {
                /*System.out.println("" + x.getVal() + "=max(" + y.getVal() + "," +
                z.getVal()+")");*/
            } while (pb.nextSolution() == Boolean.TRUE);
            System.out.println("" + pb.getSolver().getSearchSolver().getNodeCount());
            assertEquals(125, pb.getSolver().getNbSolutions());
            //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }

    public void test2() {
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x = pb.makeBoundIntVar("x", 1, 5);
            IntDomainVar y = pb.makeBoundIntVar("y", 1, 5);
            IntDomainVar z = pb.makeBoundIntVar("z", 1, 5);
            IntDomainVar w = pb.makeEnumIntVar("z", 1, 5);
            pb.post(pb.min(new IntDomainVar[]{x, y, z},w));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solve();
            do {
                //System.out.println("" + x.getVal() + "=max(" + y.getVal() + "," +
                //�    z.getVal()+")");
            } while (pb.nextSolution() == Boolean.TRUE);
            System.out.println("" + pb.getSolver().getSearchSolver().getNodeCount());
            assertEquals(125, pb.getSolver().getNbSolutions());
            //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }

    public static void testPropagMinTern1() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
        IntDomainVar z = pb.makeEnumIntVar("z", 4, 5);
        IntDomainVar min = pb.makeEnumIntVar("min", 1, 5);
        pb.post(pb.min(z, y, min));
        try {
            min.remVal(3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("min " + min.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(!y.canBeInstantiatedTo(3));
        assertTrue(!y.canBeInstantiatedTo(3));
    }

    public static void testPropagMinTern2() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
        IntDomainVar z = pb.makeEnumIntVar("z", 1, 5);
        IntDomainVar min = pb.makeEnumIntVar("min", 1, 5);
        pb.post(pb.min(z, y, min));
        try {
            y.remVal(3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("min " + min.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(z.canBeInstantiatedTo(3) && min.canBeInstantiatedTo(3));
        assertTrue(z.canBeInstantiatedTo(3) && min.canBeInstantiatedTo(3));
    }

    public static void testPropagMinTern3() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
        IntDomainVar z = pb.makeEnumIntVar("z", 1, 5);
        IntDomainVar min = pb.makeEnumIntVar("min", 1, 5);
        pb.post(pb.min(z, y, min));
        try {
            min.remVal(3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("min " + min.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(y.canBeInstantiatedTo(3) && z.canBeInstantiatedTo(3));
        assertTrue(y.canBeInstantiatedTo(3) && z.canBeInstantiatedTo(3));
    }

    public static void testPropagMinTern4() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 3);
        IntDomainVar z = pb.makeEnumIntVar("z", 4, 6);
        IntDomainVar min = pb.makeEnumIntVar("min", 1, 6);
        pb.post(pb.min(z, y, min));
        try {
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("min " + min.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(min.getDomain().getSize() == 3);
        assertTrue(min.getDomain().getSize() == 3);
    }


    public static void testRandom() {

        for (int i = 0; i < 10; i++) {

            Problem chocoCSP = new Problem();
            IntDomainVar varA = chocoCSP.makeEnumIntVar("varA", 0, 3);
            IntDomainVar varB = chocoCSP.makeEnumIntVar("varB", 0, 3);
            IntDomainVar varC = chocoCSP.makeEnumIntVar("varC", 0, 3);
            chocoCSP.post(chocoCSP.min(varA, varB, varC));

            //-----Now get solutions
            chocoCSP.getSolver().setFirstSolution(true);
            chocoCSP.getSolver().generateSearchSolver(chocoCSP);
            chocoCSP.getSolver().setValSelector(new RandomIntValSelector(100 + i));
            chocoCSP.getSolver().setVarSelector(new RandomIntVarSelector(chocoCSP, 101 + i));

            //System.out.println("Choco Solutions");
            int nbSolution = 0;
            if (chocoCSP.solve() == Boolean.TRUE) {
                do {
                    //System.out.println("Min(" + ((IntDomainVar) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntDomainVar) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntDomainVar) chocoCSP.getIntVar(2)).getVal());
                    nbSolution++;
                } while (chocoCSP.nextSolution() == Boolean.TRUE);
            }
            assertEquals(nbSolution, 16);
        }
    }


}
