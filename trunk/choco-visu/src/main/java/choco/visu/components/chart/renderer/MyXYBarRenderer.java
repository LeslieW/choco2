package choco.visu.components.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

/**
 * map a color with each key of the tasks set.
 *
 */
public class MyXYBarRenderer extends XYBarRenderer {

	public static enum ResourceRenderer  {ROW,COLUMN, COORD, SUPER}

	private static final long serialVersionUID = -5644242483289430988L;


	final private HashMap<Integer, Paint> imap = new HashMap<Integer, Paint>();
	
	final private HashMap<Point, Paint> pmap = new HashMap<Point, Paint>();

	protected final ResourceRenderer renderer;


	public MyXYBarRenderer(ResourceRenderer renderer) {
		super();
		this.renderer = renderer;
	}

	public MyXYBarRenderer(ResourceRenderer renderer,double margin) {
		super(margin);
		this.renderer = renderer;
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
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
	}

	protected final Paint getTaskPaint(int key) {
		final Integer k = Integer.valueOf(key);
		if( ! imap.containsKey(k)) {
			final Paint p = getDrawingSupplier().getNextPaint();
			imap.put(k, p);
			return p;
		}else {return imap.get(k);}
	}
	
	protected final Paint getTaskPaint(int row, int column) {
		final Point k = new Point(row, column);
		if( ! pmap.containsKey(k)) {
			final Paint p = getDrawingSupplier().getNextPaint();
			pmap.put(k, p);
			return p;
		}else {return pmap.get(k);}
	}

	@Override
	public Paint getItemPaint(int row, int column) {
		switch (renderer) {
		case ROW:return getTaskPaint(row);
		case COLUMN: return getTaskPaint(column);
		case COORD: return getTaskPaint(row, column);
		default:return super.getItemPaint(row, column);

		}
	}
}
