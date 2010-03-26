package choco.cp.solver.constraints.global.geost.layers.continuousSolver;

import choco.kernel.common.logging.ChocoLogging;

import java.util.logging.Logger;

public final class        Quimper {

    private static final Logger LOGGER = ChocoLogging.getEngineLogger();

    static {
	String jlp=System.getProperty("java.library.path");
	LOGGER.info("jlp:"+jlp);
	System.loadLibrary("quimper");
    }


    public Quimper(String filename) {
	load(filename);
    }

    protected void finalize() throws Throwable {
        super.finalize();
	    release();
    }

    public native void contract(String ctc);

    public native void set_domain(String id, double lb, double ub);

    public native void set_var_domain(int i, double lb, double ub);

    public native void set_syb_domain(int i, double lb, double ub);

    public native double get_lb(String id);

    public native double get_ub(String id);

    public native double get_var_lb(int i);

    public native double get_var_ub(int i);

    public native double get_syb_lb(int i);

    public native double get_syb_ub(int i);
    
    private native void load(String filename);

    private native void release();


}
