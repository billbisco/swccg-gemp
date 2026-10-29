package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.cards.set6.light.Card6_063;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections Gold
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Corellian Retort & Fly Casual
 */
public class Card15_072 extends AbstractUsedOrLostInterrupt {
    public Card15_072() {
        super(Side.LIGHT, 5, "Corellian Retort & Fly Casual", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");
        setCredits(0);
        addComboCardTitles("Corellian Retort","Fly Casual");
        setGameText("USED: During your deploy phase, deploy one starship (deploy -1) and/or one pilot to a system even without presence or Force icons. LOST: Take one Tydirium or Tantive IV into hand from Reserve Deck; reshuffle. OR If a bounty hunter (or gangster) and Han are involved in the same battle, you may add one battle destiny (add two if gangster is Jabba).");
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        final DeploymentRestrictionsOption deploymentRestrictionsOption = DeploymentRestrictionsOption.evenWithoutPresenceOrForceIcons();
        final Filter starshipOrPilotFilter = Filters.or(Filters.and(Filters.starship, Filters.deployableToLocation(self, Filters.system, false, false, -1, null, deploymentRestrictionsOption, null)),
                Filters.and(Filters.pilot, Filters.deployableToLocation(self, Filters.system, false, false, 0, null, deploymentRestrictionsOption, null)));
        if (GameConditions.isDuringYourPhase(game,self,Phase.DEPLOY) && GameConditions.hasInHand(game, playerId, starshipOrPilotFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Deploy a starship or pilot from hand");
            // Allow response(s)
            action.allowResponses("Deploy a starship or pilot to a system even without presence or Force icons",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseCardFromHandEffect(action, playerId, starshipOrPilotFilter, true) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                            PlayCardAction playCardAction = selectedCard.getBlueprint().getPlayCardAction(playerId, game, selectedCard, self, false,
                                                    Filters.starship.accepts(game, selectedCard) ? -1 : 0, null, deploymentRestrictionsOption, null, null, null, false, 0, Filters.locationAndCardsAtLocation(Filters.system), null);
                                            if (playCardAction != null) {
                                                action.appendEffect(
                                                        new StackActionEffect(action, playCardAction));
                                            }
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.FLY_CASUAL__UPLOAD_TYDIRIUM_OR_TANTIVE_IV;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a Tydirium or Tantive IV into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Tydirium, Filters.Tantive_IV), true));
                        }
                    }
            );
            actions.add(action);
        }

        var retort = RETORT.actions(playerId,game,self);
        if(retort!=null)for(var action:retort)if(action.getPlayedAsSubtype()==CardSubtype.LOST)actions.add(action);
        return actions;
    }
    private static final RetortActions RETORT=new RetortActions();
    private static class RetortActions extends Card6_063 {
        List<PlayInterruptAction> actions(String playerId,SwccgGame game,PhysicalCard self) {return super.getGameTextTopLevelActions(playerId,game,self);}
    }
}
