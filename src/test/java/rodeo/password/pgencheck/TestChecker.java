package rodeo.password.pgencheck;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static rodeo.password.pgencheck.CharacterGroups.DIGITS;
import static rodeo.password.pgencheck.CharacterGroups.LOWER_CASE;
import static rodeo.password.pgencheck.CharacterGroups.UPPER_CASE;

public class TestChecker {

    private static final PasswordChecker BASIC_CHECKER = PasswordChecker
            .factory()
            .addCharGroup(LOWER_CASE, 1)
            .addCharGroup(UPPER_CASE, 1)
            .addCharGroup(DIGITS, 1)
            .create();

    private static final String ALL_CHARS = LOWER_CASE + UPPER_CASE + DIGITS;
    private static final String OK_PATTERN = "abcdefABCDEF1234"; // 16 chars

    private static final String PWD_OK_1 = OK_PATTERN ;  // ok, 16 chars = lower limit
    private static final String PWD_OK_2 = OK_PATTERN + OK_PATTERN;  // ok, 32 chars = between limits
    private static final String PWD_OK_3 = OK_PATTERN + OK_PATTERN + OK_PATTERN + OK_PATTERN; // ok, 64 chars = upper limit

    private static final String PWD_TOO_SHORT1 = "aA1"; // way too short
    private static final String PWD_TOO_SHORT2 = "abcdefABCDEF123"; // one char too short
    private static final String PWD_TOO_LONG1 = PWD_OK_3 + "x";  // one char too much
    private static final String PWD_TOO_LONG2 = PWD_OK_3 + "xyz";  // 3 chars too much
    private static final String PWD_ILLEGAL_CHARS1 = "$badW1thEn0ugh0therChars";  // $ not allowed at beginning
    private static final String PWD_ILLEGAL_CHARS2 = "badW1thEn0ugh0therCh@rs";   // @ not allowed in the middle
    private static final String PWD_ILLEGAL_CHARS3 = "badW1thEn0ugh0therChars&";  // & not allowed at end
    private static final String PWD_MISSING_NUMBER = "abcdefghiABCDEFGHI";

    private static final PasswordChecker MAX_DIGIT_CHECKER = PasswordChecker
            .factory()
            .addCharGroup(LOWER_CASE, 1)
            .addCharGroup(UPPER_CASE, 1)
            .addCharGroup(DIGITS, 1, 3)
            .create();

    private static final String PWD_TOO_MANY_DIGITS1 = "abcdefgABCDEFG12";  // ok, 2 digits
    private static final String PWD_TOO_MANY_DIGITS2 = "abcdefgABCDEFG123";  // ok, 3 digits = max
    private static final String PWD_TOO_MANY_DIGITS3 = "abcdefgABCDEFG1234";  // just one digit too many
    private static final String PWD_TOO_MANY_DIGITS4 = "abcdefgABCDEFG12345";  // two digits too many

    @Test
    public void testConfig() {
        assertEquals(16, BASIC_CHECKER.getMinLength());
        assertEquals(64, BASIC_CHECKER.getMaxLength());
        assertEquals(ALL_CHARS, BASIC_CHECKER.getAllChars());

        List<String> charList = BASIC_CHECKER.getCharacterGroups();
        assertEquals(LOWER_CASE, charList.get(0));
        assertEquals(UPPER_CASE, charList.get(1));
        assertEquals(DIGITS, charList.get(2));

        assertEquals(3, BASIC_CHECKER.getCharacterGroupCount());

        assertEquals(LOWER_CASE, BASIC_CHECKER.getCharacterGroup(0));
        assertEquals(UPPER_CASE, BASIC_CHECKER.getCharacterGroup(1));
        assertEquals(DIGITS, BASIC_CHECKER.getCharacterGroup(2));
        var exception = assertThrows(IndexOutOfBoundsException.class, () -> BASIC_CHECKER.getCharacterGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> BASIC_CHECKER.getCharacterGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());

        assertEquals(1, BASIC_CHECKER.getMinCharactersInGroup(0));
        assertEquals(1, BASIC_CHECKER.getMinCharactersInGroup(1));
        assertEquals(1, BASIC_CHECKER.getMinCharactersInGroup(2));
        exception = assertThrows(IndexOutOfBoundsException.class, () -> BASIC_CHECKER.getMinCharactersInGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> BASIC_CHECKER.getMinCharactersInGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());

        assertEquals(0, BASIC_CHECKER.getMaxCharactersInGroup(0));
        assertEquals(0, BASIC_CHECKER.getMaxCharactersInGroup(1));
        assertEquals(0, BASIC_CHECKER.getMaxCharactersInGroup(2));
        exception = assertThrows(IndexOutOfBoundsException.class, () -> BASIC_CHECKER.getMaxCharactersInGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> BASIC_CHECKER.getMaxCharactersInGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());
    }

    @Test
    public void testOK() {
        assertTrue(BASIC_CHECKER.quickCheck(PWD_OK_1));
        assertTrue(BASIC_CHECKER.quickCheck(PWD_OK_2));
        assertTrue(BASIC_CHECKER.quickCheck(PWD_OK_3));
        assertEquals(PasswordCheckStatus.OK, BASIC_CHECKER.check(PWD_OK_1));
        assertEquals(PasswordCheckStatus.OK, BASIC_CHECKER.check(PWD_OK_2));
        assertEquals(PasswordCheckStatus.OK, BASIC_CHECKER.check(PWD_OK_3));
        assertTrue(BASIC_CHECKER.fullCheck(PWD_OK_1).isEmpty());
        assertTrue(BASIC_CHECKER.fullCheck(PWD_OK_2).isEmpty());
        assertTrue(BASIC_CHECKER.fullCheck(PWD_OK_3).isEmpty());
    }

    @Test
    public void testTooShort() {
        assertFalse(BASIC_CHECKER.quickCheck(PWD_TOO_SHORT1));
        assertFalse(BASIC_CHECKER.quickCheck(PWD_TOO_SHORT2));
        assertEquals(PasswordCheckStatus.TOO_SHORT, BASIC_CHECKER.check(PWD_TOO_SHORT1));
        assertEquals(PasswordCheckStatus.TOO_SHORT, BASIC_CHECKER.check(PWD_TOO_SHORT2));
        assertEquals(PasswordCheckStatus.TOO_SHORT, BASIC_CHECKER.fullCheck(PWD_TOO_SHORT1).get(0).getErrorType());
        assertEquals(PasswordCheckStatus.TOO_SHORT, BASIC_CHECKER.fullCheck(PWD_TOO_SHORT2).get(0).getErrorType());
    }

    @Test
    public void testTooLong() {
        assertFalse(BASIC_CHECKER.quickCheck(PWD_TOO_LONG1));
        assertFalse(BASIC_CHECKER.quickCheck(PWD_TOO_LONG2));
        assertEquals(PasswordCheckStatus.TOO_LONG, BASIC_CHECKER.check(PWD_TOO_LONG1));
        assertEquals(PasswordCheckStatus.TOO_LONG, BASIC_CHECKER.check(PWD_TOO_LONG2));
        assertEquals(PasswordCheckStatus.TOO_LONG, BASIC_CHECKER.fullCheck(PWD_TOO_LONG1).get(0).getErrorType());
        assertEquals(PasswordCheckStatus.TOO_LONG, BASIC_CHECKER.fullCheck(PWD_TOO_LONG2).get(0).getErrorType());
    }

    @Test
    public void testIllegalCharacter() {
        assertFalse(BASIC_CHECKER.quickCheck(PWD_ILLEGAL_CHARS1));
        assertFalse(BASIC_CHECKER.quickCheck(PWD_ILLEGAL_CHARS2));
        assertFalse(BASIC_CHECKER.quickCheck(PWD_ILLEGAL_CHARS3));
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, BASIC_CHECKER.check(PWD_ILLEGAL_CHARS1));
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, BASIC_CHECKER.check(PWD_ILLEGAL_CHARS2));
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, BASIC_CHECKER.check(PWD_ILLEGAL_CHARS3));
        PasswordCheckError error = BASIC_CHECKER.fullCheck(PWD_ILLEGAL_CHARS1).get(0);
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, error.getErrorType());
        assertTrue(error instanceof IllegalCharacterError);
        assertEquals("$".codePointAt(0), ((IllegalCharacterError) error).getIllegalCodePoint());
        assertEquals("$", ((IllegalCharacterError) error).getIllegalCharacter());
        error = BASIC_CHECKER.fullCheck(PWD_ILLEGAL_CHARS2).get(0);
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, error.getErrorType());
        assertTrue(error instanceof IllegalCharacterError);
        assertEquals("@".codePointAt(0), ((IllegalCharacterError) error).getIllegalCodePoint());
        assertEquals("@", ((IllegalCharacterError) error).getIllegalCharacter());
        error = BASIC_CHECKER.fullCheck(PWD_ILLEGAL_CHARS3).get(0);
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, error.getErrorType());
        assertTrue(error instanceof IllegalCharacterError);
        assertEquals("&".codePointAt(0), ((IllegalCharacterError) error).getIllegalCodePoint());
        assertEquals("&", ((IllegalCharacterError) error).getIllegalCharacter());
    }

    @Test
    public void testMissingCharGroup() {
        assertFalse(BASIC_CHECKER.quickCheck(PWD_MISSING_NUMBER));
        assertEquals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP, BASIC_CHECKER.check(PWD_MISSING_NUMBER));
        PasswordCheckError error = BASIC_CHECKER.fullCheck(PWD_MISSING_NUMBER).get(0);
        assertEquals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP, error.getErrorType());
        assertTrue(error instanceof BadCountForCharacterTypeError);
        assertEquals(2, ((BadCountForCharacterTypeError) error).getCharacterGroupIndex());
        assertEquals(DIGITS, ((BadCountForCharacterTypeError) error).getCharacterGroup());
        assertEquals(1, ((BadCountForCharacterTypeError) error).getExpectedCount());
        assertEquals(0, ((BadCountForCharacterTypeError) error).getActualCount());
    }

    @Test
    public void testTooManyCharsInGroup() {
        assertTrue(MAX_DIGIT_CHECKER.quickCheck(PWD_TOO_MANY_DIGITS1));
        assertTrue(MAX_DIGIT_CHECKER.quickCheck(PWD_TOO_MANY_DIGITS2));
        assertFalse(MAX_DIGIT_CHECKER.quickCheck(PWD_TOO_MANY_DIGITS3));
        assertFalse(MAX_DIGIT_CHECKER.quickCheck(PWD_TOO_MANY_DIGITS4));
        assertEquals(PasswordCheckStatus.OK, MAX_DIGIT_CHECKER.check(PWD_TOO_MANY_DIGITS1));
        assertEquals(PasswordCheckStatus.OK, MAX_DIGIT_CHECKER.check(PWD_TOO_MANY_DIGITS2));
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, MAX_DIGIT_CHECKER.check(PWD_TOO_MANY_DIGITS3));
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, MAX_DIGIT_CHECKER.check(PWD_TOO_MANY_DIGITS4));
        assertTrue(MAX_DIGIT_CHECKER.fullCheck(PWD_TOO_MANY_DIGITS1).isEmpty());
        assertTrue(MAX_DIGIT_CHECKER.fullCheck(PWD_TOO_MANY_DIGITS2).isEmpty());
        PasswordCheckError error = MAX_DIGIT_CHECKER.fullCheck(PWD_TOO_MANY_DIGITS3).get(0);
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, error.getErrorType());
        assertTrue(error instanceof BadCountForCharacterTypeError);
        assertEquals(2, ((BadCountForCharacterTypeError) error).getCharacterGroupIndex());
        assertEquals(DIGITS, ((BadCountForCharacterTypeError) error).getCharacterGroup());
        assertEquals(3, ((BadCountForCharacterTypeError) error).getExpectedCount());
        assertEquals(4, ((BadCountForCharacterTypeError) error).getActualCount());
        error = MAX_DIGIT_CHECKER.fullCheck(PWD_TOO_MANY_DIGITS4).get(0);
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, error.getErrorType());
        assertTrue(error instanceof BadCountForCharacterTypeError);
        assertEquals(2, ((BadCountForCharacterTypeError) error).getCharacterGroupIndex());
        assertEquals(DIGITS, ((BadCountForCharacterTypeError) error).getCharacterGroup());
        assertEquals(3, ((BadCountForCharacterTypeError) error).getExpectedCount());
        assertEquals(5, ((BadCountForCharacterTypeError) error).getActualCount());
    }

    @Test
    public void testMaxDigitsConfig() {
        assertEquals(16, MAX_DIGIT_CHECKER.getMinLength());
        assertEquals(64, MAX_DIGIT_CHECKER.getMaxLength());
        assertEquals(ALL_CHARS, MAX_DIGIT_CHECKER.getAllChars());

        List<String> charList = MAX_DIGIT_CHECKER.getCharacterGroups();
        assertEquals(LOWER_CASE, charList.get(0));
        assertEquals(UPPER_CASE, charList.get(1));
        assertEquals(DIGITS, charList.get(2));

        assertEquals(3, MAX_DIGIT_CHECKER.getCharacterGroupCount());

        assertEquals(LOWER_CASE, MAX_DIGIT_CHECKER.getCharacterGroup(0));
        assertEquals(UPPER_CASE, MAX_DIGIT_CHECKER.getCharacterGroup(1));
        assertEquals(DIGITS, MAX_DIGIT_CHECKER.getCharacterGroup(2));
        var exception = assertThrows(IndexOutOfBoundsException.class, () -> MAX_DIGIT_CHECKER.getCharacterGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> MAX_DIGIT_CHECKER.getCharacterGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());

        assertEquals(1, MAX_DIGIT_CHECKER.getMinCharactersInGroup(0));
        assertEquals(1, MAX_DIGIT_CHECKER.getMinCharactersInGroup(1));
        assertEquals(1, MAX_DIGIT_CHECKER.getMinCharactersInGroup(2));
        exception = assertThrows(IndexOutOfBoundsException.class, () -> MAX_DIGIT_CHECKER.getMinCharactersInGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> MAX_DIGIT_CHECKER.getMinCharactersInGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());

        assertEquals(0, MAX_DIGIT_CHECKER.getMaxCharactersInGroup(0));
        assertEquals(0, MAX_DIGIT_CHECKER.getMaxCharactersInGroup(1));
        assertEquals(3, MAX_DIGIT_CHECKER.getMaxCharactersInGroup(2));
        exception = assertThrows(IndexOutOfBoundsException.class, () -> MAX_DIGIT_CHECKER.getMaxCharactersInGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> MAX_DIGIT_CHECKER.getMaxCharactersInGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());
    }

    @Test
    public void testDuplicateChars() {
        String duplicatesInGroup = "AAbcdefghijk";
        String duplicatesInterGroup = "jklmnopqrstu";

        var checker = PasswordChecker.factory()
                .setMinMaxLength(2, 16)
                .disallowDuplicateCharacters(false)
                .addCharGroup(duplicatesInGroup, 1) // duplicate in char group
                .addCharGroup(duplicatesInterGroup, 1) // duplicates 'jk' across char group
                .create();

        assertFalse(checker.quickCheck("bcdef"));
        assertEquals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP, checker.check("bcdef"));
        assertEquals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP, checker.fullCheck("bcdef").get(0).getErrorType());

        assertTrue(checker.quickCheck("jk"));
        assertEquals(PasswordCheckStatus.OK, checker.check("jk"));
        assertTrue(checker.fullCheck("jk").isEmpty());
    }

}
