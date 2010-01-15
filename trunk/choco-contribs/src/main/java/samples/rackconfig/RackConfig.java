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

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.DomOverDynDeg;
import choco.cp.solver.search.integer.varselector.DomOverWDeg;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;

import static choco.Choco.*;
/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 24, 2009
 * Time: 1:37:18 PM
 */
public class RackConfig extends CPModel
{

    Instances inst;

    IntegerVariable obj;
    IntegerVariable[][] card;
    IntegerVariable[] rack;
    IntegerVariable[] rackModel;
    IntegerVariable[] boundOccur;
    IntegerVariable[] occur;
    int[] cost;

    public RackConfig(int instIdx)
    {
        inst = Instances.getInstance(instIdx);
    }

     IntegerVariable[] maxPower;
    IntegerVariable[][] count;
    public void addBasicConstraints()
    {
        card = new IntegerVariable[inst.getNbCardTypes()][];
        TIntArrayList cardPower = new TIntArrayList();
        for (int i  = 0; i < card.length ;i++)
        {
            card[i] = makeIntVarArray("card_"+i,inst.getCardNeed(i),0,inst.getNbRacks()-1);
            for (int j = 0 ; j < inst.getCardNeed(i);j++)
                cardPower.add(inst.getCardPower(i));
        }

        rack = makeIntVarArray("rack",inst.getNbRacks(),0,inst.getNbRackModels());

        IntegerVariable[] flatcard = ArrayUtils.flatten(card);
        IntegerVariable[] occur = new IntegerVariable[inst.getNbRacks()];



        for (int i  = 0 ; i < occur.length ; i++)
        {
            occur[i] = makeIntVar("occur_"+i,0,Integer.MAX_VALUE/1000,"cp:bound");
        }
        this.occur = occur;
        IntegerVariable[] boundOccur = makeIntVarArray("boundOccur",occur.length,0,Integer.MAX_VALUE/1000,"cp:bound");
        this.boundOccur = boundOccur;

        this.addConstraint(globalCardinality(flatcard,occur));

        int[] capa = new int[inst.getNbRackModels()+1];
        for (int i  = 0 ;i < inst.getNbRackModels() ; i++)
            capa[i] = inst.getRackCapacity(i);
        capa[inst.getNbRackModels()] = 0;
        for (int i  = 0 ; i < rack.length ; i++)
            this.addConstraint(nth(rack[i],capa,boundOccur[i]));

        for (int i  = 0 ; i < occur.length ; i++)
            this.addConstraint(leq(occur[i],boundOccur[i]));


        IntegerVariable[] maxPower = makeIntVarArray("power",inst.getNbRacks(),0,Integer.MAX_VALUE/1000,"cp:bound");
        this.maxPower = maxPower;
        int[] powers = new int[inst.getNbRackModels()+1];
        for (int i  = 0 ;i < inst.getNbRackModels() ; i++)
            powers[i] = inst.getRackMaxPower(i);
        powers[inst.getNbRackModels()] = 0;

         for (int i  = 0 ; i < rack.length ; i++)
            this.addConstraint(nth(rack[i],powers,maxPower[i]));

        IntegerVariable[] rackModel = makeIntVarArray("rackModel",flatcard.length,0,inst.getNbRackModels()-1);
        this.rackModel = rackModel;

        for (int i  = 0 ; i < flatcard.length ;i++)
            this.addConstraint(nth(flatcard[i],rack,rackModel[i]));



        IntegerVariable[][] count = new IntegerVariable[flatcard.length][inst.getNbRacks()];
        this.count = count;
        for (int i = 0 ; i  < count.length ; i++)
            for (int j  = 0 ; j < count[i].length ; j++)
            {
                count[i][j] = makeBooleanVar("bvar");
            }
        for (int i = 0 ; i < flatcard.length ; i++)
        {
            this.addConstraint(domainConstraint(flatcard[i],count[i]));
        }

        IntegerVariable[][] csum = ArrayUtils.transpose(count);
        int[] cardP = cardPower.toNativeArray();
       

         for (int i = 0 ; i<  csum.length ; i++)
         {
                this.addConstraint(leq(scalar(csum[i],cardP),maxPower[i]));
         }    

          










        IntegerVariable[] cVar = makeIntVarArray("cost",inst.getNbRacks(),0, Integer.MAX_VALUE/1000,"cp:bound");
        int[] csts = new int[inst.getNbRackModels()+1];
        this.cost = csts;
        for (int i  = 0 ;i < inst.getNbRackModels() ; i++)
            csts[i] = inst.getRackPrice(i);
        csts[inst.getNbRackModels()] = 0;

        for (int i  = 0 ; i < rack.length ; i++)
            this.addConstraint(nth(rack[i],csts,cVar[i]));

        obj = makeIntVar("obj",0,Integer.MAX_VALUE/1000,"cp:bound");

        this.addConstraint(eq(obj,sum(cVar)));


    }

    SetVariable[] rack2;
    SetVariable[] rmodel2;
    SetVariable[][] rack3;
    public void addRedondantModel()
    {
        SetVariable[] rack2 = makeSetVarArray("r2",inst.getNbRacks(),0,inst.getNbCard()-1);
        this.rack2 = rack2;
        SetVariable[] rmodel2 = makeSetVarArray("rm2",inst.getNbRackModels()+1,0,inst.getNbRacks()-1);
        this.rmodel2 = rmodel2;

        for (int i  = 0 ; i < rack2.length ; i++)
        {
            this.addConstraint(eqCard(rack2[i],occur[i]));
        }
        this.addConstraint(inverseSet(ArrayUtils.flatten(card),rack2));


        this.addConstraint(inverseSet(rack,rmodel2));

        
        IntegerVariable[] cardRM = makeIntVarArray("cardRM",inst.getNbRackModels(),0,inst.getNbRacks()-1);
        for (int i = 0 ; i < cardRM.length ; i++)
        {
            this.addConstraint(eqCard(rmodel2[i],cardRM[i]));
        }
        int[] csts = new int[inst.getNbRackModels()];
        for (int i  = 0 ;i < inst.getNbRackModels() ; i++)
            csts[i] = inst.getRackPrice(i);
        this.addConstraint(eq(obj,scalar(cardRM,csts)));


        for (int i = 0 ; i < rack2.length ; i++)
        {
            for (int j  = i+1 ; j < rack2.length ; j++)
            {
                this.addConstraint(setDisjoint(rack2[i],rack2[j]));
            }
        }
         for (int i = 0 ; i < rmodel2.length ; i++)
        {
            for (int j  = i+1 ; j < rmodel2.length ; j++)
            {
                this.addConstraint(setDisjoint(rmodel2[i],rmodel2[j]));
            }
        }

        SetVariable[][] rack3 = new SetVariable[inst.getNbRacks()][inst.getNbCardTypes()];
        this.rack3=rack3;
        int cardIdx = 0;
        for (int i  = 0 ; i < rack3.length ; i++)
        {
            for (int j = 0 ;j < rack3[i].length;j++)
            {
                rack3[i][j] = makeSetVar("r3_"+i+"_"+j,0,inst.getNbCard());

            }
        }

        




    }


    public void addSymmetryBreak()
    {
        for (int i = 0 ; i < rack.length-1 ; i++)
        {
            this.addConstraint(geq(rack[i],rack[i+1]));
        }   

        for (int i = 0 ; i < card.length ;i++)
        {
            for (int j = 0 ; j < card[i].length -1 ; j++)
            {
                this.addConstraint(geq(card[i][j],card[i][j+1]));
            }
        }


        for (int i  = 0 ; i < rack.length -1;i++)
        {

            this.addConstraint(implies(eq(rack[i],rack[i+1]),geq(occur[i],occur[i+1])));
        }


        this.setDefaultExpressionDecomposition(true);



    }







    public void solve()
    {
        CPSolver s = new CPSolver();


        s.read(this);

        ArrayList<IntegerVariable> vv = new ArrayList<IntegerVariable>();
        int sz = -1;
        for (int i  = 0 ; i < card.length ; i++)
        {
            if (card[i].length > sz) sz = card[i].length;
        }

        for (int j = 0 ; j < sz ; j++)
        {
            for (int i = 0 ; i < card.length ; i++)
            {
                if (j < card[i].length)
                vv.add(card[i][j]);
            }
        }
        IntegerVariable[] flatcard = ArrayUtils.flatten(card);
        int cut = flatcard.length/rack.length;
        int r = flatcard.length%rack.length;
        ArrayList<IntegerVariable[]> aa = new ArrayList<IntegerVariable[]>();
        for (int i  = 0 ; i < rack.length ;i++)
        {
            aa.add(subset(flatcard,i*cut,i*cut+cut));
        }
        if (r > 0)
        {
            aa.add(subset(flatcard,flatcard.length-r-1,flatcard.length));
        }
        IntegerVariable[][] tmp = aa.toArray(new IntegerVariable[aa.size()][]);

        IntegerVariable[] bui = vv.toArray(new IntegerVariable[vv.size()]);

       s.attachGoal(new AssignVar(new DomOverDynDeg(s,s.getVar(ArrayUtils.flatten(card))),new IncreasingDomain()));
        s.addGoal(new AssignVar(new DomOverDynDeg(s,s.getVar(rack)),new RackValSelector(cost)));
       // s.setGeometricRestart(10000,0.9);
      //  s.setRestart(true);
     //   s.setRecordNogoodFromRestart(true);
    //   s.attachGoal(new AssignVar(new DomOverDynDeg(s,s.getVar(rack)),new RackValSelector(cost)));
     //   s.addGoal(new AssignVar(new DomOverDynDeg(s,s.getVar(ArrayUtils.flatten(card))),new IncreasingDomain()));

     /*   s.attachGoal(new AssignVar(new StaticVarOrder(new IntDomainVar[]{s.getVar(rack[0])}),new RackValSelector(cost)));

        s.addGoal(new AssignVar(new DomOverDynDeg(s,s.getVar(tmp[0])),new IncreasingDomain()));
        for (int i = 1 ; i < rack.length ; i++)
        {
            s.addGoal(new AssignVar(new StaticVarOrder(new IntDomainVar[]{s.getVar(rack[i])}),new RackValSelector(cost)));

        s.addGoal(new AssignVar(new DomOverDynDeg(s,s.getVar(tmp[i])),new IncreasingDomain()));

        }
        s.addGoal(new AssignVar(new DomOverDynDeg(s,s.getVar(tmp[tmp.length-1])),new IncreasingDomain()));   */
      
        //if (s.solve())
        if(s.solve())
        {
            do
            {
                for (IntDomainVar v : s.getVar(rack))
                    System.out.print(v.pretty()+ " ");
                System.out.println("");
                printSolution(s);
                s.printRuntimeStatistics();
                
                s.postCut(s.lt(s.getVar(obj),s.getVar(obj).getVal()));

            } while(s.nextSolution());
        }
        s.printRuntimeStatistics();
        

    }

    public static IntegerVariable[] subset(IntegerVariable[] tab,int start, int end)
    {
        IntegerVariable[] sub = Arrays.copyOfRange(tab,start,end);
        return sub;
    }

    public void printSolution(CPSolver s)
    {
        System.out.println("COUT : "+s.getVar(obj).getVal());
        System.out.println("");
        for (int i  = 0 ; i < rack.length; i++)
        {
            int res = s.getVar(rack[i]).getVal();
            if (res < inst.getNbRackModels())
                System.out.println("Rack de model "+res+" dans l'emplacement "+i);
            else
                System.out.println("Pas de rack dans l'emplacement "+i);
        }
        System.out.println("");
        int k = 0 ;
        for (int i  = 0 ; i < card.length ; i++)
        {
            System.out.println("Carte de type "+i+" :");
            for (int j = 0 ; j < card[i].length ; j++)
            {
                System.out.println("\tCarte "+j+" dans le rack : "+s.getVar(card[i][j]).getVal()+"    |   RModel : "+s.getVar(rackModel[k++]).getVal());
            }
            System.out.println("");
        }


        for (int i = 0 ; i < maxPower.length ;i++)
        {
            System.out.println(s.getVar(maxPower[i]).getVal());

        }
        IntegerVariable[][] count = ArrayUtils.transpose(this.count);
        for (int i  = 0; i < count.length; i ++)
        {
            for (int j  = 0 ; j < count[i].length ; j++)
            {
                System.out.print(s.getVar(count[i][j]).getVal()+" ");
            }

            System.out.println("");
        }

        s.getVar(rack2[0]).getValue();
     /*   for (int i  = 0 ; i < rack2.length ; i++)
        {
            System.out.println(s.getVar(rack2[i]).pretty());
        }

        for (int i  = 0 ; i < rmodel2.length ; i++)
        {
            System.out.println(s.getVar(rmodel2[i]).pretty());
        }
        for (int i = 0 ;i < rack3.length ;i++)
        {
            for (int j = 0 ; j < rack3[i].length ; j++)
            {
                System.out.print(s.getVar(rack3[i][j]).pretty()+" ");
            }
            System.out.println("");
        }  */
    }


    public static void main(String[] args) {
        RackConfig rc = new RackConfig(1);
        rc.addBasicConstraints();
     //   rc.addSymmetryBreak();
       // rc.addRedondantModel();
        rc.solve();
    }


}