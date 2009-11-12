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


import static parser.instances.xcsp.XcspSettings.DOMOVERDEG;
import static parser.instances.xcsp.XcspSettings.DOMOVERWDEG;
import static parser.instances.xcsp.XcspSettings.IMPACT;
import static parser.instances.xcsp.XcspSettings.SIMPLE;
import static parser.instances.xcsp.XcspSettings.VERSATILE;

import java.io.File;

import parser.absconparseur.components.PVariable;
import parser.absconparseur.tools.InstanceParser;
import parser.absconparseur.tools.SolutionChecker;
import parser.absconparseur.tools.UnsupportedConstraintException;
import parser.chocogen.ChocoFactory;
import parser.chocogen.ObjectFactory;
import parser.instances.xcsp.XcspSettings;
import choco.cp.model.CPModel;
import choco.cp.solver.constraints.integer.extension.ValidityChecker;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;

//TODO InstanceParser should implement the interface. 
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

	public final XcspSettings settings;


	//temporary data
	private int cheuri;
	private String[] values;

	public XcspModel() {
		this(new XcspSettings());
	}


	public XcspModel(XcspSettings settings) {
		super(new ParserWrapper());
		this.settings = settings;
	}


	@Override
	public void initialize() {
		super.initialize();
		cheuri = XcspSettings.DOMOVERWDEG;
		values = null;
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
		settings.applyTimeLimit(s);
		return s;
	}



	@Override
	public String getValuesMessage() {
		if(values != null) {
			final StringBuilder b = new StringBuilder();
			for (int i = 1; i < values.length; i++) {
				b.append(values[i]).append(' ');
			}
			return b.toString();
		}else return "";

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
			if (settings.isRandomValue() ) s.setRandomValueOrdering( (int) getSeed());
			cheuri = settings.getHeuristic();
			//set the search
			switch (cheuri) {
			case VERSATILE:
				isFeasible = s.setVersatile(s, settings.getTimeLimitPP());
				cheuri = s.getBBSearch().determineHeuristic(s);
				break;
			case DOMOVERDEG:
				isFeasible = s.setDomOverDeg(s); break;
			case DOMOVERWDEG:
				isFeasible = s.setDomOverWeg(s, settings.getTimeLimitPP());
				//((DomOverWDegBranching) s.tempGoal).setRandomVarTies(seed);
				break;
			case IMPACT:
				isFeasible = s.setImpact(s, settings.getTimeLimitPP());
				//((ImpactBasedBranching) s.tempGoal).setRandomVarTies(seed);
				break;
			case SIMPLE:
				s.setVarIntSelector(new MinDomain(s));
				if (settings.isRandomValue()) s.setValIntSelector(new RandomIntValSelector(getSeed()));
				else s.setValIntIterator(new IncreasingDomain());
			default:
				break;
			}
		}
		//TODO Hadrien, Charles check this code samples, it is important that I did not break it
		settings.applyRestartPolicy(s);
		//		if (forcerestart != null) {
		//			if (forcerestart) {
		//				s.setGeometricRestart(base, growth);
		//				//should investigate the effect of reinitializing branching in more details
		//				//it seemed useful with restartFromSol and useless with a restart policy.
		//				s.restartConfig.setInitializeSearchAfterRestart(false); 
		//			}
		//		} else {
		//			if (s.restartMode) {
		//				s.setGeometricRestart(10, 1.3);                                
		//				s.restartConfig.setInitializeSearchAfterRestart(false);
		//				//s.setGeometricRestart(Math.min(Math.max(s.getNbIntVars(), 200), 400), 1.4d);
		//			}
		//		}
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		if (isFeasible && (cheuri == IMPACT || s.rootNodeSingleton(settings.doSingletonConsistency(), settings.getTimeLimitPP()))) {
			//			if (ngFromRestart && (s.restartMode || forcerestart)) {
			//				s.setRecordNogoodFromRestart(true);
			//				s.generateSearchStrategy();
			//				//s.getSearchStrategy().setSearchLoop(new SearchLoopWithNogoodFromRestart(s.getSearchStrategy(), s.getRestartStrategy()));
			//				s.launch();
			//				return s.isFeasible();
			//			} else return s.solve();
			s.setLoggingMaxDepth(200);
			return s.solve();
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
		InstanceParser pars = ( (ParserWrapper) parser).source;
		if( !checkEverythingIsInstantiated(pars, solver) ) throw new SolutionCheckerException("Some Variables are not instantiated");
		PVariable[] vars = pars.getVariables(); 
		values = new String[vars.length + 1];
		values[0] = parser.getInstanceFile().getPath();
		for (int i = 1; i < vars.length + 1; i++) {
			try {
				values[i] = String.valueOf(solver.getVar(vars[i-1].getChocovar()).getVal());
			} catch (NullPointerException e) {
				values[i] = String.valueOf(vars[i-1].getChocovar().getLowB());
			}
		}
		ValidityChecker.nbCheck = 0;
		if (settings.doExternalCheck()) SolutionChecker.main(values);
	}




	@Override
	protected void logOnDiagnostics() {
		super.logOnDiagnostics();
		logMsg.storeDiagnostic("CHECKS",  ValidityChecker.nbCheck);
		logMsg.storeDiagnostic("AC",  ObjectFactory.algorithmAC);
	}

	@Override
	protected void logOnConfiguration() {
		super.logOnConfiguration();
		PreProcessCPSolver psolver = (PreProcessCPSolver) solver;
		logMsg.storeConfiguration(psolver.restartMode+" RESTART    "+cheuri+" HEURISTIC    "+settings.isRandomValue()+" RANDVAL");
	}


}
