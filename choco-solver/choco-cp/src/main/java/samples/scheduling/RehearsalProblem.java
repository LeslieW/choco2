package samples.scheduling;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import gnu.trove.TIntArrayList;
import samples.Examples.PatternExample;

import java.util.logging.Logger;

public class RehearsalProblem extends PatternExample {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	public final static int[] CSPLIB_DURATIONS = {2, 4, 1, 3, 3, 2, 5, 7, 6};

	public final static int[][] CSPLIB_REQUIREMENTS = {
		{1, 1, 0, 1, 0, 1, 1, 0, 1},
		{1, 1, 0, 1, 1, 1, 0, 1, 0},
		{1, 1, 0, 0, 0, 0, 1, 1, 0},
		{1, 0, 0, 0, 1, 1, 0, 0, 1},
		{0, 0, 1, 0, 1, 1, 1, 1, 0}
	};

	public final static int CSPLIB_OBJECTIVE = 17;

	private int nbPieces;

	private int nbPlayers;

	private TIntArrayList[] requirements;

	private int[] durations;

	private int totalDuration;

	private int cumulatedDuration;

	private IntegerVariable totalWaitingTime;

	private TaskVariable[] musicPieces;

	private IntegerVariable[] arrivals;

	private IntegerVariable[] departures;

	public boolean isDisjunctiveModel = true;

	public boolean isPrecOnlyDecision = false;

	public RehearsalProblem() {
		super();
	}


	@Override
	public void setUp(Object paramaters) {
		//read duration
		durations = (int[]) ((Object[]) paramaters)[0];
		nbPieces = durations.length;
		totalDuration = 0;
		for (int i = 0; i < nbPieces; i++) {
			totalDuration += durations[i];
		}

		//read assignment matrix
		int[][] matrix = (int[][]) ((Object[]) paramaters)[1];
		nbPlayers = matrix.length;
		requirements = new TIntArrayList[nbPlayers];
		cumulatedDuration = 0;
		for (int i = 0; i < matrix.length; i++) {
			requirements[i] = new TIntArrayList();
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] == 1) {
					cumulatedDuration += durations[j];
					requirements[i].add(j);
				}
			}
		}
	}



	@Override
	public void buildModel() {
		_m = new CPModel();
		totalWaitingTime = Choco.makeIntVar("totalWaitingTime", 0, totalDuration * nbPlayers -cumulatedDuration, "cp:bound","cp:objective");
		musicPieces = Choco.makeTaskVarArray("piece", 0, totalDuration, durations, "cp:bound");
		arrivals = Choco.makeIntVarArray("arrival", nbPlayers, 0, totalDuration);
		departures = Choco.makeIntVarArray("departure", nbPlayers, 0, totalDuration);
		IntegerExpressionVariable expr = Choco.constant(-cumulatedDuration);

		_m.addVariables(musicPieces);
		//define arrival time, departure time and staying time of each player
		for (int i = 0; i < nbPlayers; i++) {
			int n = requirements[i].size();
			IntegerVariable[] atmp = new IntegerVariable[n];
			IntegerVariable[] dtmp = new IntegerVariable[n];
			for (int j = 0; j < n; j++) {
				TaskVariable t= musicPieces[requirements[i].get(j)];
				atmp[j] = t.start();
				dtmp[j] = t.end();
			}
			_m.addConstraints(
					Choco.min(atmp, arrivals[i]),
					Choco.max(dtmp, departures[i])
			);
			expr = Choco.plus(expr, Choco.minus(departures[i], arrivals[i]));
		}
		//obj. constraint
		_m.addConstraint(Choco.eq(totalWaitingTime, expr));

		if(isDisjunctiveModel) {
			//add an unary resource
			_m.addConstraint(Choco.disjunctive(musicPieces));
		}else {
			//define all possible precedence between tasks
			_m.addConstraints( Choco.precedenceDisjoint(musicPieces, isPrecOnlyDecision ? "cp:decision" :""));
		}
	}

	@Override
	public void buildSolver() {
		CPSolver s = new CPSolver();
		s.getSchedulerConfiguration().setPrecedenceNetwork(true);
		s.read(_m);
		_s = s;
	}

	@Override
	public void prettyOut() {
		LOGGER.info(""+_s.getVar(totalWaitingTime));
		LOGGER.info(StringUtils.pretty(_s.getVar(musicPieces)));

	}

	@Override
	public void solve() {
		_s.minimize(false);
	}
	
	@Override
	public void execute() {
		execute(new Object[]{CSPLIB_DURATIONS, CSPLIB_REQUIREMENTS});
	}


	public static void main(String[] args) {
		RehearsalProblem pb = new RehearsalProblem();
		pb.isDisjunctiveModel = true;
		pb.isPrecOnlyDecision = false;
		pb.execute(new Object[]{CSPLIB_DURATIONS, CSPLIB_REQUIREMENTS});
		//pb.execute(new Object[]{ dur, req});
	}


}
