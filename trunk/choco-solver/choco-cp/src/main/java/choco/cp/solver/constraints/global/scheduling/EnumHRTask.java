package choco.cp.solver.constraints.global.scheduling;

import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;

public final class EnumHRTask extends BoundHRTask {


	public EnumHRTask(IEnvironment env,
			AbstractResourceSConstraint constraint, int taskIdx) {
		super(env, constraint, taskIdx);
	}

	@Override
	protected boolean setHEST(int val) throws ContradictionException {			
		if( val > htask.getEST() ) {
			if(taskvar.start().canBeInstantiatedTo(val)){
				estH.set(val);
				checkHConsistency();
				fireHypotheticalDomain();
			}else{
				//get value, if any, after this one, and available in the domain
				if(val < taskvar.start().getSup()){
					final int newEST = taskvar.start().getNextDomainValue(val);
					assert newEST > val;
					estH.set(newEST);
					checkHConsistency();
					fireHypotheticalDomain();
				}
				else{
					remove();
					fireRemoval();
				}
			}
			return true;
		} else return false;
	}


	@Override
	protected boolean setHDuration(int duration) throws ContradictionException {
		if(taskvar.duration().canBeInstantiatedTo(duration)) {
			return super.setHDuration(duration);
		} else {
			remove();
			fireRemoval();
			return true;
		}
	}


	@Override
	protected boolean setHECT(int val) throws ContradictionException {
		if( val > htask.getECT() ) {
			if(taskvar.end().canBeInstantiatedTo(val)){
				estH.set(val - htask.getMinDuration());
				checkHConsistency();
				fireHypotheticalDomain();
			}else{
				//update value not existing in main domain
				if(val < taskvar.end().getSup()){
					final int newECT = taskvar.end().getNextDomainValue(val);
					assert newECT > val;
					estH.set(newECT - htask.getMinDuration());
					checkHConsistency();
					fireHypotheticalDomain();
				}
				else{
					remove();
					fireRemoval();
				}
			}
			return true;
		} else return false;
	}




	@Override
	protected boolean setHLCT(int val) throws ContradictionException {
		if( val < htask.getLCT() ) {
			if(taskvar.end().canBeInstantiatedTo(val)){
				lctH.set(val);
				checkHConsistency();
				fireHypotheticalDomain();
			}else{
				if(val > taskvar.end().getInf()){
					final int newLCT = taskvar.end().getPrevDomainValue(val);
					assert newLCT < val;
					lctH.set(newLCT);
					checkHConsistency();
					fireHypotheticalDomain();
				}
				else{
					remove();
					fireRemoval();
				}
			}
			return true;
		} else return false;
	}


	@Override
	protected boolean setHLST(int val) throws ContradictionException {
		if( val < htask.getLST() ) {
			if(taskvar.start().canBeInstantiatedTo(val)){
				lctH.set(val + htask.getMaxDuration());
				checkHConsistency();
				fireHypotheticalDomain();
			}else{
				if(val > taskvar.start().getInf()){
					final int newLST = taskvar.start().getPrevDomainValue(val);
					assert newLST < val;
					lctH.set(newLST + htask.getMaxDuration());
					checkHConsistency();
					fireHypotheticalDomain();
				}else{
					remove();
					fireRemoval();
				}
			}
			return true;
		} else return false;
	}


}

