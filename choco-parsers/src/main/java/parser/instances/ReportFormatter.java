/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
		final String value = name+ ' ' +DFORMAT.format(val);
		appendDiagnostic(value);
		dbInformations.add(value);
	}

	public final void storeDiagnostic(String name, Number val) {
		final String value = name+ ' ' +DFORMAT.format(val);
		appendDiagnostic(value);
		dbInformations.add(value);
	}
	public final void appendDiagnostic(String name, int val) {
		appendDiagnostic(name+ ' ' +DFORMAT.format(val));
	}

	public final void appendDiagnostic(String name, double val) {
		appendDiagnostic(name+ ' ' +DFORMAT.format(val));
	}

	public final void appendDiagnostic(String name, Number val) {
		appendDiagnostic(name+ ' ' +DFORMAT.format(val));
	}


	public final void appendDiagnostics(String name, int val, double time) {
		appendDiagnostic(name+ ' ' +DFORMAT.format(val));
		appendDiagnostic(name+"/s "+DFORMAT.format(val/time));
	}

	public final void appendConfiguration(String value) {
		b.append("c ").append(value).append('\n');
	}

	public final void storeConfiguration(String value) {
		if(value != null && value.length() != 0) {
			appendConfiguration(value);
			dbInformations.add(value);
		}
	}



}
