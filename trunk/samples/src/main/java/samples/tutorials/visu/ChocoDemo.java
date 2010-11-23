package samples.tutorials.visu;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.pack.PackModel;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.visu.components.chart.ChocoChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import samples.tutorials.scheduling.pack.binpacking.BinPackingExample;
import samples.tutorials.scheduling.pack.binpacking.CPpack;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class ChocoDemo {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	private static final int TIME_LIMIT = 15;

	/** empty constructor */
	private ChocoDemo() {}


	public static void packDemo1() {
		CPpack cppack = new CPpack();
        cppack.setUp(new Object[]{BinPackingExample.N1C1W1_N,BinPackingExample.CAPACITY_N, BinPackingExample.OPT_N});
		cppack.setTimelimit(TIME_LIMIT);
		cppack.cpPack();
		final String title = "Bin Packing Constraint Visualization 1";
		ChocoDemo.demo(title, ChocoChartFactory.createPackChart(title, cppack.solver,cppack.getModeler()));
	}

	public static void packDemo2() {
		CPModel m =new CPModel();
		PackModel m1 = new PackModel(BinPackingExample.N1C1W1_N,BinPackingExample.OPT_N+2,BinPackingExample.CAPACITY_N);
		PackModel m2 = new PackModel(BinPackingExample.N2C2W1_H,BinPackingExample.OPT_H+4,BinPackingExample.CAPACITY);
		m.addConstraints( Choco.pack(m1), Choco.pack(m2));
		//m.addConstraints( Choco.pack(m1));
		CPSolver s =new CPSolver();
		s.read(m);
		s.setTimeLimit(TIME_LIMIT*1000);
		s.solve();
		final String title = "Bin Packing Constraint Visualization 2";
		demo(title,ChocoChartFactory.createPackChart(title, s,m1,m2));
	}



	public static void unaryDemo() {
//		ChocoshopSettings settings = new ChocoshopSettings();
//		settings.setBranching(ChocoshopSettings.Branching.ST);
//		settings.setHeuristics(ChocoshopSettings.Heuristics.LPT);
//		settings.setRestartPolicy(ChocoshopSettings.RestartPolicy.OFF);
//		settings.setTimeLimitCP(5);
//		//settings.setNoCP(true);
//		OpenShopProblem osp=new OpenShopProblem(new File("/home/nono/workspace/popart/data/open_shop/hard/GP06-01.txt"), settings, 0);
//		osp.setHeuristics(new ListHeuristics(osp,null));
//		osp.solve();
//		ShopUI.createApplicationFrame(osp, null,true);
//demo("Solver Unary Resources", ChocoChartFactory.createUnaryRscChart("Solver", osp.getSolver(), true));
	}

	public static void cumulativeDemo() {
		CPModel model = new CPModel();
		IntegerVariable[] duration = Choco.constantArray(new int[]{20,30,50,15,25,40,25,30});
		int[] height = new int[]{5,6,3,7,2,3,3,4};
		TaskVariable[] tasks = Choco.makeTaskVarArray("T", 0, 100, duration, Options.V_BOUND);
		Constraint rsc = Choco.cumulativeMax(tasks, height, 10);
		model.addConstraint( rsc);
		CPSolver s = new CPSolver();
		s.read(model);
		s.solve();
		s.printRuntimeStatistics();
		if(LOGGER.isLoggable(Level.INFO)) LOGGER.info(s.solutionToString());
		final String title = "Cumulative Resource Example";
		demo(title, ChocoChartFactory.createCumulativeChart(title, s, rsc, true));
		
	}

	public static void deviationDemo() {
			YIntervalSeriesCollection dataset = createDeviationDataset(10000, 10, new double[]{0.1,0.15,0.05});
			String title = "Deviation Example";
			demo(title, ChocoChartFactory.createDeviationLineChart(null, "X", "Y", dataset));
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
    
	public static void demo(String title,JFreeChart jfreechart) {
		demo(title, 700, 600, null, jfreechart);
	}

	public static void demo(String title,int width,int height,LayoutManager layout, JFreeChart...   jfreechart) {
		final ApplicationFrame frame = new ApplicationFrame(title);
		if(layout!=null) {frame.setLayout(layout);}
		final Dimension d = new   Dimension(width, height);
		for (JFreeChart chart : jfreechart) {
			ChartPanel   chartpanel   =   new   ChartPanel(chart);
			chartpanel.setPreferredSize(d);

			frame.add(chartpanel);
		}
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		packDemo1();
		//packDemo2();
		//unaryDemo();
		//deviationDemo();
//		cumulativeDemo();
	}
}
