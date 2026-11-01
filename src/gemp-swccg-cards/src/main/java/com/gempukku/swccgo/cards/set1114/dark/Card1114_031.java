package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ChooseTargetGroupToRetargetCardBeingPlayedFromToSameSideOfForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.RetargetCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForRetargetingEffect;
import com.gempukku.swccgo.logic.effects.TargetGroupOnSameSideOfForceToRetargetBeingPlayedToEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Trade Federation Tactics
 */
public class Card1114_031 extends AbstractUsedOrLostInterrupt {
    public Card1114_031() {
        super(Side.DARK, 6, "Trade Federation Tactics", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setGameText("USED: For remainder of turn, if your starship is about to be hit (or lost), that action may instead affect your character aboard that starship. LOST: If your [Episode I] character is about to be hit (or lost), that action instead affects your other [Episode I] character present.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    private PlayInterruptAction retarget(final String playerId, final SwccgGame game, final PhysicalCard self, final Effect effect, CardSubtype subtype) {
        final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
        final PlayInterruptAction action = new PlayInterruptAction(game, self, subtype);
        action.setText("Retarget " + GameUtils.getFullName(respondableEffect.getCard()));
        action.appendTargeting(new TargetCardBeingPlayedForRetargetingEffect(action, respondableEffect) {
            @Override
            protected void cardTargetedToBeRetargeted(final PhysicalCard cardBeingPlayed) {
                action.appendTargeting(new ChooseTargetGroupToRetargetCardBeingPlayedFromToSameSideOfForceEffect(action, respondableEffect) {
                    @Override
                    public void groupChosenToRetarget(final int groupIdToRetarget) {
                        action.appendTargeting(new TargetGroupOnSameSideOfForceToRetargetBeingPlayedToEffect(action, respondableEffect, groupIdToRetarget) {
                            @Override
                            public void cardsRetargetedTo(final int targetGroupId1, Collection<PhysicalCard> targetedCards) {
                                action.allowResponses("Re-target " + GameUtils.getCardLink(cardBeingPlayed), new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId1);
                                        action.appendEffect(new RetargetCardBeingPlayedEffect(action, respondableEffect, groupIdToRetarget, finalTargets));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        return action;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.any, Filters.and(Filters.your(self), Filters.starship))
                && GameConditions.canRetargetCardBeingPlayed(game, self, effect)) {
            actions.add(retarget(playerId, game, self, effect, CardSubtype.USED));
        }
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.any, Filters.and(Filters.your(self), Filters.character, Icon.EPISODE_I))
                && GameConditions.canRetargetCardBeingPlayed(game, self, effect)) {
            actions.add(retarget(playerId, game, self, effect, CardSubtype.LOST));
        }
        return actions;
    }
}
