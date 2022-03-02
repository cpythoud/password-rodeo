package rodeo.password.pgencheck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static rodeo.password.pgencheck.CharacterSets.DIGITS;
import static rodeo.password.pgencheck.CharacterSets.LOWER_CASE;
import static rodeo.password.pgencheck.CharacterSets.UPPER_CASE;

import static rodeo.password.pgencheck.ErrorMessages.AT_LEAST_ONE_CHAR;
import static rodeo.password.pgencheck.ErrorMessages.NO_MAKER_CHAR_SET_PROVIDED;
import static rodeo.password.pgencheck.ErrorMessages.TOO_MANY_CHAR_BY_TYPE_FOR_LENGTH;
import static rodeo.password.pgencheck.ErrorMessages.TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH1;
import static rodeo.password.pgencheck.ErrorMessages.TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH2;

public class TestMakerFactory {

    private PasswordMaker.Factory factory;

    @BeforeEach
    public void init() {
        factory = PasswordMaker.factory();
    }

    @Test
    public void setLengthTests() {
        var exception = assertThrows(IllegalArgumentException.class, () -> factory.setLength(-1));
        assertEquals(AT_LEAST_ONE_CHAR + "-1", exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> factory.setLength(0));
        assertEquals(AT_LEAST_ONE_CHAR + "0", exception.getMessage());
        assertEquals(factory, factory.setLength(1));
        assertEquals(factory, factory.setLength(12));
    }

    @Test
    public void noCharSetException() {
        var exception = assertThrows(IllegalStateException.class, () -> factory.create());
        assertEquals(NO_MAKER_CHAR_SET_PROVIDED, exception.getMessage());
    }

    @Test
    public void notEnoughCharsException() {
        factory.setLength(16).addCharGroup(CharacterSets.DIGITS, 18, 25);
        var exception = assertThrows(IllegalStateException.class, () -> factory.create());
        assertEquals(TOO_MANY_CHAR_BY_TYPE_FOR_LENGTH, exception.getMessage());
    }

    @Test
    public void tooManyCharRestrictionsException() {
        String exceptionMessage = TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH1
                + 10
                + TOO_MANY_RESTRICTIONS_ON_CHAR_BY_TYPE_FOR_LENGTH2;
        factory.setLength(10)
                .addCharGroup(LOWER_CASE, 1, 3)
                .addCharGroup(UPPER_CASE, 1, 3)
                .addCharGroup(DIGITS, 1, 3);
        var exception = assertThrows(IllegalStateException.class, () -> factory.create());
        assertEquals(exceptionMessage, exception.getMessage());
    }

}
