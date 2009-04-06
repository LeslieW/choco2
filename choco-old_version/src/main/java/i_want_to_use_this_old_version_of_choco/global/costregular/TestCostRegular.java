package i_want_to_use_this_old_version_of_choco.global.costregular;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.global.costregular.FA.Automaton;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.StaticVarOrder;
import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 5, 2007
 * Time: 3:40:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestCostRegular extends Problem {

    IntDomainVar[] vars;
    IntDomainVar cost;
    Constraint constraint;
    boolean incremental;
    long[] out;

    public TestCostRegular(int nbVars, String regexp, boolean incremental)
    {
        super(new EnvironmentTrailing());

        this.incremental = incremental;
        long start = - System.currentTimeMillis();

        IntDomainVar[] vars =  this.makeEnumIntVarArray("x",nbVars,0,9);



        cost = this.makeBoundIntVar("c",0,Integer.MAX_VALUE);
        Random r = new Random(1);
        int[][] z = new int[vars.length-1][10];
        for (int i = 0 ; i < z.length ; i++)  {
            for (int j = 0 ; j < z[i].length ; j++) {
                z[i][j] = r.nextInt(200);
                //System.out.println(z[i][j]);
            }
        }
        constraint = CostRegular.make(vars,cost,new Automaton(regexp),z);
        if (incremental)
            CostRegular.INCREMENTAL = true;
        else
            CostRegular.INCREMENTAL = false;



        post(constraint);
        this.getSolver().setVarIntSelector(new StaticVarOrder(vars));
        //this.getSolver().setValSelector(new MinVal());
        //this.getSolver().setVarSelector(new MinDomain(this));
        this.getSolver().setValIntSelector(new CostRegularValSelector((CostRegular) constraint,false));
        out = test();
        this.printRuntimeSatistics();

    }


    public long[] test()
    {
        int nbSol = 0;
        long start = - System.currentTimeMillis();
        if (minimize(cost,false))
        {
            do
            {

                nbSol++;
                //for (IntDomainVar var : vars)
                //  System.out.print(var.getVal()+" ");
                // System.out.println("");
                //  if (withcost) {System.out.println("COST : "+cost.getVal());
                //System.out.println(((CostRegular) constraint).isSatisfied());
                //}

            } while (nextSolution());
        }
        long time = System.currentTimeMillis()+start;
        //System.out.println("BONNE SOLUTION ? "+constraint.isSatisfied());
        long[] out = new long[]{nbSol,getSolver().getSearchSolver().getNodeCount(),time};
        System.out.println(out[0]+"[+0] sol.");
        return out;
    }

    public static void main(String[] args) {
        String regexp;// = "1+(2|4)+(9|9)8+";
        boolean eq = true;
        long aPin = 0;
        long aIn = 0;

        for (int m = 60 ; m <=60 ; m+=30) {
            int n = m;
            Random r = new Random(m);

            for (int c = 0 ; c < 30 ; c++){
                int kleeneNb = 0;

                regexp = "";
                for (int i = 0 ; i < n ; i++) {
                    int rand = r.nextInt(10);
                    int kleene = r.nextInt(3);
                    String kleeneS = (kleene==0?"":(kleene==1?"+":"*"));

                    boolean in_exp = r.nextBoolean();
                    if (in_exp) {
                        regexp+= rand+kleeneS;
                        if (kleeneS.equals("*") || kleeneS.equals("+"))
                            kleeneNb++;
                    }

                }
                if (kleeneNb == 0 && regexp.length() != n)
                    continue;
                System.out.println(regexp);
                System.out.println("n = "+n);
                //System.out.println("PAS INC");
                //long[] pin  = new TestCostRegular(n,regexp,false).out;
                System.out.println("INC");
                long[] in = new TestCostRegular(n,regexp,true).out;
                System.out.println("");

                //eq &= (pin[0] == in[0] && pin[1] == in[1]);

                //aPin+= pin[2];
                aIn+=in[2];


            }
        }
        System.out.println("");
        //System.out.println("m�me nombre de sol et de noeud ? "+eq);
        //System.out.println("Temps cumul� pour pas increm : "+aPin);
        System.out.println("Temps cumul� pour  increm : "+aIn);



    }



}
