package i_want_to_use_this_old_version_of_choco.palm.test;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.benders.search.SumRelation;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 29 d�c. 2004
 * Time: 13:01:04
 * To change this template use File | Settings | File Templates.
 */
public class BendersTest extends TestCase {

  public void testDecomp() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    int n = 10;
    IntDomainVar[] vs = new IntDomainVar[n];
    IntDomainVar[] mastervs = new IntDomainVar[6];

    for (int i = 0; i < mastervs.length; i++) {
      mastervs[i] = pb.makeEnumIntVar("m" + i, 1, 6);
      pb.addMasterVariables(mastervs[i]);
    }

    for (int i = 0; i < vs.length; i++) {
      vs[i] = pb.makeEnumIntVar("" + i, 1, n / 2 - 1);
      if (i < n / 2)
        pb.addSubVariables(0, vs[i]);
      else
        pb.addSubVariables(1, vs[i]);
    }

    // A chain of differences
    for (int i = 0; i < (mastervs.length - 1); i++)
      pb.post(pb.neq(mastervs[i], mastervs[i + 1]));
    pb.post(pb.neq(mastervs[0], mastervs[mastervs.length - 1]));

    // two differences between master and sub problems
    pb.post(pb.eq(mastervs[2], vs[n / 2 - 1]));
    pb.post(pb.eq(mastervs[4], vs[n / 2 + 1]));

    // a clique is stated for each sub problem
    for (int i = 0; i < n / 2; i++)
      for (int j = 0; j < i; j++) {
        pb.post(pb.neq(vs[i], vs[j]));
      }
    pb.post(pb.neq(vs[0], vs[n / 2]));
    for (int i = n / 2; i < n; i++)
      for (int j = n / 2; j < i; j++) {
        pb.post(pb.neq(vs[i], vs[j]));
      }
    pb.getSolver().setTimeLimit(10000);
    pb.solve();
    //Logger.getLogger("choco").getHandlers()[0].flush();
    //System.out.println("" + ((JumpProblem) pb).getContradictionExplanation());
    //assertEquals(n/2 * (n/2-1) /2,((JumpProblem) pb).getContradictionExplanation().size());
    //pb.printRuntimeSatistics();
    System.out.println("nbSub : " + pb.getNbSubProblems() + " nbCuts : " + pb.getNbCutsLearned());
    assertEquals(0, pb.getSolver().getNbSolutions());
  }

  public void testDecomp1() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    int n = 10;
    IntDomainVar[] vs = new IntDomainVar[n];
    IntDomainVar[] mastervs = new IntDomainVar[6];

    for (int i = 0; i < mastervs.length; i++) {
      mastervs[i] = pb.makeEnumIntVar("m" + i, 1, 6);
      pb.addMasterVariables(mastervs[i]);
    }

    for (int i = 0; i < vs.length; i++) {
      vs[i] = pb.makeEnumIntVar("" + i, 1, n / 2 - 1);
      if (i < n / 2)
        pb.addSubVariables(0, vs[i]);
      else
        pb.addSubVariables(1, vs[i]);
    }

    // A chain of differences
    for (int i = 0; i < (mastervs.length - 1); i++)
      pb.post(pb.neq(mastervs[i], mastervs[i + 1]));
    pb.post(pb.neq(mastervs[0], mastervs[mastervs.length - 1]));

    // two differences between master and sub problems
    pb.post(pb.eq(mastervs[2], vs[n / 2 - 1]));
    pb.post(pb.eq(mastervs[4], vs[n / 2 + 1]));

    // a clique is stated for each sub problem
    for (int i = 0; i < n / 2; i++)
      for (int j = 0; j < i; j++) {
        pb.post(pb.neq(vs[i], vs[j]));
      }
    pb.post(pb.neq(vs[0], vs[n / 2]));
    for (int i = n / 2; i < n; i++)
      for (int j = n / 2; j < i; j++) {
        pb.post(pb.neq(vs[i], vs[j]));
      }
    pb.getSolver().setNodeLimit(10);
    pb.solve();
    System.out.println("nbSub : " + pb.getNbSubProblems() + " nbCuts : " + pb.getNbCutsLearned());
    assertEquals(0, pb.getSolver().getNbSolutions());
  }

  public void test2Decomp() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    int n = 10;
    IntDomainVar[] vs = new IntDomainVar[12];
    IntDomainVar[] mvs = new IntDomainVar[6];

    for (int i = 0; i < mvs.length; i++) {
      mvs[i] = pb.makeEnumIntVar("m" + i, 0, 2);
      pb.addMasterVariables(mvs[i]);
    }

    for (int i = 0; i < vs.length; i++) {
      vs[i] = pb.makeEnumIntVar("s" + i, 0, 2);
      int idx = i / 4;
      pb.addSubVariables(idx, vs[i]);
    }

    //
    pb.post(pb.eq(pb.scalar(new int[]{3, 2, 7, -8, 1, -12, 50, -22, -6, 19},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[0], vs[1], vs[2], vs[3]}), 10));
    pb.post(pb.eq(pb.scalar(new int[]{3, 2, 7, -8, 1, -12, 15, -2},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[0], vs[1]}), mvs[1]));
    pb.post(pb.eq(pb.scalar(new int[]{1, -12, 20, -22, -6, 2, 50, -22, -6, 19},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[4], vs[5], vs[6], vs[7]}), 11));
    pb.post(pb.eq(pb.scalar(new int[]{3, 20, 7, -8, 1, -12, 5, -2, -6, 1},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[8], vs[9], vs[10], vs[11]}), 12));

    pb.solve();

    for (int i = 0; i < mvs.length; i++) {
      System.out.println(mvs[i] + " = " + mvs[i].getVal());
    }
    for (int i = 0; i < vs.length; i++) {
      System.out.println(vs[i] + " = " + vs[i].getVal());
    }
    //Logger.getLogger("choco").getHandlers()[0].flush();
    //assertEquals(n/2 * (n/2-1) /2,((JumpProblem) pb).getContradictionExplanation().size());
    //pb.printRuntimeSatistics();
    assertEquals(1, pb.getSolver().getNbSolutions());
  }

  public void test3Decomp() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.palm.benders").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    IntDomainVar y1 = pb.makeEnumIntVar("y1", 0, 5);
    IntDomainVar y2 = pb.makeEnumIntVar("y2", 0, 5);
    IntDomainVar x1 = pb.makeEnumIntVar("x1", 0, 5);
    IntDomainVar x2 = pb.makeEnumIntVar("x2", 0, 5);
    IntDomainVar x3 = pb.makeEnumIntVar("x3", 0, 5);
    IntDomainVar x4 = pb.makeEnumIntVar("x4", 0, 5);
    IntDomainVar z = pb.makeBoundIntVar("z", 0, 1000);

    pb.addMasterVariables(y1);
    pb.addMasterVariables(y2);
    pb.addMasterVariables(z);
    pb.addSubVariables(0, x1);
    pb.addSubVariables(0, x2);
    pb.addSubVariables(1, x3);
    pb.addSubVariables(1, x4);
    IntVar vocc = pb.makeConstantIntVar("1", 1);

    pb.post(pb.eq(pb.scalar(new int[]{1, 2}, new IntDomainVar[]{y1, y2}), z));

    pb.post(pb.geq(pb.scalar(new int[]{3, 1, 2, 1}, new IntDomainVar[]{x1, x2, y1, y2}), 12));
    pb.post(pb.geq(pb.scalar(new int[]{2, 1, 3, 5}, new IntDomainVar[]{x1, x2, y1, y2}), 34));
    pb.post(pb.geq(pb.scalar(new int[]{3, 2, 1}, new IntDomainVar[]{x3, x4, y2}), 12));
    pb.post(pb.neq(x1, y1));
    pb.post(pb.occurrence(new IntDomainVar[]{x1, x2, y2}, 2, y1));
    pb.post(pb.occurrence(new IntDomainVar[]{x3, x4, y2}, 0, vocc));

    pb.minimize(z, false);
    System.out.println("best solution : ");
    System.out.println("y1 " + y1.getVal());
    System.out.println("y2 " + y2.getVal());
    System.out.println("x1 " + x1.getVal());
    System.out.println("x2 " + x2.getVal());
    System.out.println("x3 " + x3.getVal());
    System.out.println("x4 " + x4.getVal());
    System.out.println("z " + z.getVal());
    //Logger.getLogger("choco").getHandlers()[0].flush();
    assertEquals(8, pb.getSolver().getOptimumValue().intValue());
  }

  public void test4Decomp() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.palm.benders").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    IntDomainVar y1 = pb.makeEnumIntVar("y1", 0, 5);
    IntDomainVar y2 = pb.makeEnumIntVar("y2", 0, 5);
    IntDomainVar x1 = pb.makeEnumIntVar("x1", 0, 5);
    IntDomainVar x2 = pb.makeEnumIntVar("x2", 0, 5);
    IntDomainVar x3 = pb.makeEnumIntVar("x3", 0, 5);
    IntDomainVar x4 = pb.makeEnumIntVar("x4", 0, 5);
    IntDomainVar z = pb.makeBoundIntVar("z", 0, 1000);
    IntDomainVar z1 = pb.makeBoundIntVar("z1", 0, 1000);
    IntDomainVar z2 = pb.makeBoundIntVar("z2", 0, 1000);

    pb.addMasterVariables(y1);
    pb.addMasterVariables(y2);
    pb.addMasterVariables(z);
    pb.addSubVariables(0, x1);
    pb.addSubVariables(0, x2);
    pb.addSubVariables(0, z1);
    pb.addSubVariables(1, x3);
    pb.addSubVariables(1, x4);
    pb.addSubVariables(1, z2);
    IntVar vocc = pb.makeConstantIntVar("1", 1);

    pb.post(pb.eq(pb.scalar(new int[]{1, 1}, new IntDomainVar[]{y1, y2}), z));
    pb.post(pb.eq(pb.scalar(new int[]{4, 2}, new IntDomainVar[]{x1, x2}), z1));
    pb.post(pb.eq(pb.scalar(new int[]{5, 1}, new IntDomainVar[]{x3, x4}), z2));

    pb.post(pb.geq(pb.scalar(new int[]{5, 1, 2, 1}, new IntDomainVar[]{x1, x2, y1, y2}), 5));
    pb.post(pb.geq(pb.scalar(new int[]{3, 2, 1}, new IntDomainVar[]{x3, x4, y2}), 4));
    pb.post(pb.neq(x1, y1));
    pb.post(pb.occurrence(new IntDomainVar[]{x3, x4, y2}, 0, vocc));

    pb.minimize(z, new IntDomainVar[]{z1, z2}, new SumRelation());
    //Logger.getLogger("choco").getHandlers()[0].flush();
    assertEquals(5, pb.getSolver().getOptimumValue().intValue());
  }

  public void test5Decomp() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.palm.benders").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    IntDomainVar y1 = pb.makeEnumIntVar("y1", 0, 5);
    IntDomainVar y2 = pb.makeEnumIntVar("y2", 0, 5);
    IntDomainVar x1 = pb.makeEnumIntVar("x1", 0, 5);
    IntDomainVar x2 = pb.makeEnumIntVar("x2", 0, 5);
    IntDomainVar x3 = pb.makeEnumIntVar("x3", 0, 5);
    IntDomainVar x4 = pb.makeEnumIntVar("x4", 0, 5);
    IntDomainVar z = pb.makeBoundIntVar("z", 0, 1000);
    IntDomainVar z1 = pb.makeBoundIntVar("z1", -1000, 0);
    IntDomainVar z2 = pb.makeBoundIntVar("z2", -1000, 0);

    pb.addMasterVariables(y1);
    pb.addMasterVariables(y2);
    pb.addMasterVariables(z);
    pb.addSubVariables(0, x1);
    pb.addSubVariables(0, x2);
    pb.addSubVariables(0, z1);
    pb.addSubVariables(1, x3);
    pb.addSubVariables(1, x4);
    pb.addSubVariables(1, z2);
    IntVar vocc = pb.makeConstantIntVar("1", 1);

    pb.post(pb.eq(pb.scalar(new int[]{1, 1}, new IntDomainVar[]{y1, y2}), z));
    pb.post(pb.eq(pb.scalar(new int[]{-4, -2}, new IntDomainVar[]{x1, x2}), z1));
    pb.post(pb.eq(pb.scalar(new int[]{-5, -1}, new IntDomainVar[]{x3, x4}), z2));

    pb.post(pb.geq(pb.scalar(new int[]{5, 1, 2, 1}, new IntDomainVar[]{x1, x2, y1, y2}), 5));
    pb.post(pb.geq(pb.scalar(new int[]{3, 2, 1}, new IntDomainVar[]{x3, x4, y2}), 4));
    pb.post(pb.neq(x1, y1));
    pb.post(pb.occurrence(new IntDomainVar[]{x3, x4, y2}, 0, vocc));

    pb.maximize(z, new IntDomainVar[]{z1, z2}, new SumRelation());
    //Logger.getLogger("choco").getHandlers()[0].flush();
    assertEquals(9, pb.getSolver().getOptimumValue().intValue());
  }

  public void testApproximDecomp() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.palm.benders").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    int n = 10;
    IntDomainVar[] vs = new IntDomainVar[12];
    IntDomainVar[] mvs = new IntDomainVar[6];

    for (int i = 0; i < mvs.length; i++) {
      mvs[i] = pb.makeEnumIntVar("m" + i, 0, 2);
      pb.addMasterVariables(mvs[i]);
    }

    for (int i = 0; i < vs.length; i++) {
      vs[i] = pb.makeEnumIntVar("s" + i, 0, 2);
      int idx = i / 4;
      pb.addSubVariables(idx, vs[i]);
    }
    pb.setApproximatedStructure();

    pb.post(pb.eq(pb.scalar(new int[]{3, 2, 7, -8, 1, -12, 50, -22, -6, 19},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[0], vs[1], vs[2], vs[3]}), 10));
    pb.post(pb.eq(pb.scalar(new int[]{3, 2, 7, -8, 1, -12, 15, -2},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[0], vs[1]}), mvs[1]));
    pb.post(pb.eq(pb.scalar(new int[]{1, -12, 20, -22, -6, 2, 50, -22, -6, 19},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[4], vs[5], vs[6], vs[7]}), 11));
    pb.post(pb.eq(pb.scalar(new int[]{3, 20, 7, -8, 1, -12, 5, -2, -6, 1},
        new IntDomainVar[]{mvs[0], mvs[1], mvs[2], mvs[3], mvs[4], mvs[5], vs[8], vs[9], vs[10], vs[11]}), 12));

    pb.post(pb.neq(vs[0], vs[4]));
    pb.post(pb.neq(vs[5], vs[9]));
    pb.post(pb.neq(vs[10], vs[3]));
    pb.solve();
    assertEquals(1, pb.getSolver().getNbSolutions());
  }

  public void testCsclpDecomp() {
    BendersProblem pb = new BendersProblem();
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.palm.benders").setLevel(Level.FINEST);
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    IntDomainVar y2 = pb.makeEnumIntVar("y2", 0, 5);
    IntDomainVar y1 = pb.makeEnumIntVar("y1", 0, 5);
    IntDomainVar x1 = pb.makeEnumIntVar("x1", 0, 5);
    IntDomainVar x2 = pb.makeEnumIntVar("x2", 0, 5);
    IntDomainVar x3 = pb.makeEnumIntVar("x3", 0, 5);
    IntDomainVar x4 = pb.makeEnumIntVar("x4", 0, 5);
    IntDomainVar z = pb.makeBoundIntVar("z", 0, 1000);
    IntDomainVar z1 = pb.makeBoundIntVar("z1", 0, 1000);
    IntDomainVar z2 = pb.makeBoundIntVar("z2", 0, 1000);

    pb.addMasterVariables(y2);
    pb.addMasterVariables(y1);
    pb.addMasterVariables(z);
    pb.addSubVariables(0, x3);
    pb.addSubVariables(0, x4);
    pb.addSubVariables(0, z1);
    pb.addSubVariables(1, x1);
    pb.addSubVariables(1, x2);
    pb.addSubVariables(1, z2);
    IntVar vocc = pb.makeConstantIntVar("1", 1);

    //pb.post(pb.leq(pb.minus(y1,y2),2));
    pb.post(pb.eq(pb.scalar(new int[]{1, 1}, new IntDomainVar[]{y1, y2}), z));
    pb.post(pb.eq(pb.scalar(new int[]{4, 2}, new IntDomainVar[]{x1, x2}), z2));
    pb.post(pb.eq(pb.scalar(new int[]{5, 1}, new IntDomainVar[]{x3, x4}), z1));

    pb.post(pb.geq(pb.scalar(new int[]{5, 1, 2, 1}, new IntDomainVar[]{x1, x2, y1, y2}), 5));
    pb.post(pb.geq(pb.scalar(new int[]{3, 2, 1}, new IntDomainVar[]{x3, x4, y2}), 4));
    //pb.post(pb.neq(x1,y2));
    pb.post(pb.occurrence(new IntDomainVar[]{x3, x4, y2}, 0, vocc));
    pb.post(pb.occurrence(new IntDomainVar[]{x3, x4, y1}, 0, vocc));

    pb.minimize(z, new IntDomainVar[]{z1, z2}, new SumRelation());
    //Logger.getLogger("choco").getHandlers()[0].flush();
    assertEquals(5, pb.getSolver().getOptimumValue().intValue());
  }
}

