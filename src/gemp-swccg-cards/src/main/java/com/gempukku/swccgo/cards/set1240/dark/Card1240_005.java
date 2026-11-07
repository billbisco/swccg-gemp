package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Character - Imperial
 * Title: Grand Admiral Thrawn
 */
public class Card1240_005 extends AbstractImperial {
    public Card1240_005() {
        super(Side.DARK, 1, 4, 2, 4, 7, "Grand Admiral Thrawn", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("The last remaining Grand Admiral. Found legendary Katana fleet of missing dreadnaughts. Military genius. Master of unorthodox tactics. Passionate collector of art. Leader.");
        setGameText("Adds 3 to power of anything he pilots. Your [Independent Starship] starships are lost. During your turn, if at a system, may deploy a Dreadnaught here from Reserve Deck; reshuffle (or retrieve a Dreadnaught). Once per turn, if opponent's alien at same or related location, may peek at opponent's hand.");
        addPersona(Persona.THRAWN);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 3));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, result)
                && !TriggerConditions.justDeployed(game, result, self)
                && !TriggerConditions.justDeployed(game, result, Filters.and(Filters.your(self), Filters.starship, Icon.INDEPENDENT))) {
            return null;
        }
        Collection<PhysicalCard> ships = Filters.filterActive(game, self,
                Filters.and(Filters.your(self), Filters.starship, Icon.INDEPENDENT));
        if (ships.isEmpty()) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Make your Independent starships lost");
        action.setActionMsg("Make " + GameUtils.getAppendedNames(ships) + " lost");
        action.appendEffect(new LoseCardsFromTableEffect(action, ships, true));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(
            String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>(3);
        PhysicalCard system = game.getModifiersQuerying().getLocationHere(game.getGameState(), self);
        // Deploying or retrieving a Dreadnaught share one use per turn.
        GameTextActionId dreadnaughtId = GameTextActionId.LEGACY__GRAND_ADMIRAL_THRAWN_V__DEPLOY_DREADNAUGHT;
        if (GameConditions.isDuringYourTurn(game, playerId) && system != null && Filters.system.accepts(game, system)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, dreadnaughtId)) {
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, dreadnaughtId, true, false)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, dreadnaughtId);
                action.setText("Deploy a Dreadnaught here from Reserve Deck");
                action.appendUsage(new OncePerTurnEffect(action));
                action.appendEffect(new DeployCardToLocationFromReserveDeckEffect(action, Filters.Dreadnaught,
                        Filters.sameCardId(system), true));
                actions.add(action);
            }
            if (GameConditions.canSearchLostPile(game, playerId, self, dreadnaughtId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, dreadnaughtId);
                action.setText("Retrieve a Dreadnaught");
                action.appendUsage(new OncePerTurnEffect(action));
                action.appendEffect(new RetrieveCardEffect(action, playerId, Filters.Dreadnaught));
                actions.add(action);
            }
        }

        String opponent = game.getOpponent(playerId);
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasHand(game, opponent)
                && !game.getModifiersQuerying().mayNotLookAtOpponentsHand(game.getGameState(), self, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.opponents(playerId), Filters.alien,
                        Filters.at(Filters.sameOrRelatedLocation(self))))) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Peek at opponent's hand");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new PeekAtOpponentsHandEffect(action, playerId));
            actions.add(action);
        }
        return actions;
    }
}
