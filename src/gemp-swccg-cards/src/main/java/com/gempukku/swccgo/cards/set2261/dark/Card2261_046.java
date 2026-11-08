package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Subtype: Used
 * Title: One Beautiful Thing
 * Pre-errata version of 1261_46: the Virtual Block 6.1 release sheet text (Sep 2009), naming Black Leader (the Juno
 * persona). As printed (and on the 2013 Master List) it is a Used Interrupt, and the battle clause counts your cards
 * "there" (at the battle location), not only participating ones as GEMP's current card does.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_046 extends AbstractUsedInterrupt {
    public Card2261_046() {
        super(Side.DARK, 4, "One Beautiful Thing", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("'I don't know how we're gonna get out of this one.'");
        setGameText("Lose 1 Force to ▲ (or retrieve) Black Leader. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0).");
        addIcons(Icon.DAGOBAH, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
        setLegacyVersion(new LegacyVersion("1261_46", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #47", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ONE_BEAUTIFUL_THING__PULL_JUNO;

        if (GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Black Leader into hand from Reserve Deck");
            action.setActionMsg("Take Black Leader into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take Black Leader into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Juno, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve Black Leader");
            action.setActionMsg("Retrieve Black Leader");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.Juno));
                        }
                    }
            );
            actions.add(action);
        }

        // "where you have no Dark Jedi or droids and only two cards with ability there": your cards at the battle location
        if (GameConditions.isDuringBattle(game)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.at(Filters.battleLocation), Filters.or(Filters.Dark_Jedi, Filters.droid)))) {
            Collection<PhysicalCard> cardsWithAbilityThere = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.at(Filters.battleLocation), Filters.hasAbility));
            if (cardsWithAbilityThere.size() == 2
                    && !Filters.filter(cardsWithAbilityThere, game, Filters.male).isEmpty()
                    && !Filters.filter(cardsWithAbilityThere, game, Filters.female).isEmpty()
            ) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Add destiny to power and reset attrition");
                action.setActionMsg("Add one destiny to power and make attrition against you = 0");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                                    action.appendEffect(
                                            new AddDestinyToTotalPowerEffect(action, 1));
                                }
                                action.appendEffect(
                                        new AddUntilEndOfBattleModifierEffect(action,
                                                new ResetAttritionModifier(self, Filters.battleLocation, 0, playerId),
                                                "Make attrition against you = 0")
                                );
                            }
                        }
                );

                actions.add(action);
            }
        }
        return actions;
    }
}
