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

package choco.kernel.solver.constraints.global.matching;

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract class encoding assignment graphs (matching each left vertex with one single right vertex)
 * We consider a flow in the graph by adding a source linked to all right vertices
 * and a sink linked to all left vertices
 * <p/>
 * It is based on computing the strongly connected components of the residual graph,
 * then remove arcs connecting two different strongly connected components
 * <p/>
 * Computing the strongly connected components is done by an algorithm
 * of Aho, Hopcroft, Ullman using depth first search (Cormen, Leiserson, p. 478, p. 489)
 * <p/>
 * Note (EGA) on ice traduction from claire to java :
 * class StrongConnectionDecomposition have been included in this one
 */

public abstract class AbstractBipartiteGraph extends AbstractLargeIntSConstraint {
  protected Logger logger = Logger.getLogger("choco.kernel.solver.propagation.const");

  // slots storing the graph as a matching
  protected int nbLeftVertices, nbRightVertices, nbVertices;
  protected int minValue = Integer.MIN_VALUE;
  protected int maxValue = Integer.MAX_VALUE;
  protected int source;
  protected IStateIntVector refMatch;   // storing a reference matching: from leftVars onto rightVars
  protected IStateInt matchingSize;     // and its size = #{i | refMatch[i] != 0}
  protected int[] left2rightArc, right2leftArc; // storing the alternating forest (in the search for augmenting paths)
  protected IntQueue queue;

  // slots for algorithm computing the strongly connected components
  // temporary data structure: markers, iterators, ....
  protected int time = 0;               // a time counter
  protected int[] finishDate;           // finishDate[i] : value of time when the expansion of i was completed in DFS
  protected boolean[] seen;             // seen[i]=true <=> node i has been expanded in DFS
  protected int currentNode = -1;       // the current node in the second DFS exploration
  protected int currentComponent = -1;  // a counter used when building the solution
  // the solution
  protected int[] component;            // storing the solution: component[i] is the index of strong con. comp. oof i
  protected boolean[][] componentOrder; // componentOrder[i,j]=true <=> there exists an edge in the SCC graph from
  // component i to component j

  /**
   * Constructor
   *
   * @param vars    the graph, a left vextex per vars, a right vertex per domain value
   * @param nbLeft  number of left nodes, = vars.length
   * @param nbRight number of right nodes, domain values of vars
   */
  public AbstractBipartiteGraph(IntDomainVar[] vars, int nbLeft, int nbRight) {
    super(vars);
    init(nbLeft, nbRight);
  }

  public Object clone() throws CloneNotSupportedException {
    AbstractBipartiteGraph newc = (AbstractBipartiteGraph) super.clone();
    newc.init(this.nbLeftVertices, this.nbRightVertices);
    return newc;
  }

  protected void init(int nbLeft, int nbRight) {
    //this.nbLeftVertices = this.vars.length;
    this.nbLeftVertices = nbLeft;
    //this.nbRightVertices = this.maxValue - this.minValue + 1;
    this.nbRightVertices = nbRight;
    this.nbVertices = this.nbLeftVertices + this.nbRightVertices + 1;

    this.refMatch = this.getSolver().getEnvironment().makeIntVector(this.nbLeftVertices, -1);
    this.matchingSize = this.getSolver().getEnvironment().makeInt(0);
    this.queue = new IntQueue(this.nbVertices - 1);
    this.left2rightArc = new int[this.nbLeftVertices];
    this.right2leftArc = new int[this.nbRightVertices];
    this.source = this.nbVertices - 1;
    this.finishDate = new int[this.nbVertices]; // Default value : 0
    this.seen = new boolean[this.nbVertices]; // Default value : false
    this.component = new int[this.nbVertices];
    for (int i = 0; i < component.length; i++) {
      component[i] = -1;
    }
    this.componentOrder = new boolean[this.nbVertices][this.nbVertices];
    for (int i = 0; i < this.nbVertices; i++) {
      this.componentOrder[i][i] = true;
    }

    //this.logger.setLevel(Level.SEVERE);
  }

  // ==============================================================
  //           GENERIC IMPLEMENTATION OF A BIPARTITE GRAPH
  // ==============================================================

  /**
   * Accessing the edges of the bipartite graph access from the left vertex set:
   * reading domains of modeling variables
   *
   * @param i the left vertex
   * @return the set of right vertices that can be matched to i
   */
  public int[] mayMatch(int i) {
    int[] ret = new int[((IntDomainVar) this.getVar(i)).getDomain().getSize()];
    int offset = 0;
    DisposableIntIterator iterator = ((IntDomainVar) this.getVar(i)).getDomain().getIterator();
    for (; iterator.hasNext();) {
      int j = iterator.next();
      ret[offset++] = j - this.minValue;
    }
    iterator.dispose();
    return ret;

  }

  /**
   * reverse, access from the right vertex set:
   * iterating over the variables (left vertex set) and reading their domains
   *
   * @param j the right vertex
   * @return the set of left vertices that can be matched to j
   */
  public int[] mayInverseMatch(int j) {
    int[] ret = new int[this.nbLeftVertices];
    int nb = 0;
    for (int i = 0; i < this.nbLeftVertices; i++) {
      if (((IntDomainVar) this.getVar(i)).canBeInstantiatedTo(j + this.minValue)) {
        ret[nb++] = i;
      }
    }
    int[] ret2 = new int[nb];
    System.arraycopy(ret, 0, ret2, 0, nb);
    return ret2;
  }

  /**
   * @param i left vextex index
   * @return accessing the right vertex matched to i
   */
  public int match(int i) {
    return this.refMatch.get(i);
  }

  /**
   * @param i a left vertex
   * @return whether the flow from i to the sink may be increased
   */
  public boolean mayGrowFlowToSink(int i) {
    return this.match(i) == -1;
  }

  /**
   * @param j a right vertex
   * @param i a left vertex
   * @return whether the flow from j to i may be increased
   * (meaning whether that the additional flow is able to arrive to j, we don't care yet
   * whether it will be able to leave i)
   */
  public boolean mayGrowFlowBetween(int j, int i) {
    return this.match(i) != j;
  }

  /**
   * @param j a right vertex
   * @param i a left vertex
   * @return whether the flow from j to i may be decreased
   */
  public boolean mayDiminishFlowBetween(int j, int i) {
    return this.match(i) == j;
  }

  /**
   * updates the matching size when one more left vertex is matched with j
   *
   * @param j a right vertex
   */
  public abstract void increaseMatchingSize(int j);

  /**
   * updates the matching size when one more left vertex is de-matched with j
   *
   * @param j a right vertex
   */
  public abstract void decreaseMatchingSize(int j);

  /**
   * removing the arc i-j from the reference matching & update matchingSize
   *
   * @param i a left vertex
   * @param j a right vertex
   */
  public abstract void deleteMatch(int i, int j);

  /**
   * adding the arc i-j in the reference matching without any updates
   *
   * @param i a left vertex
   * @param j a right vertex
   */
  public abstract void putRefMatch(int i, int j);

  /**
   * adding the arc i-j in the reference matching & update matchingSize
   *
   * @param i a left vertex
   * @param j a right vertex
   */
  public abstract void setMatch(int i, int j);

// a flow is built in the bipartite graph from the reference matching
//          match(i) = j <=> flow(j to i) = 1
//    from a source linked to all right vertices and a sink linked to all left vertices
//    Yes, this IS counter-intuitive, the flow goes from left to right
//    but this makes the job much easier for gcc in order to compute compatible flows
//    (with lower bounds on the edges from the source to the right vertices)


  /**
   * @param j a right vertex
   * @return whether the flow from the source to j may be decreased
   */
  public abstract boolean mayDiminishFlowFromSource(int j);

  /**
   * @param j a right vertex
   * @return whether the flow from the source to j may be increased
   */
  public abstract boolean mayGrowFlowFromSource(int j);

  /**
   * @param j a right vertex
   * @return whether the flow from the source to j must be increased in order
   * to get a maximal (sink/left vertex set saturating) flow
   */
  public abstract boolean mustGrowFlowFromSource(int j);

  /**
   * two methods used for detecting that an edge should be removed from the bipartite assignment graph
   * deleteMatch          -> removes it from the graph data strutures
   * deleteEdgeAndPublish -> same + publishes the information outside the constraint
   *
   * @param i a left vertex
   * @param j a right vertex
   * @throws ContradictionException
   */
  public abstract void deleteEdgeAndPublish(int i, int j) throws ContradictionException;

// integrity check: checking that the flow is indeed maximal (yielding an assignment)
//  [choco/checkFlow(c:AbstractBipartiteGraph) : void
//   -> let n1 := c.nbLeftVertices, n2 := c.nbRightVertices in
//        assert(forall(i1 in (1 .. n1) | match(c,i1) != 0))]

  // ==============================================================
  //            FINDING AN AUGMENTING PATH IN THE GRAPH
  // ==============================================================

  /**
   * First pass: use Ford & Fulkerson algorithm to compute a reference flow (assignment)
   * finds an augmenting path using a fifo queue
   *
   * @return 0 if none found, otherwise the end of the path
   */
  public int findAlternatingPath() {
    if (logger.isLoggable(Level.INFO))
      logger.info("Search for an augmenting path to grow matching above " + this.matchingSize + " nodes.");
    int eopath = -1;
    int n = this.nbLeftVertices;
    this.queue.init();
    for (int j = 0; j < this.nbRightVertices; j++) {
      // enqueue vertives of V2 whose lower bounds haven't been reached
      if (this.mustGrowFlowFromSource(j)) this.queue.push(j + n);
    }
    if (this.queue.getSize() == 0) {
      for (int j = 0; j < this.nbRightVertices; j++) {
        // otherwise enqueue vertives of V2 whose upper bounds haven't been reached
        if (this.mayGrowFlowFromSource(j)) this.queue.push(j + n);
      }
    }
    while (this.queue.getSize() > 0) {
      int x = this.queue.pop();
      if (logger.isLoggable(Level.FINE)) logger.fine("FIFO: pop " + x);
      if (x >= n) { // if the dequeued vertex is in V1
        x -= n;
        boolean shouldBreak = false;
        int[] yy = this.mayInverseMatch(x);
        for (int i = 0; i < yy.length; i++) { // For each value y in mayInverseMatch(x)
          int y = yy[i];
          if (this.mayGrowFlowBetween(x, y) && !this.queue.onceInQueue(y)) {
            if (logger.isLoggable(Level.FINE)) logger.fine(y + "." + x + "[vs." + this.match(y) + "]");
            this.left2rightArc[y] = x;
            if (this.mayGrowFlowToSink(y)) {
              eopath = y;
              shouldBreak = true;
              break;
            } else {
              this.queue.push(y);
            }
          }
        }
        if (shouldBreak) break;
      } else {
        // assert (! this.mayGrowFlowToSink(x))
        int y = this.match(x);
        // assert (y >= 0)
        // assert (this.mayDiminishFlowBetween(y,x))
        if (!this.queue.onceInQueue(y + n)) {
          if (logger.isLoggable(Level.FINE)) logger.fine(x + "#" + y);
          this.right2leftArc[y] = x;
          this.queue.push(y + n);
        }
      }
    }
    if (logger.isLoggable(Level.INFO))
      logger.info("Found an alternating path ending in " + eopath + " (-1 if none).");
    return eopath;
  }

  /**
   * augment the matching along one alternating path
   * note: throughout the following code, we assume (1 <= x <= c.nbLeftVertices), (1 <= y <= c.nbRightVertices)
   *
   * @param x
   */
  public void augment(int x) {
    int y = this.left2rightArc[x];
    while (!this.mayGrowFlowFromSource(y)) {
      if (logger.isLoggable(Level.FINE)) logger.fine("Add " + x + "." + y);
      this.putRefMatch(x, y);
      x = this.right2leftArc[y];
      if (logger.isLoggable(Level.FINE)) logger.fine("Rem " + x + "." + y);
      assert(this.match(x) == y);
      y = this.left2rightArc[x];
      assert(y >= 0);
    }
    if (logger.isLoggable(Level.FINE)) logger.fine("Add " + x + "." + y);
    this.putRefMatch(x, y);
    this.increaseMatchingSize(y);
  }

  /**
   * keeps augmenting the flow until a maximal flow is reached
   *
   * @throws ContradictionException
   */
  public void augmentFlow() throws ContradictionException {
    int eopath = this.findAlternatingPath();
    int n1 = this.nbLeftVertices;

    if (this.matchingSize.get() < n1) {
      if (logger.isLoggable(Level.INFO)) logger.info("Current flow of size: " + this.matchingSize.get());
      while (eopath >= 0) {
        this.augment(eopath);
        eopath = this.findAlternatingPath();
      }
      if (this.matchingSize.get() < n1) {
        // assert exist i, 0 <= i < n1, this.match(i) == 0
        if (logger.isLoggable(Level.INFO)) logger.info("There exists no perfect matching.");
        this.fail();
      } else {
        if (logger.isLoggable(Level.INFO))
          logger.info("Found a perfect metching (size: " + this.matchingSize.get() + ").");
        if (logger.isLoggable(Level.INFO)) {
          for (int i = 0; i < this.nbLeftVertices; i++) {
            if (logger.isLoggable(Level.INFO)) logger.info("Match " + i + " with " + this.match(i));
          }
          // TODO CheckFlow ...
        }
      }
    }
  }

  // ==============================================================
  //       FINDING STRONGLY CONNECTED COMPONENTS IN THE GRAPH
  // ==============================================================

  /**
   * initialize the graph data structure storing the SCC decomposition
   */
  public void initSCCGraph() {
    // erase the component partial order graph
    int nbc = this.getNbComponents();
    for (int i = 0; i < nbc; i++) {
      for (int j = 0; j < nbc; j++)
        if (i != j) {
          this.componentOrder[i][j] = false;
        }
    }
    // erase the component graph
    for (int i = 0; i < this.nbVertices; i++) {
      this.component[i] = -1;
    }
    this.currentComponent = -1;
  }

  /**
   * adds a new vertex to the component graph (= a component = a set of s. connected vertices in the original graph)
   */
  public void addComponentVertex() {
    this.currentComponent++;
  }

  public int getNbComponents() {
    return currentComponent + 1;
  }

  /**
   * add an edge in the component graph between compi and compj:
   * componentOrder stores the transitive closure of that graph
   *
   * @param compi
   * @param compj
   */
  public void addComponentEdge(int compi, int compj) {
    if (!this.componentOrder[compi][compj]) {
      this.componentOrder[compi][compj] = true;
      for (int compj2 = 0; compj2 < compj; compj2++) {
        if (this.componentOrder[compj][compj2]) {
          this.componentOrder[compi][compj2] = true;
        }
      }
    }
  }

  /**
   * seen[i] = false <=> color[i] = white (in book)
   * = true               % {gray, black}
   */
  public void firstPassDFS() {
    for (int i = 0; i < this.nbVertices; i++) {
      this.finishDate[i] = 0;
      this.seen[i] = false;
    }
    this.time = 0;
    for (int i = 0; i < this.nbVertices; i++) {
      this.firstDFSearch(i);
    }
  }

  /**
   * the first search explores (DFS) the reduced graph
   *
   * @param i
   */
  public void firstDFSearch(int i) {
    if (!this.seen[i]) {
      this.time++;
      this.seen[i] = true;
      if (i < this.nbLeftVertices) {    // (i % c.leftVertices)
        // assert (this.match(i) != 0);
        this.firstDFSearch(this.match(i) + this.nbLeftVertices);
      } else if (i < this.source) {     // (i % c.rightVertices)
        int[] jj = this.mayInverseMatch(i - this.nbLeftVertices);
        for (int k = 0; k < jj.length; k++) { // for each j in mayInverseMatch...
          int j = jj[k];
          if (this.match(j) != i - this.nbLeftVertices) {
            this.firstDFSearch(j);
          }
        }
        if (this.mayDiminishFlowFromSource(i - this.nbLeftVertices)) {
          this.firstDFSearch(this.source);
        }
      } else {                          // (i = sc.source)
        for (int j = 0; j < this.nbRightVertices; j++) {
          if (this.mayGrowFlowFromSource(j)) {
            this.firstDFSearch(j + this.nbLeftVertices);
          }
        }
      }
      this.time++;
      this.finishDate[i] = this.time;
    }
  }

  public void secondPassDFS() {
    this.initSCCGraph();
    while (true) { // Pas genial :(
      int maxf = 0;
      int rootOfComp = -1;
      for (int i = 0; i < this.nbVertices; i++) {
        if ((this.component[i] == -1) && (this.finishDate[i] > maxf)) {
          maxf = this.finishDate[i];
          rootOfComp = i;
        }
      }
      if (maxf > 0) {
        this.addComponentVertex();
        this.secondDFSearch(rootOfComp);
      } else
        return;
    }
  }

  /**
   * the second search explores (DFS) the inverse of the reduced graph
   *
   * @param i
   */
  public void secondDFSearch(int i) {
    int compi = this.component[i];
    int curComp = this.currentComponent;
    if (compi == -1) {
      this.component[i] = curComp;
      this.currentNode = i;
      if (i < this.nbLeftVertices) {    // (i % c.leftVertices)
        int[] jj = this.mayMatch(i);
        for (int k = 0; k < jj.length; k++) {
          int j = jj[k];
          if (this.match(i) != j) {
            this.secondDFSearch(j + this.nbLeftVertices);
          }
        }
      } else if (i < this.source) {     // (i % c.rightVertices)
        // TODO On peut pas utiliser le reverse Match ???
        int[] jj = this.mayInverseMatch(i - this.nbLeftVertices);
        for (int k = 0; k < jj.length; k++) {
          int j = jj[k];
          if (this.match(j) == i - this.nbLeftVertices) {
            this.secondDFSearch(j);
          }
        }
        if (this.mayGrowFlowFromSource(i - this.nbLeftVertices))
          this.secondDFSearch(this.source);
      } else {                          // (i = sc.source)
        for (int j = 0; j < this.nbRightVertices; j++) {
          if (this.mayDiminishFlowFromSource(j)) {
            this.secondDFSearch(j + this.nbLeftVertices);
          }
        }
      }
    } else if (compi < curComp) {
      // ajouter � la composante du sommet "p�re" une ar�te vers la composante du sommet i
      this.addComponentEdge(curComp, compi);
    } else if (compi > curComp) {
      if (logger.isLoggable(Level.SEVERE)) logger.severe("Unexpected strong connection component of higher index: " + compi + ", " + curComp);
    }
  }

  /**
   * remove arcs connecting two different strongly connected components
   * the event generated by the flow algorithm:
   * discovering that an edge is no longer valid, and posting this event
   * to the constraint strategy: since we are already achieving GAC consistency
   * in one single loop, there is no need to post a constAwake
   *
   * @throws ContradictionException
   */
  protected void removeUselessEdges() throws ContradictionException {
    // assert
    if (this.matchingSize.get() < this.nbLeftVertices) {
      this.augmentFlow();
    }

    refreshSCC();

    int nkept = 0, ndiscard = 0;

    // TODO : voir pour rendre plus efficace les explications !!
    for (int i = 0; i < this.nbLeftVertices; i++) {
      int[] jj = this.mayMatch(i);
      for (int k = 0; k < jj.length; k++) {
        int j = jj[k];
        if (this.match(i) != j) {
          if (this.component[i] != this.component[j + this.nbLeftVertices]) {
            ndiscard++;
            if (logger.isLoggable(Level.FINER)) logger.finer("discarded : " + i + " -> " + j);
            this.deleteEdgeAndPublish(i, j);
          } else {
            if (logger.isLoggable(Level.FINER)) logger.finer("Kept : " + i + " -> " + j);
            nkept++;
          }
        }
      }
    }
    if (logger.isLoggable(Level.FINE))
      logger.fine("SCC decomposition: " + nkept + " edges kept, " + ndiscard + " discarded.");
  }

  public void refreshSCC() {
    this.firstPassDFS();
    this.secondPassDFS();
    if (logger.isLoggable(Level.FINE))
      for (int i = 0; i < this.nbVertices; i++) {
        logger.fine("Vertex " + i + " belong to comp " + this.component[i]);
      }
  }

  /**
   * Achieves generalized arc consistency in one call
   *
   * @throws choco.kernel.solver.ContradictionException
   */
  public void propagate() throws ContradictionException {
    //System.out.println("propagate" + this);
    if (logger.isLoggable(Level.FINER)) logger.finer("Propagate...");
    this.removeUselessEdges();
  }

  public int getPriority() {
    return 2;
  }

  public void prettyPrintForDebug() {
    for(int i = 0; i < nbLeftVertices; i++) {
      System.out.println(vars[i].pretty() + ": " + refMatch.get(i) + "[" + component[i] + "]");
    }
    for(int i = 0; i < nbRightVertices; i++) {
      System.out.println("val(" + i + "): [" + component[i+nbLeftVertices]+ "]");
    }
    System.out.println("Matching size = " + matchingSize.get() + "/" + nbLeftVertices);

  }

  // ==============================================================
  //   QUEUE OF REACHED VERTICES WHEN FINDING AN AUGMENTING PATH
  // ==============================================================

  protected class IntQueue {
    /**
     * Maximum size of the queue.
     */
    private int maxSize;

    /**
     * Number of elements actually in the queue.
     */
    private int nbElts;

    /**
     * Last element pushed in the queue.
     */
    private int last;

    /**
     * Linked list of the values.
     */
    private int[] contents;

    /**
     * States if the value is in the queue or not.
     */
    private boolean[] onceInQueue;

    /**
     * Constructs a new queue with the specified maximal number of values.
     *
     * @param n Maximal size of the queue.
     */
    public IntQueue(int n) {
      maxSize = n;
      contents = new int[n];
      onceInQueue = new boolean[n];
      this.init();
    }


    /**
     * @return the size of the queue.
     */
    public int getSize() {
      return this.nbElts;
    }

    /**
     * Initializes the queue.
     */
    public void init() {
      this.nbElts = 0;
      for (int i = 0; i < this.maxSize; i++) {
        this.contents[i] = -1;
        this.onceInQueue[i] = false;
      }
    }

    /**
     * Adds a value in the queue
     *
     * @param val
     */
    public void push(int val) {
      // assert (val <= this.maxSize);
      this.onceInQueue[val] = true;
      if (this.contents[val] == -1) {
        if (this.nbElts == 0) {
          this.contents[val] = val;
        } else {
          this.contents[val] = this.contents[last];
          this.contents[last] = val;
        }
        this.last = val;
        this.nbElts++;
      }
    }

    /**
     * @return the older value in the queue
     */
    public int pop() {
      int val = this.contents[this.last];
      this.nbElts--;
      this.contents[this.last] = this.contents[val];
      this.contents[val] = -1;
      return val;
    }

    /**
     * @param i
     * @return true if i is in the queue
     */
    public boolean onceInQueue(int i) {
      return this.onceInQueue[i];
    }
  }
}
