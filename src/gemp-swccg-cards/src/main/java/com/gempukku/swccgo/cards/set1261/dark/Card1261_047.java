package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.List;

/** Legacy Block 6.1 We Have A Prisoner & I Can't Shake Him! */
public class Card1261_047 extends AbstractUsedOrLostInterrupt {
    private static final String SHAKE_HIM = "I Can't Shake Him!";

    public Card1261_047() {
        super(Side.DARK, 5, Title.We_Have_A_Prisoner + " & " + SHAKE_HIM, Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent(Title.We_Have_A_Prisoner, Uniqueness.UNIQUE),
                new TitleComponent(SHAKE_HIM, Uniqueness.UNIQUE));
        setGameText("USED: During opponent's deploy phase, deploy an Imperial of ability < 3 (except an admiral) aboard your starship from Reserve Deck; reshuffle. LOST: If opponent just lost or forfeited a character from battle, use 1 Force. Character is captured instead (for remainder of turn, you may not deploy Bounty on that character).");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        GameTextActionId search = GameTextActionId.I_CANT_SHAKE_HIM__DOWNLOAD_IMPERIAL_ABOARD_YOUR_STARSHIP;
        Filter starship = Filters.and(Filters.your(self), Filters.starship);
        if (!GameConditions.isDuringOpponentsPhase(game, self, Phase.DEPLOY)
                || !GameConditions.canSpot(game, self, Filters.or(starship, Filters.siteOfStarshipOrVehicle(starship)))
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, search, true, false))
            return null;

        var action = new PlayInterruptAction(game, self, search, CardSubtype.USED);
        action.setText("Deploy Imperial aboard your starship");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardAboardFromReserveDeckEffect(action,
                        Filters.and(Filters.Imperial, Filters.abilityLessThan(3), Filters.except(Filters.admiral)),
                        starship, true));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
                                                                        EffectResult result, PhysicalCard self) {
        var reason = TargetingReason.TO_BE_CAPTURED;
        Filter character = Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle,
                Filters.canBeTargetedBy(self, reason));
        if (!GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1))
            return null;

        if (TriggerConditions.isAboutToBeLost(game, result, character)) {
            var loss = (AboutToLoseCardFromTableResult) result;
            if (loss.getPreventableCardEffect() != null)
                return List.of(capture(playerId, game, self, loss.getCardToBeLost(), loss.getPreventableCardEffect()));
        } else if (TriggerConditions.isAboutToBeForfeited(game, result, character)) {
            var forfeit = (AboutToForfeitCardFromTableResult) result;
            return List.of(capture(playerId, game, self, forfeit.getCardToBeForfeited(), forfeit.getPreventableCardEffect()));
        }
        return null;
    }

    private PlayInterruptAction capture(String playerId, SwccgGame game, PhysicalCard self,
                                        PhysicalCard character, PreventableCardEffect departure) {
        var action = new PlayInterruptAction(game, self, CardSubtype.LOST);
        action.setText("Capture " + GameUtils.getFullName(character));
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose character",
                TargetingReason.TO_BE_CAPTURED, character) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                action.addAnimationGroup(targetedCard);
                action.appendCost(new UseForceEffect(action, playerId, 1));
                action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard), new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        PhysicalCard captured = action.getPrimaryTargetCard(targetGroupId);
                        departure.preventEffectOnCard(captured);
                        action.appendEffect(new RestoreCardToNormalEffect(action, captured));
                        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                new MayNotDeployToTargetModifier(self,
                                        Filters.and(Filters.your(self), Filters.title(Title.Bounty), Filters.not(Filters.in_play)),
                                        Filters.samePermanentCardId(captured)),
                                "May not deploy Bounty on " + GameUtils.getCardLink(captured) + " for remainder of turn"));
                        action.appendEffect(new CaptureCharacterOnTableEffect(action, captured));
                    }
                });
            }
        });
        return action;
    }
}
