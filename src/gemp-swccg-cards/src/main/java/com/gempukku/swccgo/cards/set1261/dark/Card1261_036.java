package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnLostPileEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 6.1 Protocol Failure. */
public class Card1261_036 extends AbstractImmediateEffect {
    private static final Filter UNIQUE_EPISODE_I_DROID = Filters.and(Filters.unique, Icon.EPISODE_I, Filters.droid);
    private static final String DEPLOY_ON_DROID = "Deploy Protocol Failure on droid (its game text is canceled)";
    private static final String PLACE_IN_LOST_PILE = "Place Protocol Failure in Lost Pile";

    public Card1261_036() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Protocol Failure", Uniqueness.UNRESTRICTED,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("'Oh my! Oh, uh, I'm, I'm terribly sorry. I. . . I didn't mean to intrude. No, no, no, please don't get up. No!'");
        setGameText("Deploy on a site to cancel a Force drain there. Vader is deploy -1 here. At end of your turn, place this Effect in Lost Pile. OR At any time, target a unique (•) [Episode I] droid. Opponent chooses: deploy on droid (its game text is canceled), or place this Effect in Lost Pile (you may take any one card into hand from Reserve Deck; reshuffle). (Immune to Control.)");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId id, boolean asReact) {
        return Filters.or(Filters.site, UNIQUE_EPISODE_I_DROID);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        if (!GameConditions.canTarget(game, self, UNIQUE_EPISODE_I_DROID))
            return null;
        var opponent = game.getOpponent(playerId);
        var action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Target a unique [Episode I] droid");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose droid", UNIQUE_EPISODE_I_DROID) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard droid) {
                action.addAnimationGroup(droid);
                action.allowResponses("Target " + GameUtils.getCardLink(droid),
                        new com.gempukku.swccgo.logic.effects.RespondableEffect(action) {
                            @Override
                            protected void performActionResults(com.gempukku.swccgo.logic.timing.Action targetingAction) {
                                var target = action.getPrimaryTargetCard(targetGroupId);
                                action.appendEffect(new PlayoutDecisionEffect(action, opponent,
                                        new MultipleChoiceAwaitingDecision("Choose result of Protocol Failure targeting "
                                                + GameUtils.getCardLink(target), new String[]{DEPLOY_ON_DROID, PLACE_IN_LOST_PILE}) {
                                            @Override
                                            protected void validDecisionMade(int index, String result) {
                                                if (index == 0) {
                                                    game.getGameState().sendMessage(opponent + " chooses to have "
                                                            + GameUtils.getCardLink(self) + " deployed on " + GameUtils.getCardLink(target));
                                                    action.appendEffect(new DeployCardToTargetFromHandEffect(action, self,
                                                            Filters.sameCardId(target), true, false));
                                                } else {
                                                    game.getGameState().sendMessage(opponent + " chooses to have "
                                                            + GameUtils.getCardLink(self) + " placed in Lost Pile");
                                                    action.appendEffect(new PutCardFromHandOnLostPileEffect(action, playerId, self, false));
                                                    action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                                                            new YesNoDecision("Take any one card into hand from Reserve Deck?") {
                                                                @Override
                                                                protected void yes() {
                                                                    action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, true));
                                                                }
                                                            }));
                                                }
                                            }
                                        }));
                            }
                        });
            }
        });
        return List.of(action);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.forceDrainInitiated(game, result) || !GameConditions.canCancelForceDrain(game, self))
            return null;
        var drain = game.getGameState().getForceDrainState();
        if (drain == null || !Filters.site.accepts(game, drain.getLocation()))
            return null;
        var action = getPlayCardAction(playerId, game, self, self, false, 0, null, null,
                null, null, null, false, 0, Filters.and(Filters.site, Filters.sameLocationId(drain.getLocation())), null);
        return action == null ? null : List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var attachment = self.getAttachedTo();
        if (attachment != null && Filters.droid.accepts(game, attachment))
            return List.of(new CancelsGameTextModifier(self, Filters.hasAttached(self)));
        if (attachment == null || !Filters.site.accepts(game, attachment))
            return null;
        return List.of(new DeployCostToLocationModifier(self, Filters.Vader, -1, Filters.sameSite(self)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var attachment = self.getAttachedTo();
        if (attachment == null || !Filters.site.accepts(game, attachment))
            return null;
        var drain = game.getGameState().getForceDrainState();
        if (TriggerConditions.justDeployed(game, result, self) && drain != null
                && Filters.sameLocationId(attachment).accepts(game, drain.getLocation())
                && GameConditions.canCancelForceDrain(game, self)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.appendEffect(new CancelForceDrainEffect(action));
            return List.of(action);
        }
        if (TriggerConditions.isEndOfYourTurn(game, result, self)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.appendEffect(new LoseCardFromTableEffect(action, self));
            return List.of(action);
        }
        return null;
    }
}
