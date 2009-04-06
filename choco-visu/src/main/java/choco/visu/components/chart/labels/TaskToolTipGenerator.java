package choco.visu.components.chart.labels;

import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

public class TaskToolTipGenerator extends AbstractXYTaskGenerator implements XYToolTipGenerator {

	

	private static final long serialVersionUID = -3185597210687549691L;

	public TaskToolTipGenerator() {
	        this(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT);
	    }
	 public TaskToolTipGenerator(String formatString) {
		 this(formatString,NumberFormat.getInstance(),NumberFormat.getInstance());
	 }
	public TaskToolTipGenerator(String formatString, NumberFormat format,
			NumberFormat format2) {
		super(formatString, format, format2);
			}

	public TaskToolTipGenerator(String formatString, DateFormat format,
			NumberFormat format2) {
		super(formatString, format, format2);
	}

	public TaskToolTipGenerator(String formatString, NumberFormat format,
			DateFormat format2) {
		super(formatString, format, format2);
	}

	public TaskToolTipGenerator(String formatString, DateFormat format,
			DateFormat format2) {
		super(formatString, format, format2);
	}

	@Override
	public String generateToolTip(XYDataset dataset, int series, int item) {
		 return generateLabelString(dataset, series, item);
	}
	
	

//	//Format = new Integer
//	@Override
//	public String generateLabelString(XYDataset dataset, int series, int item) {
//		if (dataset instanceof XYTaskDataset) {
//			Task t = ( (XYTaskDataset) dataset).getTasks().getSeries(series).get(item);
//			StringBuilder b =new StringBuilder();
//			b.append(t.getDescription()).append(": ");
//			b.append(this.getXDateFormat().format(t.getDuration().getStart()));
//			b.append("->");
//			b.append(this.getXDateFormat().format(t.getDuration().getEnd()));
//			return new String(b);
//		}else {
//			return super.generateLabelString(dataset, series, item);
//		}
//		
//	}

	
}
