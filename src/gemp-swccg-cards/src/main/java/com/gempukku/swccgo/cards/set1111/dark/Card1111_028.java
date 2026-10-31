package com.gempukku.swccgo.cards.set1111.dark;

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
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Effect
 * Title: Restricted Access
 */
public class Card1111_028 extends AbstractNormalEffect {
    public Card1111_028() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Restricted_Access, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("In an effort to direct Luke toward Vader, Lieutenant Bewil used his control of hatchways and lift tubes to cut off Luke's support, limiting his options and resources.");
        setGameText("Deploy on table. I'd Just As Soon Kiss A Wookiee may not be played. After starting hands are drawn, to deploy a character for free (or 0 Force), opponent must first use X Force, where X = half that character's printed deploy cost (round up). (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.Id_Just_As_Soon_Kiss_A_Wookiee));
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self,
                Filters.and(Filters.opponents(self), Filters.character),
                new HalfPrintedDeployCostRoundedUpEvaluator()));
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
    }
}
