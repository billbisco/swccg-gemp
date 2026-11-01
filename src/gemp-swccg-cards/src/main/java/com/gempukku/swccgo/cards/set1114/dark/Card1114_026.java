package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Character
 * Subtype: Imperial
 * Title: Grand Admiral Thrawn
 */
public class Card1114_026 extends AbstractImperial {
    public Card1114_026() {
        super(Side.DARK, 1, 4, 2, 4, 7, "Grand Admiral Thrawn", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Adds 3 to power of anything he pilots. Your [Independent] starships are lost. Once per turn, if at a system of parsec > 6, may deploy a Dreadnaught here from under your Starting Effect. Once per turn, if opponent's alien at same or related location, may peek at opponent's hand.");
        addPersona(Persona.THRAWN);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AddsPowerToPilotedBySelfModifier(self, 3));
    }
}
