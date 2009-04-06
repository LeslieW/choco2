/*
 * regressionTest.java
 *
 * Created on 3 janvier 2006, 21:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package i_want_to_use_this_old_version_of_choco.regression;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * @author grochart
 */
public class RegressionTest extends TestCase {

  // 03/01/2006: number of constraints was not correctly maintained !
  // bug reported by R�mi Coletta
  public void testNbConstraints() {
    System.out.println("Regression currentElement: 03/01/2006 - Remi Coletta");

    Problem pb = new Problem();
    IntDomainVar[] vars = new IntDomainVar[2];
    vars[0] = pb.makeEnumIntVar("x1", 0, 3);
    vars[1] = pb.makeEnumIntVar("x2", 0, 3);

    // On place la contrainte (x1 == x2)
    this.assertEquals(0, pb.getNbIntConstraints());
    pb.worldPush();
    Constraint c1 = pb.eq(vars[0], vars[1]);
    pb.post(c1);
    this.assertEquals(1, pb.getNbIntConstraints());
    pb.solve(true);
    int nbSol1 = pb.getSolver().getNbSolutions();
    this.assertEquals(4, nbSol1);
    // On supprime maintenant la contrainte...
    pb.worldPopUntil(0);
    this.assertEquals(0, pb.getNbIntConstraints());

    // On place la seconde contrainte : (x1 != x2)
    pb.worldPush();
    Constraint c2 = pb.neq(vars[0], vars[1]);
    pb.post(c2);
    this.assertEquals(1, pb.getNbIntConstraints());
    pb.solve(true);
    int nbSol2 = pb.getSolver().getNbSolutions();
    this.assertEquals(12, nbSol2);
  }

  public void testIsFeasible() {
    System.out.println("Regression currentElement: 27/01/2006");

    Problem pb = new Problem();
    IntDomainVar[] vars = new IntDomainVar[4];
    vars[0] = pb.makeEnumIntVar("x1", 0, 2);
    vars[1] = pb.makeEnumIntVar("x2", 0, 2);
    vars[2] = pb.makeEnumIntVar("x3", 0, 2);
    vars[3] = pb.makeEnumIntVar("x4", 0, 2);
    for (int i = 0; i < 4; i++) {
      for (int j = i + 1; j < 4; j++)
        pb.post(pb.neq(vars[i], vars[j]));
    }
    pb.solve();
    // On place la contrainte (x1 == x2)
    this.assertEquals(true, pb.isFeasible() != null);
    this.assertEquals(false, pb.isFeasible().booleanValue());

  }

  public void testConstraintsIterator() {
    Problem pb = new Problem();
    IntDomainVar x = pb.makeBoundIntVar("X", 1, 5);
    IntDomainVar y = pb.makeBoundIntVar("Y", 1, 5);
    Constraint c1 = pb.eq(x, 1);
    Constraint c2 = pb.eq(x, y);
    pb.post(c1);
    pb.post(c2);
    assertEquals(x.getNbConstraints(), 2);
    assertEquals(y.getNbConstraints(), 1);

    Iterator constraints = x.getConstraintsIterator();
    assertTrue(constraints.hasNext());
    assertEquals(constraints.next(), c1);
    assertTrue(constraints.hasNext());
    assertEquals(constraints.next(), c2);
    assertFalse(constraints.hasNext());

    constraints = y.getConstraintsIterator();
    assertTrue(constraints.hasNext());
    assertEquals(constraints.next(), c2);
    assertFalse(constraints.hasNext());
  }

    /*public void testMult() {
      System.out.println("Regression currentElement: 27/01/2006");

      Problem pb = new Problem();
      IntDomainVar v = pb.makeEnumIntVar("x1", 0, 2);
      Constraint c = pb.eq(pb.mult(3,v),1);
      pb.post(c);
      pb.solve();
      this.assertEquals(true, pb.isFeasible() != null);
      this.assertEquals(false, pb.isFeasible().booleanValue());
    } */

}
