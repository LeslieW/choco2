package i_want_to_use_this_old_version_of_choco.util;

/**
 * A double linked list with constant time access, addition and deletion in o(1)
 * The list is encoded by two tables of integers. Efficient implementation regarding
 * time but very poor for memory.
 */
public class DoubleLinkedList implements IntIterator {

    /**
     *   Successors table
     */
    protected int[] nextT;

    /**
     * Predecessors table
      */
    protected int[] prevT;


    /**
     *  Current number of elements
      */
    protected int size = 0;

    /**
     * util for iteration
     */
    protected int currentT = 0;

    /**
     * maximum size of the list
      */
    protected int listSize;


    /**
     *  build a list of maximum size "listSize"
     * @param listSize
     */
    public DoubleLinkedList(int listSize) {
        this.listSize = listSize;
        this.nextT = new int[listSize + 1];
        this.prevT = new int[listSize + 1];
        for (int i = 0; i < nextT.length; i++) {
            nextT[i] = -1;
            prevT[i] = -1;
        }
    }

    /**
     *  Constructor (copy)
      */
    public DoubleLinkedList(DoubleLinkedList origin) {
        this.nextT = new int[origin.nextT.length];
        this.prevT = new int[origin.prevT.length];
        System.arraycopy(origin.nextT, 0, this.nextT, 0, this.nextT.length);
        System.arraycopy(origin.prevT, 0, this.prevT, 0, this.prevT.length);
        this.size = origin.size;
        this.listSize = origin.listSize;
    }

    /**
     * Add an element "val"
      */
    public void addVal(int val) {
        if (size == 0) {
            nextT[listSize] = val;
            prevT[listSize] = val;
        } else {
            prevT[nextT[listSize]] = val;
            nextT[val] = nextT[listSize];
            nextT[listSize] = val;
        }

        size = size + 1;
    }


    /**
     * Remove an element "val"
      */
    public void removeVal(int val) {
        if (size == 1) {
            nextT[listSize] = -1;
            prevT[listSize] = -1;
        } else if (nextT[listSize] == val) {
            nextT[listSize] = nextT[val];
            prevT[nextT[val]] = -1;
        } else if (prevT[listSize] == val) {
            prevT[listSize] = prevT[val];
            nextT[prevT[val]] = -1;
        } else {
            nextT[prevT[val]] = nextT[val];
            prevT[nextT[val]] = prevT[val];
        }
        nextT[val] = -1;
        prevT[val] = -1;
        size--;
    }

    /**
     *    Get current number of element
      */
    public int getSize() {
        return size;
    }

    /**
     * reset
     */
    public void reset() {
        size = 0;
        for (int i = 0; i < nextT.length; i++) {
            nextT[i] = -1;
            prevT[i] = -1;
        }
    }

    /**
     * Initialize the iterator
      */
    public void restart() {
        currentT = listSize;
    }

    /**
     * Set the iterator from val
     * @param val
     */
    public void restartFrom(int val) {
        currentT = val;
    }

    public boolean hasNext() {
        return ((currentT != -1) && (nextT[currentT] != -1));
    }

    public boolean hasNextTo(int val) {
        return ((currentT != -1) && (nextT[currentT] != val));
    }

    /**
     * return the next element
     */
    public int next() {
        currentT = nextT[currentT];
        return currentT;
    }

    /**
     * return the current iterated element
     */
    public int read() {
        return currentT;
    }

    /**
      * remove the current iterated element
      */
    public void remove() {
        removeVal(currentT);
        restart();
    }


    // Display the table
    public void AfficheTab() {
        for (int i = 0; i < nextT.length; i++) {
            System.out.println(nextT[i] + " | " + prevT[i]);
        }
        System.out.println("---");
    }



    // Display the table
    public String toString() {
        String n = "next    :";
        String p = "suivant :";
        for (int i = 0; i < nextT.length; i++) {
            n += nextT[i] + " ";
            p += prevT[i] + " ";
        }

        return ("first :" + nextT[listSize] + "| " + "last :" + prevT[listSize] + "| " + n + "| " + p);
    }

}