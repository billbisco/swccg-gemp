package com.gempukku.swccgo.cards.set1292.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.2
 * Type: Starship
 * Subtype: Starfighter
 * Title: Durge's NovaSword Fighter
 */
public class Card1292_005 extends AbstractStarfighter {
    public Card1292_005() {
        super(Side.DARK, 3, 3, 3, null, 4, 5, 5, "Durge's NovaSword Fighter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        setGameText("May add 2 pilots or passengers. Durge deploys -1 aboard. Adds one destiny to power if your [Separatist] card aboard (or one battle destiny if Durge aboard). Immune to attrition < 4 (< 6 if Durge aboard).");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.SEPARATIST, Icon.EPISODE_I);
        addModelType(ModelType.SPACE_SUPERIORITY_FIGHTER);
        setPilotOrPassengerCapacity(2);
    }

    private static Filter durge() {
        return Filters.and(Filters.character, Filters.title("Durge"));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, durge(), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, durge(), -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition durgeAboard = new HasAboardCondition(self, durge());
        Condition yourSeparatistCardAboard = new HasAboardCondition(self, Filters.and(Filters.your(self), Icon.SEPARATIST));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new AndCondition(yourSeparatistCardAboard, new NotCondition(durgeAboard)), 1));
        modifiers.add(new AddsBattleDestinyModifier(self, durgeAboard, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new NotCondition(durgeAboard), 4));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, durgeAboard, 6));
        return modifiers;
    }
}
