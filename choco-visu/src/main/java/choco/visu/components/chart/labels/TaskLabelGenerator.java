package choco.visu.components.chart.labels;

import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

public class TaskLabelGenerator extends AbstractXYTaskGenerator implements XYItemLabelGenerator {

	private static final long serialVersionUID = 7955578441394246380L;

	public TaskLabelGenerator() {
		this(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT);
	}
	
	public TaskLabelGenerator(String stringformat) {
		this(stringformat,NumberFormat.getNumberInstance(),
                NumberFormat.getNumberInstance());
	}

	public TaskLabelGenerator(String formatString, DateFormat format,
			DateFormat format2) {
		super(formatString, format, format2);
	}

	public TaskLabelGenerator(String formatString, DateFormat format,
			NumberFormat format2) {
		super(formatString, format, format2);
	}

	public TaskLabelGenerator(String formatString, NumberFormat format,
			DateFormat format2) {
		super(formatString, format, format2);
	}

	public TaskLabelGenerator(String formatString, NumberFormat format,
			NumberFormat format2) {
		super(formatString, format, format2);
	}

	@Override
	public String generateLabel(XYDataset dataset, int series, int item) {
		return generateLabelString(dataset, series, item);
	}
}