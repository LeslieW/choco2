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
package choco.cp.solver.constraints.global.multicostregular.asap.hci.abstraction;

import choco.cp.solver.constraints.global.multicostregular.asap.ASAPCPModel;
import choco.cp.solver.constraints.global.multicostregular.asap.heuristics.ASAPVarSelector;
import choco.cp.solver.constraints.global.multicostregular.asap.heuristics.ASAPValSelector;
import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPPattern;
import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPShift;
import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPEmployee;
import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPSkill;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.solver.Solver;
import choco.kernel.common.util.UtilAlgo;

import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 1:14:21 PM
 */
public class ASAPDataHandler extends Observable {

    ASAPCPModel model;
    Solver solver;
    String file;
    Thread solving;
    public final static String DAYS[] = new String[]{"M ","Tu","W ","Th","F ","Sa","Su"};
    public final static Integer MODEL_FED       = 0;
    public final static Integer SOLVING         = 1;
    public final static Integer SOLUTION_FOUND  = 2;
    public final static Integer NO_SOLUTION     = 3;
    private boolean solved = false;


    public ASAPDataHandler()
    {
    }

    public void feed(String name)
    {
        this.file = name;
        this.solved =false;
        model = new ASAPCPModel(name);
        model.buildModel();
        this.solving = new ASAPResolutionThread(this);
        this.setChanged();
        this.notifyObservers(MODEL_FED);

    }


    public void solve()
    {
        this.solver = new CPSolver();
        this.solver.monitorFailLimit(true);
        this.solver.read(this.model);
        this.solver.setVarIntSelector(
                new StaticVarOrder(
                        this.solver.getVar(UtilAlgo.flatten(UtilAlgo.transpose(this.model.shifts)))
                )
        );
        //this.solver.setVarIntSelector(new ASAPVarSelector(this.solver,UtilAlgo.transpose(this.model.shifts)));
        //this.solver.setValIntSelector(new ASAPValSelector(this.solver,UtilAlgo.transpose(this.model.shifts),this));

        solving.start();
        this.setChanged();
        this.notifyObservers(SOLVING);

    }

    public ASAPCPModel getCPModel()
    {
        return model;
    }

    public Solver getCPSolver()
    {                
        return solver;
    }


    public void setSolved(boolean b) {
        this.setChanged();
        //System.out.println(solving.getState());

        if (b)
        {
            solved = true;
            this.notifyObservers(SOLUTION_FOUND);
        }
        else
        {
            this.notifyObservers(NO_SOLUTION);
        }
        solving = new ASAPResolutionThread(this);
    }

    public void next() {
        solving.start();
        solving = new ASAPResolutionThread(this);
    }

    public boolean isSolved() {
        return solved;
    }
}