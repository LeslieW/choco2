package parser.instances.checker;


import static java.lang.Integer.parseInt;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import parser.instances.AbstractInstanceModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.PropertyUtils;

/**
 * Static storage of properties that represents the best-known status of some instances.
 * Properties are given as follows: 
 * <ul>
 * <li> instName=true/false (case insensitive) #CSP</li>
 * <li> instName=(OPT|LB:UB) #Optimization (LB<=UB)</li>
 * </ul>
 * 
 * @author Arnaud Malapert</br> 
 * @since 19 févr. 2010 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class SCheckFactory {

	public static final Logger LOGGER = ChocoLogging.getMainLogger();

	public static final Properties PROPERTIES = new Properties();


	public static void load(File... files) {
		PropertyUtils.loadProperties(PROPERTIES, files);
	}

	public static void load(String... resources) {
		PropertyUtils.loadProperties(PROPERTIES, resources);
	}

	public static IStatusChecker makeStatusChecker(AbstractInstanceModel model) {
		return makeStatusChecker(model.getInstanceName());
	}

	public static IStatusChecker makeStatusChecker(String key) {
		String pvalue = SCheckFactory.PROPERTIES.getProperty(key);
		if( pvalue != null) {
			pvalue = pvalue.trim();
			try {
				final int splitIndex = pvalue.indexOf(':'); 
				if(splitIndex == -1) {
					if(pvalue.equalsIgnoreCase("true")) return new SatSChecker(true);
					else if (pvalue.equalsIgnoreCase("false")) return new SatSChecker(false);
					else return new OptimSChecker(parseInt(pvalue)); //format OPT
				}else {
					//format LB:UB 
					return new OptimSChecker(
							parseInt(pvalue.substring(0, splitIndex)),
							parseInt(pvalue.substring(splitIndex+1))
					);
				}
			} catch (NumberFormatException e) {
				SCheckFactory.LOGGER.log(Level.SEVERE,"properties...[invalid-checker-format][{0}]",key);
				return null;
			}	
		}
		SCheckFactory.LOGGER.log(Level.CONFIG,"properties...[no-status-checker][{0}]",key);
		return null;
	}
}
