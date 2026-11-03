package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.DeckSize;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.List;

/**
 * A format played at a deck size chosen at setup (swccg-guild/work#90): the base format's card pool and rules, with
 * exactly {@link #getRequiredDeckSize()} cards. Its code is {@code <base>@<size>}, so it is stored and looked up like
 * any other format. Built only by {@link SwccgoFormatLibrary}.
 */
public class DeckSizeSwccgFormat implements SwccgFormat {
    private final DefaultSwccgFormat _base;
    private final int _deckSize;
    private final Integer _defaultGameTimerMinutes;
    private String _tenetsLink;

    DeckSizeSwccgFormat(DefaultSwccgFormat base, int deckSize, Integer defaultGameTimerMinutes, String tenetsLink) {
        _base = base;
        _deckSize = deckSize;
        _defaultGameTimerMinutes = defaultGameTimerMinutes;
        _tenetsLink = tenetsLink;
    }

    DefaultSwccgFormat getBase() {
        return _base;
    }

    /** Rules source for the deck-format index stamp: the base format's rules plus the chosen size. */
    String rulesStampSource() {
        return _base.rulesStampSource() + "deckSize=" + _deckSize + '|';
    }

    @Override
    public String getCode() {
        return DeckSize.code(_base.getCode(), _deckSize);
    }

    @Override
    public String getBaseCode() {
        return _base.getCode();
    }

    @Override
    public boolean isDeckSizeChosen() {
        return true;
    }

    /** game_history.format_name is VARCHAR(45); a format name longer than that can't be recorded. */
    static final int MAX_NAME_LENGTH = 45;

    @Override
    public String getName() {
        String name = _base.getName() + " (" + _deckSize + " cards)";
        if (name.length() <= MAX_NAME_LENGTH)
            return name;
        String suffix = " (" + _deckSize + ")";
        String base = _base.getName();
        if (base.length() + suffix.length() > MAX_NAME_LENGTH)
            base = base.substring(0, MAX_NAME_LENGTH - suffix.length());
        return base + suffix;
    }

    @Override
    public int getRequiredDeckSize() {
        return _deckSize;
    }

    @Override
    public int getMinimumDeckSize() {
        return _deckSize;
    }

    @Override
    public int getMaximumDeckSize() {
        return _deckSize;
    }

    @Override
    public void validateDeck(SwccgDeck deck) throws DeckInvalidException {
        _base.validateDeck(deck, _deckSize, _deckSize);
    }

    @Override
    public int getDefaultGameTimerMinutes() {
        return _defaultGameTimerMinutes != null ? _defaultGameTimerMinutes : _base.getDefaultGameTimerMinutes();
    }

    @Override
    public void addTenetsLink(String tenetsLink) {
        _tenetsLink = tenetsLink;
    }

    @Override
    public String getTenetsLink() {
        return _tenetsLink != null ? _tenetsLink : _base.getTenetsLink();
    }

    // Everything else is the base format's.

    @Override
    public boolean hasDownloadBattlegroundRule() {
        return _base.hasDownloadBattlegroundRule();
    }

    @Override
    public boolean hasLoreHeroicRule() {
        return _base.hasLoreHeroicRule();
    }

    @Override
    public boolean hasJpSealedRule() {
        return _base.hasJpSealedRule();
    }

    @Override
    public boolean isPlaytesting() {
        return _base.isPlaytesting();
    }

    @Override
    public void validateCard(String cardId, boolean skipIfNotExists) throws DeckInvalidException {
        _base.validateCard(cardId, skipIfNotExists);
    }

    @Override
    public List<Integer> getValidSets() {
        return _base.getValidSets();
    }

    @Override
    public List<String> getBannedCards() {
        return _base.getBannedCards();
    }

    @Override
    public List<String> getBannedIcons() {
        return _base.getBannedIcons();
    }

    @Override
    public List<String> getBannedRarities() {
        return _base.getBannedRarities();
    }

    @Override
    public List<String> getRestrictedCards() {
        return _base.getRestrictedCards();
    }

    @Override
    public List<String> getValidCards() {
        return _base.getValidCards();
    }

    @Override
    public boolean skipsFormatPool() {
        return _base.skipsFormatPool();
    }

    @Override
    public boolean supportsStartingCredits() {
        return _base.supportsStartingCredits();
    }

    @Override
    public int getDefaultStartingCredits() {
        return _base.getDefaultStartingCredits();
    }

    @Override
    public List<SwccgCardBlueprint> getAllCardBlueprintsValidInFormat() {
        return _base.getAllCardBlueprintsValidInFormat();
    }
}
