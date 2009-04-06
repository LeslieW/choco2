package i_want_to_use_this_old_version_of_choco.set.var;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 */
public class SetVarImpl extends AbstractVar implements SetVar {

	protected SetDomainImpl domain;

	protected IntDomainVar card;

	public SetVarImpl(AbstractProblem pb, String name, int a, int b, boolean enumcard) {
		super(pb, name);
		this.domain = new SetDomainImpl(this, a, b);
		this.event = new SetVarEvent(this);
		if (enumcard)
			this.card = pb.makeEnumIntVar(name, 0, b - a + 1);
		else this.card = pb.makeBoundIntVar("|"+name+"|", 0, b - a + 1);
	}


	public IntDomainVar getCard() {
		return card;
	}

	/**
	 * CPRU 07/12/2007: DomOverFailureDeg implementation
	 * Add:
	 * - call of super.fail()
	 * - call of raiseContradiction(this)
	 * - comment fail() initial
	 *
	 * @throws ContradictionException
	 */
	public void fail() throws ContradictionException {
		super.fail();
		problem.getPropagationEngine().raiseContradiction(this);
		//this.fail();
	}

	public boolean isInstantiated() {
		return domain.isInstantiated();  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void setValIn(int x) throws ContradictionException {
		addToKernel(x, SetVarEvent.NOCAUSE);
	}

	public void setValOut(int x) throws ContradictionException {
		remFromEnveloppe(x, SetVarEvent.NOCAUSE);
	}

	public boolean isInDomainKernel(int x) {
		return domain.getKernelDomain().contains(x);
	}

	public boolean isInDomainEnveloppe(int x) {
		return domain.getEnveloppeDomain().contains(x);
	}

	public SetDomain getDomain() {
		return domain;
	}

	/**
	 * Check if the both domain intersects
	 *
	 * @param x SetVar to be checked with
	 * @return a boolean
	 */
	public boolean canBeEqualTo(SetVar x) {
		return false;
	}

	public int getKernelDomainSize() {
		return domain.getKernelDomain().getSize();
	}

	public int getEnveloppeDomainSize() {
		return domain.getEnveloppeDomain().getSize();
	}

	public int getEnveloppeInf() {
		return domain.getEnveloppeDomain().getFirstVal();
	}

	public int getEnveloppeSup() {
		return domain.getEnveloppeDomain().getLastVal();
	}

	public int getKernelInf() {
		return domain.getKernelDomain().getFirstVal();
	}

	public int getKernelSup() {
		return domain.getKernelDomain().getLastVal();
	}

	public int[] getValue() {
		int[] val = new int[getKernelDomainSize()];
		IntIterator it = domain.getKernelIterator();
		int i = 0;
		while (it.hasNext()) {
			val[i] = it.next();
			i++;
		}
		return val;
	}

	public void setVal(int[] val) throws ContradictionException {
		instantiate(val, SetVarEvent.NOCAUSE);
	}

	public boolean addToKernel(int x, int idx) throws ContradictionException {
		return domain.addToKernel(x, idx);
	}

	public boolean remFromEnveloppe(int x, int idx) throws ContradictionException {
		return domain.remFromEnveloppe(x, idx);
	}

	public boolean instantiate(int[] x, int idx) throws ContradictionException {
		return domain.instantiate(x, idx);
	}

	/**
	 * pretty printing
	 *
	 * @return a String representation of the variable
	 */
	public String pretty() {
		return this.toString();
	}

	public String toString() {
		return this.name + " " + this.domain.toString();
	}
}
