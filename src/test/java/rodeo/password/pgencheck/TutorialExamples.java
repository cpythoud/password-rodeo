package rodeo.password.pgencheck;

import org.junit.jupiter.api.Test;

import java.util.List;

public class TutorialExamples {

    @Test
    public void example1() {
        // tag::basic-pwd-check[]
        var checker = PasswordChecker.factory()
                .setMinMaxLength(8, 16)
                .addCharGroup(CharacterGroups.LOWER_CASE, 1)
                .addCharGroup(CharacterGroups.UPPER_CASE, 1)
                .addCharGroup(CharacterGroups.DIGITS, 1)
                .create();
        // end::basic-pwd-check[]

        // tag::basic-quick-check[]
        assert checker.quickCheck("Abcdef12") == true;
        assert checker.quickCheck("Abcdef1") == false;   // too short
        assert checker.quickCheck("Abcdefgh") == false;  // no digit
        assert checker.quickCheck("@bcdef12") == false;  // illegal character
        // end::basic-quick-check[]

        // tag::basic-check[]
        assert checker.check("Abcdef12").equals(PasswordCheckStatus.OK);
        assert checker.check("Abcdef1").equals(PasswordCheckStatus.TOO_SHORT);
        assert checker.check("Abcdefgh").equals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP); // no digit
        assert checker.check("@bcdef12").equals(PasswordCheckStatus.ILLEGAL_CHARACTER);
        // end::basic-check[]

        // tag::basic-full-check[]
        List<PasswordCheckError> errors = checker.fullCheck("Ab$@");
        PasswordCheckError tooShort = errors.get(0);
        assert tooShort.getErrorType().equals(PasswordCheckStatus.TOO_SHORT);

        assert errors.get(1).getErrorType().equals(PasswordCheckStatus.ILLEGAL_CHARACTER);
        IllegalCharacterError illegalChar1 = (IllegalCharacterError) errors.get(1);
        assert illegalChar1.getIllegalCharacter().equals("$");

        assert errors.get(2).getErrorType().equals(PasswordCheckStatus.ILLEGAL_CHARACTER);
        IllegalCharacterError illegalChar2 = (IllegalCharacterError) errors.get(2);
        assert illegalChar2.getIllegalCharacter().equals("@");

        assert errors.get(3).getErrorType().equals(PasswordCheckStatus.NOT_ENOUGH_OF_CHARACTER_GROUP);
        BadCountForCharacterTypeError missingCharType = (BadCountForCharacterTypeError) errors.get(3);
        assert missingCharType.getCharacterGroup().equals(CharacterGroups.DIGITS);
        assert missingCharType.getExpectedCount() == 1;
        assert missingCharType.getActualCount() == 0;
        // end::basic-full-check[]
    }

    @Test
    public void example2() {
        // tag::sec-pwd-check[]
        var checker = PasswordChecker.factory()
                .setMinMaxLength(16, 64)
                .addCharGroup(CharacterGroups.LOWER_CASE, 1)
                .addCharGroup(CharacterGroups.UPPER_CASE, 1)
                .addCharGroup(CharacterGroups.DIGITS, 1)
                .addCharGroup(CharacterGroups.SYMBOLS)
                .create();
        // end::sec-pwd-check[]
    }

    @Test
    public void example3() {
        // tag::weird-pwd-check[]
        var checker = PasswordChecker.factory()
                .setMinMaxLength(32, 160)
                .addCharGroup(CharacterGroups.LOWER_CASE, 1)
                .addCharGroup(CharacterGroups.UPPER_CASE, 1)
                .addCharGroup(CharacterGroups.DIGITS, 1, 5)
                .addCharGroup(CharacterGroups.SYMBOLS, 1, 3)
                .create();
        // end::weird-pwd-check[]
    }

    @Test
    public void example4() {
        // tag::basic-pwd-maker[]
        var maker = PasswordMaker.factory()
                .setLength(12)
                .addCharGroup(CharacterGroups.LOWER_CASE, 1)
                .addCharGroup(CharacterGroups.UPPER_CASE, 1)
                .addCharGroup(CharacterGroups.DIGITS, 1)
                .create();
        // end::basic-pwd-maker[]
    }

    @Test
    public void example5() {
        // tag::sec-pwd-maker[]
        var maker = PasswordMaker.factory()
                .setLength(12)
                .addCharGroup(CharacterGroups.UNAMBIGUOUS_LOWER_CASE, 1)
                .addCharGroup(CharacterGroups.UNAMBIGUOUS_UPPER_CASE, 1)
                .addCharGroup(CharacterGroups.UNAMBIGUOUS_DIGITS, 1, 3)
                .addCharGroup(CharacterGroups.UNAMBIGUOUS_SYMBOLS, 2, 2)
                .create();
        // end::sec-pwd-maker[]
    }

    @Test
    public void example6() {
        // tag::hexadecimal[]
        var maker = PasswordMaker.factory()
                .setLength(20)
                .addCharGroup("0123456789ABCDEF")
                .create();
        // end::hexadecimal[]

        var checker = PasswordChecker.factory()
                .setMinMaxLength(20, 20)
                .addCharGroup("0123456789ABCDEF")
                .create();

        for (int i = 0; i < 1_000_000; i++)
            assert checker.quickCheck(maker.create());
    }

    @Test
    public void example7() {
        // tag::pwd-reset[]
        var maker = PasswordMaker.factory()
                .setLength(64)
                .addCharGroup(CharacterGroups.LOWER_CASE)
                .addCharGroup(CharacterGroups.UPPER_CASE)
                .addCharGroup(CharacterGroups.DIGITS)
                .create();
        // end::pwd-reset[]

        var checker = PasswordChecker.factory()
                .setMinMaxLength(64, 64)
                .addCharGroup(CharacterGroups.LOWER_CASE)
                .addCharGroup(CharacterGroups.UPPER_CASE)
                .addCharGroup(CharacterGroups.DIGITS)
                .create();

        for (int i = 0; i < 1_000_000; i++)
            assert checker.quickCheck(maker.create());
    }

}
