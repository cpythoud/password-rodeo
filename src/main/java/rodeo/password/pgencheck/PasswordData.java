package rodeo.password.pgencheck;

import java.util.Collections;
import java.util.List;

abstract class PasswordData {

    private final List<String> charGroups;

    private final List<Integer> groupMinCounts;
    private final List<Integer> groupMaxCounts;

    private final String allChars;

    PasswordData(List<String> charGroups, List<Integer> groupMinCounts, List<Integer> groupMaxCounts) {
        this.charGroups = charGroups;
        this.groupMinCounts = groupMinCounts;
        this.groupMaxCounts = groupMaxCounts;

        var chars = new StringBuilder();
        for (var charSet: charGroups)
            chars.append(charSet);
        allChars = chars.toString();
    }

    List<String> charGroups() {
        return charGroups;
    }

    List<Integer> groupMinCounts() {
        return groupMinCounts;
    }

    List<Integer> groupMaxCounts() {
        return groupMaxCounts;
    }

    /**
     * Returns a list of the character groups composing the generated passwords.
     * @return the character groups
     */
    public List<String> getCharacterGroups() {
        return Collections.unmodifiableList(charGroups);
    }

    /**
     * Returns how many character groups are used in composing passwords.
     * @return character group count
     */
    public int getCharacterGroupCount() {
        return charGroups.size();
    }

    /**
     * Return the n<sup>th</sup> character group used in creating passwords. Character Group are referenced in
     * the order they are added to the factory. The first index is <code>0</code> (zero).
     * @param index of the character group to retrieve
     * @return the character group at the <code>index</code>
     * @throws IndexOutOfBoundsException if the <code>index</code> is invalid
     */
    public String getCharacterGroup(int index) {
        if (!indexOK(index))
            throw new IndexOutOfBoundsException(getBadIndexErrorMessage(index));

        return charGroups.get(index);
    }

    /**
     * Return how many characters from the n<sup>th</sup> character group are required in generated passwords.
     * Character Group are referenced in the order they are added to the factory. The first index is <code>0</code>
     * (zero).
     * @param index of the character group minimum count to retrieve
     * @return IndexOutOfBoundsException if the <code>index</code> is invalid
     */
    public int getMinCharactersInGroup(int index) {
        if (!indexOK(index))
            throw new IndexOutOfBoundsException(getBadIndexErrorMessage(index));

        return groupMinCounts.get(index);
    }

    /**
     * Return the maximum number of characters from the n<sup>th</sup> character group are allowed in generated
     * passwords. Character Group are referenced in the order they are added to the factory. The first index is
     * <code>0</code> (zero).
     * @param index of the character group maximum count to retrieve
     * @return IndexOutOfBoundsException if the <code>index</code> is invalid
     */
    public int getMaxCharactersInGroup(int index) {
        if (!indexOK(index))
            throw new IndexOutOfBoundsException(getBadIndexErrorMessage(index));

        return groupMaxCounts.get(index);
    }

    private boolean indexOK(int index) {
        return index >= 0 && index < charGroups.size();
    }

    private String getBadIndexErrorMessage(int index) {
        return "Index must be between 0 and " + (charGroups.size() - 1) + ". Value received: " + index;
    }

    /**
     * Return all characters used in generating password.
     * @return characters used in generating password
     */
    public String getAllChars() {
        return allChars;
    }

}
