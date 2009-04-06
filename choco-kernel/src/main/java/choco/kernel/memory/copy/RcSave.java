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
package choco.kernel.memory.copy;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Since : Choco 2.0.0
 *
 */
public class RcSave implements RecomputableElement {

    public RecomputableElement[][] currentElement;
    private EnvironmentCopying environment;
    private int lastSavedWorldIndex;





    private Map<Integer,int []> saveInt;
    private Map<Integer,Object[][]> saveVector;
    private Map<Integer,int[][]> saveIntVector;
    private Map<Integer,boolean[]> saveBool;
    private Map<Integer,BitSet[]> saveBitSet;
//
//    private Vector<int []> saveInt;
//    private Vector<Object[][]> saveVector;
//    private Vector<int[][]> saveIntVector;
//    private Vector<boolean[]> saveBool;
//    private Vector<BitSet[]> saveBitSet;



    public RcSave(EnvironmentCopying env) {
        environment = env;
        currentElement = env.test;
        lastSavedWorldIndex = env.getWorldIndex();



        saveInt = new HashMap<Integer,int []>();
        saveVector = new HashMap<Integer,Object[][]>();
        saveIntVector = new HashMap<Integer,int[][]>();
        saveBool = new HashMap<Integer,boolean[]>();
        saveBitSet = new HashMap<Integer,BitSet[]>();

//
//        saveInt = new Vector<int []>();
//        saveVector = new Vector<Object[][]>();
//        saveIntVector = new Vector<int[][]>();
//        saveBool = new Vector<boolean[]>();
//        saveBitSet = new Vector<BitSet[]>();

    }


    public void save(int worldIndex) {
        if (lastSavedWorldIndex >= worldIndex)
            lastSavedWorldIndex = 0;

        boolean [] tmpbool =  new boolean[currentElement[BOOL].length];
        int i;// = 0;
        for (i = 0 ; i < currentElement[BOOL].length ; i++ ) {
            tmpbool[i] = ((RcBool) currentElement[BOOL][i]).deepCopy();
        }
        saveBool.put(worldIndex,tmpbool);
//        saveBool.add(worldIndex,tmpbool);

        int [] tmpint = new int [currentElement[INT].length];
        i = 0;
        for (i = 0; i < currentElement[INT].length ; i++ ) {
            tmpint[i] = ((RcInt) currentElement[INT][i]).deepCopy();
        }
        saveInt.put(worldIndex,tmpint);
//        saveInt.add(worldIndex,tmpint);

        Object[][] tmpvec = new Object[currentElement[VECTOR].length][];
        i = 0;
        for (i = 0 ; i < currentElement[VECTOR].length ; i++) {
            if (worldIndex != 0 && lastSavedWorldIndex >= (currentElement[VECTOR][i]).getTimeStamp() )
                tmpvec[i] = saveVector.get(lastSavedWorldIndex)[i];
            else
                tmpvec[i] = ((RcVector) currentElement[VECTOR][i]).deepCopy();
        }
        saveVector.put(worldIndex,tmpvec);
//        saveVector.add(worldIndex,tmpvec);

        int[][] tmpintvec = new int [currentElement[INTVECTOR].length][];
        i = 0;
        for (i = 0 ; i < currentElement[INTVECTOR].length ; i++) {
            if (worldIndex != 0 && lastSavedWorldIndex >= (currentElement[INTVECTOR][i]).getTimeStamp() )
                tmpintvec[i] = saveIntVector.get(lastSavedWorldIndex)[i];
            else
                tmpintvec[i] = ((RcIntVector) currentElement[INTVECTOR][i]).deepCopy();
        }
        saveIntVector.put(worldIndex,tmpintvec);
//        saveIntVector.add(worldIndex,tmpintvec);

        BitSet[] tmpbitset = new BitSet[currentElement[BITSET].length];
        for (i = 0 ;i < currentElement[BITSET].length ; i++) {
            if (worldIndex != 0 && lastSavedWorldIndex >= (currentElement[BITSET][i]).getTimeStamp() )
                tmpbitset[i] = saveBitSet.get(lastSavedWorldIndex)[i];
            else
                tmpbitset[i] = (java.util.BitSet) ((RcBitSet) currentElement[BITSET][i]).getBitSet().clone();
        }
        saveBitSet.put(worldIndex,tmpbitset);
//        saveBitSet.add(worldIndex,tmpbitset);


        lastSavedWorldIndex = worldIndex;


    }

    public void restore(int worldIndex) {
        boolean[] tmpbool = saveBool.get(worldIndex);
        int[] tmpint = saveInt.get(worldIndex);
        Object[][] tmpvec = saveVector.get(worldIndex);
        int[][] tmpintvec = saveIntVector.get(worldIndex);
        java.util.BitSet[] tmpbitset = saveBitSet.get(worldIndex);
        //saveVector.remove(worldIndex);

        for (int i = 0 ; i < tmpbool.length ; i++)
            ((RcBool) currentElement[BOOL][i]).set(tmpbool[i]);
        for (int i = 0 ; i < tmpint.length ; i++)
            ((RcInt) currentElement[INT][i]).set(tmpint[i]);
        for (int i = 0 ; i < tmpvec.length ; i++)
            ((RcVector) currentElement[VECTOR][i])._set(tmpvec[i]);
        for (int i = 0 ; i < tmpintvec.length ; i++)
            ((RcIntVector) currentElement[INTVECTOR][i])._set(tmpintvec[i]);
        for (int i = 0 ; i < tmpbitset.length ; i++)
            ((RcBitSet) currentElement[BITSET][i])._set(tmpbitset[i]);

        if (worldIndex == 0)
            clearMaps();
        else
            remove(worldIndex+1);
  //          removeLast();


    }

    public void remove(int worldIndex) {
        saveInt.remove(worldIndex);
        saveVector.remove(worldIndex);
        saveIntVector.remove(worldIndex);
        saveBool.remove(worldIndex);
        saveBitSet.remove(worldIndex);
    }

/*    public void removeLast() {
        int last = saveInt.size() -1 ;
        saveInt.removeElementAt(last);
        saveVector.removeElementAt(last);
        saveIntVector.removeElementAt(last);
        saveBool.removeElementAt(last);
        saveBitSet.removeElementAt(last);

    } */

    private void clearMaps() {
        saveInt.clear();
        saveVector.clear();
        saveIntVector.clear();
        saveBool.clear();
        saveBitSet.clear();
    }


    public int getType() {
        return -1;
    }

    public int getTimeStamp() {
        return 0;
    }
}
