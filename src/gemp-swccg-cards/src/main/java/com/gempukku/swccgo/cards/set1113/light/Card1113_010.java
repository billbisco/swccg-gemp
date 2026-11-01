package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtCardEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Interrupt
 * Subtype: Used
 * Title: Perimeter Scan
 */
public class Card1113_010 extends AbstractUsedInterrupt {
    public Card1113_010() {
        super(Side.LIGHT, 4, Title.Perimeter_Scan, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLore("'It's a good bet the Empire knows we're here.'");
        setGameText("If two related unique (•) sites are on table, deploy the related system from Reserve Deck; reshuffle. OR Peek at a face down card on table. OR If a character or starship's power was just reduced, reset that card's power to its printed power for remainder of turn.");
        addIcons(Icon.HOTH);
    }

    private boolean twoRelatedUniqueSites(SwccgGame game, PhysicalCard self) {
        Collection<PhysicalCard> uniqueSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.site, Filters.unique));
        for (PhysicalCard site : uniqueSites) {
            if (Filters.canSpot(game, self, Filters.and(Filters.site, Filters.unique, Filters.other(site), Filters.relatedSite(site)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Systems related to two unique sites on table. Filters.relatedSystemTo requires both locations in Zone.LOCATIONS,
     * so it cannot match a system still in Reserve Deck.
     */
    private Filter relatedSystemOfTwoUniqueSites(final PhysicalCard self) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.system.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                SwccgGame game = gameState.getGame();
                Collection<PhysicalCard> uniqueSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.site, Filters.unique));
                for (PhysicalCard site : uniqueSites) {
                    if (physicalCard.getTitle() != null
                            && physicalCard.getTitle().equals(site.getPartOfSystem())
                            && Filters.canSpot(game, self, Filters.and(Filters.site, Filters.unique, Filters.other(site), Filters.relatedSite(site)))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.cardStatModifiedOrReset(game, effectResult)
                && effectResult instanceof ResetOrModifyCardAttributeResult) {
            final PhysicalCard card = ((ResetOrModifyCardAttributeResult) effectResult).getCard();
            if (card != null && Filters.and(Filters.or(Filters.character, Filters.starship)).accepts(game, card)
                    && card.getBlueprint().hasPowerAttribute()) {
                Float printed = card.getBlueprint().getPower();
                if (printed != null && game.getModifiersQuerying().getPower(game.getGameState(), card) < printed) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Reset power to printed");
                    action.allowResponses("Reset " + GameUtils.getCardLink(card) + "'s power to printed power",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    Float printedPower = card.getBlueprint().getPower();
                                    if (printedPower != null) {
                                        action.appendEffect(
                                                new ResetPowerUntilEndOfTurnEffect(action, card, printedPower));
                                    }
                                }
                            }
                    );
                    return java.util.Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId deployId = GameTextActionId.LEGACY__PERIMETER_SCAN_V__DEPLOY_SYSTEM;
        if (twoRelatedUniqueSites(game, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, deployId);
            action.setText("Deploy related system from Reserve Deck");
            action.allowResponses("Deploy the related system from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, relatedSystemOfTwoUniqueSites(self), true));
                        }
                    }
            );
            actions.add(action);
        }

        final List<PhysicalCard> cardsFaceDown = new LinkedList<PhysicalCard>();
        cardsFaceDown.addAll(Filters.filterAllOnTable(game, Filters.and(Filters.face_down, Filters.at(Filters.location))));
        cardsFaceDown.addAll(Filters.filterStacked(game, Filters.and(Filters.face_down, Filters.stackedOn(self, Filters.locationAndCardsAtLocation(Filters.location)))));
        if (!cardsFaceDown.isEmpty()) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at face-down card");
            action.appendTargeting(
                    new ChooseCardEffect(action, playerId, "Choose face-down card", cardsFaceDown) {
                        @Override
                        protected void cardSelected(final PhysicalCard selectedCard) {
                            action.addAnimationGroup(selectedCard);
                            action.allowResponses("Peek at a face down card on table",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new PeekAtCardEffect(action, playerId, selectedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
