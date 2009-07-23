package choco.visu.searchloop;

import java.util.Vector;
import java.util.logging.Level;

import choco.IObserver;
import choco.cp.solver.search.AbstractSearchLoop;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.solver.branch.StrategyTest;

public class ObservableStepSearchLoop2 extends AbstractSearchLoop implements IObservableStepSearchLoop {


	private final Vector<IObserver> obs;

	private AbstractSearchLoop internalSearchLoop;

	private int previousNbSolutions;

	private Step action = Step.PAUSE; // 0: pause; 1: next; 2: play

	//for tree search vizualisation
	public State state;

	public ObservableStepSearchLoop2(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
		obs = new Vector<IObserver>();
	}

	public final void test() {
		state = State.NONE;
		//if action is pause, wait
		while(action.equals(Step.PAUSE)){
			/*wait*/
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, "The search was interrupted while waiting for a GUI event" , e);
			}
		}
		// if action is next, pause after one execution
		if(action.equals(Step.NEXT)){
			action=Step.PAUSE;
		}
	}

	////////////////////////////////////////TO INTERACT WITH GUI////////////////////////////////////////////////////////

	@Override
	public void initialize() {
		super.initialize();
		action = Step.PAUSE;
		internalSearchLoop.initialize();
	}




	@Override
	public void downBranch() {
		state = State.DOWN;
		internalSearchLoop.downBranch();
		notifyObservers(this);

	}




	@Override
	public Boolean endLoop() {
		state = State.END;
		notifyObservers(this);
		return internalSearchLoop.endLoop();
	}




	@Override
	public void initLoop() {
		internalSearchLoop.initLoop();
		previousNbSolutions = searchStrategy.getSolutionCount();

	}




	@Override
	public void initSearch() {
		internalSearchLoop.initSearch();

	}




	@Override
	public void openNode() {
		internalSearchLoop.openNode();
		if(searchStrategy.getSolutionCount() > previousNbSolutions) {
			state = State.SOLUTION;
		}
		notifyObservers(this);
		if(action.equals(Step.PAUSE)){
			action=Step.NEXT;
		}
	}




	@Override
	public void restart() {
		state = State.RESTART;
		notifyObservers(this);
		internalSearchLoop.restart();
	}




	@Override
	public void upBranch() {
		state = State.UP;
		internalSearchLoop.upBranch();
		notifyObservers(this);


	}


	////////////////////////////////////////TO INTERACT WITH GUI////////////////////////////////////////////////////////

	public void runStepByStep(){
		action = Step.NEXT;
	}

	public void runForAWhile(){
		action = Step.PLAY;
	}

	public void pause(){
		action = Step.PAUSE;
	}

	public void setAction(Step action) {
		this.action = action;
	}


	////////////////////////////////IObservable implementation////////////////////////////

	/**
	 * Adds an observer to the set of observers for this object, provided
	 * that it is not the same as some observer already in the set.
	 * The order in which notifications will be delivered to multiple
	 * observers is not specified. See the class comment.
	 *
	 * @param o an observer to be added.
	 * @throws NullPointerException if the parameter o is null.
	 */
	public synchronized void addObserver(IObserver o) {
		if (o == null)
			throw new NullPointerException();
		if (!obs.contains(o)) {
			obs.addElement(o);
		}
	}

	/**
	 * If this object has changed, as indicated by the
	 * <code>hasChanged</code> method, then notify all of its observers
	 * and then call the <code>clearChanged</code> method to indicate
	 * that this object has no longer changed.
	 * <p/>
	 * Each observer has its <code>update</code> method called with two
	 * arguments: this observable object and the <code>arg</code> argument.
	 *
	 * @param arg any object.
	 * @see choco.IObserver#update(choco.IObservable , Object)
	 */
	public void notifyObservers(Object arg) {
		/*
		 * a temporary array buffer, used as a snapshot of the state of
		 * current Observers.
		 */
		Object[] arrLocal;

		synchronized (this) {
			/* We don't want the Observer doing callbacks into
			 * arbitrary code while holding its own Monitor.
			 * The code where we extract each Observable from
			 * the Vector and store the state of the Observer
			 * needs synchronization, but notifying observers
			 * does not (should not).  The worst result of any
			 * potential race-condition here is that:
			 * 1) a newly-added Observer will miss a
			 *   notification in progress
			 * 2) a recently unregistered Observer will be
			 *   wrongly notified when it doesn't care
			 */
			arrLocal = obs.toArray();
		}

		for (int i = arrLocal.length-1; i>=0; i--)
			((IObserver)arrLocal[i]).update(this, arg);
	}

}
