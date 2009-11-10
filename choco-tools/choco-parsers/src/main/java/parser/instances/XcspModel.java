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


import java.io.File;
import java.util.HashMap;

import parser.absconparseur.components.PVariable;
import parser.absconparseur.tools.InstanceParser;
import parser.absconparseur.tools.SolutionChecker;
import parser.absconparseur.tools.UnsupportedConstraintException;
import parser.chocogen.ChocoFactory;
import choco.cp.model.CPModel;
import choco.cp.solver.constraints.integer.extension.ValidityChecker;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;

class ParserWrapper implements InstanceFileParser {

	public InstanceParser source = new InstanceParser(); 

	public File file;
	@Override
	public void cleanup() {
		source = new InstanceParser();
		file = null;
	}


	@Override
	public File getInstanceFile() {
		return file;
	}

	@Override
	public void loadInstance(File file) {
		this.file = file;
		source.loadInstance(file.getAbsolutePath());

	}

	@Override
	public void parse(boolean displayInstance)
	throws UnsupportedConstraintException {
		source.parse(displayInstance);

	}


}
/**
 * User:    charles
 * Date:    19 août 2008
 * <p/>
 * A class to provide facilities for loading and solving
 * CSP described in the xml format of the 2008 competition
 */
public class XcspModel extends AbstractInstanceModel {

	//heuristics
	private static final int DOMOVERDEG = 0;
	private static final int DOMOVERWDEG = 1;
	private static final int IMPACT = 2;
	private static final int VERSATILE = 3;
	private static final int SIMPLE = 4;
	
	private static int heuristic = 0; // DOMOVERDEG by default

	private static int seed;

	//algo d'ac : 2001 ou 32 ou 2008
	private static int ac = 32;

	//perform singleton consistency step or not
	private static boolean singleton = false;

	private static boolean ngFromRestart = false;

	//force to restart or not
	private static Boolean forcerestart = null;
	private static int base = 10;
	private static double growth = 1.5d;

	private static int verb = 0; // if O no verb

	//total timelimit in s
	private static int timelimit = 10000; // in sec

	//initialization timelimit (for impact) in ms
	public int initialisationtime = 60000; //60

	public boolean randvalh = false;

	//temporary data
	private int cheuri;
	private String[] values;


	
	@Override
	public void initialize() {
		super.initialize();
		cheuri = DOMOVERWDEG;
		values = null;
	}

	public static int getAcAlgo() {
		return ac;
	}

	public static boolean doSingleton() {
		return singleton;
	}


	private XcspModel() {
		super(new ParserWrapper());
	}

	


	/**
	 * Main method. Check arguments and set up the options
	 * accordingly. example of command line :
	 * -file mycsp.xml -h 3 -ac 32 -s true -verb 1 -time 30
	 *
	 * @param args arguments
	 * @throws Exception
	 */
	public void generate(String[] args) throws Exception {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		HashMap<String, String> options = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i++];
			String val = args[i];
			options.put(arg, val);
		}
		File dossier;
		if (options.containsKey("-file")) {
			dossier = new File(options.get("-file"));
			if (!dossier.exists()) {
				throw new Exception("Unknown file or directory");
			}
		} else {
			throw new Exception("file option -file is missing");
		}
		if (options.containsKey("-h")) {
			heuristic = Integer.parseInt(options.get("-h"));
		} else {
			throw new Exception("heuristic option -h is missing");
		}
		if (options.containsKey("-ac")) {
			ac = Integer.parseInt(options.get("-ac"));
		} else {
			throw new Exception("AC option -ac is missing");
		}
		if (options.containsKey("-s")) {
			singleton = Boolean.parseBoolean(options.get("-s"));
		}
		if (options.containsKey("-time")) {
			timelimit = Integer.parseInt(options.get("-time"));
		}
		if (options.containsKey("-verb")) {
			verb = Integer.parseInt(options.get("-verb"));
		}
		if (options.containsKey("-rest")) {
			forcerestart = Boolean.parseBoolean(options.get("-rest"));
		}
		if (options.containsKey("-rb")) {
			base = Integer.parseInt(options.get("-rb"));
		}
		if (options.containsKey("-rg")) {
			growth = Double.parseDouble(options.get("-rg"));
		}
		if (options.containsKey("-saclim")) {
			initialisationtime = Integer.parseInt(options.get("-saclim")) * 1000;
		}
		if (options.containsKey("-seed")) {
			seed = Integer.parseInt(options.get("-seed"));
		}
		if (options.containsKey("-randval")) {
			randvalh = Boolean.parseBoolean(options.get("-randval"));;
		}
		if (options.containsKey("-ngfres")) {
			ngFromRestart = Boolean.parseBoolean(options.get("-ngfres"));;
		}
		try {
			if (dossier.isFile()) {
				solveFile(dossier);
			} else {
				solveDirectory(dossier);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ChocoLogging.flushLogs();
	}



	/**
	 * Solve all the csps contained in the corresponding directory :
	 * dossiers
	 *
	 * @param dossiers : the directory where instances are stored
	 */
	public void solveDirectory(File dossiers) {
		File listingDonneesEntree[] = dossiers.listFiles();
		for (File fichier : listingDonneesEntree) {
			if (fichier.isFile()) {
				solveFile(fichier);
			} else if (fichier.isDirectory()) {
				solveDirectory(fichier);
			}
		}
	}


	@Override
	public Model buildModel() {
		InstanceParser parser = ( (ParserWrapper) this.parser).source;
		boolean forceExp = false; //force all expressions to be handeled by arc consistency
		CPModel m = new CPModel(parser.getMapOfConstraints().size(), parser.getNbVariables(), 50, 0, 100, 100, 100);
		ChocoFactory chocofact = new ChocoFactory(parser, m);
		chocofact.createVariables();
		chocofact.createRelations();
		chocofact.createConstraints(forceExp);
		return m;
	}


	@Override
	public Solver buildSolver() {
		PreProcessCPSolver s = new PreProcessCPSolver();
		s.read(model);
		s.setTimeLimit(timelimit * 1000);
		return s;
	}



	@Override
	public String getValuesMessage() {
		final StringBuilder b = new StringBuilder();
		for (int i = 1; i < values.length; i++) {
			b.append(values[i]).append(' ');
		}
		return b.toString();
	}



	@Override
	public Boolean preprocess() {
		return null;
	}

	@Override
	public Boolean solve() {
		PreProcessCPSolver s = (PreProcessCPSolver) solver;
		Boolean isFeasible = Boolean.TRUE;
		//do the initial propagation to decide to do restarts or not
		if (!s.initialPropagation()) {
			return Boolean.FALSE;
		} else {
			if (randvalh) s.setRandomValueOrdering(seed);
			cheuri = heuristic;
			//set the search
			switch (cheuri) {
			case VERSATILE:
				isFeasible = s.setVersatile(s, initialisationtime);
				cheuri = s.getBBSearch().determineHeuristic(s);
				break;
			case DOMOVERDEG:
				isFeasible = s.setDomOverDeg(s); break;
			case DOMOVERWDEG:
				isFeasible = s.setDomOverWeg(s, initialisationtime);
				//((DomOverWDegBranching) s.tempGoal).setRandomVarTies(seed);
				break;
			case IMPACT:
				isFeasible = s.setImpact(s, initialisationtime);
				//((ImpactBasedBranching) s.tempGoal).setRandomVarTies(seed);
				break;
			case SIMPLE:
				s.setVarIntSelector(new MinDomain(s));
				if (randvalh)
					s.setValIntSelector(new RandomIntValSelector(seed));
				else s.setValIntIterator(new IncreasingDomain());
			default:
				break;
			}
		}
		if (forcerestart != null) {
			if (forcerestart) {
				s.setGeometricRestart(base, growth);
				//should investigate the effect of reinitializing branching in more details
				//it seemed useful with restartFromSol and useless with a restart policy.
				s.restartConfig.setInitializeSearchAfterRestart(false); 
			}
		} else {
			if (s.restartMode) {
				s.setGeometricRestart(10, 1.3);                                
				s.restartConfig.setInitializeSearchAfterRestart(false);
				//s.setGeometricRestart(Math.min(Math.max(s.getNbIntVars(), 200), 400), 1.4d);
			}
		}
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		s.setLoggingMaxDepth(200);
		if (isFeasible && (cheuri == IMPACT || s.rootNodeSingleton(singleton, initialisationtime))) {
			if (ngFromRestart && (s.restartMode || forcerestart)) {
				s.setRecordNogoodFromRestart(true);
				s.generateSearchStrategy();
				//s.getSearchStrategy().setSearchLoop(new SearchLoopWithNogoodFromRestart(s.getSearchStrategy(), s.getRestartStrategy()));
				s.launch();
				return s.isFeasible();
			} else return s.solve();
		} else {
			return Boolean.FALSE;
		}
	}


	public boolean checkEverythingIsInstantiated(InstanceParser parser, Solver s) {
		for (int i = 0; i < parser.getVariables().length; i++) {
			try {
				if (!s.getVar(parser.getVariables()[i].getChocovar()).isInstantiated()) {
					return false;
				}
			} catch (NullPointerException ignored) {
			}
		}
		return true;
	}

	@Override
	public void checkSolution() throws SolutionCheckerException {
		super.checkSolution();
		if( !checkEverythingIsInstantiated((InstanceParser) parser, solver) ) throw new SolutionCheckerException("Some Variables are not instantiated");
		PVariable[] vars = ( ( InstanceParser) parser).getVariables(); 
		values = new String[vars.length + 1];
		values[0] = parser.getInstanceFile().getPath();
		for (int i = 1; i < vars.length + 1; i++) {
			try {
				values[i] = String.valueOf(solver.getVar(vars[i].getChocovar()).getVal());
			} catch (NullPointerException e) {
				values[i] = String.valueOf(vars[i].getChocovar().getLowB());
			}
		}
		ValidityChecker.nbCheck = 0;
		if (verb > 0) SolutionChecker.main(values);
	}


	

	@Override
	protected void logOnDiagnostics() {
		super.logOnDiagnostics();
		logMsg.storeDiagnostic("CHECKS",  ValidityChecker.nbCheck);
		logMsg.storeDiagnostic("AC",  ac);
	}
	
	

	@Override
	protected void logOnConfiguration() {
		super.logOnConfiguration();
		PreProcessCPSolver psolver = (PreProcessCPSolver) solver;
		logMsg.storeConfiguration(psolver.restartMode+" RESTART    "+cheuri+" HEURISTIC    "+randvalh+" RANDVAL");
	}

	
	public static void main(String[] args) throws Exception {
		//         example();
		ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		XcspModel xs = new XcspModel();
		xs.generate(args);
	}


}
