/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.kernel.common.util.disposable;

import choco.kernel.common.logging.ChocoLogging;

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
