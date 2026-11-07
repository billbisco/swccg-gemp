package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.set1271.LegacyShuffleObserver;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1271_020_Tests {
    private VirtualTableScenario scenario(String forbidden, boolean converted, List<String> effects, boolean inspectEffects) {
        var dark = new HashMap<String, String>();
        dark.put("design", "1271_20");
        dark.put("emperor", "9_109");
        dark.put("vader", "1_168");
        dark.put("noTry", "4_134");
        dark.put("mobilization", "9_129");
        dark.put("arrestOrder", "8_126");
        dark.put("oppressive", "7_234");
        dark.put("attached", "1_227");
        dark.put("shield", "13_86");
        dark.put("starship", "1_306");
        dark.put("system", "1_282");
        dark.put("firstMarker", "1282_8");
        dark.put("deathStar", "2_143");
        dark.put("starshipSite", "4_163");
        StartingSetup normalLight = converted ? StartingSetup.LSStartingLocation("1_129") : StartingSetup.DefaultLSGroundLocation;
        StartingSetup light = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return normalLight.Cards(); }
            @Override public void Setup(VirtualTableScenario s) {
                if ("starship".equals(forbidden)) {
                    s.MoveLocationToTable(s.GetDSCard("system"));
                    s.MoveCardsToLocation(s.GetDSCard("system"), s.GetDSCard("starship"));
                } else if (forbidden != null && !forbidden.equals("deathStar")) {
                    s.MoveLocationToTable(s.GetDSCard(forbidden));
                }
                normalLight.Setup(s);
            }
        };
        StartingSetup starting = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(); }
            @Override public void Setup(VirtualTableScenario s) {
                if (effects == null) {
                    if (forbidden != null && s.DSDecisionAvailable("starting interrupt")) assertFalse("Starting Interrupt must be excluded by " + forbidden,
                        s.DSHasCardChoiceAvailable(s.GetDSCard("design")));
                    if (s.DSDecisionAvailable("starting interrupt")) s.DSPass();
                    return;
                }
                s.DSChooseCard(s.GetDSCard("design"));
                s.PassAllResponses();
                s.DSChooseCard(s.GetDSCard("emperor"));
                s.PassAllResponses();
                var target = converted ? s.GetLSStartingLocation() : s.GetDSStartingLocation();
                if (s.DSHasCardChoiceAvailable(target)) {
                    s.DSChooseCard(target);
                    s.PassAllResponses();
                }
                if (inspectEffects) {
                    for (String name : List.of("noTry", "mobilization", "arrestOrder", "oppressive")) {
                        assertTrue(name, s.DSHasCardChoiceAvailable(s.GetDSCard(name)));
                    }
                    for (String name : List.of("attached", "shield", "vader")) {
                        assertFalse(name, s.DSHasCardChoiceAvailable(s.GetDSCard(name)));
                    }
                }
                for (String name : effects) {
                    s.DSChooseCard(s.GetDSCard(name));
                    s.PassAllResponses();
                }
                if (effects.size() < 3) {
                    s.DSDecided("");
                    s.PassAllResponses();
                }
            }
        };
        StartingSetup darkStart = converted ? StartingSetup.DSStartingLocation("1_291")
            : "deathStar".equals(forbidden) ? StartingSetup.DSStartingLocation("2_143") : StartingSetup.DefaultDSGroundLocation;
        return new VirtualTableScenario(new HashMap<>(), dark, 40, 40, light, darkStart,
            StartingSetup.NoLSStartingInterrupts, starting, StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void AccordingToMyDesignPrintedStatsIconsKeywordsAndLoreAreCorrect() {
        var s = scenario(null, false, null, false);
        var b = s.GetDSCard("design").getBlueprint();
        assertEquals("According To My Design", b.getTitle());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(4, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.LOST_OR_STARTING, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertEquals("'Your fleet is lost. And your friends on the Endor moon will not survive. There is no escape, my young apprentice.'", b.getLore());
        s.BlueprintCardTypeCheck(b, List.of(CardType.INTERRUPT));
        s.BlueprintKeywordCheck(b, List.of());
        s.BlueprintIconCheck(b, List.of(Icon.INTERRUPT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_7));
    }

    private VirtualTableScenario lost(int force) {
        var s = scenario(null, false, null, false);
        s.StartGame();
        s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToDSHand(s.GetDSCard("design"));
        if (force >= s.GetDSForcePileCount()) s.EnsureDSForcePile(force);
        else s.DrainDSForcePileTo(force);
        s.SkipToPhase(Phase.DEPLOY);
        return s;
    }

    @Test public void AccordingToMyDesignLostPaysThreeThenNormalEmperorDeployAndReshuffles() {
        var s = lost(12);
        var observer = LegacyShuffleObserver.observe(s);
        int force = s.GetDSForcePileCount();
        s.DSPlayCard(s.GetDSCard("design"));
        s.PassAllResponses();
        assertFalse(s.DSHasCardChoiceAvailable(s.GetDSCard("vader")));
        s.DSChooseCard(s.GetDSCard("emperor"));
        s.PassAllResponses();
        if (s.DSHasCardChoiceAvailable(s.GetDSStartingLocation())) {
            s.DSChooseCard(s.GetDSStartingLocation());
            s.PassAllResponses();
        }
        assertTrue(s.GetDSCard("emperor").getZone().isInPlay());
        assertEquals(force - 8, s.GetDSForcePileCount());
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("design")));
        assertTrue(observer.sawOwnedShuffle(s.DS, Zone.RESERVE_DECK));
    }

    @Test public void AccordingToMyDesignLostCannotBePlayedWithLessThanThreeForce() {
        var s = lost(2);
        assertEquals(2, s.GetDSForcePileCount());
        assertFalse(s.DSCardPlayAvailable(s.GetDSCard("design")));
    }

    @Test public void AccordingToMyDesignStartingDeploysEmperorForFreeAndAllowsZeroEffects() {
        var s = scenario(null, false, List.of(), false);
        s.StartGame();
        assertTrue(s.GetDSCard("emperor").getZone().isInPlay());
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("design")));
        assertEquals(0, s.GetDSUsedPile().size());
        assertEquals(s.LS, s.GetCurrentPlayer());
        for (String name : List.of("noTry", "mobilization", "arrestOrder", "oppressive")) {
            assertFalse(name, s.GetDSCard(name).getZone().isInPlay());
        }
    }

    @Test public void AccordingToMyDesignStartingDeploysAtMostThreeQualifyingEffects() {
        var s = scenario(null, false, List.of("noTry", "mobilization", "oppressive"), true);
        s.StartGame();
        for (String name : List.of("noTry", "mobilization", "oppressive")) {
            assertEquals(name, Zone.SIDE_OF_TABLE, s.GetDSCard(name).getZone());
        }
        assertFalse(s.GetDSCard("arrestOrder").getZone().isInPlay());
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("design")));
    }

    @Test public void AccordingToMyDesignStartingCanDeployEmperorToItsConvertedSite() {
        var s = scenario(null, true, List.of(), false);
        s.StartGame();
        assertTrue(s.GetDSCard("emperor").getZone().isInPlay());
        assertTrue(s.GetCardsAtLocation(s.GetLSStartingLocation()).contains(s.GetDSCard("emperor")));
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("design")));
    }

    private void forbidden(String card) {
        var s = scenario(card, false, null, false);
        s.StartGame();
        var locationOrShip = card.equals("deathStar") ? s.GetDSStartingLocation() : s.GetDSCard(card);
        assertTrue(card, locationOrShip.getZone().isInPlay());
        assertFalse(s.GetDSCard("emperor").getZone().isInPlay());
        assertFalse(s.GetDSLostPile().contains(s.GetDSCard("design")));
    }

    @Test public void AccordingToMyDesignStartingIsExcludedByItsFirstMarker() { forbidden("firstMarker"); }
    @Test public void AccordingToMyDesignStartingIsExcludedByItsDeathStarSystem() { forbidden("deathStar"); }
    @Test public void AccordingToMyDesignStartingIsExcludedByItsStarship() { forbidden("starship"); }
    @Test public void AccordingToMyDesignStartingIsExcludedByItsStarshipSite() { forbidden("starshipSite"); }
}
