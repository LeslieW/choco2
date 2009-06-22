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
    private Map<Integer,long []> saveLong;
    private Map<Integer,double []> saveDouble;
    private Map<Integer,Object []> saveObject;





    public RcSave(EnvironmentCopying env) {
        environment = env;
        currentElement = env.test;
        lastSavedWorldIndex = env.getWorldIndex();



        saveInt = new HashMap<Integer,int []>();
        saveVector = new HashMap<Integer,Object[][]>();
        saveIntVector = new HashMap<Integer,int[][]>();
        saveBool = new HashMap<Integer,boolean[]>();
        saveBitSet = new HashMap<Integer,BitSet[]>();
        saveLong = new HashMap<Integer, long[]>();
        saveDouble = new HashMap<Integer, double[]>();
        saveObject = new HashMap<Integer, Object[]>();
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

        int [] tmpint = new int [currentElement[INT].length];
        i = 0;
        for (i = 0; i < currentElement[INT].length ; i++ ) {
            tmpint[i] = ((RcInt) currentElement[INT][i]).deepCopy();
        }
        saveInt.put(worldIndex,tmpint);

        Object[][] tmpvec = new Object[currentElement[VECTOR].length][];
        i = 0;
        for (i = 0 ; i < currentElement[VECTOR].length ; i++) {
            if (worldIndex != 0 && lastSavedWorldIndex >= (currentElement[VECTOR][i]).getTimeStamp() )
                tmpvec[i] = saveVector.get(lastSavedWorldIndex)[i];
            else
                tmpvec[i] = ((RcVector) currentElement[VECTOR][i]).deepCopy();
        }
        saveVector.put(worldIndex,tmpvec);

        int[][] tmpintvec = new int [currentElement[INTVECTOR].length][];
        i = 0;
        for (i = 0 ; i < currentElement[INTVECTOR].length ; i++) {
            if (worldIndex != 0 && lastSavedWorldIndex >= (currentElement[INTVECTOR][i]).getTimeStamp() )
                tmpintvec[i] = saveIntVector.get(lastSavedWorldIndex)[i];
            else
                tmpintvec[i] = ((RcIntVector) currentElement[INTVECTOR][i]).deepCopy();
        }
        saveIntVector.put(worldIndex,tmpintvec);

        BitSet[] tmpbitset = new BitSet[currentElement[BITSET].length];
        for (i = 0 ;i < currentElement[BITSET].length ; i++) {
            if (worldIndex != 0 && lastSavedWorldIndex >= (currentElement[BITSET][i]).getTimeStamp() )
                tmpbitset[i] = saveBitSet.get(lastSavedWorldIndex)[i];
            else
                tmpbitset[i] = (java.util.BitSet) ((RcBitSet) currentElement[BITSET][i]).getBitSet().clone();
        }
        saveBitSet.put(worldIndex,tmpbitset);

        long [] tmplong = new long [currentElement[LONG].length];
        i = 0;
        for (i = 0; i < currentElement[LONG].length ; i++ ) {
            tmplong[i] = ((RcLong) currentElement[LONG][i]).deepCopy();
        }
        saveLong.put(worldIndex,tmplong);

        double[] tmpdouble = new double [currentElement[DOUBLE].length];
        i = 0;
        for (i = 0; i < currentElement[DOUBLE].length ; i++ ) {
            tmpdouble[i] = ((RcDouble) currentElement[DOUBLE][i]).deepCopy();
        }
        saveDouble.put(worldIndex,tmpdouble);

        Object[] tmpobject = new Object[currentElement[OBJECT].length];
        for (i = 0 ;i < currentElement[OBJECT].length ; i++) {
            if (worldIndex != 0 && lastSavedWorldIndex >= (currentElement[OBJECT][i]).getTimeStamp() )
                tmpobject[i] = saveObject.get(lastSavedWorldIndex)[i];
            else
                tmpobject[i] = ((RcObject) currentElement[OBJECT][i]).deepCopy();
        }
        saveObject.put(worldIndex,tmpobject);


        lastSavedWorldIndex = worldIndex;


    }

    public void restore(int worldIndex) {
        boolean[] tmpbool = saveBool.get(worldIndex);
        int[] tmpint = saveInt.get(worldIndex);
        Object[][] tmpvec = saveVector.get(worldIndex);
        int[][] tmpintvec = saveIntVector.get(worldIndex);
        java.util.BitSet[] tmpbitset = saveBitSet.get(worldIndex);
        long[] tmplong = saveLong.get(worldIndex);
        double[] tmpdouble = saveDouble.get(worldIndex);
        Object[] tmpobject = saveObject.get(worldIndex);
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
        for (int i = 0 ; i < tmplong.length ; i++)
            ((RcLong) currentElement[LONG][i]).set(tmplong[i]);
        for (int i = 0 ; i < tmpdouble.length ; i++)
            ((RcDouble) currentElement[DOUBLE][i]).set(tmpdouble[i]);
        for (int i = 0 ; i < tmpobject.length ; i++)
            ((RcObject) currentElement[OBJECT][i]).set(tmpobject[i]);

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
        saveLong.remove(worldIndex);
        saveDouble.remove(worldIndex);
        saveObject.remove(worldIndex);
    }


    private void clearMaps() {
        saveInt.clear();
        saveVector.clear();
        saveIntVector.clear();
        saveBool.clear();
        saveBitSet.clear();
        saveLong.clear();
        saveDouble.clear();
        saveObject.clear();
    }


    public int getType() {
        return -1;
    }

    public int getTimeStamp() {
        return 0;
    }
}
