package choco.ecp.solver.propagation.dbt;

public interface PalmVarEvent {
  public void restoreVariableExplanation();

  public boolean isPopping();

  public void setPopping(boolean b);

  public void reset();
}
