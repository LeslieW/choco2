package choco.cp.solver.constraints.global.softscheduling;

/**
 * Created by IntelliJ IDEA.
 * User: thierry
 * Date: 5 nov. 2009
 * Time: 14:48:09
 *
 * SoftCumulative with task interval based lower bound of the sum of costVars
 * ref: TR 09-06-Info, Mines Nantes
 *
 */


import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.memory.trailing.StoredInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Collections;


public class SoftCumulativeSum extends SoftCumulative {

	protected StoredInt profileMinSum;  // must be maintained for interval-based LB

	public SoftCumulativeSum(IntDomainVar[] starts,
                                  IntDomainVar[] ends,
                                  IntDomainVar[] durations,
                                  IntDomainVar[] heights,
                                  IntDomainVar[] costVars,
                                  IntDomainVar obj,
                                  int wishCapa,
                                  int capa) {
		super(starts,ends,durations,heights,costVars,wishCapa,capa);
		initialize(costVars, obj);
	}

	protected void initialize(IntDomainVar[] costVars, IntDomainVar obj) {
		recomputeVars(obj);
		Solver pb = obj.getSolver();
		pb.post(pb.eq(pb.sum(costVars), obj)); //  to be improved
		EnvironmentTrailing env = (EnvironmentTrailing) pb.getEnvironment();
		profileMinSum = new StoredInt(env,0);
	}

	protected void recomputeVars(IntDomainVar obj) {
		IntDomainVar[] res = new IntDomainVar[vars.length+1];
		for(int i=0; i<vars.length; i++) {
			res[i] = vars[i];
		}
		res[vars.length]=obj;
		vars = res;
		cIndices = new int[vars.length];
		if(debug) {
			for(int i=0; i<vars.length;i++)
			System.out.println(vars[i].pretty());
		}
	}

	public SoftCumulativeSum(     IntDomainVar[] starts,
            			  		  int[] durations,
            			  		  int[] heights,
            			  		  IntDomainVar[] costVars,
            			  		  IntDomainVar obj,
            			  		  int wishCapa) {
		super(starts,durations,heights,costVars,wishCapa);
		initialize(costVars, obj);
	}

	// --------------
	// Obj var getter
	// --------------

	// nbTask*4+costVarsLength == vars.length-1 only for SoftCumulativeMinimize
    protected IntDomainVar getObj() {
    	return vars[nbTask*4+costVarsLength];
    }

    // --------------------------------------------
    // update costs while maintaining profileMinSum
    // --------------------------------------------

    protected void updateCost(int low, int up) throws ContradictionException { // consider [low, up]
        for(int i=low; i<=up; i++) {
     	   if(i<costVarsLength) {
     	       if(getCostVar(i).getSup()<sum_height-wishCapa) {
     		      this.fail();
     	       }
     	       if(sum_height>wishCapa) {
     	    	  int prev = getCostVar(i).getInf();
     	    	  if(prev<sum_height-wishCapa) {
     		         fixPoint |= getCostVar(i).updateInf(sum_height-wishCapa,cIndices[nbTask*4+i]);
     		         profileMinSum.set(profileMinSum.get()+getCostVar(i).getInf()-prev);
     	          }
     	       }
           }
       }
    }

    // -------------------------------------
    // Task interval with global lower bound
	// -------------------------------------

    public int computeIncreasing(int energy, int left, int right) {
    	int diff = wishCapa * (right-left); // if all costs are = 0;
    	for(int i=left; i<right; i++){
 		   if(i<costVarsLength) { // should never occur
 		      diff += getCostVar(i).getInf();
 		   }
    	}
    	if(diff < energy) {
    		return energy-diff;
    	}
    	return 0;
    }

    public void taskIntervals() throws ContradictionException {
        Collections.sort(Xtasks, stComp);
        Collections.sort(Ytasks, endComp);
        int maxInc = 0;
        for (int i = 0; i < nbTask; i++) {
            int D = getEnd(Ytasks.get(i)).getSup();
            int energy = 0; // int to use updateInf
            for (int j = nbTask - 1; j >= 0; j--) {
                int t = Xtasks.get(j);
                int h = getHeight(t).getInf();
                int minDur = getDuration(t).getInf();
                int e = minDur * h; // int to use updateInf
                if (getLE(t) > D) e = Math.min(e, (D - getLS(t)) * h);
                if (e > 0) {
                    energy += e;
                    //System.out.println(energy);
                    long capaMaxDiff = capaMaxDiff(getES(t),D);
                    if (capaMaxDiff < energy) {
                    	this.fail();
                    } else {
                    	int inc = computeIncreasing(energy,getES(t),D);
                    	if(inc>maxInc) {
                    		maxInc = inc;
                    	}
                    }
                }
            }
        }
        IntDomainVar obj = getObj();
        if(debug) {
            System.out.println(maxInc + ", obj = [" + obj.getInf() + ", " + obj.getSup() + "]");
        }
        if(maxInc>0) {
        	if(debug) {
                System.out.println(profileMinSum.get() + "-" + "obj inf = " + obj.getInf() + "- increasing = "+maxInc);
            }
        	if(profileMinSum.get()+maxInc>obj.getSup()) {
        		this.fail();
            } else {
                obj.updateInf(profileMinSum.get()+maxInc,cIndices[nbTask*4+costVarsLength]);
            }
        }
    }
}
