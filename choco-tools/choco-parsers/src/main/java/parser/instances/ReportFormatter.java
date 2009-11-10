package parser.instances;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ReportFormatter {

	public final static NumberFormat DFORMAT = DecimalFormat.getNumberInstance(Locale.ENGLISH);
	
	static {
		DFORMAT.setGroupingUsed(false);
		DFORMAT.setMaximumFractionDigits(2);
	}
	
	private final StringBuilder b = new StringBuilder();

	private final List<String> dbInformations = new LinkedList<String>();

	public final String getLoggingMessage() {
		return b.toString();
	}

	public final List<String> getDbInformations() {
		return Collections.unmodifiableList(dbInformations);
	}

	public void reset() {
		dbInformations.clear();
		b.setLength(0);
	}

	public final void appendStatus(String status) {
		b.append("s ").append(status).append('\n');
	}

	public final void appendValues(String values) {
		b.append("v ").append(values).append('\n');
	}

	public final void appendDiagnostic(String value) {
		b.append("d ").append(value).append('\n');
	}

	public final void storeDiagnostics(String value) {
		appendDiagnostic(value);
		dbInformations.add(value);
	}

	public final void storeDiagnostic(String name, int val) {
		final String value = name+" "+DFORMAT.format(val);
		appendDiagnostic(value);
		dbInformations.add(value);
	}

	public final void storeDiagnostic(String name, Number val) {
		final String value = name+" "+DFORMAT.format(val);
		appendDiagnostic(value);
		dbInformations.add(value);
	}
	public final void appendDiagnostic(String name, int val) {
		appendDiagnostic(name+" "+DFORMAT.format(val));
	}

	public final void appendDiagnostic(String name, double val) {
		appendDiagnostic(name+" "+DFORMAT.format(val));
	}

	public final void appendDiagnostic(String name, Number val) {
		appendDiagnostic(name+" "+DFORMAT.format(val));
	}


	public final void appendDiagnostics(String name, int val, double time) {
		appendDiagnostic(name+" "+DFORMAT.format(val));
		appendDiagnostic(name+"/s "+DFORMAT.format(val/time));
	}

	public final void appendConfiguration(String value) {
		b.append("c ").append(value).append('\n');
	}

	public final void storeConfiguration(String value) {
		appendConfiguration(value);
		dbInformations.add(value);
	}



}
