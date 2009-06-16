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
package choco.cp.solver.constraints.global;


import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.constraints.global.geost.Setup;
import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.cp.solver.constraints.global.geost.layers.ExternalLayer;
import choco.cp.solver.constraints.global.geost.layers.GeometricKernel;
import choco.cp.solver.constraints.global.geost.layers.IntermediateLayer;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.IntIterator;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Vector;

public class Geost_Constraint extends AbstractLargeIntSConstraint {



	int[] oIDs;
	Constants cst;
	Setup stp;
	ExternalLayer externalLayer;
	GeometricKernel geometricKernel;
	IntermediateLayer intermediateLayer;
    protected Solver s;
    private int greedyMode = 0;
    Vector<int[]> ctrlVs ;

	/**
	 * Creates a geost constraint with the given parameters.
	 * @param vars Array of Variables for choco
	 * @param k Dimension of the problem we are working with
	 * @param objects A vector containing the objects (obj)
	 * @param shiftedBoxes A vector containing the shifted boxes
	 * @param ectr A vector containing the External Constraints in our problem
	 * @param ctrlVs A list of controlling vectors used in the greedy mode
	 */


	public Geost_Constraint(IntDomainVar[] vars, int k, Vector<Obj> objects, Vector<ShiftedBox> shiftedBoxes, Vector<ExternalConstraint> ectr, Vector<int[]> ctrlVs)
	{
		super(vars);

		cst = new Constants();
		stp = new Setup(cst);
		intermediateLayer = new IntermediateLayer();
		externalLayer = new ExternalLayer(cst, stp);
		geometricKernel = new GeometricKernel(cst, stp, externalLayer, intermediateLayer);

		cst.setDIM(k);
        this.ctrlVs = ctrlVs;

		stp.SetupTheProblem(objects, shiftedBoxes, ectr);

		//this should be changed and be provided globally to the system
		oIDs = new int[stp.getNbOfObjects()];
		for(int i = 0; i< stp.getNbOfObjects(); i++)
			oIDs[i] = objects.elementAt(i).getObjectId();


        this.s = vars[0].getSolver();
        this.greedyMode = 1;
	}


	/**
	 * Creates a geost constraint with the given parameters.
	 * @param vars Array of Variables for choco
	 * @param k Dimension of the problem we are working with
	 * @param objects A vector containing the objects (obj)
	 * @param shiftedBoxes A vector containing the shifted boxes
	 * @param ectr A vector containing the External Constraints in our problem
	 */


	public Geost_Constraint(IntDomainVar[] vars, int k, Vector<Obj> objects, Vector<ShiftedBox> shiftedBoxes, Vector<ExternalConstraint> ectr)
	{
		super(vars);

		cst = new Constants();
		stp = new Setup(cst);
		intermediateLayer = new IntermediateLayer();
		externalLayer = new ExternalLayer(cst, stp);
		geometricKernel = new GeometricKernel(cst, stp, externalLayer, intermediateLayer);

		cst.setDIM(k);

		stp.SetupTheProblem(objects, shiftedBoxes, ectr);

		//this should be changed and be provided globally to the system
		oIDs = new int[stp.getNbOfObjects()];
		for(int i = 0; i< stp.getNbOfObjects(); i++)
			oIDs[i] = objects.elementAt(i).getObjectId();


        this.s = vars[0].getSolver();
	}


    @Override
    public int getFilteredEventMask(int idx) {
        if(vars[idx].hasEnumeratedDomain()){
            return IntVarEvent.REMVALbitvector;
        }else{
            return IntVarEvent.INSTINTbitvector+IntVarEvent.BOUNDSbitvector;
        }
    }

    public void filter() throws ContradictionException{
	  if(this.greedyMode == 0)
		  filterWithoutGreedyMode();
	  else
		  filterWithGreedyMode();
	}

	private void filterWithGreedyMode() throws ContradictionException{
		s.worldPush();
		if (!geometricKernel.FixAllObjs(cst.getDIM(), oIDs, stp.getConstraints(), this.ctrlVs)){
			s.worldPop();
			filterWithoutGreedyMode();
		}
		else{
			Solution sol = new Solution(s);
			for (IntDomainVar var : vars) {
				int idx = s.getIntVarIndex(var);
                // idx = -1 means that it is not a variable but a constant
                // and we do not need to record it
                if(idx != -1){
                    sol.recordIntValue(idx, var.getVal());
                }
            }
			s.worldPop();
			s.restoreSolution(sol);
		}
	}


	private void filterWithoutGreedyMode() throws ContradictionException{
		if(!geometricKernel.FilterCtrs(cst.getDIM(), oIDs, stp.getConstraints()))
			this.fail();
	}

	public boolean isSatisfied() {
        boolean b;
        s.worldPushDuringPropagation();
        try {
            b = geometricKernel.FilterCtrs(cst.getDIM(), oIDs,
                                    stp.getConstraints());
        } catch (ContradictionException e) {
            b = false;
        }
        s.worldPopDuringPropagation();
        return b;
}


	public void propagate() throws ContradictionException {
		filter();
	}

	public void awake() throws ContradictionException {
		//this.constAwake(false);
        // the initial propagation should be done
        filter();
	}


	public void awakeOnInst(int idx) throws ContradictionException {
		this.constAwake(false);
	}



	public void awakeOnInf(int idx) throws ContradictionException {
		this.constAwake(false);
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		this.constAwake(false);
	}

    public void awakeOnBounds(int varIndex) throws ContradictionException {
    	this.constAwake(false);
		//filter();
	 }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
		this.constAwake(false);
	 }

	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		this.constAwake(false);
	}










	public Constants getCst() {
		return cst;
	}

	public Setup getStp() {
		return stp;
	}

	public void setCst(Constants cst) {
		this.cst = cst;
	}

	public void setStp(Setup stp) {
		this.stp = stp;
	}


}
