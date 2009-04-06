package i_want_to_use_this_old_version_of_choco.global.regular;

/**
 * A Transition is defined by its origine, destination and
 * the symbol used to get from origin to destination
 */
public class Transition {

    protected int origin;
    protected int value;
    protected int destination;

    public Transition(int origin, int value, int destination) {
        this.origin = origin;
        this.value = value;
        this.destination = destination;
    }

    public String toString() {
        return "(" + origin + "," + value + "," + destination + ")";
    }
}