package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.LegacyPersonaFilters;
import com.gempukku.swccgo.cards.conditions.AtSameLocationAsCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Derek 'Hobbie' Klivian
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0 (November 1998) changed it to "anything he pilots" (the current card, Card3_005). The rest of the printed text plays as the current card, so only the pilot bonus is limited to starships here. "3 if a Star Destroyer is present" plays as at same location for a starship at a system or sector.
 */
public class Card3_005o extends Card3_005 {
    public Card3_005o() {
        super();
        setGameText("Power +2 when at same site as Biggs. Adds 2 to power of any starship he pilots (3 if a Star Destroyer is present). On Rogue 4, also adds 2 to maneuver.");
        setTextState(new TextState("3_5", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, LegacyPersonaFilters.BIGGS), 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 3, new AtSameLocationAsCondition(self, Filters.Star_Destroyer)), Filters.starship));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Rogue_4, Filters.hasPiloting(self)), 2));
        return modifiers;
    }
}
