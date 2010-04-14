package choco.kernel.common.opres.pack;

public class BestFit1BP extends AbstractHeurisic1BP {

	public BestFit1BP(int capacity) {
		super(capacity);
	}

	@Override
	protected boolean handleInsertion(TLinkedBin bin) {
		if( bin.isFit(reuseSize) ) {
			reuseBin = bin;
			return false;
		} else if( reuseBin == null || 
				reuseBin.remainingArea > bin.remainingArea) {
			reuseBin = bin;
		}
		return true;
	}

}
