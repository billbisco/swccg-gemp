package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.TreeMap;

/** Explicit, source-reviewed choices, not a date inferred from publication bounds. */
final class CardTextSelection {
    private final SwccgCardBlueprintLibrary _library;
    private final Map<String, String> _choices;

    CardTextSelection(SwccgCardBlueprintLibrary library, Map<String, String> choices) {
        _library = library;
        Map<String, String> checked = new TreeMap<>();
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            String current = entry.getKey();
            String selected = entry.getValue();
            if (current == null || selected == null
                    || !current.equals(library.stripBlueprintModifiers(current))
                    || !selected.equals(library.stripBlueprintModifiers(selected))
                    || TextState.isTextStateBlueprint(current)
                    || !current.equals(library.getBaseBlueprintId(current))
                    || library.getSwccgoCardBlueprint(current) == null
                    || !current.equals(TextState.currentIdOf(selected)))
                throw new IllegalArgumentException("Invalid card text selection: " + current + " -> " + selected);
            SwccgCardBlueprint target = library.getSwccgoCardBlueprint(selected);
            if (target == null || target.getTextState() == null
                    || !current.equals(target.getTextState().currentId()))
                throw new IllegalArgumentException("Card text selection needs a loadable state: " + selected);
            checked.put(current, selected);
        }
        _choices = Collections.unmodifiableMap(checked);
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
        if (TextState.isTextStateBlueprint(front))
            return id;
        String bare = _library.stripBlueprintModifiers(front);
        String canonical = bare.replaceAll("(^|_)0+(?=\\d)", "$1");
        String selected = _choices.get(_library.getBaseBlueprintId(canonical));
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
            for (Iterator<SwccgCardBlueprint> it = cards.iterator(); it.hasNext();) {
                SwccgCardBlueprint card = it.next();
                if (card == current || card == selected) {
                    if (included)
                        it.remove();
                    else
                        included = true;
                }
            }
            int index = cards.indexOf(current);
            if (index >= 0)
                cards.set(index, selected);
        }
    }
}
