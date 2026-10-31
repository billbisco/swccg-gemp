package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: A Dark Time For The Rebellion
 */
public class Card1104_026 extends AbstractUsedInterrupt {
    public Card1104_026() {
        super(Side.DARK, 4, "A Dark Time For The Rebellion", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Absolute control wielded by the Emperor enables the Imperial forces to dominate planetary systems before the Rebel Alliance can gain a foothold.");
        setGameText("▲ a cave. OR If opponent just deployed a planet site, ▼ a related location.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId takeId = GameTextActionId.OVS__A_DARK_TIME__TAKE_CAVE;
        Filter cave = Filters.and(Filters.location, Filters.titleContains("Cave"));

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Take a cave into hand from Reserve Deck");
            action.allowResponses("Take a cave into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, cave, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId deployId = GameTextActionId.OVS__A_DARK_TIME__DEPLOY_RELATED_LOCATION;

        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.planet_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            PhysicalCard cardPlayed = ((PlayCardResult) effectResult).getPlayedCard();
            final Filter relatedLocation = Filters.relatedLocation(cardPlayed);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, deployId);
            action.setText("Deploy a related location from Reserve Deck");
            action.allowResponses("Deploy a related location from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, relatedLocation, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
