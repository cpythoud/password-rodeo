package rodeo.password.pgencheck;

import java.util.Random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A default implementation of <code>RandomUIntGenerator</code>. This class is used if no implementation of
 * <code>RandomUIntGenerator</code> is provided to <code>PasswordMaker.Factory</code>.
 * <p>
 * This class use <code>ThreadLocalRandom.current()</code> internally.
 * @see RandomUIntGenerator
 * @see PasswordMaker.Factory#setRandomUIntGenerator(RandomUIntGenerator)
 */
public final class DefaultUIntGenerator implements RandomUIntGenerator {

    /**
     * A sharable instance of <code>RandomUIntGenerator</code>.
     */
    public static final RandomUIntGenerator GENERATOR = new DefaultUIntGenerator();

    /**
     * Create a <code>DefaultUIntGenerator</code> instance.
     */
    public DefaultUIntGenerator() { }

    /**
     * Generate a random integer between 0 and <code>max</code> (not included).
     * @param max the upper-bound (not included) of generated integers
     * @return a random integer between 0 and <code>max</code> (not included)
     * @throws IllegalArgumentException if <code> max &lt; 2</code>
     */
    @Override
    public int getNextUInt(int max) {
        if (max < 2)
            throw new IllegalArgumentException("max must be >= 2");

        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * Returns a <code>java.util.Random</code> object.
     * @return a <code>java.util.Random</code> object
     */
    @Override
    public Random random() {
        return ThreadLocalRandom.current();
    }

}
