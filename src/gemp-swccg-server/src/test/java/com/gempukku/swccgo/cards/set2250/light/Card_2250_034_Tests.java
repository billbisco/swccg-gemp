package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Bail Organa PC-E1 (Master List 2013), a version of 1250_34: retrieves after you Force drain with a Rebel. */
public class Card_2250_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("bail", "2250_34"); put("current", "1250_34"); put("trooper", "1_28"); put("panaka", "12_2");
            put("senate", "12_75"); put("retrieve1", "1_28");
        }}, new HashMap<>() {{
            put("vader", "1_168");
        }}, 25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BailOrganaPCE1Identity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("bail").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_34", "PC-E1",
                LocalDate.of(2013, 4, 27), null,
                "Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique (•) Rebels of ability < 4 are power +2, and, if you just Force drained with a Rebel, may retrieve 1 Force.");
    }

    private boolean retrievalOffered(VirtualTableScenario scn) {
        return scn.GetADParam(scn.LS, "cardId") != null && scn.GetADParam(scn.LS, "actionText") != null
                && scn.LSCardActionAvailable(scn.GetLSCard("bail"), "Retrieve 1 Force");
    }

    private void checkRetrieval(boolean rebel) {
        var scn = scenario();
        scn.StartGame();
        var senate = scn.GetLSCard("senate");
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard(rebel ? "trooper" : "panaka"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("retrieve1"));
        int life = scn.GetLSLifeForceRemaining();
        scn.LSForceDrainAt(scn.GetDSStartingLocation());
        scn.DSPass();
        scn.PassForceDrainStartResponses();
        assertFalse(retrievalOffered(scn));
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        boolean offered = false;
        for (int i = 0; i < 12 && scn.IsActiveForceDrain() || i < 2; i++) {
            if (retrievalOffered(scn)) {
                offered = true;
                break;
            }
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable()) scn.DSPass();
        }
        assertEquals(rebel, offered);
        if (offered) {
            scn.LSUseCardAction(scn.GetLSCard("bail"), "Retrieve 1 Force");
            scn.PassAllResponses();
            assertEquals(life + 1, scn.GetLSLifeForceRemaining());
            assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("retrieve1")));
        }
    }

    @Test
    public void BailOrganaPCE1RetrievesAfterYouForceDrainWithARebel() {
        checkRetrieval(true);
    }

    @Test
    public void BailOrganaPCE1DoesNotRetrieveAfterDrainWithoutARebel() {
        checkRetrieval(false);
    }
}
