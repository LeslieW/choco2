package parser.instances;

public enum ResolutionStatus {

	UNSAT("UNSATISFIABLE"),
	SAT("SATISFIABLE"),
	OPTIMUM("OPTIMUM FOUND"),
	TIMEOUT("TIMEOUT"),
	UNKNOWN("UNKNOWN"),
	ERROR("ERROR"),
	UNSUPPORTED("UNSUPPORTED");

	private final String name;

	private ResolutionStatus(String name) {
		this.name = name;
	}


	public final String getName() {
		return name;
	}

	public boolean isValidWithCSP() {
		return  ! (this == ResolutionStatus.UNSUPPORTED || this  == ResolutionStatus.ERROR);
	}

	public boolean isValidWithOptimize() {
		return ! ( 
				this == ResolutionStatus.UNSUPPORTED ||
				this  == ResolutionStatus.ERROR ||
				this == ResolutionStatus.UNSAT 
		);
	}

}
