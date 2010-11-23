package choco.cp.common.util.preprocessor.detector.scheduling;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;

import java.util.BitSet;

import choco.Choco;
import choco.kernel.common.IDotty;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.TemporalConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

public class DisjunctiveModel implements IDotty {

	//number of nodes
	public final int nbNodes;

	//number of arcs
	protected int nbArcs = 0;

	//number of edges
	protected int nbEdges = 0;

	protected final BitSet[] precGraph;

	protected final BitSet[] disjGraph;

	protected final TIntIntHashMap setupTimes;

	//associate the direction variable gave rise to the edge
	public final TIntObjectHashMap<TemporalConstraint> storedConstraints;


	public DisjunctiveModel(Model model) {
		this(model.getNbStoredMultipleVars());
	}

	public DisjunctiveModel(int n) {
		precGraph = new BitSet[n]; 
		disjGraph = new BitSet[n]; 
		setupTimes = new TIntIntHashMap();
		storedConstraints = new TIntObjectHashMap<TemporalConstraint>();
		for (int i = 0; i < n; i++) {
			precGraph[i] = new BitSet(n);
			disjGraph[i] = new BitSet(n);
		}
		nbNodes = n;
	}

	public final int getNbNodes() {
		return nbNodes;
	}

	public final int getNbArcs() {
		return nbArcs;
	}

	public final int getNbEdges() {
		return nbEdges;
	}


	public final int getKey(int i, int j) {
		return (i* nbNodes) +j;
	}

	public final boolean isEmpty() {
		return nbArcs == 0 && nbEdges == 0;
	}

	public final void safeAddArc(TaskVariable i, TaskVariable j) {
		safeAddArc(i.getHook(), j.getHook(), j.start().getLowB() - i.end().getUppB());
	}

	public final void safeAddArc(int i, int j, int setupTime) {
		if(setupTime >= 0) addArc(i, j, setupTime, null);
	}

	private void addArc(int i, int j, int setupTime, TemporalConstraint c) {
		precGraph[i].set(j);
		final int key = getKey(i, j);
		if(c != null) storedConstraints.put(key, c);
		setupTimes.put(key, setupTime);
		nbArcs++;
	}



	private boolean mergeSetupTime(int key, int setupTime) {
		if(setupTimes.get(key) < setupTime) {
			setupTimes.put(key, setupTime);
			return true;
		}else return false;
	}

	private boolean mergeArc(int i, int j, int setupTime, TemporalConstraint c) {
		final int key = getKey(i, j);
		if( mergeSetupTime(key, setupTime) ) {
			final TemporalConstraint cdm = storedConstraints.get(key);
			if(cdm == null) {
				storedConstraints.put(key, c);
				return false;
			} else {
				if( i == cdm.getOHook()) cdm.setForwardSetup(setupTime);
				else cdm.setBackwardSetup(setupTime);
			}
		}
		return true;
	}

	/**
	 * @return deleteC
	 */
	public boolean safeAddArc(TemporalConstraint c) {
		final int i = c.getOHook();
		final int j = c.getDHook();
		assert c.isDirConstant();
		if(c.getDirVal() == 0) {
			//add backward arc
			if(containsArc(j, i)) return mergeArc(j, i, c.backwardSetup(), c); 
			else addArc(j, i, c.backwardSetup(), c);
		}else {
			//add forward arc
			if(containsArc(i, j)) return mergeArc(i, j, c.forwardSetup(), c); 
			else addArc(i, j, c.forwardSetup(), c);
		}
		return false;
	}

	private void mergeFwdSetup(int key,int setupTime, TemporalConstraint dest) {
		if(mergeSetupTime(key, setupTime)) dest.setForwardSetup(setupTime);
	}

	private void mergeBwdSetup(int key,int setupTime, TemporalConstraint dest) {
		if(mergeSetupTime(key, setupTime)) dest.setBackwardSetup(setupTime);
	}

	private AddEdgeStatus mergeEdge(int i, int j, TemporalConstraint ct) {
		final int key1 = getKey(i, j);
		final int key2 = getKey(j, i);
		final TemporalConstraint cij = storedConstraints.get(key1);
		final TemporalConstraint cji = storedConstraints.get(key2);
		if(cij != null) {
			mergeFwdSetup(key1, ct.forwardSetup(), cij);
			mergeBwdSetup(key2, ct.backwardSetup(), cij);
			return new AddEdgeStatus(true, cij.getDirection());
		} else if(cji != null) {
			mergeBwdSetup(key1, ct.forwardSetup(), cji);
			mergeFwdSetup(key2, ct.backwardSetup(), cji);
			return new AddEdgeStatus(true, null, cji.getDirection());
		} else throw new ModelException("No Edge");
	}

	
	protected void addEdge(int i, int j, TemporalConstraint c) {
		disjGraph[i].set(j);
		disjGraph[j].set(i);
		final int key = getKey(i, j);
		storedConstraints.put(key, c);
		setupTimes.put(key, c.forwardSetup());
		setupTimes.put(getKey(j, i), c.backwardSetup());
		nbEdges++;
	}

	final static class AddEdgeStatus {
		
		public boolean deleteC;
		
		public IntegerVariable repV;
		
		public IntegerVariable oppV;

		public AddEdgeStatus(boolean deleteC) {
			this(deleteC, null, null);
		}
		
		public AddEdgeStatus(boolean deleteC, IntegerVariable repV) {
			this(deleteC, repV, null);
		}
		
		public AddEdgeStatus(boolean deleteC, IntegerVariable repV,
				IntegerVariable oppV) {
			super();
			this.deleteC = deleteC;
			this.repV = repV;
			this.oppV = oppV;
		}
	}
	
	public final AddEdgeStatus safeAddEdge(TemporalConstraint c) {
		assert ! c.getDirection().isConstant();
		final int i = c.getOHook();
		final int j = c.getDHook();
		if( containsArc(i, j)) {
			return new AddEdgeStatus(mergeArc(i, j, c.forwardSetup(), c), Choco.ONE);
		}else if(containsArc(j, i)) {
			return new AddEdgeStatus(mergeArc(j, i, c.backwardSetup(), c), Choco.ZERO);
		} else if(containsEdge(i, j)) {
			return mergeEdge(i, j, c);
		} else {
			addEdge(i, j, c);
			return new AddEdgeStatus(false);
		}
	}
	
	public final int setupTime(int i, int j) {
		return setupTimes.get(getKey(i, j));
	}

	public final int setupTime(TaskVariable i, TaskVariable j) {
		return setupTime(i.getHook(), j.getHook());
	}

	public final boolean containsArc(int i, int j) {
		return precGraph[i].get(j);
	}

	public final boolean containsArc(TaskVariable i, TaskVariable j) {
		return containsArc(i.getHook(), j.getHook());
	}

	public final boolean containsEdge(int i, int j) {
		return disjGraph[i].get(j);
	}
	
	public final boolean containsEdge(TaskVariable i, TaskVariable j) {
		return containsEdge(i.getHook(), j.getHook());
	}
	
	public final boolean containsRelation(int i, int j) {
		return containsArc(i, j) || containsArc(j, i) || containsEdge(i, j);
	}
	
	public final boolean containsRelation(TaskVariable i, TaskVariable j) {
		return containsRelation(i.getHook(), j.getHook());
	}

	public final boolean containsConstraint(int i, int j) {
		return storedConstraints.contains(getKey(i, j));
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < nbNodes; i++) {
			for (int j = 0; j < nbNodes; j++) {
				if (precGraph[i].get(j)) {
					s.append("o ");
				}else if (disjGraph[i].get(j)) {
					s.append("x ");
				} else {
					s.append(". ");
				}
			}
			s.append("\n");
		}
		return s.toString();
	}

	public String setupTimesToString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < nbNodes; i++) {
			for (int j = 0; j < nbNodes; j++) {
				if (precGraph[i].get(j) 
						|| disjGraph[i].get(j)) {
					s.append(setupTimes.get(getKey(i, j))).append(' ');
				} else {
					s.append(". ");
				}
			}
			s.append("\n");
		}
		return s.toString();
	}
	
	@Override
	public final String toDotty() {
		final StringBuilder  b = new StringBuilder();
		for (int i = 0; i < nbNodes; i++) {
			for (int j = precGraph[i].nextSetBit(0); j >= 0; j = precGraph[i]
			                                                               .nextSetBit(j + 1)) {
				b.append(i).append(" -> ").append(j);
				b.append(" [");
				b.append("color=forestgreen");
				if(containsConstraint(i, j)) {
					final int st = setupTime(i, j);
					if(st > 0) {
						b.append(", label=\"").append(st).append("\"");
					}		
				}else {
					b.append(", style=dotted");
				}
				b.append("];\n");
			}

			for (int j = disjGraph[i].nextSetBit(0); j >= 0; j = disjGraph[i]
			                                                               .nextSetBit(j + 1)) {
				if( containsConstraint(i, j) ) {
					b.append(i).append(" -> ").append(j);
					b.append(" [");
					b.append("color=royalblue, style=bold, arrowhead=dot");
					final int st1 = setupTime(i, j);
					final int st2 = setupTime(j, i);
					b.append(", label=\"");
					//b.append(storedConstraints.get(getKey(i, j)).getDirection().getName());
					if( st1 > 0 || st2 > 0) {
						b.append(" (").append(st1).append(", ").append(st2).append(")");
					}
					b.append("\"];\n");
				}
			}
		}
		return b.toString();
	}
}
