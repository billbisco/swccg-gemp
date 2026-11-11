package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5 Ambush (V), printing 65. */
public class Card1250_065 extends AbstractLostInterrupt {
    public Card1250_065() {
        super(Side.LIGHT, 3, Title.Ambush, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"Well done. Hold them in the security tower, and keep it quiet. Move.\"");
        setGameText("Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if at Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny.");
        addIcons(Icon.CLOUD_CITY, Icon.EPISODE_I);
    }

    /**
     * "free if at Coruscant" (release sheet 2009, repeated by the PC card image 2013-02-25 and the Master List 2013-04-27,
     * the current text; Bill 2026-11-11 08:12/08:45): any Coruscant location, the system included. The PC card list
     * (2250_1065) said "on Coruscant" (sites only). Play change of batch R8-B7.
     */
    protected Filter freeRetrievalLocations() {
        return Filters.Coruscant_location;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        GameTextActionId deployId = GameTextActionId.LEGACY__AMBUSH_V__DEPLOY_CLONES;
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, deployId);
            action.setText("Deploy up to two clones");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DeployCardsFromReserveDeckEffect(action, Filters.clone, 1, 2, true));
                }
            });
            actions.add(action);
        }

        GameTextActionId retrieveId = GameTextActionId.LEGACY__AMBUSH_V__RETRIEVE_INTO_HAND;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveId)) {
            List<PhysicalCard> occupied = new ArrayList<>(Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.clone))));
            List<PhysicalCard> coruscant = occupied.stream()
                    .filter(location -> freeRetrievalLocations().accepts(game.getGameState(), game.getModifiersQuerying(), location))
                    .toList();
            boolean free = hasRelatedTriple(game, coruscant);
            int cost = free ? 0 : 2;
            if ((free || hasRelatedTriple(game, occupied))
                    && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, cost)) {
                PlayInterruptAction action = new PlayInterruptAction(game, self, retrieveId);
                action.setText("Retrieve any one card into hand");
                if (!free) action.appendCost(new UseForceEffect(action, playerId, cost));
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new RetrieveCardIntoHandEffect(action, playerId));
                    }
                });
                actions.add(action);
            }
        }

        if (GameConditions.isDuringBattle(game) && GameConditions.canAddBattleDestinyDraws(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.clone, Filters.participatingInBattle))) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                            new AddsBattleDestinyModifier(self, 1), "Add one battle destiny"));
                }
            });
            actions.add(action);
        }
        return actions;
    }

    private boolean hasRelatedTriple(SwccgGame game, List<PhysicalCard> locations) {
        var state = game.getGameState();
        var modifiers = game.getModifiersQuerying();
        for (int i = 0; i < locations.size(); i++) {
            PhysicalCard first = locations.get(i);
            for (int j = i + 1; j < locations.size(); j++) {
                PhysicalCard second = locations.get(j);
                if (!modifiers.isRelatedLocations(state, first, second)) continue;
                for (int k = j + 1; k < locations.size(); k++) {
                    PhysicalCard third = locations.get(k);
                    if (modifiers.isRelatedLocations(state, first, third)
                            && modifiers.isRelatedLocations(state, second, third)) return true;
                }
            }
        }
        return false;
    }
}
