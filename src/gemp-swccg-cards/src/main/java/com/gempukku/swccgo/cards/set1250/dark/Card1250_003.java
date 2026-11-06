package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.set1115.dark.Card1115_040;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.Collections;
import java.util.List;

/** Virtual Block 5 printing of the unchanged Original Virtual Chokk text. */
public class Card1250_003 extends Card1115_040 {
    public Card1250_003() {
        addIcons(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DeployCostToLocationModifier(self, -1, Filters.Tatooine_location));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }
}
