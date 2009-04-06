// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;


public class GAC2001LargeConstraint extends CspLargeConstraint {

  // Last valid supports Last(x_i, val) = supports( (blocks(i) + val) * size )

  protected IStateInt[] supports;

  protected int[] blocks;

  // Cardinality
  protected int size;

  // offsets(i) = Min(x_i)
  protected int[] offsets;


  public GAC2001LargeConstraint(IntDomainVar[] vs, LargeRelation relation) {
    super(vs, relation);

    this.size = vs.length;
    this.blocks = new int[size];
    this.offsets = new int[size];

    int nbElt = 0;

    for (int i = 0; i < size; i++) {
      offsets[i] = vs[i].getInf();
      blocks[i] = nbElt;
      nbElt += vars[i].getSup() - vars[i].getInf() + 1;
    }

    this.supports = new IStateInt[nbElt * size];


    for (int i = 0; i < size; i++) {
      int domSize = vs[i].getDomainSize();
      for (int j = 0; j < domSize; j++) {
        for (int k = 0; k < size; k++) {
          if (k == i) {
            supports[(blocks[i] + j) * size + k] = this.getProblem().getEnvironment().makeInt(offsets[k] + j);
          } else {
            supports[(blocks[i] + j) * size + k] = this.getProblem().getEnvironment().makeInt(offsets[k]);
          }
        }
      }
    }


  }


  // updates the support for all values in the domain of variable
  // and remove unsupported values for variable
  public void reviseVar(int indexVar) throws ContradictionException {
    int[] currentSupport;
    IntIterator itvar = vars[indexVar].getDomain().getIterator();
    int val;

    while (itvar.hasNext()) {
      val = itvar.next();
      if (!isValid(lastSupport(indexVar, val))) {
        currentSupport = seekNextSupport(indexVar, val);

        if (currentSupport != null) {
          setSupport(indexVar, val, currentSupport);
        } else {
          vars[indexVar].removeVal(val, cIndices[indexVar]);
        }
      }
    }
  }

  // Store Last(x_i, val) = support
  public void setSupport(int indexVar, int value, int[] support) {
    for (int i = 0; i < vars.length; i++) {
      supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i].set(support[i]);
    }
  }

  // Get Last(x_i, val)
  public int[] getSupport(int indexVar, int value) {
    int[] resultat = new int[size];
    for (int i = 0; i < size; i++) {
      resultat[i] = supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i].get();
    }
    return resultat;
  }

  // Get Last(x_i, val)
  public int[] lastSupport(int indexVar, int value) {
    return getSupport(indexVar, value);
  }

  // Is tuple valide ?
  public boolean isValid(int[] tuple) {
    for (int i = 0; i < size; i++)
      if (!vars[i].canBeInstantiatedTo(tuple[i])) return false;
    return true;
  }

  // seek a new support for (variable, value), the smallest tuple greater than currentSupport
  public int[] seekNextSupport(int indexVar, int value) {
    int[] currentSupport;
    currentSupport = getSupport(indexVar, value);
    int k = 0;

    if (relation.isConsistent(currentSupport) && isValid(currentSupport)) {
      return currentSupport;
    }

    while (k < vars.length) {
      if (k == indexVar) k++;
      if (k < vars.length)
        if (!vars[k].getDomain().hasNextValue(currentSupport[k])) {
          currentSupport[k] = vars[k].getInf();
          k++;
        } else {
          currentSupport[k] = vars[k].getDomain().getNextValue(currentSupport[k]);

          if ((relation.isConsistent(currentSupport)) && isValid(currentSupport)) {
            return currentSupport;
          }
          k = 0;
        }
    }

    return null;
  }


  public void awake() throws ContradictionException {
    int[] currentSupport;
    int val;

    for (int indexVar = 0; indexVar < size; indexVar++) {
      IntIterator itv = vars[indexVar].getDomain().getIterator();

      while (itv.hasNext()) {
        val = itv.next();
        currentSupport = seekNextSupport(indexVar, val);
        if (currentSupport != null) {
          setSupport(indexVar, val, currentSupport);
        } else {
          vars[indexVar].removeVal(val, cIndices[indexVar]);
        }
      }
    }
  }


  public void propagate() throws ContradictionException {
    for (int i = 0; i < size; i++)
      reviseVar(i);
  }


  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    for (int i = 0; i < size; i++)
      if (idx != i) reviseVar(i);
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    for (int i = 0; i < size; i++)
      if (idx != i) reviseVar(i);
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    for (int i = 0; i < size; i++)
      if (idx != i) reviseVar(i);
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    for (int i = 0; i < size; i++)
      if (idx != i) reviseVar(i);
  }

  public void awakeOnBounds(int varIndex) throws ContradictionException {
    for (int i = 0; i < size; i++)
      if (varIndex != i) reviseVar(i);
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    for (int i = 0; i < size; i++)
      if (idx != i) reviseVar(i);
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("GAC2001Large({");
    for (int i = 0; i < vars.length; i++) {
      if (i > 0) sb.append(", ");
      IntDomainVar var = vars[i];
      sb.append(var.pretty());
    }
    sb.append("})");
    return sb.toString();
  }
}

