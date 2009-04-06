package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.LargeRelation;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.TuplesTest;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

import java.util.ArrayList;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class NaryRelationTest extends TestCase {

    public void test1() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        pb.post(pb.relationTupleFC(new IntVar[]{x, y, z}, new NotAllEqual()));
        pb.solveAll();
        assertEquals(120, pb.getSolver().getNbSolutions());
    }

    public void test1GAC() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        pb.post(pb.relationTupleAC(new IntVar[]{x, y, z}, new NotAllEqual()));
        pb.solveAll();
        assertEquals(120, pb.getSolver().getNbSolutions());
    }

    public void test2() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        pb.post(pb.relationTupleFC(new IntVar[]{x, y, z}, (LargeRelation) (new NotAllEqual()).getOpposite()));
        pb.solveAll();
        assertEquals(5, pb.getSolver().getNbSolutions());
    }

    public void test2GAC() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        pb.post(pb.relationTupleAC(new IntVar[]{x, y, z}, (LargeRelation) (new NotAllEqual()).getOpposite()));
        pb.solveAll();
        assertEquals(5, pb.getSolver().getNbSolutions());
    }

    private class NotAllEqual extends TuplesTest {

        public boolean checkTuple(int[] tuple) {
            for (int i = 1; i < tuple.length; i++) {
                if (tuple[i - 1] != tuple[i]) return true;
            }
            return false;
        }

    }

    public void test3() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        ArrayList forbiddenTuples = new ArrayList();
        forbiddenTuples.add(new int[]{1, 1, 1});
        forbiddenTuples.add(new int[]{2, 2, 2});
        forbiddenTuples.add(new int[]{3, 3, 3});
        forbiddenTuples.add(new int[]{4, 4, 4});
        forbiddenTuples.add(new int[]{5, 5, 5});
        pb.post(pb.infeasTupleFC(new IntVar[]{x, y, z}, forbiddenTuples));
        pb.solveAll();
        assertEquals(120, pb.getSolver().getNbSolutions());
    }

    public void test3GAC() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        ArrayList forbiddenTuples = new ArrayList();
        forbiddenTuples.add(new int[]{1, 1, 1});
        forbiddenTuples.add(new int[]{2, 2, 2});
        forbiddenTuples.add(new int[]{3, 3, 3});
        forbiddenTuples.add(new int[]{4, 4, 4});
        forbiddenTuples.add(new int[]{5, 5, 5});
        pb.post(pb.infeasTupleAC(new IntVar[]{x, y, z}, forbiddenTuples));
        pb.solveAll();
        assertEquals(120, pb.getSolver().getNbSolutions());
    }


    public void test3bis() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        ArrayList forbiddenTuples = new ArrayList();
        forbiddenTuples.add(new int[]{1, 1, 1});
        forbiddenTuples.add(new int[]{2, 2, 2});
        forbiddenTuples.add(new int[]{2, 5, 3});
        pb.post(pb.infeasTupleFC(new IntVar[]{x, y, z}, forbiddenTuples));
        pb.solveAll();
        assertEquals(122, pb.getSolver().getNbSolutions());
    }

    public void test3bisGAC() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        ArrayList forbiddenTuples = new ArrayList();
        forbiddenTuples.add(new int[]{1, 1, 1});
        forbiddenTuples.add(new int[]{2, 2, 2});
        forbiddenTuples.add(new int[]{2, 5, 3});
        pb.post(pb.infeasTupleAC(new IntVar[]{x, y, z}, forbiddenTuples));
        pb.solveAll();
        assertEquals(122, pb.getSolver().getNbSolutions());
    }

    public void test3bisbis() {
        Problem pb = new Problem();
        IntVar v1 = pb.makeEnumIntVar("v1", 0, 2);
        IntVar v2 = pb.makeEnumIntVar("v2", 0, 4);
        ArrayList feasTuple = new ArrayList();
        feasTuple.add(new int[]{1, 1}); // x*y = 1
        feasTuple.add(new int[]{2, 4}); // x*y = 1
        pb.post(pb.feasTupleFC(new IntVar[]{v1, v2}, feasTuple));
        pb.solve();
        do {
            System.out.println("v1 : " + ((IntDomainVar) v1).getVal() + " v2: " + ((IntDomainVar) v2).getVal());
        } while (pb.nextSolution() == Boolean.TRUE);
        assertEquals(2, pb.getSolver().getNbSolutions());
    }

    public void test3bisbisGAC() {
        Problem pb = new Problem();
        IntVar v1 = pb.makeEnumIntVar("v1", 0, 2);
        IntVar v2 = pb.makeEnumIntVar("v2", 0, 4);
        ArrayList feasTuple = new ArrayList();
        feasTuple.add(new int[]{1, 1}); // x*y = 1
        feasTuple.add(new int[]{2, 4}); // x*y = 1
        pb.post(pb.feasTupleAC(new IntVar[]{v1, v2}, feasTuple));
        pb.solve();
        do {
            System.out.println("v1 : " + ((IntDomainVar) v1).getVal() + " v2: " + ((IntDomainVar) v2).getVal());
        } while (pb.nextSolution() == Boolean.TRUE);
        assertEquals(2, pb.getSolver().getNbSolutions());
    }

    public void test4() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        ArrayList forbiddenTuples = new ArrayList();
        int cpt = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                for (int k = 1; k <= 5; k++) {
                    if (i != j || i != k || k != j) {
                        int[] tuple = new int[3];
                        tuple[0] = i;
                        tuple[1] = j;
                        tuple[2] = k;
                        cpt++;
                        forbiddenTuples.add(tuple);
                    }
                }
            }
        }
        pb.post(pb.infeasTupleFC(new IntVar[]{x, y, z}, forbiddenTuples));
        pb.solveAll();
        assertEquals(5, pb.getSolver().getNbSolutions());
    }

    public void test5() {
        Problem pb = new Problem();
        IntVar x = pb.makeEnumIntVar("x", 1, 5);
        IntVar y = pb.makeEnumIntVar("y", 1, 5);
        IntVar z = pb.makeEnumIntVar("z", 1, 5);
        ArrayList goodTuples = new ArrayList();
        goodTuples.add(new int[]{1, 1, 1});
        goodTuples.add(new int[]{2, 2, 2});
        goodTuples.add(new int[]{3, 3, 3});
        goodTuples.add(new int[]{4, 4, 4});
        goodTuples.add(new int[]{5, 5, 5});
        pb.post(pb.feasTupleFC(new IntVar[]{x, y, z}, goodTuples));
        pb.solveAll();
        assertEquals(5, pb.getSolver().getNbSolutions());
    }

    //
    // un petit probl�me pos� en sixi�me � la petite soeur de Ludivine :
    // trouvez l'�ge de mes trois enfants :
    //     - le produit de leurs ages est �gal � 36
    //     - la somme de leurs ages est �gal � 13
    //     - mon a�n� est blond (info cruciale)
    // ici il est r�solu de mani�re compl�tement bidon (on a d�j� la solution avant
    // de lancer le solve en ayant �limin� tous les tuples infaisables :))
    public void test6() {
        Problem pb = new Problem();
        IntDomainVar x = pb.makeEnumIntVar("x", 1, 12);
        IntDomainVar y = pb.makeEnumIntVar("y", 1, 12);
        IntDomainVar z = pb.makeEnumIntVar("z", 1, 12);
        ArrayList forbiddenTuplesProduct = new ArrayList();
        ArrayList forbiddenTuplesAine = new ArrayList();
        ArrayList symetryTuples = new ArrayList();
        for (int i = 1; i <= 12; i++) {
            for (int j = 1; j <= 12; j++) {
                for (int k = 1; k <= 12; k++) {
                    int[] tuple = new int[3];
                    tuple[0] = i;
                    tuple[1] = j;
                    tuple[2] = k;
                    if (i * j * k != 36)
                        forbiddenTuplesProduct.add(tuple);
                    if (i > j || j > k || i > k)
                        symetryTuples.add(tuple);
                    if ((i == j && i > k) || (i == k && j > k) || (j == k && k > i))
                        forbiddenTuplesAine.add(tuple);
                }
            }
        }

        pb.post(pb.eq(pb.sum(new IntVar[]{x, y, z}), 13));
        pb.post(pb.infeasTupleFC(new IntVar[]{x, y, z}, forbiddenTuplesProduct));
        pb.post(pb.infeasTupleFC(new IntVar[]{x, y, z}, forbiddenTuplesAine));
        pb.post(pb.infeasTupleFC(new IntVar[]{x, y, z}, symetryTuples));
        pb.solveAll();
        System.out.println("x " + x.getVal() + " y " + y.getVal() + " z " + z.getVal());
        assertEquals(1, pb.getSolver().getNbSolutions());
        assertEquals(2, x.getVal());
        assertEquals(2, y.getVal());
        assertEquals(9, z.getVal());
    }

    public static void genereCst(ArrayList tuples, int n) {
        int[] tuple = new int[n];
        int k = 0;
        for (int i = 0; i < n; i++)
            tuple[i] = 1;
        tuple[0] = 0;
        while (k < n) {
            tuple[k]++;
            if (tuple[k] > n) {
                tuple[k] = 1;
                k++;
            } else {
                if (testDouble(tuple)) {
                    int[] t = new int[n];
                    System.arraycopy(tuple, 0, t, 0, tuple.length);
                    tuples.add(t);
                }
                k = 0;
            }
        }
    }


    public static boolean testDouble(int[] tuple) {
        for (int i = 0; i < tuple.length; i++) {
            for (int j = i + 1; j < tuple.length; j++) {
                if (tuple[i] == tuple[j])
                    return true;
            }
        }
        return false;
    }

    public void testFeasibleTupleAC() throws Exception {
        int N = 9;
        Problem p = new Problem();
        java.util.ArrayList<int[]> tuples = new java.util.ArrayList<int[]>();
        tuples.add(new int[]{0, 1, 1, 1, 0, 4, 1, 1, 1, 0});
        IntDomainVar[] v = new IntDomainVar[10];
        for (int n = 0; n < v.length; n++) {
            v[n] = p.makeBoundIntVar("V" + n, 0, N);
        }
        p.post(p.regular(v, tuples));
        Boolean b = p.solve();
        assertEquals(true, b.booleanValue());
        assertEquals(0, v[0].getVal());
        assertEquals(1, v[1].getVal());
        assertEquals(1, v[2].getVal());
        assertEquals(1, v[3].getVal());
        assertEquals(0, v[4].getVal());
        assertEquals(4, v[5].getVal());
        assertEquals(1, v[6].getVal());
        assertEquals(1, v[7].getVal());
        assertEquals(1, v[8].getVal());
        assertEquals(0, v[9].getVal());
    }


    public void test7() {
        Problem pb = new Problem();
        int n = 7;
        IntVar[] reines = new IntVar[n];
        IntVar[] diag1 = new IntVar[n];
        IntVar[] diag2 = new IntVar[n];
        //Definition Variables
        for (int i = 0; i < n; i++) {
            reines[i] = pb.makeEnumIntVar("reine-" + i, 1, n);
        }
        for (int i = 0; i < n; i++) {
            diag1[i] = pb.makeEnumIntVar("diag1-" + i, -n, 2 * n);
            diag2[i] = pb.makeEnumIntVar("diag2-" + i, -n, 2 * n);
        }
        //Tests contraintes N-aires
        ArrayList tuples = new ArrayList();
        genereCst(tuples, n);
        pb.post(pb.infeasTupleFC(reines, tuples));
//       pb.post(pb.infeasTuple(reines, tuples, 2001)); TODO: que voulait dire le parametre 2001 ?

        //Definition Contraintes restantes
        for (int i = 0; i < n; i++) {
            pb.post(pb.eq(diag1[i], pb.plus(reines[i], i)));
            pb.post(pb.eq(diag2[i], pb.minus(reines[i], i)));

            for (int j = i + 1; j < n; j++) {
                // pb.post( pb.neq(reines[i],reines[j]));
                pb.post(pb.neq(diag1[i], diag1[j]));
                pb.post(pb.neq(diag2[i], diag2[j]));
            }
        }
        // Resolution
        pb.getSolver().setValSelector(new RandomIntValSelector(110));
        pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, 110));
        pb.solveAll();
        assertEquals(40, pb.getSolver().getNbSolutions());
    }

    public void test7GAC() {
        Problem pb = new Problem();
        int n = 7;
        IntVar[] reines = new IntVar[n];
        IntVar[] diag1 = new IntVar[n];
        IntVar[] diag2 = new IntVar[n];
        //Definition Variables
        for (int i = 0; i < n; i++) {
            reines[i] = pb.makeEnumIntVar("reine-" + i, 1, n);
        }
        for (int i = 0; i < n; i++) {
            diag1[i] = pb.makeEnumIntVar("diag1-" + i, -n, 2 * n);
            diag2[i] = pb.makeEnumIntVar("diag2-" + i, -n, 2 * n);
        }
        //Tests contraintes N-aires
        ArrayList tuples = new ArrayList();
        genereCst(tuples, n);
        pb.post(pb.infeasTupleAC(reines, tuples));
//       pb.post(pb.infeasTuple(reines, tuples, 2001)); TODO: que voulait dire le parametre 2001 ?

        //Definition Contraintes restantes
        for (int i = 0; i < n; i++) {
            pb.post(pb.eq(diag1[i], pb.plus(reines[i], i)));
            pb.post(pb.eq(diag2[i], pb.minus(reines[i], i)));

            for (int j = i + 1; j < n; j++) {
                // pb.post( pb.neq(reines[i],reines[j]));
                pb.post(pb.neq(diag1[i], diag1[j]));
                pb.post(pb.neq(diag2[i], diag2[j]));
            }
        }
        // Resolution
        pb.getSolver().setValSelector(new RandomIntValSelector(110));
        pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, 110));
        pb.solveAll();
        assertEquals(40, pb.getSolver().getNbSolutions());
    }

    public void testVain() {
        Problem chocoCSP = new Problem();
        IntDomainVar[] vars = new IntDomainVar[5];
        Constraint[] cons = new Constraint[4];
        //-----Construct all variables
        int id = 0;
        vars[id] = chocoCSP.makeEnumIntVar("v" + id++, new int[]{4, 2});
        vars[id] = chocoCSP.makeEnumIntVar("v" + id++, new int[]{5, 1, 4});
        vars[id] = chocoCSP.makeEnumIntVar("v" + id++, new int[]{3, 4, 6});
        vars[id] = chocoCSP.makeEnumIntVar("v" + id++, new int[]{1, 2});
        vars[id] = chocoCSP.makeEnumIntVar("v" + id++, new int[]{7, 8, 6, 1});
        id = 0;
        ArrayList<int[]> tuples;

        //-----Now construct all constraints
        //-----Constraint0
        tuples = new ArrayList<int[]>();
        tuples.add(new int[]{1});
        cons[id] = chocoCSP.makeTupleFC(new IntVar[]{vars[3]}, tuples, true);
        chocoCSP.post(cons[id++]);

        //-----Constraint1
        tuples = new ArrayList<int[]>();
        tuples.add(new int[]{6, 7});
        cons[id] = chocoCSP.makeTupleFC(new IntVar[]{vars[2], vars[4]}, tuples, true);
        chocoCSP.post(cons[id++]);

        //-----Constraint2
        tuples = new ArrayList<int[]>();
        tuples.add(new int[]{3});
        tuples.add(new int[]{4});
        cons[id] = chocoCSP.makeTupleFC(new IntVar[]{vars[2]}, tuples, true);
        chocoCSP.post(cons[id++]);

        //-----Constraint3
        tuples = new ArrayList<int[]>();
        tuples.add(new int[]{7, 1});
        tuples.add(new int[]{8, 1});
        tuples.add(new int[]{6, 5});
        tuples.add(new int[]{1, 5});
        cons[id] = chocoCSP.makeTupleFC(new IntVar[]{vars[4], vars[1]}, tuples, true);
        chocoCSP.post(cons[id++]);

        //-----Now get solutions
        System.out.println("Choco Solutions");
        chocoCSP.solveAll();
        assertTrue(chocoCSP.isFeasible() == Boolean.FALSE);
    }

    public static ArrayList tables4() {
        int[] tuple = new int[5];
        ArrayList tuples = new ArrayList();
        for (int i = 1; i <= 5; i++) {
            tuple[0] = i;
            for (int j = 1; j <= 5; j++) {
                tuple[1] = j;
                for (int k = 1; k <= 5; k++) {
                    tuple[2] = k;
                    for (int l = 1; l <= 5; l++) {
                        tuple[3] = l;
                        for (int m = 1; m <= 5; m++) {
                            tuple[4] = m;
                            if ((i != j) && (i != k) && (i != l) && (j != k) && (j != l) && (k != l) && (m != i) && (m != j) && (m != k) && (m != l)) {
                                int[] tupleToAdd = new int[5];
                                System.arraycopy(tuple, 0, tupleToAdd, 0, 5);
                                tuples.add(tupleToAdd);
                            }
                        }
                    }
                }
            }
        }

        return tuples;
    }

    public void testGACPositive() {
        for (int seed = 0; seed < 10; seed++) {
            Problem pb = new Problem();
            //int n = Integer.parseInt(args[0]);
            int n = 5;
            int sizeDomain = n;
            int nbVar = n;
            IntDomainVar[] reines = new IntDomainVar[nbVar];
            IntDomainVar[] diag1 = new IntDomainVar[n];
            IntDomainVar[] diag2 = new IntDomainVar[n];

            for (int i = 0; i < nbVar; i++) {
                reines[i] = pb.makeEnumIntVar("reine-" + i, 1, sizeDomain);
                diag1[i] = pb.makeEnumIntVar("diag1-" + i, -n, 2 * n);
                diag2[i] = pb.makeEnumIntVar("diag2-" + i, -n, 2 * n);
            }

            for (int i = 0; i < n; i++) {
                pb.post(pb.eq(diag1[i], pb.plus(reines[i], i)));
                pb.post(pb.eq(diag2[i], pb.minus(reines[i], i)));

                for (int j = i + 1; j < n; j++) {
                    //pb.post(pb.neq(reines[i], reines[j]));
                    pb.post(pb.neq(diag1[i], diag1[j]));
                    pb.post(pb.neq(diag2[i], diag2[j]));
                }
            }

            pb.post(pb.feasTupleAC(reines, tables4()));

            pb.getSolver().setValSelector(new RandomIntValSelector(seed + 120));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 3));

            System.out.println("Choco Solutions");
            if (pb.solve() == Boolean.TRUE) {
                do {
                    for (int i = 0; i < pb.getNbIntVars(); i++) {
                        System.out.print(((IntDomainVar) pb.getIntVar(i)) + ", ");
                    }
                    System.out.println();
                } while (pb.nextSolution() == Boolean.TRUE);
            }
            assertEquals(pb.getSolver().getNbSolutions(), 10);
        }
    }


}
