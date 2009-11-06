/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
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
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.memory;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 23 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class StoredIndexBipartiteSetTest {

    StoredIndexedBipartiteSet sibs;
    IEnvironment env;

    @Before
    public void before(){
        env = new EnvironmentTrailing();
    }

    @Test
    public void test0() {
        for(int seed = 0; seed < 20; seed++ ){
            Random r = new Random(seed);

            sibs = (StoredIndexedBipartiteSet)env.makeBipartiteSet(10);
            int[] v = new int[20];
            for(int i = 0; i <4; i++){
                int n = r.nextInt(10);
                if(v[n]==0){
                    v[n] = 1;
                    sibs.remove(n);
                }
            }
            sibs.increaseSize(10);
            for(int i = 0; i <4; i++){
                int n = 10 + r.nextInt(10);
                if(v[n]==0){
                    v[n] = 1;
                    sibs.remove(n);
                }
            }
            for(int i = 0; i< 20; i++){
                Assert.assertEquals("i:"+i, v[i]==0, sibs.contain(i));
            }
        }
    }

}
