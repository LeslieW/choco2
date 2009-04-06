// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateBitSet;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public class AC4BinConstraint extends CspBinConstraint {

  protected IStateInt[] nbSupport0;
  protected IStateInt[] nbSupport1;
  protected IStateBitSet validSupport0;  //desynchronized copy of the domain of x0, updated only when an event is received
  protected IStateBitSet validSupport1;  //to be consistent with nbSupport0
  //Warning  : copies of domains are made to avoid mistakes of synchronisation between
  //    time of a value removal and the reception of the event concerning this removal.
  //    it is important that after their creation, the support counts of the constraints are meaningful.
  //    Indeed, they may be resynchronized with awake (the initial propagation)
  //    But some awakeOn... methods may be called before awake.

  protected int n1;  // TODO : 3 entiers redondant avec la BinRelation
  protected int n2;
  protected int offset1;
  protected int offset2;

  public AC4BinConstraint(IntDomainVar x0, IntDomainVar x1, BinRelation relation) {//int[][] consistencyMatrice) {
    super(x0, x1, relation);
    n1 = x0.getSup() - x0.getInf() + 1;//x0.getDomainSize();
    n2 = x1.getSup() - x1.getInf() + 1;//x1.getDomainSize();
    offset1 = x0.getInf();
    offset2 = x1.getInf();
    nbSupport0 = new IStateInt[n1];
    nbSupport1 = new IStateInt[n2];
    validSupport0 = this.getProblem().getEnvironment().makeBitSet(n1);
    validSupport1 = this.getProblem().getEnvironment().makeBitSet(n2);
    for (int i = 0; i < n1; i++) {
      nbSupport0[i] = this.getProblem().getEnvironment().makeInt();
      nbSupport0[i].set(0);
      validSupport0.clear(i);
    }
    for (int i = 0; i < n2; i++) {
      nbSupport1[i] = this.getProblem().getEnvironment().makeInt();
      nbSupport1[i].set(0);
      validSupport1.clear(i);
    }
  }

  public Object clone() {
    return new AC4BinConstraint(this.v0, this.v1, this.relation);
  }

  /**
   * decrement the number of values b that support the assignment v1=x ie: b
   * such that v1=x, v2=b is a feasible pair When this count reaches 0,
   * discard the value x from the domain of v1.
   * It takes the offset into account
   */
  public void decrementNbSupportV0(int x) throws ContradictionException {
    nbSupport0[x - offset1].add(-1);
    if (nbSupport0[x - offset1].get() == 0)
      v0.removeVal(x, cIdx0);
  }

  public void decrementNbSupportV1(int y) throws ContradictionException {
    nbSupport1[y - offset2].add(-1);
    if (nbSupport1[y - offset2].get() == 0)
      v1.removeVal(y, cIdx1);
  }

  // recompute the number of values b that support the assignment v1=x
  // ie: b such that v1=x, v2=b is a feasible pair
  // When this count equals 0, discard the value x from the domain of v1
  // It takes the offset into account
  public void resetNbSupport0(int x) throws ContradictionException {
    int nbs = 0;
    DisposableIntIterator itv1 = v1.getDomain().getIterator();
    while (itv1.hasNext()) {
      if (relation.isConsistent(x, itv1.next())) nbs += 1;
    }
    itv1.dispose();
    nbSupport0[x - offset1].set(nbs);
    if (nbs == 0) v0.removeVal(x, cIdx0);
  }

  public void resetNbSupport1(int y) throws ContradictionException {
    int nbs = 0;
    DisposableIntIterator itv0 = v0.getDomain().getIterator();
    while (itv0.hasNext()) {
      if (relation.isConsistent(itv0.next(), y)) nbs += 1;
    }
    itv0.dispose();
    nbSupport1[y - offset2].set(nbs);
    if (nbs == 0) v1.removeVal(y, cIdx1);
  }

  public void awake() throws ContradictionException {
    IntIterator itv0 = v0.getDomain().getIterator(); // initialisation des copies de domaines
    while (itv0.hasNext())
      validSupport0.set(itv0.next() - offset1);
    IntIterator itv1 = v1.getDomain().getIterator();
    while (itv1.hasNext())
      validSupport1.set(itv1.next() - offset2);
    propagate();
  }

  // standard filtering algorithm initializing all support counts
  public void propagate() throws ContradictionException {
    IntIterator itv0 = v0.getDomain().getIterator();
    while (itv0.hasNext()) resetNbSupport0(itv0.next());
    IntIterator itv1 = v1.getDomain().getIterator();
    while (itv1.hasNext()) resetNbSupport1(itv1.next());
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    boolean newEvent = true;
    if (idx == 0) {
      if (validSupport0.get(x - offset1))
        validSupport0.clear(x - offset1);
      else
        newEvent = false;
    } else {
      if (validSupport1.get(x - offset2))
        validSupport1.clear(x - offset2);
      else
        newEvent = false;
    }
    if (newEvent) {   //newEvent
      if (idx == 0) {
        for (int i = offset2; i < n2 + offset2; i++)
          if (validSupport1.get(i - offset2) && relation.isConsistent(x, i)) decrementNbSupportV1(i);//resetNbSupport1(i);
      } else {
        for (int i = offset1; i < n1 + offset1; i++)
          if (validSupport0.get(i - offset1) && relation.isConsistent(i, x)) decrementNbSupportV0(i);//resetNbSupport0(i);
      }
    }
  }

  /**
   * Propagation when a minimal bound of a variable was modified.
   *
   * @param idx The index of the variable.
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */
  // Note: these methods could be improved by considering for each value, the minimal and maximal support considered into the count
  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0) {
      for (int x = offset1; x < v0.getInf(); x++) validSupport0.clear(x - offset1);
      for (int y = offset2; y < n2 + offset2; y++)
        if (validSupport1.get(y - offset2)) {
          for (int x = offset1; x < v0.getInf(); x++) {
            if (relation.isConsistent(x, y)) {
              resetNbSupport1(y);
              break;
            }
          }
        }
    } else {
      for (int y = offset2; y < v1.getInf(); y++) validSupport1.clear(y - offset2);
      for (int x = offset1; x < n1 + offset1; x++)  //offset2 si on voulait
        if (validSupport0.get(x - offset1)) {
          for (int y = offset2; y < v1.getInf(); y++) {
            if (relation.isConsistent(x, y)) {
              resetNbSupport0(x);
              break;
            }
          }
        }
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0) {
      for (int x = (v0.getSup() + 1); x < n1 + offset1; x++) validSupport0.clear(x - offset1);
      for (int y = offset2; y < n2 + offset2; y++)
        if (validSupport1.get(y - offset2)) {
          for (int x = (v0.getSup() + 1); x < n1 + offset1; x++) {
            if (relation.isConsistent(x, y)) {
              resetNbSupport1(y);
              break;
            }
          }
        }
    } else {
      for (int y = (v1.getSup() + 1); y < n2 + offset2; y++) validSupport1.clear(y - offset2);
      for (int x = offset1; x < n1 + offset1; x++)
        if (validSupport0.get(x - offset1)) {
          for (int y = (v1.getSup() + 1); y < n2 + offset2; y++) {
            if (relation.isConsistent(x, y)) {
              resetNbSupport0(x);
              break;
            }
          }
        }
    }
  }


  /**
   * Propagation when a variable is instantiated.
   *
   * @param idx The index of the variable.
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx == 0) {
      for (int x = offset1; x < n1 + offset1; x++)  // les bornes originelles du domaine
        if (x != v0.getVal()) validSupport0.clear(x - offset1);
      for (int y = offset2; y < n2 + offset2; y++)
        if (validSupport1.get(y - offset2) & !relation.isConsistent(v0.getVal(), y))
          v1.removeVal(y, cIdx1);
    } else {
      for (int y = offset2; y < n2 + offset2; y++)
        if (y != v1.getVal()) validSupport1.clear(y - offset2);
      for (int x = offset1; x < n1 + offset1; x++)
        if (validSupport0.get(x - offset1) & !relation.isConsistent(x, v1.getVal()))
          v0.removeVal(x, cIdx0);
    }
    //setPassive();
  }

  /**
   * Checks if all remaining values of x are supported by all values of y.
   */
  public Boolean isEntailed() {
    boolean always = true;
    IntIterator itv1 = v0.getDomain().getIterator();
    while (itv1.hasNext()) {
      if (nbSupport0[itv1.next()].get() == 0) {
        always = false;
      } else if (nbSupport0[itv1.next()].get() != v1.getDomainSize()) {
        return null;
      }
    }
    if (always)
      return Boolean.TRUE;
    else
      return Boolean.FALSE;
  }

  public AbstractConstraint opposite() {
    BinRelation rela2 = (BinRelation) ((ConsistencyRelation) relation).getOpposite();
    AbstractConstraint ct = new AC4BinConstraint(v0, v1, rela2);
    return ct;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("AC4(").append(v0.pretty()).append(", ").append(v1.pretty()).append(", ").
        append(this.relation.getClass().getSimpleName()).append(")");
    return sb.toString();
  }

}
