package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Arrays;
import java.util.List;

/** Snowspeeder Garrison, Virtual Block 6.1. */
public class Card1261_110 extends AbstractCombatVehicle {
    public Card1261_110() {
        super(Side.LIGHT, 2, 9, 9, null, 4, 5, 12, "Snowspeeder Garrison", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Enclosed.");
        setGameText("Deploys -3 to Hoth. Permanent pilots provide total ability of 6. While on Hoth, defense value +2, immune to attrition < 4, and may move as a 'react' for +1 Force");
        addModelType(ModelType.T_47);
        addIcons(Icon.HOTH);
        addIcon(Icon.PILOT, 3);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Arrays.asList(new AbstractPermanentPilot(2) {}, new AbstractPermanentPilot(2) {}, new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Arrays.asList(new DeployCostToLocationModifier(self, -3, Filters.Hoth_location));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition onHoth = new OnCondition(self, Title.Hoth);
        return Arrays.asList(new DefenseValueModifier(self, onHoth, 2),
                new ImmuneToAttritionLessThanModifier(self, onHoth, 4),
                new MayMoveAsReactToLocationModifier(self, onHoth, Filters.any, 1));
    }
}
