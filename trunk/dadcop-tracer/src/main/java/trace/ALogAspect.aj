package trace;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.aspectj.lang.JoinPoint;

import choco.kernel.solver.Solver;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.search.ISolutionPool;
import choco.cp.solver.constraints.ConstantSConstraint;

public abstract aspect ALogAspect {
	
	protected static StringBuffer STR = new StringBuffer();
	
	// ___ Event attribute configuration ___
	public static final int ATTRIBUTE_CHRONO = 0;
	public static final int ATTRIBUTE_DEPTH = 1;
	public static final int ATTRIBUTE_TIME = 2;
	public static final int ATTRIBUTE_LINE = 3;
	public static final int ATTRIBUTE_FILE = 4;
	public static final int ATTRIBUTE_NB = 5;
	public static final String[] ATTRIBUTE_NAMES = new String[]{
			"chrono", "depth", "time", "line", "file"
	};
	protected static boolean[] attributeConfig = new boolean[]{
			true, // Chrono
			true, // Depth
			false, // Time
			false, // Line
			false // File
	};
	
	// ___ Event switch configuration ___
	public static final int EVENT_NEWVAR = 0;
	public static final int EVENT_NEWCST = 1;
	public static final int EVENT_CHOICEPT = 2;
	public static final int EVENT_BACKTO = 3;
	public static final int EVENT_SOLUTION = 4;
	public static final int EVENT_FAILURE = 5;
	public static final int EVENT_REDUCE = 6;
	public static final int EVENT_RESTORE = 7;
	public static final int EVENT_EXPLAIN = 8;
	public static final int EVENT_POST = 9;
	public static final int EVENT_REMOVE = 10;
	public static final int EVENT_CONTEXT = 11;
	public static final int EVENT_AWAKE = 12;
	public static final int EVENT_NB = 13;
	public static final String[] EVENT_NAMES = new String[]{
			"newvar", "newcst", "choicept", "backto", "solution", "failure",
			"reduce", "restore", "explain", "post", "remove", "context", "awake"
	};
	protected static boolean[] eventConfig = new boolean[]{
			true, true, true, true, true, true,
			true, false, false, true, false, false, true
	};
	
	// Trace output inf a file ?
	protected boolean outputInFile = false;
	
	// Configuration of the tracer
	protected Properties traceConfig = new Properties();
	
	// Print stream where Trace must be generated
	protected PrintStream stream;
	
	// Current indentation level
	protected int indent;
	// PreAffected indentation spaces
	protected final String[] indents = new String[]{"", " ", "  ", "   ", "    ",
			"     ", "      ", "       ", "        "};
	
	// Program name
	protected String programName;
	
	// Current solver
	protected Solver currentSolver;
	
	// Trace execution information
	protected int varNb = 0;
	protected int constNb = 0;
	protected int eventNb = 0;
	protected long startTime = System.currentTimeMillis();
	
	// Inter type modifications
	public int IntVar.varNumber = -1;
	public int SConstraint.constNumber = -1;
	
	
	/*************************************************/
	/** 											**/
	/** 				POINTCUTS 					**/
	/** 											**/
	/*************************************************/

	// Program execution
	pointcut programInit() : execution(public static void OADymPPACTracer.init());
	
	// Program execution
	pointcut programClose() : execution(public static void OADymPPACTracer.close());
	

	// Problem creation
	pointcut solverCreation(CPSolver solver) :
		execution(CPSolver+.new(..)) && target(solver);

	// Variable creation
	pointcut variableCreation() : call(IntDomainVar+.new(..)) &&
		!cflow(execution(* Solver+.solve*(..)));

	// User constraint creation
	pointcut constraintCreation() : call(IntSConstraint+.new(..)) && 
		!cflow(execution(* Solver+.solve*(..)));

	// Classical constraint post
	pointcut constraintPost(IntSConstraint c) : execution(void Solver+.post(SConstraint)) &&
		args(c) && !cflow(execution(* Solver+.solve*(..)));

	// Solution storing
	pointcut solution(Solver solver) : 
		execution(* ISolutionPool.recordSolution(Solver)) 
		&& args(solver)
		&& !cflowbelow(execution(* ISolutionPool.recordSolution(..)));

	// Any constraint awake
	pointcut anyAwake() : execution(* IntSConstraint+.awake*(..)) ||
		execution(* IntSConstraint+.propagate(..));

	// Awake on lower bound
	pointcut awakeOnInf(IntSConstraint c, int idx) :
		!cflowbelow(anyAwake()) && 
		execution(* IntSConstraint+.awakeOnInf(..)) &&
		target(c) && args(idx);

	// Awake on upper bound
	pointcut awakeOnSup(IntSConstraint c, int idx) :
		!cflowbelow(anyAwake()) &&
		execution(* IntSConstraint+.awakeOnSup(..)) && 
		target(c) && args(idx);

	// Awake on both bounds
	pointcut awakeOnBounds(IntSConstraint c, int idx) :
		!cflowbelow(anyAwake()) &&
		execution(* IntSConstraint+.awakeOnBounds(..)) &&
		target(c) && args(idx);

	// Awake on instantiation
	pointcut awakeOnInst(IntSConstraint c, int idx) :
		!cflowbelow(anyAwake()) &&
		execution(* IntSConstraint+.awakeOnInst(..)) &&
		target(c) && args(idx);

	// Awake on removals
	pointcut awakeOnRemovals(IntSConstraint c, int idx) :
		!cflowbelow(anyAwake()) &&
		execution(* IntSConstraint+.awakeOnRemovals(..)) &&
		target(c) && args(idx, *);

	// Awake or propagate
	pointcut awakeOrPropagate(IntSConstraint c) :
		!cflowbelow(anyAwake()) && target(c) &&
		(execution(* IntSConstraint+.awake(..)) || execution(* IntSConstraint+.propagate(..)));

	/*************************************************/
	/** 											**/
	/** 				ADVICES 					**/
	/** 											**/
	/*************************************************/
	
	// Program start
	after() : programInit() {
		init(thisJoinPointStaticPart);
		stream.println("<!DOCTYPE gentra4cp SYSTEM " +
				"\"http://contraintes.inria.fr/OADymPPaC/Public/Trace/gentra4cp.2.1.dtd\">");
		elementOpening("<gentra4cp>");
		header();
		provide();
	}
	
	// Program end
	after() : programClose() {
		elementClosing("</gentra4cp>");
		stream.flush();
	}
	
	// Problem creation
	after(CPSolver solver) returning: solverCreation(solver) {	
		currentSolver = solver;
	}
	
	// Variable creation   *********** NEW VAR ***********
	after() returning(IntDomainVar var): variableCreation() {
		var.varNumber = varNb++;
		elementOpening("<new-variable " + eventAttributes(thisJoinPointStaticPart) +
				" vident=\"v" + var.varNumber + "\" vname=\"" + var + "\">");
		elementOpening("<vardomain min=\"" + var.getInf() + "\" max=\"" + 
				var.getSup() + "\" size=\"" + var.getDomainSize() + "\">");
		if (!var.hasEnumeratedDomain() || var.getSup() - var.getInf() + 1 == 
			var.getDomainSize()) {
			singleElementLn("<range from=\"" + var.getInf() + "\" to=\"" + var.getSup() + "\"/>");
		} else {
			STR.setLength(0);
			STR.append("<values>");
			DisposableIntIterator iter = var.getDomain().getIterator();
			for(; iter.hasNext();) {
				int val = iter.next();
				STR.append(val + " ");
			}
			iter.dispose();
			STR.append("</values>");
			singleElementLn(STR.toString());
		}
		elementClosing("</vardomain>");
		elementClosing("</new-variable>");
	}
	
	// Constraint creation   *********** NEW CST ***********
	after() returning(IntSConstraint c): constraintCreation() {
		if(!ConstantSConstraint.class.isInstance(c)){		
			c.constNumber = constNb++;
			singleElementLn("<new-constraint " + eventAttributes(thisJoinPointStaticPart) +
					" cident=\"c" + c.constNumber + "\" cexternal=\"" + c + "\" orig=\"user\"/>");
		}
	}
	
	// Constraint post   *********** POST ***********
	after(IntSConstraint c) : constraintPost(c) {
		singleElementLn("<post " + eventAttributes(thisJoinPointStaticPart) +
				" cident=\"c" + c.constNumber + "\"/>");
	}
	
	// Solution storing   *********** SOLUTION ***********
	after(Solver s) : solution(s) {
		elementOpening("<solution " + eventAttributes(thisJoinPointStaticPart) + ">");
		elementOpening("<state>");
		for(int v = 0; v < s.getNbIntVars(); v++) {
			singleElementLn("<variable vident=\"v" + s.getIntVar(v).varNumber +
					"\"><vardomain><values>" + s.getIntVar(v).getVal() +
					"</values></vardomain></variable>");
		}
		elementClosing("</state>");
		elementClosing("</solution>");
	}
	
	//   *********** AWAKE ***********
	// Awake on lower bound   
	before(IntSConstraint cst, int idx) : awakeOnInf(cst, idx) {
		elementOpening("<awake " + eventAttributes(thisJoinPointStaticPart) +
				" cident=\"c" + cst.constNumber + "\">");
		singleElementLn("<update vident=\"v" + ((IntVar)cst.getVar(idx)).varNumber + 
				"\" types=\"min\"/>");
		elementClosing("</awake>");
	}
	
	// Awake on upper bound
	before(IntSConstraint cst, int idx) : awakeOnSup(cst, idx) {
		elementOpening("<awake " + eventAttributes(thisJoinPointStaticPart) +
				" cident=\"c" + cst.constNumber + "\">");
		singleElementLn("<update vident=\"v" + ((IntVar)cst.getVar(idx)).varNumber + 
				"\" types=\"max\"/>");
		elementClosing("</awake>");
	}
	
	// Awake on both bounds
	before(IntSConstraint cst, int idx) : awakeOnBounds(cst, idx) {
		elementOpening("<awake " + eventAttributes(thisJoinPointStaticPart) +
				" cident=\"c" + cst.constNumber + "\">");
		singleElementLn("<update vident=\"v" + ((IntVar)cst.getVar(idx)).varNumber + 
				"\" types=\"minmax\"/>");
		elementClosing("</awake>");
	}
	
	// Awake on instantiation
	before(IntSConstraint cst, int idx) : awakeOnInst(cst, idx) {
		elementOpening("<awake " + eventAttributes(thisJoinPointStaticPart) +
				" cident=\"c" + cst.constNumber + "\">");
		singleElementLn("<update vident=\"v" + ((IntVar)cst.getVar(idx)).varNumber + 
				"\" types=\"ground\"/>");
		elementClosing("</awake>");
	}
	
	// Awake on removals
	before(IntSConstraint cst, int idx) : awakeOnRemovals(cst, idx) {
		elementOpening("<awake " + eventAttributes(thisJoinPointStaticPart) +
				" cident=\"c" + cst.constNumber + "\">");
		singleElementLn("<update vident=\"v" + ((IntVar)cst.getVar(idx)).varNumber + 
				"\" types=\"val\"/>");
		elementClosing("</awake>");
	}
	
	// Awake on constraint
	before(IntSConstraint cst) : awakeOrPropagate(cst) {
		elementOpening("<awake " + eventAttributes(thisJoinPointStaticPart) +
				" cident=\"c" + cst.constNumber + "\">");
		elementClosing("</awake>");
	}
	
	/*************************************************/
	/** 											**/
	/** 				TOOLS	 					**/
	/** 											**/
	/*************************************************/
	
	// Stream printing
	public void elementOpening(String str) {
		try{
			int realInd = Math.min(indent, indents.length - 1);
			stream.println(indents[realInd] + str);
			indent++;
		}catch(NullPointerException e){
			System.err.println("Be sure your code starts with:\nOADymPPACTracer.init();" +
			"\nand ends with:\nOADymPPACTracer.close();");
			throw e;	
		}
	}
	
	public void singleElementLn(String str) {
		int realInd = Math.min(indent, indents.length - 1);
		stream.println(indents[realInd] + str);
	}
	
	public void singleElement(String str) {
		int realInd = Math.min(indent, indents.length - 1);
		stream.print(indents[realInd] + str);
	}
	
	public void elementClosing(String str) {
		indent--;
		int realInd = Math.min(indent, indents.length - 1);
		stream.println(indents[realInd] + str);
	}
	
	// Initialization
	protected void init(JoinPoint.StaticPart jp) {
		String value;
		String name = "TRACE_";
		// Configuration update
		try {
			InputStream is = getClass().getResourceAsStream("/trace.properties");
			traceConfig.load(is);
		} catch(IOException e) {}
		for(int i = 0; i < ATTRIBUTE_NB; i++) {
			value = (String) traceConfig.get("attribute." + ATTRIBUTE_NAMES[i]);
			if (value != null) {
				attributeConfig[i] = Boolean.valueOf(value).booleanValue();
			}
		}
		value = (String)traceConfig.getProperty("output.file");
		if (Boolean.valueOf(value).booleanValue()){
			outputInFile = true;
			value = (String) traceConfig.getProperty("output.file.prefixe");
			if(value!=null){
				name = value;
			}
		}
		
		for(int i = 0; i < EVENT_NB; i++) {
			value = (String) traceConfig.get("event." + EVENT_NAMES[i]);
			if (value != null) {
				eventConfig[i] = Boolean.valueOf(value).booleanValue();
			}
		}
		// Program name
		programName = jp.getSourceLocation().getFileName();
		// Stream creation
		if (outputInFile) {
			try {
				File file = File.createTempFile(name, ".xml");
				System.out.println("File created -> "+file.getAbsolutePath());
				OutputStream tmp = new FileOutputStream(file);
				OutputStream tmp2 = new BufferedOutputStream(tmp);
				stream = new PrintStream(tmp2);
			} catch(Exception e) {
				System.err.println("File creation failed -> display on default output.");
				stream = System.out;
			}
		} else {
			stream = System.out;
		}
	}
	
	// Header
	protected void header() {
		elementOpening("<header>");
		Date today = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		singleElementLn("<date>" + format.format(today) + "</date>");
		singleElementLn("<source>" + programName + "</source>");
		singleElementLn("<creator>Guillaume Rochart -- EMN/LINA</creator>");
		singleElementLn("<contributor>Charles Prud'homme -- EMN</contributor>");
		singleElementLn("<rights>BSD license</rights>");
		singleElementLn("<solver>CHOCO</solver>");
		elementClosing("</header>");
	}
	
	// Provide
	protected void provide() {	
		String eventAttributes = "" + 
			(attributeConfig[ATTRIBUTE_CHRONO]?"chrono=\"\" ":"") +
			(attributeConfig[ATTRIBUTE_DEPTH]?"depth=\"\" ":"") +
			(attributeConfig[ATTRIBUTE_TIME]?"time=\"\" ":"") +
			(attributeConfig[ATTRIBUTE_LINE]?"line=\"\" ":"") +
			(attributeConfig[ATTRIBUTE_FILE]?"file=\"\" ":"");
		elementOpening("<provide>");
		if (eventConfig[EVENT_NEWVAR]) {
			elementOpening("<new-variable " + eventAttributes + "vident=\"\" vname=\"\">");
			elementOpening("<vardomain min=\"\" max=\"\" size=\"\">");
			singleElementLn("<values/> <range from=\"\" to=\"\"/>");
			elementClosing("</vardomain>");
			elementClosing("</new-variable>");
		}
		if (eventConfig[EVENT_NEWCST]) {
			singleElementLn("<new-constraint " + eventAttributes + "cident=\"\" cexternal=\"\" orig=\"\"/>");
		}
		if (eventConfig[EVENT_CHOICEPT]) {
			singleElementLn("<choice-point " + eventAttributes + "/>");
		}
		if (eventConfig[EVENT_BACKTO]) {
			singleElementLn("<back-to " + eventAttributes + "/>");
		}
		if (eventConfig[EVENT_SOLUTION]) {
			elementOpening("<solution " + eventAttributes + ">");
			elementOpening("<state>");
			singleElementLn("<variable vident=\"\"><vardomain><values/></vardomain></variable>");
			elementClosing("</state>");
			elementClosing("</solution>");
		}
		if (eventConfig[EVENT_FAILURE]) {
			singleElementLn("<failure " + eventAttributes + "/>");
		}
		if (eventConfig[EVENT_AWAKE]) {
			elementOpening("<awake " + eventAttributes + "cident=\"\">");
			singleElementLn("<update vident=\"\" types=\"\"/>");
			elementClosing("</awake>");
		}
		if (eventConfig[EVENT_REDUCE]) {
			elementOpening("<reduce " + eventAttributes + "cident=\"\" vident=\"\">");
			singleElementLn("<delta><values/><range from=\"\" to=\"\"/></delta>");
			singleElementLn("<update vident=\"\" types=\"\"/>");
			if (eventConfig[EVENT_EXPLAIN]) {
				elementOpening("<explanation>");
				singleElementLn("<range from=\"\" to=\"\"/>");
				singleElementLn("<constraints cidents=\"\"/>");
				elementClosing("</explanation>");
			}
			elementClosing("</reduce>");
		}
		if (eventConfig[EVENT_RESTORE]) {
			elementOpening("<restore " + eventAttributes + ">");
			singleElementLn("<delta><values/><range from=\"\" to=\"\"/></delta>");
			elementClosing("</restore>");
		}
		if (eventConfig[EVENT_POST]) {
			singleElementLn("<post " + eventAttributes + "cident=\"\"" +
					(eventConfig[EVENT_CONTEXT]?" context=\"\"":"") + "/>");
		}
		if (eventConfig[EVENT_REMOVE]) {
			singleElementLn("<remove " + eventAttributes + "cident=\"\"" +
					(eventConfig[EVENT_CONTEXT]?" context=\"\"":"") + "/>");
		}
		elementClosing("</provide>");
	}

	// All event attributes w.r.t. attribute configuration
	public String eventAttributes(JoinPoint.StaticPart jp) {
		STR.setLength(0);
		if (attributeConfig[ATTRIBUTE_CHRONO]) STR.append("chrono=\"" + (eventNb++) + "\" ");
		if (attributeConfig[ATTRIBUTE_DEPTH]) STR.append("depth=\"" + getCurrentDepth() + "\" ");
		if (attributeConfig[ATTRIBUTE_TIME]) STR.append("time=\"" + (System.currentTimeMillis() - startTime) + "\" ");
		if (attributeConfig[ATTRIBUTE_LINE]) STR.append("line=\"" + jp.getSourceLocation().getLine() + "\" ");
		if (attributeConfig[ATTRIBUTE_FILE]) STR.append("file=\"" + jp.getSourceLocation().getFileName() + "\" ");
		return STR.toString();
	}
	
	protected abstract String solverName();
	protected abstract int getCurrentDepth();

}
