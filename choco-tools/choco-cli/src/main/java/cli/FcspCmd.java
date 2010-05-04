package cli;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;

import parser.instances.AbstractInstanceModel;
import parser.instances.BasicSettings;
import parser.instances.FcspModel;

public class FcspCmd extends AbstractBenchmarkCmd {

	public FcspCmd() {
		super(new BasicSettings());
	}

	
	@Override
	protected void checkData() throws CmdLineException {
		super.checkData();
	}

	@Override
	protected AbstractInstanceModel createInstance() {
		return new FcspModel(settings);
	}


	@Override
	public boolean execute(File file) {
		instance.solveFile(file);
		return instance.getStatus().isValidWithCSP();
	}


	public static void main( String[] args )   {
		final FcspCmd cmd = new FcspCmd();
		if(args.length==0) {cmd.help();}
		else {cmd.doMain(args);}
	}

}