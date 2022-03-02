package rodeo.password.pgencheck;

final class ErrorMessages {

    static final String AT_LEAST_ONE_CHAR = "Minimum character count must be at least 1. Value received: ";

    static final String CHAR_GROUP_NULL = "Character group cannot be null";

    static final String CHAR_GROUP_EMPTY = "Character group cannot be empty and must contain at least one character";

    static final String DUPLICATE_CHARS_FOUND_IN_GROUP =
            "Character group contains duplicates. This is not allowed. Duplicates: ";

    static final String DUPLICATE_CHARS_FOUND_IN_OTHER_GROUP =
            "Character group duplicates some characters from an other group. This is not allowed. Duplicates: ";

    static final String MAX_BIGGER_THAN_MIN = "Maximum character count cannot be smaller than minimum count: ";

    static final String MAX_CHAR_COUNT_NEG = "Maximum character count cannot be negative. Value received: ";

    static final String MIN_CHAR_COUNT_NEG = "Minimum character count cannot be negative. Value received: ";

    static final String NO_CHECKER_CHAR_SET_PROVIDED =
            "At least one charset must be specified before a PasswordChecker can be created";

    static final String NO_MAKER_CHAR_SET_PROVIDED =
            "At least one charset must be specified before a PasswordMaker can be created";

    static final String NOT_ENOUGH_CHARACTERS = "Conditions can never be fulfilled. "
            + "Not enough characters in password to satisfy all conditions, assuming character groups are disjoint";

    static final String TOO_MANY_CHAR_BY_TYPE_FOR_LENGTH =
            "Sum of required characters by type is greater than password length";

    static final String TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH1 =
            "Restrictions on character type counts would prevent a password of length ";

    static final String TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH2 = " from being generated";

    private ErrorMessages() {
        throw new UnsupportedOperationException();
    }

}
