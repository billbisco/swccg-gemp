package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ResetAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Used
 * Title: One Beautiful Thing
 * Virtual Cards Master List text (2013-04-27): a Used Interrupt (as printed on the 6.1 slip and the card image) whose
 * battle clause counts your cards with ability "there" (at the battle location), not only participating ones.
 */
public class Card601_092 extends AbstractUsedInterrupt {
    public Card601_092() {
        super(Side.DARK, 4, "One Beautiful Thing", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("'I don't know how we're gonna get out of this one.'");
        setGameText("Lose 1 Force to ▲ (or retrieve) Juno Eclipse. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0).");
        addIcons(Icon.DAGOBAH, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    /** The Juno persona as the text names it (Black Leader before the 2013 persona change). */
    protected String junoName() {
        return "Juno Eclipse";
    }

    /** The PC card list text (PC-E1) counts "participating cards with ability"; the printed text counts cards "there". */
    protected boolean countsOnlyParticipatingCards() {
        return false;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ONE_BEAUTIFUL_THING__PULL_JUNO;
        String juno = junoName();

        if (GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take " + juno + " into hand from Reserve Deck");
            action.setActionMsg("Take " + juno + " into hand from Reserve Deck");
            action.appendCost(new LoseForceEffect(action, playerId, 1));
            action.allowResponses("Take " + juno + " into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Juno, true));
                        }
                    });
            actions.add(action);
        }

        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve " + juno);
            action.setActionMsg("Retrieve " + juno);
            action.appendCost(new LoseForceEffect(action, playerId, 1));
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new RetrieveCardEffect(action, playerId, Filters.Juno));
                        }
                    });
            actions.add(action);
        }

        // "where you have no Dark Jedi or droids and only two cards with ability there": your cards at the battle
        // location (PC-E1: participating cards only).
        Filter where = countsOnlyParticipatingCards() ? Filters.participatingInBattle : Filters.at(Filters.battleLocation);
        if (GameConditions.isDuringBattle(game)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), where, Filters.or(Filters.Dark_Jedi, Filters.droid)))) {
            Collection<PhysicalCard> cardsWithAbility = Filters.filterActive(game, self, Filters.and(Filters.your(self), where, Filters.hasAbility));
            if (cardsWithAbility.size() == 2
                    && !Filters.filter(cardsWithAbility, game, Filters.male).isEmpty()
                    && !Filters.filter(cardsWithAbility, game, Filters.female).isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Add destiny to power and reset attrition");
                action.setActionMsg("Add one destiny to power and make attrition against you = 0");
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                                    action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1));
                                }
                                action.appendEffect(
                                        new AddUntilEndOfBattleModifierEffect(action,
                                                new ResetAttritionModifier(self, Filters.battleLocation, 0, playerId),
                                                "Make attrition against you = 0"));
                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }
}
