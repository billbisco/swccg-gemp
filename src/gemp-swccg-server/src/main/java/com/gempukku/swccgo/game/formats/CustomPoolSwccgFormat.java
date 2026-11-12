package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardCounts;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.CardPool;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A format with a tournament's custom card pool (swccg-guild/work#222): the inner format (a library format, at a chosen
 * deck size and with a table's deck rules if any) with the event's sets in place of the format's, plus card-by-card
 * allows and bans. Only the pool differs: deck size, copy limits, side, Objective and Starting Effect rules and the
 * table's game options are the inner format's, and an allow never lifts them. Its code is the inner format's, so the
 * event is stored and listed as before; the pool rides in the event's {@link com.gempukku.swccgo.game.setup.GameSetup}.
 * <p>
 * A card is checked in this order: it must exist and not be a game aid; the table's "Shields: none" still applies; a
 * ban on any printing of it removes it; an allow on any printing of it admits it; otherwise the format's rules apply with
 * the event's sets. Built only by {@link SwccgoFormatLibrary#effectiveFormat}.
 */
public class CustomPoolSwccgFormat implements SwccgFormat {
    private final SwccgFormat _inner;
    private final DefaultSwccgFormat _base;
    private final DeckRules _rules;
    private final CardPool _pool;
    private final SwccgCardBlueprintLibrary _library;
    private final Set<String> _allowedPrintings;
    private final Set<String> _bannedPrintings;
    private List<SwccgCardBlueprint> _allCardBlueprints;

    CustomPoolSwccgFormat(SwccgFormat inner, CardPool pool, SwccgCardBlueprintLibrary library) {
        _inner = inner;
        SwccgFormat sized = inner instanceof DeckRulesSwccgFormat ? ((DeckRulesSwccgFormat) inner).getInner() : inner;
        if (sized instanceof DeckSizeSwccgFormat)
            _base = ((DeckSizeSwccgFormat) sized).getBase();
        else if (sized instanceof DefaultSwccgFormat)
            _base = (DefaultSwccgFormat) sized;
        else
            throw new IllegalArgumentException("A custom card pool applies to a library format, not " + inner.getClass().getSimpleName());
        _rules = inner instanceof DeckRulesSwccgFormat ? ((DeckRulesSwccgFormat) inner).getDeckRules() : DeckRules.NONE;
        _pool = pool;
        _library = library;
        _allowedPrintings = printings(library, pool.getAllowed());
        _bannedPrintings = printings(library, pool.getBanned());
    }

    public CardPool getPool() { return _pool; }

    /** Every printing GEMP treats as the same card as one of {@code ids}: the id, its base id and their alternates. */
    static Set<String> printings(SwccgCardBlueprintLibrary library, List<String> ids) {
        Set<String> result = new HashSet<>();
        for (String id : ids)
            result.addAll(printingsOf(library, id));
        return result;
    }

    static Set<String> printingsOf(SwccgCardBlueprintLibrary library, String id) {
        Set<String> result = new HashSet<>();
        String stripped = library.stripBlueprintModifiers(id);
        String base = library.getBaseBlueprintId(id);
        result.add(stripped);
        result.add(base);
        for (String key : new String[] {stripped, base}) {
            Set<String> alternates = library.getAllAlternates(key);
            if (alternates != null)
                result.addAll(alternates);
        }
        return result;
    }

    private boolean anyPrintingIn(String blueprintId, Set<String> ids) {
        if (ids.isEmpty())
            return false;
        for (String printing : printingsOf(_library, blueprintId))
            if (ids.contains(printing))
                return true;
        return false;
    }

    private List<Integer> effectiveSets() {
        return _pool.hasSets() ? _pool.getSets() : _base.getValidSets();
    }

    @Override
    public void validateCard(String blueprintId, boolean skipIfNotExists) throws DeckInvalidException {
        SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(_library.getBaseBlueprintId(blueprintId));
        if (blueprint == null) {
            if (skipIfNotExists)
                return;
            throw new DeckInvalidException("<span class=\"validate-invalid-card\">" + blueprintId + "</span> card not permitted in this tournament.");
        }
        String fullName = GameUtils.getFullName(blueprint);
        if (blueprint.getCardCategory() == CardCategory.GAME_AID)
            throw new DeckInvalidException("Deck contains card type, from card <span class=\"validate-invalid-card-type\">" + fullName + "</span>, not permitted in this format.");
        // a table option is not part of the pool: an allow does not lift it
        if (_rules.isNoShields() && blueprint.hasIcon(Icon.DEFENSIVE_SHIELD))
            throw new DeckInvalidException("Defensive Shields are not permitted at this table (Shields: none).");
        if (_rules.isFourTwenty()) {
            // "Deck: 420" (#224) is a table option too: an allow does not lift it
            String violation = FourTwenty.violation(blueprint);
            if (violation != null)
                throw new DeckInvalidException("<span class=\"validate-invalid-card\">" + fullName
                        + "</span> not permitted at this table (Deck: 420): " + violation + ".");
        }
        if (anyPrintingIn(blueprintId, _bannedPrintings))
            throw new DeckInvalidException("Deck contains <span class=\"validate-invalid-card\">" + fullName
                    + "</span> (" + blueprintId + "), banned in this tournament's custom card pool.");
        if (anyPrintingIn(blueprintId, _allowedPrintings))
            return;
        try {
            _base.validateCard(blueprintId, skipIfNotExists, effectiveSets());
        } catch (DeckInvalidException exp) {
            if (_pool.hasSets() && exp.getMessage() != null && exp.getMessage().contains("from set banned"))
                throw new DeckInvalidException("Deck contains card, <span class=\"validate-invalid-card-set\">" + fullName
                        + "</span> (" + blueprintId + "), from a set not in this tournament's custom card pool.");
            throw exp;
        }
    }

    @Override
    public void validateDeck(SwccgDeck deck) throws DeckInvalidException {
        _base.validateDeck(deck, _inner.getMinimumDeckSize(), _inner.getMaximumDeckSize(), _rules, id -> validateCard(id, false));
    }

    @Override
    public List<Integer> getValidSets() { return effectiveSets(); }

    @Override
    public List<String> getBannedCards() {
        List<String> banned = new ArrayList<>(_inner.getBannedCards());
        banned.removeAll(_pool.getAllowed());
        for (String id : _pool.getBanned())
            if (!banned.contains(id))
                banned.add(id);
        return Collections.unmodifiableList(banned);
    }

    @Override
    public List<String> getValidCards() {
        List<String> valid = new ArrayList<>(_inner.getValidCards());
        for (String id : _pool.getAllowed())
            if (!valid.contains(id))
                valid.add(id);
        valid.removeAll(_pool.getBanned());
        return Collections.unmodifiableList(valid);
    }

    @Override
    public List<SwccgCardBlueprint> getAllCardBlueprintsValidInFormat() {
        if (_allCardBlueprints != null)
            return _allCardBlueprints;
        List<SwccgCardBlueprint> all = new ArrayList<>();
        addValid(all, CardCounts.FULL_SETS_CARD_COUNTS, 1);
        addValid(all, CardCounts.PREMIUM_SETS_CARD_COUNTS, 101);
        addValid(all, CardCounts.VIRTUAL_SETS_CARD_COUNTS, 200);
        addValid(all, CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS, 301);
        addValid(all, CardCounts.DREAM_CARD_SETS_CARD_COUNTS, 401);
        addValid(all, CardCounts.PLAYTESTING_SETS_CARD_COUNTS, 501);
        _allCardBlueprints = Collections.unmodifiableList(all);
        return _allCardBlueprints;
    }

    private void addValid(List<SwccgCardBlueprint> all, int[] counts, int firstSet) {
        for (int i = 0; i < counts.length; i++) {
            for (int j = 1; j <= counts[i]; j++) {
                String id = (firstSet + i) + "_" + j;
                try {
                    validateCard(id, true);
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(id);
                    if (blueprint != null)
                        all.add(blueprint);
                } catch (DeckInvalidException | IllegalArgumentException exp) {
                    // not in the pool
                }
            }
        }
    }

    @Override public List<String> getBannedIcons() { return _inner.getBannedIcons(); }
    @Override public boolean allowsMultipleObjectives() { return _inner.allowsMultipleObjectives(); }
    @Override public boolean hasDownloadBattlegroundRule() { return _inner.hasDownloadBattlegroundRule(); }
    @Override public boolean hasLoreHeroicRule() { return _inner.hasLoreHeroicRule(); }
    @Override public boolean hasJpSealedRule() { return _inner.hasJpSealedRule(); }
    @Override public boolean isPlaytesting() { return _inner.isPlaytesting(); }
    @Override public String getName() { return _inner.getName(); }
    @Override public String getCode() { return _inner.getCode(); }
    @Override public void addTenetsLink(String tenetsLink) { _inner.addTenetsLink(tenetsLink); }
    @Override public String getTenetsLink() { return _inner.getTenetsLink(); }
    @Override public List<String> getBannedRarities() { return _inner.getBannedRarities(); }
    @Override public List<String> getRestrictedCards() { return _inner.getRestrictedCards(); }
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
}
