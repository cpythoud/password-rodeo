package rodeo.password.pgencheck;

import java.util.Random;

/**
 * Configuration information for generating random numbers for password creation
 */
public interface RandomUIntGenerator {

    /**
     * Generate a random integer between 0 and <code>max</code> (not included).
     * @param max the upper-bound (not included) of generated integers
     * @return a random integer between 0 and <code>max</code> (not included)
     * @see PasswordMaker.Factory#setRandomUIntGenerator(RandomUIntGenerator)
     */
    int getNextUInt(int max);

    /**
     * Returns a <code>java.util.Random</code> object used to reorder the characters composing a password.
     * @return a <code>java.util.Random</code> object
     * @see PasswordMaker.Factory#setRandomUIntGenerator(RandomUIntGenerator)
     * @see PasswordMaker#create()
     */
    Random random();

}
