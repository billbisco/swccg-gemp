package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Look Sir, Droids
 */
public class Card1210_064 extends AbstractUsedOrStartingInterrupt {
    public Card1210_064() {
        super(Side.DARK, 3, Title.Look_Sir_Droids, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Trooper 1124, Davin Felth, discovered droid plating dropped by R2- D2 and C-3PO when exiting escape pod.");
        setGameText("USED: Take into hand from Reserve Deck a non-[Droid with Presence], non-[Permanent Weapon] droid. OR Place a card from hand on Used Pile. STARTING: Deploy from Reserve Deck two Effects which are always immune to Alter. If you have only one location (with < 3 [Dark Side Force]) on table, may deploy from Reserve Deck Central Office or Mos Eisley. Place this interrupt in hand.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LOOK_SIR_DROIDS_V__UPLOAD_DROID;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take droid into hand from Reserve Deck");
            action.setActionMsg("Take a non-[Droid with Presence], non-[Permanent Weapon] droid into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.droid, Filters.not(Icon.PRESENCE), Filters.not(Icon.PERMANENT_WEAPON)), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, Filters.not(self))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place a card from hand on Used Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromHandOnUsedPileEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy Effects from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploy two Effects that are always immune to Alter from Reserve Deck. If you have only one location (with < 3 Dark Force) on table, may deploy Central Office or Mos Eisley.",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 2, 2, true, false));
                        action.appendEffect(new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                Collection<PhysicalCard> myLocations = Filters.filterTopLocationsOnTable(game, Filters.your(self));
                                if (myLocations.size() == 1) {
                                    PhysicalCard location = myLocations.iterator().next();
                                    if (game.getModifiersQuerying().getIconCount(game.getGameState(), location, Icon.DARK_FORCE) < 3) {
                                        action.appendEffect(
                                                new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.title("Coruscant: ISB Central Office"), Filters.Mos_Eisley), true, true));
                                    }
                                }
                            }
                        });
                        action.appendEffect(
                                new TakeCardFromVoidIntoHandEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}
