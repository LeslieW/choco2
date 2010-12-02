/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
