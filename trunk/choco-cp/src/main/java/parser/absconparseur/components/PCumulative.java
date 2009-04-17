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
	private Task[] tasks;

	private int limit;

	public static class Task {
		private Object origin; // may be null if absent, an Integer or a PVariable

		private int originPositionInScope;

		private int originValue;

		private Object duration;

		private int durationPositionInScope;

		private int durationValue;

		private Object end;

		private int endPositionInScope;

		private int endValue;

		private Object height;

		private int heightPositionInScope;

		private int heightValue;

		public Task(Object origin, Object duration, Object end, Object height) {
			this.origin = origin;
			this.duration = duration;
			this.end = end;
			this.height = height;
		}

		private void setVariablePositions(int originPositionInScope, int durationPositionInScope, int endPositionInScope, int heightPositionInScope) {
			this.originPositionInScope = originPositionInScope;
			this.durationPositionInScope = durationPositionInScope;
			this.endPositionInScope = endPositionInScope;
			this.heightPositionInScope = heightPositionInScope;
		}

		private int evaluate(int[] tuple) {
			if (origin != null)
				originValue = origin instanceof Integer ? (Integer) origin : tuple[originPositionInScope];
			if (duration != null)
				durationValue = duration instanceof Integer ? (Integer) duration : tuple[durationPositionInScope];
			if (end != null)
				endValue = end instanceof Integer ? (Integer) end : tuple[endPositionInScope];
			if (origin != null && duration != null && end != null && originValue + durationValue != endValue)
				return 1;
			if (origin == null)
				originValue = endValue - durationValue;
			if (end == null)
				endValue = originValue + durationValue;
			heightValue = height instanceof Integer ? (Integer) height : tuple[heightPositionInScope];
			return 0;
		}

		public void displayEvaluations() {
			LOGGER.info(originValue + " " + durationValue + " " + endValue + " " + heightValue);
			
		}

        public Object getOrigin() {
            return origin;
        }

        public Object getDuration() {
            return duration;
        }

        public Object getEnd() {
            return end;
        }

        public Object getHeight() {
            return height;
        }

        //		public String toString() {
//			String s = "  [origin=" + computeStringRepresentationOf(origin) + " " + "duration=" + computeStringRepresentationOf(duration) + " ";
//			return s + "end=" + computeStringRepresentationOf(end) + " " + "height=" + computeStringRepresentationOf(height) + "]\n\t";
//		}
	}

	public PCumulative(String name, PVariable[] scope, Task[] tasks, int limit) {
		super(name, scope);
		this.tasks = tasks;
		for (Task task : tasks)
			task.setVariablePositions(computeObjectPositionInScope(task.origin), computeObjectPositionInScope(task.duration), computeObjectPositionInScope(task.end),
					computeObjectPositionInScope(task.height));
		this.limit = limit;
	}

	public int computeCostOf(int[] tuple) {
		for (Task task : tasks) {
			if (task.evaluate(tuple) == 1)
				return 1;
			//task.displayEvaluations();
		}
		for (int i = 0; i < tasks.length; i++) {
			for (int period = tasks[i].originValue; period < tasks[i].endValue; period++) {
				int heightSum = tasks[i].heightValue;
				for (int j = i + 1; j < tasks.length; j++) {
					if (period >= tasks[j].originValue && period < tasks[j].endValue)
						heightSum += tasks[j].heightValue;
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
		for (int i = 0; i < tasks.length; i++) {
			s += "  [origin=" + computeStringRepresentationOf(tasks[i].origin) + "(" + tasks[i].originPositionInScope + ")" + " duration=" + computeStringRepresentationOf(tasks[i].duration) + " ";
			s += "end=" + computeStringRepresentationOf(tasks[i].end) + " " + "height=" + computeStringRepresentationOf(tasks[i].height) + "]\n\t";
		}
		s += "nbTasks=" + tasks.length + " limit=" + limit;
		return s;
	}

    public Task[] getTasks() {
        return tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }

    public int getLimit() {
        return limit;
    }
}
