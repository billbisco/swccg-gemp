package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.*;

public class Card_1261_099_Tests {
    private static final String RAISE = "Raise your converted Kashyyyk system to the top";

    private VirtualTableScenario scenario(boolean kashyyykStart, int effects) {
        var starting = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(); }
            @Override public void Setup(VirtualTableScenario scn) {
                if (!scn.LSDecisionAvailable("Choose starting interrupt")) return;
                assertTrue(kashyyykStart);
                scn.LSChooseCard(scn.GetLSCard("protector"));
                for (String key : List.of("immune", "cell")) {
                    if (!scn.LSDecisionAvailable("to deploy from Reserve Deck")) break;
                    int ordinary = scn.LSGetBPChoices().indexOf("1_66");
                    assertTrue(ordinary < 0 || !Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(ordinary)));
                    if (effects == 0) { scn.LSDecided(""); break; }
                    scn.LSChooseCard(scn.GetLSCard(key));
                    scn.PassAllResponses();
                }
                scn.PassAllResponses();
            }
        };
        return new VirtualTableScenario(new HashMap<>(Map.of(
                "protector", "1261_99", "immune", "9_47", "cell", "601_129", "ordinary", "1_66")),
                new HashMap<>(Map.of("conversion", "2_146", "sense", "1_267", "vader", "1_168", "site", "1_295")),
                40, 40, kashyyykStart ? StartingSetup.LSStartingLocation("2_64") : StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSSpaceSystem, effects < 0 ? StartingSetup.NoLSStartingInterrupts : starting,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void protectorUsesTheFinalLegacyPrinting() {
        var scn = scenario(true, -1); var b = scn.GetLSCard("protector").getBlueprint();
        assertEquals("Protector", b.getTitle()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.USED_OR_STARTING, b.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private PhysicalCard topKashyyyk(VirtualTableScenario scn) {
        return Filters.findFirstFromTopLocationsOnTable(scn.game(), Filters.Kashyyyk_system);
    }

    @Test public void protectorRaisesActualConvertedKashyyykAndCannotBeSensed() {
        var scn = scenario(true, -1); scn.StartGame();
        var conversion = scn.GetDSCard("conversion"); var original = scn.GetLSStartingLocation();
        var protector = scn.GetLSCard("protector"); var site = scn.GetDSCard("site");
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(conversion, scn.GetDSCard("sense"));
        scn.MoveCardsToLSHand(protector);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSActivateForceCheat(5);
        scn.DSDeployLocation(conversion); scn.PassAllResponses();
        assertEquals(conversion, topKashyyyk(scn));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(protector, RAISE));
        scn.LSPlayCard(protector, RAISE);
        for (int i = 0; i < 14; i++) {
            assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Draw destiny to cancel")));
            var d = scn.GetCurrentDecision();
            if (d == null || !d.getText().toLowerCase().contains("response")) break;
            scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
        }
        if (scn.LSHasCardChoiceAvailable(original)) scn.LSChooseCard(original);
        scn.PassAllResponses();
        assertEquals(original, topKashyyyk(scn));
        assertTrue(scn.GetLSUsedPile().contains(protector));
    }

    @Test public void protectorCannotRaiseUnconvertedKashyyyk() {
        var scn = scenario(true, -1); scn.StartGame();
        scn.MoveCardsToLSHand(scn.GetLSCard("protector")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(RAISE)));
    }

    @Test public void protectorStartsOnlyWithKashyyykAndReturnsToHand() {
        var scn = scenario(true, 2); scn.StartGame(false);
        assertInHand(scn.GetLSCard("protector"));
        assertTrue(scn.GetLSCard("immune").getZone().isInPlay());
        assertTrue(scn.GetLSCard("cell").getZone().isInPlay());
        assertFalse(scn.GetLSCard("ordinary").getZone().isInPlay());
        var remote = scenario(false, 2); remote.StartGame(false);
        assertFalse(remote.GetLSCard("immune").getZone().isInPlay());
        assertFalse(remote.GetLSCard("cell").getZone().isInPlay());
    }

    @Test public void protectorMayDeclineBothOptionalEffectsAndStillReturnsToHand() {
        var scn = scenario(true, 0); scn.StartGame(false);
        assertInHand(scn.GetLSCard("protector"));
        assertFalse(scn.GetLSCard("immune").getZone().isInPlay());
        assertFalse(scn.GetLSCard("cell").getZone().isInPlay());
    }
}
