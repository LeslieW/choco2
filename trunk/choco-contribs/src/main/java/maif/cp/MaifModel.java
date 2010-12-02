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