package choco.visu.components.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYSeries;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.pack.PrimalDualPack;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.pack.PackModeler;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.Limit;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;

public final class ChocoDatasetFactory {



	/** empty constructor */
	private ChocoDatasetFactory() {}


	//*****************************************************************//
	//*******************  Tasks  ********************************//
	//***************************************************************//
	public static Task createTask(ITask t) {
		return new Task(t.getName(),getTimePeriod(t.getEST(),t.getLCT()));
	}

	public static Task createTask(CPSolver s,TaskVariable t) {
		return createTask(s.getVar(t));
	}

//	public static Task createTask(BTask t) {
//		return new Task(t.toString(),getTimePeriod(t.startd,t.end));
//	}


	public static TimePeriod getTimePeriod(final long begin,final long end) {
		return new SimpleTimePeriod(new Date(begin), new Date(end));
	}

	public static TaskSeries createTaskSeries(IResource<TaskVar> rsc) {
		TaskSeries s = new TaskSeries(rsc.getRscName());
		for (int i = 0; i < rsc.getNbTasks(); i++) {
			s.add(createTask(rsc.getTask(i)));
		}
		return s;
	}
//	public static TaskSeriesCollection createJobsTaskCollection(BSolution bsol) {
//		final TaskSeriesCollection c = new TaskSeriesCollection();
//		for (int j = 0; j < bsol.nbJ; j++) {
//			final TaskSeries s = new TaskSeries("J"+j);
//			for (int i = 0; i < bsol.nbM; i++) {
//				s.add(createTask(bsol.tasks_mat[i][j]));
//			}
//			c.add(s);
//		}
//		return c;
//	}
//
//	public static TaskSeriesCollection createMachinesTaskCollection(BSolution bsol) {
//		final TaskSeriesCollection c = new TaskSeriesCollection();
//		for (int i = 0; i < bsol.nbM; i++) {
//			final TaskSeries s = new TaskSeries("M"+i);
//			for (int j = 0; j < bsol.nbJ; j++) {
//				s.add(createTask(bsol.tasks_mat[i][j]));
//			}	
//			c.add(s);
//		}
//		return c;
//	}
	public static TaskSeriesCollection createTaskCollection(IResource<TaskVar>... resources) {
		TaskSeriesCollection c = new TaskSeriesCollection();
		for (IResource<TaskVar> rsc : resources) {
			c.add(createTaskSeries(rsc));
		}
		return c;
	}
	
	@SuppressWarnings("unchecked")
	public static TaskSeriesCollection createTaskCollection(Solver s, Constraint... resources) {
		TaskSeriesCollection c = new TaskSeriesCollection();
		for (Constraint rsc : resources) {
			c.add(createTaskSeries((IResource<TaskVar>) s.getCstr(rsc)));
		}
		return c;
	}

	@SuppressWarnings("unchecked")
	public static TaskSeriesCollection createUnaryRscTaskCollection(Solver s) {
		final TaskSeriesCollection c = new TaskSeriesCollection();
		final Iterator<Constraint> iter = s.getModel().getConstraintByType(ConstraintType.DISJUNCTIVE);
		while(iter.hasNext()) {
			c.add(createTaskSeries((IResource<TaskVar>) s.getCstr(iter.next())));
		}
		return c;
	}

	

	protected static Integer[] createDates(ICumulativeResource<TaskVar> rsc) {
		final Set<Integer> dateSet = new HashSet<Integer>();
		Iterator<TaskVar> iter = rsc.getTaskIterator();
		while(iter.hasNext()) {
			TaskVar t = iter.next();
			dateSet.add(t.start().getVal());
			dateSet.add(t.end().getVal());
		}
		Integer[] res = dateSet.toArray(new Integer[dateSet.size()]);
		Arrays.sort(res);
		return res;
	}

	public static TimeTableXYDataset createCumulativeDataset(ICumulativeResource<TaskVar> rsc) {
		//create dates and time periods
		Integer[] dates = createDates(rsc);
		TimePeriod[] periods = new TimePeriod[dates.length-1];
		for (int i = 0; i < periods.length; i++) {
			periods[i] = new SimpleTimePeriod(dates[i],dates[i+1]);
		}
		final TimeTableXYDataset dataset = new TimeTableXYDataset();
		for (int i = 0; i < rsc.getNbTasks(); i++) {
			TaskVar t = rsc.getTask(i);
			int b = Arrays.binarySearch(dates, t.start().getVal());
			int e = Arrays.binarySearch(dates, t.end().getVal());
			int h = rsc.getHeight(i).getVal();
			for (int j = b; j < e; j++) {
				dataset.add(periods[j], h, t.getName());
			}
		}
		return dataset;
	}

	//*****************************************************************//
	//*******************  Pack  ********************************//
	//***************************************************************//
	
	
	public static CategoryDataset[] createPackDataset(String title, Solver s) {
		final int n = s.getModel().getNbConstraintByType(ConstraintType.PACK);
		Iterator<Constraint> cstr = s.getModel().getConstraintByType(ConstraintType.PACK);
		CategoryDataset[] datasets = new CategoryDataset[n];
		for (int i = 0; i < n; i++) {
			PrimalDualPack pack = (PrimalDualPack) s.getCstr(cstr.next());
			datasets[i] = createPackDataset(pack.getNbBins(), pack.getBins(), pack.getSizes());
		}
		return datasets;
	}
	
	public static CategoryDataset createPackDataset(int nbBins, IntDomainVar[] bins,IntDomainVar[] sizes) {
		DefaultCategoryDataset   dataset =   new   DefaultCategoryDataset();
		int[] series = new int[nbBins];
		for (int i = 0; i < bins.length; i++) {
			if(bins[i].isInstantiated()) {
				int b = bins[i].getVal();
				dataset.addValue(sizes[i].getVal(),   "Series "+series[b],   "B"+b);	
				series[b]++;
			}
		}
		return dataset;
	}

	public static CategoryDataset createPackDataset(Solver s,PackModeler modeler) {
		return createPackDataset(modeler.nbBins, s.getVar(modeler.bins), s.getVar(modeler.sizes));
	}

	//*****************************************************************//
	//*******************  Solver solutions  ********************************//
	//***************************************************************//

	public static XYSeries createSolutionXYSeries(CPSolver s, Limit limit) {
		XYSeries series = new XYSeries("solver sol.");
		final AbstractGlobalSearchStrategy strat = s.getSearchStrategy();
		if(strat.getLimit(limit) != null) {
				for (Solution sol : strat.getStoredSolutions()) {
					series.add(sol.getLimitValue(limit), sol.getObjectiveValue());
				}
			}
		return series;
	}

	public static CategoryDataset createSolutionCategoryDataset(CPSolver s, Limit limit) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		final AbstractGlobalSearchStrategy strat = s.getSearchStrategy();
		final String series = "Solver sol.";
		if(strat.getLimit(limit) != null) {
				//reversed loop
				final List<Solution> sols = strat.getStoredSolutions();
				for (int i = sols.size()-1; i >=0; i--) {
					final Solution sol = sols.get(i);
					dataset.addValue(sol.getObjectiveValue(), series, Integer.valueOf(sol.getLimitValue(limit)));
				}

		}
		return dataset;
	}

//	public static CategoryDataset createHeuristicsCategoryDataset(ListHeuristics heuristics) {
//		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//		List<HeuristicsEvent> func = heuristics.getEvents().getFunction();
//		final String series = "Heuristics sol.";
//		for (HeuristicsEvent evt : func) {
//			dataset.addValue(evt.getMakespan(), series, Integer.valueOf(evt.getCoordinate()));
//		}
//		return dataset;
//	}


	//*****************************************************************//
	//*******************  Linear function (YInterval) ***************//
	//***************************************************************//

//	public static YIntervalSeries createFunctionDataset(String keys, IPiecewiseLinearFunction<StatEvent> function) {
//		YIntervalSeries series = new YIntervalSeries(keys);
//		for (StatEvent evt : function.getFunction()) {
//			final double m = evt.getContribution();
//			final double std = evt.getContribStat().getStandardDeviation();
//			series.add(evt.getCoordinate()+1, m, m-std, m+std);
//		}
//		return series;
//	}

}


