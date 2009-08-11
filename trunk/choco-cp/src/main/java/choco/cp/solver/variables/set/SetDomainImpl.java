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
package choco.cp.solver.variables.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.set.SetDomain;
import choco.kernel.solver.variables.set.SetSubDomain;
import choco.kernel.solver.variables.set.SetVar;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Time: 14:20:54
 */
public final class SetDomainImpl implements SetDomain {

    /**
     * The (optimization or decision) model to which the entity belongs.
     */

  public Solver solver;

  protected SetVar variable;
  /**
   * the initial size of the domain (never increases)
   */
  private int capacity;

  private BitSetEnumeratedDomain kernel;

  private BitSetEnumeratedDomain enveloppe;

//  protected SetDomainIterator lastIterator;

//    protected SetOpenDomainIterator openiterator;

  public SetDomainImpl(SetVar v, int a, int b) {
    variable = v;
    solver = v.getSolver();
    capacity = b - a + 1;           // number of entries
    kernel = new BitSetEnumeratedDomain(v, a, b, false);
    enveloppe = new BitSetEnumeratedDomain(v, a, b, true);

  }

  public SetDomainImpl(SetVar v, int[] sortedValues) {
    variable = v;
    solver = v.getSolver();
    capacity = sortedValues.length;           // number of entries
    kernel = new BitSetEnumeratedDomain(v, sortedValues, false);
    enveloppe = new BitSetEnumeratedDomain(v, sortedValues, true);
  }

    /**
     * Constructor of set var, allow creation of constant set var and empty set var.
     * @param v
     * @param sortedValues values of the set var. If null or lenght=0 => empty set
     * @param constant if true, build a constant set var
     */
  public SetDomainImpl(SetVar v, int[] sortedValues, boolean constant) {
      variable = v;
      solver = v.getSolver();
      capacity = sortedValues.length;           // number of entries
      if (sortedValues!=null && sortedValues.length > 0) {
          kernel = new BitSetEnumeratedDomain(v, sortedValues, constant);
          enveloppe = new BitSetEnumeratedDomain(v, sortedValues, true);
      } else {
          kernel = BitSetEnumeratedDomain.empty(v);
          enveloppe = BitSetEnumeratedDomain.empty(v);
      }
  }


  public SetSubDomain getKernelDomain() {
    return kernel;
  }

  public SetSubDomain getEnveloppeDomain() {
    return enveloppe;
  }

  public boolean addToKernel(int x) {
    kernel.add(x);
    return true;
  }

  public boolean isInstantiated() {
    return kernel.getSize() == enveloppe.getSize();
  }

  public boolean isInstantiatedTo(int[] setVal) {
    if (setVal.length == kernel.getSize() && setVal.length == enveloppe.getSize()) {
      for (int i = 0; i < setVal.length; i++) {
        if (!kernel.contains(setVal[i])) return false;
      }
      return true;
    } else
      return false;
  }

  //TODO : a bitset instead of a int[] ?
  public boolean canBeInstantiatedTo(int[] setVal) {
    if (kernel.getSize() <= setVal.length && enveloppe.getSize() >= setVal.length) {
      Arrays.sort(setVal);   // TODO : can we suppose that the table is sorted ?
      for (int i = 0; i < setVal.length; i++)
        if (!enveloppe.contains(setVal[i])) return false;
      for (int i = kernel.getFirstVal(); i >= 0; i = kernel.getNextValue(i))
        if (Arrays.binarySearch(setVal, i) < 0) return false;
      return true;
    } else
      return false;

  }

    public String toString() {
        StringBuffer buf = new StringBuffer("{Env[");
    int count = 0;
    DisposableIntIterator it = this.getEnveloppeIterator();
    while (it.hasNext()) {
      int val = it.next();
      count += 1;
      if (count > 1) buf.append(",");
      buf.append(val);
    }
    it.dispose();
    buf.append("], Ker[");
    count = 0;
    it = this.getKernelIterator();
    while (it.hasNext()) {
      int val = it.next();
      count += 1;
      if (count > 1) buf.append(",");
      buf.append(val);
    }
    it.dispose();
    buf.append("]}");
    return buf.toString();
    }

    public String pretty() {
        return toString();
  }
  // ============================================
  // methods for posting propagation events and
  // maintaining the domain.
  // ============================================

  // Si promotion, il faut annuler la cause
  public boolean remFromEnveloppe(int x, int idx) throws ContradictionException {
    if (_remFromEnveloppe(x, idx)) {
      if (isInstantiated())
        solver.getPropagationEngine().postInstSet(variable, SetVarEvent.NOCAUSE);
      else
        solver.getPropagationEngine().postRemEnv(variable, idx);
      return true;
    }
    return false;
  }

  // Si promotion, il faut annuler la cause
  public boolean addToKernel(int x, int idx) throws ContradictionException {
    if (_addToKernel(x,idx)) {
      if (isInstantiated())
        solver.getPropagationEngine().postInstSet(variable, SetVarEvent.NOCAUSE);
      else
        solver.getPropagationEngine().postAddKer(variable, idx);
      return true;
    }
    return false;
  }

  public boolean instantiate(int[] x, int idx) throws ContradictionException {
    if (_instantiate(x, idx)) {
      solver.getPropagationEngine().postInstSet(variable, idx);
      return true;
    } else
      return false;
  }

  // Si promotion, il faut annuler la cause
	protected boolean _remFromEnveloppe(int x, int idx) throws ContradictionException {
		if (kernel.contains(x)) {
			this.getSolver().getPropagationEngine().raiseContradiction(idx, variable);
            return true; // just for compilation
		} else if (enveloppe.contains(x)) {
			enveloppe.remove(x);
			return true;
		}
		return false;
	}

	// Si promotion, il faut annuler la cause
	protected boolean _addToKernel(int x, int idx) throws ContradictionException {
		if (!enveloppe.contains(x)) {
		    this.getSolver().getPropagationEngine().raiseContradiction(idx, variable);
      return true; // just for compilation
		} else if (!kernel.contains(x)) {
			kernel.add(x);
			return true;
		}
		return false;
	}

	protected boolean _instantiate(int[] values, int idx) throws ContradictionException {
		if (isInstantiated()) {
			if (!isInstantiatedTo(values)) {
				this.getSolver().getPropagationEngine().raiseContradiction(idx, variable);
                return true; // just for compilation
			} else
				return true;
		} else {
			if (!canBeInstantiatedTo(values)) {
				this.getSolver().getPropagationEngine().raiseContradiction(idx, variable);
                return true; // just for compilation
			} else {
				for (int i = 0; i < values.length; i++) // TODO: ajouter un restrict(int[] val) dans le BitSetEnumeratedDomain
					kernel.add(values[i]);
				for (int i = enveloppe.getFirstVal(); i >= 0; i = enveloppe.getNextValue(i))
					if (!kernel.contains(i)) enveloppe.remove(i);
				return true;
			}
		}
	}

  // ============================================
  // Iterators on kernel and enveloppe.
  // ============================================

    protected SetDomainIterator _cachedKernelIterator = null;

    public DisposableIntIterator getKernelIterator() {
      SetDomainIterator iter = _cachedKernelIterator;
      if (iter != null && iter.reusable) {
          iter.init(this.kernel);
          return iter;
      }
      _cachedKernelIterator = new SetDomainImpl.SetDomainIterator(this.kernel);
      return _cachedKernelIterator;
  }

    protected SetDomainIterator _cachedEnveloppeIterator = null;

    public DisposableIntIterator getEnveloppeIterator() {
      SetDomainIterator iter = _cachedEnveloppeIterator;
      if (iter != null && iter.reusable) {
          iter.init(this.enveloppe);
          return iter;
      }
      _cachedEnveloppeIterator = new SetDomainImpl.SetDomainIterator(this.enveloppe);
      return _cachedEnveloppeIterator;
  }

  protected static class SetDomainIterator extends DisposableIntIterator {
    protected BitSetEnumeratedDomain domain;
    protected int currentValue = Integer.MIN_VALUE;

    private SetDomainIterator(BitSetEnumeratedDomain dom) {
      init(dom);
    }

    public void init(BitSetEnumeratedDomain dom){
        super.init();
        domain = dom;
        currentValue = Integer.MIN_VALUE; // dom.getInf();
      }

    public boolean hasNext() {
      if (domain.getSize() == 0) return false;  // Le kernel peut �tre vide lui !
      return (Integer.MIN_VALUE == currentValue) ? true : (currentValue < domain.getLastVal());
    }

    public int next() {
      currentValue = (Integer.MIN_VALUE == currentValue) ? domain.getFirstVal() : domain.getNextValue(currentValue);
      return currentValue;
    }

    public void remove() {
      if (currentValue == Integer.MIN_VALUE) {
        throw new IllegalStateException();
      } else {
        throw new UnsupportedOperationException();
      }
    }
  }

  protected SetOpenDomainIterator _cachedIterator = null;

  public DisposableIntIterator getOpenDomainIterator() {
      SetOpenDomainIterator iter = _cachedIterator;
      if (iter != null && iter.reusable) {
          iter.init(this.enveloppe, this.kernel);
          return iter;
      }
      _cachedIterator = new SetDomainImpl.SetOpenDomainIterator(this.enveloppe, this.kernel);
      return _cachedIterator;
//      return new SetOpenDomainIterator(this.enveloppe, this.kernel);
  }

  protected static class SetOpenDomainIterator extends DisposableIntIterator {
    protected BitSetEnumeratedDomain envdomain;
    protected BitSetEnumeratedDomain kerdomain;
    protected int currentValue = Integer.MIN_VALUE;
    protected int nbValueToBeIterated = Integer.MAX_VALUE;

    private SetOpenDomainIterator(BitSetEnumeratedDomain dom1, BitSetEnumeratedDomain dom2) {
      init(dom1, dom2);
    }

      public void init(BitSetEnumeratedDomain dom1, BitSetEnumeratedDomain dom2){
          super.init();
          envdomain = dom1;
        kerdomain = dom2;
        currentValue = Integer.MIN_VALUE; // dom.getInf();
        nbValueToBeIterated = dom1.getSize() - dom2.getSize();
      }

    public boolean hasNext() {
      return nbValueToBeIterated > 0;
    }

    public int next() {
      int i = (currentValue == Integer.MIN_VALUE) ? envdomain.getFirstVal() : envdomain.getNextValue(currentValue);
      for (; i >= 0; i = envdomain.getNextValue(i))
        if (!kerdomain.contains(i)) {
          currentValue = i;
          break;
        }
      nbValueToBeIterated -= 1;
      return currentValue;
    }

    public void remove() {
      if (currentValue == Integer.MIN_VALUE) {
        throw new IllegalStateException();
      } else {
        throw new UnsupportedOperationException();
      }
    }
  }

     /**
   * Retrieves the solver of the entity
   */

  public Solver getSolver() {
    return solver;
  }

  public void setSolver(Solver solver) {
    this.solver = solver;
  }
}
