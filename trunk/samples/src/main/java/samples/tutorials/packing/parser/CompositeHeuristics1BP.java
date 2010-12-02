package samples.tutorials.packing.parser;

import gnu.trove.TIntArrayList;
import parser.instance.AbstractHeuristics;
import choco.kernel.common.opres.pack.AbstractHeuristic1BP;
import choco.kernel.common.opres.pack.BestFit1BP;
import choco.kernel.common.opres.pack.FirstFit1BP;

public class CompositeHeuristics1BP extends AbstractHeuristics {

	private final TIntArrayList sizes;
	private final AbstractHeuristic1BP ff, bf;

	public CompositeHeuristics1BP(BinPackingFileParser parser) {
		super();
		ff = new FirstFit1BP(parser.capacity);
		bf = new BestFit1BP(parser.capacity);
		this.sizes = new TIntArrayList(parser.sizes);
		this.sizes.sort();
	}


	@Override
	protected int apply() {
		return Math.min( ff.computeUB(sizes), bf.computeUB(sizes));
	}


	@Override
	public int getIterationCount() {
		return 2;
	}
	
	

}
