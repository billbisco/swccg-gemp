package com.gempukku.swccgo.cards.set1220.dark;

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
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Starship
 * Subtype: Starfighter
 * Title: Slave I
 */
public class Card1220_079 extends AbstractStarfighter {
    public Card1220_079() {
        super(Side.DARK, 1, 3, 4, null, 4, 4, 5, Title.Slave_I, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Originally designed as a planetary defense craft. Uses restricted jamming technology, allowing it to appear out of nowhere. Contains many hidden armaments.");
        setGameText("May add 1 alien pilot and 3 passengers. May deploy with non-[Maintenance] Boba Fett as a react (for free). While Boba Fett piloting, maneuver +2, power may not be reduced, and immune to attrition < 5.");
        addIcons(Icon.CLOUD_CITY, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.FIRESPRAY_CLASS_ATTACK_SHIP);
        addPersona(Persona.SLAVE_I);
        setPilotCapacity(1);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.and(Filters.Boba_Fett, Filters.not(Filters.icon(Icon.MAINTENANCE))));
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactForFreeModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        HasPilotingCondition bobaPiloting = new HasPilotingCondition(self, Filters.Boba_Fett);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, bobaPiloting, 2));
        modifiers.add(new MayNotHavePowerReducedModifier(self, self, bobaPiloting, self.getOwner()));
        modifiers.add(new MayNotHavePowerReducedModifier(self, self, bobaPiloting, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, bobaPiloting, 5));
        return modifiers;
    }
}
