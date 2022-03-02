package rodeo.password.pgencheck;

/**
 * Record a bad character count error in password validation.
 * <p>
 * This class is used to record an error when a password does not contain enough representatives of a certain
 * character group or when it contains too many of these.
 */
public class BadCountForCharacterTypeError extends PasswordCheckError {

    private final int characterListIndex;
    private final String characterList;
    private final int expectedCount;
    private final int actualCount;

    BadCountForCharacterTypeError(
            PasswordCheckStatus status,
            int characterListIndex,
            String characterList,
            int expectedCount,
            int actualCount) {
        super(status);
        this.characterListIndex = characterListIndex;
        this.characterList = characterList;
        this.expectedCount = expectedCount;
        this.actualCount = actualCount;

        assert status.equals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP) || status.equals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP);
    }

    /**
     * Returns the index of the character group. The groups are indexed in their order of insertion via one of the
     * <code>PasswordChecker.Factory.addCharGroup</code> functions.
     * @return the index of the character group.
     * @see PasswordChecker.Factory
     */
    public int getCharacterGroupIndex() {
        return characterListIndex;
    }

    /**
     * Returns the character group.
     * @return the character group
     */
    public String getCharacterGroup() {
        return characterList;
    }

    /**
     * Returns either the expected minimum or expected maximum representatives expected for the character group,
     * depending on the error type: the minimum for
     * {@link PasswordCheckStatus#NOT_ENOUGH_OF_CHARACTER_GROUP PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP}
     * and the maximum for
     * {@link PasswordCheckStatus#TOO_MANY_OF_CHARACTER_GROUP PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP}.
     * @return the minimum or maximum expected count
     */
    public int getExpectedCount() {
        return expectedCount;
    }

    /**
     * Returns the actual, problematic, count.
     * @return the problematic count
     */
    public int getActualCount() {
        return actualCount;
    }

}
