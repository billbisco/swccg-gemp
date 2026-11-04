package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Vehicle
 * Subtype: Combat
 * Title: Low-Altitude Assault Transport
 */
public class Card1291_073 extends AbstractCombatVehicle {
    public Card1291_073() {
        super(Side.LIGHT, 3, 4, 4, null, 5, 3, 5, "Low-Altitude Assault Transport", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Enclosed.");
        setGameText("May add 2 pilots and 6 passengers. May move as a 'react'. Permanent pilot provides ability of 2. Clones on Cloning Cylinders deploy -1 aboard. While a clone aboard, immune to attrition < 4.");
        addIcons(Icon.CLONE_ARMY, Icon.PILOT, Icon.EPISODE_I);
        addModelType(ModelType.LAAT);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(2);
        setPassengerCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new DeployCostAboardModifier(self, Filters.and(Filters.clone, Filters.stackedOn(self, Filters.Cloning_Cylinders)), -1, self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasAboardCondition(self, Filters.clone), 4));
        return modifiers;
    }
}
