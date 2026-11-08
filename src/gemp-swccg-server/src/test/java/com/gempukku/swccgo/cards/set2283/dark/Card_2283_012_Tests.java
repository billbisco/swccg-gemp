package com.gempukku.swccgo.cards.set2283.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2283_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("momaw", "1_20"); // Momaw Nadon: not a pilot
                    put("ouneeta3", "13_37"); // [Reflections III] Ounee Ta
                    put("ouneeta", "112_4"); // another Ounee Ta
                }},
                new HashMap<>() {{
                    put("spaceport", "2283_12");
                    put("current", "1283_12");
                    put("db94", "1_291"); // Tatooine: Docking Bay 94 (Dark)
                    put("vader", "1_168");
                    put("tie", "1_304"); // TIE Fighter (starfighter)
                    put("houndstooth", "4_168"); // Hound's Tooth (freighter)
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
    public void SpaceportDockingBayVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("spaceport").getBlueprint();
        assertEquals("Spaceport Docking Bay", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPACEPORT_SITE);
            add(Keyword.DOCKING_BAY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SpaceportDockingBayVMayNotBeDeployedToBespinDagobahEndorHothOrYavin4() {
        var scn = GetScenario();
        var spaceport = scn.GetDSCard("spaceport");
        scn.StartGame();
        for (String system : new String[]{Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Yavin_4}) {
            assertTrue(system, spaceport.getBlueprint().mayNotBePartOfSystem(scn.game(), system));
        }
        assertFalse(spaceport.getBlueprint().mayNotBePartOfSystem(scn.game(), Title.Tatooine));
    }

    private float transitCost(VirtualTableScenario scn, PhysicalCard card, PhysicalCard from, PhysicalCard to) {
        return scn.game().getModifiersQuerying().getDockingBayTransitCost(scn.game().getGameState(), card, from, to, 0);
    }

    @Test
    public void SpaceportDockingBayVTransitFromHereRequires2ForceForDarkAnd3ForLight() {
        var scn = GetScenario();
        var spaceport = scn.GetDSCard("spaceport");
        var db94 = scn.GetDSCard("db94");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(spaceport);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(spaceport, vader, luke);
        assertEquals(2, transitCost(scn, vader, spaceport, db94), scn.epsilon);
        assertEquals(3, transitCost(scn, luke, spaceport, db94), scn.epsilon);
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCard card, PhysicalCard target) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.game().getGameState(), card, card, target, false, null, false, 0, null, false);
    }

    @Test
    public void SpaceportDockingBayVYourStarfightersDeployMinus1AndFreightersMinus2Here() {
        var scn = GetScenario();
        var spaceport = scn.GetDSCard("spaceport");
        var db94 = scn.GetDSCard("db94");
        var tie = scn.GetDSCard("tie");
        var houndstooth = scn.GetDSCard("houndstooth");
        scn.StartGame();
        scn.MoveLocationToTable(db94);
        float tieElsewhere = deployCostTo(scn, tie, db94);
        float houndsToothElsewhere = deployCostTo(scn, houndstooth, db94);
        scn.MoveLocationToTable(spaceport);
        assertEquals(Math.max(0, tieElsewhere - 1), deployCostTo(scn, tie, spaceport), scn.epsilon);
        assertEquals(houndsToothElsewhere - 2, deployCostTo(scn, houndstooth, spaceport), scn.epsilon);
    }

    @Test
    public void SpaceportDockingBayVIsImmuneToReflectionsIIIOuneeTaOnly() {
        var scn = GetScenario();
        var spaceport = scn.GetDSCard("spaceport");
        var ouneeta3 = scn.GetLSCard("ouneeta3");
        var ouneeta = scn.GetLSCard("ouneeta");
        scn.StartGame();
        scn.MoveLocationToTable(spaceport);
        var qs = scn.game().getModifiersQuerying();
        assertFalse(qs.canBeTargetedBy(scn.game().getGameState(), spaceport, ouneeta3));
        assertTrue(qs.canBeTargetedBy(scn.game().getGameState(), spaceport, ouneeta));
    }

    @Test
    public void SpaceportDockingBayVOriginalIdentityAndText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("spaceport").getBlueprint();

        assertEquals("Spaceport Docking Bay", card.getTitle());
        assertTrue(card.isLegacy());
        assertEquals("Dark: Your docking bay transit from here requires 2 Force. Your starfighters deploy -1 (or -2 if freighter) here. Immune to [Reflections III] Ounee Ta. Light: May not be deployed to Bespin, Dagobah, Endor, Hoth or Yavin 4. Your docking bay transit from here requires 3 Force.",
                card.getGameText());
        var version = card.getLegacyVersion();
        org.junit.Assert.assertNotNull(version);
        assertEquals("1283_12", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(java.time.LocalDate.of(2013, 3, 28), version.printedOn());
        assertEquals(java.time.LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals("Spaceport Docking Bay (V)", com.gempukku.swccgo.logic.GameUtils.getFullName(card));
    }

    @Test
    public void OriginalSpaceportDockingBayVHasNoPilotDeployClause() {
        var scn = GetScenario();
        var current = scn.GetDSCard("spaceport");
        var db94 = scn.GetDSCard("db94");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(db94);
        float lukeElsewhere = deployCostTo(scn, luke, db94);
        scn.MoveLocationToTable(current);
        assertEquals(lukeElsewhere, deployCostTo(scn, luke, current), scn.epsilon);
    }

    @Test
    public void CurrentSpaceportDockingBayVLightSidePilotsDeployPlus1Here() {
        var scn = GetScenario();
        var spaceport = scn.GetDSCard("current");
        var db94 = scn.GetDSCard("db94");
        var luke = scn.GetLSCard("luke"); // pilot
        var momaw = scn.GetLSCard("momaw"); // not a pilot
        var vader = scn.GetDSCard("vader"); // Dark Side pilot: not affected
        scn.StartGame();
        scn.MoveLocationToTable(db94);
        float lukeElsewhere = deployCostTo(scn, luke, db94);
        float momawElsewhere = deployCostTo(scn, momaw, db94);
        float vaderElsewhere = deployCostTo(scn, vader, db94);
        scn.MoveLocationToTable(spaceport);
        assertEquals(lukeElsewhere + 1, deployCostTo(scn, luke, spaceport), scn.epsilon);
        assertEquals(momawElsewhere, deployCostTo(scn, momaw, spaceport), scn.epsilon);
        assertEquals(vaderElsewhere, deployCostTo(scn, vader, spaceport), scn.epsilon);
    }
}
