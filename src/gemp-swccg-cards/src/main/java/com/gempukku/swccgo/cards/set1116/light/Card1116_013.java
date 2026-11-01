package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.StackedOnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Alien
 * Title: Chewbacca Of Kashyyyk (V)
 */
public class Card1116_013 extends AbstractAlien {
    public Card1116_013() {
        super(Side.LIGHT, 1, 6, 7, 2, 7, "Chewbacca Of Kashyyyk", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setSpecies(Species.WOOKIEE);
        setGameText("Adds 2 to power of anything he pilots. Deploys -2 to Kashyyyk. While stacked on Grrrghrrrgh!, Wookiees are power and forfeit +1. While hit, Chewie's power is doubled. If you have 8 or more cards in hand, add one destiny to total power in any battle involving your Wookiee.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_I);
        addPersona(Persona.CHEWIE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        StackedOnCondition stackedOnRoar = new StackedOnCondition(self, Filters.title("Grrrghrrrgh!"));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Kashyyyk_site));
        modifiers.add(new PowerModifier(self, Filters.Wookiee, stackedOnRoar, 1));
        modifiers.add(new ForfeitModifier(self, Filters.Wookiee, stackedOnRoar, 1));
        return modifiers;
    }
}
