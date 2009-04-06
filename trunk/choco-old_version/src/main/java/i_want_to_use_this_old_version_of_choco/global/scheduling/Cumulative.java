package i_want_to_use_this_old_version_of_choco.global.scheduling;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.CumTreeT;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractTree;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;
import i_want_to_use_this_old_version_of_choco.util.IntList;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * Author: Hadrien
 * Date: 29 juil. 2005
 * <p/>
 * Cumulative : Given a set of tasks defined by their starting dates, ending dates, durations and
 * consumptions/heights, the cumulative ensures that at any time t, the sum of the heights of the tasks
 * which are executed at time t does not exceed a given limit C (the capacity of the ressource).
 * The notion of task does not exist yet in choco. The cumulative takes therefore as input three arrays
 * of integer variables (of same size n) denoting the starting, ending, and duration of each task.
 * The heights of the tasks are considered constant and given via an array of size n of positive integers.
 * The last parameter Capa denotes the Capacity of the cumulative (of the ressource).
 * The implementation is based on the papers of
 * Bediceanu and al :
 * "A new multi-resource cumulatives constraint with negative heights" in CP02
 * Van Hentenrick and Mercier :
 * "Edge finding for cumulative scheduling"
 * Petr Vilim :
 * Extension of O(nlogn) algorithms for the unary ressource constraint to optionnal activities
 * <p/>
 * todo: - add the assignment variables for multiple machines
 * todo: - add filtering for optionnal activities
 */
public class Cumulative extends AbstractLargeIntConstraint implements ITasksSet {

	private Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const");

	public boolean isHeightConstant;
	public boolean isDurationConstant;

	/**
	 * the number of tasks of the cumulative
	 */
	protected int nbTask;

	/**
	 * the capacity of the cumulative
	 */
	protected int capaMax;

	/**
	 * first data structure of the sweep algorithm
	 * event point series : list of Event
	 */
	protected LinkedList<Event> events;
	protected Comparator evtComp;

	protected EventTaskStructure[] task_evts;

	/**
	 * data structures of the sweep algorithm
	 * sweep line status
	 */
	protected int sum_height;
	protected IntList taskToPrune;

	/**
	 * starting and ending variables
	 */
	protected IntDomainVar[] starts, ends;


	private static IntDomainVar[] createAllVarsArray(IntDomainVar[] starts,
			IntDomainVar[] ends,
			IntDomainVar[] duration,
			IntDomainVar[] heights) {
		int n = starts.length;
		IntDomainVar[] ret = new IntDomainVar[4 * n];
		for (int i = 0; i < n; i++) {
			ret[4 * i] = starts[i];
			ret[4 * i + 1] = ends[i];
			ret[4 * i + 2] = duration[i];
			ret[4 * i + 3] = heights[i];
		}
		return ret;
	}

	public Cumulative(IntDomainVar[] starts, IntDomainVar[] ends, IntDomainVar[] duration, IntDomainVar[] heights, int Capa) {
		super(createAllVarsArray(starts, ends, duration, heights));
		nbTask = starts.length;
		Xtasks = new ArrayList<Integer>();
		Ytasks = new ArrayList<Integer>();

		for (int i = 0; i < nbTask; i++) {
			Xtasks.add(i);
			Ytasks.add(i);
		}
		taskToPrune = new IntList(nbTask);
		taskToPrune.reInit();
		contributions = new int[nbTask];
		taskheights = new Consumption[nbTask];
		this.starts = starts;
		this.ends = ends;
		capaMax = Capa;
		isDurationConstant = isDurationConstant(duration);
		isHeightConstant = isHeightConstant(heights);
		initEvts();
	}

	public boolean isHeightConstant(IntDomainVar[] heights) {
		for (int i = 0; i < heights.length; i++) {
			if (!heights[i].isInstantiated()) {
				return false;
			}
		}
		return true;
	}

	public boolean isDurationConstant(IntDomainVar[] durations) {
		for (int i = 0; i < durations.length; i++) {
			if (!durations[i].isInstantiated()) {
				return false;
			}
		}
		return true;
	}

	public void initEvts() {
		events = new LinkedList<Event>();
		evtComp = new EventComparator();
		stComp = new StartingDateComparator();
		rev_stComp = new RevStartingDateComparator();
		endComp = new EndingDateComparator();
		rev_endComp = new RevEndingDateComparator();
		task_evts = new EventTaskStructure[nbTask];
		for (int i = 0; i < nbTask; i++) {
			task_evts[i] = new EventTaskStructure(i);
		}
	}

	public IntDomainVar getStart(int i) {
		return vars[i * 4];
	}

	public IntDomainVar getEnd(int i) {
		return vars[i * 4 + 1];
	}

	public IntDomainVar getDuration(int i) {
		return vars[i * 4 + 2];
	}

	public IntDomainVar getVHeight(int i) {
		return vars[i * 4 + 3];
	}

	@Override
	public void awakeOnBounds(int varIndex) throws ContradictionException {
		this.constAwake(false);
	}

	@Override
	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		//<hca> nothing to do here isn'it ?
		//this.constAwake(false);
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		this.constAwake(false);
	}

	@Override
	public void awake() throws ContradictionException {
		energy = new long[nbTask];
		if (isHeightConstant) {
			initializeEdgeFindingData();
			if (isDurationConstant) {
				initializeEnergy();
			}
		}
		thetatree = new CumTreeT(this,AbstractTree.ECT_TREE, capaMax);
		filter();
	}

	@Override
	public void propagate() throws ContradictionException {
		filter();
	}

	public boolean isSatisfied(int[] tuple) {
		int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
		for(int t = 0; t < nbTask; t++) {
			start = Math.min(start,  tuple[t * 4]);//this.getStart(t).getVal());
			end = Math.max(end,  tuple[t * 4 + 1]);// this.getEnd(t).getVal());
		}
		int[] load = new int[end - start];
		for(int t = 0; t < nbTask; t++) {
			for(int i = this.getStart(t).getVal(); i < this.getEnd(t).getVal(); i++) {
				load[i - start] += tuple[i * 4 + 3];//this.getVHeight(t).getVal();
			}
		}
		for(int i = start; i <= end; i++) {
			if (load[i] > this.capaMax) {
				return false;
			}
		}
		return true;
	}


	@Override
	public Boolean isEntailed() {
		throw new Error("isEntailed not yet implemented on dev.i_want_to_use_this_old_version_of_choco.global.scheduling.Cumulative");
	}

	@Override
	public String pretty() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cumulative({");
		for(int t = 0; t < this.nbTask; t++) {
			if (t > 0) {
				sb.append(", ");
			}
			sb.append("Task(s=").append(this.getStart(t).pretty()).append(", d=").append(this.getDuration(t).pretty()).
			append(", e=").append(this.getEnd(t).pretty()).append(", h=").append(this.getVHeight(t).pretty()).append(")");
		}
		sb.append("},").append(this.capaMax).append(")");
		return sb.toString();
	}

	public boolean isScheduled(int i) {
		return getStart(i).isInstantiated() && getEnd(i).isInstantiated() && getDuration(i).isInstantiated() && getVHeight(i).isInstantiated();
	}

	//****************************************************************//
	//********* Interface ITasksSet***********************************//
	//****************************************************************//

	/**
	 * return earliest start of task i
	 *
	 * @param i
	 */
	//@Override
	public int getEST(int i) {
		return getStart(i).getInf();
	}

	/**
	 * return latest start of task i
	 *
	 * @param i
	 */
	//@Override
	public int getLST(int i) {
		return getStart(i).getSup();
	}

	/**
	 * return earliest end of task i
	 *
	 * @param i
	 */
	//@Override
	public int getECT(int i) {
		return getEnd(i).getInf();
	}

	/**
	 * return latest end of task i
	 *
	 * @param i
	 */
	//@Override
	public int getLCT(int i) {
		return getEnd(i).getSup();
	}


	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getNbTasks()
	 */
	//@Override
	public int getNbTasks() {
		return nbTask;
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getProcessingTime(int)
	 */
	//@Override
	public int getProcessingTime(int i) {
		// TODO Compléter interface
		return 0;
	}


	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getConsumption(int)
	 */
	//@Override
	public long getConsumption(int i) {
		return getDuration(i).getInf() * getVHeight(i).getInf();
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getTotalLoad()
	 */
	//@Override
	public int getTotalLoad() {
		// TODO Compléter interface
		return 0;
	}

	//****************************************************************//



	// todo : maintenir incrementalement la liste des taches qui ont une partie obligatoire ?
	// todo : maintenir la liste des taches non instancies dans le profile ?
	public boolean generateEvents() {
		events.clear();
		boolean someprof = false;
		for (int i = 0; i < nbTask; i++) {
			if (getStart(i).getSup() < getEnd(i).getInf()) { // t has a compulsory part
				final int h = getVHeight(i).getInf();
				task_evts[i].setStartEvt(getStart(i).getSup(), h);
				task_evts[i].setEndEvt(getEnd(i).getInf(), -h);
				events.add(task_evts[i].sevt);
				events.add(task_evts[i].endevt);
				someprof = true;
			}
			if (!isScheduled(i)) {
				task_evts[i].setPruningEvt(getStart(i).getInf(), 0);
				events.add(task_evts[i].pruneevt);
			}
		}
		return someprof;
	}

	protected int[] contributions;
	protected boolean fixPoint;

	public void initMainIteration() {
		fixPoint = false;
		taskToPrune.reInit();
	}

	/**
	 * Main loop to achieve the fix point over the
	 * sweep and edge-finding algorithms
	 *
	 * @throws ContradictionException
	 */
	public void filter() throws ContradictionException {
		updateCompulsoryPart();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("========= Filtering on resource" + this + "========");
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Initial state for resource ");
			for (int i = 0; i < nbTask; i++) {
				logger.fine("start=" + getStart(i).pretty() + "; duration=" + getDuration(i).pretty() + "; end=" + getEnd(i).pretty() + "; height=" + getVHeight(i).pretty());
			}
		}

		fixPoint = true;
		while (fixPoint) {  // apply the sweep process until saturation
			initMainIteration();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("------ Start sweep for resource" + this + "========");
			}
			sweep();
			if (SchedulingSettings.cumulative_taskInterval) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("------ Energetic for resource" + this + "========");
				}
				//initial sorting of the tasks
				Collections.sort(Xtasks, stComp);
				Collections.sort(Ytasks, endComp);
				//1-) Ensure first E-feasability, also called overload checking (Vilim)
				taskIntervals();
				//2-) then compute the set different heights dynamically
				if (SchedulingSettings.cumulative_edgeFinding) {
					if (!isHeightConstant) {
						initializeEdgeFindingData();
					} else {
						reinitConsumption();
					}

					//3-) Prune the starting dates with edge finding rule
					if (SchedulingSettings.cumulative_edgefindingAlgo == 0) {
						fixPoint |= vilimEF_start();   // in O(n^2 \times k)
					} else if (SchedulingSettings.cumulative_edgefindingAlgo == 1) {
						fixPoint |= calcEF_start();    // in O(n^2 \times k)
					}

					//4-) reset the flags of dynamic computation
					reinitConsumption();

					//5-) Prune ending dates with edge finding rule
					Collections.sort(Xtasks, rev_endComp);
					Collections.sort(Ytasks, rev_stComp);
					if (SchedulingSettings.cumulative_edgefindingAlgo == 0) {
						fixPoint |= vilimEF_end();    //O(n^2 \times k)
					} else if (SchedulingSettings.cumulative_edgefindingAlgo == 1) {
						fixPoint |= calcEF_end();    // in O(n^2 \times k)
					}


					if (logger.isLoggable(Level.FINE)) {
						logger.fine("------ Energetic  filtering done" + this + " =========");
					}
				}
			}
		}
	}

	/**
	 * Compute the compulsory part of each task by enforcing all equations :
	 * end = start + duration
	 *
	 * @throws ContradictionException
	 */
	public void updateCompulsoryPart() throws ContradictionException {
		for (int i = 0; i < nbTask; i++) {
			updateCompulsoryPartTask(i);
		}
	}

	public void updateCompulsoryPartTask(int i) throws ContradictionException {
		fixPoint = true;
		while (fixPoint) {
			fixPoint = false;
			IntDomainVar s = getStart(i);
			IntDomainVar e = getEnd(i);
			IntDomainVar d = getDuration(i);
			fixPoint |= s.updateInf(e.getInf() - d.getSup(), cIndices[4 * i]);
			fixPoint |= s.updateSup(e.getSup() - d.getInf(), cIndices[4 * i]);
			fixPoint |= e.updateInf(s.getInf() + d.getInf(), cIndices[4 * i + 1]);
			fixPoint |= e.updateSup(s.getSup() + d.getSup(), cIndices[4 * i + 1]);
			fixPoint |= d.updateInf(e.getInf() - s.getSup(), cIndices[4 * i + 2]);
			fixPoint |= d.updateSup(e.getSup() - s.getInf(), cIndices[4 * i + 2]);
		}
	}

	/**
	 * Build to cumulative profile and achieve the pruning  regarding this
	 * profile.
	 *
	 * @throws ContradictionException
	 */
	public void sweep() throws ContradictionException {
		if (generateEvents()) { // events are start/end of mandatory parts (CHECKPROF event) and start of tasks (PRUNING events)
			Collections.sort(events, evtComp);  // sort event by date
			for (int i = 0; i < contributions.length; i++) {
				contributions[i] = 0;
			}
			sum_height = 0;
			int d = events.getFirst().getDate(); // get first date
			Iterator it = events.iterator(); // about to iterate on events
			while (it.hasNext()) {
				Event evt = (Event) it.next();  // get next event
				//----- profile event
				if (evt.type != Event.PRUNING) {
					if (d != evt.date) { // if event of a different date it means that all profile events <= d have been taken into account
						if (sum_height > capaMax) {
							this.fail();
						}
						prune(d, evt.date - 1); // caution: prune is called on non-pruning events !
						d = evt.date; // register new date
					}
					if (evt.type == Event.CHECKPROF) { // if part of the profile  (this currentElement will always be true since type!=pruning
						sum_height += evt.prof_increment; // consuming a certain quantity
						contributions[evt.task] += evt.prof_increment; // update contribution too
					} else {
						throw new Error("" + evt.type + " should not be used");
					}
				}
				//----- pruning event
				else {
					taskToPrune.add(evt.task); // if not a pruning event then add the new task to the list of "active" tasks (taskToPrune is decreased in the prune method ?)
				}
			}
			if (sum_height > capaMax) {
				this.fail();
			}
			prune(d, d); // <hca> hum c'est quoi ca ?
			// Insert the last pruning phase
		}
	}


	public void prune(int low, int up) throws ContradictionException {
		IntIterator it = taskToPrune.iterator();
		while (it.hasNext()) { // prune all task that intersect with the current time
			int idx = it.next();
			IntDomainVar s = getStart(idx);
			IntDomainVar e = getEnd(idx);
			IntDomainVar d = getDuration(idx);
			IntDomainVar h = getVHeight(idx);
			int height = h.getInf();
			// we remove contribution of task v and imagine that if overlaps some date between low and up (plateau of the current profile)
			if (sum_height - contributions[idx] + height > capaMax) { // exclure celles qui overlap for sure
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("START PRUNING ON TASK " + idx + " BETWEEN [" + low + "," + up + "]");
				}
				// call removeInterval on start since some starting time are not possible anymore
				fixPoint |= s.removeInterval(low - d.getInf() + 1, up, cIndices[idx * 4]);
				//TODO: this second removeInterval is only relevant if a duration variable exists
				fixPoint |= e.removeInterval(low + 1, up + d.getInf(), cIndices[idx * 4 + 1]);
				int maxd = Math.max(Math.max(low - s.getInf(), 0), e.getSup() - up - 1);
				fixPoint |= d.updateSup(maxd, cIndices[idx * 4 + 2]); // t is either to the left or to the right of this interval -> it has an impact on the duration !
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("END PRUNING ON " + idx);
				}
				//Mise � jour de TaskToPrune: on retire les taches telles que t.end().sup() < date
				if (e.getSup() <= up + 1) {
					it.remove();
				}
			}
			// prune the height of tasks that overlap for sure
			if (e.getInf() > low && s.getSup() <= up && d.getInf() > 0) {
				fixPoint |= h.updateSup(capaMax - (sum_height - contributions[idx]), cIndices[idx * 4 + 3]);
			}
			//inclure ici le pruning sur la hauteur pour les taches qui sont dans [low,up]
		}
	}

	//****************************************************************//
	//********* Tasks Intervals **************************************//
	//********* - version n^2 ****************************************//
	//********* - version nlog(n) with a theta-tree ******************//
	//****************************************************************//

	//store the nergy of each task once to avoid redondant computations of energies
	protected long[] energy;

	protected ArrayList<Integer> Xtasks;
	protected Comparator stComp; // tasks sorted by increasing starting date
	protected Comparator rev_stComp;


	protected ArrayList<Integer> Ytasks;
	protected Comparator endComp; // tasks sorted by increasing ending date
	protected Comparator rev_endComp;

	protected CumTreeT thetatree;

	public void initializeEnergy() {
		energy = new long[nbTask];
		for (int i = 0; i < nbTask; i++) {
			energy[i] = getDuration(i).getInf() * getVHeight(i).getInf();
		}
	}

	/**
	 * fast task intervals in n*log(n)
	 */
	public void taskIntervals() throws ContradictionException {
		//sort in ascending order of lct_i
		//thetatree.initializeLeaves();
		if (!(isHeightConstant && isDurationConstant)) {
			initializeEnergy();
		}
		//thetatree.reset(); <hca> todo: the reset should resort the leaves...
		thetatree = new CumTreeT(this,AbstractTree.ECT_TREE, capaMax);
		for (int i : Ytasks) {
			thetatree.insert(i);
			if (thetatree.getEnergy() > capaMax * getLCT(i)) {
				this.fail();
			}
		}
	}

	/**
	 * a basic n^2 tasks interval
	 */
	public void slowTaskIntervals() throws ContradictionException {
		if (!(isHeightConstant && isDurationConstant)) {
			initializeEnergy();
		}
		for (int i = 0; i < nbTask; i++) {
			//Y[i].sup is the right bound of the interval
			int D = getEnd(Ytasks.get(i)).getSup(); // D is the end of this interval
			long tote = 0; // energy
			for (int j = nbTask - 1; j >= 0; j--) {
				int t = Xtasks.get(j);
				// we consider interval  (X[j].inf .... Y[i].sup)
				//NB: if not fully included in this interval it may have a mandatory part in this interval anyway ?
				//if (getLE(t) <= D) { // if this task is fully included in this interval
				int h = getVHeight(t).getInf();
				//int minDur = getDuration(t).getInf();
				long e = energy[t];//minDur * h;
				if (getLCT(t) > D) {
					e = Math.min(e, (D - getLST(t)) * h); // if task can end after D, part (or all) of it can be outside
				}
				if (e > 0) { // e<=0 means that task can be fully outside
					tote += e;
					long diff = D - getEST(t);		   //avoid integer overload
					long capaMaxDiff = capaMax * diff;
					if (capaMaxDiff < tote) {
						this.fail();
					}
				}
				//}
			}
		}
	}

	//***************************************************************//
	//********* Edge finding ****************************************//
	//***************************************************************//

	/**
	 * The different ressource consumptions of all tasks.
	 * Sc.lenght <= nbTask
	 * It can varies over time with variable consumption (or heights) !
	 */
	protected Consumption[] Sc;

	/**
	 * Reference to the consumption object related to each task
	 */
	protected Consumption[] taskheights;

	/**
	 * temporary data for edge finding (initialized by dynprog)
	 * to store the inner maximization of the edge finding bound on the start/end variables of each task.
	 */
	protected long[][] R;

	/**
	 * A class to manipulate the different consumption
	 * of tasks and their indexes in R
	 */
	private class Consumption {
		// denote a height or consumption of a task and its index in R
		public int h, idx;

		// Tells the algorithm that the R values have already been computed for
		// this height/consumption (for lazy computation purposes)
		public boolean dyncomputation;

		public Consumption(int h, int idx) {
			this.h = h;
			this.idx = idx;
			dyncomputation = false;
		}
	}

	/**
	 * reset all the flags for dynamic computation of R
	 */
	public void reinitConsumption() {
		for (int i = 0; i < Sc.length; i++) {
			Sc[i].dyncomputation = false;
		}
	}

	/**
	 * Initialize some data structure for the edge finding.
	 * If the height are constant, this is done only once
	 * at the beginning, otherwise it has to be recomputed at each call.
	 * Shall we maintain it incrementally ?
	 */
	public void initializeEdgeFindingData() {
		HashMap map = new HashMap();
		//extract all different heights among all the tasks
		int cpt = 0;
		for (int j = 0; j < nbTask; j++) {
			int h = getVHeight(j).getInf();
			Consumption cons = (Consumption) map.get(h);
			if (cons == null) {
				cons = new Consumption(h, cpt);
				map.put(h, cons);
				cpt++;
			}
			taskheights[j] = cons;
		}
		//build the consumption table
		Sc = new Consumption[map.size()];
		Set<Map.Entry<Integer, Consumption>> entries = map.entrySet();
		for (Iterator<Map.Entry<Integer, Consumption>> it = entries.iterator(); it.hasNext();) {
			Consumption cons = it.next().getValue();
			Sc[cons.idx] = cons;
		}
		R = new long[Sc.length][nbTask];
	}

	//*************************************************************//
	//********* Edge finding for updating earliest start **********//
	//********* - version O(n^2k) without theta-lambda-tree *******//
	//********* - version O(n^2k) with    theta-lambda-tree *******//
	//*************************************************************//

	/**
	 * Lazy computation of the inner maximization of
	 * the edge finding. Instead of precumputed the R values, we call this method
	 * for a given consumption
	 * this method assumes that the task intervals have not failed !
	 */
	public void calcR_start(Consumption cons) {
		int c = cons.h;
		int i = cons.idx;
		long[] E = new long[nbTask];
		for (int j = 0; j < nbTask; j++) {
			E[j] = 0;
			R[i][j] = Long.MIN_VALUE;
		}
		for (int j = nbTask - 1; j >= 0; j--) {
			int x = Xtasks.get(j);
			long ex = energy[x];//getHeight(x).getInf() * getDuration(x).getInf();
			for (int k = 0; k < nbTask; k++) {
				int y = Ytasks.get(k);
				if (getLCT(x) <= getLCT(y)) {
					E[y] += ex;
					long rest = E[y] - (capaMax - c) * (getLCT(y) - getEST(x));
					long q1 = R[i][y];
					long q2 = (k == 0) ? Long.MIN_VALUE : R[i][Ytasks.get(k - 1)];
					long q3 = (rest > 0) ? getEST(x) + (long) Math.ceil((double) rest / (double) c) : Long.MIN_VALUE;
					R[i][y] = Math.max(Math.max(q1, q2), q3);
				}
			}
		}
	}

	/**
	 * Edge finding algorithm for starting dates in O(n^2 \times k) where
	 * k is the number of distinct heights.
	 */
	public boolean calcEF_start() throws ContradictionException {
		int[] newSdates = new int[nbTask];
		long[] E = new long[nbTask];
		for (int i = 0; i < nbTask; i++) {
			newSdates[i] = getEST(Xtasks.get(i));
		}
		for (int i = 0; i < nbTask; i++) {
			int y = Ytasks.get(i);
			long ETot = 0;
			for (int j = nbTask - 1; j >= 0; j--) {
				int x = Xtasks.get(j);
				if (getLCT(x) <= getLCT(y)) {
					ETot += energy[x];
				}
				E[j] = ETot;
			}
			long CEF = Long.MIN_VALUE;
			for (int j = 0; j < nbTask; j++) {
				int x = Xtasks.get(j);
				long ex = energy[x];
				//System.out.println("E[" + y + "," + x + "] = " + E[j] + " vs " + capaMax * (getLE(y) - getES(x)));
				CEF = Math.max(CEF, E[j] - capaMax * (getLCT(y) - getEST(x)));
				if (CEF + ex > 0 && getLCT(x) > getLCT(y)) {
					//System.out.println("update " + x + " to " + R[Sc.get(getHeight(x).getInf())][y]);
					if (!taskheights[x].dyncomputation) { //lazy edge finding
						calcR_start(taskheights[x]);
						taskheights[x].dyncomputation = true;
					}
					newSdates[j] = (int) Math.max(newSdates[j], R[taskheights[x].idx][y]);
				}
			}
		}
		boolean modif = false;
		//pruning phase
		for (int i = 0; i < nbTask; i++) {
			int x = Xtasks.get(i);
			if (logger.isLoggable(Level.FINE) && newSdates[i] > getStart(x).getInf()) {
				logger.fine("edge finding update lb of " + getStart(x) + " to " + newSdates[i]);
			}
			boolean change = getStart(x).updateInf(newSdates[i], cIndices[4 * x]);
			modif |= change;
			if (change) {
				updateCompulsoryPartTask(x);
			}
		}
		return modif;
	}

	/**
	 * Edge finding algorithm for starting dates in O(n^2 \times k) where
	 * k is the number of distinct heights. Vilim version based on the theta-
	 * lambda tree.
	 */
	public boolean vilimEF_start() throws ContradictionException {
		throw new Error("Vilim version of edge finding for starting dates remain to be done");
	}

	//*************************************************************//
	//********* Edge finding for updating latestend ***************//
	//********* - version O(n^2k) without theta-lambda-tree *******//
	//********* - version O(n^2k) with    theta-lambda-tree *******//
	//*************************************************************//

	/**
	 * precomputation for the edge finding using dynamic programming
	 * this method assumes that the task intervals have not failed !
	 */
	public void calcR_end(Consumption cons) {
		int c = cons.h;
		int i = cons.idx;
		long[] E = new long[nbTask];
		for (int j = 0; j < nbTask; j++) {
			E[j] = 0;
			R[i][j] = Long.MAX_VALUE;
		}
		for (int j = nbTask - 1; j >= 0; j--) {
			int x = Xtasks.get(j);
			long ex = energy[x];
			for (int k = 0; k < nbTask; k++) {
				int y = Ytasks.get(k);
				if (getEST(x) >= getEST(y)) {
					E[y] += ex;
					long rest = E[y] - (capaMax - c) * (getLCT(x) - getEST(y));
					long q1 = R[i][y];
					long q2 = (k == 0) ? Long.MAX_VALUE : R[i][Ytasks.get(k - 1)];
					long q3 = (rest > 0) ? getLCT(x) - (long) Math.ceil((double) rest / (double) c) : Long.MAX_VALUE;
					R[i][y] = Math.min(Math.min(q1, q2), q3);
				}
			}
		}
	}

	/**
	 * Edge finding algorithm for ending dates in O(n^2 \times k) where
	 * k is the number of distinct heights.
	 */
	public boolean calcEF_end() throws ContradictionException {
		int[] newEdates = new int[nbTask];
		long[] E = new long[nbTask];
		for (int i = 0; i < nbTask; i++) {
			newEdates[i] = getLCT(Xtasks.get(i));
		}
		for (int i = 0; i < nbTask; i++) {
			int y = Ytasks.get(i);
			long ETot = 0;
			for (int j = nbTask - 1; j >= 0; j--) {
				int x = Xtasks.get(j);
				if (getEST(x) >= getEST(y)) {
					ETot += energy[x];
				}
				E[j] = ETot;
			}
			long CEF = Long.MIN_VALUE;
			for (int j = 0; j < nbTask; j++) {
				int x = Xtasks.get(j);
				long ex = energy[x];
				//System.out.println("E[" + y + "," + x + "] = " + E[j] + " vs " + capaMax * (getLE(y) - getES(x)));
				CEF = Math.max(CEF, E[j] - capaMax * (getLCT(x) - getEST(y)));
				if (CEF + ex > 0 && getEST(x) < getEST(y)) {
					//System.out.println("update " + x + " to " + R[Sc.get(getHeight(x).getInf())][y]);
					if (!taskheights[x].dyncomputation) { //lazy edge finding
						calcR_end(taskheights[x]);
						taskheights[x].dyncomputation = true;
					}
					newEdates[j] = (int) Math.min(newEdates[j], R[taskheights[x].idx][y]);
				}
			}
		}
		boolean modif = false;
		//pruning phase
		for (int i = 0; i < nbTask; i++) {
			int x = Xtasks.get(i);
			if (logger.isLoggable(Level.FINE) && newEdates[i] < getEnd(x).getSup()) {
				logger.fine("edge finding update ub of " + getEnd(x) + " to " + newEdates[i]);
			}
			boolean change = getEnd(x).updateSup(newEdates[i], cIndices[4 * x + 1]);
			modif |= change;
			if (change) {
				updateCompulsoryPartTask(x);
			}
		}
		return modif;
	}

	/**
	 * Edge finding algorithm for ending dates in O(n^2 \times k) where
	 * k is the number of distinct heights. Vilim version based on the theta-
	 * lambda tree.
	 */
	public boolean vilimEF_end() throws ContradictionException {
		throw new Error("Vilim version of edge finding for the ending dates remain to be done");
	}

//	***************************************************************//
//	********* Events managment ************************************//
//	***************************************************************//


	protected class EventComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			int date1 = ((Event) o1).getDate();
			int date2 = ((Event) o2).getDate();

			if (date1 < date2) {
				return -1;
			} else if (date1 == date2) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	protected class StartingDateComparator implements Comparator {

		public StartingDateComparator() {
		}

		public int compare(Object o1, Object o2) {
			int date1 = getStart((Integer) o1).getInf();
			int date2 = getStart((Integer) o2).getInf();

			if (date1 < date2) {
				return -1;
			} else if (date1 == date2) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	protected class RevStartingDateComparator implements Comparator {

		public RevStartingDateComparator() {
		}

		public int compare(Object o1, Object o2) {
			int date1 = getStart((Integer) o1).getInf();
			int date2 = getStart((Integer) o2).getInf();

			if (date1 < date2) {
				return 1;
			} else if (date1 == date2) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	protected class EndingDateComparator implements Comparator {

		public EndingDateComparator() {
		}

		public int compare(Object o1, Object o2) {
			int date1 = getEnd((Integer) o1).getSup();
			int date2 = getEnd((Integer) o2).getSup();

			if (date1 < date2) {
				return -1;
			} else if (date1 == date2) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	protected class RevEndingDateComparator implements Comparator {

		public RevEndingDateComparator() {
		}

		public int compare(Object o1, Object o2) {
			int date1 = getEnd((Integer) o1).getSup();
			int date2 = getEnd((Integer) o2).getSup();

			if (date1 < date2) {
				return 1;
			} else if (date1 == date2) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	/**
	 * A Class to preallocate the events needed per tasks
	 */
	protected class EventTaskStructure {
		protected Event sevt;
		protected Event endevt;
		protected Event pruneevt;

		/**
		 * Build the event structure of task i
		 *
		 * @param i
		 */
		public EventTaskStructure(int i) {
			sevt = new Event(Event.CHECKPROF, i, -1, -1);
			endevt = new Event(Event.CHECKPROF, i, -1, -1);
			pruneevt = new Event(Event.PRUNING, i, -1, -1);
		}

		public void setStartEvt(int d, int pinc) {
			sevt.date = d;
			sevt.prof_increment = pinc;
		}

		public void setEndEvt(int d, int pinc) {
			endevt.date = d;
			endevt.prof_increment = pinc;
		}

		public void setPruningEvt(int d, int pinc) {
			pruneevt.date = d;
			pruneevt.prof_increment = pinc;
		}

	}


	protected class Event {
		public final static int CHECK = 0;  // never used
		public final static int PROFILE = 1; // never used
		public final static int PRUNING = 2;
		public final static int CHECKPROF = 3;

		public int type; // among CHECK, PROFILE, CHECKPROFILE and PRUNING
		public int task;
		public int date;
		public int prof_increment;


		public Event(int type, int task, int date, int pinc) {
			this.type = type;
			this.task = task;
			this.date = date;
			this.prof_increment = pinc;
		}

		@Override
		public String toString() {
			String typ = "";
			switch (type) {
			case 0:
				typ = "CHECK  ";
				break;
			case 1:
				typ = "PROFILE";
				break;
			case 2:
				typ = "PRUNING";
				break;
			case 3:
				typ = "CHECK-PROFILE";
				break;
			}
			return "[" + typ + " on task " + task + " at date " + date + " with incH " + prof_increment + "]";
		}

		public int getType() {
			return type;
		}

		public int getTask() {
			return task;
		}

		public int getDate() {
			return date;
		}

		public int getProfIncrement() {
			return prof_increment;
		}

	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getTotalConsumption()
	 */
	//@Override
	public int getTotalConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getHeight(int)
	 */
	//@Override
	public int getHeight(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

}
