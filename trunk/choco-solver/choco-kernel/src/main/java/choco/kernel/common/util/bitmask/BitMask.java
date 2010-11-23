package choco.kernel.common.util.bitmask;

import java.util.List;

public final class BitMask implements IBitMask {

	private long bitMask;

	public BitMask() {
		super();
		bitMask = 0;
	}
	
	/**
	 * copy constructor.
	 */
	public BitMask(BitMask toCopy) {
		super();
		this.bitMask = toCopy.bitMask;
	}

	
	@Override
	public long getBitMask() {
		return bitMask;
	}

	public void clear() {
		bitMask = 0;
	}
	
	public boolean isEmpty() {
		return bitMask == 0;
	}

	public boolean contains(final long mask) {
		return (bitMask & mask) == mask;
	}
	
	public boolean or(final IBitMask... settings) {
		for (IBitMask setting : settings) {
			if(contains(setting)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(final IBitMask setting) {
		return contains(setting.getBitMask());
	}

	
	public void set(final long mask) {
		bitMask |= mask;		
	}

	public void toggle(final long mask) {
		bitMask ^= mask;
	}

	public void unset(final long mask) {
		bitMask &= ~mask;
	}

	public void read(List<String> options, StringMask... settings) {
		for (StringMask setting : settings) {
			if(options.contains(setting.getOption())) {
				set(setting);
			}
		}
	}
	
	public void unset(IBitMask... settings) {
		for (IBitMask setting : settings) {
			unset(setting.getBitMask());
		}
	}
	
	public void set(IBitMask... settings) {
		for (IBitMask setting : settings) {
			set(setting.getBitMask());
		}
	}

	public String toSettingsString() {
		StringBuilder b = new StringBuilder();
		//FIXME
//		for(IBitMask setting : IBitMask.values()) {
//			if(contains(setting)) {
//				b.append(setting).append(" ");
//			}
//		}
		return new String(b);
	}

	@Override
	public String toString() {
		return String.valueOf(bitMask);
	}
}
