/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.integer.IntVarEventTest.java, last modified by FLABURTHE 2 janv. 2004 */
package choco.model.variables.integer;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.EqualXYC;
import choco.cp.solver.propagation.ChocEngine;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.logging.Logger;

public class IntVarEventTest  {
	private final static Logger LOGGER = ChocoLogging.getTestLogger();
  private Solver s;
  private ChocEngine pe;
  private IntDomainVarImpl x;
  private IntDomainVarImpl y;
  private IntDomainVarImpl z;
  private SConstraint c1;
  private SConstraint c2;
  PropagationEvent evt;

  class LocalSConstraintClass extends AbstractBinIntSConstraint {
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

    public LocalSConstraintClass(IntDomainVar x, IntDomainVar y) {
      super(x, y);
    }
  }

    @Before
  public void setUp() {
    LOGGER.fine("IntVarEvent Testing...");
    s = new CPSolver();
    pe = (ChocEngine) s.getPropagationEngine();
    x = (IntDomainVarImpl) ((CPSolver)s).createIntVar("X",1, 0, 100);
    y = (IntDomainVarImpl) ((CPSolver)s).createIntVar("Y", 1, 0, 100);
    z = (IntDomainVarImpl) ((CPSolver)s).createIntVar("Z",1,  0, 100);
    c1 = new LocalSConstraintClass(x, y);
    c2 = new LocalSConstraintClass(y, z);
  }

    @After
  public void tearDown() {
    c1 = null;
    c2 = null;
    x = null;
    y = null;
    z = null;
    s = null;
    pe = null;
    evt = null;
  }

    @Test
  public void test1() {
    assertEquals(0, pe.getNbPendingEvents());
    s.post(c1);
    s.post(c2);
    assertEquals(2, pe.getNbPendingEvents());
    HashSet expectedSet = new HashSet();
    expectedSet.add(c1);
    expectedSet.add(c2);
    HashSet tmp = new HashSet();
    tmp.add(pe.getPendingEvent(0).getModifiedObject());
    tmp.add(pe.getPendingEvent(1).getModifiedObject());
    assertEquals(expectedSet, tmp);
    try {
      s.propagate();
    } catch (ContradictionException e) {
      assertFalse(true);
    }
    assertEquals(0, pe.getNbPendingEvents());
    x.getDomain().updateInf(1);
    pe.postUpdateInf(x, c2, false);
    y.getDomain().updateSup(95);
    pe.postUpdateSup(y, c2, false);
    y.getDomain().updateInf(3);
    pe.postUpdateInf(y, c1, false); // and not a value above 1, such as 2 !!

    assertEquals(2, pe.getNbPendingEvents());
    evt = pe.getPendingEvent(0);
    assertEquals(evt.getModifiedObject(), x);
    assertEquals(IntVarEvent.INCINFbitvector + IntVarEvent.REMVALbitvector, ((IntVarEvent) evt).getEventType());
    assertEquals(c2, ((IntVarEvent) evt).getCause());

    evt = pe.getPendingEvent(1);
    assertEquals(evt.getModifiedObject(), y);
    assertEquals(IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector, ((IntVarEvent) evt).getEventType());
    assertEquals(null, ((IntVarEvent) evt).getCause());
  }

  /**
   * tests that a bound event on a variable with two constraints and no cause, yields two propagation steps
   */
  @Test
  public void test2() {
    c1 = new EqualXYC(x, y, 2);
    c2 = new EqualXYC(y, z, 1);
    assertEquals(0, pe.getNbPendingEvents());
    s.post(c1);
    s.post(c2);
    assertEquals(2, pe.getNbPendingEvents());
    HashSet expectedSet = new HashSet();
    expectedSet.add(c1);
    expectedSet.add(c2);
    HashSet tmp = new HashSet();
    tmp.add(pe.getPendingEvent(0).getModifiedObject());
    tmp.add(pe.getPendingEvent(1).getModifiedObject());
    assertEquals(expectedSet, tmp);
    try {
      s.propagate();
    } catch (ContradictionException e) {
      assertFalse(true);
    }
    assertEquals(0, pe.getNbPendingEvents());

    y.getDomain().updateSup(90);
    pe.postUpdateSup(y, c1, false);
    y.getDomain().updateInf(10);
    pe.postUpdateInf(y, c2, false);
    assertEquals(1, pe.getNbPendingEvents());
    evt = pe.getPendingEvent(0);
    assertEquals(evt.getModifiedObject(), y);
    assertEquals(IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector, ((IntVarEvent) evt).getEventType());

    PartiallyStoredVector constraints = y.getConstraintVector();
    DisposableIntIterator cit = constraints.getIndexIterator();
    assertTrue(cit.hasNext());
    assertEquals(PartiallyStoredVector.STORED_OFFSET + 0, cit.next());
    assertTrue(cit.hasNext());
    assertEquals(PartiallyStoredVector.STORED_OFFSET + 1, cit.next());
    assertFalse(cit.hasNext());
  }

}
