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

package samples.tutorials.trunk;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.tutorials.PatternExample;

import java.util.Arrays;

import static choco.Choco.*;

/**
 * @author Arnaud Malapert
 *
 */
public class GolombRuler extends PatternExample {

	public final static int[][] OPTIMAL_RULER =	{ 
		{5, 11}, {6, 17}, {7, 25}, {8, 34}, {9, 44}, {10, 55}, {11, 72}
	};
	
    IntegerVariable[] ticks, diff;
	public int m;
	public int length;
    private boolean useAllDiff = false;
    
    @Override
    public void setUp(Object paramaters) {

        if(paramaters instanceof Object[]){
            Object[] params = (Object[])paramaters;
            m = (Integer)params[0];
            length = (Integer)params[1];
            useAllDiff = (Boolean)params[2];
        }
    }

    @Override
    public void buildModel() {
        model = new CPModel();
		ticks = makeIntVarArray("a", m, 0,length);
		diff = makeIntVarArray("d", ((m)*(m-1))/2, 0,length);
		breakSymmetries();
		setAuxVarConstraints();
		if(useAllDiff) {
			model.addConstraint(allDifferent(diff));
		}else {
			// d_ij != d_kl
			for (int i = 0; i < diff.length; i++) {
				for (int j = i+1; j < diff.length; j++) {
					model.addConstraint(neq(diff[i],diff[j]));
				}
			}
		}
    }

    @Override
    public void buildSolver() {
       solver = new CPSolver();
       solver.read(model);
    }

    @Override
    public void solve() {
    	solver.solveAll();
    }

    @Override
    public void prettyOut() {
        LOGGER.info(Arrays.toString(solver.getVar(ticks)));
    }

    private void breakSymmetries() {
		//monotony
		for (int i = 2; i < m; i++) {
			model.addConstraint(geq(ticks[i],plus(ticks[i-1],1)));
		}
		//translation
		model.addConstraint(eq(ticks[0], 0));
		//reflexion
		model.addConstraint(leq(plus(minus(ticks[1],ticks[0]),1),minus(ticks[m-1],ticks[m-2])));
	}

	private void setAuxVarConstraints() {
		//d_ij = x_j - x_i et d_ij< dik
		int cpt =0;
		for (int i = 0; i < m; i++) {
			for (int j = i+1; j < m; j++) {
				model.addConstraint(eq(diff[cpt++],minus(ticks[j],ticks[i])));
				if(j>i+1) {
                    model.addConstraint(geq(diff[cpt-1],plus(diff[cpt-2],1)));}
			}
		}
	}

	

	@Override
	public void execute() {
		execute(new Object[]{OPTIMAL_RULER[4][0], OPTIMAL_RULER[4][1], true});
	}

	public static void main(String[] args) {
		new GolombRuler().execute();
	}

}
