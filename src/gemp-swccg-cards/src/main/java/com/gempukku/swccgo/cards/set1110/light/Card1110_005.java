package com.gempukku.swccgo.cards.set1110.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10: Fourth Anthology
 * Type: Character
 * Subtype: Rebel
 * Title: Luke
 */
public class Card1110_005 extends AbstractRebel {
    public Card1110_005() {
        super(Side.LIGHT, 1, 3, 3, 4, 7, "Luke", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_10, Rarity.V);
        setVirtualSuffix(true);
        setLore("Raised by guardians Owen and Beru Lars on a moisture farm on Tatooine, where Owen wanted him to stay. Nicknamed 'Wormie' by childhood friends Camie and Fixer.");
        setGameText("Adds 3 to power of anything he pilots. Adds one battle destiny with Biggs or if piloting a T-16. While piloting a T-16 alone at Beggar's Canyon, opponent must first use 1 Force to draw a card from Force Pile or retrieve a Force. Once per game, may deploy a T-16 here from Reserve Deck; reshuffle.");
        addPersona(Persona.LUKE);
        addIcons(Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(new WithCondition(self, Filters.Biggs), new PilotingCondition(self, Filters.T_16)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LUKE_V__DEPLOY_T16;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a T-16 here from Reserve Deck");
            action.setActionMsg("Deploy a T-16 here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.T_16, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());
        AndCondition t16AloneAtCanyon = new AndCondition(new PilotingCondition(self, Filters.and(Filters.T_16, Filters.alone)),
                new AtCondition(self, Filters.Beggars_Canyon));
        if (!t16AloneAtCanyon.isFulfilled(game.getGameState(), game.getModifiersQuerying())) {
            return null;
        }

        if (TriggerConditions.isAboutToDrawCardFromForcePile(game, effectResult, opponent)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add cost to draw from Force Pile");
            action.setActionMsg("Make opponent use 1 Force to draw a card from Force Pile");
            action.appendEffect(
                    new UseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }

        if (TriggerConditions.isAboutToRetrieveForce(game, effectResult, opponent)) {
            AboutToRetrieveForceResult result = (AboutToRetrieveForceResult) effectResult;
            if (result.getSourceCard() != null && game.getModifiersQuerying().isForceRetrievalImmuneToSecretPlans(game.getGameState(), result.getSourceCard())) {
                return null;
            }
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add cost to retrieve Force");
            action.setActionMsg("Make opponent use 1 Force to retrieve Force");
            if (GameConditions.canUseForce(game, opponent, 1)) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new YesNoDecision("Do you want to use 1 Force to proceed with Force retrieval?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new UseForceEffect(action, opponent, 1));
                                    }

                                    @Override
                                    protected void no() {
                                        action.appendEffect(
                                                new CancelForceRetrievalEffect(action));
                                    }
                                }
                        )
                );
            } else {
                action.appendEffect(
                        new CancelForceRetrievalEffect(action));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
