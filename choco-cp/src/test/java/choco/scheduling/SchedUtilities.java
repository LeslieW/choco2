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

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.BitFlags;
import choco.cp.solver.constraints.global.scheduling.AbstractResourceSConstraint;
import choco.kernel.common.util.IntList;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.AbstractTask;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public final class SchedUtilities {

	public final static Random RANDOM=new Random();
	
	public static final int CHECK_NODES = -1;
	
	public static final int NO_CHECK_NODES = -2;


    private SchedUtilities(){}


    public static void message(String header,String label,Boolean r,Solver solver) {
		StringBuilder buffer=new StringBuilder();
		if( ! header.isEmpty() ) {
			buffer.append(header).append("\t");
		}
		buffer.append(label).append(' ').append(r).append(" ; nb Sol. ").append(solver.getNbSolutions());
		buffer.append(" ; ").append(solver.getTimeCount()).append("ms ; ").append(solver.getNodeCount()).append(" node(s)");
		System.out.println(buffer);
	}

	private static String jmsg(String op,String label) {
		return op+" ("+label+") : ";
	}


	public static void compare(int nbsol,int nbNodes,String label ,Solver... solvers) {
		Choco.DEBUG=true;
		IntList bests = new IntList(solvers.length);
		int bestTime = Integer.MAX_VALUE;
		for (int i = 0; i < solvers.length; i++) {
			final Solver s=solvers[i];
			//CPSolver.setVerbosity(CPSolver.SOLUTION);
			s.solveAll();
			CPSolver.flushLogs();
			final String str = label +" index "+i;
			message(str,"",s.isFeasible(),s);
			if( s.getTimeCount()< bestTime) {
				bests.reInit();
				bests.add(i);
				bestTime = s.getTimeCount();
			}else if(s.getTimeCount() == bestTime) {
				bests.add(i);
			}
			if(nbsol>=0) {
				assertEquals("check-cmp NbSols "+str,nbsol,s.getNbSolutions());
			}else if(i>0){
				assertEquals("check-cmp NbSols "+str,solvers[i-1].getNbSolutions(),s.getNbSolutions());
			}
			if(nbNodes>=0) {
				assertEquals("check-cmp NbNodes "+str,nbNodes,s.getNodeCount());
			}else if(nbNodes == CHECK_NODES && i>0){
				assertEquals("check-cmp NbNodes "+str,solvers[i-1].getNodeCount(),s.getNodeCount());
			}
		}
		System.out.println("Best solver: index "+bests+" in "+bestTime+" ms");
	}

	public static void solveRandom(CPSolver solver,int nbsol,int nbNodes,String label) {
		Choco.DEBUG=true;
		//CPSolver.setVerbosity(CPSolver.SEARCH);
		solver.setLoggingMaxDepth(10000);
		solver.setRandomSelectors();
		//System.out.println(solver.pretty());
		Boolean r=solver.solveAll();
		CPSolver.flushLogs();
		message(label, "solve (random) : ", r, solver);
		logRandom(solver, r, nbsol, nbNodes, label);
		
	}
//
	public static void logRandom(Solver solver,Boolean r,int nbsol,int nbNodes,String label) {
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


	public static IntegerVariable[] makeIntvarArray(String name, int[] min, int[] max) {
		IntegerVariable[] vars=new IntegerVariable[min.length];
		for (int i = 0; i < vars.length; i++) {
			vars[i]=Choco.makeIntVar(name+"-"+i, min[i],max[i],"cp:bound");
		}
		return vars;
	}

}



abstract class AbstractTestProblem {

	public CPModel model;

	public CPSolver solver;

	public Constraint rsc;

	public IntegerVariable[] starts;

	public IntegerVariable[] durations;

	public TaskVariable[] tasks;

	public int horizon = 10000000;



	public AbstractTestProblem() {
		super();
	}

	public AbstractTestProblem(IntegerVariable[] starts, IntegerVariable[] durations) {
		super();
		this.starts = starts;
		this.durations = durations;
	}

	public AbstractTestProblem(IntegerVariable[] durations) {
		super();
		this.durations = durations;
	}

	public final void setFlags(BitFlags flags) {
		final AbstractResourceSConstraint cstr = (AbstractResourceSConstraint) solver.getCstr(this.rsc);
		cstr.getFlags().clear();
		cstr.getFlags().set(flags);
	}

	public void generateSolver() {
		solver = new CPSolver();
		solver.setHorizon(horizon);
		solver.read(model);
	}

	/**
	 * 
	 */
	protected abstract Constraint[] generateConstraints();

	public void initializeModel() {
		model = new CPModel();
		initializeTasks();
		Constraint[] cstr = generateConstraints();
		if(cstr!=null) {
			rsc = cstr[0];
			model.addConstraints(cstr);
		}else {
			System.err.println("no model constraint ?");
		}

	}

	public void initializeTasks() {
		if(starts==null) { tasks=Choco.makeTaskVarArray("T", 0, horizon, durations);}
		else {
			tasks=new TaskVariable[durations.length];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i]= Choco.makeTaskVar("T_"+i, starts[i], Choco.makeIntVar("end-"+i, 0, horizon, "cp:bound"), durations[i]);
			}
		}
	}

	public void setHorizon(int horizon) {
		this.horizon = horizon;
	}

	protected void horizonConstraints(IntegerVariable[] starts, IntegerVariable[] durations) {
		if (horizon > 0) {
			for (int i = 0; i < starts.length; i++) {
				model.addConstraint(Choco.geq(horizon, Choco.plus(starts[i], durations[i])));
			}
		}
	}

	public IntegerVariable[] generateRandomDurations(int n) {
		IntegerVariable[] durations = new IntegerVariable[n];
		int gap = horizon / n;
		int max = gap + horizon % n;
		for (int i = 0; i < n - 1; i++) {
			final int v = SchedUtilities.RANDOM.nextInt(max) + 1;
			max += gap - v;
			durations[i] = Choco.constant(v);
		}
		durations[n - 1] = Choco.constant(max);
		return durations;
	}

	public void setRandomProblem(int size) {
		starts = null;
		durations = generateRandomDurations(size);
	}
}


class SimpleTask extends AbstractTask {

	private static int nextID=0;

	private final Point domain;

	private final int duration;


    /**
     *
     * @param est
     * @param lst
     * @param duration
     */
	public SimpleTask(int est,int lst, int duration) {
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
		return getMinDuration();
	}


}


class SimpleResource implements ICumulativeResource<SimpleTask> {

	
	public final List<SimpleTask> tasksL;

	public int[] heights;
	
	public int capacity;
	
	
	public SimpleResource(List<SimpleTask> tasksL, int[] heights, int capacity) {
		super();
		this.tasksL = tasksL;
		this.heights = heights;
		this.capacity = capacity;
	}

	public SimpleResource(List<SimpleTask> tasksL) {
		super();
		this.tasksL=new ArrayList<SimpleTask>(tasksL);
		this.capacity = 1;
		this.heights = new int[tasksL.size()];
		Arrays.fill(heights, 1);
	}

	
	@Override
	public IRTask getRTask(int idx) {
		return null;
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
	public SimpleTask getTask(int idx) {
		return tasksL.get(idx);
	}

	@Override
	public Iterator<SimpleTask> getTaskIterator() {
		return tasksL.listIterator();
	}
	
	@Override
	public List<SimpleTask> asList() {
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

	public IntDomainVar getHeight(int idx) {
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
	
	
}