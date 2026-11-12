package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Captain Piett
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0
 * (November 1998) changed it to "anything he pilots" (the current card, Card3_085). The rest of the printed text plays
 * as the current card, so only the pilot bonus is limited to starships here.
 */
public class Card3_085o extends Card3_085 {
    public Card3_085o() {
        super();
        setGameText("Power +1 when at same site as Vader. Adds 2 to power of any starship he pilots (3 if starship is Executor). May use 1 Force to search your Reserve Deck and take one Probe Droid into hand. Shuffle, cut and replace.");
        setTextState(new TextState("3_85", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.Vader), 1));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Executor), Filters.starship));
        return modifiers;
    }
}
