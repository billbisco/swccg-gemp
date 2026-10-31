package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Effect
 * Title: The Professor
 */
public class Card1109_022 extends AbstractNormalEffect {
    public Card1109_022() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.The_Professor, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Protocol droids are programmed to interface with a variety of computer technologies. Quick and precise interpretation can dramatically increase operational efficiency.");
        setGameText("Deploy on table. It Can Wait may not be played. After starting hands are drawn, for opponent to deploy a card with ability for free, opponent must first use X Force, where X = half that card's printed deploy cost (round up). May not be canceled. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.It_Can_Wait));
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self,
                Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility),
                new HalfPrintedDeployCostRoundedUpEvaluator()));
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        return modifiers;
    }

    private static class HalfPrintedDeployCostRoundedUpEvaluator extends BaseEvaluator {
        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
            float printed = cardAffected.getBlueprint().getDeployCost();
            if (printed < 0) {
                printed = 0;
            }
            return (float) Math.ceil(printed / 2.0f);
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
            return evaluateExpression(gameState, modifiersQuerying, cardAffected);
        }
    }
}
