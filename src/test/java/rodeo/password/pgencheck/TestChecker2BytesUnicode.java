package rodeo.password.pgencheck;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static rodeo.password.pgencheck.CharacterSets.LOWER_CASE;

public class TestChecker2BytesUnicode {

    private static final String UNICODE_TEST_CHARS = "®Δ你好ಡತع";

    private static final PasswordChecker WEIRD_CHECKER = PasswordChecker
            .factory()
            .setMinMaxLength(1, 5)
            .addCharGroup(LOWER_CASE, 1)
            .addCharGroup(UNICODE_TEST_CHARS, 1, 2)
            .create();

    @Test
    public void variousTests() {
        assertEquals(PasswordCheckStatus.TOO_LONG, WEIRD_CHECKER.check("abcdefgh"));
        assertEquals(PasswordCheckStatus.OK, WEIRD_CHECKER.check("ab你"));
        assertEquals(PasswordCheckStatus.OK, WEIRD_CHECKER.check("aತ你"));
        assertEquals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP, WEIRD_CHECKER.check("Δ你好"));
        assertEquals(PasswordCheckStatus.TOO_MANY_OF_CHARACTER_GROUP, WEIRD_CHECKER.check("aΔ你好"));
    }

}
