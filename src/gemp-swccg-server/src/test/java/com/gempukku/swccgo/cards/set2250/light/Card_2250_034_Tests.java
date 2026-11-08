package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.*;

/**
 * Original Bail Organa (Virtual Block 5 sheet), a version of 1250_34: retrieves 1 Force after opponent just lost Force
 * to a Force drain by a Rebel (tests moved here from Card_1250_034_Tests when the current card took the Master List text).
 */
public class Card_2250_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("bail", "2250_34"); put("current", "1250_34"); put("leia", "1_17"); put("luke", "1_19"); put("trooper", "1_28");
            put("panaka", "12_2"); put("valorum", "12_30"); put("senator", "12_28"); put("political", "12_40");
            put("senate", "12_75"); put("platform", "1113_3"); put("cantina", "1_128");
            put("retrieve1", "1_28"); put("retrieve2", "1_28");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("senator", "12_110"); put("political", "12_127");
        }}, 25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BailOrganaOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("bail").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_34", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27),
                "Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique (•) Rebels of ability < 4 are power +2, and, if opponent just lost Force to a Force drain by a Rebel, may retrieve 1 Force.");
    }

    @Test
    public void BailOrganaAtSenateRetrievesAfterOpponentActuallyLosesForceToRebelDrain() { checkRetrieval(true, true, true, false); }
    @Test
    public void BailOrganaMayDeclineRetrievalAfterRebelDrain() { checkRetrieval(true, true, false, false); }
    @Test
    public void BailOrganaDoesNotRetrieveAfterDrainByNonRebelRepublicCharacter() { checkRetrieval(true, false, false, false); }
    @Test
    public void BailOrganaAwayFromSenateDoesNotRetrieve() { checkRetrieval(false, true, false, false); }
    @Test
    public void BailOrganaDoesNotOfferRetrievalWithEmptyLostPile() { checkRetrieval(true, true, false, true); }

    private boolean retrievalOffered(VirtualTableScenario scn) {
        return scn.GetADParam(scn.LS, "cardId") != null && scn.GetADParam(scn.LS, "actionText") != null
                && scn.LSCardActionAvailable(scn.GetLSCard("bail"), "Retrieve 1 Force");
    }

    private void checkRetrieval(boolean atSenate, boolean rebel, boolean accept, boolean emptyLost) {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(atSenate ? senate : scn.GetLSStartingLocation(), scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard(rebel ? "trooper" : "panaka"));
        scn.SkipToLSTurn(Phase.CONTROL);
        if (!emptyLost) scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("retrieve1"));
        var target = scn.GetLSCard("retrieve1"); int life = scn.GetLSLifeForceRemaining();
        scn.LSForceDrainAt(scn.GetDSStartingLocation()); scn.DSPass(); scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal()); assertFalse(retrievalOffered(scn));
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        assertEquals(atSenate && rebel && !emptyLost, retrievalOffered(scn));
        if (accept) scn.LSUseCardAction(scn.GetLSCard("bail"), "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(life + (accept ? 1 : 0), scn.GetLSLifeForceRemaining());
        assertEquals(accept, scn.GetLSUsedPile().contains(target));
        if (!emptyLost) assertEquals(!accept, scn.GetLSLostPile().contains(target));
    }
    @Test
    public void BailOrganaRetrievesOnlyOnceForTwoForceRebelDrain() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); var cantina = scn.GetLSCard("cantina");
        scn.MoveLocationToTable(senate); scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(senate, scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(cantina, scn.GetLSCard("trooper")); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("retrieve1"), scn.GetLSCard("retrieve2"));
        int life = scn.GetLSLifeForceRemaining();
        scn.LSForceDrainAt(cantina); scn.DSPass(); scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        assertTrue(retrievalOffered(scn));
        scn.LSUseCardAction(scn.GetLSCard("bail"), "Retrieve 1 Force"); scn.PassAllResponses();
        assertEquals(life + 1, scn.GetLSLifeForceRemaining());
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        assertFalse(retrievalOffered(scn));
        scn.PassAllResponses();
        assertEquals(life + 1, scn.GetLSLifeForceRemaining());
        assertEquals(1, scn.GetLSUsedPile().stream().filter(c -> c == scn.GetLSCard("retrieve1") || c == scn.GetLSCard("retrieve2")).count());
        assertFalse(scn.IsActiveForceDrain());
    }
}
