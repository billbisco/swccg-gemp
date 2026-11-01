package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Starship
 * Subtype: Capital
 * Title: Republic Corvette
 */
public class Card1115_026 extends AbstractCapitalStarship {
    public Card1115_026() {
        super(Side.LIGHT, 1, 5, 5, 4, null, 3, 8, "Republic Corvette", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setGameText("May add 3 Republic pilots and 4 passengers. Quad Laser Cannons may not deploy aboard. Permanent pilot provides ability of 2. While with an Imperial-class Star Destroyer, power +1. Has ship-docking capability.");
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        setPilotCapacity(3);
        setPassengerCapacity(4);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Republic;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.title(Title.Quad_Laser_Cannon), self));
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.Imperial_class_Star_Destroyer), 1));
        return modifiers;
    }
}
