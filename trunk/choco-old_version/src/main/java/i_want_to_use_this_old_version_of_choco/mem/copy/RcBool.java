package i_want_to_use_this_old_version_of_choco.mem.copy;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateBool;

/**
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Time: 18:11:41
 * To change this template use File | Settings | File Templates.
 */
public class RcBool implements IStateBool, RecomputableElement {

    private EnvironmentCopying environment;
    private boolean currentValue;
    private int timeStamp;

    public RcBool(EnvironmentCopying env, boolean b) {
        environment = env;
        currentValue = b;
        environment.add(this);
        timeStamp = env.getWorldIndex();
    }

    public boolean get() {
        return currentValue;
    }

    public void set(boolean b) {
        timeStamp = environment.getWorldIndex();
        currentValue = b ;
    }

    public boolean deepCopy() {
        return currentValue;
    }

    public IEnvironment getEnvironment() {
        return environment;
    }

    public int getType() {
        return BOOL;
    }

    public int getTimeStamp() {
        return timeStamp;
    }
}
