package choco.cp.model.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.AbstractResourceSConstraint;
import choco.cp.solver.constraints.global.scheduling.AbstractUseResourcesSConstraint;
import choco.cp.solver.constraints.global.scheduling.TempTaskConstraintWrapper;
import choco.cp.solver.constraints.global.scheduling.UseResourcesEq;
import choco.cp.solver.constraints.global.scheduling.UseResourcesGeq;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;


public class UseResourcesManager extends ConstraintManager<Variable> {


	@Override
	public SConstraint makeConstraint(Solver solver,
			Variable[] variables, Object parameters, Set<String> options) {
		if (solver instanceof CPSolver) {
			if(variables.length == 1 && variables[0] instanceof TaskVariable) {
				//check task
				final TaskVar task = solver.getVar(variables[0]);
				if (parameters instanceof Object[]) {
					final Object[] params = (Object[]) parameters;
					if(params.length > 2) {
						if (params[0] instanceof Constraint[] &&
								params[1] instanceof Integer &&
								params[2] instanceof Boolean ) {
							//check parameters
							final Constraint[] resources = (Constraint[]) params[0];
							//check number of resources
							final List<IRTask> rtaskL = new ArrayList<IRTask>();
							int k = (Integer) params[1];
							final boolean equal = (Boolean) params[2];
							for (int i = 0; i < resources.length; i++) {
								final SConstraint c = solver.getCstr(resources[i]);
								if ( c != null && c instanceof AbstractResourceSConstraint) {
									final AbstractResourceSConstraint rsc = (AbstractResourceSConstraint) c;
									final int idx = rsc.indexOf(task);
									if( idx >= 0) {
										final IRTask rt = rsc.getRTask(idx);
										if(rt.isRegular()) k--;
										else if(rt.isOptional()){
											rtaskL.add( rt);
										}
									}
								}
							}
							if(k <= 0) return CPSolver.TRUE;
							else if (rtaskL.size() < k) return CPSolver.FALSE;
							else {
								final IntDomainVar[] uvars = new IntDomainVar[rtaskL.size()];
								final IRTask[] rtasks = new IRTask[rtaskL.size()];
								final ListIterator<IRTask> iter = rtaskL.listIterator();
								while(iter.hasNext()) {
									final int idx = iter.nextIndex();
									final IRTask rt = iter.next();
									rtasks[idx] = rt;
									uvars[idx] = rt.getUsage();
								}
								AbstractUseResourcesSConstraint cstr = equal ? 
										new UseResourcesEq(solver.getEnvironment(), task, k, uvars, rtasks) :
											new UseResourcesGeq(solver.getEnvironment(), task, k, uvars, rtasks);
								solver.post( new TempTaskConstraintWrapper(task,cstr));	
								return cstr;
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public int[] getFavoriteDomains(Set<String> options) {
		return getBCFavoriteIntDomains();
	}

	@Override
	public SConstraint[] makeConstraintAndOpposite(Solver solver,
			Variable[] variables, Object parameters, Set<String> options) {
		//TODO should simply return the opposite bool sum
		return null;
	}

	@Override
	public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
		return null;
	}





}
