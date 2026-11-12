package com.gempukku.swccgo.cards.set1.dark;

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
 * Set: Premiere
 * Type: Character
 * Title: Grand Moff Tarkin
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Grand Moff Tarkin]]. As printed, the pilot bonus applies only to
 * a starship
 * he pilots ("any starship he pilots"); the errata ([[Grand Moff Tarkin (Errata)]], Decipher Glossary Supplement (29
 * January 2002)) made it "anything he pilots", so vehicles get it
 * too, which is how the current card (Card1_179) plays. Everything else plays as Card1_179 ("may cancel one
 * opponent's destiny draw" is read as once per battle, which the 2002 Glossary Supplement wording states).
 */
public class Card1_179o extends Card1_179 {
    public Card1_179o() {
        super();
        setGameText("If with Vader in a battle, may cancel one opponent's destiny draw immediately after it is revealed. Adds 2 to power of any starship he pilots.");
        setTextState(new TextState("1_179", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        for (Modifier modifier : super.getGameTextWhileActiveInPlayModifiers(game, self)) {
            if (modifier instanceof AddsPowerToPilotedBySelfModifier)
                modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
            else
                modifiers.add(modifier);
        }
        return modifiers;
    }
}
