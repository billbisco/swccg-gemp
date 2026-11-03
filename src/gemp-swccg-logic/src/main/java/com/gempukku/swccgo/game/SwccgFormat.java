package com.gempukku.swccgo.game;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import java.util.List;
/*
 * This interface represents a Gemp-Swccg format.
 *
 * A Gemp-Swccg format defines which cards are valid
 * to be used in a deck playing a game of that format.
 */
public interface SwccgFormat {
    boolean hasDownloadBattlegroundRule();
    boolean hasLoreHeroicRule();
    boolean hasJpSealedRule();
    boolean isPlaytesting();
    String getName();
    String getCode();
    void validateCard(String cardId, boolean skipIfNotExists) throws DeckInvalidException;
    void validateDeck(SwccgDeck deck) throws DeckInvalidException;
    List<Integer> getValidSets();
    List<String> getBannedCards();
    void addTenetsLink(String tenetsLink);
    String getTenetsLink();
    List<String> getBannedIcons();
    List<String> getBannedRarities();
    List<String> getRestrictedCards();
    List<String> getValidCards();
    int getRequiredDeckSize();
    int getMinimumDeckSize();
    int getMaximumDeckSize();
    boolean skipsFormatPool();
    int getDefaultGameTimerMinutes();

    /** Whether this format uses the Reflections Gold opening-hand credit rule. */
    default boolean supportsStartingCredits() { return false; }

    default int getDefaultStartingCredits() { return supportsStartingCredits() ? 5 : 0; }

    /** The code of the format this one is based on; differs from {@link #getCode()} only when a deck size was chosen. */
    default String getBaseCode() { return getCode(); }

    /** Whether the deck size was chosen at setup instead of being the format's own (swccg-guild/work#90). */
    default boolean isDeckSizeChosen() { return false; }

    /** The largest deck size a table, league or tournament may choose in this format (#120; Anything Goes allows more). */
    default int getMaxDeckSizeOption() { return DeckSize.MAX_SUPPORTED; }
    List<SwccgCardBlueprint> getAllCardBlueprintsValidInFormat();
}
