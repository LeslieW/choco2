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
package samples.seminar.binpacking;


import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.integer.search.DecreasingDomain;
import i_want_to_use_this_old_version_of_choco.integer.search.StaticVarOrder;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Xavier Lorca
 * Date: 2 oct. 2007
 * Time: 07:42:16
 */
public class ExBinPackingv1 {

    public int[] getRandomPackingPb(int nbObj, int capa, int seed) {
        Random rand = new Random(seed);
        int[] instance = new int[nbObj];
        for (int i = 0; i < nbObj; i++) {
            instance[i] = rand.nextInt(capa) + 1;
        }
        return instance;
    }

    public void afficheInst(int[] instance) {
        for (int j = 0; j < instance.length; j++) {
            System.out.println("Bin " + j + ": " + instance[j]);
        }
        System.out.println("");
    }

    public int computeLB(int[] instance, int capa) {
        int load = 0;
        for (int i = 0; i < instance.length; i++) {
            load += instance[i];
        }
        return (int) Math.ceil((double) load / (double) capa);
    }

    // modèle de satisfaction
    // n : nombre d'objets
    // capaBin : la capacité des bins
    public void binPacking1(int n, int capaBin, int seed) {
        boolean keepSolving = true;
        int[] instance = getRandomPackingPb(n, capaBin, seed);
        QuickSort sort = new QuickSort(instance); // trie les objets par ordre de taille croissante
        sort.sort();
        afficheInst(instance);
        int nbBin = computeLB(instance, capaBin);

        while (keepSolving) {
            System.out.print("------------------------" + nbBin + " bins");
            Problem pb = new Problem();
            IntDomainVar[][] vs = new IntDomainVar[n][nbBin];
            IntDomainVar[] vars = new IntDomainVar[n * nbBin];
            //IntVar[] object = new IntVar[n];
            IntDomainVar[] sumBin = new IntDomainVar[nbBin];
            int cpt = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < nbBin; j++) {
                    vs[i][j] = pb.makeEnumIntVar("obj " + i + "_" + j, 0, 1);
                    vars[cpt] = vs[i][j];
                    cpt++;
                }
            }
            for (int j = 0; j < nbBin; j++) {
                sumBin[j] = pb.makeBoundIntVar("sumBin " + j + "_" + j, 0, capaBin);
            }
            for (int j = 0; j < nbBin; j++) { // les bin ont une capacité limitée
                IntVar[] col = new IntVar[n];
                for (int i = 0; i < n; i++) {
                    col[i] = vs[i][j];
                }
                pb.post(pb.eq(pb.scalar(col, instance), sumBin[j])); // sumBin correspond à la capacité utilisée de chaque bin
                pb.post(pb.leq(sumBin[j], capaBin));
            }
            for (int i = 0; i < n; i++) { // chaque objet doit aller dans un seul et unique bin
                pb.post(pb.eq(pb.sum(vs[i]), 1));
            }

            //long tps = System.currentTimeMillis();
            pb.getSolver().setVarIntSelector(new StaticVarOrder(vars));
            pb.getSolver().setValIntIterator(new DecreasingDomain());
            Solver.setVerbosity(Solver.SOLUTION);
            pb.solve();
            Solver.flushLogs();
            //tps = System.currentTimeMillis() - tps;
            //System.out.println(" tps " + tps + " node " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
            // affichage du packing
            if (pb.isFeasible() == Boolean.TRUE) {
                for (int j = 0; j < nbBin; j++) {
                    System.out.print("Bin " + j + ": ");
                    int load = 0;
                    for (int i = 0; i < n; i++) {
                        if (vs[i][j].isInstantiatedTo(1)) {
                            System.out.print(i + " ");
                            load += instance[i];
                        }
                    }
                    System.out.println(" - load " + load + " = " + sumBin[j].getVal());
                }
                keepSolving = false;
            }
            nbBin++;
        }
    }

    // modèle d'optimisation
    // n : nombre d'objets
    // capaBin : la capacité des bins
    public void binPacking2(int n, int capaBin, int seed) {
        int[] instance = getRandomPackingPb(n, capaBin, seed);
        QuickSort sort = new QuickSort(instance); // trie les objets par ordre de taille croissante
        sort.sort();
        Problem pb = new Problem();
        IntDomainVar[] debut = new IntDomainVar[n];
        IntDomainVar[] duree = new IntDomainVar[n];
        IntDomainVar[] fin = new IntDomainVar[n];

        int nbBinMin = computeLB(instance, capaBin);
        for (int i = 0; i < n; i++) {
            debut[i] = pb.makeEnumIntVar("debut " + i, 0, n);
            duree[i] = pb.makeEnumIntVar("duree " + i, 1, 1);
            fin[i] = pb.makeEnumIntVar("fin " + i, 0, n);
        }
        IntDomainVar obj = pb.makeEnumIntVar("nbBin ", nbBinMin, n);
        pb.post(pb.cumulative(debut, fin, duree, instance, capaBin));
        for (int i = 0; i < n; i++) {
            pb.post(pb.geq(obj, debut[i]));
        }

        IntDomainVar[] branchvars = new IntDomainVar[n + 1];
        System.arraycopy(debut, 0, branchvars, 0, n);
        branchvars[n] = obj;

        //long tps = System.currentTimeMillis();
        pb.getSolver().setVarIntSelector(new StaticVarOrder(branchvars));
        Solver.setVerbosity(Solver.SOLUTION);
        pb.minimize(obj, false);
        Solver.flushLogs();
        //tps = System.currentTimeMillis() - tps;
        // print solution
        System.out.println("------------------------ " + (obj.getVal() + 1) + " bins");
        if (pb.isFeasible() == Boolean.TRUE) {
            for (int j = 0; j <= obj.getVal(); j++) {
                System.out.print("Bin " + j + ": ");
                int load = 0;
                for (int i = 0; i < n; i++) {
                    if (debut[i].isInstantiatedTo(j)) {
                        System.out.print(i + " ");
                        load += instance[i];
                    }
                }
                System.out.println(" - load " + load);
            }
            //System.out.println("tps " + tps + " node " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
        }
    }

    public static void main(String[] args) {
        ExBinPackingv1 tp2 = new ExBinPackingv1();
        System.out.println("************** Modèle Booléen **************");
        tp2.binPacking1(10, 13, 1);
        System.out.println("");
        System.out.println("************** Modèle Cumulatif ***************");
        tp2.binPacking2(10, 13, 1);
    }
}
