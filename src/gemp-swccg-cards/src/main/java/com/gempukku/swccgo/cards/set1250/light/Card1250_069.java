package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5 Light Wookiee Roar. */
public class Card1250_069 extends AbstractLostInterrupt {
    public Card1250_069() {
        super(Side.LIGHT, 3, Title.Wookiee_Roar, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"GHRRRRAARRRRHG!\"");
        setGameText("If a battle was just initiated involving a Wookiee, add one destiny to your total power (two if alone). That Wookiee is defense value +3 for remainder of turn. OR If you have 8 or more cards in hand, cancel a Force drain.");
        addIcons(Icon.A_NEW_HOPE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        Filter wookiee = Filters.and(Filters.Wookiee, Filters.participatingInBattle, Filters.canBeTargetedBy(self));
        if (TriggerConditions.battleInitiated(game, effectResult) && GameConditions.canTarget(game, self, wookiee)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add destiny to total power");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose a Wookiee", wookiee) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard target = targetingAction.getPrimaryTargetCard(targetGroupId);
                            int draws = Filters.alone.accepts(game, target) ? 2 : 1;
                            action.appendEffect(new AddDestinyToTotalPowerEffect(action, draws));
                            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                    new DefenseValueModifier(self, target, 3), "Wookiee is defense value +3"));
                        }
                    });
                }
            });
            actions.add(action);
        }
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.canBeTargetedBy(self))
                && GameConditions.canCancelForceDrain(game, self)
                && game.getGameState().getHand(playerId).size() >= 8) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new CancelForceDrainEffect(action));
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
