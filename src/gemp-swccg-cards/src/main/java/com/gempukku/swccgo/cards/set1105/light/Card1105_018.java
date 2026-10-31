package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandEqualToOrFewerThanCondition;
import com.gempukku.swccgo.cards.effects.PeekAtCardEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotLookAtOpponentsHandModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotRemoveCardsFromOpponentsHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Effect
 * Title: Scrambled Transmission
 */
public class Card1105_018 extends AbstractNormalEffect {
    public Card1105_018() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Scrambled_Transmission, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Rebel Alliance employs sophisticated jamming technology to block Imperial communications.");
        setGameText("Deploy on table. Unless you have 13 or more cards in hand, opponent may not peek at or remove those cards (except with Monnok). Once per turn, may take Logistical Delay into hand from Reserve Deck; reshuffle, peek at a card placed face down by You're A Slave?, or lose 2 Force to cancel a Political Effect. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotRemoveCardsFromOpponentsHandModifier(self, opponent, new CardsInHandEqualToOrFewerThanCondition(playerId, 12), Filters.except(Filters.Monnok)));
        modifiers.add(new MayNotLookAtOpponentsHandModifier(self, opponent, new CardsInHandEqualToOrFewerThanCondition(playerId, 12), Filters.except(Filters.Monnok)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OVS__SCRAMBLED_TRANSMISSION__ONCE_PER_TURN;

        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            return actions;
        }

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction upload = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            upload.setText("Take Logistical Delay into hand from Reserve Deck");
            upload.appendUsage(
                    new OncePerTurnEffect(upload));
            upload.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(upload, playerId, Filters.Logistical_Delay, true));
            actions.add(upload);
        }

        PhysicalCard youreASlave = Filters.findFirstActive(game, self, Filters.Youre_A_Slave);
        if (youreASlave != null && GameConditions.hasStackedCards(game, youreASlave, Filters.face_down)) {
            final List<PhysicalCard> stacked = new LinkedList<PhysicalCard>(Filters.filterStacked(game, Filters.and(Filters.face_down, Filters.stackedOn(youreASlave))));
            if (!stacked.isEmpty()) {
                final TopLevelGameTextAction peek = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                peek.setText("Peek at a card placed face-down by You're A Slave?");
                peek.appendUsage(
                        new OncePerTurnEffect(peek));
                peek.appendEffect(
                        new PeekAtCardEffect(peek, playerId, stacked.get(0)));
                actions.add(peek);
            }
        }

        Filter politicalEffect = Filters.Political_Effect;
        if (GameConditions.canSpot(game, self, politicalEffect)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTargetToCancel(game, self, politicalEffect)) {
            final TopLevelGameTextAction cancel = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            cancel.setText("Lose 2 Force to cancel a Political Effect");
            cancel.appendUsage(
                    new OncePerTurnEffect(cancel));
            cancel.appendCost(
                    new LoseForceEffect(cancel, playerId, 2, true));
            cancel.appendTargeting(
                    new TargetCardOnTableEffect(cancel, playerId, "Choose Political Effect to cancel", politicalEffect) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            cancel.allowResponses("Cancel " + targetedCard.getTitle(),
                                    new UnrespondableEffect(cancel) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = cancel.getPrimaryTargetCard(targetGroupId);
                                            cancel.appendEffect(
                                                    new CancelCardOnTableEffect(cancel, finalTarget));
                                        }
                                    });
                        }
                    });
            actions.add(cancel);
        }

        return actions;
    }
}
