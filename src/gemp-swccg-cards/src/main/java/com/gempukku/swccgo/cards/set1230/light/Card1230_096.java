package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.PlayCardAsAttachedAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Virtual Block 3: The Time For Our Attack Has Come (V). */
public class Card1230_096 extends AbstractImmediateEffect {
    public Card1230_096() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "The Time For Our Attack Has Come", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'With the Imperial Fleet spread throughout the galaxy in a vain effort to engage us, it is relatively unprotected.'");
        setGameText("If you just deployed or moved a second Rebel starfighter to a system, deploy from Reserve Deck (reshuffle) (or deploy from Lost Pile) a [Death Star II] starship there; deploy on that starship: it is power and hyperspeed +2 and immune to attrition < 5. Once per game, this starship may make an additional move. (Immune to Control.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
                                                        PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.starship, Icon.DEATH_STAR_II);
    }

    private PhysicalCard secondStarfighterDestination(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        PhysicalCard destination;
        int arrivals;
        if (TriggerConditions.justDeployedToLocation(game, result, playerId, Filters.Rebel_starfighter, Filters.system)) {
            PlayCardResult play = (PlayCardResult) result;
            destination = play.getToLocation();
            arrivals = Filters.and(Filters.in_play, Filters.Rebel_starfighter, Filters.at(destination))
                    .acceptsCount(game.getGameState(), game.getModifiersQuerying(), play.getPlayedCard());
        }
        else if (TriggerConditions.movedToLocationBy(game, result, playerId, Filters.Rebel_starfighter, Filters.system)) {
            MovedResult move = (MovedResult) result;
            destination = move.getMovedTo();
            arrivals = 0;
            for (PhysicalCard card : move.getMovedCards()) {
                arrivals += Filters.and(Filters.in_play, Filters.Rebel_starfighter, Filters.at(destination))
                        .acceptsCount(game.getGameState(), game.getModifiersQuerying(), card);
            }
        }
        else {
            return null;
        }
        int present = Filters.countActive(game, self, Filters.and(Filters.Rebel_starfighter, Filters.at(destination)));
        return present >= 2 && present - arrivals < 2 ? destination : null;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
                                                                   EffectResult effectResult, PhysicalCard self,
                                                                   int gameTextSourceCardId) {
        PhysicalCard system = secondStarfighterDestination(playerId, game, effectResult, self);
        if (system == null)
            return null;

        List<PlayCardAction> actions = new LinkedList<>();
        GameTextActionId searchId = GameTextActionId.LEGACY__THE_TIME_FOR_OUR_ATTACK_HAS_COME__DEPLOY_STARSHIP;
        boolean forFree = forceMayDeployForFree() || isCardTypeAlwaysPlayedForFree();
        for (PlayCardOption option : getPlayCardOptions(playerId, game)) {
            if (!checkPlayRequirements(playerId, game, self, null, option, null)
                    || !GameConditions.canUseForceToDeployCard(game, self, self, option, forFree, 0, false))
                continue;
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, searchId, true, false))
                actions.add(deployWithNewHost(playerId, game, self, option, system, forFree, true));
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, searchId, true))
                actions.add(deployWithNewHost(playerId, game, self, option, system, forFree, false));
        }
        return actions;
    }

    private PlayCardAction deployWithNewHost(String playerId, SwccgGame game, PhysicalCard self,
                                              PlayCardOption option, PhysicalCard system,
                                              boolean forFree, boolean fromReserve) {
        final PhysicalCard[] host = new PhysicalCard[1];
        Filter normalTarget = getValidDeployTargetFilter(playerId, game, self, self, option,
                forFree, 0, null, null, null, false, false);
        Filter newlyDeployedHost = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
                return card == host[0] && Filters.and(Filters.in_play, Filters.at(system)).accepts(gameState, modifiersQuerying, card)
                        && normalTarget.accepts(gameState, modifiersQuerying, card);
            }
        };
        PlayCardAsAttachedAction action = new PlayCardAsAttachedAction(self, self, option,
                forFree, 0, null, self.getBlueprint().getDeployTargetSpotOverride(option.getId()), newlyDeployedHost);
        action.setText("Deploy with starship from " + (fromReserve ? "Reserve Deck" : "Lost Pile"));
        Filter ship = Filters.and(Filters.starship, Icon.DEATH_STAR_II, Filters.canBeTargetedBy(self));
        if (fromReserve) {
            action.appendPreTargetingCost(new DeployCardToLocationFromReserveDeckEffect(action, ship, Filters.sameCardId(system), true) {
                @Override
                protected void cardDeployed(PhysicalCard card) {
                    host[0] = card;
                }
            });
        }
        else {
            action.appendPreTargetingCost(new DeployCardToLocationFromLostPileEffect(action, ship, Filters.sameCardId(system), false, false) {
                @Override
                protected void cardDeployed(PhysicalCard card) {
                    host[0] = card;
                }
            });
        }
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter host = Filters.hasAttached(self);
        return Arrays.asList(new PowerModifier(self, host, 2), new HyperspeedModifier(self, host, 2),
                new ImmuneToAttritionLessThanModifier(self, host, 5));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                      PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId moveId = GameTextActionId.OVS__TIME_FOR_OUR_ATTACK__ADDITIONAL_MOVE;
        PhysicalCard host = self.getAttachedTo();
        if (host == null || !GameConditions.isOncePerGame(game, self, moveId)
                || !Filters.movableAsAdditionalMove(playerId).accepts(game, host))
            return null;
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, moveId);
        action.setText("Make attached starship move again");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new MoveCardAsRegularMoveEffect(action, playerId, host, false, true, Filters.any));
        return Collections.singletonList(action);
    }
}
