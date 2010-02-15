/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.kernel.model.variables.scheduling;

import java.util.Properties;

import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.ManagerFactory;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerVariable;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 23 janv. 2009
 * Since : Choco 2.0.1
 * Update : Choco 2.0.1
 */
public class TaskVariable extends MultipleVariables implements ITaskVariable<IntegerVariable>{

	protected String variableManager;

	public TaskVariable(String name, IntegerVariable start, IntegerVariable end, IntegerVariable duration) {
		super(true, true, start,end,duration);
		this.setName(name);
	}


	@Override
	public VariableManager<?> getVariableManager() {
		return ManagerFactory.loadVariableManager(variableManager);
	}

	/**
	 * Get the duration of the task
	 * @return
	 */
	public final IntegerVariable duration() {
		return (IntegerVariable) getVariable(2);
	}


	/**
	 * Get the end time of the task
	 * @return
	 */
	public final IntegerVariable end() {
		return (IntegerVariable) getVariable(1);
	}


	/**
	 * Get the start time of the task
	 * @return
	 */
	public final IntegerVariable start() {
		return (IntegerVariable)getVariable(0);
	}


	@Override
	public boolean isEquivalentTo(MultipleVariables mv) {
		if (mv instanceof TaskVariable) {
			TaskVariable t = (TaskVariable) mv;
			boolean r = (t.start().getIndex() == this.start().getIndex() &&
					t.duration().getIndex() == this.duration().getIndex());
			/*r &= (t.end() == null ||
                    this.end() == null ||
                    t.end().getIndex() == this.end().getIndex());*/
			return r;

		}
		return false;
	}

	@Override
	public String pretty() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(name).append(" {").append(start().pretty());
		buffer.append(" + ").append(duration().pretty());
		buffer.append(" = ").append(end().pretty());
		buffer.append('}');
		return buffer.toString();
	}

	@Override
	public final void findManager(Properties propertiesFile) {
		if (variableManager == null) {
			variableManager = propertiesFile.getProperty(VariableType.TASK.property);
		}
		if (variableManager == null) {
			throw new ModelException("Can not find " + type.property + " in application.properties");
		}
		super.findManager(propertiesFile);
	}

}
