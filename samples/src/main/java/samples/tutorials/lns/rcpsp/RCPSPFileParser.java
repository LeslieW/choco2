package samples.tutorials.lns.rcpsp;

import parser.absconparseur.tools.UnsupportedConstraintException;
import parser.instances.InstanceFileParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Parser for instance files from benchmark sets of the Resource-Constrained Project Scheduling Problem:
 * BL (Baptiste-Le Pape)
 * PSPLib (KSD)
 * @author Sophie Demassey
 * @since 11/01/11 - 14:49
 */
public class RCPSPFileParser implements InstanceFileParser {

File file;
RCPSPData data;

public RCPSPData getData()
{
	return data;
}

@Override
public File getInstanceFile()
{
	return file;
}

@Override
public void loadInstance(File file)
{
	this.file = file;
}

@Override
public void parse(boolean displayInstance) throws UnsupportedConstraintException
{
	try {
		Scanner sc = new Scanner(file);
		if (file.getName().startsWith("bl")) { parseBL(sc); } else {
			throw new UnsupportedConstraintException("no parser for file " + file.getName());
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	if (displayInstance && LOGGER.isLoggable(Level.INFO)) {
		LOGGER.log(Level.INFO, data.toString());
	}
}

public void parseBL(Scanner sc)
{
	final int nDummyAct = 2;
	int nAct = sc.nextInt() - nDummyAct;
	int bidon = sc.nextInt();
	assert bidon == 9999;
	int nRes = sc.nextInt();

	data = new RCPSPData(nAct, nRes);

	for (int i = 0; i < nRes + 2; i++) {
		bidon = sc.nextInt();
	}
	for (int i = 0; i < nAct; i++) {
		int act = sc.nextInt();
		assert act == i + nDummyAct;
		data.setDuration(i, sc.nextInt());
		for (int k = 0; k < nRes; k++) {
			data.setRequest(i, k, sc.nextInt());
		}
	}
	for (int i = 0; i < nRes + 2; i++) {
		bidon = sc.nextInt();
	}

	bidon = sc.nextInt();
	assert bidon == 9999;
	for (int k = 0; k < nRes; k++) {
		data.setCapacity(k, sc.nextInt());
	}

	int act1 = sc.nextInt();
	while (act1 != 9999) {
		int act2 = sc.nextInt();
		if (act1 != 1 && act2 != nAct + nDummyAct) {
			data.setPrecedence(act1 - nDummyAct, act2 - nDummyAct);
		}
		act1 = sc.nextInt();
	}
}

@Override
public void cleanup()
{
	file = null;
	data = null;
}

}
