package choco.memory;

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


import choco.Choco;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.structure.StoredBipartiteList;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.model.variables.scheduling.TaskVariable;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Arnaud Malapert</br> 
 * @since 5 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class StoredBipartiteListTest {
  
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();
    private EnvironmentTrailing env;
   
    private StoredBipartiteList<TaskVariable> iVectA;
    private TaskVariable[] tasks;
    @Before
    public void setUp() {
        LOGGER.fine("StoredIntBipartiteList Testing...");

        env = new EnvironmentTrailing();
        tasks = Choco.makeTaskVarArray("T", 0, 20, new int[]{1, 2, 3, 4, 5, 10, 11, 12, 13, 14, 15, 200});
        iVectA = new StoredBipartiteList<TaskVariable>(env, tasks);
    }

    @After
    public void tearDown() {
        iVectA = null;
        env = null;
    }

    @Test
    public void test1() {
        assertEquals(12, iVectA.size());
        LOGGER.info(StringUtils.pretty(iVectA));
        env.worldPush();
        Iterator<TaskVariable> it = iVectA.iterator();
        int cpt = 5;
        while (it.hasNext() && cpt > 0) {
            it.next();
            cpt--;
            if (cpt == 0) {
                it.remove();
            }
        }
       // it.dispose();
        LOGGER.info(StringUtils.pretty(iVectA));
        assertEquals(11, iVectA.size());
        assertEquals(tasks[10], iVectA.get(10));
        env.worldPush();
        it = iVectA.iterator();
        cpt = 6;
        while (it.hasNext() && cpt > 0) {
            it.next();
            it.remove();
            cpt--;
        }
      //  it.dispose();
        LOGGER.info(StringUtils.pretty(iVectA));
        assertEquals(5, iVectA.size());
        env.worldPop();
        LOGGER.info(StringUtils.pretty(iVectA));
        assertEquals(11, iVectA.size());
        env.worldPop();
        assertEquals(12, iVectA.size());
        env.worldPush();
        it = iVectA.iterator();
        LOGGER.info(StringUtils.pretty(iVectA));
        while (it.hasNext()) {
            LOGGER.log(Level.INFO, "value {0}", it.next());
            it.remove();
        }
       // it.dispose();
        LOGGER.info(StringUtils.pretty(iVectA));
        assertEquals(0, iVectA.size());

    }

  
}
