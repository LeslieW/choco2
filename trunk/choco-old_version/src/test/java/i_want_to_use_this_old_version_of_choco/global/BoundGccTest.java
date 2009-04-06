package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 8 janv. 2008
 * Time: 18:22:15
 * To change this template use File | Settings | File Templates.
 */
public class BoundGccTest extends TestCase {
  public void testIsSatisfied() {
    AbstractProblem pb = new Problem();
    IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 1);
    IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 1);
    IntDomainVar v3 = pb.makeEnumIntVar("v3", 2, 2);
    IntDomainVar v4 = pb.makeEnumIntVar("v4", 2, 2);
    Constraint c1 = pb.boundGcc(new IntDomainVar[]{v1, v2, v3, v4}, 1, 2, new int[]{1, 1}, new int[]{2, 2});
    Constraint c2 = pb.boundGcc(new IntDomainVar[]{v1, v2, v3, v4}, 1, 2, new int[]{1, 1}, new int[]{1, 3});
    System.out.println(c1.pretty());
    System.out.println(c2.pretty());
    assertTrue(c1.isSatisfied());
    assertFalse(c2.isSatisfied());
  }

  public void testIsSatisfiedVar() {
    AbstractProblem pb = new Problem();
    IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 1);
    IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 1);
    IntDomainVar v3 = pb.makeEnumIntVar("v3", 2, 2);
    IntDomainVar v4 = pb.makeEnumIntVar("v4", 2, 2);
    IntDomainVar x = pb.makeEnumIntVar("x", 2, 2);
    IntDomainVar y = pb.makeEnumIntVar("y", 1, 1);
    Constraint c1 = pb.boundGccVar(new IntDomainVar[]{v1, v2, v3}, 1, 2, new IntVar[]{x, y});
    Constraint c2 = pb.boundGccVar(new IntDomainVar[]{v1, v3, v4}, 1, 2, new IntVar[]{x, y});
    System.out.println(c1.pretty());
    System.out.println(c2.pretty());
    assertTrue(c1.isSatisfied());
    assertFalse(c2.isSatisfied());
  }
}
