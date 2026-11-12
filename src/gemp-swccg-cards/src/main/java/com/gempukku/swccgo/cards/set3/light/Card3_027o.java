package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Zev Senesca
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0 (November 1998) changed it to "anything he pilots" (the current card, Card3_027). The rest of the printed text plays as the current card, so only the pilot bonus is limited to starships here. "On Rogue 2" is played as piloting Rogue 2, as the Glossary worded it.
 */
public class Card3_027o extends Card3_027 {
    public Card3_027o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. On Rogue 2, also adds 3 to maneuver and may draw one battle destiny if not able to otherwise.");
        setTextState(new TextState("3_27", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Rogue_2, Filters.hasPiloting(self)), 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Rogue_2), 1));
        return modifiers;
    }
}
