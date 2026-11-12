package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePurchasedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Title: IG-88
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0
 * (November 1998) changed it to "anything he pilots" (the current card, Card4_101). The rest of the printed text plays
 * as the current card, so only the pilot bonus is limited to starships here.
 */
public class Card4_101o extends Card4_101 {
    public Card4_101o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. May 'assassinate' any character at same site hit by IG-88 (victim is immediately lost). May use two different weapons. May initiate battle and be battled. Immune to Restraining Bolt and purchase.");
        setTextState(new TextState("4_101", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new MayBeBattledModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        modifiers.add(new MayNotBePurchasedModifier(self));
        return modifiers;
    }
}
