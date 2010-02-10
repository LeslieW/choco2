/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package parser.instances;

import choco.cp.solver.CPSolver;
import choco.cp.solver.configure.RestartConfiguration;
import choco.cp.solver.constraints.integer.bool.sat.ClauseStore;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.checker.SolutionCheckerException;
import choco.kernel.solver.search.measure.IMeasures;
import db.DbManager;
import db.DbTables;
import parser.absconparseur.tools.UnsupportedConstraintException;
import static parser.instances.ResolutionStatus.*;

import java.io.File;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to provide facilities for loading and solving instance described by a file (txt, xml, ...). </br>
 */
public abstract class AbstractInstanceModel {

	public final static Logger LOGGER = ChocoLogging.getMainLogger();

	//configuration
	private long seed;

	protected Boolean doMaximize;


	//computed fields
	private final long[] time = new long[6];

	private Boolean isFeasible;

	private Number initialObjective;

	protected  Number objective;

	private ResolutionStatus status;

	//model
	protected final InstanceFileParser parser;

	protected Model model;

	protected Solver solver;

	//logs and reporting	

	protected File outputDirectory;

	protected DbManager dbManager;

	protected final ReportFormatter logMsg = new ReportFormatter();

	public AbstractInstanceModel(InstanceFileParser parser) {
		super();
		this.parser = parser;

	}

	public void initialize() {
		parser.cleanup();
		Arrays.fill(time, 0);
		isFeasible = null;
		status = ERROR;
		if(model != null) model.freeMemory();
        model = null;
		if(solver != null) solver.freeMemory();
		solver = null;
		initialObjective = null;
		objective = null;
		logMsg.reset();
	}

	//*****************************************************************//
	//*******************  Getters/Setters ***************************//
	//***************************************************************//
	public final Boolean isFeasible() {
		return isFeasible;
	}

	public final ResolutionStatus getStatus() {
		return status;
	}

	public final Number getInitialObjectiveValue() {
		return initialObjective;
	}

	public final Number getObjectiveValue() {
		return objective;
	}

	public final File getOutputDirectory() {
		return outputDirectory;
	}

	public final void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public final Boolean getDoMaximise() {
		return doMaximize;
	}

	public final void setDoMaximise(Boolean doMaximise) {
		this.doMaximize = doMaximise;
	}

	public final long getSeed() {
		return seed;
	}

	public final void setSeed(long seed) {
		this.seed = seed;
	}


	public final boolean isDatabaseReporting() {
		return dbManager != null;
	}

	public final DbManager getDatabaseManager() {
		return dbManager;
	}

	public final void setDatabaseManager(DbManager dbManager) {
		this.dbManager = dbManager;
	}

	public final InstanceFileParser getParser() {
		return parser;
	}

	public final Model getModel() {
		return model;
	}

	public final Solver getSolver() {
		return solver;
	}


	protected final void setObjective(Number objective) {
		this.objective = objective;
	}

	//*****************************************************************//
	//*******************  Logging  **********************************//
	//***************************************************************//




	private final static String INSTANCE_MSG="========================================================\nTreatment of: {0}";

	private final static String DESCR_MSG="{0}...dim:[nbv:{1}][nbc:{2}][nbconstants:{3}]";


	private final void logOnModel() {
		if(LOGGER.isLoggable(Level.CONFIG)) {
			if(model == null) LOGGER.log(Level.CONFIG, "model...[null]");
			else {
				LOGGER.config(MessageFormat.format(DESCR_MSG, "model", model.getNbIntVars(), model.getNbConstraints(), model.getNbConstantVars()));
				if(LOGGER.isLoggable(Level.FINEST)) {
					LOGGER.fine(model.pretty());
				}
			}
		}
	}

	private final void logOnSolver() {
		if(LOGGER.isLoggable(Level.CONFIG)) {
			if(solver == null) LOGGER.log(Level.CONFIG, "solver...[null]");
			else {
				LOGGER.config(MessageFormat.format(DESCR_MSG, "solver", solver.getNbIntVars(), solver.getNbIntConstraints(), solver.getNbConstants()));
				if(LOGGER.isLoggable(Level.FINER)) {
					LOGGER.fine(solver.pretty());
				}
			}
		}
	}

	private final void logOnPP() {
		if(LOGGER.isLoggable(Level.CONFIG)) {
			if( isFeasible == Boolean.TRUE && doMaximize != null) {
				LOGGER.log(Level.CONFIG, "preprocessing...[status:{0}][obj:{1}]", new Object[]{status, objective});
			}else {
				LOGGER.log(Level.CONFIG, "preprocessing...[status:{0}]", status);
			}
		}
	}

	private void logOnError(ResolutionStatus error, Exception e) {
		LOGGER.log(Level.INFO, "s {0}", error.getName());
		if(e != null) {
			LOGGER.log(Level.CONFIG, parser.getInstanceFile().getName()+"...[FAIL]", e);
		}
		status = error;
		isFeasible = null;
	}

	//*****************************************************************//
	//*******************    ********************************//
	//***************************************************************//
	/**
	 * Solve the csp given by file {@code file}
	 *
	 * @param file instance file to solve
	 */
	public final void solveFile(File file) {
		initialize();
		try {
			LOGGER.log(Level.CONFIG, INSTANCE_MSG, file.getName());
			boolean isLoaded = false;
			time[0] = System.currentTimeMillis();
			try {
				load(file);
				isLoaded = true;
			} catch (UnsupportedConstraintException e) {
				Arrays.fill(time, 1, time.length, time[0]);
				logOnError(UNSUPPORTED, e);
			} 
			if( isLoaded) {
				LOGGER.config("loading...[OK]");
				time[1] = System.currentTimeMillis();
				isFeasible = preprocess();
				status = postAnalyzePP();
				initialObjective = objective; 
				logOnPP();
				time[2] = System.currentTimeMillis();
				if( status == UNKNOWN || ( doMaximize != null && status == SAT) ) {
					//try to solve the problem using CP.
					model = buildModel();
					logOnModel();
					time[3] = System.currentTimeMillis();
					solver = buildSolver();
					logOnSolver();
					time[4] = System.currentTimeMillis();
					//isFeasible is either null or TRUE;
					if( isFeasible == Boolean.TRUE) solve();
					else isFeasible = solve();
					time[5] = System.currentTimeMillis();
					status = postAnalyzeCP();
				}else {
					//preprocess is enough to determine the instance status
					Arrays.fill(time, 3, time.length, time[2]);
				}
				//check the solution, if any
				if( isFeasible == Boolean.TRUE) {
					checkSolution();
					LOGGER.config("checker...[OK]");
				}
				//reporting
				makeReports();
			}

		} catch (Exception e) {
			logOnError(ERROR, e);
		} 
		ChocoLogging.flushLogs();
	}


	/**
	 * Parse the xml and return the parser object (Christophe parser) which
	 * can be used to access variables, constraints, etc...
	 *
	 * @param fichier
	 * @return A parser object containing the description of the problem
	 * @throws Exception
	 * @throws Error
	 */
	public void load(File fichier) throws UnsupportedConstraintException {
		parser.loadInstance(fichier);
		parser.parse(false);
	}


	/**
	 * Executes preprocessing ( bounding, heuristics ...)
	 * default implementation: do nothing.
	 * @return <code>true</code> if a solution has been found, <code>false</code> if the infeasibility has been proven and <code>null</code> otherwise. 
	 */
	public abstract Boolean preprocess();

	/**
	 * create the choco model after the preprocessing phase.
	 */
	public abstract Model buildModel();

	/**
	 * create a solver from the current model
	 */
	public abstract Solver buildSolver();

	/**
	 * configure and launch the resolution.
	 */
	public abstract Boolean solve();


	protected void checkIsSatisfied() throws SolutionCheckerException {
		//check with isSatisfied(int[])
		if(solver != null && solver.existsSolution() && solver instanceof CPSolver &&
				( ! ( (CPSolver) solver).checkSolution(false) == Boolean.TRUE)) {
			throw new SolutionCheckerException("isSatisfied(int[]) checker");
		}
	}


	/**
	 * The method checks the validity of the solution. 
	 * The default implementation only uses the embedded checker.
	 * So, the solution is not validated by an external program.
	 * @return <code>true</code> if the solution is valid, <code>false</code> otherwise.
	 */
	public void checkSolution() throws SolutionCheckerException {
		//FIXME checkIsSatisfied(); checks les clauses
	}


	/**
	 * compute the resolution status after the preprocessing stage (no solver build yet).
	 */
	public ResolutionStatus postAnalyzePP() {
		if( isFeasible == Boolean.TRUE) return SAT;
		else if( isFeasible == Boolean.FALSE) return UNSAT;
		else return UNKNOWN;
	}

	/**
	 * compute the resolution status after the cp search (solver is not null).
	 */
	public ResolutionStatus postAnalyzeCP() {
		if( isFeasible == Boolean.TRUE) {
			if( solver.isOptimizationSolver()) { //deal with optimization
				if( solver.existsSolution()) {
					//cp find new solution(s)
					objective = solver.getObjectiveValue(); //update objective value
					return solver.getFirstSolution() || solver.isEncounteredLimit() ? SAT : OPTIMUM;
				}else {
					//cp did not find any solution
					return solver.isEncounteredLimit() ? SAT : OPTIMUM;
				}
			}else return SAT; //deal with CSP
		}	
		else if ( isFeasible == Boolean.FALSE) return UNSAT;
		else if (solver == null) return ERROR; 
		else return solver.isEncounteredLimit() ? TIMEOUT : UNKNOWN;
	}


	protected void logOnDiagnostics() {
		logMsg.appendDiagnostic("RUNTIME", getFullSecTime());
		//objective
		if( objective != null) {
			logMsg.appendDiagnostic("OBJECTIVE", objective);
			if(initialObjective != null) logMsg.appendDiagnostic("INITIAL_OBJECTIVE", initialObjective);
		}
		//measures
		if(solver != null) {
			logMsg.appendDiagnostic("NBSOLS ", solver.getSolutionCount());
			final double rtime = getFullSecTime();
			logMsg.appendDiagnostics("NODES", solver.getNodeCount(), rtime);
			logMsg.appendDiagnostics("BACKTRACKS", solver.getBackTrackCount(), rtime);
			logMsg.appendDiagnostics("RESTARTS", solver.getRestartCount(), rtime);
			//best solution
			if(solver.existsSolution() && doMaximize != null) {
				final IMeasures mes = solver.getSearchStrategy().getSolutionPool().getBestSolution().getMeasures();
				logMsg.appendDiagnostic("BESTSOLTIME", mes.getTimeCount());
				logMsg.appendDiagnostic("BESTSOLBACKTRACKS", mes.getBackTrackCount());
			}
			//nogood
			if (solver instanceof CPSolver) {
				final ClauseStore ngs = ( (CPSolver) solver).nogoodStore;
				if( ngs != null) logMsg.storeDiagnostic("NBNOGOODS", ngs.getNbClause());
			}
		}
	}

	protected void logOnConfiguration() {
		logMsg.appendConfiguration( createInstanceConfiguration() );
		logMsg.storeConfiguration( createTimeConfiguration() );
		if (solver != null && solver instanceof CPSolver) {
			final String str = ( (CPSolver) solver).getRestartConfiguration().pretty();
			if( str != RestartConfiguration.NO_RESTART) { 
				logMsg.storeConfiguration(str);
			}
		}
	}


	public void makeReports() {
		consoleReport();
		if( isDatabaseReporting()) databaseReport();
	}

	/**
	 * the default console report as described in http://www.cril.univ-artois.fr/CPAI09/call2009/call2009.html#SECTION00080000000000000000
	 */
	public void consoleReport() {
		if(LOGGER.isLoggable(Level.INFO)) {
			//status s
			logMsg.appendStatus(status.getName());
			//solution v
			if( isFeasible == Boolean.TRUE) {
				//display best solution
				logMsg.appendValues(getValuesMessage());
			}
			//diagnostics and configuration d/c 
			logOnDiagnostics();
			logOnConfiguration();
			LOGGER.info(logMsg.getLoggingMessage());
		}
	}

	/**
	 * connect to a embedded, local or remote database and add an entry associated with the current resolution.
	 * Method does not commit changes.
	 */
	public void databaseReport() {
		//insert solver
		Integer solverID = dbManager.insertEntryAndRetrieveGPK(DbTables.T_SOLVERS, 
				new Object[]{ parser.getInstanceFile().getName(), status.getName(), getFullSecTime(), "", //getValuesMessage(),
				dbManager.getModelID(solver), dbManager.getEnvironmentID(), seed, new Timestamp(System.currentTimeMillis())}
		);
		//TODO remettre getModelID en protected quand dans choco
		Integer measuresID;
		if( solver != null) {
			//insert measures
			if(solver.existsSolution()) {
				for (Solution sol : solver.getSearchStrategy().getStoredSolutions()) {
					dbManager.insertMeasures(solverID, sol.getMeasures());
				}
			}
			measuresID = dbManager.insertEntryAndRetrieveGPK(DbTables.T_MEASURES, 
					solver.getSolutionCount(), objective, solver.getTimeCount(), 
					solver.getNodeCount(), solver.getBackTrackCount(), 
					solver.getFailCount(), solver.getRestartCount()
			);
		} else {
			measuresID = dbManager.insertEntryAndRetrieveGPK(DbTables.T_MEASURES, 0, objective, 0, 0, 0, 0, 0);
		}
		dbManager.jdbcTemplate.update(DbTables.T_LIMITS.createInsertQuery(false), new Object[] { measuresID, solverID});
		for (String c: logMsg.getDbInformations()) {
			dbManager.insertConfiguration(solverID, c);
		}
	}


	//*****************************************************************//
	//*******************  Console Report Utilities ******************//
	//***************************************************************//


	public String getValuesMessage() {
		if( solver != null && solver.existsSolution()) return solver.solutionToString();
		else return "";
	}

	protected String createInstanceConfiguration() {
		final StringBuilder b = new StringBuilder();
		if( doMaximize == Boolean.TRUE) b.append("MAXIMIZE    ");	
		else if( doMaximize == Boolean.FALSE) b.append("MINIMIZE    ");
		else b.append("CSP    ");

		b.append(parser.getInstanceFile().getName()).append("    ");
		b.append(seed).append(" SEED");
		return b.toString();
	}



	private final String createTimeConfiguration() {
		final StringBuilder b = new StringBuilder();
		b.append(getFullSecTime()).append(" TIME    ");
		b.append(getParseTime()).append(" PARSTIME    ");
		b.append(getPreProcTime()).append(" PREPROC    ");
		b.append(getBuildTime()).append(" BUILDPB    ");
		b.append(getConfTime()).append(" CONFIG    ");
		b.append(getResTime()).append(" RES    ");
		return b.toString();
	}


	//*****************************************************************//
	//*******************  Time Measures  ****************************//
	//***************************************************************//

	public final long getParseTime() {
		return (time[1] - time[0]);
	}

	public final long getPreProcTime() {
		return (time[2] - time[1]);
	}

	public final long getBuildTime() {
		return (time[3] - time[2]);
	}

	public final long getConfTime() {
		return (time[4] - time[3]);
	}

	public final long getResTime() {
		return (time[5] - time[4]);
	}

	public final long getFullTime() {
		return (time[5] - time[0]);
	}

	public final double getFullSecTime() {
		return getFullTime() / 1000D;
	}


}
