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

package choco.kernel.common.opres.pack;

import java.util.Arrays;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntProcedure;

abstract class AbstractProcedureDDFF implements TIntProcedure {

	public FunctionDDFF ddff;

	public abstract void reset();

	public final FunctionDDFF getDDFF() {
		return ddff;
	}

	public final void setDDFF(FunctionDDFF ddff) {
		this.ddff = ddff;
	}

}

final class ComputeL0 extends AbstractProcedureDDFF {

	private int l0;

	private int size;

	@Override
	public void reset() {
		l0 = 0;
		size = 0;
	}

	public int getL0() {
		return size > 0 ? l0 + 1 : l0;
	}

	@Override
	public boolean execute(int arg0) {
		size += ddff.apply(arg0);
		if( size > ddff.getCurrentCapacity()) {
			size -= ddff.getCurrentCapacity();
			l0++;
		}
		return true;
	}
}


final class FindParameters extends AbstractProcedureDDFF {

	public final TIntHashSet parameters = new TIntHashSet();

	public final TIntHashSet getParameters() {
		return parameters;
	}

	@Override
	public void reset() {
		parameters.clear();
	}

	@Override
	public boolean execute(int arg0) {
		final int param = ddff.findParameter(arg0);
		if(param > 0) parameters.add(param);
		return true;
	}
}




public class PackDDFF {

	private TIntArrayList items;

	public final FunctionF0 f0;

	public final FunctionF1 f1;

	public final FunctionF2 f2;

	private int lb = 0;

	private int ub = Integer.MAX_VALUE;

	private ComputeL0 computeL0 = new ComputeL0();

	private FindParameters findParameters = new FindParameters();

	private ApplyDDFF applyDDFF = new ApplyDDFF();

	public PackDDFF(int capacity) {
		super();
		f0 = new FunctionF0(capacity);
		f1 = new FunctionF1(capacity);
		f2 = new FunctionF2(capacity);
	}

	public final TIntArrayList getItems() {
		return items;
	}

	public final void setItems(TIntArrayList items) {
		this.items = items;
		//TODO remove nil items  ?
		f1.setItems(items);
	}


	public final int getUB() {
		return ub;
	}

	public final void setUB(int ub) {
		this.ub = ub;
	}

	protected boolean computeDDFF(final FunctionDDFF ddff) {
		findParameters.setDDFF(ddff);
		findParameters.reset();
		items.forEach(findParameters);
		//System.out.println("params: "+Arrays.toString(findParameters.parameters.toArray()));
		computeL0.setDDFF(ddff);
		applyDDFF.setDDFF(ddff);
		return findParameters.parameters.forEach(applyDDFF);
	}

	public int computeDDFF() {
		lb = 0;
		if( ! items.isEmpty() 
				&& computeDDFF(f0) 
				&& computeDDFF(f1) 
		) computeDDFF(f2);
		return lb;
	}

	final class ApplyDDFF extends AbstractProcedureDDFF {

		@Override
		public void reset() {}

		@Override
		public boolean execute(int arg0) {
			ddff.setParameter(arg0);
			computeL0.reset();
			items.forEach(computeL0);
			if( computeL0.getL0() > lb) {
				lb = computeL0.getL0();
				if( lb == ub) return false;
				assert(lb < ub);
			}
			return true;
		}
	}

}


