package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.constraints.MaxOfAList;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 17 janv. 2007
 * Time: 14:44:10
 * To change this template use File | Settings | File Templates.
 */
public class MaxTest extends TestCase {

    public void test1() {
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x = pb.makeEnumIntVar("x", 1, 5);
            IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
            IntDomainVar z = pb.makeEnumIntVar("z", 1, 5);
            IntDomainVar w = pb.makeEnumIntVar("z", 1, 5);
            pb.post(pb.max(new IntDomainVar[]{x, y, z},w));
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
            pb.post(pb.max(new IntDomainVar[]{x, y, z},w));
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

    public void test2bis() {
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x = pb.makeBoundIntVar("x", 1, 5);
            IntDomainVar y = pb.makeBoundIntVar("y", 1, 5);
            IntDomainVar z = pb.makeBoundIntVar("z", 1, 5);
            pb.post(pb.max(y, z, x));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solve();
            do {
                //System.out.println("" + x.getVal() + "=max(" + y.getVal() + "," +
                //�    z.getVal()+")");
            } while (pb.nextSolution() == Boolean.TRUE);
            System.out.println("" + pb.getSolver().getSearchSolver().getNodeCount());
            assertEquals(25, pb.getSolver().getNbSolutions());
            //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }


    public void test3() {
        Random rand = new Random();
        for (int i = 0; i <= 10; i++) {
            Problem pb = new Problem();
            IntDomainVar x;
            if (rand.nextBoolean()) x = pb.makeBoundIntVar("x", 1, 5);
            else x = pb.makeEnumIntVar("x", 1, 5);
            IntDomainVar y;
            if (rand.nextBoolean()) y = pb.makeBoundIntVar("y", 1, 5);
            else y = pb.makeEnumIntVar("y", 1, 5);
            IntDomainVar z;
            if (rand.nextBoolean()) z = pb.makeBoundIntVar("z", 1, 5);
            else z = pb.makeEnumIntVar("z", 1, 5);

            pb.post(new MaxOfAList(new IntDomainVar[]{x, y, z}));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solve();
            do {
                /*System.out.println("" + x.getVal() + "=max(" + y.getVal() + "," +
                z.getVal()+")");*/
            } while (pb.nextSolution() == Boolean.TRUE);
            assertEquals(25, pb.getSolver().getNbSolutions());
            //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
        }
    }

    public static void testPropagMaxTern1() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
        IntDomainVar z = pb.makeEnumIntVar("z", 1, 2);
        IntDomainVar max = pb.makeEnumIntVar("max", 1, 5);
        pb.post(pb.max(z, y, max));
        try {
            max.remVal(3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("max " + max.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(!y.canBeInstantiatedTo(3));
        assertTrue(!y.canBeInstantiatedTo(3));
    }

    public static void testPropagMaxTern2() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
        IntDomainVar z = pb.makeEnumIntVar("z", 1, 5);
        IntDomainVar max = pb.makeEnumIntVar("max", 1, 5);
        pb.post(pb.max(z, y, max));
        try {
            y.remVal(3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("max " + max.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(z.canBeInstantiatedTo(3) && max.canBeInstantiatedTo(3));
        assertTrue(z.canBeInstantiatedTo(3) && max.canBeInstantiatedTo(3));
    }

    public static void testPropagMaxTern3() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 5);
        IntDomainVar z = pb.makeEnumIntVar("z", 1, 5);
        IntDomainVar max = pb.makeEnumIntVar("max", 1, 5);
        pb.post(pb.max(z, y, max));
        try {
            max.remVal(3);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("max " + max.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(y.canBeInstantiatedTo(3) && z.canBeInstantiatedTo(3));
        assertTrue(y.canBeInstantiatedTo(3) && z.canBeInstantiatedTo(3));
    }

    public static void testPropagMaxTern4() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 3);
        IntDomainVar z = pb.makeEnumIntVar("z", 4, 6);
        IntDomainVar max = pb.makeEnumIntVar("max", 1, 6);
        pb.post(pb.max(z, y, max));
        try {
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("max " + max.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(max.getDomain().getSize() == 3);
        assertTrue(max.getDomain().getSize() == 3);
    }

    public static void testPropagMaxTern5() {
        Problem pb = new Problem();

        IntDomainVar y = pb.makeEnumIntVar("y", 1, 4);
        IntDomainVar z = pb.makeEnumIntVar("z", 4, 8);
        IntDomainVar max = pb.makeEnumIntVar("max", 1, 8);
        pb.post(pb.max(z, y, max));
        try {
            z.remVal(5);
            max.remVal(8);
            pb.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("max " + max.getDomain().pretty());
        System.out.println("y " + y.getDomain().pretty());
        System.out.println("z " + z.getDomain().pretty());
        System.out.println(max.getDomain().getSize() == 3);
        assertEquals(max.getDomain().getSize(),3);
        assertEquals(z.getDomainSize(),3);
    }

    public static void testRandom() {
        for (int i = 0; i < 10; i++) {

            Problem chocoCSP = new Problem();


            IntDomainVar varA = chocoCSP.makeEnumIntVar("varA", 0, 3);
            IntDomainVar varB = chocoCSP.makeEnumIntVar("varB", 0, 3);
            IntDomainVar varC = chocoCSP.makeEnumIntVar("varC", 0, 3);
            chocoCSP.post(chocoCSP.max(varA, varB, varC));

            //-----Now get solutions


            chocoCSP.getSolver().setFirstSolution(true);
            chocoCSP.getSolver().generateSearchSolver(chocoCSP);
            chocoCSP.getSolver().setValSelector(new RandomIntValSelector(100 + i));
            chocoCSP.getSolver().setVarSelector(new RandomIntVarSelector(chocoCSP, i));


            //System.out.println("Choco Solutions");
            int nbSolution = 0;
            if (chocoCSP.solve() == Boolean.TRUE) {
                do {
                    //System.out.println("Max(" + ((IntDomainVar) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntDomainVar) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntDomainVar) chocoCSP.getIntVar(2)).getVal());
                    nbSolution++;
                } while (chocoCSP.nextSolution() == Boolean.TRUE);
            }

            assertEquals(nbSolution, 16);
        }
    }

}
