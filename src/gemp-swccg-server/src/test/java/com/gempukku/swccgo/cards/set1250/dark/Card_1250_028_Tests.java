package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_028_Tests {
    private static final String DEPLOY = "Deploy Sil Unch from Reserve Deck";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(),
                new HashMap<>(Map.of("tactics", "1250_28", "sil", "14_88", "nute", "14_81", "trooper", "1_194")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void TradeFederationTacticsPrintedErrataIsUsedAndLegacy() {
        var scn = scenario();
        var b = scn.GetDSCard("tactics").getBlueprint();
        assertEquals("Trade Federation Tactics", b.getTitle());
        assertEquals("Organizations as wealthy as the Trade Federation can afford large amounts of military hardware, all purchased under the guise of protecting their commercial interests.", b.getLore());
        assertEquals(6, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.USED, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.THEED_PALACE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void TradeFederationTacticsActuallyDeploysSilUnchForNormalCostAndReshuffles() {
        var scn = scenario();
        var tactics = scn.GetDSCard("tactics");
        var sil = scn.GetDSCard("sil");
        var nute = scn.GetDSCard("nute");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(tactics);
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(tactics, DEPLOY));
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToTopOfDSReserveDeck(sil, nute);
        scn.SkipToPhase(Phase.DEPLOY);
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        remaining.remove(sil);
        scn.gameState().getRandom().setSeed(4096);
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(tactics, DEPLOY);
        boolean selected = false;
        for (int i = 0; i < 50 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(sil)) {
                int nuteIndex = scn.DSGetBPChoices().indexOf(nute.getBlueprintId(true));
                assertTrue(nuteIndex >= 0);
                assertEquals("false", scn.GetADParam(scn.DS, "selectable")[nuteIndex]);
                scn.DSChooseCard(sil);
                selected = true;
            } else if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected Sil Unch deploy decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(selected);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertSame(site, sil.getAtLocation());
        assertInZone(Zone.USED_PILE, tactics);
        assertEquals(force - (int) (float) sil.getBlueprint().getDeployCost(), scn.GetDSForcePileCount());
        assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
        assertNotEquals(remaining, scn.GetDSReserveDeck());
    }
}
