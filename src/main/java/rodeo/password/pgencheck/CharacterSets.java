package rodeo.password.pgencheck;

/**
 * Contains a few standard character groups
 */
public final class CharacterSets {

    /**
     * Lower-case characters: <code>abcdefghijklmnopqrstuvwxyz</code>
     */
    public static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    /**
     * Upper-case characters: <code>ABCDEFGHIJKLMNOPQRSTUVWXYZ</code>
     */
    public static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * Digits: <code>0123456789</code>
     */
    public static final String DIGITS = "0123456789";
    /**
     * Symbols accessible on most keyboard layout: <code>!@#$%&amp;*-_=+|?{}[]()/'",.;:&lt;&gt;</code>
     */
    public static final String SYMBOLS = "!@#$%&*-_=+|?{}[]()/'\",.;:<>";

    /**
     * Lower-case characters that cannot be mistaken for other symbols: <code>abcdefghijkmnpqrstuvwxyz</code>
     */
    public static final String UNAMBIGUOUS_LOWER_CASE = "abcdefghijkmnpqrstuvwxyz";
    /**
     * Upper-case characters that cannot be mistaken for other symbols: <code>ACDEFGHJKLMNPQRSTUVWXYZ</code>
     */
    public static final String UNAMBIGUOUS_UPPER_CASE = "ACDEFGHJKLMNPQRSTUVWXYZ";
    /**
     * Digit characters that cannot be mistaken for other symbols: <code>2345679</code>
     */
    public static final String UNAMBIGUOUS_DIGITS = "2345679";
    /**
     * Symbol characters that cannot be mistaken for other symbols: <code>!@#$%&amp;*-_=+|</code>
     */
    public static final String UNAMBIGUOUS_SYMBOLS = "!@#$%&amp;*-_=+|?";

    private CharacterSets() {
        throw new UnsupportedOperationException();
    }

}
