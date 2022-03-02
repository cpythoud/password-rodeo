package rodeo.password.pgencheck;

/**
 * Records a password validation error.
 * <p>
 * For some errors, more information need to be reported. For these errors, a subclass of this class is used.
 */
public class PasswordCheckError {

    private final PasswordCheckStatus errorType;

    PasswordCheckError(PasswordCheckStatus errorType) {
        this.errorType = errorType;
    }

    static PasswordCheckError tooShort() {
        return new PasswordCheckError(PasswordCheckStatus.TOO_SHORT);
    }

    static PasswordCheckError tooLong() {
        return new PasswordCheckError(PasswordCheckStatus.TOO_LONG);
    }

    static PasswordCheckError illegalCharacter(int illegalCharacter) {
        return new IllegalCharacterError(illegalCharacter);
    }

    static PasswordCheckError notEnoughOfCharacterType(
            int missingCharacterListIndex,
            String missingCharacterList,
            int expectedCount,
            int actualCount) {
        return new BadCountForCharacterTypeError(
                PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP,
                missingCharacterListIndex,
                missingCharacterList,
                expectedCount,
                actualCount);
    }

    static PasswordCheckError tooManyOfCharacterType(
            int missingCharacterListIndex,
            String missingCharacterList,
            int expectedCount,
            int actualCount) {
        return new BadCountForCharacterTypeError(
                PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP,
                missingCharacterListIndex,
                missingCharacterList,
                expectedCount,
                actualCount);
    }

    /**
     * Returns the recorded error types.
     * @return the error type
     * @see PasswordCheckStatus
     */
    public PasswordCheckStatus getErrorType() {
        return errorType;
    }

}
