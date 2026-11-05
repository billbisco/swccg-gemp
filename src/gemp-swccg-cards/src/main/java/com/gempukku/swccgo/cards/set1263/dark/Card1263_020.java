package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Vehicle
 * Subtype: Combat
 * Title: Cyclone Walker
 */
public class Card1263_020 extends AbstractCombatVehicle {
    public Card1263_020() {
        super(Side.DARK, 1, 5, 5, 7, null, 1, 6, "Cyclone Walker", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("Enclosed.");
        setGameText("May add 1 pilot and 8 passengers. Permanent pilot provides ability of 1. While AT-AT Driver piloting, he is forfeit +2 and this vehicle is power +1 and immune to attrition < 5.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(8);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter atatDriver = Filters.title("AT-AT Driver");
        Condition driverPiloting = new HasPilotingCondition(self, atatDriver);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(atatDriver, Filters.piloting(self)), 2));
        modifiers.add(new PowerModifier(self, driverPiloting, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, driverPiloting, 5));
        return modifiers;
    }
}
