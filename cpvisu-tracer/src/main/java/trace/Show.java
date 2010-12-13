package trace;

/**
 * tree or viz
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 9 déc. 2010
 */
public enum Show {
    TREE(1), VIZ(2);

    final int mask;
    Show(int i) {
        this.mask = i;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}