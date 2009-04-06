package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 5 avr. 2006
 * Time: 08:42:43
 * To change this template use File | Settings | File Templates.
 */
public class LexTest extends TestCase {

    public void testLessLexq() {
        for (int seed = 0; seed < 5; seed++) {
            AbstractProblem pb = new Problem();
            int n1 = 8;
            int k = 2;
            IntDomainVar[] vs1 = new IntDomainVar[n1 / 2];
            IntDomainVar[] vs2 = new IntDomainVar[n1 / 2];
            for (int i = 0; i < n1 / 2; i++) {
                vs1[i] = pb.makeEnumIntVar("" + i, 0, k);
                vs2[i] = pb.makeEnumIntVar("" + i, 0, k);
            }
            pb.post(pb.lexeq(vs1, vs2));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed));
            pb.getSolver().setValSelector(new RandomIntValSelector(seed));
            pb.solveAll();
            int kpn = (int) Math.pow(k + 1, n1 / 2);
            assertEquals(pb.getSolver().getNbSolutions(), (kpn * (kpn + 1) / 2));
            //if (pb.getSolver().getNbSolutions() != kpn*(kpn + 1)/2 )
            //throw new Error("nbSol " + pb.getSolver().getNbSolutions() + " should be " + (kpn*(kpn + 1)/2));
            System.out.println("NbSol : " + pb.getSolver().getNbSolutions() + " =? " + (kpn * (kpn + 1) / 2));
        }
    }


    public static void testLex() {
        for (int seed = 0; seed < 5; seed++) {
            AbstractProblem pb = new Problem();
            int n1 = 8;
            int k = 2;
            IntDomainVar[] vs1 = new IntDomainVar[n1 / 2];
            IntDomainVar[] vs2 = new IntDomainVar[n1 / 2];
            for (int i = 0; i < n1 / 2; i++) {
                vs1[i] = pb.makeEnumIntVar("" + i, 0, k);
                vs2[i] = pb.makeEnumIntVar("" + i, 0, k);
            }
            pb.post(pb.lex(vs1, vs2));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed));
            pb.getSolver().setValSelector(new RandomIntValSelector(seed));
            //pb.getSolver().setValIterator(new IncreasingTrace());
            pb.solveAll();
            assertEquals(3240, pb.getSolver().getNbSolutions());
            System.out.println("NbSol : " + pb.getSolver().getNbSolutions() + " =? 3240");
        }
    }

    public static void testLexiMin() {
        for (int seed = 0; seed < 10; seed++) {
            Problem p = new Problem();
            IntDomainVar[] u = p.makeEnumIntVarArray("u", 3, 2, 5);
            IntDomainVar[] v = p.makeEnumIntVarArray("v", 3, 2, 4);
            p.post(new LeximinConstraint(u, v));
            p.post(p.allDifferent(v));
            p.getSolver().setValSelector(new RandomIntValSelector(seed));
            p.getSolver().setVarSelector(new RandomIntVarSelector(p, seed + 2));
            p.solve();

            do {
                System.out.print("u = [ " + u[0].getVal() + " " + u[1].getVal() + " " + u[2].getVal() + " ] - ");
                System.out.println("v = [ " + v[0].getVal() + " " + v[1].getVal() + " " + v[2].getVal() + " ]");
            } while (p.nextSolution() == Boolean.TRUE);
            assertEquals(78, p.getSolver().getNbSolutions());
        }
    }

  public void testLexiSatisfied() {
    AbstractProblem pb = new Problem();
    IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 1);
    IntDomainVar v2 = pb.makeEnumIntVar("v2", 2, 2);
    IntDomainVar v3 = pb.makeEnumIntVar("v3", 3, 3);
    Constraint c1 = pb.lex(new IntDomainVar[]{v1, v2}, new IntDomainVar[]{v1, v3});
    Constraint c2 = pb.lex(new IntDomainVar[]{v1, v2}, new IntDomainVar[]{v1, v2});
    Constraint c3 = pb.lex(new IntDomainVar[]{v1, v2}, new IntDomainVar[]{v1, v1});
    Constraint c4 = pb.lexeq(new IntDomainVar[]{v1, v2}, new IntDomainVar[]{v1, v3});
    Constraint c5 = pb.lexeq(new IntDomainVar[]{v1, v2}, new IntDomainVar[]{v1, v2});
    Constraint c6 = pb.lexeq(new IntDomainVar[]{v1, v2}, new IntDomainVar[]{v1, v1});
    System.out.println(c2.pretty());
    System.out.println(c5.pretty());
    assertTrue(c1.isSatisfied());
    assertFalse(c2.isSatisfied());
    assertFalse(c3.isSatisfied());
    assertTrue(c4.isSatisfied());
    assertTrue(c5.isSatisfied());
    assertFalse(c6.isSatisfied());
  }
}
