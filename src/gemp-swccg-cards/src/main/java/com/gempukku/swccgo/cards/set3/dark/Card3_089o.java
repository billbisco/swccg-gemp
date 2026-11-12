package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Lieutenant Cabbel
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0
 * (November 1998) changed it to "anything he pilots" (the current card, Card3_089). The rest of the printed text plays
 * as the current card, so only the pilot bonus is limited to starships here.
 */
public class Card3_089o extends Card3_089 {
    public Card3_089o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. On Tyrant, also adds 1 to armor. When in battle with an Imperial leader, subtracts 1 from opponent's total battle destiny.");
        setTextState(new TextState("3_89", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new ArmorModifier(self, Filters.and(Filters.Tyrant, Filters.hasPiloting(self)), 1));
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleWithCondition(self, Filters.Imperial_leader),
                -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
