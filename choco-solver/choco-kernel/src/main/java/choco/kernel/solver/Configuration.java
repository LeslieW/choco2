/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.solver;

import choco.kernel.common.logging.ChocoLogging;

import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 21 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class Configuration extends Properties {

    private final static Logger LOGGER = ChocoLogging.getMainLogger();

    //////////////////////////////////////// ANNOTATION ////////////////////////////////////////////////////////////////

    /**
     * Annotation to define a default value for a field.
     */
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Default {
        String value();
    }

    //////////////////////////////////////// DEFAULT KEYS //////////////////////////////////////////////////////////////

    /**
     * <br/><b>Goal</b>: Search for all solutions of a problem.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = "true")
    public static final String STOP_AT_FIRST_SOLUTION = "cp.solve.stop_at_first_solution";

    /**
     * <br/><b>Goal</b>: Search for the optimization of a declared objective variable.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String OPTIMIZE = "cp.optimize";

    /**
     * <br/><b>Goal</b>: When optimization is declared, set to true for maximization, set to false for minimization
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = "true")
    public static final String MAXIMIZE = "cp.maximize";

    /**
     * <br/><b>Goal</b>: do restart from root node after each solution.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String RESTART_AFTER_SOLUTION = "cp.restart.after_solution";

    /**
     * <br/><b>Goal</b>: To enable luby restart.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String RESTART_LUBY = "cp.restart.luby";

    /**
     * <br/><b>Goal</b>: To enable geometrical restart.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String RESTART_GEOMETRICAL = "cp.restart.geometrical";

    /**
     * <br/><b>Goal</b>: initial number of fails limiting the first search.
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 200
     */
    @Default(value = "200")
    public static final String RESTART_BASE = "cp.restart.base";

    /**
     * <br/><b>Goal</b>: initial number of fails limiting the first search.
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 200
     */
    @Default(value = "200")
    public static final String RESTART_MAX_NB = "cp.restart.number";

    /**
     * <br/><b>Goal</b>: geometrical factor for restart strategy
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 2
     */
    @Default(value = "2")
    public static final String RESTART_LUBY_GROW = "cp.restart.luby.grow";

    /**
     * <br/><b>Goal</b>: geometrical factor for restart strategy
     * <br/><b>Type</b>: double
     * <br/><b>Default value</b>: 1.2
     */
    @Default(value = "1.2")
    public static final String RESTART_GEOM_GROW = "cp.restart.geometrical.grow";

    /**
     * <br/><b>Goal</b>: Enable nogood recording from restart.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String NOGOOD_RECORDING_FROM_RESTART = "cp.nogood_from_restart";

    /**
     * <br/><b>Goal</b>: Tells the strategy wether or not use recomputation.
     * The value of the parameter indicates the maximum recomputation gap, i.e. the maximum number of decisions between two storages.
     * If the parameter is lower than or equal to 1, the trailing storage mechanism is used (default).
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 1
     */
    @Default(value = "1")
    public static final String RECOMPUTATION_GAP = "cp.recomputation.gap";

    /**
     * <br/><b>Goal</b>: Enable card reasonning: decide if redundant constraints are automatically added
     * to the model to reason on cardinalities on sets as well as kernel and enveloppe.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = "true")
    public static final String CARD_REASONNING = "cp.cardinality_reasonning";

    /**
     * <br/><b>Goal</b>: Initial seed to generate streams of pseudorandom numbers
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 29091981
     */
    @Default(value = "29091981")
    public static final String RANDOM_SEED = "cp.random.seed";

    /**
     * <br/><b>Goal</b>: Search limit type. If the search has not ended in the define limit bound, it is automatically stopped.
     * <br/><b>Type</b>: Limit
     * <br/><b>Default value</b>: UNDEFINED
     */
    @Default(value = "UNDEFINED")
    public static final String SEARCH_LIMIT = "cp.search.limit.type";

    /**
     * <br/><b>Goal</b>: Search limit bound. If the search has not ended in the define search limit bound,
     * it is automatically stopped.
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 2147483647 ({@link Integer.MAX_VALUE})
     */
    @Default(value = "2147483647")
    public static final String SEARCH_LIMIT_BOUND = "cp.search.limit.value";

    /**
     * <br/><b>Goal</b>: Restart limit type. If the limit bound is reached, the search is restarted.
     * <br/><b>Type</b>: Limit
     * <br/><b>Default value</b>: BACKTRACK
     */
    @Default(value = "BACKTRACK")
    public static final String RESTART_LIMIT = "cp.restart.limit.type";

    /**
     * <br/><b>Goal</b>: Restart limit bound. If the limit bound is reached, the search is restarted.
     * it is automatically stopped.
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 2147483647 ({@link Integer.MAX_VALUE})
     */
    @Default(value = "2147483647")
    public static final String RESTART_LIMIT_BOUND = "cp.restart.limit.value";

    /**
     * <br/><b>Goal</b>: Enforce the use of shaving before starting the search.
     * <br/><i>The shaving mechanism is related to singloton consistency</i>.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String ROOT_SHAVING = "cp.shaving.root";

    /**
     * <br/><b>Goal</b>: Compute a destructive lower bound before starting the search (optimization).
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String DESTRUCTIVE_LOWER_BOUND = "cp.destructive_lb";

    /**
     * <br/><b>Goal</b>: Apply shaving while computing the destructive lower bound.
     * <br/><i> For each hypothetical upper bound, the consistency test applies shaving</i>.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String DLB_SHAVING = "cp.shaving.destructive_lb";

    /**
     * <br/><b>Goal</b>: Apply a bottom-up search algorithm (optimization).
     * <br/><i> The top-down strategy (default) starts with a upper bound and tries to improve it.</i>
     * <br/><i> The bottom-up starts with a lower bound as target upper bound which is incremented by one unit until the
     * problem becomes feasible.</i>.
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: false
     */
    @Default(value = "false")
    public static final String BOTTOM_UP = "cp.search.bottom_up";

    /**
     * <br/><b>Goal</b>:
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 21474836 ({@link choco.Choco.MAX_UPPER_BOUND})
     */
    @Default(value = "21474836")
    public static final String HORIZON_UPPER_BOUND = "cp.horizon.upper_bound";

    /**
     * <br/><b>Goal</b>: Solution pool capacity, number of solutions to store within the solutions' pool.
     * <br/><b>Type</b>: int
     * <br/><b>Default value</b>: 1
     */
    @Default(value = "1")
    public static final String SOLUTION_POOL_CAPACITY = "cp.solutionpool.capacity";

    /**
     * <br/><b>Goal</b>:
     * <br/><b>Type</b>: boolean
     * <br/><b>Default value</b>: true
     */
    @Default(value = "true")
    public static final String RESTORE_BEST_SOLUTION = "cp.restore_best_solution";

    /**
     * <br/><b>Goal</b>: Precision of the search for problem involving real variables
     * <br/><b>Type</b>: double
     * <br/><b>Default value</b>: 1.0e-6
     */
    @Default(value = "1.0e-6")
    public final static String REAL_PRECISION = "cp.real.precision";

    /**
     * <br/><b>Goal</b>: Minimal width reduction between two propagations, for problem involving real variables
     * <br/><b>Type</b>: double
     * <br/><b>Default value</b>: 0.99
     */
    @Default(value = "0.99")
    public final static String REAL_REDUCTION = "cp.real.reduction";


    /**
     * Creates an empty property list with no default values.
     */
    public Configuration() {
        setDefault();
    }


    /**
     * Creates an empty property list with the specified defaults.
     *
     * @param defaults the defaults.
     */
    public Configuration(final Properties defaults) {
        this.putAll(defaults);
    }

    /**
     * Set default configuration.
     * First clear then load default values for defined keys.
     */
    public void setDefault() {
        this.clear();
        load();
    }

    /**
     * Load the default value of keys defined in @Default annotation
     *
     * @throws IllegalAccessException if the specified object is not an
     *                                instance of the class or interface declaring the underlying
     */
    private void load() {
        Field[] fields = Configuration.class.getFields();
        for (Field f : fields) {
            Default annotation = f.getAnnotation(Default.class);
            try {
                this.put(f.get(this), annotation.value());
            } catch (IllegalAccessException e) {
                logOnFailure(f.getName());
            }
        }
        logOnSuccess("default");
    }

    /**
     * Load the default value of keys defined in @Default annotation
     *
     * @throws IllegalAccessException if the specified object is not an
     *                                instance of the class or interface declaring the underlying
     */
    private String loadDefault(String key){
        Field[] fields = Configuration.class.getFields();
        for (Field f : fields) {
            try {
                if(f.get(this).equals(key)){
                    Default ann = f.getAnnotation(Default.class);
                    return ann.value();
                }
            } catch (IllegalAccessException e) {
                logOnFailure("key");
            }
        }
        throw new NullPointerException();
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as a boolean.
     */
    public boolean readBoolean(String key) {
        String value = this.getProperty(key);
        if (value == null) {
            logOnAbsence(key);
            value = loadDefault(key);
        }
        return Boolean.valueOf(value);
    }

    /**
     * Returns the value to which the specified key is mapped, if exists.
     * Otherwise, return default value.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue value to return the key does not exist in the configuration
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as an integer.
     */
    public boolean readBoolean(final String key, boolean defaultValue) {
        final String b = this.getProperty(key);
        if (b == null) {
            logOnAbsence(key);
            return defaultValue;
        } else{
            return Boolean.parseBoolean(b);
        }
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as an integer.
     */
    public int readInt(String key) {
        String value = this.getProperty(key);
        if (value == null) {
            logOnAbsence(key);
            value = loadDefault(key);
        }
        return Integer.valueOf(value);
    }

    /**
     * Returns the value to which the specified key is mapped, if exists.
     * Otherwise, return default value.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue value to return the key does not exist in the configuration
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as an integer.
     */
    public int readInt(final String key, int defaultValue) {
        final String b = this.getProperty(key);
        if (b == null) {
            logOnAbsence(key);
            return defaultValue;
        } else{
            return Integer.parseInt(b);
        }
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as a double.
     */
    public double readDouble(String key) {
        String value = this.getProperty(key);
        if (value == null) {
            logOnAbsence(key);
            value = loadDefault(key);
        }
        return Double.valueOf(value);
    }

    /**
     * Returns the value to which the specified key is mapped, if exists.
     * Otherwise, return default value.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue value to return the key does not exist in the configuration
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as an integer.
     */
    public double readDouble(final String key, double defaultValue) {
        final String b = this.getProperty(key);
        if (b == null) {
            logOnAbsence(key);
            return defaultValue;
        } else{
            return Double.parseDouble(b);
        }
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as an integer.
     */
    public String readString(final String key) {
        String value = this.getProperty(key);
        if (value == null) {
            logOnAbsence(key);
            value = loadDefault(key);
        }
        return value;
    }


    /**
     * Returns the value to which the specified key is mapped, if exists.
     * Otherwise, return default value.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue value to return the key does not exist in the configuration
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as an integer.
     */
    public String readString(final String key, String defaultValue) {
        final String b = this.getProperty(key);
        if (b == null) {
            logOnAbsence(key);
            return defaultValue;
        } else return b;
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key   the key whose associated value is to be returned
     * @param clazz the class of the enum expected
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as a boolean.
     */
    @SuppressWarnings({"unchecked"})
    public <T extends Enum<T>> T readEnum(String key, Class clazz) {
        String value = this.getProperty(key);
        if (value == null) {
            logOnAbsence(key);
            value = loadDefault(key);
        }
        return (T) Enum.valueOf(clazz, value);
    }

    /**
     * Returns the value to which the specified key is mapped, if exists.
     * Otherwise, return default value.
     *
     * @param key          the key whose associated value is to be returned
     * @param clazz        the class of the enum expected
     * @param defaultValue value to return the key does not exist in the configuration
     * @return the value to which the specified key is mapped
     * @throws NullPointerException  if the specified key is null
     * @throws NumberFormatException if the value cannot be parsed
     *                               as a boolean.
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T readEnum(final String key, T defaultValue) {
        final String b = this.getProperty(key);
        if (b == null) {
            logOnAbsence(key);
            return defaultValue;
        } else return (T) Enum.valueOf(defaultValue.getClass(), b);
    }

    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this hashtable. Neither the key nor the
     * value can be <code>null</code>. <p>
     * *
     *
     * @param key   the hashtable key
     * @param value the value
     * @throws NullPointerException if the key or value is
     *                              <code>null</code>
     */
    public void putInt(String key, int value) {
        this.put(key, Integer.toString(value));
    }

    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this hashtable. Neither the key nor the
     * value can be <code>null</code>. <p>
     * *
     *
     * @param key   the hashtable key
     * @param value the value
     * @throws NullPointerException if the key or value is
     *                              <code>null</code>
     */
    public void putDouble(String key, double value) {
        this.put(key, Double.toString(value));
    }

    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this hashtable. Neither the key nor the
     * value can be <code>null</code>. <p>
     * *
     *
     * @param key   the hashtable key
     * @param value the value
     * @throws NullPointerException if the key or value is
     *                              <code>null</code>
     */
    public void putBoolean(String key, boolean value) {
        this.put(key, Boolean.toString(value));
    }

    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this hashtable. Neither the key nor the
     * value can be <code>null</code>. <p>
     * *
     *
     * @param key   the hashtable key
     * @param value the value
     * @throws NullPointerException if the key or value is
     *                              <code>null</code>
     */
    public void putEnum(String key, Enum value) {
        this.put(key, value.name());
    }

    private static void logOnAbsence(String key) {
        LOGGER.log(Level.CONFIG, "properties...[read-property-from-key:{0}][FAIL]", key);
    }

    private static void logOnFailure(String resource) {
        System.err.println("properties...[load-properties:" + resource + "][FAIL]");
    }

    private static void logOnSuccess(String resource) {
        LOGGER.log(Level.CONFIG, "properties...[load-properties:{0}]", resource);
    }

}
