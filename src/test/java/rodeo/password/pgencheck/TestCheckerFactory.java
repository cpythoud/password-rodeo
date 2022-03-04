package rodeo.password.pgencheck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static rodeo.password.pgencheck.CharacterGroups.DIGITS;
import static rodeo.password.pgencheck.CharacterGroups.UPPER_CASE;

import static rodeo.password.pgencheck.ErrorMessages.AT_LEAST_ONE_CHAR;
import static rodeo.password.pgencheck.ErrorMessages.DUPLICATE_CHARS_FOUND_IN_GROUP;
import static rodeo.password.pgencheck.ErrorMessages.DUPLICATE_CHARS_FOUND_IN_OTHER_GROUP;
import static rodeo.password.pgencheck.ErrorMessages.MAX_BIGGER_THAN_MIN;
import static rodeo.password.pgencheck.ErrorMessages.NOT_ENOUGH_CHARACTERS;
import static rodeo.password.pgencheck.ErrorMessages.NO_CHECKER_CHAR_SET_PROVIDED;

public class TestCheckerFactory {

    private PasswordChecker.Factory factory;

    @BeforeEach
    public void init() {
        factory = PasswordChecker.factory();
    }

    @Test
    public void minMaxCharCountExceptions() {
        var exception = assertThrows(IllegalArgumentException.class, () -> factory.setMinMaxLength(-1, 5));
        assertEquals(AT_LEAST_ONE_CHAR + "-1", exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> factory.setMinMaxLength(0, 5));
        assertEquals(AT_LEAST_ONE_CHAR + "0", exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> factory.setMinMaxLength(6, 5));
        assertEquals(MAX_BIGGER_THAN_MIN + "5 < 6", exception.getMessage());
    }

    @Test
    public void testFluentInterfaceAndBasicCharGroupAdjonction() {
        var returnedFactory = factory.addCharGroup(UPPER_CASE);
        assertSame(factory, returnedFactory);
    }

    @Test
    public void testDuplicateCharsInGroup() {
        var exception = assertThrows(IllegalArgumentException.class, () -> factory.addCharGroup("AAbcdefghijk"));
        assertEquals(DUPLICATE_CHARS_FOUND_IN_GROUP + "A", exception.getMessage());
    }

    @Test
    public void testDuplicateCharsInOtherGroup() {
        factory.addCharGroup(DIGITS);
        var exception = assertThrows(IllegalArgumentException.class, () -> factory.addCharGroup(DIGITS));
        assertEquals(DUPLICATE_CHARS_FOUND_IN_OTHER_GROUP + DIGITS, exception.getMessage());
    }

    @Test
    public void testDuplicateCharsOK() {
        factory.disallowDuplicateCharacters(false);
        assertEquals(factory, factory.addCharGroup("AAbcdefghijk"));
        assertEquals(factory, factory.addCharGroup(DIGITS).addCharGroup(DIGITS));
    }

    @Test
    public void noCharSetException() {
        var exception = assertThrows(IllegalStateException.class, () -> factory.create());
        assertEquals(NO_CHECKER_CHAR_SET_PROVIDED, exception.getMessage());
    }

    @Test
    public void notEnoughCharsException() {
        factory.setMinMaxLength(8, 16).addCharGroup(CharacterGroups.DIGITS, 18, 25);
        var exception = assertThrows(IllegalStateException.class, () -> factory.create());
        assertEquals(NOT_ENOUGH_CHARACTERS, exception.getMessage());
    }

}
