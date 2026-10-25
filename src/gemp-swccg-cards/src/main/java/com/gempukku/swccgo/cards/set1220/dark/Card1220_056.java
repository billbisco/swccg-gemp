package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: ComScan Detection
 */
public class Card1220_056 extends AbstractUsedInterrupt {
    public Card1220_056() {
        super(Side.DARK, 4, Title.ComScan_Detection, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Imperial Navy boasts the best communications network in the galaxy. Sophisticated control technology allows the Empire to dispatch armed forces without delay.");
        setGameText("Peek at the top card of opponent's Force Pile. OR Raise your converted Mos Espa to the top. OR If opponent just deployed a planet site deploy a related location from Reserve Deck; reshuffle. OR Once per game, deploy Death Star: War Room, Meditation Chamber, or Wampa Cave from Reserve Deck; reshuffle.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        if (GameConditions.hasForcePile(game, opponent)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at top of opponent's Force Pile");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PeekAtTopCardOfForcePileEffect(action, playerId, opponent));
                        }
                    }
            );
            actions.add(action);
        }

        Filter mosEspaFilter = Filters.and(Filters.Mos_Espa, Filters.canBeConvertedByRaisingYourLocationToTop(playerId));
        if (GameConditions.canTarget(game, self, mosEspaFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Raise converted Mos Espa to the top");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Mos Espa", mosEspaFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId deploySiteId = GameTextActionId.LEGACY__COMSCAN_DETECTION_V__DEPLOY_SITE;
        Filter siteFilter = Filters.or(Filters.title(Title.Death_Star_War_Room), Filters.title(Title.Meditation_Chamber), Filters.title(Title.Wampa_Cave));
        if (GameConditions.isOncePerGame(game, self, deploySiteId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deploySiteId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, deploySiteId);
            action.setText("Deploy War Room, Meditation Chamber, or Wampa Cave");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Deploy Death Star: War Room, Meditation Chamber, or Wampa Cave from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, siteFilter, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMSCAN_DETECTION_V__DEPLOY_RELATED_LOCATION;
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.planet_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            PhysicalCard site = ((PlayCardResult) effectResult).getPlayedCard();
            final Filter relatedFilter = Filters.relatedLocation(site);
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy a related location");
            action.allowResponses("Deploy a related location from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, relatedFilter, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
