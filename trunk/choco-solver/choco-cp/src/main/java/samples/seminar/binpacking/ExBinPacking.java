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


import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import samples.pack.CPpack;

import static java.text.MessageFormat.format;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Xavier Lorca
 * Date: 2 oct. 2007
 * Time: 07:42:16
 */
public class ExBinPacking {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

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
            LOGGER.info("Bin " + j + ": " + instance[j]);
        }
        LOGGER.info("");
    }

    public int computeLB(int[] instance, int capa) {
        int load = 0;
        for (int i = 0; i < instance.length; i++) {
            load += instance[i];
        }
        return (int) Math.ceil((double) load / (double) capa);
    }

    /**
     * First model
     * @param n nb bin
     * @param capaBin capacity
     * @param seed random root
     */
    public void binPacking1(int n, int capaBin, int seed) {
        boolean keepSolving = true;
        int[] instance = getRandomPackingPb(n, capaBin, seed);
        QuickSort sort = new QuickSort(instance); // trie les objets par ordre de taille croissante
        sort.sort();
        afficheInst(instance);
        int nbBin = computeLB(instance, capaBin);

        while (keepSolving) {
            LOGGER.info("------------------------" + nbBin + " bins");
            Model m = new CPModel();
            IntegerVariable[][] vs = new IntegerVariable[n][nbBin];
            IntegerVariable[] vars = new IntegerVariable[n * nbBin];
            IntegerVariable[] sumBin = new IntegerVariable[nbBin];
            int cpt = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < nbBin; j++) {
                    vs[i][j] = makeIntVar("obj " + i + "_" + j, 0, 1);
                    vars[cpt] = vs[i][j];
                    cpt++;
                }
            }
            for (int j = 0; j < nbBin; j++) {
                sumBin[j] = makeIntVar("sumBin " + j + "_" + j, 0, capaBin);
            }
            m.addVariables("cp:bound",sumBin);
            for (int j = 0; j < nbBin; j++) {
                IntegerVariable[] col = new IntegerVariable[n];
                for (int i = 0; i < n; i++) {
                    col[i] = vs[i][j];
                }
                m.addConstraint(eq(scalar(col, instance), sumBin[j]));
                m.addConstraint(leq(sumBin[j], capaBin));
            }
            for (int i = 0; i < n; i++) { // Each object has to be placed in one bin
                m.addConstraint(eq(sum(vs[i]), 1));
            }

            Solver s = new CPSolver();
            s.read(m);
            s.setVarIntSelector(new StaticVarOrder(s, s.getVar(vars)));
            s.setValIntIterator(new DecreasingDomain());
            s.solve();
            // Print of solution
            if (s.isFeasible() == Boolean.TRUE) {
                for (int j = 0; j < nbBin; j++) {
                    StringBuffer st = new StringBuffer();
                    st.append("Bin " + j + ": ");
                    int load = 0;
                    for (int i = 0; i < n; i++) {
                        if (s.getVar(vs[i][j]).isInstantiatedTo(1)) {
                            st.append(i + " ");
                            load += instance[i];
                        }
                    }
                    st.append(" - load " + load + " = " + s.getVar(sumBin[j]).getVal());
                    LOGGER.info(st.toString());
                }
                keepSolving = false;
            }
            nbBin++;
        }
    }

    /**
     * Optimize model
     * @param n nb of objects
     * @param capaBin capacity
     * @param seed
     */
    public void binPacking2(int n, int capaBin, int seed) {
        int[] instance = getRandomPackingPb(n, capaBin, seed);
        QuickSort sort = new QuickSort(instance);
        sort.sort();
        Model m = new CPModel();
        IntegerVariable[] debut = new IntegerVariable[n];
        IntegerVariable[] duree = new IntegerVariable[n];
        IntegerVariable[] fin = new IntegerVariable[n];
        int nbBinMin = computeLB(instance, capaBin);
        for (int i = 0; i < n; i++) {
            debut[i] = makeIntVar("debut " + i, 0, n);
            duree[i] = makeIntVar("duree " + i, 1, 1);
            fin[i] = makeIntVar("fin " + i, 0, n);
        }
        IntegerVariable obj = makeIntVar("nbBin ", nbBinMin, n);
        TaskVariable[] tasks = makeTaskVarArray("t", debut, fin, duree);
        m.addConstraint(cumulativeMax(tasks, instance, capaBin));
        for (int i = 0; i < n; i++) {
            m.addConstraint(geq(obj, debut[i]));
        }

        IntegerVariable[] branchvars = new IntegerVariable[n + 1];
        System.arraycopy(debut, 0, branchvars, 0, n);
        branchvars[n] = obj;

        Solver s = new CPSolver();
        s.read(m);
        s.monitorBackTrackLimit(true);
        s.monitorNodeLimit(true);
        s.monitorTimeLimit(false);
        s.monitorFailLimit(false);
        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(branchvars)));
        s.minimize(s.getVar(obj), false);
        LOGGER.info("------------------------ " + (s.getVar(obj).getVal() + 1) + " bins");
        if (s.isFeasible() == Boolean.TRUE) {
            for (int j = 0; j <= s.getVar(obj).getVal(); j++) {
                StringBuffer st = new StringBuffer();
                st.append(format("Bin {0}: ", j));
                int load = 0;
                for (int i = 0; i < n; i++) {
                    if (s.getVar(debut[i]).isInstantiatedTo(j)) {
                        st.append(format("{0} ", i));
                        load += instance[i];
                    }
                }
                st.append(format(" - load {0}", load));
                LOGGER.info(st.toString());
            }
        }
    }

    public void binPacking3(int n, int capaBin, int seed) {
        int[] instance = getRandomPackingPb(n, capaBin, seed);
        CPpack pack = new CPpack(instance, capaBin, -1);
        pack.cpPack();
        
    }
    
    public static void main(String[] args) {
    	ExBinPacking tp2 = new ExBinPacking();
        LOGGER.info("************** Modèle Booléen **************");
        tp2.binPacking1(10, 13, 1);
        LOGGER.info("");
        LOGGER.info("************** Modèle Cumulatif ***************");
        tp2.binPacking2(10, 13, 1);
        LOGGER.info("************** Modèle Pack ***************");
        tp2.binPacking3(10, 13, 1);
    }
}