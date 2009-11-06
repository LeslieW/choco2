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
package choco.kernel.solver.search.task;

import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Arnaud Malapert</br> 
 * @since 25 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class RandomizedTaskSelector extends TaskSelector {

	private final Random rnd;

	public RandomizedTaskSelector(Comparator<ITask> comparator) {
		super(comparator);
		rnd = new Random();
	}

	@Override
	public TaskVar selectTaskVar(Collection<TaskVar> vars) {
		final Iterator<TaskVar> iter= vars.iterator();
		if(iter.hasNext()) {
			TaskVar selected=iter.next();
			while(iter.hasNext()) {
				final TaskVar current = iter.next();
				final double cmp = comparator.compare(selected, current);
				int n = 2;
				if(cmp > 0) {
					selected = current;
					n=2;
				}else if(cmp == 0) {
					//random breaking tie
					final double p = 1/n;
					if(rnd.nextDouble()< p) {
						selected = current;
					}
					n++;
				}
			}
			return selected;
		}
		return null;
	}
}
