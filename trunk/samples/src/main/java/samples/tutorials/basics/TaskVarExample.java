package samples.tutorials.basics;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import samples.tutorials.PatternExample;

import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: njussien
 * Date: 8 mai 2010
 * Time: 22:53:03
 * To change this template use File | Settings | File Templates.
 */
public class TaskVarExample extends PatternExample{

    int n, m; // n jobs, m machines
    int[][] durations;
    TaskVariable[][] tasks;
    IntegerVariable makespan;

    @Override
    public void setUp(Object parameters) {
        super.setUp(parameters);
        if (parameters instanceof int[][]) {
            durations = (int[][]) parameters;
            n = durations.length;
            m = durations[0].length;

        }
        else if (parameters instanceof int[]) {
            n = ((int[]) parameters)[0];
            m = ((int[]) parameters)[1];
            generateRandomDurations(n, m);

        } else {
            n = 3;
            m = 3;
            generateRandomDurations(n, m);
        }
    }

    private void generateRandomDurations(int n, int m) {
        durations = new int[n][m];
        for (int i = 0; i < n ; i++) {
            for (int j = 0 ; j < m ; j++) {
                durations[i][j] = 1 + (int) Math.round(Math.random() * 9);
            }
        }
    }

    @Override
    public void printDescription() {
        super.printDescription();

        LOGGER.info("The flow shop scheduling problem involves n jobs to be scheduled on m machines.");
        LOGGER.info("Each job must follow the same flow on machines: from machine 1 to machine m");
        LOGGER.info("Each machine can only handle one job at a time");
        LOGGER.info("Knowing the durations of each task, the aim is to minimize the makespan.");

        LOGGER.info("\nHere is an instance with " + n + " jobs and " + m + " machines");

        printInstance();
    }

    private void printInstance() {

        StringBuffer aff = new StringBuffer();

        for (int i = 0; i < n ; i++) {
            aff.append(MessageFormat.format("JOB {0}: durations ", i));
            for (int j = 0 ; j < m - 1 ; j++) {
                aff.append(MessageFormat.format("{0} - ", durations[i][j]));
            }
            aff.append(MessageFormat.format("{0}\n", durations[i][m-1]));
        }
        LOGGER.info(aff.toString());

    }

    @Override
    public void buildModel() {

        model = new CPModel();

        tasks = Choco.makeTaskVarArray("t", 0, computeUB(durations), durations);
        makespan = Choco.makeIntVar("makespan", 0, computeUB(durations));

        // setting up constraints
        for (int i = 0; i < n ; i++) {
            for (int j = 0; j < m - 1; j++) {
                // the flow constraints ... all jobs are sequences on machine 1, then 2, etc.
                model.addConstraint(Choco.startsAfterEnd(tasks[i][j+1], tasks[i][j]));
            }
            // computing the makespan
            model.addConstraint(Choco.leq(tasks[i][m-1].end(), makespan));

        }

        for (int j = 0 ; j < m ; j++){
            // the machine constraint - one job at a time on each machine
            model.addConstraint(Choco.disjunctive(ArrayUtils.getColumn(tasks,j)));
        }

    }

    private int computeUB(int[][] durations) {
        int lb = 0;
        for (int i = 0; i < durations.length; i++) {
            for (int j = 0; j < durations[i].length; j++){
                lb += durations[i][j];
            }
        }
        return lb; 
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);

    }

    @Override
    public void solve() {
        solver.minimize(solver.getVar(makespan), false);

        solver.pretty(); 
    }

    @Override
    public void prettyOut() {

        LOGGER.info("\nThe minimum makespan for this instance is " + solver.getVar(makespan).getVal());

        printSolution();

    }

    private void printSolution() {
        StringBuffer aff = new StringBuffer();

        for (int i = 0 ; i < n ; i ++) {
            aff.append(MessageFormat.format("JOB {0}: ", i));
            for (int j = 0; j < m ; j++) {
                aff.append(MessageFormat.format("{0} ",solver.getVar(tasks[i][j]).pretty()) );
            }
            aff.append("\n");
        }

        aff.append("\n");

        for (int j = 0 ; j < m ; j ++) {
            aff.append(MessageFormat.format("MACHINE {0}: ", j));
            for (int i = 0; i < n ; i++) {
                aff.append(MessageFormat.format("{0} ",solver.getVar(tasks[i][j]).pretty()) );
            }
            aff.append("\n");
        }

        LOGGER.info(aff.toString());

    }

    public static void main(String[] args) {
		new TaskVarExample().execute();
	}
}
