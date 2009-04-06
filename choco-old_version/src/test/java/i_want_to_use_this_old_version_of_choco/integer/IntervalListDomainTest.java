package i_want_to_use_this_old_version_of_choco.integer;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomain;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import junit.framework.TestCase;

public class IntervalListDomainTest extends TestCase {

    Problem pb;
    IntDomainVarImpl v;
    IntDomain d;

    protected void tearDown() throws Exception {
        //System.out.println(v.pretty());
        d=null;
        v=null;
        pb = null;
        super.tearDown();
    }

    protected void setUp() throws Exception {
        super.setUp();
        pb = new Problem();
        v = new IntDomainVarImpl(pb, "v", 3, 0, 100);
        d = v.getDomain();
    }

    public void test1(){
        assertFalse("Domain contains -50", d.contains(-50));
        assertFalse("Domain contains -1", d.contains(-1));
        assertTrue("Domain not contains 0", d.contains(0));
        assertTrue("Domain not contains 1", d.contains(1));
        assertTrue("Domain not contains 50", d.contains(50));
        assertTrue("Domain not contains 99", d.contains(99));
        assertTrue("Domain not contains 100", d.contains(100));
        assertFalse("Domain contains 101", d.contains(101));
        assertFalse("Domain contains 150", d.contains(150));

        assertTrue("Lower bound incorrect", d.getInf()==0);
        assertTrue("Upper bound incorrect", d.getSup()==100);

        assertTrue("Next value -50 incorrect", d.getNextValue(-50) == 0);
        assertTrue("Next value -1 incorrect", d.getNextValue(-1) == 0);
        assertTrue("Next value 0 incorrect", d.getNextValue(0) == 1);
        assertTrue("Next value 1 incorrect", d.getNextValue(1) == 2);
        assertTrue("Next value 50 incorrect", d.getNextValue(50) == 51);
        assertTrue("Next value 99 incorrect", d.getNextValue(99) == 100);
        assertTrue("Next value 100 incorrect", d.getNextValue(100) == Integer.MAX_VALUE);
        assertTrue("Next value 101 incorrect", d.getNextValue(101) == Integer.MAX_VALUE);
        assertTrue("Next value 150 incorrect", d.getNextValue(150) == Integer.MAX_VALUE);

        assertTrue("Previous value -50 incorrect", d.getPrevValue(-50) == Integer.MIN_VALUE);
        assertTrue("Previous value -1 incorrect", d.getPrevValue(-1) == Integer.MIN_VALUE);
        assertTrue("Previous value 0 incorrect", d.getPrevValue(0) == Integer.MIN_VALUE);
        assertTrue("Previous value 1 incorrect", d.getPrevValue(1) == 0);
        assertTrue("Previous value 50 incorrect", d.getPrevValue(50) == 49);
        assertTrue("Previous value 99 incorrect", d.getPrevValue(99) == 98);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(100) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(101) == 100);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(150) == 100);

        assertTrue("Has next value -50 incorrect", d.hasNextValue(-50));
        assertTrue("Has next value -1 incorrect", d.hasNextValue(-1));
        assertTrue("Has next value 0 incorrect", d.hasNextValue(0));
        assertTrue("Has next value 1 incorrect", d.hasNextValue(1));
        assertTrue("Has next value 50 incorrect", d.hasNextValue(50));
        assertTrue("Has next value 99 incorrect", d.hasNextValue(99));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(100));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(101));
        assertFalse("Has next value 150 incorrect", d.hasNextValue(150));

        assertFalse("Has previous value -50 incorrect", d.hasPrevValue(-50));
        assertFalse("Has previous value -1 incorrect", d.hasPrevValue(-1));
        assertFalse("Has previous value 0 incorrect", d.hasPrevValue(0));
        assertTrue("Has previous value 1 incorrect", d.hasPrevValue(1));
        assertTrue("Has previous value 50 incorrect", d.hasPrevValue(50));
        assertTrue("Has previous value 99 incorrect", d.hasPrevValue(99));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(100));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(101));
        assertTrue("Has previous value 150 incorrect", d.hasPrevValue(150));

        assertTrue("Random value incorrect", (d.getRandomValue()>=0 && d.getRandomValue()<=100));
        assertTrue("Size incorrect", d.getSize()==101);

    }

     public void test2(){
        assertTrue("Remove 0 incorrect",d.remove(0));
        assertFalse("Domain contains -50", d.contains(-50));
        assertFalse("Domain contains -1", d.contains(-1));
        assertFalse("Domain contains 0", d.contains(0));
        assertTrue("Domain not contains 1", d.contains(1));
        assertTrue("Domain not contains 50", d.contains(50));
        assertTrue("Domain not contains 99", d.contains(99));
        assertTrue("Domain not contains 100", d.contains(100));
        assertFalse("Domain contains 101", d.contains(101));
        assertFalse("Domain contains 150", d.contains(150));

        assertFalse("Lower bound incorrect (0)", d.getInf()==0);
        assertTrue("Lower bound incorrect (1)", d.getInf()==1);
        assertTrue("Upper bound incorrect", d.getSup()==100);

        assertTrue("Next value -50 incorrect", d.getNextValue(-50) == 1);
        assertTrue("Next value -1 incorrect", d.getNextValue(-1) == 1);
        assertTrue("Next value 0 incorrect", d.getNextValue(0) == 1);
        assertTrue("Next value 1 incorrect", d.getNextValue(1) == 2);
        assertTrue("Next value 50 incorrect", d.getNextValue(50) == 51);
        assertTrue("Next value 99 incorrect", d.getNextValue(99) == 100);
        assertTrue("Next value 100 incorrect", d.getNextValue(100) == Integer.MAX_VALUE);
        assertTrue("Next value 101 incorrect", d.getNextValue(101) == Integer.MAX_VALUE);
        assertTrue("Next value 150 incorrect", d.getNextValue(150) == Integer.MAX_VALUE);

        assertTrue("Previous value -50 incorrect", d.getPrevValue(-50) == Integer.MIN_VALUE);
        assertTrue("Previous value -1 incorrect", d.getPrevValue(-1) == Integer.MIN_VALUE);
        assertTrue("Previous value 0 incorrect", d.getPrevValue(0) == Integer.MIN_VALUE);
        assertTrue("Previous value 1 incorrect", d.getPrevValue(1) == Integer.MIN_VALUE);
        assertTrue("Previous value 50 incorrect", d.getPrevValue(50) == 49);
        assertTrue("Previous value 99 incorrect", d.getPrevValue(99) == 98);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(100) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(101) == 100);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(150) == 100);

        assertTrue("Has next value -50 incorrect", d.hasNextValue(-50));
        assertTrue("Has next value -1 incorrect", d.hasNextValue(-1));
        assertTrue("Has next value 0 incorrect", d.hasNextValue(0));
        assertTrue("Has next value 1 incorrect", d.hasNextValue(1));
        assertTrue("Has next value 50 incorrect", d.hasNextValue(50));
        assertTrue("Has next value 99 incorrect", d.hasNextValue(99));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(100));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(101));
        assertFalse("Has next value 150 incorrect", d.hasNextValue(150));

        assertFalse("Has previous value -50 incorrect", d.hasPrevValue(-50));
        assertFalse("Has previous value -1 incorrect", d.hasPrevValue(-1));
        assertFalse("Has previous value 0 incorrect", d.hasPrevValue(0));
        assertFalse("Has previous value 1 incorrect", d.hasPrevValue(1));
        assertTrue("Has previous value 50 incorrect", d.hasPrevValue(50));
        assertTrue("Has previous value 99 incorrect", d.hasPrevValue(99));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(100));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(101));
        assertTrue("Has previous value 150 incorrect", d.hasPrevValue(150));

        assertTrue("Random value incorrect", (d.getRandomValue()>=1 && d.getRandomValue()<=100));
        assertTrue("Size incorrect", d.getSize()==100);

    }

    public void test3(){
        assertTrue("Remove 100 incorrect",d.remove(100));
        assertFalse("Domain contains -50", d.contains(-50));
        assertFalse("Domain contains -1", d.contains(-1));
        assertTrue("Domain not contains 0", d.contains(0));
        assertTrue("Domain not contains 1", d.contains(1));
        assertTrue("Domain not contains 50", d.contains(50));
        assertTrue("Domain not contains 99", d.contains(99));
        assertFalse("Domain contains 100", d.contains(100));
        assertFalse("Domain contains 101", d.contains(101));
        assertFalse("Domain contains 150", d.contains(150));

        assertTrue("Lower bound incorrect", d.getInf()==0);
        assertTrue("Upper bound incorrect", d.getSup()==99);

        assertTrue("Next value -50 incorrect", d.getNextValue(-50) == 0);
        assertTrue("Next value -1 incorrect", d.getNextValue(-1) == 0);
        assertTrue("Next value 0 incorrect", d.getNextValue(0) == 1);
        assertTrue("Next value 1 incorrect", d.getNextValue(1) == 2);
        assertTrue("Next value 50 incorrect", d.getNextValue(50) == 51);
        assertTrue("Next value 99 incorrect", d.getNextValue(99) == Integer.MAX_VALUE);
        assertTrue("Next value 100 incorrect", d.getNextValue(100) == Integer.MAX_VALUE);
        assertTrue("Next value 101 incorrect", d.getNextValue(101) == Integer.MAX_VALUE);
        assertTrue("Next value 150 incorrect", d.getNextValue(150) == Integer.MAX_VALUE);

        assertTrue("Previous value -50 incorrect", d.getPrevValue(-50) == Integer.MIN_VALUE);
        assertTrue("Previous value -1 incorrect", d.getPrevValue(-1) == Integer.MIN_VALUE);
        assertTrue("Previous value 0 incorrect", d.getPrevValue(0) == Integer.MIN_VALUE);
        assertTrue("Previous value 1 incorrect", d.getPrevValue(1) == 0);
        assertTrue("Previous value 50 incorrect", d.getPrevValue(50) == 49);
        assertTrue("Previous value 99 incorrect", d.getPrevValue(99) == 98);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(100) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(101) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(150) == 99);

        assertTrue("Has next value -50 incorrect", d.hasNextValue(-50));
        assertTrue("Has next value -1 incorrect", d.hasNextValue(-1));
        assertTrue("Has next value 0 incorrect", d.hasNextValue(0));
        assertTrue("Has next value 1 incorrect", d.hasNextValue(1));
        assertTrue("Has next value 50 incorrect", d.hasNextValue(50));
        assertFalse("Has next value 99 incorrect", d.hasNextValue(99));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(100));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(101));
        assertFalse("Has next value 150 incorrect", d.hasNextValue(150));

        assertFalse("Has previous value -50 incorrect", d.hasPrevValue(-50));
        assertFalse("Has previous value -1 incorrect", d.hasPrevValue(-1));
        assertFalse("Has previous value 0 incorrect", d.hasPrevValue(0));
        assertTrue("Has previous value 1 incorrect", d.hasPrevValue(1));
        assertTrue("Has previous value 50 incorrect", d.hasPrevValue(50));
        assertTrue("Has previous value 99 incorrect", d.hasPrevValue(99));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(100));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(101));
        assertTrue("Has previous value 150 incorrect", d.hasPrevValue(150));

        assertTrue("Random value incorrect", (d.getRandomValue()>=0 && d.getRandomValue()<=99));
        assertTrue("Size incorrect", d.getSize()==100);

    }

    public void test4(){
        assertTrue("Remove 50 incorrect",d.remove(50));
        assertFalse("Domain contains -50", d.contains(-50));
        assertFalse("Domain contains -1", d.contains(-1));
        assertTrue("Domain not contains 0", d.contains(0));
        assertTrue("Domain not contains 1", d.contains(1));
        assertFalse("Domain contains 50", d.contains(50));
        assertTrue("Domain not contains 99", d.contains(99));
        assertTrue("Domain not contains 100", d.contains(100));
        assertFalse("Domain contains 101", d.contains(101));
        assertFalse("Domain contains 150", d.contains(150));

        assertTrue("Lower bound incorrect", d.getInf()==0);
        assertTrue("Upper bound incorrect", d.getSup()==100);

        assertTrue("Next value -50 incorrect", d.getNextValue(-50) == 0);
        assertTrue("Next value -1 incorrect", d.getNextValue(-1) == 0);
        assertTrue("Next value 0 incorrect", d.getNextValue(0) == 1);
        assertTrue("Next value 1 incorrect", d.getNextValue(1) == 2);
        assertTrue("Next value 49 incorrect", d.getNextValue(49) == 51);
        assertTrue("Next value 50 incorrect", d.getNextValue(50) == 51);
        assertTrue("Next value 99 incorrect", d.getNextValue(99) == 100);
        assertTrue("Next value 100 incorrect", d.getNextValue(100) == Integer.MAX_VALUE);
        assertTrue("Next value 101 incorrect", d.getNextValue(101) == Integer.MAX_VALUE);
        assertTrue("Next value 150 incorrect", d.getNextValue(150) == Integer.MAX_VALUE);

        assertTrue("Previous value -50 incorrect", d.getPrevValue(-50) == Integer.MIN_VALUE);
        assertTrue("Previous value -1 incorrect", d.getPrevValue(-1) == Integer.MIN_VALUE);
        assertTrue("Previous value 0 incorrect", d.getPrevValue(0) == Integer.MIN_VALUE);
        assertTrue("Previous value 1 incorrect", d.getPrevValue(1) == 0);
        assertTrue("Previous value 50 incorrect", d.getPrevValue(50) == 49);
        assertTrue("Previous value 51 incorrect", d.getPrevValue(51) == 49);
        assertTrue("Previous value 99 incorrect", d.getPrevValue(99) == 98);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(100) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(101) == 100);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(150) == 100);

        assertTrue("Has next value -50 incorrect", d.hasNextValue(-50));
        assertTrue("Has next value -1 incorrect", d.hasNextValue(-1));
        assertTrue("Has next value 0 incorrect", d.hasNextValue(0));
        assertTrue("Has next value 1 incorrect", d.hasNextValue(1));
        assertTrue("Has next value 49 incorrect", d.hasNextValue(49));
        assertTrue("Has next value 50 incorrect", d.hasNextValue(50));
        assertTrue("Has next value 99 incorrect", d.hasNextValue(99));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(100));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(101));
        assertFalse("Has next value 150 incorrect", d.hasNextValue(150));

        assertFalse("Has previous value -50 incorrect", d.hasPrevValue(-50));
        assertFalse("Has previous value -1 incorrect", d.hasPrevValue(-1));
        assertFalse("Has previous value 0 incorrect", d.hasPrevValue(0));
        assertTrue("Has previous value 1 incorrect", d.hasPrevValue(1));
        assertTrue("Has previous value 50 incorrect", d.hasPrevValue(50));
        assertTrue("Has previous value 51 incorrect", d.hasPrevValue(51));
        assertTrue("Has previous value 99 incorrect", d.hasPrevValue(99));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(100));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(101));
        assertTrue("Has previous value 150 incorrect", d.hasPrevValue(150));

        int k = d.getRandomValue();
        assertTrue("Random value incorrect", (k>=0 && k<=100 && k!=50));
        System.out.printf("size:"+d.getSize());
        assertTrue("Size incorrect", d.getSize()==100);
    }

    public void test5(){
        assertTrue("Update 1 incorrect",d.updateInf(1)==1);
        assertFalse("Domain contains -50", d.contains(-50));
        assertFalse("Domain contains -1", d.contains(-1));
        assertFalse("Domain contains 0", d.contains(0));
        assertTrue("Domain not contains 1", d.contains(1));
        assertTrue("Domain not contains 50", d.contains(50));
        assertTrue("Domain not contains 99", d.contains(99));
        assertTrue("Domain not contains 100", d.contains(100));
        assertFalse("Domain contains 101", d.contains(101));
        assertFalse("Domain contains 150", d.contains(150));

        assertFalse("Lower bound incorrect (0)", d.getInf()==0);
        assertTrue("Lower bound incorrect (1)", d.getInf()==1);
        assertTrue("Upper bound incorrect", d.getSup()==100);

        assertTrue("Next value -50 incorrect", d.getNextValue(-50) == 1);
        assertTrue("Next value -1 incorrect", d.getNextValue(-1) == 1);
        assertTrue("Next value 0 incorrect", d.getNextValue(0) == 1);
        assertTrue("Next value 1 incorrect", d.getNextValue(1) == 2);
        assertTrue("Next value 50 incorrect", d.getNextValue(50) == 51);
        assertTrue("Next value 99 incorrect", d.getNextValue(99) == 100);
        assertTrue("Next value 100 incorrect", d.getNextValue(100) == Integer.MAX_VALUE);
        assertTrue("Next value 101 incorrect", d.getNextValue(101) == Integer.MAX_VALUE);
        assertTrue("Next value 150 incorrect", d.getNextValue(150) == Integer.MAX_VALUE);

        assertTrue("Previous value -50 incorrect", d.getPrevValue(-50) == Integer.MIN_VALUE);
        assertTrue("Previous value -1 incorrect", d.getPrevValue(-1) == Integer.MIN_VALUE);
        assertTrue("Previous value 0 incorrect", d.getPrevValue(0) == Integer.MIN_VALUE);
        assertTrue("Previous value 1 incorrect", d.getPrevValue(1) == Integer.MIN_VALUE);
        assertTrue("Previous value 50 incorrect", d.getPrevValue(50) == 49);
        assertTrue("Previous value 99 incorrect", d.getPrevValue(99) == 98);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(100) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(101) == 100);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(150) == 100);

        assertTrue("Has next value -50 incorrect", d.hasNextValue(-50));
        assertTrue("Has next value -1 incorrect", d.hasNextValue(-1));
        assertTrue("Has next value 0 incorrect", d.hasNextValue(0));
        assertTrue("Has next value 1 incorrect", d.hasNextValue(1));
        assertTrue("Has next value 50 incorrect", d.hasNextValue(50));
        assertTrue("Has next value 99 incorrect", d.hasNextValue(99));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(100));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(101));
        assertFalse("Has next value 150 incorrect", d.hasNextValue(150));

        assertFalse("Has previous value -50 incorrect", d.hasPrevValue(-50));
        assertFalse("Has previous value -1 incorrect", d.hasPrevValue(-1));
        assertFalse("Has previous value 0 incorrect", d.hasPrevValue(0));
        assertFalse("Has previous value 1 incorrect", d.hasPrevValue(1));
        assertTrue("Has previous value 50 incorrect", d.hasPrevValue(50));
        assertTrue("Has previous value 99 incorrect", d.hasPrevValue(99));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(100));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(101));
        assertTrue("Has previous value 150 incorrect", d.hasPrevValue(150));

        assertTrue("Random value incorrect", (d.getRandomValue()>=1 && d.getRandomValue()<=100));
        assertTrue("Size incorrect", d.getSize()==100);

    }

     public void test6(){
        assertTrue("Remove 99 incorrect",d.updateSup(99)==99);
        assertFalse("Domain contains -50", d.contains(-50));
        assertFalse("Domain contains -1", d.contains(-1));
        assertTrue("Domain not contains 0", d.contains(0));
        assertTrue("Domain not contains 1", d.contains(1));
        assertTrue("Domain not contains 50", d.contains(50));
        assertTrue("Domain not contains 99", d.contains(99));
        assertFalse("Domain contains 100", d.contains(100));
        assertFalse("Domain contains 101", d.contains(101));
        assertFalse("Domain contains 150", d.contains(150));

        assertTrue("Lower bound incorrect", d.getInf()==0);
        assertTrue("Upper bound incorrect", d.getSup()==99);

        assertTrue("Next value -50 incorrect", d.getNextValue(-50) == 0);
        assertTrue("Next value -1 incorrect", d.getNextValue(-1) == 0);
        assertTrue("Next value 0 incorrect", d.getNextValue(0) == 1);
        assertTrue("Next value 1 incorrect", d.getNextValue(1) == 2);
        assertTrue("Next value 50 incorrect", d.getNextValue(50) == 51);
        assertTrue("Next value 99 incorrect", d.getNextValue(99) == Integer.MAX_VALUE);
        assertTrue("Next value 100 incorrect", d.getNextValue(100) == Integer.MAX_VALUE);
        assertTrue("Next value 101 incorrect", d.getNextValue(101) == Integer.MAX_VALUE);
        assertTrue("Next value 150 incorrect", d.getNextValue(150) == Integer.MAX_VALUE);

        assertTrue("Previous value -50 incorrect", d.getPrevValue(-50) == Integer.MIN_VALUE);
        assertTrue("Previous value -1 incorrect", d.getPrevValue(-1) == Integer.MIN_VALUE);
        assertTrue("Previous value 0 incorrect", d.getPrevValue(0) == Integer.MIN_VALUE);
        assertTrue("Previous value 1 incorrect", d.getPrevValue(1) == 0);
        assertTrue("Previous value 50 incorrect", d.getPrevValue(50) == 49);
        assertTrue("Previous value 99 incorrect", d.getPrevValue(99) == 98);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(100) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(101) == 99);
        assertTrue("Previous value 100 incorrect", d.getPrevValue(150) == 99);

        assertTrue("Has next value -50 incorrect", d.hasNextValue(-50));
        assertTrue("Has next value -1 incorrect", d.hasNextValue(-1));
        assertTrue("Has next value 0 incorrect", d.hasNextValue(0));
        assertTrue("Has next value 1 incorrect", d.hasNextValue(1));
        assertTrue("Has next value 50 incorrect", d.hasNextValue(50));
        assertFalse("Has next value 99 incorrect", d.hasNextValue(99));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(100));
        assertFalse("Has next value 100 incorrect", d.hasNextValue(101));
        assertFalse("Has next value 150 incorrect", d.hasNextValue(150));

        assertFalse("Has previous value -50 incorrect", d.hasPrevValue(-50));
        assertFalse("Has previous value -1 incorrect", d.hasPrevValue(-1));
        assertFalse("Has previous value 0 incorrect", d.hasPrevValue(0));
        assertTrue("Has previous value 1 incorrect", d.hasPrevValue(1));
        assertTrue("Has previous value 50 incorrect", d.hasPrevValue(50));
        assertTrue("Has previous value 99 incorrect", d.hasPrevValue(99));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(100));
        assertTrue("Has previous value 100 incorrect", d.hasPrevValue(101));
        assertTrue("Has previous value 150 incorrect", d.hasPrevValue(150));

        assertTrue("Random value incorrect", (d.getRandomValue()>=0 && d.getRandomValue()<=99));
        assertTrue("Size incorrect", d.getSize()==100);
    }

    public void test7(){
        int n= 1000000;
        v = new IntDomainVarImpl(pb, "v", 3, 0, n);
        d = v.getDomain();
        for(int i = 0; i < n; i=i+2){
            d.remove(i);
            assertFalse("valeur "+i+" non retirée", d.contains(i));
        }
        assertEquals("Domaine non réduit à n/2 valeurs", d.getSize(), (n/2)+1);
    }

    public void test8(){
        int n= 1000000;
        v = new IntDomainVarImpl(pb, "v", 3, 0, n);
        d = v.getDomain();
        for(int j = 0; j < n; j++){
            int i = d.getRandomValue();                                                                      
            d.remove(i);
            assertFalse("valeur "+i+" non retirée", d.contains(i));
            //System.out.println(i);
        }
        System.out.printf(v.pretty());
        assertTrue("Domaine non réduit à 1 valeur", d.getSize()==1);

    }
}