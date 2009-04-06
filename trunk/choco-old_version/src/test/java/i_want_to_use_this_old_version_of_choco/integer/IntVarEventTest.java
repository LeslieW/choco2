// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.integer.IntVarEventTest.java, last modified by FLABURTHE 2 janv. 2004 */
package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractBinIntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.constraints.EqualXYC;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.integer.var.IntVarEvent;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.prop.ChocEngine;
import i_want_to_use_this_old_version_of_choco.prop.PropagationEvent;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.logging.Logger;

public class IntVarEventTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private ChocEngine pe;
  private IntDomainVarImpl x;
  private IntDomainVarImpl y;
  private IntDomainVarImpl z;
  private Constraint c1;
  private Constraint c2;
  private Constraint c3;
  PropagationEvent evt;

  class LocalConstraintClass extends AbstractBinIntConstraint {
    public boolean isSatisfied() {
      return false;
    }

    public void propagate() {
    }

    public void awakeOnInf(int idx) {
    }

    public void awakeOnSup(int idx) {
    }

    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

    public LocalConstraintClass(IntDomainVar x, IntDomainVar y) {
      super(x, y);
    }
  }

  protected void setUp() {
    logger.fine("IntVarEvent Testing...");
    pb = new Problem();
    pe = (ChocEngine) pb.getPropagationEngine();
    x = (IntDomainVarImpl) pb.makeBoundIntVar("X", 0, 100);
    y = (IntDomainVarImpl) pb.makeBoundIntVar("Y", 0, 100);
    z = (IntDomainVarImpl) pb.makeBoundIntVar("Z", 0, 100);
    c1 = new LocalConstraintClass(x, y);
    c2 = new LocalConstraintClass(y, z);
    c3 = new LocalConstraintClass(z, x);
  }

  protected void tearDown() {
    c1 = null;
    c2 = null;
    c3 = null;
    x = null;
    y = null;
    z = null;
    pb = null;
    pe = null;
    evt = null;
  }

  public void test1() {
    assertEquals(0, pe.getNbPendingEvents());
    pb.post(c1);
    pb.post(c2);
    assertEquals(2, pe.getNbPendingEvents());
    HashSet expectedSet = new HashSet();
    expectedSet.add(c1);
    expectedSet.add(c2);
    HashSet tmp = new HashSet();
    tmp.add(pe.getPendingEvent(0).getModifiedObject());
    tmp.add(pe.getPendingEvent(1).getModifiedObject());
    assertEquals(expectedSet, tmp);
    try {
      pb.propagate();
    } catch (ContradictionException e) {
      assertFalse(true);
    }
    assertEquals(0, pe.getNbPendingEvents());
    x.getDomain().updateInf(1);
    pe.postUpdateInf(x, 1);
    y.getDomain().updateSup(95);
    pe.postUpdateSup(y, 1);
    y.getDomain().updateInf(3);
    pe.postUpdateInf(y, 0); // and not a value above 1, such as 2 !!

    assertEquals(2, pe.getNbPendingEvents());
    evt = pe.getPendingEvent(0);
    assertEquals(evt.getModifiedObject(), x);
    assertEquals(IntVarEvent.INCINFbitvector, ((IntVarEvent) evt).getEventType());
    assertEquals(1, ((IntVarEvent) evt).getCause());

    evt = pe.getPendingEvent(1);
    assertEquals(evt.getModifiedObject(), y);
    assertEquals(IntVarEvent.BOUNDSbitvector, ((IntVarEvent) evt).getEventType());
    assertEquals(-1, ((IntVarEvent) evt).getCause());
  }

  /**
   * tests that a bound event on a variable with two constraints and no cause, yields two propagation steps
   */
  public void test2() {
    c1 = new EqualXYC(x, y, 2);
    c2 = new EqualXYC(y, z, 1);
    assertEquals(0, pe.getNbPendingEvents());
    pb.post(c1);
    pb.post(c2);
    assertEquals(2, pe.getNbPendingEvents());
    HashSet expectedSet = new HashSet();
    expectedSet.add(c1);
    expectedSet.add(c2);
    HashSet tmp = new HashSet();
    tmp.add(pe.getPendingEvent(0).getModifiedObject());
    tmp.add(pe.getPendingEvent(1).getModifiedObject());
    assertEquals(expectedSet, tmp);
    try {
      pb.propagate();
    } catch (ContradictionException e) {
      assertFalse(true);
    }
    assertEquals(0, pe.getNbPendingEvents());

    y.getDomain().updateSup(90);
    pe.postUpdateSup(y, 0);
    y.getDomain().updateInf(10);
    pe.postUpdateInf(y, 1);
    assertEquals(1, pe.getNbPendingEvents());
    evt = pe.getPendingEvent(0);
    assertEquals(evt.getModifiedObject(), y);
    assertEquals(IntVarEvent.BOUNDSbitvector, ((IntVarEvent) evt).getEventType());

    PartiallyStoredVector constraints = y.getConstraintVector();
    IntIterator cit = constraints.getIndexIterator();
    assertTrue(cit.hasNext());
    assertEquals(PartiallyStoredVector.STORED_OFFSET + 0, cit.next());
    assertTrue(cit.hasNext());
    assertEquals(PartiallyStoredVector.STORED_OFFSET + 1, cit.next());
    assertFalse(cit.hasNext());
  }

}
