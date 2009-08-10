package choco.cp.solver.constraints.global;


import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.constraints.global.geost.Setup;
import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.cp.solver.constraints.global.geost.internalConstraints.InternalConstraint;
import choco.cp.solver.constraints.global.geost.layers.ExternalLayer;
import choco.cp.solver.constraints.global.geost.layers.GeometricKernel;
import choco.cp.solver.constraints.global.geost.layers.IntermediateLayer;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.objects.Pair;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashMap;
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
    boolean increment = false;
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

	public Geost_Constraint(IntDomainVar[] vars, int k, Vector<Obj> objects, Vector<ShiftedBox> shiftedBoxes, Vector<ExternalConstraint> ectr, Vector<int[]> ctrlVs, boolean memo_active, HashMap<Pair<Integer,Integer>,Boolean> included, Long a, Long b,
                            boolean increment_)
	{

        super(vars);

        cst = new Constants();
		stp = new Setup(cst);
		intermediateLayer = new IntermediateLayer();
		externalLayer = new ExternalLayer(cst, stp);
		geometricKernel = new GeometricKernel(cst, stp, externalLayer, intermediateLayer,memo_active,included,a,b);

		cst.setDIM(k);
        this.ctrlVs = ctrlVs;

		stp.SetupTheProblem(objects, shiftedBoxes, ectr);

		//this should be changed and be provided globally to the system
		oIDs = new int[stp.getNbOfObjects()];
		for(int i = 0; i< stp.getNbOfObjects(); i++)
			oIDs[i] = objects.elementAt(i).getObjectId();


        this.s = vars[0].getSolver();
        this.greedyMode = 1;
        this.increment=increment_;


        IntDomainVarImpl D = new IntDomainVarImpl(s,"D",IntDomainVar.BOUNDS,0,100);


	}


	/**
	 * Creates a geost constraint with the given parameters.
	 * @param vars Array of Variables for choco
	 * @param k Dimension of the problem we are working with
	 * @param objects A vector containing the objects (obj)
	 * @param shiftedBoxes A vector containing the shifted boxes
	 * @param ectr A vector containing the External Constraints in our problem
	 */

	public Geost_Constraint(IntDomainVar[] vars, int k, Vector<Obj> objects, Vector<ShiftedBox> shiftedBoxes, Vector<ExternalConstraint> ectr, boolean memo, HashMap<Pair<Integer,Integer>, Boolean> included, Long a, Long b)
	{
        super(vars);

        cst = new Constants();
		stp = new Setup(cst);
		intermediateLayer = new IntermediateLayer();
		externalLayer = new ExternalLayer(cst, stp);
		geometricKernel = new GeometricKernel(cst, stp, externalLayer, intermediateLayer, memo, included, a, b);

		cst.setDIM(k);

		stp.SetupTheProblem(objects, shiftedBoxes, ectr);

		//this should be changed and be provided globally to the system
		oIDs = new int[stp.getNbOfObjects()];
		for(int i = 0; i< stp.getNbOfObjects(); i++)
			oIDs[i] = objects.elementAt(i).getObjectId();


        this.s = vars[0].getSolver();

	}
    
    public void filter() throws ContradictionException{
      if(this.greedyMode == 0)  {
		  filterWithoutGreedyMode();

      }
	  else {          
        long tmpTime = (System.nanoTime() / 1000000);
        filterWithGreedyMode();
        stp.timefilterWithGreedyMode += ((System.nanoTime()/1000000) - tmpTime);
      }

	}

	private void filterWithGreedyMode() throws ContradictionException{
        if (stp.debug) System.out.println("Geost_Constraint:filterWithGreedyMode()");
        s.worldPush();    //Starts a new branch in the search tree
        boolean result = false;

        if (!increment) {
            long tmpTimeFixAllObj = System.nanoTime() / 1000000;
            result=geometricKernel.FixAllObjs(cst.getDIM(), oIDs, stp.getConstraints(), this.ctrlVs);            
            stp.timeFixAllObj += ((System.nanoTime() / 1000000) - tmpTimeFixAllObj);
        }
        else {
            long tmpTimeFixAllObj = System.nanoTime() / 1000000;
            result=geometricKernel.FixAllObjs_incr(cst.getDIM(), oIDs, stp.getConstraints(), this.ctrlVs);
            stp.timeFixAllObj += ((System.nanoTime() / 1000000) - tmpTimeFixAllObj);
        }
        if (!result){
			s.worldPop();
            long tmpTime = (System.nanoTime() / 1000000);
			filterWithoutGreedyMode();
            stp.timefilterWithoutGreedyMode += ((System.nanoTime()/1000000) - tmpTime);

		}
		else{

           long tmpTime = (System.nanoTime() / 1000000);
            //s.getSearchStrategy().recordSolution();
			Solution sol = new Solution(s);

            for (int i=0; i<s.getNbIntVars(); i++) {
				//int idx = s.getIntVarIndex(var);
                // idx = -1 means/**/ that it is not a variable but a constant
                // and we do not need to record it
                //if(idx != -1){
                    sol.recordIntValue(i, ((IntDomainVar) s.getIntVar(i)).getVal());

                //}
            }
            stp.handleSolution1 += ((System.nanoTime()/1000000) - tmpTime);
            tmpTime = (System.nanoTime() / 1000000);
			s.worldPop();  //Come back to the state before propagation
            stp.handleSolution2 += ((System.nanoTime()/1000000) - tmpTime);
            tmpTime = (System.nanoTime() / 1000000);
            //s.getSearchStrategy().restoreBestSolution();

            s.restoreSolution(sol);//Restore the solution
            stp.handleSolution3 += ((System.nanoTime()/1000000) - tmpTime);
		}
	}


	private void filterWithoutGreedyMode() throws ContradictionException{
        if (stp.debug) System.out.println("Geost_Constraint:filterWithoutGreedyMode()");        
        if(!geometricKernel.FilterCtrs(cst.getDIM(), oIDs, stp.getConstraints()))
			this.fail();
	}

	public boolean isSatisfied() {
        boolean b = false;
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
//        int l=vars.length;
//        for (int i=0; i<l; i++)
//            System.out.println("Geost_Constraint:propagate():vars["+i+"]:"+vars[i]+","+vars[i].getInf()+","+vars[i].getSup());
//        System.out.println("----propagate");          ^
        if (stp.debug) System.out.println("GeostConstraint:propagate()");
		filter();
	}

	public void awake() throws ContradictionException {
		this.constAwake(false);
		//filter();
	}


	public void awakeOnInst(int idx) throws ContradictionException {
		this.constAwake(false);
		//filter();
	}



	public void awakeOnInf(int idx) throws ContradictionException {
		this.constAwake(false);
		//filter();
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		this.constAwake(false);
		//filter();
	}

    public void awakeOnBounds(int varIndex) throws ContradictionException {
    	this.constAwake(false);
		//filter();
	 }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
		this.constAwake(false);
		//filter();
	 }

	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
		this.constAwake(false);
		//filter();
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

    public ExternalLayer getExternalLayer() {
        return externalLayer;
    }

    public Vector<InternalConstraint> getForbiddenRegions(Obj o) {

        //Should be set up only once during a single fixpoint
        Vector<ExternalConstraint> ectrs  = stp.getConstraints();
        for (int i = 0; i < ectrs.size(); i++)
		{
			ectrs.elementAt(i).setFrame(externalLayer.InitFrameExternalConstraint(ectrs.elementAt(i), oIDs));
		}
        
        //TODO: Holes should be generated here

        for (int i = 0; i < o.getRelatedExternalConstraints().size(); i++)
        {
            Vector<InternalConstraint> v = externalLayer.GenInternalCtrs(o.getRelatedExternalConstraints().elementAt(i), o);
            for (int j = 0; j < v.size(); j++)
            {
                o.addRelatedInternalConstraint(v.elementAt(j));
            }
        }

        return o.getRelatedInternalConstraints();
    }


}
