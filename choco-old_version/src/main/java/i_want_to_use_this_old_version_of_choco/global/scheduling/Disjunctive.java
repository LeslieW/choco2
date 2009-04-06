/**
 *
 */
package i_want_to_use_this_old_version_of_choco.global.scheduling;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractTree;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.awt.*;
import java.util.List;



/**
 * The Class Disjunctive implements a disjunctive constraint.
 * The algorithms are based on : <br><br>
 * Vilim, P.; Bartak, R. & Cepek, O.<br>
 * Extension of O ( n log n ) Filtering Algorithms for the Unary Resource Constraint to Optional Activities <br>
 * <i> Constraints</i>, <b>2005</b>, 10, 403-425<br><br>
 *  There is four rules :
 * <ul><li>Edge Finding</li><li>Not First/Not Last</li> <li>Detectable Precedence</li><li>Overload Checking</i></ul>
 * Edge Finding realizes overload checking, this two rules are exclusive,<i>i.e.</i> you can't use them together.
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 */
public class Disjunctive extends UnaryResourceConstraint {


	protected final DisjunctiveSettings settings;


	protected final DisjRules rules;

	protected boolean noFixPoint;

	/**
	 * Instantiates a new disjunctive.
	 *
	 * @param vars the vars the tasks involved in the constraint
	 * @param processingTimes their processing times
	 */
	public Disjunctive(final IntDomainVar[] vars,final int[] processingTimes) {
		this(vars,processingTimes,new DisjunctiveSettings());
	}

	/**
	 * Instantiates a new disjunctive.
	 *
	 * @param vars the vars the tasks involved in the constraint
	 * @param processingTimes their processing times
	 */
	public Disjunctive(final IntDomainVar[] vars, final int[] processingTimes, final DisjunctiveSettings settings) {
		super(vars,processingTimes);
		this.settings=settings;
		rules=new DisjRules(this,this.vars, this.processingTimes);
	}



	/**
	 * Instantiates a new disjunctive.
	 *
	 * @param vars the vars the tasks involved in the constraint
	 * @param processingTimes their processing times
	 */
	protected Disjunctive(final IntDomainVar[] vars,final IntDomainVar objective,final int[] processingTimes, final DisjunctiveSettings settings) {
		super(vars,processingTimes,objective);
		//TODO Pas très propre cala suppose implicitement un constrcuteur pour des sous-classes : Hadrien une idée ?
		this.settings=settings;
		rules=new DisjRules(this,vars, this.processingTimes);
	}






	//****************************************************************//
	//********************** RULES  **********************************//
	//****************************************************************//


	private final void applyOtherRules() throws ContradictionException {
		boolean change=false;
		rules.disjTreeT.setEctOrLst(AbstractTree.ECT_TREE);
		rules.disjTreeT.update();

		if(settings.overloadChecking()) {
			if(!rules.overloadChecking()) {this.fail();}
			change=true;
		}

		if(settings.notFirstNotLast()) {
			if(change) {rules.disjTreeT.reset();}
			else{change=true;}
			noFixPoint |= updateLCT(rules.notLast());
		}

		if(settings.detectablePrecedence()) {
			if(change) {rules.disjTreeT.reset();}
			noFixPoint |= updateEST(rules.detectablePrecedenceEST());
		}

		rules.disjTreeT.setEctOrLst(AbstractTree.LST_TREE);
		change=true;
		if(settings.notFirstNotLast()) {
			rules.disjTreeT.update();
			noFixPoint |= updateEST(rules.notFirst());
			change=false;
		}

		if(settings.detectablePrecedence()) {
			if(change) {rules.disjTreeT.update();}
			else {rules.disjTreeT.reset();}
			noFixPoint |= updateLCT(rules.detectablePrecedenceLCT());
		}
	}


	private final void applyEdgeFindingEST() throws ContradictionException {
		rules.disjTreeTL.setEctOrLst(AbstractTree.ECT_TREE);
		rules.disjTreeTL.update();
		final List<Point> newBounds=rules.edgeFindingEST();
		if(newBounds!=null) {
			this.updateEST(newBounds);
		}
		else {this.fail();}
	}

	private final void applyEdgeFindingLCT() throws ContradictionException {
		rules.disjTreeTL.setEctOrLst(AbstractTree.LST_TREE);
		rules.disjTreeTL.update();
		final List<Point> newBounds = rules.edgeFindingLCT();
		if(newBounds!=null) {
			this.updateLCT(newBounds);
		}
		else {this.fail();}
	}

	protected final void applyEdgeFinding() throws ContradictionException {
		applyEdgeFindingEST();
		applyEdgeFindingLCT();
	}

	private final void applySingleRule() throws ContradictionException {
		switch (settings.getSingleRule()) {
		case DisjunctiveSettings.NOT_FIRST: {
			rules.disjTreeT.setEctOrLst(AbstractTree.LST_TREE);
			rules.disjTreeT.update();
			updateEST(rules.notFirst());
			break;
		}
		case DisjunctiveSettings.NOT_LAST: {
			rules.disjTreeT.setEctOrLst(AbstractTree.ECT_TREE);
			rules.disjTreeT.update();
			updateLCT(rules.notLast());
			break;
		}
		case DisjunctiveSettings.DP_EST: {
			rules.disjTreeT.setEctOrLst(AbstractTree.ECT_TREE);
			rules.disjTreeT.update();
			updateEST(rules.detectablePrecedenceEST());
			break;
		}
		case DisjunctiveSettings.DP_LCT: {
			rules.disjTreeT.setEctOrLst(AbstractTree.LST_TREE);
			rules.disjTreeT.update();
			updateLCT(rules.detectablePrecedenceLCT());
			break;
		}
		case DisjunctiveSettings.EF_EST: {
			applyEdgeFindingEST();
			break;
		}
		case DisjunctiveSettings.EF_LCT: {
			applyEdgeFindingLCT();
			break;
		}
		default:

			System.err.println("no rule activated (SINGLE_RULE settings) in Disjunctive constraint");
		break;
		}
		rules.disjTreeT.update();
	}

	//****************************************************************//
	//********* EVENTS - PROPAGATION *********************************//
	//****************************************************************//


	/**
	 * Update LCT.
	 *
	 * @param newSups a list of points. the x coordinate gives the index of the task and the y its new sup value.
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final boolean updateLCT(final List<Point> newSups) throws ContradictionException {
		for (Point p : newSups) {
			if(getLCT(p.x)>p.y) {
				noFixPoint|= updateLCT(p.x, p.y);
			}
		}
		return noFixPoint;
	}

	/**
	 * Update EST.
	 *
	 * @param newInfs a list of points. the x coordinate gives the index of the task and the y its new inf value
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final boolean updateEST(final List<Point> newInfs) throws ContradictionException {
		for (Point p : newInfs) {
			noFixPoint|=  updateEST(p.x, p.y);
		}
		return noFixPoint;
	}

	/**
	 * Propagate.
	 * called for any bound events.
	 * @throws ContradictionException the contradiction exception
	 *
	 * @see choco.integer.constraints.AbstractLargeIntConstraint#propagate()
	 */
	@Override
	public void propagate() throws ContradictionException {
		//Solver.flushLogs();
		noFixPoint=true;
		while(noFixPoint) {
			noFixPoint=false;
			if(settings.singleRule()) {
				applySingleRule();
			}else {
				if(settings.useDPorNFNLorOLC()) {
					applyOtherRules();
				}
				if(settings.edgeFinding()) {
					applyEdgeFinding();
				}
			}
		}
	}




	/**
	 * Awake on inf.
	 *
	 * @param idx the idx
	 *
	 * @throws ContradictionException the contradiction exception
	 *
	 * @see choco.integer.constraints.AbstractIntConstraint#awakeOnInf(int)
	 */
	@Override
	public void awakeOnInf(final int idx) throws ContradictionException {
		this.constAwake(false);
	}

	/**
	 * Awake on inst.
	 *
	 * @param idx the idx
	 *
	 * @throws ContradictionException the contradiction exception
	 *
	 * @see choco.integer.constraints.AbstractIntConstraint#awakeOnInst(int)
	 */
	@Override
	public void awakeOnInst(final int idx) throws ContradictionException {
		this.constAwake(false);
	}



	/**
	 * Awake on sup.
	 *
	 * @param idx the idx
	 *
	 * @throws ContradictionException the contradiction exception
	 *
	 * @see choco.integer.constraints.AbstractIntConstraint#awakeOnSup(int)
	 */
	@Override
	public void awakeOnSup(final int idx) throws ContradictionException {
		this.constAwake(false);
	}

}





