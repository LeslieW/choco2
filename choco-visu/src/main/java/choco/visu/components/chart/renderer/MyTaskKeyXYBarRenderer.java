package choco.visu.components.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.XYDataset;

/**
 * map a color with each key of the tasks set.
 *
 */
public class MyTaskKeyXYBarRenderer extends XYBarRenderer {

	private static final long serialVersionUID = -5644242483289430988L;

	final private HashMap<String, Paint> map = new HashMap<String, Paint>();

	private XYDataset tmp;

	public MyTaskKeyXYBarRenderer() {
		super();
	}

	public MyTaskKeyXYBarRenderer(double margin) {
		super(margin);
	}

	@Override
	public void drawItem(Graphics2D g2,
			XYItemRendererState state,
			Rectangle2D dataArea,
			PlotRenderingInfo info,
			XYPlot plot,
			ValueAxis domainAxis,
			ValueAxis rangeAxis,
			XYDataset dataset,
			int series,
			int item,
			CrosshairState crosshairState,
			int pass) {
		tmp =dataset;
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
	}



	@Override
	public Paint getItemPaint(int row, int column) {
		if (tmp instanceof XYTaskDataset) {
			final String des = ( (XYTaskDataset) tmp).getTasks().getSeries(row).get(column).getDescription();
			if( ! map.containsKey(des)) {map.put(des, getDrawingSupplier().getNextPaint());}
			return map.get(des);
		}else {
			return super.getItemPaint(row, column);
		}
		
	}
}
