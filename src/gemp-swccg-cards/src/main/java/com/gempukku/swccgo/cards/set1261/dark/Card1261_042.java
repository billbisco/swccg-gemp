package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyNumCardsDrawnInStartingHandEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardCombinationIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.List;

/** Legacy Block 6.1 Lone Pilot (V). */
public class Card1261_042 extends AbstractUsedOrStartingInterrupt {
    private static final Filter PILOT = Filters.and(Filters.character,
            Filters.or(Filters.Bossk, Filters.Dengar, Filters.DS_61_2, Filters.Guri, Filters.Juno));
    private static final Filter SYSTEM = Filters.and(Filters.system, new Filter() {
        @Override
        public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
            return card.getBlueprint().getIconCount(Icon.DARK_FORCE) == 1;
        }
    });
    private static final Filter EFFECT = Filters.and(Filters.Effect, Filters.alwaysImmuneToCardTitle(Title.Alter));
    private static final String CHOOSE = "Choose Bossk, Dengar, DS-61-2, Guri, or Juno Eclipse "
            + "(may also choose a one-Dark-icon system, matching starship, and an always Alter-immune Effect)";

    public Card1261_042() {
        super(Side.DARK, 4, "Lone Pilot", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("One lone TIE fighter, acting as a scout near the Death Star, suddenly encountered the Millennium Falcon.");
        setGameText("USED: Activate 1 Force. STARTING: Take into hand from Reserve Deck Bossk, Dengar, DS-61-2, Guri, or Juno Eclipse (may also take into hand from Reserve Deck a system with exactly 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect). When drawing your starting hand, draw four cards instead of eight. Place Interrupt in Lost Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.canActivateForce(game, playerId) || game.getGameState().getReserveDeckSize(playerId) == 0)
            return null;

        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setText("Activate 1 Force");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new ActivateForceEffect(action, playerId, 1));
            }
        });
        return List.of(action);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(String playerId, SwccgGame game, PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Take cards into hand from Reserve Deck");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                if (!Filters.canSpot(game.getGameState().getReserveDeck(playerId), game, PILOT)) {
                    // AR: return an unplayable Starting Interrupt to Reserve Deck; keep the normal starting draw.
                    action.appendEffect(new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                    action.appendEffect(new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (game.getGameState().getStartingInterruptPlayed(playerId) == self)
                                game.getGameState().setStartingInterruptPlayed(playerId, null);
                        }
                    });
                    return;
                }

                action.appendEffect(new TakeCardCombinationIntoHandFromReserveDeckEffect(action, playerId, false) {
                    @Override
                    public String getChoiceText(SwccgGame game, Collection<PhysicalCard> selected) {
                        return CHOOSE;
                    }

                    @Override
                    public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> selected) {
                        Collection<PhysicalCard> pilots = Filters.filter(selected, game, PILOT);
                        if (pilots.isEmpty())
                            return PILOT;

                        Filter ship = Filters.matchingStarship(pilots.iterator().next());
                        return Filters.or(unselectedCategory(game, selected, SYSTEM),
                                unselectedCategory(game, selected, ship), unselectedCategory(game, selected, EFFECT));
                    }

                    @Override
                    public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> selected) {
                        Collection<PhysicalCard> pilots = Filters.filter(selected, game, PILOT);
                        if (pilots.size() != 1)
                            return false;

                        int systems = Filters.filter(selected, game, SYSTEM).size();
                        int ships = Filters.filter(selected, game, Filters.matchingStarship(pilots.iterator().next())).size();
                        int effects = Filters.filter(selected, game, EFFECT).size();
                        return systems <= 1 && ships <= 1 && effects <= 1
                                && selected.size() == 1 + systems + ships + effects;
                    }
                });
                action.appendEffect(new ModifyNumCardsDrawnInStartingHandEffect(action, playerId, 4));
                action.appendEffect(new PutCardFromVoidInLostPileEffect(action, playerId, self));
            }
        });
        return action;
    }

    private static Filter unselectedCategory(SwccgGame game, Collection<PhysicalCard> selected, Filter category) {
        return Filters.canSpot(selected, game, category) ? Filters.none : category;
    }
}
