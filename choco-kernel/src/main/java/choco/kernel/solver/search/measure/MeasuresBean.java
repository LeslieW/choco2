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

package choco.kernel.solver.search.measure;


public class MeasuresBean implements IMeasures {

	public int objectiveIntValue = Integer.MAX_VALUE;

	public double objectiveRealValue = Double.POSITIVE_INFINITY;
	
	public boolean objectiveOptimal; 

	public long timeCount;
	
	public long nodeCount ;
	
	public long backtrackCount;
	
	public long restartCount;
	
	public long failCount;
	
	public long solutionCount;
	
	public MeasuresBean() {
		super();
	}
	
	public final void reset() {
		objectiveIntValue = Integer.MAX_VALUE;
		objectiveRealValue = Double.POSITIVE_INFINITY;
		timeCount = 0;
		nodeCount = 0;
		backtrackCount = 0; 
		restartCount = 0;
		failCount = 0;
		solutionCount=0;
	}
		
	
	public final void setSearchMeasures(ISearchMeasures toCopy) {
		timeCount = toCopy.getTimeCount();
		nodeCount = toCopy.getNodeCount();
		backtrackCount = toCopy.getBackTrackCount();
		restartCount = toCopy.getRestartCount();
		failCount = toCopy.getFailCount();
		solutionCount = toCopy.getSolutionCount();
	}
	
	public final void addSearchMeasures(ISearchMeasures toAdd) {
		timeCount += toAdd.getTimeCount();
		nodeCount += toAdd.getNodeCount();
		backtrackCount += toAdd.getBackTrackCount();
		restartCount += toAdd.getRestartCount();
		failCount += toAdd.getFailCount();
		solutionCount += toAdd.getSolutionCount();
	}
	
	@Override
	public boolean existsSolution() {
		return solutionCount > 0;
	}

	@Override
	public long getSolutionCount() {
		return solutionCount;
	}

	@Override
	public Number getObjectiveValue() {
		return ( 
				objectiveIntValue == Integer.MAX_VALUE ? 
						( objectiveRealValue == Double.POSITIVE_INFINITY? (Number) null: Double.valueOf(objectiveRealValue) ) :
							Integer.valueOf(objectiveIntValue) 
		);
	}


	@Override
	public boolean isObjectiveOptimal() {
		return objectiveOptimal;
	}


	@Override
	public final long getBackTrackCount() {
		return backtrackCount;
	}

	@Override
	public final long getFailCount() {
		return failCount;
	}

	@Override
	public final long getNodeCount() {
		return nodeCount;
	}

    @Override
	public final long getTimeCount() {
		return timeCount;
	}

	@Override
	public final long getRestartCount() {
		return restartCount;
	}

	public final void setSolutionCount(long solutionCount) {
		this.solutionCount = solutionCount;
	}

	public final void setObjectiveIntValue(int objectiveIntValue) {
		this.objectiveIntValue = objectiveIntValue;
	}

	public final void setObjectiveRealValue(double objectiveRealValue) {
		this.objectiveRealValue = objectiveRealValue;
	}

	public final void setObjectiveOptimal(boolean objectiveOptimal) {
		this.objectiveOptimal = objectiveOptimal;
	}

	public final void setRestartCount(long restartCount) {
		this.restartCount = restartCount;
	}

    public final void setTimeCount(long timeCount) {
		this.timeCount = timeCount;
	}

	public final void setNodeCount(long nodeCount) {
		this.nodeCount = nodeCount;
	}

	public final void setBacktrackCount(long backtrackCount) {
		this.backtrackCount = backtrackCount;
	}

	public final void setFailCount(long failCount) {
		this.failCount = failCount;
	}

}
