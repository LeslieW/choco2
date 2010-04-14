package choco.kernel.common.opres.pack;

public class FirstFit1BP extends AbstractHeurisic1BP {

	public FirstFit1BP(int capacity) {
		super(capacity);
	}

	@Override
	protected boolean handleInsertion(TLinkedBin bin) {
		reuseBin = bin;
		return false;
	}

}
