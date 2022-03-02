package rodeo.password.pgencheck;

/**
 * Records an illegal character error in password validation.
 */
public class IllegalCharacterError extends PasswordCheckError {

    private final int illegalCharacter;

    IllegalCharacterError(int illegalCharacter) {
        super(PasswordCheckStatus.ILLEGAL_CHARACTER);
        this.illegalCharacter = illegalCharacter;
    }

    /**
     * Returns the illegal character as a code point.
     * @return the illegal character code point
     */
    public int getIllegalCodePoint() {
        return illegalCharacter;
    }

    /**
     * Returns the illegal character as a <code>String</code>.
     * @return the illegal character
     */
    public String getIllegalCharacter() {
        return Character.toString(illegalCharacter);
    }

}
