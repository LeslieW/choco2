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
package choco.memory;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.PartiallyStoredVector;
import static choco.kernel.memory.PartiallyStoredVector.STORED_OFFSET;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.logging.Logger;

public class PartiallyStoredVectorTest {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private IEnvironment env;
  private PartiallyStoredVector vector;

    @Before
  public void setUp() {
    logger.fine("StoredIntVector Testing...");

    env = new EnvironmentTrailing();
    vector = env.makePartiallyStoredVector();
  }

    @After
  public void tearDown() {
    vector = null;
    env = null;
  }

  /**
   * testing the empty constructor with a few backtracks, additions, and updates
   */
  @Test
  public void test1() {
    logger.finer("test1");
    assertEquals(0, env.getWorldIndex());
    assertTrue(vector.isEmpty());
    env.worldPush();
    assertEquals(1, env.getWorldIndex());
    vector.add(new Integer(0));
    vector.add(new Integer(1));
    env.worldPush();
    assertEquals(2, env.getWorldIndex());
    vector.add(new Integer(2));
    vector.add(new Integer(3));
    vector.staticAdd(new Integer(4));
    assertTrue(vector.size() == 5);
    env.worldPop();
    assertTrue(vector.size() == 3);
    assertEquals(1, env.getWorldIndex());
    env.worldPop();
    assertTrue(vector.size() == 1);
    assertEquals(0, env.getWorldIndex());
  }

    static int A=0, B=1, C=2, D=3;
    @Test
    @Ignore
    public void test2() {
        int[] types = {A, B, C, D};
        int n = 10;
        Random r = new Random(0);

        IStateInt b = env.makeInt(0);
        IStateInt c = env.makeInt(0);
        IStateInt d = env.makeInt(0);
        int bs = 0, cs = 0, ds = 0;

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            int obj = types[r.nextInt(types.length-1)];
            int stat = 0;//r.nextInt(2);

            if ((obj == A)) {
                if ((stat == 0)) {
                    vector.insert(b.get(), obj);
                    b.add(1);
                    c.add(1);
                    d.add(1);
                } else {
                    vector.staticInsert(bs, obj);
                    bs++;
                    cs++;
                    ds++;
                }
            } else if ((obj == B)) {
                if ((stat == 0)) {
                    vector.insert(c.get(), obj);
                    c.add(1);
                    d.add(1);
                } else {
                    vector.staticInsert(cs, obj);
                    cs++;
                    ds++;
                }
            } else if ((obj == C)) {
                if ((stat == 0)) {
                    vector.insert(d.get(), obj);
                    d.add(1);
                } else {
                    vector.staticInsert(ds, obj);
                    ds++;
                }
            } else if ((obj == D)) {
                if ((stat == 0)) {
                    vector.add(obj);
                } else {
                    vector.staticAdd(obj);
                }
            }
            env.worldPush();
        }
        long t2 = System.currentTimeMillis();
        for(int i= 0; i < bs; i++){
            Assert.assertEquals(A, vector.get(i));
        }
        for(int i= bs; i < cs; i++){
            Assert.assertEquals(B, vector.get(i));
        }
        for(int i= cs; i < ds; i++){
            Assert.assertEquals(C, vector.get(i));
        }
        for(int i= ds; i < vector.getLastStaticIndex(); i++){
            Assert.assertEquals(D, vector.get(i));
        }
        long t3 = System.currentTimeMillis();
        for(int j = 0; j < n; j++){
            env.worldPop();
            for(int i= 0; i < b.get(); i++){
            Assert.assertEquals(A, vector.get(i+STORED_OFFSET));
            }
            for(int i= b.get(); i < c.get(); i++){
                Assert.assertEquals(B, vector.get(i+STORED_OFFSET));
            }
            for(int i= c.get(); i < d.get(); i++){
                Assert.assertEquals(C, vector.get(i+STORED_OFFSET));
            }
            for(int i= d.get(); i < vector.getLastStoredIndex()+1; i++){
                Assert.assertEquals(D, vector.get(i+STORED_OFFSET));
            }
        }
        long t4 = System.currentTimeMillis();
        System.out.println("t1:"+t1);
        System.out.println("t2:"+t2);
        System.out.println("t3:"+t3);
        System.out.println("t4:"+t4);
    }

}
