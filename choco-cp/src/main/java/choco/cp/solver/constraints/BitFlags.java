package choco.cp.solver.constraints;

import static choco.cp.solver.SettingType.ADDITIONAL_RULES;
import static choco.cp.solver.SettingType.DEFAULT_FILTERING;
import static choco.cp.solver.SettingType.DETECTABLE_PRECEDENCE;
import static choco.cp.solver.SettingType.DYNAMIC_LB;
import static choco.cp.solver.SettingType.EDGE_FINDING_D;
import static choco.cp.solver.SettingType.FILL_BIN;
import static choco.cp.solver.SettingType.LAST_BINS_EMPTY;
import static choco.cp.solver.SettingType.NF_NL;
import static choco.cp.solver.SettingType.OVERLOAD_CHECKING;
import static choco.cp.solver.SettingType.SINGLE_RULE_FILTERING;
import static choco.cp.solver.SettingType.TASK_INTERVAL;
import static choco.cp.solver.SettingType.TASK_INTERVAL_SLOW;
import static choco.cp.solver.SettingType.VHM_CEF_ALGO_N2K;
import static choco.cp.solver.SettingType.VILIM_CEF_ALGO;
import static choco.cp.solver.SettingType.VILIM_FILTERING;

import java.util.Set;

import choco.cp.solver.SettingType;
import choco.kernel.solver.SolverException;

public final class BitFlags {


	private long flags;

	
	public BitFlags() {
		super();
		flags = 0;
	}
	
	/**
	 * copy constructor.
	 */
	public BitFlags(BitFlags toCopy) {
		super();
		this.flags = toCopy.flags;
	}

	public long getValue() {
		return flags; 
	}

	public void clear() {
		flags = 0;
	}
	
	public boolean isEmpty() {
		return flags == 0;
	}

	public boolean contains(final long mask) {
		return (flags & mask) == mask;
	}
	
	public boolean or(SettingType... settings) {
		for (SettingType setting : settings) {
			if(contains(setting)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(SettingType setting) {
		return contains(setting.getBitMask());
	}

	public void set(final long mask) {
		flags |= mask;		
	}

	public void toggle(final long mask) {
		flags ^= mask;
	}

	public void unset(final long mask) {
		flags &= ~mask;
	}

	public void read(Set<String> options, SettingType... settings) {
		for (SettingType setting : settings) {
			if(options.contains(setting.getOptionName())) {
				set(setting);
			}
		}
	}
	public void set(BitFlags f) {
		this.set(f.getValue());
	}
	public void unset(SettingType... settings) {
		for (SettingType setting : settings) {
			unset(setting.getBitMask());
		}
	}
	
	public void set(SettingType... settings) {
		for (SettingType setting : settings) {
			set(setting.getBitMask());
		}
	}

	public String toSettingsString() {
		StringBuilder b = new StringBuilder();
		for(SettingType setting : SettingType.values()) {
			if(contains(setting)) {
				b.append(setting.getName()).append(" ");
			}
		}
		if(b.length()>0) {b.deleteCharAt(b.length()-1);}
		return new String(b);
	}

	public String toSettingsLabels() {
		StringBuilder b = new StringBuilder();
		for(SettingType setting : SettingType.values()) {
			if(contains(setting)) {
				b.append(setting.getLabel()).append("-");
			}
		}
		if(b.length()>0) {b.deleteCharAt(b.length()-1);}
		return new String(b);
	}

	@Override
	public String toString() {
		return String.valueOf(flags);
	}

	public void readPackOptions(final Set<String> options) {
		read(options, ADDITIONAL_RULES, DYNAMIC_LB, FILL_BIN, LAST_BINS_EMPTY);
	}
	
	public void readCumulativeOptions(final Set<String> options) {
		read(options, TASK_INTERVAL, TASK_INTERVAL_SLOW, VHM_CEF_ALGO_N2K, VILIM_CEF_ALGO);
	}


	public void readDisjunctiveOptions(final Set<String> options) {
		read(options, OVERLOAD_CHECKING, NF_NL, DETECTABLE_PRECEDENCE, EDGE_FINDING_D);
		if(isEmpty()) set(NF_NL, DETECTABLE_PRECEDENCE, EDGE_FINDING_D);
		if(options.contains(VILIM_FILTERING.getOptionName())) {
			set(VILIM_FILTERING);
		} else {
			set(DEFAULT_FILTERING);
		}
		if(options.contains(SINGLE_RULE_FILTERING.getOptionName())) {
			throw new SolverException("invalid disjunctive setting:" + SINGLE_RULE_FILTERING.getName());
		}
	}
}
