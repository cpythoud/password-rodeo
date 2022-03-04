package rodeo.password.pgencheck;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static rodeo.password.pgencheck.CharacterGroups.DIGITS;

public class TestHiraKata {

    private static final int ITERATIONS = 100_000;

    private static final String SOME_HIRAGANA = "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん";
    private static final String SOME_KATAKANA = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン";

    private static final String ALL_CHARS = SOME_HIRAGANA + SOME_KATAKANA + DIGITS;

    private static final PasswordChecker CHECKER = PasswordChecker
            .factory()
            .setMinMaxLength(12, 24)
            .addCharGroup(SOME_HIRAGANA, 1)
            .addCharGroup(SOME_KATAKANA, 1)
            .addCharGroup(DIGITS, 1, 3)
            .create();

    private static final PasswordMaker MAKER = PasswordMaker
            .factory()
            .setLength(18)
            .addCharGroup(SOME_HIRAGANA, 1)
            .addCharGroup(SOME_KATAKANA, 1)
            .addCharGroup(DIGITS, 1, 3)
            .create();

    private static final String OK_PATTERN = "すツカキぬね1ロワンいう"; // 12 chars
    private static final String PWD_OK_1 = OK_PATTERN ;  // ok, 12 chars = lower limit
    private static final String PWD_OK_2 = OK_PATTERN + "なに3セソタ";  // ok, 18 chars = between limits
    private static final String PWD_OK_3 = OK_PATTERN + OK_PATTERN; // ok, 24 chars = upper limit

    private static final String PWD_TOO_SHORT1 = "そク6トナニう8マミム";
    private static final String PWD_TOO_SHORT2 = "サシスセ2くけちつユ";
    private static final String PWD_TOO_LONG1 = PWD_OK_3 + "ネ";  // one char too much
    private static final String PWD_TOO_LONG2 = PWD_OK_3 + "うえお";  // 3 chars too much
    private static final String PWD_ILLEGAL_CHARS1 = "aオカキおか3ロワるれく";   // 'a' not allowed at beginning
    private static final String PWD_ILLEGAL_CHARS2 = "オカキbおか3ロワるれく";   // 'b' not allowed in the middle
    private static final String PWD_ILLEGAL_CHARS3 = "オカキおか3ロワるれくc";   // 'c' not allowed at end
    private static final String PWD_MISSING_NUMBER = "オカキおかサロワるれくノ";

    private static final String PWD_TOO_MANY_DIGITS1 = "オカキおかサロワるれくノ12";  // ok, 2 digits
    private static final String PWD_TOO_MANY_DIGITS2 = "オカキおかサロワるれくノ123";  // ok, 3 digits = max
    private static final String PWD_TOO_MANY_DIGITS3 = "オカキおかサロワるれくノ1234";  // just one digit too many
    private static final String PWD_TOO_MANY_DIGITS4 = "オカキおかサロワるれくノ12345";  // two digits too many

    @Test
    public void testCheckerConfig() {
        assertEquals(12, CHECKER.getMinLength());
        assertEquals(24, CHECKER.getMaxLength());
        assertEquals(ALL_CHARS, CHECKER.getAllChars());

        List<String> charList = CHECKER.getCharacterGroups();
        assertEquals(SOME_HIRAGANA, charList.get(0));
        assertEquals(SOME_KATAKANA, charList.get(1));
        assertEquals(DIGITS, charList.get(2));

        assertEquals(3, CHECKER.getCharacterGroupCount());

        assertEquals(SOME_HIRAGANA, CHECKER.getCharacterGroup(0));
        assertEquals(SOME_KATAKANA, CHECKER.getCharacterGroup(1));
        assertEquals(DIGITS, CHECKER.getCharacterGroup(2));
        var exception = assertThrows(IndexOutOfBoundsException.class, () -> CHECKER.getCharacterGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> CHECKER.getCharacterGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());

        assertEquals(1, CHECKER.getMinCharactersInGroup(0));
        assertEquals(1, CHECKER.getMinCharactersInGroup(1));
        assertEquals(1, CHECKER.getMinCharactersInGroup(2));
        exception = assertThrows(IndexOutOfBoundsException.class, () -> CHECKER.getMinCharactersInGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> CHECKER.getMinCharactersInGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());

        assertEquals(0, CHECKER.getMaxCharactersInGroup(0));
        assertEquals(0, CHECKER.getMaxCharactersInGroup(1));
        assertEquals(3, CHECKER.getMaxCharactersInGroup(2));
        exception = assertThrows(IndexOutOfBoundsException.class, () -> CHECKER.getMaxCharactersInGroup(3));
        assertEquals("Index must be between 0 and 2. Value received: 3", exception.getMessage());
        exception = assertThrows(IndexOutOfBoundsException.class, () -> CHECKER.getMaxCharactersInGroup(4));
        assertEquals("Index must be between 0 and 2. Value received: 4", exception.getMessage());
    }

    @Test
    public void testCheckerOK() {
        assertTrue(CHECKER.quickCheck(PWD_OK_1));
        assertTrue(CHECKER.quickCheck(PWD_OK_2));
        assertTrue(CHECKER.quickCheck(PWD_OK_3));
        assertEquals(PasswordCheckStatus.OK, CHECKER.check(PWD_OK_1));
        assertEquals(PasswordCheckStatus.OK, CHECKER.check(PWD_OK_2));
        assertEquals(PasswordCheckStatus.OK, CHECKER.check(PWD_OK_3));
        assertTrue(CHECKER.fullCheck(PWD_OK_1).isEmpty());
        assertTrue(CHECKER.fullCheck(PWD_OK_2).isEmpty());
        assertTrue(CHECKER.fullCheck(PWD_OK_3).isEmpty());
    }

    @Test
    public void testCheckerTooShort() {
        assertFalse(CHECKER.quickCheck(PWD_TOO_SHORT1));
        assertFalse(CHECKER.quickCheck(PWD_TOO_SHORT2));
        assertEquals(PasswordCheckStatus.TOO_SHORT, CHECKER.check(PWD_TOO_SHORT1));
        assertEquals(PasswordCheckStatus.TOO_SHORT, CHECKER.check(PWD_TOO_SHORT2));
        assertEquals(PasswordCheckStatus.TOO_SHORT, CHECKER.fullCheck(PWD_TOO_SHORT1).get(0).getErrorType());
        assertEquals(PasswordCheckStatus.TOO_SHORT, CHECKER.fullCheck(PWD_TOO_SHORT2).get(0).getErrorType());
    }

    @Test
    public void testCheckerTooLong() {
        assertFalse(CHECKER.quickCheck(PWD_TOO_LONG1));
        assertFalse(CHECKER.quickCheck(PWD_TOO_LONG2));
        assertEquals(PasswordCheckStatus.TOO_LONG, CHECKER.check(PWD_TOO_LONG1));
        assertEquals(PasswordCheckStatus.TOO_LONG, CHECKER.check(PWD_TOO_LONG2));
        assertEquals(PasswordCheckStatus.TOO_LONG, CHECKER.fullCheck(PWD_TOO_LONG1).get(0).getErrorType());
        assertEquals(PasswordCheckStatus.TOO_LONG, CHECKER.fullCheck(PWD_TOO_LONG2).get(0).getErrorType());
    }

    @Test
    public void testCheckerIllegalCharacter() {
        assertFalse(CHECKER.quickCheck(PWD_ILLEGAL_CHARS1));
        assertFalse(CHECKER.quickCheck(PWD_ILLEGAL_CHARS2));
        assertFalse(CHECKER.quickCheck(PWD_ILLEGAL_CHARS3));
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, CHECKER.check(PWD_ILLEGAL_CHARS1));
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, CHECKER.check(PWD_ILLEGAL_CHARS2));
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, CHECKER.check(PWD_ILLEGAL_CHARS3));
        PasswordCheckError error = CHECKER.fullCheck(PWD_ILLEGAL_CHARS1).get(0);
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, error.getErrorType());
        assertTrue(error instanceof IllegalCharacterError);
        assertEquals("a".codePointAt(0), ((IllegalCharacterError) error).getIllegalCodePoint());
        error = CHECKER.fullCheck(PWD_ILLEGAL_CHARS2).get(0);
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, error.getErrorType());
        assertTrue(error instanceof IllegalCharacterError);
        assertEquals("b".codePointAt(0), ((IllegalCharacterError) error).getIllegalCodePoint());
        error = CHECKER.fullCheck(PWD_ILLEGAL_CHARS3).get(0);
        assertEquals(PasswordCheckStatus.ILLEGAL_CHARACTER, error.getErrorType());
        assertTrue(error instanceof IllegalCharacterError);
        assertEquals("c".codePointAt(0), ((IllegalCharacterError) error).getIllegalCodePoint());
    }

    @Test
    public void testCheckerMissingCharGroup() {
        assertFalse(CHECKER.quickCheck(PWD_MISSING_NUMBER));
        assertEquals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP, CHECKER.check(PWD_MISSING_NUMBER));
        PasswordCheckError error = CHECKER.fullCheck(PWD_MISSING_NUMBER).get(0);
        assertEquals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP, error.getErrorType());
        assertTrue(error instanceof BadCountForCharacterTypeError);
        assertEquals(2, ((BadCountForCharacterTypeError) error).getCharacterGroupIndex());
        assertEquals(DIGITS, ((BadCountForCharacterTypeError) error).getCharacterGroup());
        assertEquals(1, ((BadCountForCharacterTypeError) error).getExpectedCount());
        assertEquals(0, ((BadCountForCharacterTypeError) error).getActualCount());
    }

    @Test
    public void testCheckerTooManyCharsInGroup() {
        assertTrue(CHECKER.quickCheck(PWD_TOO_MANY_DIGITS1));
        assertTrue(CHECKER.quickCheck(PWD_TOO_MANY_DIGITS2));
        assertFalse(CHECKER.quickCheck(PWD_TOO_MANY_DIGITS3));
        assertFalse(CHECKER.quickCheck(PWD_TOO_MANY_DIGITS4));
        assertEquals(PasswordCheckStatus.OK, CHECKER.check(PWD_TOO_MANY_DIGITS1));
        assertEquals(PasswordCheckStatus.OK, CHECKER.check(PWD_TOO_MANY_DIGITS2));
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, CHECKER.check(PWD_TOO_MANY_DIGITS3));
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, CHECKER.check(PWD_TOO_MANY_DIGITS4));
        assertTrue(CHECKER.fullCheck(PWD_TOO_MANY_DIGITS1).isEmpty());
        assertTrue(CHECKER.fullCheck(PWD_TOO_MANY_DIGITS2).isEmpty());
        PasswordCheckError error = CHECKER.fullCheck(PWD_TOO_MANY_DIGITS3).get(0);
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, error.getErrorType());
        assertTrue(error instanceof BadCountForCharacterTypeError);
        assertEquals(2, ((BadCountForCharacterTypeError) error).getCharacterGroupIndex());
        assertEquals(DIGITS, ((BadCountForCharacterTypeError) error).getCharacterGroup());
        assertEquals(3, ((BadCountForCharacterTypeError) error).getExpectedCount());
        assertEquals(4, ((BadCountForCharacterTypeError) error).getActualCount());
        error = CHECKER.fullCheck(PWD_TOO_MANY_DIGITS4).get(0);
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, error.getErrorType());
        assertTrue(error instanceof BadCountForCharacterTypeError);
        assertEquals(2, ((BadCountForCharacterTypeError) error).getCharacterGroupIndex());
        assertEquals(DIGITS, ((BadCountForCharacterTypeError) error).getCharacterGroup());
        assertEquals(3, ((BadCountForCharacterTypeError) error).getExpectedCount());
        assertEquals(5, ((BadCountForCharacterTypeError) error).getActualCount());
    }

    @Test
    public void testMakerConfig() {
        assertEquals(18, MAKER.getLength());
        assertEquals(ALL_CHARS, MAKER.getAllChars());
        assertEquals(DefaultUIntGenerator.GENERATOR, MAKER.getRandomUIntGenerator());
    }

    @Test
    public void basicGenerationTest() {
        for (int i = 0; i < ITERATIONS; i++) {
            String password = MAKER.create();
            assertTrue(CHECKER.quickCheck(password));
            assertEquals(PasswordCheckStatus.OK, CHECKER.check(password));
            assertTrue(CHECKER.fullCheck(password).isEmpty());
        }
    }
    
}
