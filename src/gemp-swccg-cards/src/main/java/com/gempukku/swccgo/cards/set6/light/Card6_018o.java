package com.gempukku.swccgo.cards.set6.light;

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
 * Title: H'nemthe
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[H'nemthe]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * As printed, the pilot bonus is "any starship"; the errata (current card, Card6_018) made it "anything", so this state adds power only to starships it pilots and otherwise plays as the current card.
 */
public class Card6_018o extends Card6_018 {
    public Card6_018o() {
        super();
        setGameText("Adds 2 to power of any starship she pilots. Adds 2 to deploy cost of each opponent's male Imperial when that Imperial is deploying to same or adjacent site.");
        setTextState(new TextState("6_18", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // As printed, the pilot bonus applies only to starships (the errata made it "anything").
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(0, new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        return modifiers;
    }
}
