package choco.visu.components.chart.labels;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;

public class CumulTaskToolTipGenerator implements XYToolTipGenerator {

	protected final ICumulativeResource<?> rsc;

	
	public CumulTaskToolTipGenerator(ICumulativeResource<?> rsc) {
		super();
		this.rsc = rsc;
	}

	@Override
	public String generateToolTip(XYDataset dataset, int series, int item) {
		return rsc.getTask(series).pretty()+" h="+rsc.getHeight(series).getVal();
	}
}
