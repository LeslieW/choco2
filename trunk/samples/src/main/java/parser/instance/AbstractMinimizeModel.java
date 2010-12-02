/**
 *
 */
package parser.instance;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.io.FilenameUtils;
import org.jfree.chart.JFreeChart;

import parser.instances.AbstractInstanceModel;
import parser.instances.BasicSettings;
import parser.instances.InstanceFileParser;
import parser.instances.ResolutionStatus;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.ResolutionPolicy;
import choco.kernel.solver.Solver;
import choco.visu.components.chart.ChocoChartFactory;
import choco.visu.components.chart.PdfExport;

/**
 * @author Arnaud Malapert
 *
 */
public abstract class AbstractMinimizeModel extends AbstractInstanceModel {

	protected IHeuristicAlgorithm heuristics = IHeuristicAlgorithm.SINGLOTON;

	protected int computedLowerBound;

	public AbstractMinimizeModel(InstanceFileParser parser, Configuration settings) {
		super(parser, settings);
		settings.putEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.MINIMIZE);
		settings.putFalse(Configuration.STOP_AT_FIRST_SOLUTION);
	}

	public final void cancelHeuristics() {
		heuristics = IHeuristicAlgorithm.SINGLOTON;
	}

	@Override
	public final String getInstanceName() {
		if( parser == null || parser.getInstanceFile() == null) return "UNKNOWN";
		else {
			return FilenameUtils.removeExtension(parser.getInstanceFile().getName());
		}
	}


	public final IHeuristicAlgorithm getHeuristics() {
		return heuristics;
	}

	public final void setHeuristics(IHeuristicAlgorithm heuristics) {
		this.heuristics = heuristics;
	}

	public int getComputedLowerBound() {
		return computedLowerBound;
	}

	public final void setComputedLowerBound(int computedLowerBound) {
		this.computedLowerBound = computedLowerBound;
	}



	@Override
	public void initialize() {
		super.initialize();
		computedLowerBound = Integer.MIN_VALUE;
	}

	@Override
	public Boolean preprocess() {
		if(heuristics != null && 
				defaultConf.readBoolean(BasicSettings.PREPROCESSING_HEURISTICS)) {
			heuristics.execute();
			if( heuristics.existsSolution()) {
				objective = heuristics.getObjectiveValue();
				return Boolean.TRUE;
			}
		}
		return null;
	}



	@Override
	protected void logOnDiagnostics() {
		super.logOnDiagnostics();
		if(heuristics != null && heuristics.hasSearched()) {
			logMsg.storeDiagnostic("HEUR_TIME", heuristics.getTimeCount());
			logMsg.storeDiagnostic("HEUR_ITERATION", heuristics.getIterationCount());
		}
	}


	@Override
	public ResolutionStatus postAnalyzePP() {
		//register diagnostics and config
		final ResolutionStatus r = super.postAnalyzePP();
		if( r == ResolutionStatus.SAT 
				&& objective.intValue() == computedLowerBound ) {
			return ResolutionStatus.OPTIMUM;
		}
		return r;
	}


	@Override
	public Solver buildSolver() {
		CPSolver solver = new CPSolver(this.defaultConf);
		BasicSettings.updateTimeLimit(solver.getConfiguration(),  - getPreProcTime());
		return solver;
	}


	@Override
	public Boolean solve() {
		solver.launch();
		return solver.isFeasible();
	}

	private final static String PDF_SUFFIX = ".pdf";

	private final File createChartFile() {
		final File dir = getOutputDirectory();
		File r = new File(dir, getInstanceName()+PDF_SUFFIX);
		if( r.exists()) {
			try {
				r = File.createTempFile(getInstanceName(), PDF_SUFFIX, dir);
			} catch (IOException e) {
				r = null;
			}
		}
		return r;
	}
	
	public abstract JFreeChart makeSolutionChart();

	@Override
	public void makeReports() {
		super.makeReports();
		if( defaultConf.readBoolean(BasicSettings.SOLUTION_REPORT) ) {
			JFreeChart chart = makeSolutionChart();
			if( chart == null) {
				LOGGER.config("chart...[drawChart][FAIL]");
			}else {
				if( defaultConf.readBoolean(BasicSettings.SOLUTION_EXPORT)) {
					//export chart
					final File pdf = createChartFile();
					if( pdf == null) {
						LOGGER.config("chart...[pdfExport:createFile][FAIL]");
					}else {
						try {
							PdfExport.saveChartAsPDF(pdf, chart, 800, 600);
							//PdfExport.saveChartAsPDF(pdf, chart, 800, 200);
							LOGGER.log(Level.CONFIG, "chart...[pdfExport:{0}][OK]", pdf);
						} catch (IOException e) {
							LOGGER.log(Level.WARNING, "chart...[pdfExport:{0}][FAIL]", e);
						}
					}
				} else {
					ChocoChartFactory.createAndShowGUI(getClass().getSimpleName(), chart);
					LOGGER.config("chart...[visu][OK]");
				}
			}	
		}
	}

}
