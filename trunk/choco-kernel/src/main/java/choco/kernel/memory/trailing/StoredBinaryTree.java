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
package choco.kernel.memory.trailing;
/* ************************************************
*           _       _                            *
*          |  °(..)  |                           *
*          |_  J||L _|       Choco-Solver.net    *
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
*                    N. Jussien   1999-2008      *
**************************************************/


import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBinaryTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Apr 24, 2008
 * Time: 1:27:38 PM
 */
public class StoredBinaryTree implements IStateBinaryTree {



    EnvironmentTrailing env;
    StoredBinaryTreeTrail trail;
    Node root;
    int lastSavedWorld;
    boolean addLeft = true;
    boolean remLeft = true;


    public StoredBinaryTree(EnvironmentTrailing env, int a, int b)
    {
        this.env = env;
        this.trail = (StoredBinaryTreeTrail) env.getTrail(IEnvironment.BTREE_TRAIL);
        this.lastSavedWorld = 0;
        this.add(a,b);


    }


    public int getSize() {
        int out = 0;
        IntIterator it = this.getIterator();
        while (it.hasNext())
        {
            it.next();
            out++;
        }
        return out;
    }

    public Node find (int value)
    {
        Node current = this.root;
        while (current != null)
        {
            if (current.contains(value))
                return current;
            if (value < current.inf)
                current = current.leftNode;
            else
                current = current.rightNode;
        }
        return current;
    }



    public Node nextNode(int value)
    {
        Node current = this.root;
        while (current != null)
        {
            if (current.contains(value+1))
                return current;
            else if (value+1 < current.inf)
            {
                Node n = current.leftNode;
                if (n == null)
                    return current;
                current = n;
            }
            else
            {
                Node n = current.rightNode;
                if (n == null)
                    return this.nextNode(current);
                current = n;


            }


        }
        return null;
    }
    public Node prevNode(int value)
    {
        Node current = this.root;
        while (current != null)
        {
            if (current.contains(value-1))
                return current;
            else if (value-1 < current.inf)
            {
                Node n = current.leftNode;
                if (n == null)
                    return this.prevNode(current);
                current = n;
            }
            else
            {
                Node n = current.rightNode;
                if (n == null)
                    return current;
                current = n;


            }


        }
        return null;
    }



    public void add(Node n)
    {
        add(n,true);
    }

    public void remove(Node n)
    {
        remove(n,true);
    }


    public void remove(Node n, boolean save)
    {
        if (save)
        {
            trail.stack(this,n,REM);
        }


        if (n.leftNode == null && n.rightNode == null)
        {
            if (n.father == null)
                this.root = null;
            else if (n.father.leftNode == n)
                n.father.leftNode = null;
            else
                n.father.rightNode = null;
        }
        else if (n.leftNode == null)
        {
            n.rightNode.father = n.father;
            if (n.father == null)
                this.root = n.rightNode;
            else if (n.father.leftNode == n)
                n.father.leftNode = n.rightNode;
            else
                n.father.rightNode = n.rightNode;
        }
        else if (n.rightNode == null)
        {
            n.leftNode.father = n.father;
            if (n.father == null)
                this.root = n.leftNode;
            else if (n.father.leftNode == n)
                n.father.leftNode = n.leftNode;
            else
                n.father.rightNode = n.leftNode;

        }
        else
        {
            Node current;
            Node curSon;
            Node curFat;
            if (remLeft)
            {
                current = n.leftNode;
                while (current.rightNode != null)
                    current = current.rightNode;
                curSon = current.leftNode;
                curFat  = current.father;
            }
            else
            {
                current = n.rightNode;
                while (current.leftNode != null)
                    current = current.leftNode;
                curSon = current.rightNode;
                curFat = current.father;
            }


            if (curFat != n)
            {
                current.rightNode = n.rightNode;
                current.leftNode = n.leftNode;
                current.rightNode.father = current;
                current.leftNode.father = current;
                if (remLeft) {
                    curFat.rightNode = curSon;
                }
                else {
                    curFat.leftNode = curSon;
                }
                if (curSon != null)
                    curSon.father = curFat;
            }
            else
            {
                if (remLeft)
                {
                    current.rightNode = n.rightNode;
                    current.rightNode.father = current;
                }
                else
                {
                    current.leftNode = n.leftNode;
                    current.leftNode.father = current;

                }
            }




            current.father = n.father;
            if (current.father == null)
                this.root = current;
            else if (current.father.leftNode == n)
            {
                current.father.leftNode = current;
            }
            else
            {
                current.father.rightNode = current;
            }

            remLeft = !remLeft;

        }
    }

    public void add(int a, int b) {
        this.add(new Node(this,a,b),false);
    }


    public void add(Node n, boolean save)
    {
        if (save)
        {
            trail.stack(this,n,ADD);
        }
        Node current = this.root;
        boolean done = false;
        if (current == null) {
            this.root = n;
            done = true;
        }
        while (!done)
        {
            if (current.inf > n.inf)
            {
                if (current.leftNode == null)
                {
                    current.leftNode = n;
                    n.father = current;
                    done = true;
                }
                else
                    current = current.leftNode;

            }
            else if (current.inf < n.inf)
            {
                if (current.rightNode == null)
                {
                    current.rightNode = n;
                    n.father = current;
                    done = true;
                }
                else current = current.rightNode;
            }
            else          {
                LOGGER.severe("GROS PB");
                done = true;
            }
        }
    }

    public Node getRoot() {
        return this.root;
    }

    public Node prevNode(Node n)
    {
        Node cur = n;
        if (cur.leftNode != null) {
            cur = cur.leftNode;
            while (cur.rightNode != null)
                cur = cur.rightNode;
            return cur;
        }
        else if (cur.father == null)
            return null;
        else if (cur.father.rightNode == cur)
            return cur.father;
        else
        {
            while (cur.father != null && cur.father.leftNode == cur)
                cur = cur.father;

            return cur.father ;

        }

    }

    public Node nextNode(Node n)
    {
        Node cur = n;
        if (cur.rightNode != null) {
            cur = cur.rightNode;
            while (cur.leftNode != null)
                cur = cur.leftNode;
            return cur;
        }
        else if (cur.father == null)
            return null;
        else if (cur.father.leftNode == cur)
            return cur.father;
        else
        {
            while (cur.father != null && cur.father.rightNode == cur)
                cur = cur.father;

            return cur.father ;

        }

    }


    public boolean remove(int value)
    {
        Node container = this.find(value);

        if (container == null)
            return false;

        else if (container.getSize() == 1)
        {
            this.remove(container,true);
        }

        else if (container.inf == value)
        {
            container.setInf(container.inf+1);
        }
        else if (container.sup == value)
        {
            container.setSup(container.sup-1);
        }
        else
        {
            Node n2;
            if (addLeft)
            {
                n2 = new Node(this,value+1,container.sup);
                container.setSup(value-1);
            }
            else
            {
                n2 = new Node(this,container.inf,value-1);
                container.setInf(value+1);
            }
            this.add(n2);
            addLeft = !addLeft;
        }
        return true;
    }

    public StoredBinaryTreeTrail getTrail()
    {
        return this.trail;
    }

    public IEnvironment getEnvironment()
    {
        return env;
    }

    public Node getFirstNode()
    {
        Node current = this.root;
        if (current == null)
            return null;
        while (current.leftNode != null)
            current = current.leftNode;
        return current;
    }

    public Node getLastNode()
    {
        Node current = this.root;
        if (current == null)
            return null;
        while (current.rightNode != null)
            current = current.rightNode;
        return current;
    }

    private void rem(int value)
    {
        remove(value);
    }


    public String toString()
    {
        return toListString();
        /* StringBuffer b = new StringBuffer();
      b.append("[");
      IntIterator it = this.getIterator();
      while (it.hasNext())
          b.append(it.next()).append(",");

      b.deleteCharAt(b.length()-1);
      b.append("]");

      return b.toString(); */
    }

    public IntIterator getIterator()
    {
        return new TreeIterator();
    }

    public List<Node> toList()
    {
        ArrayList<Node> out = new ArrayList<Node>();
        Node current = this.getFirstNode();
        while (current != null)
        {
            out.add(current);
            current = this.nextNode(current);
        }
        return out;
    }

    public String toListString()
    {
        StringBuffer buf = new StringBuffer("[");
        List<Node> tmp = this.toList();
        for (Node iv : tmp)
            buf.append(iv.toString()).append(" ");
        if (!tmp.isEmpty())
            buf.deleteCharAt(buf.length()-1);
        buf.append("]");
        return buf.toString();
    }

    public class TreeIterator implements IntIterator
    {
        int currentValue;
        Node currentNode;
        Node lastNode;

        public TreeIterator()
        {
            currentValue = Integer.MIN_VALUE;
            currentNode = getFirstNode();
            lastNode = getLastNode();
        }

        public boolean hasNext() {
            return (lastNode != null && currentValue < lastNode.sup);
        }

        public int next() {

            if (currentValue == Integer.MIN_VALUE)
            {
                currentValue = currentNode.inf;
            }
            else if (currentValue+1 <= currentNode.sup)
            {
                currentValue++;
            }
            else
            {
                currentNode = nextNode(currentNode);
                currentValue = currentNode.inf;
            }
            return currentValue;
        }

        public void remove() {
            rem(currentValue);
            currentNode = nextNode(currentValue);
            lastNode = getLastNode();

        }
    }

    public String toDotty()
    {
        String s = "digraph binary_tree_domain {\n";
        s+=this.toDotty(this.root);
        s+="\n}";
        return s;
    }



    public String toDotty(Node n)
    {
        String s ="";
        if (n.leftNode != null)
        {
            s+= "\""+n+"\" -> \""+n.leftNode+"\";\n";
            s+= this.toDotty(n.leftNode);
        }
        if (n.rightNode != null)
        {
            s+= "\""+n+"\" -> \""+n.rightNode+"\";\n";
            s+= this.toDotty(n.rightNode);
        }


        return s;

    }

    public static void print(IStateBinaryTree b)
    {

        String f = "bui.dot";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(f)));
            bw.write(b.toDotty());
            bw.flush();
            bw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}