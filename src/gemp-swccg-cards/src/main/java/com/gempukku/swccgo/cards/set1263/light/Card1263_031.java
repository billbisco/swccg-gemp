package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.ArrayList;
import java.util.List;

/** Final Legacy master list, page 34 (destiny 6; earlier full-card image has 5). */
public class Card1263_031 extends AbstractUsedInterrupt {
    public Card1263_031() {
        super(Side.LIGHT, 6, "It's Not My Fault!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"It's not fair! The transfer circuits are working!\"");
        setGameText("Use 1 Force to take [Virtual Block 6] Phylo Gandish or an Immediate Effect into hand from Reserve Deck; reshuffle. OR Use 2 Force to take an Effect that deploys for free into hand from Reserve Deck; reshuffle. OR If a Dark Jedi on table, retrieve Return Of The Jedi into hand.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        addTakeAction(actions, playerId, game, self, 1, GameTextActionId.LEGACY__ITS_NOT_MY_FAULT__TAKE_PHYLO_OR_IMMEDIATE_EFFECT,
                "Take Phylo Gandish or an Immediate Effect", Filters.or(Filters.and(Icon.LEGACY_BLOCK_6, Filters.title("Phylo Gandish")), Filters.Immediate_Effect));
        addTakeAction(actions, playerId, game, self, 2, GameTextActionId.LEGACY__ITS_NOT_MY_FAULT__TAKE_FREE_EFFECT,
                "Take an Effect that deploys for free", Filters.and(Filters.Effect, Filters.deploysForFree));
        GameTextActionId retrieveId = GameTextActionId.LEGACY__ITS_NOT_MY_FAULT__RETRIEVE_RETURN_OF_THE_JEDI;
        if (GameConditions.canSpot(game, self, Filters.Dark_Jedi)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveId, true)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, retrieveId);
            action.setText("Retrieve Return Of The Jedi into hand");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new RetrieveCardIntoHandEffect(action, playerId, Filters.title(Title.Return_Of_The_Jedi)));
                }
            });
            actions.add(action);
        }
        return actions;
    }

    private void addTakeAction(List<PlayInterruptAction> actions, String playerId, SwccgGame game, PhysicalCard self,
                               int force, GameTextActionId id, String text, Filter filter) {
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, force)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, id);
            action.setText(text);
            action.appendCost(new UseForceEffect(action, playerId, force));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, filter, true));
                }
            });
            actions.add(action);
        }
    }
}
