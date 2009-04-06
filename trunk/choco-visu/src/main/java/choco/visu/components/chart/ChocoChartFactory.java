package choco.visu.components.chart;

import static choco.visu.components.chart.ChocoDatasetFactory.createPackDataset;
import static choco.visu.components.chart.ChocoDatasetFactory.createSolutionCategoryDataset;
import static choco.visu.components.chart.ChocoDatasetFactory.createUnaryRscTaskCollection;

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.pack.PackModeler;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.Limit;
import choco.visu.components.chart.axis.Log2Axis;
import choco.visu.components.chart.dataset.MyXYTaskDataset;
import choco.visu.components.chart.labels.TaskLabelGenerator;
import choco.visu.components.chart.labels.TaskToolTipGenerator;
import choco.visu.components.chart.renderer.MyTaskKeyXYBarRenderer;

public final class ChocoChartFactory {

	public final static ChartTheme CHOCO_THEME= createChocoChartTheme();

	public final static DateFormat INTEGER_DATE_FORMAT = new IntegerDateFormat();

	private ChocoChartFactory() {}

	protected static ChartTheme createChocoChartTheme() {
		StandardChartTheme theme = new StandardChartTheme("Choco");
		theme.setPlotBackgroundPaint(Color.white);
		theme.setDomainGridlinePaint(Color.black);
		theme.setRangeGridlinePaint(Color.black);
		theme.setXYBarPainter(new StandardXYBarPainter());
		theme.setShadowVisible(false);
		theme.setErrorIndicatorPaint(Color.RED);
		theme.setAxisOffset(new RectangleInsets());
		//		theme.setDrawingSupplier(new DefaultDrawingSupplier( (new ColorPalette(2,2)).getSequence(),
		//				DEFAULT_FILL_PAINT_SEQUENCE,
		//				DEFAULT_OUTLINE_PAINT_SEQUENCE,
		//				DEFAULT_STROKE_SEQUENCE,
		//				DEFAULT_OUTLINE_STROKE_SEQUENCE,
		//				DEFAULT_SHAPE_SEQUENCE)
		//		);
		return theme;
	}

	public static DateFormat getIntegerDateFormat() {
		return INTEGER_DATE_FORMAT;
	}
	protected final static Marker createMarker(int value, String label, Color color) {
		return createMarker(value, label, color, TextAnchor.BASELINE_LEFT, LengthAdjustmentType.EXPAND);
	}

	protected final static Marker createMarker(int value, String label, Color color, TextAnchor anchor, LengthAdjustmentType adjust) {
		final Marker lbMarker = new ValueMarker(value);
		if(label!=null) {
			lbMarker.setLabel(label);
			lbMarker.setLabelTextAnchor(anchor);
			lbMarker.setLabelOffsetType(adjust);
		}
		lbMarker.setPaint(color);
		return lbMarker;
	}

	//*****************************************************************//
	//*******************  Pack Visualization  ***********************//
	//***************************************************************//

	protected static void setPackRendererSettings(StackedBarRenderer3D   renderer) {
		renderer.setRenderAsPercentages(false);
		renderer.setDrawBarOutline(false);
		renderer.setBaseItemLabelGenerator(new   StandardCategoryItemLabelGenerator( "{2} ",   NumberFormat.getIntegerInstance()));
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
	}



	public static JFreeChart createPackChart(String title, Solver s,PackModeler modeler) {
		return createPackChart(title, createPackDataset(s,modeler), modeler.maxCapacity, false);
	}

	public static JFreeChart createPackChart(String title, Solver s,PackModeler... modelers) {
		final int n = modelers.length;
		CategoryDataset[] datasets = new CategoryDataset[n];
		int capa[] = new int[n];
		for (int i = 0; i < n; i++) {
			datasets[i] = createPackDataset(s,modelers[i]);
			capa[i] = modelers[i].maxCapacity;
		}
		return createPackChart(title, datasets, capa, false);
	}

	public static JFreeChart createPackChart(String title, Model m, Solver s) {
		final int n = m.getNbConstraintByType(ConstraintType.PACK);
		//TIntObjectIterator<Constraint> cstr = m.getConstraintByType(ConstraintType.PACK);
//		CategoryDataset[] datasets = new CategoryDataset[n];
//		for (int i = 0; i < n; i++) {
//			PrimalDualPack pack = (PrimalDualPack) s.getCstr(cstr.next());
//			datasets[i] = createPackDataset(pack.getNbBins(), pack.getBins(), pack.getSizes());
//		}
//		return createPackChart(title, datasets, null, false);
		return null;
	}

	public static JFreeChart createPackChart(String title, CategoryDataset dataset, int capacity, boolean legend) {
		final JFreeChart chart = null; //ChartFactory.createBarChart3D(title, "Bins","Load", dataset, PlotOrientation.VERTICAL, legend, false, false);
		final CategoryPlot plot = chart.getCategoryPlot();
		StackedBarRenderer3D   renderer   =   new StackedBarRenderer3D();
		setPackRendererSettings(renderer);
		plot.setRenderer(renderer);
		if(capacity>0) {
			Marker capaMarker = createMarker(capacity, "Capacity", Color.lightGray);
			plot.addRangeMarker(0, capaMarker, Layer.FOREGROUND);
		}
		CHOCO_THEME.apply(chart);
		plot.setDomainGridlinesVisible(false);
		return chart;

	}


	public static JFreeChart createPackChart(String title, CategoryDataset[] datasets, int[] capacity, boolean legend) {
		//Renderer
		StackedBarRenderer3D   renderer = new StackedBarRenderer3D();
		setPackRendererSettings(renderer);
		//Axis
		final NumberAxis laxis = new NumberAxis("Load");
		laxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		final CategoryAxis baxis = new CategoryAxis("Bins");
		//create and add subplots
		final CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(baxis);
		plot.setGap(10);
		for (int i = 0; i < datasets.length; i++) {
			final CategoryPlot subplot = new CategoryPlot(datasets[i],null, laxis,renderer);
			int w =1;
			if(capacity==null || capacity[i]>0) {
				Marker capaMarker = createMarker(capacity[i], "Capacity", Color.lightGray);
				plot.addRangeMarker(0, capaMarker, Layer.FOREGROUND);
				w =capacity[i];
			}
			plot.add(subplot, w);
		}
		JFreeChart chart = new JFreeChart(
				title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, legend
		);
		CHOCO_THEME.apply(chart);
		plot.setDomainGridlinesVisible(false);
		return chart;
	}


	//*****************************************************************//
	//*******************  Unary Resources   *************************//
	//***************************************************************//

	protected static String[] generateRscLabels(MyXYTaskDataset dataset) {
		String[] res = null;
		final TaskSeriesCollection coll = dataset.getTasks();
		if(dataset.isInverted()) {
			int nbi = 1;
			for (int i = 0; i < coll.getSeriesCount(); i++) {
				if(nbi<coll.getSeries(i).getItemCount()) {
					nbi = coll.getSeries(i).getItemCount();
				}
			}
			res =new String[nbi];
			for (int i = 0; i < nbi; i++) {
				res[i] = "rsc "+i;
			}	
		}else {
			final int nbs = dataset.getSeriesCount();
			res =new String[nbs];
			for (int i = 0; i < nbs; i++) {
				res[i] = coll.getSeries(i).getKey().toString();
			}
		}
		return res;

	}


	protected static void generateAxis(MyXYTaskDataset dataset,XYPlot plot, String[] rscAxisLabels, boolean intDate) {
		DateAxis daxis = new DateAxis("Date/Time");
		if(intDate) {daxis.setDateFormatOverride(INTEGER_DATE_FORMAT);}
		SymbolAxis raxis =  new SymbolAxis("Resources", 
				rscAxisLabels==null ? generateRscLabels(dataset) : rscAxisLabels) ;
		raxis.setGridBandsVisible(false);
		if(dataset.isTransposed()) {
			plot.setRangeAxis(raxis);
			plot.setDomainAxis(daxis);
		}else {
			plot.setRangeAxis(daxis);
			plot.setDomainAxis(raxis);
		}
	}

//FIXME move or remove method	
//	public static JFreeChart createUnaryRscChart(String title, BSolution bsol, boolean isMachineChart) {
//		final TaskSeriesCollection coll = isMachineChart ? createJobsTaskCollection(bsol) : createJobsTaskCollection(bsol);
//		final MyXYTaskDataset dataset = new MyXYTaskDataset(coll);
//		dataset.setTransposed(true);
//		dataset.setInverted(true);
//		return createUnaryRscChart(title, dataset, true, false, null);
//	}

	
	public static JFreeChart createUnaryRscChart(String title, CPSolver scheduler, boolean taskColor) {
		final TaskSeriesCollection coll = createUnaryRscTaskCollection(scheduler);
		final MyXYTaskDataset dataset = new MyXYTaskDataset(coll);
		dataset.setTransposed(true);
		dataset.setInverted(false);
		return createUnaryRscChart(title, dataset, true, taskColor, null);

	}



//	public static JFreeChart createUnaryRscChart(String title, GenericShopProblem shop, boolean isMachineChart) {
//		final TaskSeriesCollection coll = new TaskSeriesCollection();
//		//create axis labels
//		IResource<TaskVar>[] tmp =  isMachineChart ? shop.machines: shop.jobs;
//		String[] labs = null;
//		if(tmp!=null) {
//			labs = new String[tmp.length];
//			for (int i = 0; i < labs.length; i++) {
//				labs[i]= tmp[i].getRscName();
//			}
//		}
//		//create dataset
//		tmp =  isMachineChart ? shop.jobs : shop.machines;
//		final Scheduler s= (Scheduler) shop.getSolver();
//		for (UnaryResource rsc : tmp) {
//			coll.add(createTaskSeries( (UnarySResource) s.getCstr(rsc)));
//		}
//
//		final MyXYTaskDataset dataset = new MyXYTaskDataset(coll);
//		dataset.setTransposed(true);
//		dataset.setInverted(true);
//		return createUnaryRscChart(title, dataset, true, false, labs);
//		throw new UnsupportedOperationException("regression");
//	}

	public static JFreeChart createUnaryRscChart(String title,MyXYTaskDataset dataset,boolean intDate, boolean taskColor, String[] rscAxisLabels) {
		JFreeChart chart = ChartFactory.createXYBarChart(title,
				"Date/Time", true, "Resources", dataset, PlotOrientation.VERTICAL,
				!taskColor, false, false);

		XYPlot plot = chart.getXYPlot();
		generateAxis(dataset, plot, rscAxisLabels,intDate);
		//Renderer
		XYBarRenderer renderer = taskColor ? new MyTaskKeyXYBarRenderer() : new XYBarRenderer();
		renderer.setBaseItemLabelGenerator(new TaskLabelGenerator("{0}"));
		renderer.setBaseToolTipGenerator(new TaskToolTipGenerator("{0}: {1} -> {3}"));
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.BOTTOM_CENTER,TextAnchor.CENTER, 0.0), true);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setUseYInterval(true);
		//Plot
		plot.setRenderer(renderer);
		CHOCO_THEME.apply(chart);
		return chart;
	}


	//*****************************************************************//
	//*******************  Cumulative Resource ***********************//
	//***************************************************************//

	public static JFreeChart createCumulativeChart(String title, CPSolver s, boolean legend) {
		return null;
	}
	public static JFreeChart createCumulativeChart(String title, CPSolver s, Constraint rsc, boolean legend) {
		throw new UnsupportedOperationException("regression");
		//return createCumulativeChart(title, ChocoDatasetFactory.createCumulativeDataset(s, rsc),
		//		rsc.getCapacity().getLowB(), legend,INTEGER_DATE_FORMAT,new CumulTaskToolTipGenerator(s, rsc));
	}

	public static JFreeChart createCumulativeChart(String title, TableXYDataset dataset, int capacity,boolean legend, DateFormat format,XYToolTipGenerator tooltip) {
		DateAxis domainAxis = new DateAxis("Time");
		if(format!=null) {domainAxis.setDateFormatOverride(INTEGER_DATE_FORMAT);}
		domainAxis.setPositiveArrowVisible(true);
		//domainAxis.setLowerMargin(0);
		//domainAxis.setAutoRange(true);
		//domainAxis.setTickUnit(new DateTickUnit(DateTickUnit.MILLISECOND,5));		// Задаем отступ от графика
		NumberAxis rangeAxis = new NumberAxis("Load");
		//rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		rangeAxis.setTickUnit(new NumberTickUnit(1));
		rangeAxis.setPositiveArrowVisible(true);
		// Renderer
		StackedXYBarRenderer renderer = new StackedXYBarRenderer();
		renderer.setShadowVisible(false);
		if(tooltip==null) {
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: h={2}", new SimpleDateFormat(), NumberFormat.getInstance()));
		}else {
			renderer.setBaseToolTipGenerator(tooltip);
		}
		renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator("{0}",new SimpleDateFormat(), NumberFormat.getInstance()));
		renderer.setBaseItemLabelsVisible(true);
		// Plot
		XYPlot plot =  new XYPlot(dataset,domainAxis,rangeAxis,renderer);
		//Marker
		if(capacity>0) {
			Marker capaMarker = createMarker(capacity, "Capacity", Color.red);
			plot.addRangeMarker(0, capaMarker, Layer.FOREGROUND);
		}
		// Chart
		JFreeChart chart = new JFreeChart(title,plot);
		CHOCO_THEME.apply(chart);
		return chart;
	}


	//*****************************************************************//
	//*******************  Solution Charts  ********************************//
	//***************************************************************//




	private final static CategoryAxis createSolutionAxis(String name) {
		final CategoryAxis axis = new CategoryAxis(name);
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		return axis;
	}

	protected final static BarRenderer createSolutionBarRenderer() {
		final BarRenderer renderer = new BarRenderer();
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		final ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, Math.PI/2);
		renderer.setBasePositiveItemLabelPosition(p);
		renderer.setBaseItemLabelsVisible(true);
		return renderer;
	}


//	public static JFreeChart createHeuristicsChart(ListHeuristics heuritics) {
//		final JFreeChart chart = createBarChart("Heuristics Solutions", createHeuristicsCategoryDataset(heuritics), "Itérations", 0);
//		CategoryPlot plot =  chart.getCategoryPlot();
//		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//		CHOCO_THEME.apply(chart);
//		return chart;
//	}

	public static JFreeChart createSolutionChart(CPSolver s, Limit l) {
		final JFreeChart chart = createBarChart("Solver Solutions", createSolutionCategoryDataset(s, l), l.toString(), 0);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		CHOCO_THEME.apply(chart);
		return chart;
	}

//	public static JFreeChart createSolutionChart(GenericShopProblem shop, Limit limit) {
//		if (shop.getHeuristics() instanceof ListHeuristics) {
//			ListHeuristics h = (ListHeuristics) shop.getHeuristics();
//			if(h.hasSolution()) {
//				if(shop.getCPSearch().hasSolution()) {
//					final CategoryDataset[] datasets = { createHeuristicsCategoryDataset(h),createSolutionCategoryDataset((CPSolver) shop.getSolver(), limit)};
//					final String[] labels = {"Itération", limit.toString()};
//					return createCombinedBarChart("Solutions", datasets, labels, shop.getComputedLowerBound());
//
//				}else {
//					return createHeuristicsChart(h);
//				}
//			}
//		}
//		return createSolutionChart( (CPSolver) shop.getSolver(), limit);
//	}

	public static JFreeChart createBarChart(String title, CategoryDataset dataset,String categoryAxisLabel, int lowerBound) {
		JFreeChart result = ChartFactory.createBarChart(title, categoryAxisLabel, "Objective", dataset, 
				PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot plot =  result.getCategoryPlot();
		Marker marker = createMarker(lowerBound,"Lower Bound",Color.red,TextAnchor.CENTER_LEFT,LengthAdjustmentType.EXPAND);
		plot.addRangeMarker(0, marker, Layer.FOREGROUND);
		plot.setRenderer(createSolutionBarRenderer());
		CHOCO_THEME.apply(result);
		return result;
	}

	public static JFreeChart createCombinedBarChart(String title, CategoryDataset[] datasets,String[] categoryAxisLabel, int lowerBound) {
		final Marker lbMarker =  createMarker(lowerBound,"Lower Bound",Color.red,TextAnchor.CENTER_LEFT,LengthAdjustmentType.EXPAND);
		final CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(new NumberAxis("Objective"));
		plot.setOrientation(PlotOrientation.VERTICAL);
		for (int i = 0; i < datasets.length; i++) {
			final CategoryPlot subplot = new CategoryPlot(datasets[i], createSolutionAxis(categoryAxisLabel[i]), null, createSolutionBarRenderer());
			subplot.addRangeMarker(0, lbMarker, Layer.FOREGROUND);
			plot.add(subplot, datasets[i].getColumnCount());
		}
		JFreeChart chart = new JFreeChart(
				title,
				new Font("SansSerif", Font.BOLD, 12),
				plot,
				true
		);
		CHOCO_THEME.apply(chart);
		return chart;
	}

	//*****************************************************************//
	//*******************  Deviation Chart  **************************//
	//***************************************************************//

	public static JFreeChart createDeviationLineChart(String title,String xlabel, String ylabel, YIntervalSeriesCollection dataset) {
		DeviationRenderer renderer = new DeviationRenderer(true, false);
		LogAxis lxaxis = new LogAxis(xlabel);
		lxaxis.setLowerMargin(0);
		//FIXME lxaxis.setMinorTickMarksVisible(true);
		LogAxis lyaxis = new Log2Axis(ylabel);
		XYPlot plot = new XYPlot(dataset, lxaxis, lyaxis, renderer);
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,plot, true);
		CHOCO_THEME.apply(chart);
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			//renderer.setSeriesStroke(0, new BasicStroke(2f));
			Color color = (Color) renderer.lookupSeriesPaint(i);
			renderer.setSeriesFillPaint(i,color.brighter().brighter());
		}  
		return chart;
	}

}


final class IntegerDateFormat extends SimpleDateFormat {

	private static final long serialVersionUID = 9044123458512557258L;

	public IntegerDateFormat() {
		super();
		this.setNumberFormat(NumberFormat.getInstance());
	}


	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo,
			FieldPosition pos) {
		return numberFormat.format(date.getTime(), toAppendTo, pos);
	}


}
