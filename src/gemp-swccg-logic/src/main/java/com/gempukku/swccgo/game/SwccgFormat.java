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
    List<SwccgCardBlueprint> getAllCardBlueprintsValidInFormat();
}
