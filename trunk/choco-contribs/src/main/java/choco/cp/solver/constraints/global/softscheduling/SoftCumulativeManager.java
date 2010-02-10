package choco.cp.solver.constraints.global.softscheduling;

/**
 * Created by IntelliJ IDEA.
 * User: thierry
 * Date: 9 nov. 2009
 * Time: 15:37:30
 * Var-based manager. Todo : change with taskVars
 */

import java.util.Set;

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class SoftCumulativeManager extends IntConstraintManager {

    // Todo : update with API
	  public SConstraint makeConstraint(Solver solver,
			                            IntegerVariable[] variables,
			                            Object parameters,
			                            Set<String> options){
          Object[] par = (Object[]) parameters;
          int[] durations = (int[]) par[0];
          int[] heights = (int[]) par[1];
          Integer wishCapa = (Integer) par[2];
          int nbTasks = durations.length;
          int nbCosts = variables.length-nbTasks;
          IntDomainVar[] starts = new IntDomainVar[nbTasks];
          for(int i=0; i<starts.length; i++) {
              starts[i] =  solver.getVar(variables[i]);
          }
          IntDomainVar[] costVars = new IntDomainVar[nbCosts];
          for(int i=0; i<costVars.length; i++) {
              costVars[i] = solver.getVar(variables[i+nbTasks]);
          }
          return new SoftCumulative(starts,durations,heights,costVars,wishCapa);
      }
}
