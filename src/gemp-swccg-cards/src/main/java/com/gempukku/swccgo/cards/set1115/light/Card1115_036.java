package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AllAbilityOnTableProvidedByCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Starship
 * Subtype: Starfighter
 * Title: YT-1300 Transport (V)
 */
public class Card1115_036 extends AbstractStarfighter {
    public Card1115_036() {
        super(Side.LIGHT, 3, 2, 2, null, 3, 5, 4, Title.YT_1300_Transport, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Reliable and durable, Widely used freighter made by Corellian Engineering Corporation. Sales have dramatically increased in proportion to the fame of Han Solo's ship.");
        setGameText("May add 1 pilot and 4 passengers. Permanent pilot provides ability of 1. While all your ability is provided by aliens, Jedi, and [Independent] starship pilots, this starship is forfeit +1, maneuver +1, and immune to attrition < 4.");
        addIcons(Icon.ENDOR, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(1);
        setPassengerCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition abilityProviders = new AllAbilityOnTableProvidedByCondition(self, self.getOwner(),
                Filters.or(Filters.alien, Filters.Jedi,
                        Filters.and(Filters.character, Filters.piloting(Filters.and(Filters.starship, Icon.INDEPENDENT))),
                        Filters.and(Filters.starship, Icon.INDEPENDENT, Filters.hasPermanentPilot)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, abilityProviders, 1));
        modifiers.add(new ManeuverModifier(self, abilityProviders, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, abilityProviders, 4));
        return modifiers;
    }
}
