/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  Â°(..)  |                           *
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
package choco.cp.solver.constraints.global.scheduling.trees;

import choco.cp.solver.constraints.global.scheduling.trees.AbstractVilimTree.NodeType;
import choco.cp.solver.constraints.global.scheduling.trees.IVilimTree.TreeMode;
import choco.kernel.common.opres.graph.INodeLabel;
import choco.kernel.solver.variables.scheduling.ITask;

/**
 * @author Arnaud Malapert</br> 
 * @since 10 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 * @param <E>
 */
public abstract class AbstractVilimStatus<E> implements INodeLabel {

	protected NodeType type;

	protected ITask task;

	protected final E status;

	public AbstractVilimStatus(NodeType type, E status) {
		super();
		this.status = status;
		this.type = type;
	}

	public final NodeType getType() {
		return type;
	}

	public final void setType(NodeType type) {
		this.type = type;
	}

	public final ITask getTask() {
		return task;
	}

	public void setTask(ITask task) {
		this.task = task;
	}

	public final E getStatus() {
		return status;
	}
	private String getDotStyle() {
		switch ( getType()) {
		case LAMBDA: 
		case NIL: return "filled";
		default: return "solid";
		}
	}
	
	private String getFillColor() {
		switch ( getType()) {
		case LAMBDA: return "gray";
		case NIL: return "black";
		default: return "white";
		}
	}

	private String getFontColor() {
		switch ( getType() ) {
		case NIL: return "white";
		default: return "black";
		}
	}
	
	private String getBorderColor() {
		switch ( getType() ) {
		case INTERNAL: return "green";
		default: return "black";
		}
	}
	
	protected int getResetIntValue(TreeMode mode) {
		return mode.value() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
	}
	
	protected long getResetLongValue(TreeMode mode) {
		return mode.value() ? Long.MIN_VALUE : Long.MAX_VALUE;
	}

	protected void writeRow(StringBuilder buffer, String label1, String str1,String label2, String str2) {
		buffer.append('{').append(label1).append('=').append(str1);
		buffer.append('|');
		buffer.append(label2).append('=').append(str2).append('}');
	}
	
	protected String format(int value) {
		return value == Integer.MIN_VALUE ? "-inf" : value == Integer.MAX_VALUE ? "+inf" : String.valueOf(value);
	}
	
	protected String format(long value) {
		return value == Long.MIN_VALUE ? "-inf" : value == Long.MAX_VALUE ? "+inf" : String.valueOf(value);
	}
	
	@Override
	public String toDotty() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("shape=Mrecord,");
		buffer.append("style=").append(getDotStyle());
		buffer.append(", fillcolor=").append(getFillColor());
		buffer.append(", fontcolor=").append(getFontColor());
		buffer.append(", color=").append(getBorderColor());
		buffer.append(", label=\"{");
		if( getTask() != null) {
			buffer.append(getTask().getName());
			buffer.append('|');
		}
		writeDotStatus(buffer);
		buffer.append("}\"");
		return new String(buffer);
	}

	protected abstract void writeDotStatus(StringBuilder buffer);

	public abstract void reset();

	@Override
	public int getNbParameters() {
		return 3;
	}

	@Override
	public Object getParameter(int idx) {
		switch (idx) {
		case 0: return type;
		case 1: return status;
		case 2: return task;
		default:return null;
		}
	}

	@Override
	public void setParameter(int idx, Object parameter) {
		switch (idx) {
		case 0: this.setType((NodeType) parameter);break;
		case 1: throw new UnsupportedOperationException("cant change status");
		case 2: this.setTask((ITask) parameter);break;
		default:
			throw new ArrayIndexOutOfBoundsException("index out of range");
		}

	}

}
