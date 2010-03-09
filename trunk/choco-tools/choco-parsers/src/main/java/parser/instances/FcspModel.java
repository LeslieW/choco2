package parser.instances;

import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import parser.absconparseur.tools.UnsupportedConstraintException;
import parser.flatzinc.ast.SolveGoal;
import parser.flatzinc.parser.FZNParser;
import parser.instances.fcsp.FcspSettings;

import java.io.File;


final class FznParserWrapper implements InstanceFileParser {

    public FZNParser source = new FZNParser();
	public File file;

	@Override
	public void cleanup() {
        source = new FZNParser();
		file = null;
	}

	@Override
	public File getInstanceFile() {
		return file;
	}

    @Override
	public void loadInstance(File file) {
		this.file = file;
		source.loadInstance(file);
	}

	@Override
	public void parse(boolean displayInstance)throws UnsupportedConstraintException {
        // nothing to do,
        // cause the parsing build directly the model
    }

}

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 19 janv. 2010
 * Since : Choco 2.1.1
 *
 * A class to provide facilities for loading and solving
 * CSP described in the flatzinc grammar.
 */
public final class FcspModel extends AbstractInstanceModel {

    private boolean searchSet;

    private final FcspSettings settings;

    private SolveGoal solveGoal;

    public FcspModel(FcspSettings settings) {
        super(new FznParserWrapper());
        this.settings = settings;
    }

    /**
     * Executes preprocessing ( bounding, heuristics ...)
     * default implementation: do nothing.
     *
     * @return <code>true</code> if a solution has been found, <code>false</code> if the infeasibility has been proven and <code>null</code> otherwise.
     */
    @Override
    public Boolean preprocess() {
        return null;
    }

    /**
     * create the choco model after the preprocessing phase.
     */
    @Override
    public Model buildModel() {
        final FZNParser parser = ( (FznParserWrapper) this.parser).source;
		solveGoal = parser.parse();
        return parser.model;
    }

    /**
     * create a solver from the current model
     */
    @Override
    public Solver buildSolver() {
        PreProcessCPSolver s = new PreProcessCPSolver();
		s.read(model);
        settings.applyTimeLimit(s);
        searchSet = solveGoal.defineGoal(s);
		return s;
    }

    /**
     * configure and launch the resolution.
     */
    @Override
    public Boolean solve() {
        PreProcessCPSolver s = (PreProcessCPSolver) solver;
		Boolean isFeasible = Boolean.TRUE;
		//do the initial propagation to decide to do restarts or not
		if (!s.initialPropagation()) {
			return Boolean.FALSE;
		} else {
            if(!searchSet){
			// TODO : set default search when 'searchSet' is false
            }
		}
		//TODO Hadrien, Charles check this code samples, it is important that I did not break it
//		settings.applyRestartPolicy(s);
		if (isFeasible){ 
                //&& (cheuri == IMPACT || s.rootNodeSingleton(settings.doSingletonConsistency(), settings.getTimeLimitPP()))) {
			//			if (ngFromRestart && (s.restartMode || forcerestart)) {
			//				s.setRecordNogoodFromRestart(true);
			//				s.generateSearchStrategy();
			//				//s.getSearchStrategy().setSearchLoop(new SearchLoopWithNogoodFromRestart(s.getSearchStrategy(), s.getRestartStrategy()));
			//				s.launch();
			//				return s.isFeasible();
			//			} else return s.solve();
			ChocoLogging.setLoggingMaxDepth(200);
            s.launch();
			return s.isFeasible();
		} else {
			return Boolean.FALSE;
		}
    }
}
