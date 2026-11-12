package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Title: Imperial Helmsman
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0 (November 1998) changed it to "anything he pilots" (the current card, Card4_102). The rest of the printed text plays as the current card, so only the pilot bonus is limited to starships here. "On a Star Destroyer" is played as piloting one, as the Glossary worded it.
 */
public class Card4_102o extends Card4_102 {
    public Card4_102o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. On a Star Destroyer, draws one battle destiny if not able to otherwise.");
        setTextState(new TextState("4_102", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Star_Destroyer), 1));
        return modifiers;
    }
}
