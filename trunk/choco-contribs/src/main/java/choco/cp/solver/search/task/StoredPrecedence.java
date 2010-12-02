/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.cp.solver.search.task;

import choco.IPretty;
import choco.kernel.model.constraints.ITemporalRelation;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;



/**
 * @author Arnaud Malapert</br> 
 * @since 18 juin 2009 version 2.1.0</br>
 * @version 2.1.0</br>
 */
public final class StoredPrecedence implements IPretty, ITemporalRelation<TaskVar<?>, IntDomainVar> {

	private final ITask t1;

	private final ITask t2;

	public final IntDomainVar direction;

	public StoredPrecedence(ITask t1, ITask t2, IntDomainVar direction) {
		super();
		this.t1 = t1;
		this.t2 = t2;
		this.direction = direction;
	}

	public final TaskVar getOrigin() {
		return (TaskVar) t1;
	}

	public final TaskVar getDestination() {
		return (TaskVar) t2;
	}

	public final IntDomainVar getDirectionVar() {
		return direction;
	}
	
	@Override
	public String toString() {
		return "("+t1.getName()+","+t2.getName()+")";
	}

	@Override
	public String pretty() {
		return toString();
	}


	@Override
	public boolean canBeBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean IsFixed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IntDomainVar getDirection() {
		return direction;
	}

	@Override
	public int backwardSetup() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int forwardSetup() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	
	
}