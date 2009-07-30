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
package choco.visu;

import choco.IObservable;
import choco.IObserver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.search.ISearchLoop;
import choco.kernel.solver.search.IntBranchingTrace;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.visu.searchloop.ObservableStepSearchLoop;
import choco.visu.variables.VisuVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 23 oct. 2008
 * Since : Choco 2.0.1
 *
 * This class is the main observer of the visualization.
 * Every modification over variables are observed and send to
 * the specific vizualisation.
 *
 */
public final class Tracer implements IObserver {

	protected ArrayList<VisuVariable> vars;
	protected HashMap<Var, VisuVariable> mapvars;
	protected int breaklength;

	public Tracer() {
		this.breaklength = 10;
	}

	/**
	 * Change the break length value
	 * @param breaklength the new break length
	 */
	public final void setBreaklength(final int breaklength) {
		this.breaklength = breaklength;
	}

	/**
	 * Set the variable event queue to observe
	 *
	 * @param observable
	 */
	public final void addObservable(final IObservable observable) {
		observable.addObserver(this);
		observable.notifyObservers(null);
	}

	/**
	 * Set the variables to draw
	 *
	 * @param vars
	 */
	public final void setVariables(final Collection<VisuVariable> vars) {
		if(this.vars == null){
			this.vars = new ArrayList();
			mapvars = new HashMap();
		}

		final Iterator it = vars.iterator();
		while(it.hasNext()){
			VisuVariable vv = (VisuVariable) it.next();
			this.vars.add(vv);
			mapvars.put(vv.getSolverVar(), vv);
		}
	}

	/**
	 * This method is called whenever the observed object is changed. An
	 * application calls an <tt>Observable</tt> object's
	 * <code>notifyObservers</code> method to have all the object's
	 * observers notified of the change.
	 *
	 * @param o   the observable object.
	 * @param arg an argument passed to the <code>notifyObservers</code>
	 *            method.
	 *            <p/>
	 *            In that case, it redraw the canvas of the modified variable
	 *            or redraw every canvas if "fail" (arg = 1).
	 */
	public final void update(final IObservable o, final Object arg) {
		haveBreak();
		if(arg instanceof VarEvent){
			VarEvent ve = (VarEvent)arg;
			VisuVariable v =mapvars.get(ve.getModifiedVar());
			if(v != null){
				v.refresh(ve.getEventType());
			}
		}else if(arg instanceof ISearchLoop){
			throw new SolverException("not yet implemented");
			//IntBranchingTrace ctx = ((ObservableStepSearchLoop)arg).getCtx();
			//            if(ctx==null)return;
			//            Object ob = ctx.getBranchingObject();
			//            VisuVariable v = null;
			//            if(ob instanceof IntDomainVar){
			//                v = mapvars.get(ob);
			//            }else if (ob instanceof Object[]){
			//                v = mapvars.get(((Object[])ob)[0]);
			//            }
			//            if(v != null){
			//                v.refresh(arg);
			//            }
		}
	}

	/**
	 * Create a visual pause
	 */
	private final void haveBreak(){
		try {
			Thread.sleep(this.breaklength);
		} catch (InterruptedException e) {

		}
	}
}
