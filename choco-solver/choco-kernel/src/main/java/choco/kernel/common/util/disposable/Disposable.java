/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.common.util.disposable;

import choco.kernel.common.logging.ChocoLogging;
import gnu.trove.TObjectIntHashMap;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 28 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 * <p/>
 * An interface to declare disposable object, ie object that can be reused (iterators,...)
 */
public abstract class Disposable {

    private static final Logger LOGGER  = ChocoLogging.getMainLogger();

    /**
     * Set the satus of Disposable object.
     * If set to true, the object is disposable, available for a new usage.
     * MANDATORY to avoid multiple call to dispose()!
     */
    protected boolean disposable = true;

    static TObjectIntHashMap<String> classes = new TObjectIntHashMap<String>();

    /**
     * This method allows to declare that an object is not used anymore. It
     * can be reused by another object.
     */
    @SuppressWarnings({"unchecked"})
    public synchronized void dispose() {
        if(!disposable){
            getContainer().offer(this);
        }
        disposable = true;
    }

    public void init(){
        disposable = false;
    }

    /**
     * Get the containerof disposable objects where free ones are available
     * @return a {@link java.util.Deque}
     */
    public abstract Queue getContainer();


    public synchronized static <E> Queue<E> createContainer() {
        return Factory.create();
    }

    public static void flush(){
        Factory.flush();
    }

    private static class Factory {
        private static Queue[] stacks = new Queue[15];
        private static int index;

        private Factory() {}

        public static <E> Queue<E> create() {
            Queue<E> stack = new ArrayDeque<E>(8);
            if (index == stacks.length) {
                increaseCapacity();
            }
            stacks[index++] = stack;
            return stack;
        }

        private static void increaseCapacity() {
            Queue[] temp = stacks;
            stacks = new Queue[(index << 1)];
            System.arraycopy(temp, 0, stacks, 0, index);
        }

        private static void flush(){
            LOGGER.info("*******************************");
            LOGGER.info("FLUSH");
            for(int i = 0; i<  index; i++){
                if(!stacks[i].isEmpty()){
                    LOGGER.info(String.format("%s->%d", stacks[i].peek().getClass().getName(), stacks[i].size()));
                }
            }
            LOGGER.info("*******************************");
        }
    }
}
