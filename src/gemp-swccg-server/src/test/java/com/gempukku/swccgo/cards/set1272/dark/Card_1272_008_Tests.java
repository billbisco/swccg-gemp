package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.common.Zone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("collab", "1272_8");
                    put("evax", "1240_12");
                    put("janus", "1240_7");
                    put("aloo", "1230_18");
                    put("yularen", "1240_4");
                    put("commander", "1272_2");
                    put("bewil", "1220_4");
                    put("room", "1240_51");
                    put("office", "1240_53");
                    put("site", "1_291");
                    put("tatooine", "1_289");
                    put("trooper", "1_194");
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

    @Test
    public void DarkCollaborationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("collab").getBlueprint();

        assertEquals("Dark Collaboration", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DarkCollaborationTakesCouncilMemberIntoHand() {
        var scn = GetScenario();
        var collab = scn.GetDSCard("collab");
        var evax = scn.GetDSCard("evax");

        scn.StartGame();
        scn.MoveCardsToHand(collab);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(1);
        scn.MoveCardsToTopOfDSReserveDeck(evax);
        assertTrue(scn.DSCardActionAvailable(collab, "Take Council Member"));
        scn.DSUseCardAction(collab, "Take Council Member");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue("expected evax in Reserve search, decision=" + scn.DSGetDecision(),
                scn.DSHasCardChoiceAvailable(evax));
        scn.DSChooseCard(evax);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, evax.getZone());
    }

    @Test
    public void DarkCollaborationTakesJanusIntoHand() {
        var scn = GetScenario();
        var collab = scn.GetDSCard("collab");
        var janus = scn.GetDSCard("janus");

        scn.StartGame();
        scn.MoveCardsToHand(collab);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(1);
        scn.MoveCardsToTopOfDSReserveDeck(janus);
        assertTrue(scn.DSCardActionAvailable(collab, "Take Council Member"));
        scn.DSUseCardAction(collab, "Take Council Member");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue("expected Janus in Reserve search, decision=" + scn.DSGetDecision(),
                scn.DSHasCardChoiceAvailable(janus));
        scn.DSChooseCard(janus);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, janus.getZone());
    }

    @Test
    public void DarkCollaborationTakesBewilIntoHand() {
        var scn = GetScenario();
        var collab = scn.GetDSCard("collab");
        var bewil = scn.GetDSCard("bewil");

        scn.StartGame();
        scn.MoveCardsToHand(collab);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(1);
        scn.MoveCardsToTopOfDSReserveDeck(bewil);
        assertTrue(scn.DSCardActionAvailable(collab, "Take Council Member"));
        scn.DSUseCardAction(collab, "Take Council Member");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue("expected Bewil in Reserve search, decision=" + scn.DSGetDecision(),
                scn.DSHasCardChoiceAvailable(bewil));
        scn.DSChooseCard(bewil);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, bewil.getZone());
    }

    @Test
    public void DarkCollaborationDeploysIsbSiteFromReserveDeck() {
        var scn = GetScenario();
        var collab = scn.GetDSCard("collab");
        var office = scn.GetDSCard("office");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToHand(collab);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(1);
        scn.MoveCardsToTopOfDSReserveDeck(office);
        assertTrue(scn.DSCardActionAvailable(collab, "Deploy an ISB site"));
        scn.DSUseCardAction(collab, "Deploy an ISB site");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue("expected office in Reserve search, decision=" + scn.DSGetDecision(),
                scn.DSHasCardChoiceAvailable(office));
        scn.DSChooseCard(office);
        assertTrue("expected side-of-site picker, text="
                        + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText()),
                scn.DSDecisionAvailable("On which side"));
        scn.DSChoose("Left");
        scn.PassAllResponses();
        assertTrue(office.getZone() == Zone.LOCATIONS || office.getZone() == Zone.AT_LOCATION);
    }

    @Test
    public void DarkCollaborationAddsInfluenceToTotalPowerCappedAtSix() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var collab = scn.GetDSCard("collab");
        var evax = scn.GetDSCard("evax");
        var aloo = scn.GetDSCard("aloo");
        var yularen = scn.GetDSCard("yularen");
        var commander = scn.GetDSCard("commander");
        var room = scn.GetDSCard("room");
        var site = scn.GetDSCard("site");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(room, evax, aloo, yularen, commander);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToHand(collab);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.InfluenceModifier(aloo, 3));
        assertTrue(scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room) > 6);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        int powerBefore = scn.GetDSTotalPower();
        assertTrue(scn.DSCardActionAvailable(collab, "Add influence to total power"));
        scn.DSUseCardAction(collab, "Add influence to total power");
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();
        assertEquals(powerBefore + 6, scn.GetDSTotalPower());
    }
}
