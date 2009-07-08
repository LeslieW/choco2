package i_want_to_use_this_old_version_of_choco.palm.cbj.explain;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.search.AssignVar;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.palm.search.Assignment;
import i_want_to_use_this_old_version_of_choco.palm.search.SymbolicDecision;
import i_want_to_use_this_old_version_of_choco.search.IntBranchingTrace;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpExplanation implements Explanation {

  protected BitSet originalConstraints;
  protected BitSet originalStaticConstraints;
  protected BitSet decisionConstraints;
  protected AbstractProblem pb;

  public JumpExplanation(AbstractProblem pb) {
    originalConstraints = new BitSet();
    originalStaticConstraints = new BitSet();
    decisionConstraints = new BitSet();
    this.pb = pb;
  }

  public JumpExplanation(int level, AbstractProblem pb) {
    this(pb);
    decisionConstraints.set(level);
  }


  public void merge(ConstraintCollection collection) {
    if (collection instanceof JumpExplanation) {
      JumpExplanation exp = (JumpExplanation) collection;
      originalConstraints.or(exp.originalConstraints);
      decisionConstraints.or(exp.decisionConstraints);
      originalStaticConstraints.or(exp.originalStaticConstraints);
    } else {
      System.err.println("dev.i_want_to_use_this_old_version_of_choco.palm.jump.JumpExplanation merge");
    }
  }

  public ConstraintCollection copy() {
    JumpExplanation expl = new JumpExplanation(pb);
    expl.merge(this);
    return expl;
  }

  public void add(Propagator constraint) {
    int idx = ((JumpConstraintPlugin) constraint.getPlugIn()).getConstraintIdx();
    if (PartiallyStoredVector.isStaticIndex(idx)) {
      this.originalStaticConstraints.set(PartiallyStoredVector.getSmallIndex(idx));
    } else {
      this.originalConstraints.set(PartiallyStoredVector.getSmallIndex(idx));
    }
  }

  public void add(int level) {
    this.decisionConstraints.set(level);
  }

  public void add(int from, int to) {
    this.decisionConstraints.set(from, to);
  }

  public void delete(Propagator constraint) {
    int idx = ((JumpConstraintPlugin) constraint.getPlugIn()).getConstraintIdx();
    if (PartiallyStoredVector.isStaticIndex(idx)) {
      this.originalStaticConstraints.clear(PartiallyStoredVector.getSmallIndex(idx));
    } else {
      this.originalConstraints.clear(PartiallyStoredVector.getSmallIndex(idx));
    }
  }

  public void delete(int level) {
    this.decisionConstraints.clear(level);
  }

  public void addAll(Collection collection) {
    //TODO
  }

  public boolean isEmpty() {
    return originalConstraints.isEmpty() && originalStaticConstraints.isEmpty()
        && decisionConstraints.isEmpty();
  }

  public int size() {
    return originalConstraints.cardinality() + originalStaticConstraints.cardinality() +
        decisionConstraints.cardinality();
  }

  public void clear() {
    originalConstraints.clear();
    originalStaticConstraints.clear();
    decisionConstraints.clear();
  }

  public boolean contains(Propagator ct) {
    int idx = ((JumpConstraintPlugin) ct.getPlugIn()).getConstraintIdx();
    if (PartiallyStoredVector.isStaticIndex(idx)) {
      return this.originalStaticConstraints.get(PartiallyStoredVector.getSmallIndex(idx));
    } else {
      return this.originalConstraints.get(PartiallyStoredVector.getSmallIndex(idx));
    }
  }

  public boolean contains(int level) {
    return this.decisionConstraints.get(level);
  }

  public int getLastLevel(int currentLevel) {
    for (int i = currentLevel; i >= 0; i--) {
      if (decisionConstraints.get(i)) return i;
    }
    return -1;
  }

  // TODO
  public boolean containsAll(ConstraintCollection collec) {
    return false;
  }

  // TODO : currentElement
  public Set toSet() {
    Set ret = new HashSet();
    for (int i = originalConstraints.nextSetBit(0); i >= 0; i = originalConstraints.nextSetBit(i + 1)) {
      ret.add(((ExplainedProblem) pb).getConstraintNb(i));
    }
    return ret;
  }

  public boolean isValid() {
    return false;
  }

  public void empties() {
    clear();
  }

  public int nogoodSize() {
    return decisionConstraints.cardinality();
  }

  public BitSet getDecisionBitSet() {
    return decisionConstraints;
  }

  public BitSet getOriginalConstraintBitSet() {
    return originalConstraints;
  }

  public Propagator getConstraint(int i) {
    IntBranchingTrace btrace = (IntBranchingTrace) ((JumpGlobalSearchSolver) pb.getSolver().getSearchSolver()).traceStack.get(i - 1);
    if (btrace.getBranching() instanceof AssignVar)
      return new Assignment((ExplainedIntVar) btrace.getBranchingObject(), btrace.getBranchIndex());
    else {
      throw new UnsupportedOperationException("the branching " + btrace.getBranching() + " is not yet supported by the JumpExplanation");
    }
  }

  public SymbolicDecision[] getNogood() {
    SymbolicDecision[] nogood = new SymbolicDecision[decisionConstraints.cardinality()];
    int cpt = 0;
    for (int i = decisionConstraints.nextSetBit(0); i >= 0; i = decisionConstraints.nextSetBit(i + 1)) {
      nogood[cpt] = (SymbolicDecision) this.getConstraint(i);
      cpt++;
    }
    return nogood;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("{");

    for (int i = originalConstraints.nextSetBit(0); i >= 0; i = originalConstraints.nextSetBit(i + 1)) {
      str.append(((ExplainedProblem) this.pb).getConstraintNb(PartiallyStoredVector.getGlobalIndex(i, false)));
      if (originalConstraints.nextSetBit(i + 1) >= 0)
        str.append(", ");
    }
    str.append(" || ");
    for (int i = originalStaticConstraints.nextSetBit(0); i >= 0; i = originalStaticConstraints.nextSetBit(i + 1)) {
      str.append(((ExplainedProblem) this.pb).getConstraintNb(PartiallyStoredVector.getGlobalIndex(i, true)));
      if (originalStaticConstraints.nextSetBit(i + 1) >= 0)
        str.append(", ");
    }
    str.append(" || ");
    for (int i = decisionConstraints.nextSetBit(0); i >= 0; i = decisionConstraints.nextSetBit(i + 1)) {
      str.append("decision nb : " + i);
      if (decisionConstraints.nextSetBit(i + 1) >= 0)
        str.append(", ");
    }
    str.append("}");
    return str.toString();
  }
}