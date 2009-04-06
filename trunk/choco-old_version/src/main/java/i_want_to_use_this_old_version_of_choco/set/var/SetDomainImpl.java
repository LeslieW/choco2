package i_want_to_use_this_old_version_of_choco.set.var;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractEntity;
import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Time: 14:20:54
 */
public class SetDomainImpl extends AbstractEntity implements SetDomain {

	protected SetVar variable;
	/**
	 * the initial size of the domain (never increases)
	 */
	private int capacity;

	private BitSetEnumeratedDomain kernel;

	private BitSetEnumeratedDomain enveloppe;

	public SetDomainImpl(SetVar v, int a, int b) {
		variable = v;
		problem = v.getProblem();
		capacity = b - a + 1;           // number of entries
		kernel = new BitSetEnumeratedDomain(v, a, b, false);
		enveloppe = new BitSetEnumeratedDomain(v, a, b, true);

	}

	public BitSetEnumeratedDomain getKernelDomain() {
		return kernel;
	}

	public BitSetEnumeratedDomain getEnveloppeDomain() {
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
		IntIterator it = this.getEnveloppeIterator();
		while (it.hasNext()) {
			int val = it.next();
			count += 1;
			if (count > 1) buf.append(",");
			buf.append(val);
		}
		buf.append("], Ker[");
		count = 0;
		IntIterator it2 = this.getKernelIterator();
		while (it2.hasNext()) {
			int val = it2.next();
			count += 1;
			if (count > 1) buf.append(",");
			buf.append(val);
		}
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

	protected boolean remFromEnveloppe(int x, int idx) throws ContradictionException {
		if (_remFromEnveloppe(x, idx)) {
			if (isInstantiated())
				problem.getPropagationEngine().postInstSet(variable, SetVarEvent.NOCAUSE);
			else
				problem.getPropagationEngine().postRemEnv(variable, idx);
			return true;
		}
		return false;
	}

	// Si promotion, il faut annuler la cause
	protected boolean addToKernel(int x, int idx) throws ContradictionException {
		if (_addToKernel(x, idx)) {
			if (isInstantiated())
				problem.getPropagationEngine().postInstSet(variable, SetVarEvent.NOCAUSE);
			else
				problem.getPropagationEngine().postAddKer(variable, idx);
			return true;
		}
		return false;
	}

	protected boolean instantiate(int[] x, int idx) throws ContradictionException {
		if (_instantiate(x,idx)) {
			problem.getPropagationEngine().postInstSet(variable, idx);
			return true;
		} else
			return false;
	}

	// Si promotion, il faut annuler la cause
	protected boolean _remFromEnveloppe(int x, int idx) throws ContradictionException {
		if (kernel.contains(x)) {
			if (idx == -1)
				throw new ContradictionException(this.variable);
			else
				throw new ContradictionException((AbstractVar) this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx));
		} else if (enveloppe.contains(x)) {
			enveloppe.remove(x);
			return true;
		}
		return false;
	}

	// Si promotion, il faut annuler la cause
	protected boolean _addToKernel(int x, int idx) throws ContradictionException {
		if (!enveloppe.contains(x)) {
			if (idx == -1)
				throw new ContradictionException(this.variable);
			else
				throw new ContradictionException((AbstractVar) this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx));
		} else if (!kernel.contains(x)) {
			kernel.add(x);
			return true;
		}
		return false;
	}

	protected boolean _instantiate(int[] values, int idx) throws ContradictionException {
		if (isInstantiated()) {
			if (!isInstantiatedTo(values)) {
				if (idx == -1)
					throw new ContradictionException(this.variable);
				else
					throw new ContradictionException((AbstractVar) this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx));
			} else
				return true;
		} else {
			if (!canBeInstantiatedTo(values)) {
				if (idx == -1)
					throw new ContradictionException(this.variable);
				else
					throw new ContradictionException((AbstractVar) this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx));
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

	public IntIterator getKernelIterator() {
		return new SetDomainImpl.SetDomainIterator(this.kernel);
	}

	public IntIterator getEnveloppeIterator() {
		return new SetDomainImpl.SetDomainIterator(this.enveloppe);
	}

	protected class SetDomainIterator implements IntIterator {
		protected BitSetEnumeratedDomain domain;
		protected int currentValue = Integer.MIN_VALUE;

		private SetDomainIterator(BitSetEnumeratedDomain dom) {
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

	public IntIterator getOpenDomainIterator() {
		return new SetDomainImpl.SetOpenDomainIterator(this.enveloppe, this.kernel);
	}

	protected class SetOpenDomainIterator implements IntIterator {
		protected BitSetEnumeratedDomain envdomain;
		protected BitSetEnumeratedDomain kerdomain;
		protected int currentValue = Integer.MIN_VALUE;
		protected int nbValueToBeIterated = Integer.MAX_VALUE;

		private SetOpenDomainIterator(BitSetEnumeratedDomain dom1, BitSetEnumeratedDomain dom2) {
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
}
