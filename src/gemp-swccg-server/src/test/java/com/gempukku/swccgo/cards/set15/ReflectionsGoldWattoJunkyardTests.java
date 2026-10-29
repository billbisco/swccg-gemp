package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class ReflectionsGoldWattoJunkyardTests {
    private static final String DOWNLOAD = "Deploy Watto from Reserve Deck";

    private VirtualTableScenario scenario() {
        var dark = new HashMap<String, String>();
        dark.put("junkyard", "12_178");
        dark.put("watto", "15_168");
        dark.put("originalWatto", "11_65");
        dark.put("trooper", "1_194");
        var scn = new VirtualTableScenario(new HashMap<>(), dark);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("junkyard"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("watto"), scn.GetDSCard("originalWatto"));
        scn.MoveCardsToDSHand(scn.GetDSCard("trooper"));
        scn.EnsureDSForcePile(6);
        return scn;
    }

    private void assertDownloadNotOffered(VirtualTableScenario scn) {
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertNotNull(scn.DSGetDecision());
        assertTrue("An independent legal action proves the deploy decision is active",
                scn.DSCardPlayAvailable(scn.GetDSCard("trooper")));
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("junkyard"), DOWNLOAD));
    }

    @Test
    public void WattosJunkyardDownloadsReflectionsGoldWattoWhenPersonaIsAvailable() {
        var scn = scenario();
        var watto = scn.GetDSCard("watto");
        var junkyard = scn.GetDSCard("junkyard");
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(watto, scn.GetDSCard("originalWatto"));
        assertTrue(scn.DSCardActionAvailable(junkyard, DOWNLOAD));
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(junkyard, DOWNLOAD);
        scn.DSChooseCard(watto);
        scn.PassAllResponses();
        assertAtLocation(junkyard, watto);
        assertEquals("Junkyard's -2 deploy modifier applies to the Watto persona", force, scn.GetDSForcePileCount());
    }

    @Test
    public void WattosJunkyardDoesNotOfferDownloadWhenReflectionsGoldWattoIsInPlay() {
        var scn = scenario();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("watto"));
        assertDownloadNotOffered(scn);
    }

    @Test
    public void WattosJunkyardDoesNotOfferDownloadWhenReflectionsGoldWattoIsOutOfPlay() {
        var scn = scenario();
        scn.MoveOutOfPlay(scn.GetDSCard("watto"));
        assertDownloadNotOffered(scn);
    }
}
