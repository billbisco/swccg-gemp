package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Character
 * Subtype: Alien
 * Title: Wicket
 */
public class Card1109_025 extends AbstractAlien {
    public Card1109_025() {
        super(Side.LIGHT, 2, 3, 1, 2, 2, Title.Wicket, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Ewok scout. Son of Shodu and Deej. Found Leia and befriended her. Persuaded his tribe to help.");
        setGameText("Deploys only on Endor. Once per game, may use 3 Force to release a Rebel captive present on Endor. During your deploy phase, may use 1 Force to deploy an Ewok weapon or a unique (•) Ewok from Reserve Deck; reshuffle.");
        addIcons(Icon.ENDOR);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.EWOK);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Endor;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId releaseId = GameTextActionId.LEGACY__WICKET_V__RELEASE_CAPTIVE;
        Filter rebelCaptiveOnEndor = Filters.and(Filters.Rebel, Filters.captive, Filters.on(Title.Endor));

        if (GameConditions.isOncePerGame(game, self, releaseId)
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, rebelCaptiveOnEndor)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, releaseId);
            action.setText("Release a Rebel captive on Endor");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Rebel captive", SpotOverride.INCLUDE_CAPTIVE, rebelCaptiveOnEndor) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Release " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalCaptive = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new ReleaseCaptiveEffect(action, finalCaptive));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId deployId = GameTextActionId.LEGACY__WICKET_V__DEPLOY_EWOK;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, deployId, Phase.DEPLOY)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployId);
            action.setText("Deploy Ewok weapon or unique Ewok");
            action.setActionMsg("Deploy an Ewok weapon or a unique Ewok from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.and(Keyword.EWOK_WEAPON), Filters.and(Filters.Ewok, Filters.unique)), true));
            actions.add(action);
        }

        return actions;
    }
}
