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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.PartiallyStoredIntVector;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

public class PartiallyStoredIntVectorTest {
  
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();
	
  private IEnvironment env;
  private PartiallyStoredIntVector vector;

    @Before
  public void setUp() {
    LOGGER.fine("StoredIntVector Testing...");

    env = new EnvironmentTrailing();
    vector = env.makePartiallyStoredIntVector();
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
    LOGGER.finer("test1");
    assertEquals(0, env.getWorldIndex());
    assertTrue(vector.isEmpty());
    env.worldPush();
    assertEquals(1, env.getWorldIndex());
    vector.add(0);
    vector.add(1);
    env.worldPush();
    assertEquals(2, env.getWorldIndex());
    vector.add(2);
    vector.add(3);
    vector.staticAdd(4);
    assertTrue(vector.size() == 5);
    int[] values = new int[]{4, 0, 1, 2, 3};
    int nValue = 0;
    for (IntIterator it = vector.getIndexIterator(); it.hasNext();) {
      int index = it.next();
      int value = vector.get(index);
      assertEquals(values[nValue], value);
      nValue++;
    }
    env.worldPop();
    assertTrue(vector.size() == 3);
    values = new int[]{4, 0, 1};
    nValue = 0;
    for (IntIterator it = vector.getIndexIterator(); it.hasNext();) {
      int index = it.next();
      int value = vector.get(index);
      assertEquals(values[nValue], value);
      nValue++;
    }

    assertEquals(1, env.getWorldIndex());
    env.worldPop();
    assertTrue(vector.size() == 1);
    values = new int[]{4};
    nValue = 0;
    for (IntIterator it = vector.getIndexIterator(); it.hasNext();) {
      int index = it.next();
      int value = vector.get(index);
      assertEquals(values[nValue], value);
      nValue++;
    }

    assertEquals(0, env.getWorldIndex());
  }

    @Test
    public void test2(){
        IntIterator it = vector.getIndexIterator();
        Assert.assertFalse(it.hasNext());

        vector.staticAdd(5);
        it = vector.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        int ind = it.next();
        Assert.assertEquals(0, ind);
        Assert.assertEquals(5, vector.get(ind));
        Assert.assertFalse(it.hasNext());

        vector.remove(0);
        vector.add(5);
        it = vector.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        ind = it.next();
        Assert.assertEquals(PartiallyStoredIntVector.STORED_OFFSET, ind);
        Assert.assertEquals(5, vector.get(ind));
        Assert.assertFalse(it.hasNext());

        vector.staticAdd(4);
        it = vector.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        ind = it.next();
        Assert.assertEquals(0, ind);
        Assert.assertEquals(4, vector.get(ind));
        Assert.assertTrue(it.hasNext());
        ind = it.next();
        Assert.assertEquals(PartiallyStoredIntVector.STORED_OFFSET, ind);
        Assert.assertEquals(5, vector.get(ind));
        Assert.assertFalse(it.hasNext());


    }
}
