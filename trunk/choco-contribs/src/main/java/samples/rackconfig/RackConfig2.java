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
package samples.rackconfig;

import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 25, 2009
 * Time: 8:10:35 PM
 */
public class RackConfig2 extends CPModel {

    Instances inst;
    IntegerVariable cost;
    IntegerVariable[] w;

    public RackConfig2(int idx)
    {
        this.inst = Instances.getInstance(idx);
    }

    IntegerVariable[][] rel;
    public void makePrimalModel()
    {
        rel = makeIntVarArray("rel",inst.getNbRackModels()*inst.getNbRacks(),inst.getNbCard(),0,1);


        for (int i  = 0 ; i < inst.getNbRackModels() ; i++)
        {
            for (int j = 0 ; j < inst.getNbRacks() ; j++)
            {
                this.addConstraint(leq(sum(rel[i*inst.getNbRacks()+j]),inst.getRackCapacity(i)));

                ArrayList<IntegerExpressionVariable> iee = new ArrayList<IntegerExpressionVariable>();
                int l = 0;
                for (int k = 0 ; k < inst.getNbCardTypes() ; k++)
                {
                    IntegerVariable[] tmp = Arrays.copyOfRange(rel[i*inst.getNbRacks()+j],l,inst.getCardNeed(k)+l);
                    l+= inst.getCardNeed(k);
                    iee.add(mult(sum(tmp),inst.getCardPower(k)));
                }

                this.addConstraint(leq(sum(iee.toArray(new IntegerExpressionVariable[iee.size()])),inst.getRackMaxPower(i)));

            }
        }

        //demand constraint

        IntegerVariable[][] relTrans = ArrayUtils.transpose(rel);
        int idx = 0;
        for (int i = 0 ; i < inst.getNbCardTypes();  i++)
        {
            for (int j = 0 ;j < inst.getCardNeed(i) ; j++)
            {
                this.addConstraint(eq(sum(relTrans[idx++]),1));

            }
        }

        //cardinality constraint
        w = makeIntVarArray("w",inst.getNbRackModels()*inst.getNbRacks(),0,1);

        this.addConstraint(leq(sum(w),inst.getNbRacks()));

        //linking constraint


        for (int i  = 0 ; i < inst.getNbRackModels() ; i++)
        {
            for (int j = 0 ; j < inst.getNbRacks() ; j++)
            {
                int sofar = 0;
                for (int k = 0 ; k < inst.getNbCardTypes() ; k++)
                {
                    for (int l = 0 ; l < inst.getCardNeed(k) ; l++)
                    {
                        this.addConstraint(implies(eq(rel[i*inst.getNbRacks()+j][sofar++],1),eq(w[i*inst.getNbRacks()+j],1)));
                    }
                }
            }
        }



        cost  = makeIntVar("cost",0,Integer.MAX_VALUE/1000, CPOptions.V_BOUND);

        ArrayList<IntegerExpressionVariable> ie = new ArrayList<IntegerExpressionVariable>();
        for (int i = 0 ; i < inst.getNbRackModels() ; i++)
        {
            IntegerVariable[] tmp = Arrays.copyOfRange(w,i*inst.getNbRacks(),(i+1)*inst.getNbRacks());
            ie.add(mult(sum(tmp),inst.getRackPrice(i)));
        }
        this.addConstraint(eq(cost,sum(ie.toArray(new IntegerExpressionVariable[ie.size()]))));


    }


    public static void main(String[] args) {
        RackConfig2 rc = new RackConfig2(0);

        rc.makePrimalModel();


        CPSolver s = new CPSolver();

        s.read(rc);
        System.out.println(s.minimize(s.getVar(rc.cost),false));
        System.out.println(s.getVar(rc.cost).getVal());

        for (int i = 0 ; i < rc.rel.length ;i++)
        {
            for (int j = 0 ; j < rc.rel[i].length ; j++)
            {
                System.out.print(s.getVar(rc.rel[i][j]).getVal()+" ");
            }
            System.out.println("");
        }
        for (int i = 0 ; i < rc.w.length ; i++)
        {
            System.out.print(s.getVar(rc.w[i]).getVal());
        }
        System.out.println("");
        s.printRuntimeStatistics();

    }

}