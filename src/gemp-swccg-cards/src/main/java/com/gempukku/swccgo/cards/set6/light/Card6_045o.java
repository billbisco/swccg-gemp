package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.conditions.DoubledCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Title: Vul Tazaene
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Vul Tazaene]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), whose text the current card keeps.
 * As printed, the pilot bonus is "any starship"; the errata (current card, Card6_045) made it "anything", so this state adds power only to starships it pilots and otherwise plays as the current card.
 */
public class Card6_045o extends Card6_045 {
    public Card6_045o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. Twice during battle at same system, may use 2 Force to add 2 to any destiny of 2. If present with Tonnika Sisters, Vul and Tonnika Sisters are lost.");
        setTextState(new TextState("6_45", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // As printed, the pilot bonus applies only to starships (the errata made it "anything").
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(0, new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 4, new DoubledCondition(self)), Filters.starship));
        return modifiers;
    }
}
