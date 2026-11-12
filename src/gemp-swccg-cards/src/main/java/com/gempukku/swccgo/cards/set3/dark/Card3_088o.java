package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Imperial Gunner
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to weapon destiny draws only when aboard a starship as a passenger; Decipher Glossary 2.0 (November 1998) changed it to "anything he is aboard as a passenger" (the current card, Card3_088), which adds vehicles.
 */
public class Card3_088o extends Card3_088 {
    public Card3_088o() {
        super();
        setGameText("If aboard a starship as a passenger, adds 1 to starship's weapon destiny draws.");
        setTextState(new TextState("3_88", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, Filters.and(Filters.starship, Filters.hasPassenger(self)), 1));
        return modifiers;
    }
}
