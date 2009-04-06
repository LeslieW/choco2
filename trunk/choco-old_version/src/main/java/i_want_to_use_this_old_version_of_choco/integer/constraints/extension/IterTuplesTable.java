package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 30 janv. 2007
 * Time: 11:47:53
 * To change this template use File | Settings | File Templates.
 */
public class IterTuplesTable extends TuplesTable implements IterLargeRelation {

    protected ArrayList[][] allowedTuples;

    public IterTuplesTable(int[] offsetTable, int[] sizesTable) {
        super(true, offsetTable, sizesTable);
        allowedTuples = new ArrayList[offsetTable.length][];

        for (int i = 0; i < offsetTable.length; i++) {
            allowedTuples[i] = new ArrayList[sizesTable[i]];

            for (int j = 0; j < sizesTable[i]; j++) {
                allowedTuples[i][j] = new ArrayList();
                allowedTuples[i][j].add(Integer.MAX_VALUE);
            }
        }
    }

    public void setTuple(int[] tuple) {
        super.setTuple(tuple);
        for (int i = 0; i < tuple.length; i++) {
            insertTuple(tuple, i, tuple[i]);
        }

    }

    public int keyTuple(int[] tuple) {
        int address = tuple[0] - offsets[0];
        for (int i = 1; i < n; i++) {
            address = address * sizes[i] + (tuple[i] - offsets[i]);
        }
        return address;
    }

    public int[] convertTuple(int key) {
        int[] tuple = new int[n];
        for (int i = n - 1; i > 0; i--) {
            tuple[i] = key % sizes[i] + offsets[i];
            key /= sizes[i];
        }
        tuple[0] = key + offsets[0];
        return tuple;
    }


    public void insertTuple(int[] tuple, int indexVar, int value) {
        int key = keyTuple(tuple);
        convertTuple(key);
        int[] yy = convertTuple(key);
        ArrayList ht = allowedTuples[indexVar][value - offsets[indexVar]];
        int index = 0;
        int currentKey = (Integer) ht.get(index);

        while (currentKey < key) {
            index++;
            currentKey = (Integer) ht.get(index);
        }

        ht.add(index, key);

    }


    public int[] seekAllowedSupport(int[] currentSupport, int indexVar, int value) {
        int key = keyTuple(currentSupport);
        int next = 0;
        try {
            next = getMax(allowedTuples[indexVar][value - offsets[indexVar]], 0, allowedTuples[indexVar][value - offsets[indexVar]].size() - 1, key);
            if (next == Integer.MAX_VALUE) {
                return null;
            } else {
                return convertTuple(next);
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }


    public int getMax(ArrayList supports, int valD, int valF, int value) {
        while (valF - valD > 1) {
            //Double kk = Math.ceil((double) ((valF - valD) / 2) + valD);
            int pivot = (int) Math.ceil((double) ((valF - valD) / 2) + valD);
            if ((Integer) supports.get(pivot) < value) {
                valD = pivot;
            } else {
                valF = pivot;
            }
        }

        if (((Integer) supports.get(valD)) >= value) {
            return ((Integer) supports.get(valD));
        } else {
            return ((Integer) supports.get(valF));
        }
    }

}
