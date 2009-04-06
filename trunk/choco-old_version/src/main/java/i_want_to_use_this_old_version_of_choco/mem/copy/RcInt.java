package i_want_to_use_this_old_version_of_choco.mem.copy;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;

/**
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Time: 11:51:54
 * To change this template use File | Settings | File Templates.
 */
public class RcInt implements IStateInt, RecomputableElement {

    private EnvironmentCopying environment;
    private int currentValue;
    private int timeStamp;

    public RcInt(EnvironmentCopying env) {
        this(env,UNKNOWN_INT);
    }

    public RcInt(EnvironmentCopying env, int i ) {
        environment = env;
        currentValue= i;
        environment.add(this);
        timeStamp = environment.getWorldIndex();
    }

    public int get() {
        return currentValue;
    }

    public boolean isKnown() {
        return (currentValue != UNKNOWN_INT);
    }

    public void set(int y) {
        //if (y != currentValue)
            currentValue = y;
        timeStamp = environment.getWorldIndex();
    }

    public void add(int delta) {
        set(get() + delta);
    }

    public IEnvironment getEnvironment() {
        return environment;
    }

    public int deepCopy() {
        return currentValue;
    }

    public String toString() {
        if (isKnown())
            return String.valueOf(currentValue);
        else
            return "?";
    }

    public int getType() {
        return INT;
    }

    public int getTimeStamp() {
        return timeStamp;
    }
}
