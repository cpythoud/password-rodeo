package rodeo.password.pgencheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rodeo.password.pgencheck.ErrorMessages.AT_LEAST_ONE_CHAR;
import static rodeo.password.pgencheck.ErrorMessages.NOT_ENOUGH_CHARACTERS;
import static rodeo.password.pgencheck.ErrorMessages.NO_CHECKER_CHAR_SET_PROVIDED;
import static rodeo.password.pgencheck.ErrorMessages.MAX_BIGGER_THAN_MIN;

/**
 * Validate passwords according to predefined criteria.
 * <p>
 * The following criteria are available:
 * <ul>
 *     <li>minimum length;</li>
 *     <li>maximum length;</li>
 *     <li>
 *         constraints on characters composing the password:
 *         <ul>
 *             <li>character must belong to a predefined group of characters;</li>
 *             <li>minimum and maximum number of characters per group can be specified.</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public final class PasswordChecker extends PasswordData {

    private final int minLength;
    private final int maxLength;

    private PasswordChecker(
            int minLength,
            int maxLength,
            List<String> charGroups,
            List<Integer> groupMinCounts,
            List<Integer> groupMaxCounts)
    {
        super(charGroups, groupMinCounts, groupMaxCounts);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    /**
     * Create a factory to specify password validation criteria and create a <code>PasswordChecker</code> object.
     * @return an internal <code>PasswordChecker</code> factory
     * @see Factory
     */
    public static Factory factory() {
        return new Factory();
    }

    /**
     * Get the minimum length required for a password to be accepted.
     * @return the minimum length required for a password to be accepted
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Get the maximum length required for a password to be accepted.
     * @return the maximum length required for a password to be accepted
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Check if a password can be validated against the specified criteria and return <code>true</code>
     * if that's the case.
     * @param password to be validated
     * @return <code>true</code> if <code>password</code> matches the criteria, false otherwise
     */
    public boolean quickCheck(String password) {
        if (password.length() < minLength)
            return false;

        if (password.length() > maxLength)
            return false;

        for (int codePoint: password.codePoints().toArray())
            if (getAllChars().indexOf(codePoint) == -1)
                return false;

        var charCounts = getCharacterTypeCounts(password);
        for (int i = 0; i < groupMinCounts().size(); ++i) {
            int count = charCounts.get(i);
            if (count < groupMinCounts().get(i)
                    || (groupMaxCounts().get(i) != 0 && count > groupMaxCounts().get(i)))
                return false;
        }

        return true;
    }

    private Map<Integer, Integer> getCharacterTypeCounts(String password) {
        var counts = new HashMap<Integer, Integer>();
        for (int i = 0; i < charGroups().size(); ++i)
            counts.put(i, 0);

        password.codePoints().forEach(codePoint -> {
            for (int i = 0; i < charGroups().size(); ++i)
                if (charGroups().get(i).indexOf(codePoint) != - 1)
                    counts.put(i, counts.get(i) + 1);
        });

        return counts;
    }

    /**
     * Check if a password can be validated against the specified criteria and return the first error encountered
     * if any or <code>PasswordCheckStatus.OK</code> otherwise.
     * <p>
     * Criteria are checked in this order:
     * <ul>
     *     <li>minimum length of password;</li>
     *     <li>maximum length of password;</li>
     *     <li>illegal characters;</li>
     *     <li>not enough characters from a certain group;</li>
     *     <li>too many characters form a certain group.</li>
     * </ul>
     * @param password to be validated
     * @return <code>PasswordCheckStatus.OK</code> if <code>password</code> matches the criteria, otherwise a code
     * for the first error encountered
     * @see PasswordCheckStatus
     */
    public PasswordCheckStatus check(String password) {
        if (password.length() < minLength)
            return PasswordCheckStatus.TOO_SHORT;

        if (password.length() > maxLength)
            return PasswordCheckStatus.TOO_LONG;

        for (int codePoint: password.codePoints().toArray())
            if (getAllChars().indexOf(codePoint) == -1)
                return PasswordCheckStatus.ILLEGAL_CHARACTER;

        var charCounts = getCharacterTypeCounts(password);
        for (int i = 0; i < groupMinCounts().size(); ++i) {
            int count = charCounts.get(i);
            if (count < groupMinCounts().get(i))
                return PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP;
            if (groupMaxCounts().get(i) > 0 && count > groupMaxCounts().get(i))
                return PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP;
        }

        return PasswordCheckStatus.OK;
    }

    /**
     * Check if a password can be validated against the specified criteria and return a list of all the problems
     * encountered. This list is empty if there is no error.
     * @param password to be validated
     * @return a list of all the errors encountered while validating the password or an empty list if the
     * <code>password</code> matches all the criteria
     * @see PasswordCheckError
     */
    public List<PasswordCheckError> fullCheck(String password) {
        var errors = new ArrayList<PasswordCheckError>();

        if (password.length() < minLength)
            errors.add(PasswordCheckError.tooShort());

        if (password.length() > maxLength)
            errors.add(PasswordCheckError.tooLong());

        password.codePoints().forEach(codePoint -> {
            if (getAllChars().indexOf(codePoint) == -1)
                errors.add(PasswordCheckError.illegalCharacter(codePoint));
        });

        var charCounts = getCharacterTypeCounts(password);
        for (int i = 0; i < groupMinCounts().size(); ++i) {
            int count = charCounts.get(i);
            if (count < groupMinCounts().get(i))
                errors.add(PasswordCheckError.notEnoughOfCharacterType(i, charGroups().get(i), groupMinCounts().get(i), charCounts.get(i)));
            if (groupMaxCounts().get(i) > 0 && count > groupMaxCounts().get(i))
                errors.add(PasswordCheckError.tooManyOfCharacterType(i, charGroups().get(i), groupMaxCounts().get(i), charCounts.get(i)));
        }

        return errors;
    }

    /**
     * Internal factory to create <code>PasswordChecker</code>s.
     * <p>
     * This factory class allows you to build a <code>PasswordChecker</code> using a fluent interface. You create a
     * <code>Factory</code> by calling {@link PasswordChecker#factory() PasswordChecker.factory()}.
     * Once all the criteria have been specified, you call the {@link #create() create} function
     * to create a <code>PasswordChecker</code> object.
     */
    public static final class Factory extends AbstractFactory<Factory> {

        private int minLength = 16;
        private int maxLength = 64;

        private Factory() { }

        /**
         * Sets the minimum and maximum password lengths allowed.
         * @param minLength the password minimum length
         * @param maxLength the password maximum length
         * @return <code>this</code> factory
         */
        public Factory setMinMaxLength(int minLength, int maxLength) {
            if (minLength < 1)
                throw new IllegalArgumentException(AT_LEAST_ONE_CHAR + minLength);
            if (maxLength < minLength)
                throw new IllegalArgumentException(MAX_BIGGER_THAN_MIN + maxLength + " < " + minLength);

            this.minLength = minLength;
            this.maxLength = maxLength;

            return this;
        }

        /**
         * Create a <code>PasswordChecker</code> according to the specified criteria.
         * @return a new <code>PasswordChecker</code> matching the specified criteria
         * @throws IllegalStateException if no character group has been specified
         * @throws IllegalStateException if validation would be impossible because the minimum count requirements
         * on character groups would exceed the maximum length allowed for the password
         */
        public PasswordChecker create() {
            if (charGroups().isEmpty())
                throw new IllegalStateException(NO_CHECKER_CHAR_SET_PROVIDED);
            if (isSetCountSumLargerThanMaxPasswordCount())
                throw new IllegalStateException(NOT_ENOUGH_CHARACTERS);

            return new PasswordChecker(
                    minLength,
                    maxLength,
                    charGroupsCopy(),
                    groupMinCountsCopy(),
                    groupMaxCountsCopy());
        }

        private boolean isSetCountSumLargerThanMaxPasswordCount() {
            int sum = 0;
            for (int count: groupMinCounts())
                sum += count;
            return sum > maxLength;
        }

        @Override
        Factory getThis() {
            return this;
        }

        // !! The 4 methods below are only overloaded for documentation purpose !!

        /**
         * Add a group of allowed characters in the composition of the password.
         * @param charGroup a <code>String</code> containing all characters allowed in this group
         * @return <code>this</code> factory
         * @throws IllegalArgumentException if the character group contains duplicates or if the character group
         * contains characters already present in other character groups, unless duplicates have been explicitly
         * allowed by calling <code>disallowDuplicateCharacters(false)</code>
         * @see #disallowDuplicateCharacters(boolean)
         */
        @Override
        public Factory addCharGroup(String charGroup) {
            return super.addCharGroup(charGroup);
        }

        /**
         * Add a group of allowed characters in the composition of the password and specifies a minimum character
         * count.
         * @param charGroup a <code>String</code> containing all characters allowed in this group
         * @param minCount minimum number of characters from this group that must be present in the password
         * @return <code>this</code> factory
         * @throws IllegalArgumentException if the character group contains duplicates or if the character group
         * contains characters already present in other character groups, unless duplicates have been explicitly
         * allowed by calling <code>disallowDuplicateCharacters(false)</code>
         * @throws IllegalArgumentException if <code>minCount &lt; 0</code>
         * @see #disallowDuplicateCharacters(boolean)
         */
        @Override
        public Factory addCharGroup(String charGroup, int minCount) {
            return super.addCharGroup(charGroup, minCount, 0);
        }

        /**
         * Add a group of allowed characters in the composition of the password and specifies a minimum and maximum
         * character count.
         * @param charGroup a <code>String</code> containing all characters allowed in this group
         * @param minCount minimum number of characters from this group that must be present in the password
         * @param maxCount maximum number of characters from this group allowed in the password; a value of
         *                 <code>0</code> (zero) means "unlimited" (same as calling
         *                 {@link #addCharGroup(String, int) addCharGroup(String, int)})
         * @return <code>this</code> factory
         * @throws IllegalArgumentException if the character group contains duplicates or if the character group
         * contains characters already present in other character groups, unless duplicates have been explicitly
         * allowed by calling <code>disallowDuplicateCharacters(false)</code>
         * @throws IllegalArgumentException if <code>minCount &lt; 0</code>, or <code>maxCount &lt; 0</code>, or
         * <code>maxCount &lt; minCount</code> (unless <code>maxCount == 0</code>)
         * @see #disallowDuplicateCharacters(boolean)
         */
        @Override
        public Factory addCharGroup(String charGroup, int minCount, int maxCount) {
            return super.addCharGroup(charGroup, minCount, maxCount);
        }

        /**
         * Disallow or allow duplicates inside character groups and between character groups. Allowing duplicate is
         * usually unnecessary and error-prone.
         * @param disallowDuplicateCharacters <code>true</code> if duplicate characters should be disallowed (default),
         *                                    <code>false</code> otherwise
         * @return <code>this</code> factory
         */
        @Override
        public Factory disallowDuplicateCharacters(boolean disallowDuplicateCharacters) {
            return super.disallowDuplicateCharacters(disallowDuplicateCharacters);
        }

    }

}
