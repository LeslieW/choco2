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
package choco.solver.search;

import static choco.Choco.minus;
import static choco.Choco.neq;
import static choco.Choco.plus;
import static choco.cp.solver.search.BranchingFactory.domDDeg;
import static choco.cp.solver.search.BranchingFactory.domDDegBin;
import static choco.cp.solver.search.BranchingFactory.domDeg;
import static choco.cp.solver.search.BranchingFactory.domDegBin;
import static choco.cp.solver.search.BranchingFactory.domWDeg;
import static choco.cp.solver.search.BranchingFactory.domWDegBin;
import static choco.cp.solver.search.BranchingFactory.incDomWDeg;
import static choco.cp.solver.search.BranchingFactory.incDomWDegBin;
import static choco.cp.solver.search.BranchingFactory.minDomMinVal;
import static choco.cp.solver.search.BranchingFactory.randomIntBinSearch;
import static choco.cp.solver.search.BranchingFactory.randomIntSearch;
import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.ValSelector;
/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 23 sept. 2005
 * Time: 21:08:43
 * To change this template use File | Settings | File Templates.
 */
public class BranchingTest {
	public static final int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};

	private final static Logger LOGGER = ChocoLogging.getTestLogger();

	private Model m;
	private Solver s;

	private IntegerVariable[] queens;

	private void buildQueen(int n) {
		LOGGER.log(Level.CONFIG, "n queens, binary model, n={0}", n);
		if( queens == null || queens.length != n) {
			m = new CPModel();
			// create variables
			queens = Choco.makeIntVarArray("Q", n, 1, n);
			// diagonal constraints
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					int k = j - i;
					m.addConstraint(neq(queens[i], queens[j]));
					m.addConstraint(neq(queens[i], plus(queens[j], k)));
					m.addConstraint(neq(queens[i], minus(queens[j], k)));
				}
			}
		}
	}

	private void checkResolution() {
		final int n =queens.length;
		if(n < nbQueensSolution.length) {
			if( nbQueensSolution[n] == 0) assertEquals(Boolean.FALSE, s.isFeasible());
			else assertEquals(Boolean.TRUE, s.isFeasible());
			assertEquals(nbQueensSolution[n], s.getNbSolutions());
		} else assertEquals(Boolean.TRUE, s.isFeasible());
	}

	private final static int NB_BRANCHING = 13;
	
	private void setBranching(int type) {
		s = new CPSolver();
		s.read(m);
		final ValSelector valsel = new RandomIntValSelector(type);
		switch (type) {
		case 0:	s.attachGoal(randomIntBinSearch(s, type));
		case 1:	s.attachGoal(randomIntSearch(s, type));
		case 2:s.attachGoal(domWDeg(s));break;
		case 3:s.attachGoal(domWDegBin(s));break;
		case 4:s.attachGoal(incDomWDeg(s));break;
		case 5:s.attachGoal(incDomWDegBin(s));break;
		case 6:s.attachGoal(domWDeg(s, valsel));break;
		case 7:s.attachGoal(domWDegBin(s, valsel));break;
		case 8:s.attachGoal(incDomWDegBin(s, valsel));break;
		case 9:s.attachGoal(domDegBin(s, valsel));break;
		case 10:s.attachGoal(domDeg(s, valsel));break;
		case 11:s.attachGoal(domDDegBin(s, valsel));break;
		case 12:s.attachGoal(domDDeg(s, valsel));break;
		default: s.attachGoal(minDomMinVal(s));break;
		}
	}

	private void testQueen(int n) {
		buildQueen(n);
		for (int i = 0; i < NB_BRANCHING; i++) {
			setBranching(i);
			s.solveAll();
			checkResolution();
		}

	}
	@Test
	public void testQueen4() {
		testQueen(4);
	}

	@Test
	public void testQueen5() {
		testQueen(5);
	}

	@Test
	public void testQueen6() {
		testQueen(6);
	}

	@Test
	public void testQueen7() {
		testQueen(7);
	}

	@Test
	public void testQueen8() {
		testQueen(8);
	}

	@Test
	public void testQueen9() {
		testQueen(9);
	}

	@Test
	public void testQueen10() {
		testQueen(10);
	}
	
	@Test
	public void testQueen11() {
		testQueen(11);
	}
}
