package rodeo.password.pgencheck;

/**
 * Describes the result of password validation.
 * <p>
 * The <code>OK</code> value is returned by the {@link PasswordChecker#check(String) PasswordChecker.check} function
 * if the password satisfies all the criteria. One of the other values is returned if this is not the case.
 * <p>
 * <code>PasswordCheckStatus</code> values are also returned by
 * the {@link PasswordCheckError#getErrorType() PasswordCheckError.getErrorType} function.
 * @see PasswordChecker#check(String)
 * @see PasswordCheckError#getErrorType()
 */
public enum PasswordCheckStatus {

    /**
     * no problem found with the password
     */
    OK,
    /**
     * password is too short
     */
    TOO_SHORT,
    /**
     * password is too long
     */
    TOO_LONG,
    /**
     * password contains illegal characters, not present in any character group
     */
    ILLEGAL_CHARACTER,
    /**
     * password does not contain enough representatives of a character group
     */
    NOT_ENOUGH_OF_CHARACTER_GROUP,
    /**
     * password contains too many representatives from a character group
     */
    TOO_MANY_OF_CHARACTER_GROUP

}
