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

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 24, 2009
 * Time: 1:11:38 PM
 */
public class Instances
{


    public static ArrayList<Instances> instances = new ArrayList<Instances>();

    public static int getNbInstances() { return instances.size(); }

    public static Instances getInstance(int idx) { return instances.get(idx); }
    static
    {
        Instances i1 = new Instances(2,4,5,new int[][]{{150,8,150},{200,16,200}},new int[][]{{20,10},{40,4},{50,2},{75,1}});
        Instances i2 = new Instances(2,4,10,new int[][]{{150,8,150},{200,16,200}},new int[][]{{20,20},{40,8},{50,4},{75,2}});
        Instances i3 = new Instances(2,6,12,new int[][]{{150,8,150},{200,16,200}},new int[][]{{10,20},{20,10},{40,8},{50,4},{75,3},{100,1}});



        Instances i4 = new Instances(6,6,9,new int[][]{{50,2,50},
                                                       {100,4,100},
                                                       {150,8,150},
                                                       {200,16,200},
                                                       {250,32,250},
                                                       {300,64,300}
                                                      },new int[][]{{20,10},
                                                                    {40,6},
                                                                    {50,4},
                                                                    {75,2},
                                                                    {100,2},
                                                                    {150,1}
                                                                    });

        instances.add(i1);
        instances.add(i2);
        instances.add(i3);
        instances.add(i4);


    }





    int nbRackModels;
    int nbCardTypes;
    int nbRacks;
    int[][] rackModels;
    int[][] cardTypes;


    private Instances(int nbRackModels, int nbCardTypes, int nbRacks, int[][] rackModels, int[][] cardTypes)
    {
        this.nbRackModels = nbRackModels;
        this.nbCardTypes = nbCardTypes;
        this.nbRacks = nbRacks;
        this.rackModels = rackModels;
        this.cardTypes = cardTypes;
    }


    public final int getNbRackModels()
    {
        return this.nbRackModels;
    }
    public final int getNbCardTypes()
    {
        return this.nbCardTypes;
    }
    public final int getNbRacks()
    {
        return this.nbRacks;
    }



    public final int getRackMaxPower(int rackModel)
    {
        return this.rackModels[rackModel][0];
    }
    public final int getRackCapacity(int rackModel)
    {
        return this.rackModels[rackModel][1];
    }
    public final int getRackPrice(int rackModel)
    {
        return this.rackModels[rackModel][2];
    }


    public final int getCardPower(int cardType)
    {
        return this.cardTypes[cardType][0];
    }
    public final int getCardNeed(int cardType)
    {
        return this.cardTypes[cardType][1];
    }

    public int getNbCard()
    {
        int out = 0;
        for (int i  = 0 ; i < this.getNbCardTypes() ;i++)
            out+=this.getCardNeed(i);
        return out;
    }
    



}