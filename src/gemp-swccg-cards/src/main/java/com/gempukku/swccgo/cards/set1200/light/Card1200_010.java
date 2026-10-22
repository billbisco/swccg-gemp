package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.evaluators.OneIfDeploysForZeroForceEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Shields
 * Type: Defensive Shield
 * Title: The Professor
 */
public class Card1200_010 extends AbstractDefensiveShield {
    public Card1200_010() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Professor", ExpansionSet.VIRTUAL_SHIELDS, Rarity.V);
        setVirtualSuffix(true);
        setLore("Protocol droids are programmed to interface with a variety of computer technologies. Quick and precise interpretation can dramatically increase operational efficiency.");
        setGameText("Plays on table. It Can Wait may not be played. For opponent to deploy a card with ability for free, they must first use X Force, where X = 1/2 that card's printed deploy cost (round up). For opponent to deploy a card with ability for 0 Force, they must first use 1 Force. May not be canceled.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.It_Can_Wait));
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self,
                Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility),
                new HalfPrintedDeployCostRoundedUpEvaluator()));
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self,
                Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility),
                new OneIfDeploysForZeroForceEvaluator()));
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
