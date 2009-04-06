package choco.visu.components.chart.labels;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.XYDataset;

public abstract class AbstractXYTaskGenerator extends
		AbstractXYItemLabelGenerator {

	private static final long serialVersionUID = -6976436429618157861L;

	public AbstractXYTaskGenerator(String formatString, NumberFormat format,
			NumberFormat format2) {
		super(formatString, format, format2);
	}

	public AbstractXYTaskGenerator(String formatString, DateFormat format,
			NumberFormat format2) {
		super(formatString, format, format2);
	}

	public AbstractXYTaskGenerator(String formatString, NumberFormat format,
			DateFormat format2) {
		super(formatString, format, format2);
	}

	public AbstractXYTaskGenerator(String formatString, DateFormat format,
			DateFormat format2) {
		super(formatString, format, format2);
	}

	protected static String format(double val, DateFormat dformat,
			NumberFormat format) {
		return dformat == null ? format.format(val) : dformat.format(new Date(
				(long) val));
	}

	protected String xformat(double val) {
		return format(val, getXDateFormat(), getXFormat());
	}

	protected String yformat(double val) {
		return format(val, getYDateFormat(), getYFormat());
	}

	@Override
	protected Object[] createItemArray(XYDataset dataset, int series, int item) {
		if (dataset instanceof XYTaskDataset) {
			Task t = ((XYTaskDataset) dataset).getTasks().getSeries(series)
					.get(item);
			Object[] result = new Object[4];
			result[0] = t.getDescription();
			result[1] = xformat(t.getDuration().getStart().getTime());
			double y = dataset.getYValue(series, item);
			if (Double.isNaN(y) && dataset.getY(series, item) == null) {
				result[2] = this.getNullYString();
			} else {
				result[2] = yformat(y);
			}
			result[3] = xformat(t.getDuration().getEnd().getTime());
			return result;
		} else {
			return super.createItemArray(dataset, series, item);
		}
	}

}
