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
package choco.scheduling;

import choco.Choco;
import choco.Options;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.AbstractTask;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;
import gnu.trove.TIntArrayList;
import static org.junit.Assert.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public final class SchedUtilities {

	private final static Logger LOGGER = ChocoLogging.getTestLogger();
	
	final static Random RANDOM=new Random();
	
	public static final int CHECK_NODES = -1;
	
	public static final int NO_CHECK_NODES = -2;


    private SchedUtilities(){}


    public static void message(final Object header, final Object label, final Solver solver) {
		if(LOGGER.isLoggable(Level.INFO)) {
		LOGGER.log(Level.INFO,"{0}\t{1}: {2} solutions\n{3}", new Object[]{header, label,solver.getNbSolutions(), solver.runtimeStatistics()});
		}
	}

	private static String jmsg(final String op, final String label) {
		return op+" ("+label+") : ";
	}


	public static void compare(final int nbsol, final int nbNodes, final String label , final Solver... solvers) {
		final TIntArrayList bests = new TIntArrayList(solvers.length);
		int bestTime = Integer.MAX_VALUE;
		for (int i = 0; i < solvers.length; i++) {
			final Solver s=solvers[i];
			//System.out.println(s.pretty());
			s.solveAll();
			final String str = String.format("%s index %d", label, i);
			message(str,"",s);
			if( s.getTimeCount()< bestTime) {
				bests.clear();
				bests.add(i);
				bestTime = s.getTimeCount();
			}else if(s.getTimeCount() == bestTime) {
				bests.add(i);
			}
			if(nbsol > 0) {
				assertEquals(String.format("check-cmp NbSols %s", str),nbsol,s.getSolutionCount());
				assertTrue("isFeasible", s.isFeasible());
			}else if(nbsol>=0) {
				assertEquals(String.format("check-cmp NbSols %s", str),nbsol,s.getSolutionCount());
				assertFalse("isFeasible", s.isFeasible());
				
			}else if(i>0){
				assertEquals(String.format("check-cmp NbSols %s", str),solvers[i-1].getSolutionCount(),s.getSolutionCount());
			}
			if(nbNodes>=0) {
				assertEquals(String.format("check-cmp NbNodes %s", str),nbNodes,s.getNodeCount());
			}else if(nbNodes == CHECK_NODES && i>0){
				assertEquals(String.format("check-cmp NbNodes %s", str),solvers[i-1].getNodeCount(),s.getNodeCount());
			}
		}
		LOGGER.log(Level.INFO,"Best solver: index {0} in {1}ms", new Object[]{bests,bestTime});
	}

	public static void solveRandom(final CPSolver solver, final int nbsol, final int nbNodes, final String label) {
		solveRandom(solver, nbsol, nbNodes, null, label);
	}
	
	public static void solveRandom(final CPSolver solver, final int nbsol, final int nbNodes, final Integer seed, final String label) {
		//solver.setLoggingMaxDepth(10000);
		if(seed == null) solver.setRandomSelectors();
		else solver.setRandomSelectors(seed.longValue());
		final Boolean r=solver.solveAll();
		message(label, "solve (random) : ", solver);
		checkRandom(solver, r, nbsol, nbNodes, label);
		
	}

	public static void checkRandom(final Solver solver, final Boolean r, final int nbsol, final int nbNodes, final String label) {
		if(nbsol==0) {
			assertEquals(jmsg("unsat",label),Boolean.FALSE,r);
		}else {
			assertEquals(jmsg("sat",label),Boolean.TRUE,r);
			assertEquals(jmsg("check nb Sol.",label),nbsol,solver.getNbSolutions());
			if(nbNodes>=0) {
				assertEquals(jmsg("check nb nodes",label),nbNodes,solver.getNodeCount());
			}
		}
	}


	public static IntegerVariable[] makeIntvarArray(final String name, final int[] min, final int[] max) {
		final IntegerVariable[] vars=new IntegerVariable[min.length];
		for (int i = 0; i < vars.length; i++) {
			vars[i]=Choco.makeIntVar(String.format("%s-%d", name, i), min[i],max[i], Options.V_BOUND);
		}
		return vars;
	}

}


class SimpleTask extends AbstractTask {

	private static int nextID;

	private final Point domain;

	private final int duration;


    /**
     *
     * @param est
     * @param lst
     * @param duration
     */
	public SimpleTask(final int est, final int lst, final int duration) {
		super(nextID++, "T"+nextID);
		this.domain = new Point(est, lst>=est ? lst :est);
		this.duration = duration>0 ? duration : 0;
	}


	/**
	 * @see ITask#getECT()
	 */
	@Override
	public int getECT() {
		return domain.x+duration;
	}

	/**
	 * @see ITask#getEST()
	 */
	@Override
	public int getEST() {
		return domain.x;
	}

	/**
	 * @see ITask#getLCT()
	 */
	@Override
	public int getLCT() {
		return domain.y+duration;
	}

	/**
	 * @see ITask#getLST()
	 */
	@Override
	public int getLST() {
		return domain.y;
	}

	/**
	 * @see ITask#getMinDuration()
	 */
	@Override
	public int getMinDuration() {
		return duration;
	}

	/**
	 * @see ITask#hasConstantDuration()
	 */
	@Override
	public boolean hasConstantDuration() {
		return true;
	}

	/**
	 * @see ITask#isScheduled()
	 */
	@Override
	public boolean isScheduled() {
		return domain.x==domain.y;
	}

	/**
	 * @see ITask#getMaxDuration()
	 */
	@Override
	public int getMaxDuration() {
		return duration;
	}


}


class SimpleResource implements ICumulativeResource<SimpleTask> {

	
	public final List<SimpleTask> tasksL;

	public int[] heights;
	
	public int capacity;
	
	
	public SimpleResource(final List<SimpleTask> tasksL, final int[] heights, final int capacity) {
		super();
		this.tasksL = tasksL;
		this.heights = heights;
		this.capacity = capacity;
	}

	public SimpleResource(final List<SimpleTask> tasksL) {
		super();
		this.tasksL=new ArrayList<SimpleTask>(tasksL);
		this.capacity = 1;
		this.heights = new int[tasksL.size()];
		Arrays.fill(heights, 1);
	}

	
	@Override
	public IRTask getRTask(final int idx) {
		return null;
	}


	@Override
	public List<IRTask> asRTaskList() {
		return Collections.emptyList();
	}

	@Override
	public int getNbTasks() {
		return tasksL.size();
	}

	@Override
	public String getRscName() {
		return "internal resource (test)";
	}

	@Override
	public SimpleTask getTask(final int idx) {
		return tasksL.get(idx);
	}

	@Override
	public Iterator<SimpleTask> getTaskIterator() {
		return tasksL.listIterator();
	}
	
	@Override
	public List<SimpleTask> asTaskList() {
		return Collections.unmodifiableList(tasksL);
	}

	@Override
	public IntDomainVar getCapacity() {
		return null;
	}
	@Override
	
	public int getMaxCapacity() {
		return capacity;
	}

	@Override
	public int getMinCapacity() {
		return getCapacity().getInf();
	}

	public IntDomainVar getHeight(final int idx) {
		return null;
	}
	
	
	@Override
	public IntDomainVar getConsumption() {
		return null;
	}

	@Override
	public int getMaxConsumption() {
		return 0;
	}

	@Override
	public int getMinConsumption() {
		return 0;
	}

	@Override
	public boolean isInstantiatedHeights() {
		return true;
	}

	@Override
	public boolean hasOnlyPosisiveHeights() {
		return true;
	}
	
	@Override
	public int getNbOptionalTasks() {
		return 0;
	}

	@Override
	public int getNbRegularTasks() {
		return getNbTasks();
	}

}