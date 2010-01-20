package cli;

import org.kohsuke.args4j.CmdLineException;
import parser.instances.AbstractInstanceModel;
import parser.instances.FcspModel;
import parser.instances.fcsp.FcspSettings;

import java.io.File;

public class FcspCmd extends AbstractBenchmarkCmd {

	public FcspCmd() {
		super("/fcsp.properties");
	}

	FcspSettings getFcspSettings() {
		return (FcspSettings) settings;
	}

	@Override
	protected void checkData() throws CmdLineException {
		super.checkData();
		//overrides properties
		final FcspSettings set = getFcspSettings();
	}


	@Override
	protected AbstractInstanceModel createInstance() {
		return new FcspModel( getFcspSettings());
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