package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Wes Janson
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to weapon destiny draws only when aboard a starship as a passenger; Decipher Glossary 2.0 (November 1998) changed it to "anything he is aboard as a passenger" (the current card, Card3_025), which adds vehicles.
 */
public class Card3_025o extends Card3_025 {
    public Card3_025o() {
        super();
        setGameText("If aboard a starship as a passenger, adds 1 to starship's weapon destiny draws (adds 3 if aboard Rogue 3 or with Wedge or Jek).");
        setTextState(new TextState("3_25", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, Filters.and(Filters.starship, Filters.hasPassenger(self)), new ConditionEvaluator(1, 3,
                new OrCondition(new AboardCondition(self, Filters.Rogue_3), new WithCondition(self, Filters.or(Filters.Wedge, Filters.Jek))))));
        return modifiers;
    }
}
