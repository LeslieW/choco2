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
package choco.cp.solver.search.task;

import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.variables.scheduling.ITask;

import gnu.trove.TIntIntHashMap;

import java.lang.reflect.Array;
import java.util.*;



/**
 * Probabilistic profile of an unary resource.
 * @author Arnaud Malapert
 *
 */
public final class ProbabilisticProfile  {

	private final TIntIntHashMap indexMap = new TIntIntHashMap();


	private final List<EventRPP> function = new LinkedList<EventRPP>();

	private final EventDataStructure[] structL;

	private double slope;

	private double gap;

	private int coordinate;

	private final BitSet involved = new BitSet();

	private final MaximumDataStruct max = new MaximumDataStruct();

	public IPrecedenceStore precStore;

	public int minimalSize = 1;

	/**
	 *
	 */
	public ProbabilisticProfile(ITask[] tasks) {
		this(Arrays.asList(tasks));
	}

	public ProbabilisticProfile(List<? extends ITask> tasks) {
		super();
		structL=new EventDataStructure[tasks.size()];
		for (int i = 0; i < structL.length; i++) {
			structL[i] = new EventDataStructure(tasks.get(i));
			indexMap.put(tasks.get(i).getID(), i);
		}
	}


	public ProbabilisticProfile(Solver solver) {
		super();
		structL=new EventDataStructure[solver.getNbTaskVars()];
		for (int i = 0; i < structL.length; i++) {
			structL[i] = new EventDataStructure(solver.getTaskVar(i));
			indexMap.put(solver.getTaskVar(i).getID(), i);
		}
	}




	public final double getIndividualContribution(final ITask task,final int coordinate) {
		return getEDS(task).getIndividualContribution(coordinate);
	}

	protected EventDataStructure getEDS(final ITask task) {
		return structL[indexMap.get(task.getID())];
	}

	public final void generateEventsList(IResource<? extends ITask> rsc) {
		function.clear();
		Iterator<? extends ITask> iter = rsc.getTaskIterator();
		while(iter.hasNext()) {
			function.addAll(Arrays.asList(getEDS(iter.next()).events));
		}
		Collections.sort(function);
	}

	protected void resetSweepData() {
		slope=gap= 0;
		coordinate=Integer.MIN_VALUE;
		this.involved.clear();
	}


	protected void handleEvent(final EventRPP e,final ListIterator<EventRPP> iter) {
		handleEvent(e);
		EventRPP next;
		while(iter.hasNext()) {
			next=iter.next();
			if(next.coordinate>e.coordinate) {
				iter.previous();
				break;
			}else {
				handleEvent(next);
			}

		}

	}

	protected void handleEventMax(final EventRPP e,final ListIterator<EventRPP> iter) {
		handleEventMax(e);
		EventRPP next;
		while(iter.hasNext()) {
			next=iter.next();
			if(next.coordinate>e.coordinate) {
				iter.previous();
				break;
			}else {
				handleEventMax(next);
			}

		}

	}


	protected void handleEvent(final EventRPP e) {
		switch (e.type) {
		case EventRPP.START_EVENT : {
			slope+=e.slope;
			if(e.gap>0) {gap+=e.gap;}
			break;
		}
		case EventRPP.END_EVENT : {
			slope-=e.slope;
			if(e.gap<0) {gap+=e.gap;}
			break;
		}
		default:
			throw new IllegalArgumentException("can't handle event");
		}

	}

	protected void handleEventMax(final EventRPP e) {
		handleEvent(e);
		if(e.task!=null) {
			if(e.type == EventRPP.START_EVENT) {
				involved.set(e.task.getID());
			}else if(e.type == EventRPP.END_EVENT) {
				involved.set(e.task.getID(), false);
			}else {
				throw new SolverException("unknown event");
			}
		}


	}


	public void initializeEvents() {
		for (EventDataStructure eds : structL) {
			eds.reset();
		}
	}

	protected final void sweep() {
		final ListIterator<EventRPP> iter=function.listIterator();
		while(iter.hasNext()) {
			final EventRPP e=iter.next();
			update(e.coordinate);
			handleEventMax(e,iter);
			if(gap>max.value && involved.cardinality() >= minimalSize && isValid() ) {
				max.value=gap;
				max.coordinate=e.coordinate;
				max.involved.clear();
				max.involved.or(involved);
			} 
		}
	}
	/**
	 * compute a maximum using the specified set checker
	 */
	public final void computeMaximum(IResource<?>... resources) {
		//reset Events and max data struc
		max.reset();
		//lazy computation of the maximum over all resource
		for (IResource<?> rsc : resources) {
			generateEventsList(rsc);
			resetSweepData();
			sweep();
		}
	}


	public boolean isValid() {
		if(precStore != null) {
			for (int i = involved.nextSetBit(0); i >= 0; i = involved
			.nextSetBit(i + 1)) {
				final ITask t1 = structL[ indexMap.get(i)].task;
				for (int j = involved.nextSetBit(i+1); j >= 0; j = involved
				.nextSetBit(j + 1)) {
					final ITask t2 = structL[ indexMap.get(j)].task;
					if(precStore.isReified(t1, t2)) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}

	public double getMaxProfileValue() {
		return max.value;
	}

	public int getMaxProfileCoord() {
		return max.coordinate;
	}

	public List<ITask> getMaxProfInvolved() {
		List<ITask> list = new LinkedList<ITask>();
		for (int i = max.involved.nextSetBit(0); i >= 0; i = max.involved.nextSetBit(i + 1)) {
			list.add( structL[ indexMap.get(i)].task);
		}
		return list;
	}

	public double compute(final int x) {
		this.resetSweepData();
		final ListIterator<EventRPP> iter=function.listIterator();
		while(iter.hasNext()) {
			final EventRPP e=iter.next();
			if(e.coordinate<=x) {
				update(e.coordinate);
				handleEvent(e);
			} else {
				break;
			}
		}
		update(x);
		return gap;
	}

	private double shift(final int x) {
		return (x-coordinate)*slope;
	}

	protected void update(final int x) {
		gap+=shift(x);
		coordinate=x;
	}
	private void drawPoint(final StringBuilder buffer) {
		buffer.append(coordinate).append(' ').append(gap).append('\n');
	}

	public StringBuilder draw() {
		this.resetSweepData();
		final StringBuilder buffer=new StringBuilder();
		final ListIterator<EventRPP> iter=function.listIterator();
		while(iter.hasNext()) {
			final EventRPP e=iter.next();
			update(e.coordinate);
			drawPoint(buffer);
			handleEvent(e,iter);
			drawPoint(buffer);

		}
		return buffer;
	}






	protected static class EventDataStructure  {

		protected final ITask task;

		protected EventRPP[] events;

		/**
		 * @param task
		 */
		public EventDataStructure(final ITask task) {
			super();
			this.task = task;
			events= (EventRPP[]) Array.newInstance(EventRPP.class, 4);
			events[0]=new EventRPP(EventRPP.START_EVENT,task);
			events[1]=new EventRPP(EventRPP.END_EVENT);
			events[2]=new EventRPP(EventRPP.START_EVENT);
			events[3]=new EventRPP(EventRPP.END_EVENT,task);
		}


		public double getIndividualContribution(final int x) {
			double contrib=0;
			if(x>=events[0].coordinate && x<events[3].coordinate) {
				contrib+=events[0].gap;
				contrib+= (events[0].slope)*(Math.min(x,events[1].coordinate)-events[0].coordinate);
				if(x>=events[2].coordinate) {
					contrib+= (events[2].slope)*(x-events[2].coordinate);
				}
			}
			return contrib;
		}


		private void set(final int idx,final int x,final double slope,final double gap) {
			events[idx].coordinate=x ;
			events[idx].gap=gap;
			events[idx].slope=slope;
		}

		public void reset() {
			if(task.getMinDuration()>0) {
				final double std=task.getLST()-task.getEST()+1;
				final double gap=1/std;
				//double slope=1/std;
				final double slope= std<= task.getMinDuration() ? 1/std : (task.getMinDuration()-1)/(std*task.getMinDuration());
				set(0, task.getEST(), slope, gap);
				set(1, task.getLST(), slope,0);
				set(2, task.getECT(), -slope,0);
				set(3, task.getLCT(), -slope, -gap);


			}

		}


	}



	/**
	 * Event for the resource probabilistic profile
	 * @author Arnaud Malapert : arnaud(dot)malapert(at)emn(dot)fr
	 *
	 */
	protected static class EventRPP implements Comparable<EventRPP>{

		public final static int START_EVENT=0;

		public final static int END_EVENT=1;

		public final ITask task;

		public final int type;

		public int coordinate;

		public double slope;

		public double gap;

		public EventRPP(final int type) {
			this(type, 0, 0, 0,null);
		}

		public EventRPP(final int type,final ITask task) {
			this(type, 0, 0, 0,task);
		}

		/**
		 * @param type
		 * @param coordinates
		 * @param slope
		 * @param gap
		 */
		public EventRPP(final int type, final int coordinates, final double slope, final double gap,final ITask task) {
			super();
			this.type = type;
			this.coordinate = coordinates;
			this.slope = slope;
			this.gap = gap;
			this.task=task;
		}



		/**
		 * @return the coordinate
		 */
		public final int getCoordinates() {
			return coordinate;
		}


		/**
		 * @param coordinates the coordinate to set
		 */
		public final void setCoordinates(final int coordinates) {
			this.coordinate = coordinates;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final StringBuilder buffer=new StringBuilder();
			switch (type) {
			case START_EVENT: buffer.append("START ");break;
			case END_EVENT: buffer.append("END ");break;
			default:
				buffer.append("ERROR");
			break;
			}
			buffer.append(coordinate).append(" (");
			buffer.append(slope).append(',').append(gap).append(')');
			return buffer.toString();
		}


		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final EventRPP o) {
			final int x1=coordinate;
			final int x2=o.getCoordinates();
			if(x1<x2) {return -1;}
			else if(x1>x2) {return 1;}
			else {
				return 0;
			}
		}
	}

	protected static class MaximumDataStruct {

		public int coordinate;

		public double value;

		public final BitSet involved = new BitSet();


		public void reset() {
			coordinate=Integer.MIN_VALUE;
			value=Double.MIN_VALUE;
			involved.clear();
		}

	}


}

