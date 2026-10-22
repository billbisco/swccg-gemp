package com.gempukku.swccgo.cards.set1200.dark;

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
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Shields
 * Type: Defensive Shield
 * Title: Imperial Detention
 */
public class Card1200_003 extends AbstractDefensiveShield {
    public Card1200_003() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Detention", ExpansionSet.VIRTUAL_SHIELDS, Rarity.V);
        setLore("In an effort to direct Luke toward Vader, Captain Bewil used his control of hatchways and lift tubes to cut off Luke's support, limiting his options and resources.");
        setGameText("Play on table. I'd Just As Soon Kiss A Wookiee may not be played. For opponent to deploy a card with ability for free, they must first use X Force, where X = 1/2 that card's printed deploy cost (round up). For opponent to deploy a card with ability for 0 Force, they must first use 1 Force.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.Id_Just_As_Soon_Kiss_A_Wookiee));
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self,
                Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility),
                new HalfPrintedDeployCostRoundedUpEvaluator()));
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self,
                Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility),
                new OneIfDeploysForZeroForceEvaluator()));
        return modifiers;
    }

    static class HalfPrintedDeployCostRoundedUpEvaluator extends BaseEvaluator {
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
