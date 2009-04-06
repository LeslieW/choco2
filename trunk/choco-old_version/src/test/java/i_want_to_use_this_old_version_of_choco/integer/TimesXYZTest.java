package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

import java.util.logging.Logger;


public class TimesXYZTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x, y, z;

  protected void setUp() {
    logger.fine("choco.currentElement.bool.TimesXYZTest Testing...");
    pb = new Problem();
  }

  protected void tearDown() {
    y = null;
    x = null;
    z = null;
    pb = null;
  }

  public void test1() {
    logger.finer("test1");
    x = pb.makeEnumIntVar("x", -7, 12);
    y = pb.makeEnumIntVar("y", 3, 5);
    z = pb.makeEnumIntVar("z", 22, 59);
    pb.post(pb.times(x, y, z));
    try {
      pb.propagate();
      assertEquals(x.getInf(), 5);
      y.setVal(3);
      pb.propagate();
      assertEquals(x.getInf(), 8);
      assertEquals(z.getSup(), 36);
      assertEquals(z.getInf(), 24);
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  public void test2() {
    for (int i = 0; i < 10; i++) {
      pb = new Problem();
      logger.finer("test2");
      x = pb.makeEnumIntVar("x", 1, 2);
      y = pb.makeEnumIntVar("y", 3, 5);
      z = pb.makeEnumIntVar("z", 3, 10);
      pb.post(pb.times(x, y, z));
      pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
      pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
      pb.solve();
      do {
        System.out.println("" + x.getVal() + "*" + y.getVal() + "=" + z.getVal());
      } while (pb.nextSolution() == Boolean.TRUE);
      System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
      //assertEquals( pb.getSolver().getNbSolutions(), 14);
    }
  }

  public void test2b() throws ContradictionException {
    for (int i = 0; i < 10; i++) {
      pb = new Problem();
      logger.finer("test2");
      x = pb.makeBoundIntVar("x", 1, 2);
      y = pb.makeBoundIntVar("y", 3, 5);
      z = pb.makeBoundIntVar("z", 3, 10);
      pb.post(pb.times(x, y, z));
      pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
      pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
      pb.solve();
      do {
        //System.out.println("" + x.getVal() + "*" + y.getVal() + "=" +
        //    z.getVal());
        assertEquals(x.getVal() * y.getVal(), z.getVal());
      } while (pb.nextSolution() == Boolean.TRUE);
      //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
      assertEquals(pb.getSolver().getNbSolutions(), 6);
    }
  }

  public void test3() {
    for (int i = 0; i < 10; i++) {
      System.out.println("test3-" + i);
      try {
        AbstractProblem pb = new Problem();

        IntDomainVar x = pb.makeEnumIntVar("x", -10, 10);
        IntDomainVar y = pb.makeEnumIntVar("y", -10, 10);
        IntDomainVar z = pb.makeEnumIntVar("z", -20, 20);
        //IntVar oneFourFour = pb.makeConstantIntVar(144);

        pb.post(pb.times(x, y, z));
        //pb.post(pb.neq(z, oneFourFour));

        pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
        pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
        pb.solve();
        do {
          //System.out.println("" + x.getVal() + "*" + y.getVal() + "=" + z.getVal());
          assertEquals(x.getVal() * y.getVal(), z.getVal());
        } while (pb.nextSolution() == Boolean.TRUE);
        assertEquals(225, pb.getSolver().getNbSolutions()); // with 10,10,20
        //assertEquals(3993, pb.getSolver().getNbSolutions()); // with 100,100,200
        //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
      } catch (Exception e) {
        e.printStackTrace();
        assertTrue(false);
      }
    }
  }

    public void test3b() {
    for (int i = 0; i < 10; i++) {
      System.out.println("test3-" + i);
      try {
        AbstractProblem pb = new Problem();

        IntDomainVar x = pb.makeEnumIntVar("x", -10, 10);
        IntDomainVar y = pb.makeEnumIntVar("y", -10, 10);
        IntDomainVar z = pb.makeEnumIntVar("z", -20, 20);
        IntVar oneFourFour = pb.makeConstantIntVar(14);
        //IntVar oneFourFour = pb.makeConstantIntVar(144);

        pb.post(pb.times(x, y, z));
        pb.post(pb.neq(z, oneFourFour));

        pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
        pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
        pb.solve();
        do {
          //System.out.println("" + x.getVal() + "*" + y.getVal() + "=" + z.getVal());
          assertEquals(x.getVal() * y.getVal(), z.getVal());
        } while (pb.nextSolution() == Boolean.TRUE);
        assertEquals(221, pb.getSolver().getNbSolutions()); // with 10,10,20,14
        //assertEquals(3967, pb.getSolver().getNbSolutions()); // with 100,100,200,144
        //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
      } catch (Exception e) {
        e.printStackTrace();
        assertTrue(false);
      }
    }
  }

  public void test3c() {
    for (int i = 0; i < 10; i++) {
      System.out.println("test3-" + i);
      try {
        AbstractProblem pb = new Problem();

        IntDomainVar x = pb.makeBoundIntVar("x", -10, 10);
        IntDomainVar y = pb.makeBoundIntVar("y", -10, 10);
        IntDomainVar z = pb.makeBoundIntVar("z", -20, 20);

        pb.post(pb.times(x, y, z));

        pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
        pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
        pb.solve();
        do {
          //System.out.println("" + x.getVal() + "*" + y.getVal() + "=" + z.getVal());
          assertEquals(x.getVal() * y.getVal(), z.getVal());
        } while (pb.nextSolution() == Boolean.TRUE);
        assertEquals(225, pb.getSolver().getNbSolutions()); // with 10,10,20
        //assertEquals(3993, pb.getSolver().getNbSolutions()); // with 100,100,200
        //System.out.println("Nb solution : " + pb.getSolver().getNbSolutions());
      } catch (Exception e) {
        e.printStackTrace();
        assertTrue(false);
      }
    }
  }

}
