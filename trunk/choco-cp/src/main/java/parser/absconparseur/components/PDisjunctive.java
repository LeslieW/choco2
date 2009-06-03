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
package parser.absconparseur.components;


public class PDisjunctive extends PGlobalConstraint {
	private Task[] tasks;

	public PDisjunctive(String name, PVariable[] scope, Task[] tasks) {
		super(name, scope);
		this.tasks = tasks;
		for (Task task : tasks)
			task.setVariablePositions(scope);
	}

	public long computeCostOf(int[] tuple) {
		for (Task task : tasks) {
			if (task.evaluate(tuple) == 1)
				return 1;
			//task.displayEvaluations();
		}
		return 0;
	}

	public String toString() {
		String s = super.toString() + " : disjunctive\n\t";
        for (Task task : tasks) {
            s += "  [origin=" + computeStringRepresentationOf(task.getOrigin()) + " " + "duration=" + computeStringRepresentationOf(task.getDuration())  + "]\n\t";
        }
		s += "nbTasks=" + tasks.length;
		return s;
	}

    public Task[] getTasks() {
        return tasks;
}

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }
}