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

public class PCumulative extends PGlobalConstraint {
	private PTask[] tasks;

	private int limit;

	public PCumulative(String name, PVariable[] scope, PTask[] tasks, int limit) {
		super(name, scope);
		this.tasks = tasks;
		for (PTask task : tasks)
			task.setVariablePositions(scope); 
		this.limit = limit;
	}

	public long computeCostOf(int[] tuple) {
		for (PTask task : tasks) {
			if (task.evaluate(tuple) == 1)
				return 1;
			//task.displayEvaluations();
		}
		for (int i = 0; i < tasks.length; i++) {
			for (int period = tasks[i].getOriginValue(); period < tasks[i].getEndValue(); period++) {
				int heightSum = tasks[i].getHeightValue();
				for (int j = i + 1; j < tasks.length; j++) {
					if (period >= tasks[j].getOriginValue() && period < tasks[j].getEndValue())
						heightSum += tasks[j].getHeightValue();
				}
				if (heightSum > limit) {
					//LOGGER.info(" i = " + i + " time = " + period);
					return 1;
				}
			}
		}
		return 0;
	}

	public String toString() {
		String s = super.toString() + " : cumulative\n\t";
        for (PTask task : tasks) {
            s += "  [origin=" + computeStringRepresentationOf(task.getOrigin()) + " " + "duration=" + computeStringRepresentationOf(task.getDuration()) + " ";
            s += "end=" + computeStringRepresentationOf(task.getEnd()) + " " + "height=" + computeStringRepresentationOf(task.getHeight()) + "]\n\t";
        }
		s += "nbTasks=" + tasks.length + " limit=" + limit;
		return s;
	}

    public PTask[] getTasks() {
        return tasks;
}

    public void setTasks(PTask[] tasks) {
        this.tasks = tasks;
    }

    public int getLimit() {
        return limit;
    }
}
