package cli;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import parser.instances.AbstractInstanceModel;
import parser.instances.XcspModel;
import parser.instances.xcsp.XcspSettings;

public class XcspCmd extends AbstractBenchmarkCmd {

	@Option(name="-singloton", usage="Performs Singloton Consistency step")
	protected Boolean singloton;

	@Option(name="-h", usage="Heuristics")
	protected Integer heuristic;


	public XcspCmd() {
		super(new XcspSettings());
	}

	XcspSettings getXcspSettings() {
		return (XcspSettings) settings;
	}
	
	@Override
	protected void checkData() throws CmdLineException {
		super.checkData();	
		//overrides properties
		final XcspSettings set = getXcspSettings();
		if( singloton != null) set.setDoSingletonConsistency(singloton);
		if( heuristic != null) set.setHeuristic(heuristic);
	}


	@Override
	protected AbstractInstanceModel createInstance() {
		return new XcspModel( getXcspSettings());
	}


	@Override
	public boolean execute(File file) {
//		LOGGER.info(file.toString());
//		return true;
		instance.solveFile(file);
		return instance.getStatus().isValidWithCSP();
	}


	public static void main( String[] args )   {
		final XcspCmd cmd = new XcspCmd();
		if(args.length==0) {cmd.help();}
		else {cmd.doMain(args);}
	}
	
}
