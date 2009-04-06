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
package samples.seminar.socialgolfer;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.StaticVarOrder;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 2 juin 2008
 * Since : Choco 2.0.0
 *
 */
public class ExSocialGolferv1 {

    public int w; // number of weeks
    public int g; // number of groups
    public int s; // size of the groups

    public ExSocialGolferv1(int w, int g, int s) {
        this.w = w;
        this.g = g;
        this.s = s;
    }

    public int[] getOneMatrix(int n) {
        int[] mat = new int[n];
        for (int i = 0; i < n; i++) {
            mat[i] = 1;
        }
        return mat;
    }

    public void booleanSocialGofler() {
        Problem pb = new Problem();

        int numplayers = g * s;
        IntDomainVar[][][] golfmat = new IntDomainVar[g][w][numplayers];
        // golfmat[i][j][k] : est ce que le joueur numéro k joue semaine j dans le groupe i ?
        for (int i = 0; i < g; i++) {
            for (int j = 0; j < w; j++) {
                for (int k = 0; k < numplayers; k++) {
                    golfmat[i][j][k] = pb.makeEnumIntVar("(" + i + "_" + j + "_" + k + ")", 0, 1);
                }
            }
        }
        //every golfer plays once in every week
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < numplayers; j++) {
                IntDomainVar[] vars = new IntDomainVar[g];
                for (int k = 0; k < g; k++) {
                    vars[k] = golfmat[k][i][j];
                }
                pb.post(pb.eq(pb.scalar(vars, getOneMatrix(g)), 1)); // tout golfer doit être placé dans un groupe
            }
        }

        //every group is of size s
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < g; j++) {
                IntDomainVar[] vars = new IntDomainVar[numplayers];
                System.arraycopy(golfmat[j][i], 0, vars, 0, numplayers);
                pb.post(pb.eq(pb.scalar(vars, getOneMatrix(numplayers)), s));
            }
        }

        // each pair of players only meet once
        // Efficient way : use of a ScalarAtMost
        for (int i = 0; i < numplayers; i++) {
            for (int j = i + 1; j < numplayers; j++) {
                IntDomainVar[] vars = new IntDomainVar[w * g * 2];
                int cpt = 0;
                for (int k = 0; k < w; k++) {
                    for (int l = 0; l < g; l++) {
                        vars[cpt] = golfmat[l][k][i];
                        vars[cpt + w * g] = golfmat[l][k][j];
                        cpt++;
                    } 
                }
                pb.post(new ScalarAtMostv1(vars, w * g, 1));
            }
        }

        // break symetries among weeks
        // enforce a lexicgraphic ordering between any pairs of week
        for (int i = 0; i < w; i++) {
            for (int j = i + 1; j < w; j++) {
                IntDomainVar[] vars1 = new IntDomainVar[numplayers * g];
                IntDomainVar[] vars2 = new IntDomainVar[numplayers * g];
                int cpt = 0;
                for (int k = 0; k < numplayers; k++) {
                    for (int l = 0; l < g; l++) {
                        vars1[cpt] = golfmat[l][i][k];
                        vars2[cpt] = golfmat[l][j][k];
                        cpt++;
                    }
                }
                pb.post(pb.lex(vars1, vars2));
            }
        }

        // break symetries among groups
        for (int i = 0; i < numplayers; i++) {
            for (int j = i + 1; j < numplayers; j++) {
                IntDomainVar[] vars1 = new IntDomainVar[w * g];
                IntDomainVar[] vars2 = new IntDomainVar[w * g];
                int cpt = 0;
                for (int k = 0; k < w; k++) {
                    for (int l = 0; l < g; l++) {
                        vars1[cpt] = golfmat[l][k][i];
                        vars2[cpt] = golfmat[l][k][j];
                        cpt++;
                    }
                }
                pb.post(pb.lex(vars1, vars2));
            }
        }

        // break symetries among players
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < g; j++) {
                for (int p = j + 1; p < g; p++) {
                    IntDomainVar[] vars1 = new IntDomainVar[numplayers];
                    IntDomainVar[] vars2 = new IntDomainVar[numplayers];
                    int cpt = 0;
                    for (int k = 0; k < numplayers; k++) {
                        vars1[cpt] = golfmat[j][i][k];
                        vars2[cpt] = golfmat[p][i][k];
                        cpt++;
                    }
                    pb.post(pb.lex(vars1, vars2));
                }
            }
        }

        // gather branching variables
        IntDomainVar[] staticvars = new IntDomainVar[g * w * numplayers];
        int cpt = 0;
        for (int i = 0; i < numplayers; i++) {
            for (int j = 0; j < w; j++) {
                for (int k = 0; k < g; k++) {
                    staticvars[cpt] = golfmat[k][j][i];
                    cpt++;
                }
            }
        }
        pb.getSolver().setVarIntSelector(new StaticVarOrder(staticvars));

        pb.getSolver().setTimeLimit(120000);

        Solver.setVerbosity(Solver.SOLUTION);
        pb.solve();
        Solver.flushLogs();

        if (pb.isFeasible() == Boolean.TRUE) printSol(golfmat);
    }

    public void printSol(IntDomainVar[][][] gvars) {
        for (int i = 0; i < w; i++) {
            String semi = "";
            for (int j = 0; j < g; j++) {
                String gj = "(-";
                for (int k = 0; k < g * s; k++) {
                    if (gvars[j][i][k].isInstantiatedTo(1)) gj += k + "-";
                }
                semi += gj + ") ";
            }
            System.out.println("" + semi);
        }
    }

    public static void main(String[] args) {
        ExSocialGolferv1 e = new ExSocialGolferv1(11, 6, 2);
        e.booleanSocialGofler();
    }
}
