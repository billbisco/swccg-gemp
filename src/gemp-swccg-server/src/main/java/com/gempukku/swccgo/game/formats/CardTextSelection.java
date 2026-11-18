package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/** Explicit, source-reviewed choices, not a date inferred from publication bounds. */
final class CardTextSelection {
    private final SwccgCardBlueprintLibrary _library;
    private final Map<String, String> _choices;
    private final Set<String> _forcedFamilies;

    CardTextSelection(SwccgCardBlueprintLibrary library, Map<String, String> choices) {
        this(library, choices, Set.of());
    }

    private CardTextSelection(SwccgCardBlueprintLibrary library, Map<String, String> choices, Set<String> forcedFamilies) {
        _library = library;
        _forcedFamilies = Collections.unmodifiableSet(new TreeSet<>(forcedFamilies));
        Map<String, String> checked = new TreeMap<>();
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            String current = entry.getKey();
            String selected = entry.getValue();
            String selectedFamily = TextState.currentIdOf(selected);
            if (selectedFamily == null)
                selectedFamily = selected;
            if (current == null || selected == null
                    || !current.equals(library.stripBlueprintModifiers(current))
                    || !selected.equals(library.stripBlueprintModifiers(selected))
                    || TextState.isTextStateBlueprint(current)
                    || !current.equals(library.getBaseBlueprintId(current))
                    || library.getSwccgoCardBlueprint(current) == null
                    || !current.equals(selectedFamily))
                throw new IllegalArgumentException("Invalid card text selection: " + current + " -> " + selected);
            SwccgCardBlueprint target = library.getSwccgoCardBlueprint(selected);
            if (target == null || (!(forcedFamilies.contains(current) && current.equals(selected))
                    && (target.getTextState() == null || !current.equals(target.getTextState().currentId()))))
                throw new IllegalArgumentException("Card text selection needs a loadable state: " + selected);
            checked.put(current, selected);
        }
        _choices = Collections.unmodifiableMap(checked);
    }

    /** An explicit table/event choice overrides both bare and imported states of this family. */
    CardTextSelection withForcedChoice(String family, String selected) {
        Map<String, String> choices = new TreeMap<>(_choices);
        choices.put(family, selected);
        Set<String> forced = new HashSet<>(_forcedFamilies);
        forced.add(family);
        return new CardTextSelection(_library, choices, forced);
    }

    Set<String> forcedFamilies() {
        return _forcedFamilies;
    }

    Map<String, String> choices() {
        return _choices;
    }

    boolean selects(String id) {
        return _choices.containsValue(_library.stripBlueprintModifiers(id));
    }

    String resolve(String id) {
        if (id == null || _choices.isEmpty())
            return id;
        int slash = id.indexOf('/');
        String front = slash < 0 ? id : id.substring(0, slash);
        String bare = _library.stripBlueprintModifiers(front);
        String canonical = bare.replaceAll("(^|_)0+(?=\\d)", "$1");
        String family = TextState.currentIdOf(_library.getBaseBlueprintId(canonical));
        if (family == null)
            family = _library.getBaseBlueprintId(canonical);
        if (TextState.isTextStateBlueprint(front) && !_forcedFamilies.contains(family))
            return id;
        String selected = _choices.get(family);
        if (selected == null)
            return id;
        String modifiers = front.replaceAll("[^*^]", "");
        return selected + modifiers + (slash < 0 ? "" : id.substring(slash));
    }

    SwccgDeck apply(SwccgDeck deck) {
        return _choices.isEmpty() ? deck : DeckCardResolver.apply(deck, this::resolve);
    }

    void selectPool(List<SwccgCardBlueprint> cards) {
        for (Map.Entry<String, String> choice : _choices.entrySet()) {
            SwccgCardBlueprint current = _library.getSwccgoCardBlueprint(choice.getKey());
            SwccgCardBlueprint selected = _library.getSwccgoCardBlueprint(choice.getValue());
            boolean included = false;
            for (var it = cards.listIterator(); it.hasNext();) {
                SwccgCardBlueprint card = it.next();
                boolean familyState = _forcedFamilies.contains(choice.getKey()) && card.getTextState() != null
                        && choice.getKey().equals(card.getTextState().currentId());
                if (card == current || card == selected || familyState) {
                    if (included)
                        it.remove();
                    else {
                        it.set(selected);
                        included = true;
                    }
                }
            }
        }
    }
}
