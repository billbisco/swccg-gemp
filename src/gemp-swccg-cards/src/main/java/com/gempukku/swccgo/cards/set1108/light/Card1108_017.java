package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Tamtel Skreej
 */
public class Card1108_017 extends AbstractAlien {
    public Card1108_017() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, Title.Tamtel_Skreej, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Gambler. Lando Calrissian posed as a guard for Jabba in order to spy on the Hutt. Feared that he would be recognized by some of Jabba's companions.");
        setGameText("Deploys only to a Tatooine site as an Undercover spy. Once during each control phase, if Undercover, may relocate your character at a related site to Lando's site (for free). Once per game, may deploy Vibro-Ax on Lando from Reserve Deck for free; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.JABBAS_PALACE);
        addPersona(Persona.LANDO);
        addKeywords(Keyword.GAMBLER, Keyword.SPY);
        setDeploysAsUndercoverSpy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Tatooine_site;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId relocateId = GameTextActionId.LEGACY__TAMTEL_V__RELOCATE;
        Filter characterAtRelatedSite = Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.relatedSite(self)));

        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, relocateId, Phase.CONTROL)
                && GameConditions.isUndercover(game, self)
                && GameConditions.canTarget(game, self, characterAtRelatedSite)
                && self.getAtLocation() != null) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, relocateId);
            action.setText("Relocate character to Lando's site");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character to relocate", characterAtRelatedSite) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to Lando's site",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, finalCharacter, self.getAtLocation()));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId vibroId = GameTextActionId.LEGACY__TAMTEL_V__DEPLOY_VIBRO_AX;
        if (GameConditions.isOncePerGame(game, self, vibroId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, vibroId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, vibroId);
            action.setText("Deploy Vibro-Ax on Lando from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Vibro_Ax, Filters.sameCardId(self), true, true));
            actions.add(action);
        }

        return actions;
    }
}
