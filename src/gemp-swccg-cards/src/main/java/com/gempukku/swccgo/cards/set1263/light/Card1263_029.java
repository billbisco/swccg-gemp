package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.ArrayList;
import java.util.List;

/** I'm Here To Rescue You (V), Legacy Block 6.3, final Light master page 34. */
public class Card1263_029 extends AbstractLostOrStartingInterrupt {
    private static final GameTextActionId LOST = GameTextActionId.LEGACY__IM_HERE_TO_RESCUE_YOU__LOST;
    private static final GameTextActionId START = GameTextActionId.LEGACY__IM_HERE_TO_RESCUE_YOU__STARTING;

    public Card1263_029() {
        super(Side.LIGHT, 6, Title.Im_Here_To_Rescue_You, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Huh?'");
        setGameText("If Rescue The Princess on table, LOST: Once per game, choose: Cancel an attempt to make non-Heroic R2-D2 lost. OR Lose 2 Force to cancel a battle just initiated if Leia defending alone. (Immune to Sense.) STARTING: ▼ Cell 2187 and one always-[Immune to Alter] Effect. Place this Interrupt in hand.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(String playerId, SwccgGame game, PhysicalCard self) {
        // 'If Rescue The Princess on table' prefixes only the LOST section; STARTING is always available.
        PlayInterruptAction action = new PlayInterruptAction(game, self, START, CardSubtype.STARTING);
        action.setText("Deploy Cell 2187 and an Effect always immune to Alter");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Cell_2187, true, false));
                Filter effect = Filters.and(Filters.Effect, Filters.always_immune_to_Alter, Filters.not(Filters.Cell_2187));
                action.appendEffect(new DeployCardsFromReserveDeckEffect(action, effect, 1, 1, true, true));
                action.appendEffect(new TakeCardFromVoidIntoHandEffect(action, playerId, self));
            }
        });
        return action;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        if (!GameConditions.canSpot(game, self, Filters.Rescue_The_Princess) || !GameConditions.isOncePerGame(game, self, LOST)) {
            return null;
        }
        List<PlayInterruptAction> actions = new ArrayList<>();
        Filter nonHeroic = Filters.and(Filters.R2D2, Filters.not(Filters.and(Keyword.HEROIC)));
        if (TriggerConditions.isAboutToBeLost(game, result, nonHeroic)) {
            AboutToLoseCardFromTableResult loss = (AboutToLoseCardFromTableResult) result;
            if (loss.getPreventableCardEffect() != null) {
                PlayInterruptAction action = lost(game, self, "Cancel the attempt to make R2-D2 lost");
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                loss.getPreventableCardEffect().preventEffectOnCard(loss.getCardToBeLost());
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }
        if (TriggerConditions.battleInitiated(game, result, game.getOpponent(playerId))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Leia, Filters.alone))
                && !game.getModifiersQuerying().mayNotCancelBattle(game.getGameState(), playerId, game.getGameState().getBattleLocation())
                && canLoseTwo(game, playerId, self)) {
            PlayInterruptAction action = lost(game, self, "Lose 2 Force to cancel battle");
            action.appendCost(new LoseForceEffect(action, playerId, 2, true));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new CancelBattleEffect(action));
                }
            });
            actions.add(action);
        }
        return actions;
    }

    private PlayInterruptAction lost(SwccgGame game, PhysicalCard self, String text) {
        PlayInterruptAction action = new PlayInterruptAction(game, self, LOST, CardSubtype.LOST);
        action.setText(text);
        action.setImmuneTo(Title.Sense);
        action.appendUsage(new OncePerGameEffect(action));
        return action;
    }

    private boolean canLoseTwo(SwccgGame game, String playerId, PhysicalCard self) {
        var state = game.getGameState();
        int hand = state.getHand(playerId).size() - (self.getZone() == Zone.HAND ? 1 : 0);
        return hand + state.getReserveDeckSize(playerId) + state.getForcePileSize(playerId) + state.getUsedPile(playerId).size() >= 2;
    }
}
