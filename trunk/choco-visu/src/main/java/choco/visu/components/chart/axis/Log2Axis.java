package choco.visu.components.chart.axis;

import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;

public class Log2Axis extends LogAxis {

	private static final long serialVersionUID = -52483373074414880L;

	public Log2Axis(String label) {
		super(label);
		this.setBase(2);
		this.setLowerBound(1);
		this.setLowerMargin(0);
		setTickLabelsVisible(true);
		//FIXME this.setMinorTickMarksVisible(true);
		setAutoRange(true);
		setAutoTickUnitSelection(true);
		
		TickUnits units = (TickUnits) getStandardTickUnits();
        units.add(new NumberTickUnit(0.5));
        units.add(new NumberTickUnit(0.25));
        
	}

	@Override
	protected void autoAdjustRange() {
		Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }
        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }

            double upper = r.getUpperBound();
            double lower = Math.max(r.getLowerBound(), this.getSmallestValue());
            lower = Math.min(lower, getLowerBound()); // added code
            double range = upper - lower;

            // if fixed auto range, then derive lower bound...
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = Math.max(upper - fixedAutoRange, this.getSmallestValue());
            }
            else {
                // ensure the autorange is at least <minRange> in size...
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                }

                // apply the margins - these should apply to the exponent range
                double logUpper = calculateLog(upper);
                double logLower = calculateLog(lower);
                double logRange = logUpper - logLower;
                logUpper = logUpper + getUpperMargin() * logRange;
                logLower = logLower - getLowerMargin() * logRange;
                upper = calculateValue(logUpper);
                lower = calculateValue(logLower);
            }

            setRange(new Range(lower, upper), false, false);
        }
	}

	
	
	
	
}
