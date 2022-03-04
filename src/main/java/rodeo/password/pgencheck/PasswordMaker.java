package rodeo.password.pgencheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static rodeo.password.pgencheck.ErrorMessages.AT_LEAST_ONE_CHAR;
import static rodeo.password.pgencheck.ErrorMessages.NO_MAKER_CHAR_SET_PROVIDED;
import static rodeo.password.pgencheck.ErrorMessages.TOO_MANY_CHAR_BY_TYPE_FOR_LENGTH;
import static rodeo.password.pgencheck.ErrorMessages.TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH1;
import static rodeo.password.pgencheck.ErrorMessages.TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH2;

/**
 * Create passwords according to predefined criteria.
 * <p>
 * The following criteria are available:
 * <ul>
 *     <li>length;</li>
 *     <li>
 *         constraints on characters composing the password:
 *         <ul>
 *             <li>character must belong to a predefined group of characters;</li>
 *             <li>minimum and maximum number of characters per group can be specified;</li>
 *         </ul>
 *     </li>
 *     <li>
 *         the way random numbers are used in password generation can be configured by implementing a
 *         {@link RandomUIntGenerator RandomUIntGenerator}.
 *     </li>
 * </ul>
 */
public final class PasswordMaker extends PasswordData {

    private final int length;
    private final RandomUIntGenerator randomUIntGenerator;

    private final List<PasswordChar> passwordChars = new ArrayList<>();
    private final List<List<PasswordChar>> groupPasswordChars = new ArrayList<>();

    private PasswordMaker(
            int length,
            List<String> charGroups,
            List<Integer> groupMinCounts,
            List<Integer> groupMaxCounts,
            RandomUIntGenerator randomUIntGenerator)
    {
        super(charGroups, groupMinCounts, groupMaxCounts);
        this.length = length;
        this.randomUIntGenerator = randomUIntGenerator;

        for (int groupIndex = 0; groupIndex < charGroups.size(); ++groupIndex) {
            var pcs = new ArrayList<PasswordChar>();
            for (int codePoint: charGroups.get(groupIndex).codePoints().toArray())
                pcs.add(new PasswordChar(codePoint, groupIndex));

            passwordChars.addAll(pcs);
            groupPasswordChars.add(pcs);
        }
    }

    /**
     * Create a factory to specify password generation criteria and create a <code>PasswordMaker</code> object.
     * @return an internal <code>PasswordMaker</code> factory
     * @see PasswordMaker.Factory
     */
    public static Factory factory() {
        return new Factory();
    }

    /**
     * Returns the specified length for generated passwords.
     * @return the specified length for generated passwords
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the implementation of the random number generator used to create passwords.
     * @return the implementation of the random number generator used to create passwords
     * @see RandomUIntGenerator
     * @see DefaultUIntGenerator
     */
    public RandomUIntGenerator getRandomUIntGenerator() {
        return randomUIntGenerator;
    }

    /**
     * Generate a new password.
     * @return the generated password
     */
    public String create() {
        var charList = passwordChars;
        var groupCounts = initGroupCounts();
        var passwordChars = new ArrayList<Integer>();

        for (int i = 0; i < groupCounts.size(); i++) {
            for (int j = 0; j < groupCounts.get(i).min; j++) {
                passwordChars.add(getRandomCharacter(groupPasswordChars.get(i)).getCodePoint());
                groupCounts.get(i).count++;
            }
        }

        while (passwordChars.size() < length) {
            var pc = getRandomCharacter(charList);
            var groupCount = groupCounts.get(pc.getCharGroupIndex());
            if (groupCount.canAddChar()) {
                passwordChars.add(pc.getCodePoint());
                groupCount.count++;
            } else
                charList = updateCharacterList(groupCounts);
        }

        Collections.shuffle(passwordChars, randomUIntGenerator.random());

        var password = new StringBuilder();
        for (int cp: passwordChars)
            password.appendCodePoint(cp);

        return password.toString();
    }

    private List<GroupCount> initGroupCounts() {
        var groupCounts = new ArrayList<GroupCount>();
        for (int groupIndex = 0; groupIndex < charGroups().size(); ++groupIndex)
            groupCounts.add(new GroupCount(groupMinCounts().get(groupIndex), groupMaxCounts().get(groupIndex)));
        return groupCounts;
    }

    private PasswordChar getRandomCharacter(List<PasswordChar> passwordChars) {
        return passwordChars.get(randomUIntGenerator.getNextUInt(passwordChars.size()));
    }

    private List<PasswordChar> updateCharacterList(List<GroupCount> groupCounts) {
        var passwordChars = new ArrayList<PasswordChar>();

        for (int i = 0; i < groupCounts.size(); i++)
            if (groupCounts.get(i).canAddChar())
                passwordChars.addAll(groupPasswordChars.get(i));

        return passwordChars;
    }

    private static final class PasswordChar {

        private final int codePoint;
        private final int charGroupIndex;

        PasswordChar(int codePoint, int charGroupIndex) {
            this.codePoint = codePoint;
            this.charGroupIndex = charGroupIndex;
        }

        int getCodePoint() {
            return codePoint;
        }

        int getCharGroupIndex() {
            return charGroupIndex;
        }

    }

    private static class GroupCount {
        final int min;
        final int max;
        int count = 0;

        GroupCount(int min, int max) {
            this.min = min;
            this.max = max;
        }

        boolean canAddChar() {
            return max == 0 || count < max;
        }
    }

    /**
     * Internal factory to create <code>PasswordMaker</code>s.
     * <p>
     * This factory class allows you to build a <code>PasswordMaker</code> using a fluent interface. You create a
     * <code>Factory</code> by calling {@link PasswordMaker#factory() PasswordMaker.factory()}.
     * Once all the criteria have been specified, you call the {@link #create() create} function
     * to create a <code>PasswordMaker</code> object.
     */
    public static final class Factory extends AbstractFactory<Factory> {

        private int length = 16;
        private RandomUIntGenerator randomUIntGenerator = DefaultUIntGenerator.GENERATOR;

        private Factory() { }

        /**
         * Sets the length of generated password.
         * @param length the password length
         * @return <code>this</code> factory
         */
        public Factory setLength(int length) {
            if (length < 1)
                throw new IllegalArgumentException(AT_LEAST_ONE_CHAR + length);

            this.length = length;
            return this;
        }

        /**
         * Specify the method used to generate random numbers used for password generation.
         * @param randomUIntGenerator an implementation of the {@link RandomUIntGenerator RandomUIntGenerator}
         *                            interface
         * @return <code>this</code> factory
         * @see RandomUIntGenerator
         * @see DefaultUIntGenerator
         */
        public Factory setRandomUIntGenerator(RandomUIntGenerator randomUIntGenerator) {
            this.randomUIntGenerator = randomUIntGenerator;
            return this;
        }

        /**
         * Create a <code>PasswordMaker</code> according to the specified criteria.
         * @return a new <code>PasswordMaker</code> matching the specified criteria
         * @throws IllegalStateException if no character group has been specified
         * @throws IllegalStateException if password generation would be impossible because the minimum count
         * requirements on character groups would exceed the maximum length allowed for the password
         * @throws IllegalStateException if password generation would be impossible because too many restrictions
         * are placed on the maximum character count of each character group so that the specified password length
         * could not be reached.
         */
        public PasswordMaker create() {
            if (charGroups().isEmpty())
                throw new IllegalStateException(NO_MAKER_CHAR_SET_PROVIDED);

            if (sumOfRequiredCharactersIsGreaterThanPasswordLength())
                throw new IllegalStateException(TOO_MANY_CHAR_BY_TYPE_FOR_LENGTH);

            if (passwordTooLongForPerCharTypeCountRestrictions())
                throw new IllegalStateException(TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH1
                        + length + TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH2);

            return new PasswordMaker(length, charGroupsCopy(), groupMinCountsCopy(), groupMaxCountsCopy(), randomUIntGenerator);
        }

        private boolean sumOfRequiredCharactersIsGreaterThanPasswordLength() {
            int sum = groupMaxCounts().stream().reduce(0, Integer::sum);
            return sum > length;
        }

        private boolean passwordTooLongForPerCharTypeCountRestrictions() {
            int sum = 0;
            for (int maxCount: groupMaxCounts()) {
                if (maxCount == 0)
                    return false;
                sum += maxCount;
            }

            return sum < length;
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
         * Add a group of allowed characters in the composition of the password and specifies a minimum and a maximum
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
        public Factory addCharGroup(String charGroup, int minCount, int maxCount) {
            return super.addCharGroup(charGroup, minCount, maxCount);
        }

        /**
         * Disallow or allow duplicate character groups and between character groups. Allowing duplicate is
         * usually unnecessary and error-prone.
         * @param disallowDuplicateCharacters <code>true</code> if duplicates character should be disallowed (default),
         *                                    <code>false</code> otherwise
         * @return <code>this</code> factory
         */
        @Override
        public Factory disallowDuplicateCharacters(boolean disallowDuplicateCharacters) {
            return super.disallowDuplicateCharacters(disallowDuplicateCharacters);
        }

    }

}
