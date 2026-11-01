package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Jedi Master
 * Title: Plo Koon (V)
 */
public class Card1116_035 extends AbstractJediMaster {
    public Card1116_035() {
        super(Side.LIGHT, 2, 6, 5, 7, 5, "Plo Koon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Adds 2 to power of anything he pilots. Once per game, may deploy a weapon here from Reserve Deck; reshuffle. While piloting, may battle regardless of objective restrictions. If a battle was just initiated here, may place your weapon here in Used Pile to make all non-vehicle weapon destiny draws -1.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.PLO);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.<Modifier>singletonList(new AddsPowerToPilotedBySelfModifier(self, 2));
    }
}
