package choco.kernel.model.constraints.automaton.penalty;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Apr 27, 2010
 * Time: 11:31:07 AM
 */
public class IsoPenaltyFunction extends AbstractPenaltyFunction
{
int factor;


public IsoPenaltyFunction()
{
        this(1);
}

public IsoPenaltyFunction(int factor)
{
        this.factor = factor;
}

@Override
public final int getPenalty(int value)
{
       /* if (value >=1 && value <= 2)
                return 0;
        else
                return 10;  */
        return value*factor;
}

public final int getFactor()
{
        return factor;
}
}
