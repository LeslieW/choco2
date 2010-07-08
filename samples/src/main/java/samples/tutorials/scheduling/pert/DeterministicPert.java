/* * * * * * * * * * * * * * * * * * * * * * * * *
 *           _      _                            *
 *          |  (..)  |                           *
 *          |_ J||L _|        CHOCO solver       *
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
package samples.tutorials.scheduling.pert;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import samples.tutorials.PatternExample;

import java.util.logging.Logger;

import static choco.Choco.makeTaskVar;


/**
 * The following example is inspired from an example read in the Ilog Scheduler userguide.
 * We keep the duration but we change the task network.
 * Instead of dealing with resources, we perform Pert/CPM calculations.
 * We also provide a simple decision tool to find the solution with the minimal makespan and minimal price (for the given makespan) to deal with alternatives.
 *
 * @author Arnaud Malapert
 *         Date : 2 déc. 2008
 *         Since : 2.0.1
 *         Update : 2.0.1
 */
public class DeterministicPert extends PatternExample {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();


    protected int horizon = 28;

    protected String[] activities = {"masonry", "carpentry", "plumbing", "ceiling", "roofing",
            "painting", "windows", "facade", "garden", "moving"};
    protected IntegerVariable[] durations = Choco.constantArray(new int[]{7, 3, 8, 3, 1, 2, 1, 2, 1, 1});
    protected int nb_tasks = 10;
    protected int[][] temporalconstraints = {{0, 1}, {0, 2}, {0, 3}, {1, 4}, {3, 4}, {4, 6}, {6, 5}, {4, 7},
            {2, 7}, {4, 8}, {2, 8}, {7, 9}, {8, 9}, {5, 9}};

    protected TaskVariable[] tasks;

    public DeterministicPert() {
    }

    public DeterministicPert(int horizon) {
        this.horizon = horizon;
    }

    @Override
    public void setUp(Object parameters) {
        if (parameters != null) {
            Object[] params = (Object[]) parameters;
            this.horizon = (Integer) params[0];
            this.nb_tasks = (Integer) params[1];
            this.activities = (String[]) params[2];
            this.durations = Choco.constantArray((int[]) params[3]);
            this.temporalconstraints = (int[][]) params[4];
        }
    }

    @Override
    public void printDescription() {
        StringBuilder st = new StringBuilder(128);
        st.append("PERT (Programm Evaluation and Review Technique) is a model for project management \n" +
                "designed to analyze and represent the tasks involved in completing a given project.\n" +
                "It is commonly used in conjunction with the critical path method or CPM.\n\n");
        st.append(String.format("This specific resolution involves the following %d tasks -- task(duration):\n", nb_tasks));
        for (int i = 0; i < nb_tasks; i++) {
            st.append(String.format("%s(%s),\n", activities[i], durations[i].pretty()));
        }
        st.append("\nAccording to the following temporal contraints:\n");
        for (int i = 0; i < temporalconstraints.length; i++) {
            st.append(String.format("%s --> %s\n", activities[temporalconstraints[i][0]],
                    activities[temporalconstraints[i][1]]));
        }
        st.append("\n\n");
        LOGGER.info(st.toString());
        ChocoLogging.flushLogs();
    }

    @Override
    public void buildModel() {
        model = new CPModel();
        tasks = new TaskVariable[nb_tasks];
        for (int i = 0; i < nb_tasks; i++) {
            tasks[i] = makeTaskVar(activities[i], horizon, durations[i], Options.V_BOUND);
        }
        model.addVariable(tasks);
        addTemporalConstraints();
    }

    protected void addTemporalConstraints() {
        for (int t = 0; t < temporalconstraints.length; t++) {
            model.addConstraint(Choco.endsBeforeBegin(tasks[temporalconstraints[t][0]], tasks[temporalconstraints[t][1]]));
        }
    }

    /**
     * function used in junit tests.
     */
    public void requireUnaryResource() {
        model.addConstraints(Choco.disjunctive(tasks));
    }


    protected void criticalPathMethod(CPSolver solver) {
        //precedence are represente with linear constraints
        solver.setHorizon(horizon);
        solver.read(model);
        solver.createMakespan();
        solver.postMakespanConstraint();
        try {
            solver.propagate();
        } catch (ContradictionException e) {
            LOGGER.info("infeasible pert problem");
            e.printStackTrace();
        }
        try {
            //then we instantiate the makespan variable and compute slack times
            IntDomainVar e = solver.getMakespan();
            LOGGER.info(e.pretty());
            e.instantiate(e.getInf(), null, true);
            solver.propagate();
            LOGGER.info("\nCRITICAL PATH METHOD");
            //LOGGER.info(solver.pretty());
            //LOGGER.info(this);
        } catch (ContradictionException e) {
            LOGGER.severe("ERROR : problem should be feasible.");
            e.printStackTrace();
        }
    }

    public void criticalPathMethod() {
        criticalPathMethod((CPSolver) solver);
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        criticalPathMethod();
    }

    @Override
    public void solve() {
        solver.solveAll();
    }

    public final boolean isCritical(int i) {
        return solver.getVar(tasks[i]).isScheduled();
    }

    protected int getSlack(TaskVariable task) {
        return TaskUtils.getSlack(solver.getVar(task));
    }

    protected StringBuilder toString(int i) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(solver.getVar(tasks[i]).pretty());
        buffer.append("\tslack=").append(getSlack(tasks[i]));
        return buffer;
    }


    public void generateDottyFile() {
    }

    @Override
    public void prettyOut() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Cmax=").append(solver.getMakespan().getVal());
        buffer.append('\n');
        for (int i = 0; i < tasks.length; i++) {
            buffer.append(toString(i));
            buffer.append('\n');
        }
        LOGGER.info(buffer.toString());
    }

    public static void main(String[] args) {
        DeterministicPert pert = new DeterministicPert();
        pert.execute(null);
    }


}

