package choco.cp.solver.constraints.global.softscheduling;

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: thierry
 * Date: 9 nov. 2009
 * Time: 17:54:23
 * To change this template use File | Settings | File Templates.
 */
public class SoftCumulativeSumManager extends IntConstraintManager {

public SConstraint makeConstraint(Solver solver,
			                            IntegerVariable[] variables,
			                            Object parameters,
			                            Set<String> options){
          Object[] par = (Object[]) parameters;
          int[] durations = (int[]) par[0];
          int[] heights = (int[]) par[1];
          Integer wishCapa = (Integer) par[2];
          int nbTasks = durations.length;
          int nbCosts = variables.length-nbTasks-1;
          IntDomainVar[] starts = new IntDomainVar[nbTasks];
          for(int i=0; i<starts.length; i++) {
              starts[i] =  solver.getVar(variables[i]);
          }
          IntDomainVar[] costVars = new IntDomainVar[nbCosts];
          for(int i=0; i<costVars.length; i++) {
              costVars[i] = solver.getVar(variables[i+nbTasks]);
          }
          IntDomainVar obj = solver.getVar(variables[variables.length-1]);
          return new SoftCumulativeSum(starts,durations,heights,costVars,obj,wishCapa, solver);

      }
}


