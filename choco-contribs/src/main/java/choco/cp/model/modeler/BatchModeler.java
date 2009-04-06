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
package choco.cp.model.modeler;

/**
 *
 * @author Arnaud Malapert</br>
 * @since 7 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public class BatchModeler {

//	protected final int nbBatchs;
//
//	protected final int nbJobs;
//
//	protected final String rscName;
//
//	////////// META MODEL ///////////
//
//	protected UnaryResource batchRsc;
//
//	protected CumulativeResource jobRsc;
//
//	protected final PackModeler pack;
//
//	public final Constraint packCstr;
//
//	///////// VARIABLES /////////////
//
//	protected final int horizon = Integer.MAX_VALUE;
//	
//	protected final IPermutation permutation;
//
//	protected final IntegerConstantVariable capacity;
//
//	protected final IntegerConstantVariable dmin;
//
//	protected final IntegerConstantVariable dmax;
//
//	protected final IntegerConstantVariable[] sizes;
//
//	protected final IntegerConstantVariable[] durations;
//
//	public BatchModeler(String rscName, IntegerConstantVariable[] durations, IntegerConstantVariable[] sizes, IntegerConstantVariable capacity, int maximumNumberOfBatch) {
//		//FIXME REMOVE super(ConstraintType.BATCH);
//		super();
//		this.capacity=capacity;
//		this.nbJobs=durations.length;
//		this.nbBatchs= Math.max(Math.min(nbJobs, maximumNumberOfBatch), 0);
//		this.rscName = rscName;
//		pack = new PackModeler(getRscName(),sizes,nbBatchs,capacity.getValue());
//		packCstr = pack(pack);
//		this.permutation = pack.permutation;
//		this.durations=ChocoUtil.applyPermutation(permutation, durations);
//		this.sizes=pack.sizes;
//		//create other variables
//		List<IntegerConstantVariable> dl = Arrays.asList(durations);
//		this.dmin=Collections.min(dl);
//		this.dmax=Collections.max(dl);
//
//	}
//
//
//	public final String getRscName() {
//		return rscName;
//	}
//
//	public final void initializeTasks() {
//		initializeBatchs();
//		initializeJobs();
//	}
//
//	protected void initializeBatchs() {
//		batchRsc = Scheduling.makeUnaryResource("unary-"+getRscName());
//		for (int i = 0; i < nbBatchs; i++) {
//			String name = "batch-"+this.getRscName()+"-"+i;
//			IntegerVariable d = makeIntVar("duration-"+name, dmin.getValue(), dmax.getValue(),"cp:bound");
//			batchRsc.addTask(factory.makeTaskVar(name, d));
//		}
//	}
//
//	protected void initializeJobs() {
//		jobRsc = Scheduling.makeCumulativeResource("cumulative-"+getRscName(),capacity);
//		for (int i = 0; i < nbJobs; i++) {
//			String name = "job-"+this.getRscName()+"-"+i;
//			jobRsc.addTask(Choco.makeTaskVar(name, horizon, durations[i]),sizes[i]);
//		}
//	}
//
//
//
//	public final void addConstraints(CPModel model) {
////		model.addConstraints(
////				packCstr,
////				batchRsc,
////				jobRsc
////				);
//		addJobAssignmentConstraints(model);
//
//	}
//	public final void addBatchDurationConstraints(CPModel model) {
//		//ajouter une dummy duration quand le batch est vide
//	}
//
//	public final void addJobAssignmentConstraints(CPModel model) {
//		IntegerVariable[] s =new IntegerVariable[nbBatchs];
//		IntegerVariable[] e =new IntegerVariable[nbBatchs];
//		IntegerVariable[] d =new IntegerVariable[nbBatchs];
//		for (int i = 0; i < nbBatchs; i++) {
//			s[i] = batchRsc.getTask(i).start();
//			d[i] = batchRsc.getTask(i).duration();
//			e[i] = batchRsc.getTask(i).end();
//		}
//		for (int i = 0; i < nbJobs; i++) {
//			//QUESTION est-ce qu'il ne vaudrait pas mieux poser un channeling sur start et end ?
//			model.addConstraints(
//					nth(pack.bins[i], s, jobRsc.getTask(i).start()),
//					nth(pack.bins[i], d, jobRsc.getTask(i).duration()),
//					nth(pack.bins[i], e, jobRsc.getTask(i).end()) //redondant, utile ?
//					);
//		}
//	}
//
//	public final TaskVariable getJob(int job) {
//		return jobRsc.getTask(job);
//	}
//
//	public final IntegerConstantVariable getDuration(int job) {
//		return durations[job];
//	}
//
//	public final IntegerConstantVariable getSize(int job) {
//		return sizes[job];
//	}
//
//
//
//	public final TaskVariable getBatch(int batch) {
//		return batchRsc.getTask(batch);
//	}
//
//	public final SetVariable getBatchSet(int batch) {
//		return pack.itemSets[batch];
//	}

	

}
