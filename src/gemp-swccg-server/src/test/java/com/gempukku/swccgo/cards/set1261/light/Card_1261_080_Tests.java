package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1261_080_Tests {
    private static final String PLAY_SHIELD = "Play a Defensive Shield";

    private VirtualTableScenario scenario(boolean includeEffect, boolean outsideCards) {
        var light = new HashMap<String, String>();
        if (includeEffect) light.put("jabba", "1261_80");
        light.put("celebration", "7_55"); light.put("bespin", "5_76"); light.put("plaza", "7_113");
        light.put("obi", "1_21"); light.put("han", "1_11");
        var shields = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                var cards = new HashMap<String, String>();
                cards.put("a", "13_1"); cards.put("b", "13_3"); cards.put("c", "13_4");
                cards.put("d", "13_6"); cards.put("ordinary", "1_66");
                return cards;
            }
            @Override public void Setup(VirtualTableScenario scn) { }
        };
        return new VirtualTableScenario(light, new HashMap<>(), 60, 60,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                outsideCards ? shields : StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void JabbaUsesFinalStartingEffectWithPrintedEmptyLore() {
        var scn = scenario(true, false); var b = scn.GetLSCard("jabba").getBlueprint();
        assertEquals("Jabba, This Is Your Last Chance!", b.getTitle()); assertEquals(5, b.getDestiny(), scn.epsilon);
        assertTrue("the slip and PC card image print an empty lore box", b.getLore() == null || b.getLore().isEmpty()); assertFalse(b.hasVirtualSuffix()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(CardSubtype.STARTING, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void JabbaActuallyStartsWithFiveOutsideCardsFaceDownIncludingANonShield() {
        var scn = scenario(true, true); scn.StartGame(); var effect = scn.GetLSCard("jabba");
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        var stacked = scn.gameState().getStackedCards(effect);
        assertEquals(5, stacked.size());
        for (var key : List.of("a", "b", "c", "d", "ordinary")) {
            assertTrue(stacked.contains(scn.GetLSCard(key)));
            assertInZone(Zone.STACKED_FACE_DOWN, scn.GetLSCard(key));
        }
    }

    private void playShield(VirtualTableScenario scn, String key) {
        var effect = scn.GetLSCard("jabba"); var shield = scn.GetLSCard(key);
        scn.LSUseCardAction(effect, PLAY_SHIELD);
        assertTrue(scn.LSHasCardChoiceAvailable(shield));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ordinary")));
        scn.LSChooseCard(shield); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, shield);
    }

    @Test public void JabbaActuallyPlaysTwoShieldsAndCannotPlayAThirdNextTurn() {
        var scn = scenario(true, true); scn.StartGame(); scn.SkipToLSTurn(Phase.CONTROL);
        playShield(scn, "a");
        if (scn.GetAwaitingDecision(scn.DS) != null) scn.DSPass();
        playShield(scn, "b");
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("jabba"), PLAY_SHIELD));
        assertInZone(Zone.STACKED_FACE_DOWN, scn.GetLSCard("c"), scn.GetLSCard("d"), scn.GetLSCard("ordinary"));
    }

    private VirtualTableScenario celebration(boolean includeEffect) {
        var scn = scenario(includeEffect, false); scn.StartGame();
        var chasm = scn.GetLSStartingLocation(); var plaza = scn.GetLSCard("plaza");
        if (chasm.getZone() != Zone.LOCATIONS) scn.MoveLocationToTable(chasm);
        scn.MoveLocationToTable(plaza); scn.MoveLocationToTable(scn.GetLSCard("bespin"));
        scn.MoveCardsToLocation(chasm, scn.GetLSCard("han")); scn.MoveCardsToLocation(plaza, scn.GetLSCard("obi"));
        scn.MoveCardsToLSHand(scn.GetLSCard("celebration")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("celebration")));
        scn.LSPlayCard(scn.GetLSCard("celebration"));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("bespin"))) scn.LSChooseCard(scn.GetLSCard("bespin"));
        scn.PassAllResponses();
        return scn;
    }

    @Test public void JabbaCancelsActualCloudCityCelebrationWithoutEitherObjectiveFace() {
        var scn = celebration(true); assertInZone(Zone.LOST_PILE, scn.GetLSCard("celebration"));
        var ordinary = celebration(false);
        assertEquals(ordinary.GetLSCard("bespin"), ordinary.GetLSCard("celebration").getAttachedTo());
    }

    private String jabbaId = "1261_80";

    private VirtualTableScenario saberScenario(boolean qmc) {
        var light = new HashMap<String, String>(Map.of("jabba", jabbaId, "qmc", "109_4",
                "luke", "9_24", "lukeSaber", "9_90"));
        var dark = new HashMap<String, String>(Map.of("vader", "1_168", "vaderSaber", "1110_8", "maul", "14_77"));
        var scn = new VirtualTableScenario(light, dark, 40, 40,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DSStartingLocation("1_295"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        // Keep the Objective out of the starting choice; it is placed on table directly when the test needs it.
        scn.MoveOutOfPlay(scn.GetLSCard("qmc"));
        scn.StartGame();
        if (qmc) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("qmc"));
        return scn;
    }

    private int vaderDrainTotal(boolean qmc) {
        var scn = saberScenario(qmc); var site = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");
        if (site.getZone() != Zone.LOCATIONS) scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, vader); scn.AttachCardsTo(vader, scn.GetDSCard("vaderSaber"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertTrue(scn.IsActiveForceDrain());
        return scn.GetForceDrainTotal();
    }

    @Test public void JabbaStopsLightsaberDrainBonusOnlyWhileQuietMiningColonyOnTable() {
        int off = vaderDrainTotal(false); int on = vaderDrainTotal(true);
        assertEquals(off - 1, on);
        assertTrue(on >= 1);
    }

    private boolean maulCanSwingAgain(boolean qmc) {
        var scn = saberScenario(qmc); var luke = scn.GetLSCard("luke"); var maul = scn.GetDSCard("maul");
        var battle = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(battle, luke, maul);
        scn.AttachCardsTo(luke, scn.GetLSCard("lukeSaber"));
        var modifiers = scn.game().getModifiersQuerying();
        assertEquals(qmc && jabbaId.equals("2261_1080"), modifiers.mayFireOnlyOncePerBattle(scn.gameState(), scn.GetLSCard("lukeSaber")));
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(battle);
        assertTrue(scn.IsBattleStarted());
        assertTrue(scn.DSCardActionAvailable(maul, "Fire"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(0), scn.GetDSDestiny(1));
        scn.DSUseCardAction(maul, "Fire");
        if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
        scn.PassAllResponses(); scn.PassWeaponFireWithDestinyDraw(2);
        assertFalse(luke.isHit());
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        return scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Double-Bladed Lightsaber"));
    }

    @Test public void LightsabersMaySwingTwiceWithoutQuietMiningColonyOrIndependentOperation() {
        assertTrue(maulCanSwingAgain(false));
    }

    @Test public void OriginalVersionAddsNoSwingLimitEvenWhileQuietMiningColonyOnTable() {
        // 2261_80 (Virtual Block 6.1 sheet, Sep 2009): "lightsabers may not add to Force drains." (no swing clause).
        jabbaId = "2261_80";
        assertTrue(maulCanSwingAgain(true));
        assertEquals(vaderDrainTotal(false) - 1, vaderDrainTotal(true));
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E1, by 2012-07-21) adds "or 'swing' more than once per battle"; it is the latest text (Bill option b).
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(java.util.Map.of("c", "1261_80")), new java.util.HashMap<>(), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetLSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains. Cloud City Celebration is canceled.", text);
        org.junit.Assert.assertNull(scn.GetLSCard("c").getBlueprint().getLegacyVersion());
    }

    // Master List (2013, current) and the sheet: no 'swing' limit (the card list, 2261_1080, adds one)
    @Test public void currentCardAddsNoSwingLimitWhileQuietMiningColonyOnTable() {
        // 2261_80 (Virtual Block 6.1 sheet, Sep 2009): "lightsabers may not add to Force drains." (no swing clause).
        jabbaId = "1261_80";
        assertTrue(maulCanSwingAgain(true));
        assertEquals(vaderDrainTotal(false) - 1, vaderDrainTotal(true));
    }
}
