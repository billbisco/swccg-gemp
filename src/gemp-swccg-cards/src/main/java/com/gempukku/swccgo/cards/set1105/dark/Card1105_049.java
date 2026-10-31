package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.LoseDestinyCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Effect
 * Title: The Dark Path
 */
public class Card1105_049 extends AbstractNormalEffect {
    public Card1105_049() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.The_Dark_Path, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("'If once you start down the dark path, forever will it dominate your destiny. Consume you it will, as it did Obi-Wan's apprentice.'");
        setGameText("Deploy on table. During battle, if you just drew a destiny, may lose that card to cancel and redraw that destiny. When you draw battle destiny, may lose X Force (limit 3) to make attrition against opponent +X.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose destiny card to cancel and redraw");
            action.appendCost(
                    new LoseDestinyCardEffect(action));
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            actions.add(action);
        }

        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose X Force to add attrition");
            action.appendCost(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose X (Force to lose)", 1, Math.min(3, game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), playerId)), 1) {
                                @Override
                                public void decisionMade(int x) {
                                    action.appendCost(
                                            new LoseForceEffect(action, playerId, x));
                                    action.appendEffect(
                                            new AddUntilEndOfBattleModifierEffect(action,
                                                    new AttritionModifier(self, x, opponent),
                                                    "Attrition against " + opponent + " is +" + x));
                                }
                            }));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
