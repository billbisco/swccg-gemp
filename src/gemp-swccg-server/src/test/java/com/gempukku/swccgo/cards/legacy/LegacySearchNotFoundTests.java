package com.gempukku.swccgo.cards.legacy;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LegacySearchNotFoundResult;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Engine tests for the "search found nothing" result (LegacySearchNotFoundResult): it is reported only when a search
 * to take a card into hand or deploy a card finds nothing, and its retry repeats the same search in another pile.
 * Secret Plans (take Shocking Revelation into hand) and Imperial Arrest Order (deploy a docking bay) make the searches.
 */
public class LegacySearchNotFoundTests {
    private static final String RETRY_TEXT = "Search Force Pile for that card instead";

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("plans", "7_240");
                    put("shocking", "5_153");
                    put("arrest", "8_126");
                    put("bay", "1_291");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Stands in for a card that responds to a failed search: records every result and offers to repeat the search in
     * the Force Pile when it was the Dark Side player's own Reserve Deck.
     */
    private static class SearchNotFoundListener extends AbstractActionProxy {
        private final PhysicalCard _source;
        private final String _player;
        private final List<LegacySearchNotFoundResult> _seen = new LinkedList<>();

        SearchNotFoundListener(PhysicalCard source, String player) {
            _source = source;
            _player = player;
        }

        @Override
        public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult) {
            if (effectResult instanceof LegacySearchNotFoundResult && !_seen.contains(effectResult)) {
                _seen.add((LegacySearchNotFoundResult) effectResult);
            }
            if (playerId.equals(_player)
                    && LegacySearchNotFoundResult.isOwnSearchNotFound(effectResult, playerId, Zone.RESERVE_DECK)) {
                LegacySearchNotFoundResult result = (LegacySearchNotFoundResult) effectResult;
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(_source, _source.getCardId());
                action.setText(RETRY_TEXT);
                action.appendEffect(result.createSearchOfOtherPile(action, Zone.FORCE_PILE, true));
                return Collections.singletonList(action);
            }
            return null;
        }
    }

    /**
     * Both players look at the pile after the failed search, then pass the responses to that verification and to the
     * reshuffle, stopping at the Dark Side player's responses to the failed search itself.
     */
    private static void passVerify(VirtualTableScenario scn) {
        assertTrue(scn.DSDecisionAvailable("Verify"));
        scn.DSPass();
        scn.LSPass();
        for (int i = 0; i < 10; ++i) {
                if (scn.DSDecisionAvailable("LEGACY__SEARCH_NOT_FOUND") || scn.DSDecisionAvailable("Just failed to find")) {
                return;
            }
            if (scn.LSDecisionAvailable("LEGACY__SEARCH_NOT_FOUND")) {
                scn.LSPass();
                continue;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().contains("Optional responses")) {
                return;
            }
            scn.PassResponses();
        }
    }

    @Test
    public void SearchNotFoundIsReportedWhenTakeIntoHandSearchFindsNothing() {
        //test1: the result is reported with the searcher, the pile, the kind of search and the searching card
        //test2: the retry finds the card in the Force Pile and takes it into hand
        var scn = GetScenario();
        var plans = scn.GetDSCard("plans");
        var shocking = scn.GetDSCard("shocking");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(plans);
        var listener = new SearchNotFoundListener(plans, scn.DS);
        scn.ApplyAdHocAction(listener);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSForcePile(shocking);

        scn.DSUseCardAction(plans, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        passVerify(scn);

        assertEquals(1, listener._seen.size()); //test1
        LegacySearchNotFoundResult result = listener._seen.get(0);
        assertEquals(scn.DS, result.getPerformingPlayerId()); //test1
        assertEquals(scn.DS, result.getCardPileOwner()); //test1
        assertEquals(Zone.RESERVE_DECK, result.getCardPile()); //test1
        assertEquals(LegacySearchNotFoundResult.SearchKind.TAKE_INTO_HAND, result.getSearchKind()); //test1
        assertEquals(plans, result.getSearchSource()); //test1

        assertTrue(scn.DSCardActionAvailable(plans, RETRY_TEXT));
        scn.DSUseCardAction(plans, RETRY_TEXT);
        scn.DSChooseCard(shocking);
        scn.PassAllResponses();
        assertInHand(shocking); //test2
    }

    @Test
    public void SearchNotFoundIsNotReportedWhenTakeIntoHandSearchFindsTheCard() {
        //test1: no result when the search finds the card
        //test2: the card is taken into hand normally
        var scn = GetScenario();
        var plans = scn.GetDSCard("plans");
        var shocking = scn.GetDSCard("shocking");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(plans);
        var listener = new SearchNotFoundListener(plans, scn.DS);
        scn.ApplyAdHocAction(listener);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(shocking);

        scn.DSUseCardAction(plans, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.DSChooseCard(shocking);
        scn.PassAllResponses();

        assertInHand(shocking); //test2
        assertTrue(listener._seen.isEmpty()); //test1
    }

    @Test
    public void SearchNotFoundIsReportedWhenDeploySearchFindsNothing() {
        //test1: the result is reported as a deploy search
        //test2: the retry deploys the docking bay from the Force Pile
        var scn = GetScenario();
        var arrest = scn.GetDSCard("arrest");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(arrest);
        var listener = new SearchNotFoundListener(arrest, scn.DS);
        scn.ApplyAdHocAction(listener);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSForcePile(bay);

        scn.DSUseCardAction(arrest, "Deploy docking bay from Reserve Deck");
        scn.PassAllResponses();
        passVerify(scn);

        assertEquals(1, listener._seen.size()); //test1
        assertEquals(LegacySearchNotFoundResult.SearchKind.DEPLOY, listener._seen.get(0).getSearchKind()); //test1
        assertEquals(Zone.RESERVE_DECK, listener._seen.get(0).getCardPile()); //test1

        assertTrue(scn.DSCardActionAvailable(arrest, RETRY_TEXT));
        scn.DSUseCardAction(arrest, RETRY_TEXT);
        scn.DSChooseCard(bay);
        scn.DSChoose("Left");
        scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, bay); //test2
    }

    @Test
    public void SearchNotFoundIsNotReportedWhenDeploySearchFindsTheCard() {
        //test1: no result when the deploy search finds the card
        //test2: the docking bay deploys normally
        var scn = GetScenario();
        var arrest = scn.GetDSCard("arrest");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(arrest);
        var listener = new SearchNotFoundListener(arrest, scn.DS);
        scn.ApplyAdHocAction(listener);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(bay);

        scn.DSUseCardAction(arrest, "Deploy docking bay from Reserve Deck");
        scn.PassAllResponses();
        scn.DSChooseCard(bay);
        scn.DSChoose("Left");
        scn.PassAllResponses();

        assertInZone(Zone.LOCATIONS, bay); //test2
        assertTrue(listener._seen.isEmpty()); //test1
    }

    @Test
    public void SearchNotFoundRetryVerifiesWhenTheOtherPileDoesNotHaveTheCardEither() {
        //test1: a retry that also finds nothing leaves the card where it was
        //test2: the retry reports its own failed search of the Force Pile
        var scn = GetScenario();
        var plans = scn.GetDSCard("plans");
        var shocking = scn.GetDSCard("shocking");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(plans);
        var listener = new SearchNotFoundListener(plans, scn.DS);
        scn.ApplyAdHocAction(listener);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSUsedPile(shocking);

        scn.DSUseCardAction(plans, "Take card into hand from Reserve Deck");
        passVerify(scn);
        scn.DSUseCardAction(plans, RETRY_TEXT);
        passVerify(scn);
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_USED_PILE, shocking.getZone()); //test1
        assertEquals(2, listener._seen.size()); //test2
        assertEquals(Zone.FORCE_PILE, listener._seen.get(1).getCardPile()); //test2
    }
}
