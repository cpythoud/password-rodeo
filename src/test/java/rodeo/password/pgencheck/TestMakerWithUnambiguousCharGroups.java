package rodeo.password.pgencheck;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static rodeo.password.pgencheck.CharacterGroups.UNAMBIGUOUS_DIGITS;
import static rodeo.password.pgencheck.CharacterGroups.UNAMBIGUOUS_LOWER_CASE;
import static rodeo.password.pgencheck.CharacterGroups.UNAMBIGUOUS_SYMBOLS;
import static rodeo.password.pgencheck.CharacterGroups.UNAMBIGUOUS_UPPER_CASE;

public class TestMakerWithUnambiguousCharGroups {

    private static final int ITERATIONS = 1_000_000;

    private static final PasswordMaker BASIC_MAKER = PasswordMaker
            .factory()
            .addCharGroup(UNAMBIGUOUS_LOWER_CASE, 1)
            .addCharGroup(UNAMBIGUOUS_UPPER_CASE, 1)
            .addCharGroup(UNAMBIGUOUS_DIGITS, 1)
            .create();

    private static final String ALL_CHARS = UNAMBIGUOUS_LOWER_CASE + UNAMBIGUOUS_UPPER_CASE + UNAMBIGUOUS_DIGITS;

    private static final PasswordChecker BASIC_CHECKER = PasswordChecker
            .factory()
            .addCharGroup(UNAMBIGUOUS_LOWER_CASE, 1)
            .addCharGroup(UNAMBIGUOUS_UPPER_CASE, 1)
            .addCharGroup(UNAMBIGUOUS_DIGITS, 1)
            .create();

    @Test
    public void testConfig() {
        assertEquals(16, BASIC_MAKER.getLength());
        assertEquals(ALL_CHARS, BASIC_MAKER.getAllChars());
        assertEquals(DefaultUIntGenerator.GENERATOR, BASIC_MAKER.getRandomUIntGenerator());
    }

    @Test
    public void testAltConfig() {
        RandomUIntGenerator uIntGenerator = new BadUIntGenerator();
        PasswordMaker maker = PasswordMaker.factory()
                .setLength(12)
                .addCharGroup(UNAMBIGUOUS_LOWER_CASE, 1)
                .addCharGroup(UNAMBIGUOUS_UPPER_CASE, 1, 5)
                .setRandomUIntGenerator(uIntGenerator)
                .create();
        assertEquals(12, maker.getLength());
        assertEquals(UNAMBIGUOUS_LOWER_CASE + UNAMBIGUOUS_UPPER_CASE, maker.getAllChars());
        assertEquals(uIntGenerator, maker.getRandomUIntGenerator());
    }

    @Test
    public void basicGenerationTest() {
        for (int i = 0; i < ITERATIONS; i++) {
            String password = BASIC_MAKER.create();
            assertTrue(BASIC_CHECKER.quickCheck(password));
            assertEquals(PasswordCheckStatus.OK, BASIC_CHECKER.check(password));
            assertTrue(BASIC_CHECKER.fullCheck(password).isEmpty());
        }
    }

    @Test
    public void multiConstraintTest() {
        PasswordMaker maker = PasswordMaker.factory()
                .setLength(32)
                .addCharGroup(UNAMBIGUOUS_LOWER_CASE, 1)
                .addCharGroup(UNAMBIGUOUS_UPPER_CASE, 1)
                .addCharGroup(UNAMBIGUOUS_DIGITS, 1, 5)
                .addCharGroup(UNAMBIGUOUS_SYMBOLS, 1, 3)
                .create();
        PasswordChecker checker = PasswordChecker.factory()
                .addCharGroup(UNAMBIGUOUS_LOWER_CASE, 1)
                .addCharGroup(UNAMBIGUOUS_UPPER_CASE, 1)
                .addCharGroup(UNAMBIGUOUS_DIGITS, 1, 5)
                .addCharGroup(UNAMBIGUOUS_SYMBOLS, 1, 3)
                .create();
        for (int i = 0; i < ITERATIONS; i++) {
            String password = maker.create();
            assertTrue(checker.quickCheck(password));
            assertEquals(PasswordCheckStatus.OK, checker.check(password));
            assertTrue(checker.fullCheck(password).isEmpty());
        }
    }

    private static class BadUIntGenerator implements RandomUIntGenerator {
        private final Random random = new Random();

        @Override
        public int getNextUInt(int max) {
            if (max < 2)
                throw new IllegalArgumentException("max must be >= 2");

            return random.nextInt(max);
        }

        @Override
        public Random random() {
            return random;
        }
    }
    
}
