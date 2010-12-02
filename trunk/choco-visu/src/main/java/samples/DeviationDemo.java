package samples;

import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import static choco.visu.components.chart.ChocoChartFactory.*;

public class DeviationDemo {

	public static void run() {
		YIntervalSeriesCollection dataset = createDeviationDataset(10000, 10, new double[]{0.1,0.15,0.05});
		createAndShowGUI("Deviation Example", createDeviationLineChart(null, "X", "Y", dataset));
	}
	
	private static YIntervalSeriesCollection createDeviationDataset(int length, int stdDiv, double coeffs[]) {
    	  YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
    	  for (int i = 0; i < coeffs.length; i++) {
    		  YIntervalSeries series = new YIntervalSeries("Series "+i);
    		  for (int c = 1; c < length; c++) {
    			  double m = 1.5 + coeffs[i]*Math.exp(1-0.01*c);
    			  double std = (m-1)/stdDiv;
				series.add(c, m, m-std, m+std);
			}
    		  dataset.addSeries(series);
		}
    	  return dataset;
    }
    
	public static void main(String[] args) {
		run();
	}
}
