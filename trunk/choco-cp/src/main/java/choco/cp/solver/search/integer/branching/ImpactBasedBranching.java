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
package choco.cp.solver.search.integer.branching;

import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractLargeIntBranching;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.*;

/**
 * Impact based branchging based on the code from Hadrien
 * <p/>
 * Written by Guillaumme on 17 may 2008
 */
public class ImpactBasedBranching extends AbstractLargeIntBranching {
	Solver _solver;
	IntDomainVar[] _vars;
	AbstractImpactStrategy _ibs;

    protected Random randValueChoice;
    protected Random randomBreakTies;

    private static final int ABSTRACTVAR_EXTENSION =
			AbstractVar.getAbstractVarExtensionNumber("choco.cp.cpsolver.search.integer.ImpactBasedBranching");

	protected static final class ImpactBasedBranchingVarExtension {
		private int index = 0;
	}

	static IntDomainVar[] varsFromSolver(Solver s) {
		IntDomainVar[] vars = new IntDomainVar[s.getNbIntVars()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = (IntDomainVar) s.getIntVar(i);
		}
		return vars;
	}

	public ImpactBasedBranching(Solver solver, IntDomainVar[] vars, AbstractImpactStrategy ibs) {
		_solver = solver;
		_vars = vars;
		for (IntDomainVar var : _vars) {
			((AbstractVar) var).setExtension(ABSTRACTVAR_EXTENSION, new ImpactBasedBranchingVarExtension());
		}
		_ibs = ibs;
	}

	public ImpactBasedBranching(Solver solver, IntDomainVar[] vars) {
		this(solver, vars, null);
		_ibs = new ImpactRef(this, _vars);
	}

	public ImpactBasedBranching(Solver solver) {
		this(solver, varsFromSolver(solver));
	}

	public AbstractImpactStrategy getImpactStrategy() {
		return _ibs;
	}

    public void setRandomVarTies(int seed) {
        randomBreakTies = new Random(seed);
    }

    public Object selectBranchingObject() throws ContradictionException {
		double min = Double.MAX_VALUE;
		IntDomainVar minVar = null;
		if (randomBreakTies == null) {
            for (IntDomainVar var : _vars) {
                if (!var.isInstantiated()) {
                    double note;
                    if (var.hasEnumeratedDomain())
                        note = _ibs.getEnumImpactVar(var);
                    else
                        note = _ibs.getBoundImpactVar(var);
                    if (note < min) {
                        min = note;
                        minVar = var;
                    }
                }
            }
            return minVar;
        } else {
            //return null;
            List<IntDomainVar> lvs = new LinkedList<IntDomainVar>();
            for (IntDomainVar var : _vars) {
                if (!var.isInstantiated()) {
                    double note;
                    if (var.hasEnumeratedDomain())
                        note = _ibs.getEnumImpactVar(var);
                    else
                        note = _ibs.getBoundImpactVar(var);
                    if (note < min) {
                        lvs.clear();
                        min = note;
                        lvs.add(var);
                    } else if (note == min) {
                        lvs.add(var);
                    }
                }
            }
            if (lvs.size() == 0) {
                return null;
            }
            return lvs.get(randomBreakTies.nextInt(lvs.size()));
        }

	}

	public int getFirstBranch(Object x) {
		return getBestVal(x);
	}

	public int getNextBranch(Object x, int i) {
		return getBestVal(x);
	}

    public void setRandomValueChoice(long seed) {
        randValueChoice = new Random(seed);
    }

    public int getBestVal(Object x) {
        IntDomainVar var = (IntDomainVar) x;
        if (randValueChoice == null) {
            if (var.hasEnumeratedDomain()) {
                DisposableIntIterator iter = var.getDomain().getIterator();
                double min = Double.MAX_VALUE;
                int minVal = Integer.MAX_VALUE;
                while (iter.hasNext()) {
                    int val = iter.next();
                    double note = _ibs.getImpactVal(var, val);
                    if (note < min) {
                        min = note;
                        minVal = val;
                    }
                }
                iter.dispose();
                return minVal;
            } else {
                return var.getInf();
            }
        } else {
            if (var.hasEnumeratedDomain()) {
                if (var.isInstantiated()) return var.getVal();                
                int val = (randValueChoice.nextInt(var.getDomainSize()));
                DisposableIntIterator iterator = var.getDomain().getIterator();
                for (int i = 0; i < val; i++) {
                    iterator.next();
                }
                int res = iterator.next();
                iterator.dispose();
                return res;
            } else {
                int val = (randValueChoice.nextInt(2));
                if (val == 0) return var.getInf();
                else return var.getSup();
            }
        }
    }

	public boolean finishedBranching(Object x, int i) {
		return ((IntDomainVar) x).getDomainSize() == 0;
	}

	@Override
	public void goDownBranch(Object x, int i) throws ContradictionException {
		logDownBranch(x, i);
		IntDomainVar y = (IntDomainVar) x;
		_ibs.doBeforePropagDownBranch(x, i);
		try {
			y.setVal(i);
			_solver.propagate();
		} catch (ContradictionException e) {
			_ibs.doAfterFail(x, i);

			throw e;
		}
		_ibs.doAfterPropagDownBranch(x, i);
	}

	@Override
	public void goUpBranch(Object x, int i) throws ContradictionException {
		super.goUpBranch(x, i);
		IntDomainVarImpl y = (IntDomainVarImpl) x;
		y.remVal(i);
	}


	public interface ImpactStrategy {
		/**
		 * return the impact of the variable var.
		 *
		 * @return the value of the impact.
		 */
		public double getEnumImpactVar(IntDomainVar var);

		/**
		 * Only one impact is stored for a BoundIntVar (not an impact per value)
		 *
		 * @return the value of the impact.
		 */
		public double getBoundImpactVar(IntDomainVar var);

		/**
		 * return the impact of the choice var == val.
		 *
		 * @return the value of the impact.
		 */
		public double getImpactVal(IntDomainVar var, int val);

		public void doBeforePropagDownBranch(Object o, int i);

		public void doAfterPropagDownBranch(Object o, int i);


		public void doAfterFail(Object o, int i);
	}

	public abstract static class AbstractImpactStrategy implements ImpactStrategy {
		ImpactBasedBranching _branching;

		// The subset of variables on which impact are maintained and computed
		ArrayList svars;
		int nbVar;
		int sumDom = 0;
		ImpactStorage dataS;

		public AbstractImpactStrategy(ImpactBasedBranching branching, ArrayList subset) {
			svars = subset;
			_branching = branching;
			dataS = new ImpactStorage(_branching._solver, subset);
			nbVar = subset.size();
			for (Object svar : svars) {
				sumDom += ((IntDomainVar) svar).getDomainSize();
			}
		}

		public void setDataS(ImpactStorage dataS) {
			this.dataS = dataS;
		}

		/**
		 * Each value of each variable is tried to initialize
		 * impact. A pruning according to this singleton
		 * consistency phase's is done.
		 *
		 * @param timelimit limit to achieve the singleton algorithm
		 * @return true if no contradiction occured
		 */
		public boolean initImpacts(int timelimit) {
			if (timelimit != 0) {
            long tps = System.currentTimeMillis();
			_branching._solver.generateSearchStrategy();
			try {
				_branching._solver.propagate();
				_branching._solver.worldPush();
				for (int i = 0; i < svars.size(); i++) {
					//for (Object svar : svars) {
					IntDomainVar v = (IntDomainVar) svars.get(i);
					if (!v.isInstantiated() && v.hasEnumeratedDomain()) {
						IntIterator it = v.getDomain().getIterator();
						while (it != null && it.hasNext()) {
							int val = it.next();
							boolean cont = false;
							if (v.hasBooleanDomain() && val > v.getInf() && val < v.getSup())
								break;							
							_branching._solver.worldPush();
							try {
								goDownBranch(v, val);
							} catch (ContradictionException e) {
								cont = true;
							}
							_branching._solver.worldPop();
							if (cont) {
								_branching._solver.worldPop();
								try {
									v.remVal(val);
									_branching._solver.propagate();
								} catch (ContradictionException e) {
									return false;
								}
								_branching._solver.worldPush();								
							}
							if ((System.currentTimeMillis() - tps) > timelimit) {
								_branching._solver.worldPop();
								_branching._solver.getSearchStrategy().clearTrace();
								((CPSolver) _branching._solver).resetSearchStrategy();
								return true;
							}
						}
					}
				}
				_branching._solver.worldPop();
			} catch (ContradictionException e) {
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			_branching._solver.getSearchStrategy().clearTrace();
			((CPSolver) _branching._solver).resetSearchStrategy();
            }
            return true;

        }


		public void goDownBranch(Object x, int i) throws ContradictionException {
			_branching.goDownBranch(x, i);
		}

		protected static class ImpactStorage {

			public int[] offsets;
			public int[] sizes;

			/**
			 * in order to speed up the computation of the index of a tuple
			 * in the table, blocks[i] stores the sum of the domain sizes of variables j with j < i.
			 */
			public int[] blocks;

			public Solver pb;

			public ImpactStorage(ImpactStorage impst) {
				offsets = impst.offsets;
				sizes = impst.sizes;
				blocks = impst.blocks;
			}

			public ImpactStorage(Solver pb, ArrayList subset) {
				this.pb = pb;
				offsets = new int[subset.size()];
				sizes = new int[subset.size()];
				blocks = new int[subset.size()];
				if (subset.size() > 0) 
                    blocks[0] = 0;
				for (int i = 0; i < subset.size(); i++) {
					IntDomainVar tv = (IntDomainVar) subset.get(i);
					((ImpactBasedBranchingVarExtension) ((AbstractVar) tv).getExtension(ABSTRACTVAR_EXTENSION)).index = i;
					if (tv.hasEnumeratedDomain()) {
						offsets[i] = tv.getInf();
						sizes[i] = tv.getSup() - tv.getInf() + 1; //((IntDomainVar) subset.get(i)).getDomainSize(); // tv.getSup() - tv.getInf() + 1;
					} else { // pour les variables sur bornes, on ne stocke que l'impact sur la variable pas sur la valeur
						offsets[i] = 0;
						sizes[i] = 1;
					}
					if (i > 0)
						blocks[i] = blocks[i - 1] + sizes[i - 1];
				}
			}

			public double computeCurrentTreeSize() {
				double prod = 1;
				for (int i = 0; i < pb.getNbIntVars(); i++) {
					prod *= ((IntDomainVar) pb.getIntVar(i)).getDomainSize();
				}
				return prod;
			}

			public int getChoiceAddress(IntDomainVar var, int val) {
				int idx = ((ImpactBasedBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).index;
				//int idx = ((IBSIntVarImpl) var).getIndex();
				return (blocks[idx] + val - offsets[idx]);
			}
		}

	}

	private final class ImpactRef extends AbstractImpactStrategy {

		/**
		 * I(x_i = a) = 1 - Pafter(x_i = a) / Pbefore(x_i = a)
		 * High impacts (close to 1) denote high search space reductions
		 */
		protected double[] impact;

		/**
		 * the number of time a decision x_i = a is taken
		 */
		protected int[] nbDecOnVarVal;

		protected ImpactBasedBranching _branching;


		public ImpactRef(ImpactBasedBranching branching, IntDomainVar[] subset) {
			this(branching, new ArrayList(Arrays.asList(subset)));
		}


		public ImpactRef(ImpactBasedBranching branching, ArrayList vars) {
			super(branching, vars);
			_branching = branching;
			int totalSize = 0;
            if (vars.size() != 0)
                totalSize = dataS.blocks[vars.size() - 1] + dataS.sizes[vars.size() - 1];
			impact = new double[totalSize];
			nbDecOnVarVal = new int[totalSize];
			domBefore = new int[vars.size()];
			domAfter = new int[vars.size()];
		}

		public void addImpact(IntDomainVar v, int val, double value) {
			if (v.hasEnumeratedDomain())
				impact[dataS.getChoiceAddress(v, val)] += value;
			else impact[dataS.getChoiceAddress(v, 0)] += value;
		}

		public void updateSearchState(IntDomainVar var, int val) {
			if (var.hasEnumeratedDomain())
				nbDecOnVarVal[dataS.getChoiceAddress(var, val)] += 1;
			else nbDecOnVarVal[dataS.getChoiceAddress(var, 0)] += 1;
		}

		/**
		 * Return I(var = val)
		 */
		public double getImpactVal(IntDomainVar var, int val) {
			int idx = dataS.getChoiceAddress(var, val);
			if (nbDecOnVarVal[idx] > 0) {
				return impact[idx] / (double) nbDecOnVarVal[idx];
			} else
				return 0.0;
		}

		/**
		 * Return impact by giving directly the adress in the table
		 */
		public double getImpactVal(int idx) {
			if (nbDecOnVarVal[idx] > 0) {
				return impact[idx] / (double) nbDecOnVarVal[idx];
			} else
				return 0.0;
		}

		/**
		 * sum over each value of var, the remaining search space
		 *
		 * @param var
		 */
		public double getEnumImpactVar(IntDomainVar var) {
			int idx = ((ImpactBasedBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).index;
			if (idx != -1) {
				double imp = 0.0;
				IntIterator it = var.getDomain().getIterator();
				int blockadress = dataS.blocks[idx] - dataS.offsets[idx];
				while (it.hasNext()) {
					int val = it.next();
					imp += 1 - getImpactVal(blockadress + val);
				}
				return imp;
			} else
				return 0;
		}

		public double getBoundImpactVar(IntDomainVar var) {
			int idx = ((ImpactBasedBranchingVarExtension) ((AbstractVar) var).getExtension(ABSTRACTVAR_EXTENSION)).index;
			if (idx != -1) {
				return 1 - getImpactVal(var, 0);
			} else
				return 0;
		}

		// --------------- Computation of tree sizes  --------------------------- //

		protected int[] domBefore, domAfter;
		protected boolean flag = false;

		/**
		 * The sizes of the domains are stored before and after each choice.
		 * The search space reduction is then computed as the product of pAfter[i]/pBfore[i] for all i
		 */
		public void computeSearchReduction(IntDomainVar x, int val, int[] pAfter, int[] pBefore) {
			double reduc = 1.0;
			for (int i = 0; i < pAfter.length; i++) {
				reduc *= (double) pAfter[i] / (double) pBefore[i];
			}
			reduc = 1.0 - reduc;
			addImpact(x, val, reduc);
		}

		public void computeCurrentDomSize(int[] domSizes) {
			for (int i = 0; i < domSizes.length; i++) {
				domSizes[i] = ((IntDomainVar) svars.get(i)).getDomainSize();
			}
		}

		public void doBeforePropagDownBranch(Object o, int i) {
			flag = ((IntDomainVar) o).getDomainSize() > 1;
			if (flag) {  // Once a domain is reduced to a singleton, choco still call the godownbranch method
				computeCurrentDomSize(domBefore);
				updateSearchState((IntDomainVar) o, i);
			}
		}

		public void doAfterPropagDownBranch(Object o, int i) {
			if (flag) {
				computeCurrentDomSize(domAfter);
				computeSearchReduction((IntDomainVar) o, i, domAfter, domBefore);
			}
		}

		public void doAfterFail(Object o, int i) {
			addImpact((IntDomainVar) o, i, 1.0);
		}
	}
}
