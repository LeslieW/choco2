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
package maif.cp;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.kernel.model.variables.integer.IntegerVariable;
import maif.entities.Person;
import maif.entities.Preference;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 21, 2009
 * Time: 11:38:05 AM
 */
public class MaifModel extends CPModel {

    ArrayList<Person> people;
    ArrayList<Date> days;

    private IntegerVariable[] dayVars;
    IntegerVariable[] card;
    IntegerVariable max;
    IntegerVariable min;
    IntegerVariable deviation;
    ArrayList<IntegerVariable[]> perday;
    HashMap<IntegerVariable, Preference> varMap;
    boolean bouchon;


    public MaifModel(ArrayList<Person> people,ArrayList<Date> days,boolean bouchon)
    {
        this.people = people;
        this.bouchon = bouchon;
        this.days = days;
        this.perday = new ArrayList<IntegerVariable[]>();
        this.varMap = new HashMap<IntegerVariable,Preference>();
        fillModel();

    }

    private void fillModel()
    {
        dayVars = makeIntVarArray("shift",this.days.size()*4,0,this.people.size()-1);
        this.addVariables(dayVars);
        for (int i = 0 ; i < this.days.size() ; i++)
        {

            IntegerVariable[] aDay = Arrays.copyOfRange(dayVars,i*4,i*4+4);
            int k = 0;
            for (IntegerVariable v : aDay)
                varMap.put(v,new Preference(this.days.get(i),(k++)));
            perday.add(aDay);
            this.addConstraint(allDifferent(Options.C_ALLDIFFERENT_BC,aDay));

            //On Supprime les gens en vacances :)
            for (Person p : this.people)
            {
                if (p.getHollydays().contains(this.days.get(i)))
                {
                    for (IntegerVariable v : aDay)
                        this.addConstraint(neq(v,p.getIndex()));
                }
            }



        }

        card = makeIntVarArray("card",this.people.size(),0,this.dayVars.length);

        this.addConstraint(globalCardinality(dayVars,card, 0));
        max = makeIntVar("max",0,this.dayVars.length, Options.V_BOUND);
        this.addConstraint(max(card,max));
        min = makeIntVar("max",0,this.dayVars.length, Options.V_BOUND);
        this.addConstraint(min(card,min));
        deviation = makeIntVar("max",0,this.dayVars.length, Options.V_BOUND);
        this.addConstraint(eq(deviation,minus(max,min)));

        HashSet<Person> prior = new HashSet<Person>();
        HashSet<Person> nonprior = new HashSet<Person>();
        for (Person p : this.people)
        {
            if (p.getPriority())
                prior.add(p);
            else
                nonprior.add(p);

        }

        if (bouchon)
        {
            for (Person p : prior)
            {
                for (Person p2 : nonprior)
                {
                    this.addConstraint(geq(card[p.getIndex()],card[p2.getIndex()]));
                }
            }
        }



    }



    public IntegerVariable[] getDayVars()
    {
        return this.dayVars;
    }

    public IntegerVariable[] getCardVars()
    {
        return this.card;
    }

    public ArrayList<Date> getDays()
    {
        return this.days;
    }


    public HashMap<IntegerVariable,Preference> getVarMap()
    {
        return this.varMap;
    }

    public ArrayList<Person> getPeople()
    {
        return this.people;
    }


    public IntegerVariable getDeviation() {
        return deviation;
    }

    public IntegerVariable getMin()
    {
        return min;
    }

    public IntegerVariable getMax() {
        return max;
    }
}