package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.EachAsteroidDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Starship
 * Subtype: Starfighter
 * Title: Mist Hunter
 */
public class Card1105_044 extends AbstractStarfighter {
    public Card1105_044() {
        super(Side.DARK, 4, 3, 2, null, 3, 5, 6, Title.Mist_Hunter, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Commissioned by a group of Gand venture capitalists headed by Zuckuss. Manufactured by Byblos Drive Yards. Uses repulsor lift technology developed for combat cloud cars.");
        setGameText("May add 2 alien pilots and 3 passengers. Zuckuss deploys free aboard. While Zuckuss piloting, opponent's weapon, battle, and asteroid destiny draws here are -2. Immune to attrition < 3.");
        addIcons(Icon.DAGOBAH, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.BYBLOS_G1A_TRANSPORT);
        addPersona(Persona.MIST_HUNTER);
        setPilotCapacity(2);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.Zuckuss);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Zuckuss, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        HasPilotingCondition zuckussPiloting = new HasPilotingCondition(self, Filters.Zuckuss);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.weapon, Filters.here(self)), zuckussPiloting, -2));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), zuckussPiloting, -2, opponent));
        modifiers.add(new EachAsteroidDestinyModifier(self, zuckussPiloting, -2, Filters.here(self)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
