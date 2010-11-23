package choco.kernel.common.util.bitmask;

public final class StringMask implements IBitMask {

	public final String option;
	
	public final long bitMask;
	
	public StringMask(String option, long mask) {
		super();
		this.option = option;
		this.bitMask = mask;
	}

	public final String getOption() {
		return option;
	}

	@Override
	public long getBitMask() {
		return bitMask;
	}

}
