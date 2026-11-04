package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A format with the deck rules a table's game options add (swccg-guild/work#142/#144), e.g. "Shields: none". Everything
 * is the inner format's (the base format, or the base at a chosen deck size), including its code, so storing and
 * showing the game is unchanged; the options themselves are shown as chips. Only deck validation differs.
 * Built only by {@link SwccgoFormatLibrary#effectiveFormat}.
 */
public class DeckRulesSwccgFormat implements SwccgFormat {
    private final SwccgFormat _inner;
    private final DeckRules _rules;
    private final com.gempukku.swccgo.game.SwccgCardBlueprintLibrary _library;

    DeckRulesSwccgFormat(SwccgFormat inner, DeckRules rules, com.gempukku.swccgo.game.SwccgCardBlueprintLibrary library) {
        _library = library;
        if (!(inner instanceof DefaultSwccgFormat) && !(inner instanceof DeckSizeSwccgFormat))
            throw new IllegalArgumentException("Deck rules apply to a library format, not " + inner.getClass().getSimpleName());
        _inner = inner;
        _rules = rules;
    }

    public DeckRules getDeckRules() { return _rules; }

    @Override
    public void validateDeck(SwccgDeck deck) throws DeckInvalidException {
        if (_inner instanceof DeckSizeSwccgFormat)
            ((DeckSizeSwccgFormat) _inner).validateDeck(deck, _rules);
        else
            ((DefaultSwccgFormat) _inner).validateDeck(deck, _inner.getMinimumDeckSize(), _inner.getMaximumDeckSize(), _rules);
    }

    @Override
    public void validateCard(String cardId, boolean skipIfNotExists) throws DeckInvalidException {
        _inner.validateCard(cardId, skipIfNotExists);
        // the deck builder's card filter checks cards one by one: "Shields: none" leaves shields out, as Open (no shields)
        if (_rules.isNoShields() && _library != null) {
            SwccgCardBlueprint card = _library.getSwccgoCardBlueprint(cardId);
            if (card != null && card.hasIcon(Icon.DEFENSIVE_SHIELD))
                throw new DeckInvalidException("Defensive Shields are not permitted at this table (Shields: none).");
        }
    }

    @Override
    public List<String> getBannedIcons() {
        if (!_rules.isNoShields() || _inner.getBannedIcons().contains(Icon.DEFENSIVE_SHIELD.name()))
            return _inner.getBannedIcons();
        List<String> icons = new ArrayList<>(_inner.getBannedIcons());
        icons.add(Icon.DEFENSIVE_SHIELD.name());
        return Collections.unmodifiableList(icons);
    }

    @Override
    public boolean allowsMultipleObjectives() { return _inner.allowsMultipleObjectives() || _rules.isMultipleObjectives(); }

    @Override public boolean hasDownloadBattlegroundRule() { return _inner.hasDownloadBattlegroundRule(); }
    @Override public boolean hasLoreHeroicRule() { return _inner.hasLoreHeroicRule(); }
    @Override public boolean hasJpSealedRule() { return _inner.hasJpSealedRule(); }
    @Override public boolean isPlaytesting() { return _inner.isPlaytesting(); }
    @Override public String getName() { return _inner.getName(); }
    @Override public String getCode() { return _inner.getCode(); }
    @Override public List<Integer> getValidSets() { return _inner.getValidSets(); }
    @Override public List<String> getBannedCards() { return _inner.getBannedCards(); }
    @Override public void addTenetsLink(String tenetsLink) { _inner.addTenetsLink(tenetsLink); }
    @Override public String getTenetsLink() { return _inner.getTenetsLink(); }
    @Override public List<String> getBannedRarities() { return _inner.getBannedRarities(); }
    @Override public List<String> getRestrictedCards() { return _inner.getRestrictedCards(); }
    @Override public List<String> getValidCards() { return _inner.getValidCards(); }
    @Override public int getRequiredDeckSize() { return _inner.getRequiredDeckSize(); }
    @Override public int getMinimumDeckSize() { return _inner.getMinimumDeckSize(); }
    @Override public int getMaximumDeckSize() { return _inner.getMaximumDeckSize(); }
    @Override public boolean skipsFormatPool() { return _inner.skipsFormatPool(); }
    @Override public int getDefaultGameTimerMinutes() { return _inner.getDefaultGameTimerMinutes(); }
    @Override public boolean supportsStartingCredits() { return _inner.supportsStartingCredits(); }
    @Override public int getDefaultStartingCredits() { return _inner.getDefaultStartingCredits(); }
    @Override public String getBaseCode() { return _inner.getBaseCode(); }
    @Override public boolean isDeckSizeChosen() { return _inner.isDeckSizeChosen(); }
    @Override public int getMaxDeckSizeOption() { return _inner.getMaxDeckSizeOption(); }
    @Override public List<SwccgCardBlueprint> getAllCardBlueprintsValidInFormat() { return _inner.getAllCardBlueprintsValidInFormat(); }
}
