package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_067_Tests {
    private VirtualTableScenario fixture(boolean darkStartsPrize) {
        var light = new HashMap<>(Map.of("maintenance", "13_21", "regularFalcon", "1_143",
                "space", "6_87", "effect", "13_5"));
        StartingSetup darkStart = darkStartsPrize ? new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("cct", "7_296", "chamber", "5_166", "console", "5_107",
                        "tower", "5_172", "darkPrize", "10_42"));
            }
            @Override public void Setup(VirtualTableScenario scn) {
                for (int i = 0; i < 20 && scn.GetCurrentDecision() != null; i++) {
                    var prompt = scn.GetCurrentDecision().getText();
                    if (prompt.contains("On which side of")) scn.DSChooseOption("Left");
                    else if (prompt.contains("Choose Carbonite Chamber Console")) scn.DSChooseCard(scn.GetDSCard("console"));
                    else if (prompt.contains("Choose Carbonite Chamber to deploy")) scn.DSChooseCard(scn.GetDSCard("chamber"));
                    else if (prompt.contains("Choose Security Tower to deploy")) scn.DSChooseCard(scn.GetDSCard("tower"));
                    else if (prompt.contains("Choose Jabba's Prize to deploy")) scn.DSChooseCard(scn.GetDSCard("darkPrize"));
                    else if (prompt.startsWith("Verify ") || prompt.toLowerCase().contains("optional response")) {
                        scn.PlayerPass(scn.GetDecidingPlayer());
                    } else break;
                }
            }
        } : StartingSetup.DefaultDSGroundLocation;
        var outside = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(Map.of("prize", "1261_67")); }
            @Override public void Setup(VirtualTableScenario scn) {}
        };
        return new VirtualTableScenario(light, new HashMap<>(), 40, 40, StartingSetup.DefaultLSGroundLocation,
                darkStart, StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                outside, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void lightJabbasPrizeHasOnePlayableFaceAndExactPrintedIdentity() {
        var scn = fixture(false); var b = scn.GetLSCard("prize").getBlueprint();
        assertEquals(Title.Jabbas_Prize, b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(0, b.getDestiny(), scn.epsilon); assertEquals(0, b.getAbility(), scn.epsilon);
        assertEquals(0, b.getPower(), scn.epsilon); assertEquals(0, b.getDeployCost(), scn.epsilon);
        assertEquals(0, b.getForfeit(), scn.epsilon); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertTrue(b.hasVirtualSuffix()); assertFalse(scn.GetLSCard("prize").isDoubleSided());
        assertTrue(b.isDoesNotCountTowardDeckLimit()); assertTrue(b.isMayNotBePlacedInReserveDeck());
        assertEquals(Rarity.V, b.getRarity()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(List.of(Persona.CORRAN_HORN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REBEL, Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        assertFalse(Filters.Corran_Horn.accepts(scn.game(), scn.GetLSCard("prize")));
    }

    @Test public void prizeIsOutOfPlayWithoutTheOpponentFrozenPrizeAndFalconRestrictionPersists() {
        var scn = fixture(false); scn.StartGame(); var prize = scn.GetLSCard("prize");
        assertEquals(scn.GetCurrentDecision().getText(), Zone.OUT_OF_PLAY, prize.getZone());
        assertFalse(Filters.Corran_Horn.accepts(scn.game(), prize));
        var space = scn.GetLSCard("space"); var normal = scn.GetLSCard("regularFalcon"); var maintenance = scn.GetLSCard("maintenance");
        scn.MoveLocationToTable(space); scn.MoveCardsToLSHand(normal, maintenance);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(12);
        assertTrue(scn.LSCardPlayAvailable(normal));
        assertFalse(scn.LSCardPlayAvailable(maintenance));
    }

    @Test public void replacesActualDarkJabbasPrizeAtCarbonChamberTestingSetup() {
        var scn = fixture(true); scn.StartGame();
        var prize = scn.GetLSCard("prize");
        assertTrue("decision=" + scn.GetCurrentDecision().getText() + " zone=" + prize.getZone(),
                Filters.imprisonedIn(scn.GetDSCard("tower")).accepts(scn.game(), prize));
        assertTrue(Filters.frozenCaptive.accepts(scn.game(), prize));
        assertTrue(Filters.Corran_Horn.accepts(scn.game(), prize));
        assertEquals(Zone.OUT_OF_PLAY, scn.GetDSCard("darkPrize").getZone());
    }
}
