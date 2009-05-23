package choco.kernel.solver.variables.scheduling;

import choco.kernel.common.IDotty;

public abstract class AbstractTask implements ITask, IDotty {

	protected final int id;

	/**
	 * A name may be associated to each variable.
	 */
	protected final String name;


	public AbstractTask(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}


	@Override
	public final int getID() {
		return id;
	}


	@Override
	public final String getName() {
		return name;
	}


	@Override
	public final int getSlack() {
		return getLST()-getEST();
	}

	@Override
	public final double getCentroid() {
		return ( (double) (getECT()+getLST()) )/2;
	}


	@Override
	public final boolean hasCompulsoryPart() {
		return getECT() > getLST();
	}

	protected final String format(int lb, int ub) {
		return lb == ub ? String.valueOf(lb) : lb + ".."+ub ;	
	}


	//*****************************************************************//
	//*******************  output functions (graph, string) **********//
	//***************************************************************//

	/**
	 * convert a task into .dot format.
	 * @param label  information appended to the default label
	 * @param format if <code>true</code> then format the node, else do nothing
	 * @param options the options passed to the .dot node.
	 * @return
	 */
	public final String toDotty(String label,boolean format,String... options) {
		StringBuilder b= new StringBuilder();
		b.append(getID()).append("[ shape=record,");
		//label
		b.append("label=\"{ ");
		b.append('{').append(getEST()).append('|');
		b.append(format(getMinDuration(), getMaxDuration()));
		b.append('|').append(getECT()).append('}');
		b.append('|').append(getName());
		if(!isScheduled()) {
			b.append('|');
			b.append('{').append(getLST()).append('|').append(getSlack()).append('|').append(getLCT()).append('}');
		}
		b.append(" }");
		if(label!=null) {b.append(label);}
		b.append(" \"");
		if(format){
			if(isScheduled()) {
				b.append(", style=bold, color=firebrick");
			}else {
				b.append(", style=dashed, color=navyblue");
			}
		}
		if(options!=null) {
			for (int i = 0; i < options.length; i++) {
				b.append(", ").append(options[i]);
			}
		}
		b.append(" ];");
		return new String(b);
	}

	@Override
	public String toDotty() {
		return toDotty(null, true);
	}


	@Override
	public String pretty() {
		final StringBuilder  b = new StringBuilder();
		b.append(this.getName()).append(":[");
		b.append(format(getEST(), getLST())).append(" + ");
		b.append(format(getMinDuration(), getMaxDuration())).append(" -> ");
		b.append(format(getECT(), getLCT())).append("]");
		return new String(b);
	}


	@Override
	public String toString() {
		return getName()+"["+getEST()+", "+getLCT()+"]";
	}


}
