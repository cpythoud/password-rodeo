package rodeo.password.pgencheck;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static rodeo.password.pgencheck.ErrorMessages.CHAR_GROUP_EMPTY;
import static rodeo.password.pgencheck.ErrorMessages.CHAR_GROUP_NULL;
import static rodeo.password.pgencheck.ErrorMessages.DUPLICATE_CHARS_FOUND_IN_GROUP;
import static rodeo.password.pgencheck.ErrorMessages.DUPLICATE_CHARS_FOUND_IN_OTHER_GROUP;
import static rodeo.password.pgencheck.ErrorMessages.MAX_BIGGER_THAN_MIN;
import static rodeo.password.pgencheck.ErrorMessages.MAX_CHAR_COUNT_NEG;
import static rodeo.password.pgencheck.ErrorMessages.MIN_CHAR_COUNT_NEG;

abstract class AbstractFactory<F extends AbstractFactory<F>> {

    private final List<String> charGroups = new ArrayList<>();
    private final List<Integer> groupMinCounts = new ArrayList<>();
    private final List<Integer> groupMaxCounts = new ArrayList<>();

    private boolean disallowDuplicateCharacters = true;

    List<String> charGroups() {
        return charGroups;
    }

    List<Integer> groupMinCounts() {
        return groupMinCounts;
    }

    List<Integer> groupMaxCounts() {
        return groupMaxCounts;
    }

    List<String> charGroupsCopy() {
        return new ArrayList<>(charGroups);
    }

    List<Integer> groupMinCountsCopy() {
        return new ArrayList<>(groupMinCounts);
    }

    List<Integer> groupMaxCountsCopy() {
        return new ArrayList<>(groupMaxCounts);
    }

    F addCharGroup(String charGroup) {
        return addCharGroup(charGroup, 0);
    }

    F addCharGroup(String charGroup, int minCount) {
        return addCharGroup(charGroup, minCount, 0);
    }

    F addCharGroup(String charGroup, int minCount, int maxCount) {
        if (charGroup == null)
            throw new NullPointerException(CHAR_GROUP_NULL);
        if (charGroup.isEmpty())
            throw new IllegalArgumentException(CHAR_GROUP_EMPTY);
        if (minCount < 0)
            throw new IllegalArgumentException(MIN_CHAR_COUNT_NEG + minCount);
        if (maxCount < 0)
            throw new IllegalArgumentException(MAX_CHAR_COUNT_NEG + minCount);
        if (maxCount != 0 && maxCount < minCount)
            throw new IllegalArgumentException(MAX_BIGGER_THAN_MIN + maxCount + " < " + minCount);

        if (disallowDuplicateCharacters) {
            var duplicates = getGroupDuplicates(charGroup);
            if (!duplicates.isEmpty())
                throw new IllegalArgumentException(DUPLICATE_CHARS_FOUND_IN_GROUP + duplicates);
            duplicates = getInterGroupDuplicates(charGroup);
            if (!duplicates.isEmpty())
                throw new IllegalArgumentException(DUPLICATE_CHARS_FOUND_IN_OTHER_GROUP + duplicates);
        }

        charGroups.add(charGroup);
        groupMinCounts.add(minCount);
        groupMaxCounts.add(maxCount);

        return getThis();
    }

    private String getGroupDuplicates(String charGroup) {
        var counts = new  LinkedHashMap<Integer, Integer>();
        for (int codePoint: charGroup.codePoints().toArray())
            counts.merge(codePoint, 1, Integer::sum);

        var duplicates = new StringBuilder();
        for (var codePointCount: counts.entrySet())
            if (codePointCount.getValue() > 1)
                duplicates.appendCodePoint(codePointCount.getKey());

        return duplicates.toString();
    }

    private String getInterGroupDuplicates(String charGroup) {
        var duplicates = new StringBuilder();
        for (String group: charGroups)
            for (int codePoint: charGroup.codePoints().toArray())
                if (group.indexOf(codePoint) != -1)
                    duplicates.appendCodePoint(codePoint);

        return duplicates.toString();
    }

    F disallowDuplicateCharacters(boolean disallowDuplicateCharacters) {
        this.disallowDuplicateCharacters = disallowDuplicateCharacters;
        return getThis();
    }

    abstract F getThis();

}
