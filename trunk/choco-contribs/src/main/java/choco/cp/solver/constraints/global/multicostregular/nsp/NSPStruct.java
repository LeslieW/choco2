package choco.cp.solver.constraints.global.multicostregular.nsp;

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 8, 2008
 * Time: 11:55:09 PM
 */


public class NSPStruct extends AbstractLargeIntSConstraint {

    public IStateInt[][] need;
    IStateInt nbInstanciated;
    IStateInt[] instPerDay;
    IStateInt[] sumPerDay;
    IStateInt positiveSum;
    public NSPInstance instance;



    public NSPStruct(IntDomainVar[] vars,NSPInstance instance) {
        super(vars);
        this.instance = instance;
        this.nbInstanciated = this.getSolver().getEnvironment().makeInt(0);
        this.positiveSum = this.getSolver().getEnvironment().makeInt(0);

        this.need = new IStateInt[instance.nbDays][instance.nbShifts];

        this.instPerDay =  new IStateInt[instance.nbDays];
        this.sumPerDay =  new IStateInt[instance.nbDays];

        for (int i = 0 ; i < this.instPerDay.length ; i++)
        {
            this.instPerDay[i] = this.getSolver().getEnvironment().makeInt(0);
            this.sumPerDay[i] = this.getSolver().getEnvironment().makeInt(0);
        }


    }

    protected IStateInt getTypeNeed(int nbDays, int shiftType)
    {
        IStateInt tmp = need[nbDays][shiftType] ;
        if (tmp == null)
        {
            tmp = need[nbDays][shiftType] = this.getSolver().getEnvironment().makeInt(0);
        }
        return tmp;
    }


    public void awakeOnInst(int idx) throws ContradictionException {
        int day = idx%instance.nbDays;
        this.nbInstanciated.add(1);
        IStateInt tmp = getTypeNeed(day,vars[idx].getVal());
        tmp.add(-1);
        if (tmp.get() >=0 )
        {
            this.positiveSum.add(-1);
        }

        for (int i = 0 ; i < instance.nbDays ; i++) {
            StringBuffer st = new StringBuffer();
            for  (int j =0 ; j < instance.nbShifts ; j++)
                st.append(getTypeNeed(i,j).get()+"\t");
            LOGGER.info(st.toString());
        }

      /*  if (this.positiveSum.get() > (this.vars.length-this.nbInstanciated.get()))
            this.fail();
        if (this.sumPerDay[day].get() > instance.nbNurses - this.instPerDay[day].get())
            this.fail();*/

    }
    public void awakeOnRemovals(int idx, IntIterator it){}
    public void awakeOnRem(int idx, int val){}
    public void awakeOnSup(int idx){}
    public void awakeOnInf(int idx){}

    public void propagate() {}


    public void awake() throws ContradictionException
    {
        for (int i =0 ; i < instance.nbDays ;i++)
            for (int j = 0 ;j < instance.nbShifts ;j++)
                getTypeNeed(i,j).set(instance.coverages[i][j]);

        for (int i = 0 ; i < instance.nbDays ;i++)
        {
            for (int j = 0 ; j < instance.nbNurses ; j++)
            {
                if (vars[j*instance.nbDays+i].isInstantiated())
                {
                    this.nbInstanciated.add(1);
                    this.instPerDay[i].add(1);
                    int k =  vars[j*instance.nbDays+i].getVal();
                    getTypeNeed(i,k).add(-1);
                }
            }
        }

        for (int i = 0 ; i < instance.nbDays ;i++)
        {
            for (int j = 0 ; j < instance.nbShifts ; j++)
            {
                int tmp = getTypeNeed(i,j).get();
                if (tmp>0) {
                    this.positiveSum.add(tmp);
                    this.sumPerDay[i].add(tmp);

                }


            }
        }

    }

    public static class NSPStrucManager extends IntConstraintManager
    {

        public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {

            IntegerVariable[] vs = (IntegerVariable[]) variables;
            if (parameters instanceof NSPInstance)
            {
                NSPInstance instance = (NSPInstance) parameters;
                return new NSPStruct(solver.getVar(vs),instance);
            }
            return null;
        }
    }
}
