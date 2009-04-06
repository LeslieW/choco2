package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 24-Jan-2007
 * Time: 19:31:59
 * To change this template use File | Settings | File Templates.
 */
public class NValueTest extends TestCase {


    public void testSolve1() {
        for (int k = 0; k < 10; k++) {
            AbstractProblem pb = new Problem();
            int n = 5;
            IntDomainVar[] vars = pb.makeEnumIntVarArray("v", n, 1, 3);
            IntDomainVar v = pb.makeEnumIntVar("nvalue", 2, 2);
            pb.post(pb.atMostNValue(vars, v));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, k));
            pb.getSolver().setValSelector(new RandomIntValSelector(k + 1));
            pb.solve();
            if (pb.isFeasible()) {
                do {
                    //for (int i = 0; i < n; i++) {
                    //    System.out.print("" + vars[i].getVal());
                    //}
                    //System.out.println("");
                } while (pb.nextSolution() == Boolean.TRUE);
            }
            System.out.println("noeud : " + pb.getSolver().getSearchSolver().getNodeCount());
            System.out.println("temps : " + pb.getSolver().getSearchSolver().getTimeCount());
            assertEquals(pb.getSolver().getNbSolutions(),93);
        }
    }

    public void testSolve2() {
        for (int k = 0; k < 10; k++) {
            AbstractProblem pb = new Problem();
            int n = 5;
            IntDomainVar[] vars = pb.makeBoundIntVarArray("v", n, 1, 3);
            IntDomainVar v = pb.makeBoundIntVar("nvalue", 2, 2);
            pb.post(pb.atMostNValue(vars, v));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, k));
            pb.getSolver().setValSelector(new RandomIntValSelector(k + 1));
            pb.solve();
            if (pb.isFeasible()) {
                do {
                    //for (int i = 0; i < n; i++) {
                    //    System.out.print("" + vars[i].getVal());
                    //}
                    //System.out.println("");
                } while (pb.nextSolution() == Boolean.TRUE);
            }
            System.out.println("noeud : " + pb.getSolver().getSearchSolver().getNodeCount());
            System.out.println("temps : " + pb.getSolver().getSearchSolver().getTimeCount());
            assertEquals(pb.getSolver().getNbSolutions(),93);
        }

    }

    /**
     * Domination de graphes de reines. Peux t on "dominer" un echiquier
     * de taille n*n par val reines (toutes les cases sont attaquees).
     * @param n
     * @param val
     * @return la liste des positions des reines.
     */
    public List dominationQueen(int n, int val) {
        System.out.println("domination queen Q" + n + ":" + val);
        AbstractProblem pb = new Problem();
        IntDomainVar[] vars = new IntDomainVar[n * n];
        //une variable par case avec pour domaine la reine qui l attaque. (les reines
        //sont ainsi designees par les valeurs, et les cases par les variables)
        for (int i = 0; i < vars.length; i++) {
            vars[i] =  pb.makeIntVar("v",IntDomainVar.LINKEDLIST, 1, n * n);            
        }
        IntDomainVar v = pb.makeEnumIntVar("nvalue", val, val);

        //i appartient a la variable j ssi la case i est sur une ligne/colonne/diagonale de j
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                //pour chaque case
                for (int k = 1; k <= n; k++) {
                    for (int l = 1; l <= n; l++) {
                        if (!(k == i || l == j || Math.abs(i - k) == Math.abs(j - l))) {
                            try {
                                //System.out.println("remove from " + (n * (i - 1) + j - 1) + " value " + ((k - 1) * n + l));
                                vars[n * (i - 1) + j - 1].remVal((k - 1) * n + l);
                            } catch (ContradictionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        // une seule contrainte
        pb.post(pb.atMostNValue(vars, v));
        //pb.getSolver().setTimeLimit(30000);
        pb.solve();
        System.out.println("noeud : " + pb.getSolver().getSearchSolver().getNodeCount());
        System.out.println("temps : " + pb.getSolver().getSearchSolver().getTimeCount());
        List values = new LinkedList();
        if (pb.isFeasible()) {
        for (int i = 0; i < n*n; i++) {
            if (!values.contains(vars[i].getVal()))
                values.add(vars[i].getVal());
        }
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            System.out.println("" + iterator.next());
        }
        } else System.out.println("pas de solution");
        return values;
    }

    public void testDomination1() {
        assertEquals(dominationQueen(6,3).size(),3);
    }

    public void testDomination2() {
        assertEquals(dominationQueen(7,4).size(),4);
    }

    public void testDomination3() {
        assertEquals(dominationQueen(8,5).size(),5);
    }

    //todo : currentElement trop long, voir perf de l intersection de domaines...
    //public void testDomination4() {
    //    assertEquals(dominationQueen(9,5).size(),0);
    //}

    public void testIsSatisfied() {
        AbstractProblem pb = new Problem();
        IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 1);
        IntDomainVar v2 = pb.makeEnumIntVar("v2", 2, 2);
        IntDomainVar v3 = pb.makeEnumIntVar("v3", 3, 3);
        IntDomainVar v4 = pb.makeEnumIntVar("v4", 4, 4);
        IntDomainVar n = pb.makeEnumIntVar("n", 3, 3);
        Constraint c1 = pb.atMostNValue(new IntDomainVar[]{v1, v2, v3}, n);
        Constraint c2 = pb.atMostNValue(new IntDomainVar[]{v1, v2, v3, v4}, n);
      System.out.println(c1.pretty());
        assertTrue(c1.isSatisfied());
        assertFalse(c2.isSatisfied());
    }

}
