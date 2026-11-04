package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("motivator", "1274_17");
                    put("r2x2", "1_24");
                    put("r2d2", "2_14");
                    put("artoothreepio", "10_2");
                    put("push", "2_41");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    @Test
    public void MotivatorStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("motivator").getBlueprint();

        assertEquals("Motivator", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MotivatorDeploysOnlyOnYourNonReflectionsIIAstromech() {
        var scn = GetScenario();
        var motivator = scn.GetLSCard("motivator");
        var r2x2 = scn.GetLSCard("r2x2");
        var artooThreepio = scn.GetLSCard("artoothreepio");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2x2, artooThreepio, luke);
        scn.MoveCardsToHand(motivator);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(motivator));
        scn.LSDeployCard(motivator);
        assertTrue(scn.LSHasCardChoiceAvailable(r2x2));
        assertFalse(scn.LSHasCardChoiceAvailable(artooThreepio));
        assertFalse(scn.LSHasCardChoiceAvailable(luke));
        scn.LSChooseCard(r2x2);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(r2x2, motivator));
    }

    @Test
    public void MotivatorDroidIsPowerPlusTwoAndGainsPresenceIcon() {
        var scn = GetScenario();
        var motivator = scn.GetLSCard("motivator");
        var r2x2 = scn.GetLSCard("r2x2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2x2);
        assertEquals(1, scn.GetPower(r2x2), scn.epsilon);
        assertEquals(0, scn.GetIconCount(r2x2, Icon.PRESENCE));
        scn.AttachCardsTo(r2x2, motivator);
        assertEquals(3, scn.GetPower(r2x2), scn.epsilon);
        assertEquals(1, scn.GetIconCount(r2x2, Icon.PRESENCE));
    }

    @Test
    public void MotivatorDroidPresentAtSiteDrawsBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var motivator = scn.GetLSCard("motivator");
        var r2x2 = scn.GetLSCard("r2x2");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2x2, trooper);
        scn.AttachCardsTo(r2x2, motivator);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.SkipToPowerSegment();

        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        assertEquals(1, scn.GetLSBattleDestinyLimit());
    }

    @Test
    public void MotivatorPlacesInLostPileDuringMovePhaseToTakeWhatreYouTryinToPushOnUsIntoHand() {
        var scn = GetScenario();
        var motivator = scn.GetLSCard("motivator");
        var r2x2 = scn.GetLSCard("r2x2");
        var push = scn.GetLSCard("push");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2x2);
        scn.AttachCardsTo(r2x2, motivator);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(push, luke);
        assertFalse(scn.LSCardActionAvailable(motivator, "Place in Lost Pile to take card into hand"));

        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertTrue(scn.LSCardActionAvailable(motivator, "Place in Lost Pile to take card into hand"));
        scn.LSUseCardAction(motivator, "Place in Lost Pile to take card into hand");
        scn.PassAllResponses();
        // What're You Tryin' To Push On Us? and R2-D2 are selectable; Luke is not
        assertEquals(2, scn.LSGetSelectableCount());
        scn.LSChooseCard(push);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, motivator);
        assertInZone(Zone.HAND, push);
    }

    @Test
    public void MotivatorMayTakeR2D2IntoHandDuringOpponentsMovePhase() {
        var scn = GetScenario();
        var motivator = scn.GetLSCard("motivator");
        var r2x2 = scn.GetLSCard("r2x2");
        var r2d2 = scn.GetLSCard("r2d2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2x2);
        scn.AttachCardsTo(r2x2, motivator);
        scn.SkipToDSTurn(Phase.MOVE);
        scn.MoveCardsToTopOfOwnReserveDeck(r2d2);
        assertTrue(scn.AwaitingDSMovePhaseActions());
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(motivator, "Place in Lost Pile to take card into hand"));
        scn.LSUseCardAction(motivator, "Place in Lost Pile to take card into hand");
        scn.PassAllResponses();
        scn.LSChooseCard(r2d2);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, r2d2);
    }
}
