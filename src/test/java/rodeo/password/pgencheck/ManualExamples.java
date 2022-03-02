package rodeo.password.pgencheck;

import org.junit.jupiter.api.Test;

import java.util.Random;

public class ManualExamples {

    @Test
    public void example1() {
        // tag::minimal-check[]
        PasswordChecker checker = PasswordChecker.factory()
                .addCharGroup("abcdefgh")
                .create();
        // end::minimal-check[]
    }

    @Test
    public void example2() {
        var factory = PasswordChecker.factory();
        // tag::check-setMinMax[]
        factory.setMinMaxLength(32, 96); // min length = 32, max length = 96
        // end::check-setMinMax[]
        // tag::check-setGroup[]
        factory.addCharGroup("abcdef"); // no min or max count
        factory.addCharGroup("xyzuvw", 1); // at least one character from group, no max
        factory.addCharGroup("0123456789", 2, 5); // 2 chars from group but no more than 5
        // end::check-setGroup[]
        // tag::allow-duplicates[]
        factory.disallowDuplicateCharacters(false);
        // end::allow-duplicates[]
        // tag::create-checker[]
        PasswordChecker checker = factory.create();
        // end::create-checker[]
    }

    @Test
    public void example3() {
        // tag::full-create-checker-ex[]
        PasswordChecker checker = PasswordChecker.factory()
                .setMinMaxLength(32, 96)
                .addCharGroup(CharacterSets.LOWER_CASE, 1)
                .addCharGroup(CharacterSets.UPPER_CASE, 1)
                .addCharGroup(CharacterSets.DIGITS, 1, 2)
                .addCharGroup(CharacterSets.SYMBOLS, 0, 3)
                .create();
        // end::full-create-checker-ex[]
    }

    @Test
    public void example4() {
        // tag::minimal-maker[]
        PasswordMaker maker = PasswordMaker.factory()
                .addCharGroup("abcdefgh")
                .create();
        // end::minimal-maker[]
    }

    @Test
    public void example5() {
        var factory = PasswordMaker.factory();
        // tag::maker-length[]
        factory.setLength(32); // password length = 32
        // end::maker-length[]
        // tag::maker-setGroup[]
        factory.addCharGroup("abcdef"); // no min or max count
        factory.addCharGroup("xyzuvw", 1); // at least one character from group, no max
        factory.addCharGroup("0123456789", 2, 5); // 2 chars from group but no more than 5
        // end::maker-setGroup[]
        // tag::random-change[]
        factory.setRandomUIntGenerator(new CustomUIntGenerator());
        // end::random-change[]
        // tag::create-maker[]
        PasswordMaker passwordMaker = factory.create();
        // end::create-maker[]
    }

    // !! awful, for documentation purpose only, don't do this !!
    private static class CustomUIntGenerator implements RandomUIntGenerator {
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

    @Test
    public void example6() {
        // tag::full-create-maker-ex[]
        PasswordMaker passwordMaker = PasswordMaker.factory()
                .setLength(32)
                .addCharGroup(CharacterSets.LOWER_CASE, 1)
                .addCharGroup(CharacterSets.UPPER_CASE, 1)
                .addCharGroup(CharacterSets.DIGITS, 1, 2)
                .addCharGroup(CharacterSets.SYMBOLS, 0, 3)
                .create();
        // end::full-create-maker-ex[]
    }

    @Test
    public void example7() {
        // tag::full-monty[]
        PasswordChecker passwordChecker = PasswordChecker.factory()
                .setMinMaxLength(32, 32)
                .addCharGroup(CharacterSets.LOWER_CASE, 1)
                .addCharGroup(CharacterSets.UPPER_CASE, 1)
                .addCharGroup(CharacterSets.DIGITS, 1, 2)
                .addCharGroup(CharacterSets.SYMBOLS, 0, 3)
                .create();

        PasswordMaker passwordMaker = PasswordMaker.factory()
                .setLength(32)
                .addCharGroup(CharacterSets.LOWER_CASE, 1)
                .addCharGroup(CharacterSets.UPPER_CASE, 1)
                .addCharGroup(CharacterSets.DIGITS, 1, 2)
                .addCharGroup(CharacterSets.SYMBOLS, 0, 3)
                .create();

        for (int i = 0; i < 1_000_000; i++)
            assert passwordChecker.quickCheck(passwordMaker.create());
        // end::full-monty[]
    }

}
