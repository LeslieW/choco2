/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package samples.tutorials.scheduling;

import choco.Choco;
import static choco.Choco.neq;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.scheduling.TaskVariable;

import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Nov 28, 2008
 * Time: 2:23:37 PM
 * A simple example 
 */
public class MeetingScheduling {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

    protected int nbP;          // number of proposals
    protected int nbE;          // number of evaluators

    protected int[][] reva;     // reva[i][j] = 1 if reviewer i is assigned to meeting j
    protected int[] numberOfEv; //the number of evaluators for each proposal

    protected int horizon;
    
    protected CPModel m;
    protected TaskVariable[] proposals;

    public MeetingScheduling(int nbP, int nbE, int[][] revAssignment) {
        this.nbP = nbP;
        this.nbE = nbE;
        this.reva = revAssignment;
        numberOfEv = new int[nbE];
        for (int i = 0; i < nbE; i++) {
            for (int j = 0; j < nbP; j++) {
                if (reva[i][j] == 1) numberOfEv[j]++;
            }
        }
    }

    public void buildModel() {
        horizon = 2*nbP;
        m = new CPModel();

        proposals = new TaskVariable[nbP];
        for (int i = 0; i < nbP; i++) {
            proposals[i] = Choco.makeTaskVar("p_" + i, horizon, 1);
        }

        //each reviewer is a unary resource
        Constraint[] rsc = new Constraint[nbE];
//        for (int i = 0; i < nbE; i++) {
//            rsc[i] = Scheduling.makeUnaryResource();           
//        }
//        for (int i = 0; i < nbE; i++) {
//            for (int j = 0; j < nbP; j++) {
//                if (reva[i][j] == 1) rsc[i].addTask(proposals[j]);                
//            }
//        }

        //evaluator 1 is not available at timeslots 1 and 2
        for (int i = 0; i < nbP; i++) {
            if (reva[0][i] == 1) { //evaluator 2 is needed for proposal i
                //m.addConstraint(neq(proposals[i].start(),0));
                m.addConstraint(neq(proposals[i].start(),1));
                m.addConstraint(neq(proposals[i].start(),2));
            }
        }

        //proposals are ordered by number of reviewers
        for (int i = 0; i < nbP; i++) {
            for (int j = i + 1; j < nbP; j++) {
                if (numberOfEv[i] < numberOfEv[j]) {
                   m.addConstraint(Choco.startsAfterEnd(proposals[j],proposals[i]));
                }
            }
        }
        m.addConstraints(rsc);
    }

    public void solve() {
        CPSolver solver=new CPSolver();
        solver.setHorizon(horizon);
		solver.read(m);
        solver.minimize(solver.getMakespan(),false);

        //print solution
        if(solver.isFeasible()) {
        for (int i = 0; i < nbP; i++) {
            LOGGER.info("P" + i + ": " + solver.getVar(proposals[i]));
        }
        } else LOGGER.info("no solution");
    }


    public static void main(String[] args) {
        MeetingScheduling mt = new MeetingScheduling(3,3,new int[][]{{1,1,0},
                                                                     {1,0,1},
                                                                     {0,0,1}});
        mt.buildModel();
        mt.solve();
    }
}
