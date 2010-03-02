package choco.kernel.common.opres.nosum;

import gnu.trove.TLinkableAdapter;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class NoSumCell extends TLinkableAdapter implements INoSumCell {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6178195062253959771L;

	private final int id;
	
	private final int val;
	
	public NoSumCell(int id, IntDomainVar constant) {
		this(id, constant.getVal());
	}

	public NoSumCell(int id, int val) {
		super();
		this.id = id;
		this.val = val;
	}

	public final int getID() {
		return id;
	}

	public final int getVal() {
		return val;
	}
	
	
}
