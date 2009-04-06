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

import choco.kernel.memory.IStateBinaryTree;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Apr 24, 2008
 * Time: 4:47:45 PM
 */
public class StoredBinaryTreeTrail implements ITrailStorage {


	private IStateBinaryTree[] treeStack;

	private StoredBinaryTree.Node[] nodeStack;
	private int[] opStack;
	private int[] oldValues;
	private int[] stampStack;


	private int[] worldStartLevels;

	private EnvironmentTrailing environment;

	private int maxHist;
	private int currentLevel;

	public StoredBinaryTreeTrail(EnvironmentTrailing env, int maxHist, int maxWorld)
	{

		this.environment = env;
		this.currentLevel = 0;
		this.opStack = new int[maxHist];
		this.oldValues = new int[maxHist];
		this.treeStack = new IStateBinaryTree[maxHist];
		this.nodeStack = new IStateBinaryTree.Node[maxHist];
		this.stampStack = new int[maxHist];
		this.worldStartLevels = new int[maxWorld];
		this.maxHist = maxHist;
	}


	public void stack(IStateBinaryTree b, IStateBinaryTree.Node n, int operation)
	{
		treeStack[currentLevel] = b;
		nodeStack[currentLevel] = n;
		opStack[currentLevel] = operation;
		switch(operation)
		{
		case IStateBinaryTree.INF : oldValues[currentLevel] = n.inf ; stampStack[currentLevel] = n.infStamp; break ;
		case IStateBinaryTree.SUP : oldValues[currentLevel] = n.sup ; stampStack[currentLevel] = n.supStamp; break ;
		default : break;
		}

		currentLevel++;




		if (currentLevel == maxHist)
			resizeUpdateCapacity();

	}

	private void resizeUpdateCapacity() {
		final int newCapacity = ((maxHist * 3) / 2);
		final IStateBinaryTree[] tmp1 = new IStateBinaryTree[newCapacity];
		System.arraycopy(treeStack, 0, tmp1, 0, treeStack.length);
		treeStack = tmp1;

		// then, copy the stack of former values
		final StoredBinaryTree.Node[] tmp2 = new StoredBinaryTree.Node[newCapacity];
		System.arraycopy(nodeStack, 0, tmp2, 0, nodeStack.length);
		nodeStack = tmp2;


		final int[] tmp3 = new int[newCapacity];
		System.arraycopy(opStack, 0, tmp3, 0, opStack.length);
		opStack = tmp3;

		// then, copy the stack of world stamps
		final int[] tmp4 = new int[newCapacity];
		System.arraycopy(oldValues, 0, tmp4, 0, oldValues.length);
		oldValues = tmp4;

		final int[] tmp5 = new int[newCapacity];
		System.arraycopy(stampStack, 0, tmp5, 0, stampStack.length);
		stampStack = tmp5;

		// last update the capacity
		maxHist = newCapacity;
	}


	public void worldPush() {
		worldStartLevels[environment.getWorldIndex() +1] = currentLevel;
	}

	public void worldPop() {



		while (currentLevel > worldStartLevels[environment.getWorldIndex()]) {
			currentLevel--;

			final IStateBinaryTree b = treeStack[currentLevel];



			final IStateBinaryTree.Node n = nodeStack[currentLevel];
			int operation = opStack[currentLevel];

			switch (operation)
			{
			case IStateBinaryTree.INF : n._setInf(oldValues[currentLevel],stampStack[currentLevel]); break;
			case IStateBinaryTree.SUP : n._setSup(oldValues[currentLevel],stampStack[currentLevel]); break;
			case IStateBinaryTree.ADD :
				b.remove(n,false);
				break;
			case IStateBinaryTree.REM : n.leftNode = null ;
			n.rightNode = null ;
			n.father = null ;
			b.add(n,false); break;
			}
		}
	}

	public void worldCommit() {
		//TODO
	}

	public int getSize() {
		return currentLevel;
	}


	public void resizeWorldCapacity(int newWorldCapacity) {
		final int[] tmp = new int[newWorldCapacity];
		System.arraycopy(worldStartLevels, 0, tmp, 0, worldStartLevels.length);
		worldStartLevels = tmp;
	}
}