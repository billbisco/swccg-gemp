package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Toryn Farr
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship she pilots ("any starship she pilots"); Decipher Glossary 2.0 (November 1998) changed it to "anything she pilots" (the current card, Card3_023). The rest of the printed text plays as the current card, so only the pilot bonus is limited to starships here.
 */
public class Card3_023o extends Card3_023 {
    public Card3_023o() {
        super();
        setGameText("Adds 2 to power of any starship she pilots. When at any war room, adds 1 to weapon destiny draws of your Planet Defender Ion Cannon on same planet.");
        setTextState(new TextState("3_23", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(self), Filters.Planet_Defender_Ion_Cannon, Filters.onSamePlanet(self)),
                new AtCondition(self, Filters.war_room), 1));
        return modifiers;
    }
}
