package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardCombinationIntoHandFromReserveDeckEffect;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Any Methods Necessary (STARTING: "Take into hand from Reserve Deck one prison and one bounty hunter (may also take a matching
 * weapon and/or starship)"). The test rig can't play starting interrupts, so the card's combination selection is run as an ad-hoc
 * effect with the card's own selection rules (copied from Card109_007).
 */
public class Card_109_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                }},
                new HashMap<>()
                {{
                    put("tower", "5_172");     // Cloud City: Security Tower (prison)
                    put("boba", "5_091");      // Boba Fett (bounty hunter)
                    put("lom", "109_006");     // 4-LOM With Concussion Rifle (bounty hunter)
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /** The combination selection of Any Methods Necessary (same rules as Card109_007). */
    private static TopLevelGameTextAction anyMethodsNecessarySelection(PhysicalCard source, String playerId) {
        var action = new TopLevelGameTextAction(source, playerId, source.getCardId());
        action.appendEffect(
                new TakeCardCombinationIntoHandFromReserveDeckEffect(action, playerId, false) {
                    @Override
                    public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                        return "Choose a prison and a bounty hunter (may also choose a matching weapon and/or starship)";
                    }

                    @Override
                    public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                        Filter filter = Filters.none;
                        if (Filters.filterCount(cardsSelected, game, 1, Filters.prison).isEmpty()) {
                            filter = Filters.or(filter, Filters.prison);
                        }
                        if (Filters.filterCount(cardsSelected, game, 1, Filters.bounty_hunter).isEmpty()) {
                            filter = Filters.or(filter, Filters.bounty_hunter);
                        }
                        else {
                            PhysicalCard bountyHunter = Filters.filterCount(cardsSelected, game, 1, Filters.bounty_hunter).iterator().next();
                            Filter matchingWeapon = Filters.matchingWeaponForCharacter(bountyHunter);
                            Filter matchingStarship = Filters.matchingStarship(bountyHunter);
                            if (Filters.filterCount(cardsSelected, game, 1, matchingWeapon).isEmpty()) {
                                filter = Filters.or(filter, matchingWeapon);
                            }
                            if (Filters.filterCount(cardsSelected, game, 1, matchingStarship).isEmpty()) {
                                filter = Filters.or(filter, matchingStarship);
                            }
                        }
                        return filter;
                    }

                    @Override
                    public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                        return Filters.filter(cardsSelected, game, Filters.prison).size() == 1
                                && Filters.filter(cardsSelected, game, Filters.bounty_hunter).size() == 1
                                && cardsSelected.size() == 2;
                    }
                });
        return action;
    }

    @Test
    public void AnyMethodsNecessaryRejectsReplacingThePrisonWithTwoBountyHunters() {
        //Previously accepted: {prison} -> {bounty hunter, bounty hunter} made the combination invalid for good (endless re-ask)
        var scn = GetScenario();
        var tower = scn.GetDSCard("tower");
        var boba = scn.GetDSCard("boba");
        var lom = scn.GetDSCard("lom");

        scn.StartGame();
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(tower, boba, lom);

        //source of the ad-hoc action: any DS card (Any Methods Necessary itself would be offered as starting interrupt at setup)
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, anyMethodsNecessarySelection(scn.GetDSStartingLocation(), scn.DS));
        assertTrue(scn.DSDecisionAvailable("Choose a prison and a bounty hunter"));
        scn.DSChooseCards(tower);

        //two bounty hunters (the prison dropped) is not a valid partial combination: rejected, the same decision is asked again
        assertThrows(RuntimeException.class, () -> scn.DSChooseCards(boba, lom));
        assertTrue(scn.DSDecisionAvailable("Choose a prison and a bounty hunter"));

        scn.DSChooseCards(tower, boba);
        scn.PassAllResponses();

        assertEquals(Zone.HAND, tower.getZone());
        assertEquals(Zone.HAND, boba.getZone());
        assertTrue(lom.getZone() != Zone.HAND);
    }
}
