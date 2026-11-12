package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Title: Cane Adiss
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Cane Adiss]] (Decipher archive scan): all your cards may deploy here regardless of
 * presence and location deployment restrictions. Decipher's Glossary Supplement (29 January 2002) limited it to your
 * characters, vehicles and starships, which the current card (Card6_142) plays, so this state differs only in which of
 * your cards it lets deploy here.
 */
public class Card6_142o extends Card6_142 {
    public Card6_142o() {
        super();
        setGameText("If opponent just initiated a Force drain at a non-shielded planet location, deploy on that location. Your cards may deploy here regardless of presence and location deployment restrictions. (Immune to Control.)");
        setTextState(new TextState("6_142", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // As printed: any of your cards, not only characters, vehicles and starships.
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, Filters.your(self), Filters.here(self)));
        return modifiers;
    }
}
