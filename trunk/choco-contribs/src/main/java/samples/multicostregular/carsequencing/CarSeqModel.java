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
package samples.multicostregular.carsequencing;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import gnu.trove.TIntHashSet;
import samples.multicostregular.carsequencing.parser.CarSeqInstance;
import samples.multicostregular.carsequencing.parser.GraphGenerator;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 28, 2009
 * Time: 4:25:06 PM
 */
public class CarSeqModel extends CPModel {

    public IntegerVariable[] seqVars;
    public IntegerVariable[] occVars;

    private CarSeqInstance instance;


    public CarSeqModel(String description,boolean unique)
    {

        this.instance = new CarSeqInstance(description);
        this.buildModelFromInstance(this.instance,unique);
    }

    public CarSeqInstance getInstance()
    {
        return this.instance;
    }

    public void buildModelFromInstance(CarSeqInstance make,boolean unique)
    {


        FiniteAutomaton auto = new FiniteAutomaton();
        ArrayList<dk.brics.automaton.Automaton> list = new ArrayList<dk.brics.automaton.Automaton>();

        IntegerVariable[] vs = makeIntVarArray("pos",make.nbCars,0,make.nbClasses-1);
        IntegerVariable[] z = new IntegerVariable[make.nbClasses];
        this.seqVars = vs;
        this.occVars = z;
        for (int i = 0 ; i < z.length ; i++)
        {
            int nb = make.optionRequirement[i][1];
            z[i] = makeIntVar("nb",nb,nb);
        }

        this.addVariables(Options.V_ENUM,vs);
        this.addVariables(Options.V_BOUND,z);




        TIntHashSet alphabet = new TIntHashSet();
        for (int i = 0; i < make.nbClasses ; i++) alphabet.add(i);

        for (int i = 0 ; i < make.blockSize.length ; i++)
        {

            TIntHashSet cin = new TIntHashSet();
            TIntHashSet cout = new TIntHashSet();

            for (int j = 0 ; j < make.nbClasses ; j++)
            {
                if (make.optionRequirement[j][i+2] == 1)
                    cin.add(j);
                else
                    cout.add(j);
            }



            GraphGenerator gg = GraphGenerator.make(make.maxPerBlock[i],make.blockSize[i]);
            dk.brics.automaton.Automaton aa = gg.toBricsAutomaton(cin.toArray(),cout.toArray());
            aa.minimize();
            list.add(aa);
        }






        int[][][] csts = new int[vs.length][make.nbClasses][make.nbClasses];
        for (int i = 0 ; i < csts.length ; i++)
            for (int j = 0 ; j < csts[i].length ; j++)
                for (int k = 0 ; k < csts[i][j].length ;k++)
                {
                    if (j == k) csts[i][j][k] = 1;
                }



        if (unique)
        {

            dk.brics.automaton.Automaton inter = list.get(0);
            for (dk.brics.automaton.Automaton a : list)
            {
                inter = inter.intersection(a);
                inter.minimize();
            }

            auto.fill(inter,alphabet);


            Constraint cons = multiCostRegular(z, vs, auto,csts);
            this.addConstraint(cons);
        }
        else
        {
            int i = 0;
            for (dk.brics.automaton.Automaton a : list)
            {
                if (true || i == 0)
                {
                    FiniteAutomaton tomate = new FiniteAutomaton();
                    tomate.fill(a,alphabet);
                    Constraint cons = multiCostRegular(z, vs, tomate,csts);
                    this.addConstraint(cons);

                    i++;
                }
            }

        }






    }

}