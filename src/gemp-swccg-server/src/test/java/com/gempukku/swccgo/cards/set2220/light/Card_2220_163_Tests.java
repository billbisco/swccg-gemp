package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** Local Uprising / Liberation: original (pre-errata) version of 1220_163 (front changed, back unchanged). */
public class Card_2220_163_Tests {
    private VirtualTableScenario fixture() {
        var ls = new HashMap<>(Map.ofEntries(
                Map.entry("original", "3_61"), Map.entry("trench", "3_63"),
                Map.entry("perimeter", "3_56"), Map.entry("ridge", "3_62"),
                Map.entry("echo", "3_57"), Map.entry("rebel", "3_6"), Map.entry("speeder", "3_69")));
        ls.put("formation", "7_107");
        ls.put("secondFormation", "7_107");
        var ds = new HashMap<>(Map.of("vader", "1_168", "sense", "1_267", "garrisonOne", "104_7", "garrisonTwo", "104_7"));
        return new VirtualTableScenario(ls, ds, 40, 40, new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("objective", "2220_163", "hoth", "3_55",
                        "generators", "1261_112", "garrison", "111_3", "flaps", "7_69"));
            }

            @Override public void Setup(VirtualTableScenario scn) {
                var trace = new java.util.ArrayList<String>();
                for (int i = 0; i < 20 && scn.GetCurrentDecision() != null; i++) {
                    var prompt = scn.GetCurrentDecision().getText();
                    trace.add(prompt);
                    if (prompt.startsWith("On which side of")) {
                        scn.LSChooseOption("Left");
                    } else if (prompt.startsWith("Verify ")) {
                        scn.PlayerPass(scn.GetDecidingPlayer());
                    } else if (prompt.contains("Choose Hoth system to deploy")) {
                        scn.LSChooseCard(scn.GetLSCard("hoth"));
                    } else if (prompt.contains("Choose Main Power Generators to deploy")) {
                        scn.LSChooseCard(scn.GetLSCard("generators"));
                    } else if (prompt.equals("Deploy Echo Base Garrison?") || prompt.equals("Deploy Maneuvering Flaps?")) {
                        scn.LSChooseNo();
                    } else if (prompt.toLowerCase().contains("optional response")) {
                        scn.PlayerPass(scn.GetDecidingPlayer());
                    } else {
                        break;
                    }
                }
                assertEquals(trace.toString(), Zone.LOCATIONS, scn.GetLSCard("generators").getZone());
            }
        }, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void VersionHasTheOldTextAndVersionInfo() {
        var scn = fixture();
        var card = scn.GetLSCard("objective").getBlueprint();
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1220_163");
        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy Hoth system and [Virtual Block 6] Main Power Generators. May deploy Echo Base Garrison and/or Maneuvering Flaps. For remainder of game, you may not deploy Hoth Sentry or non-Hoth locations (except Rendezvous Point). T-47 Battle Formation is an Out Of Play Interrupt and immune to Sense. While this side up, during your deploy phase, may deploy an exterior Hoth site or a non-unique Rebel to Hoth from Reserve Deck; reshuffle. Flip this card during your move phase if you control three exterior Hoth sites with T-47s and/or non-unique Rebels. Place out of play if Main Power Generators 'blown away.'",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_163", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 11, 19), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));

        var back = new Card2220_163_BACK();
        assertEquals("Liberation", back.getTitle());
        assertEquals(new com.gempukku.swccgo.cards.set1220.light.Card1220_163_BACK().getGameText(), back.getGameText());
        assertEquals("1220_163", back.getLegacyVersion().currentPrintingId());
    }

    @Test
    public void FormationIsAnOutOfPlayInterruptImmuneToSenseAndMayBePlayedAgain() {
        var scn = fixture();
        scn.StartGame();
        var objective = scn.GetLSCard("objective");
        var first = scn.GetLSCard("formation");
        var second = scn.GetLSCard("secondFormation");
        var garrison = scn.GetDSCard("garrisonOne");
        var later = scn.GetDSCard("garrisonTwo");

        //test1: Out Of Play Interrupt and immune to Sense
        //test2: placed out of play when played
        //test3: a second copy may be played (the current card: once per game)
        assertTrue(scn.IsCardActive(objective));
        assertEquals(CardSubtype.OUT_OF_PLAY, scn.game().getModifiersQuerying().getModifiedSubtype(scn.gameState(), first)); //test1
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), first, Title.Sense)); //test1
        scn.MoveCardsToDSSideOfTable(garrison);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToLSHand(first, second);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(5);
        assertTrue(scn.LSCardPlayAvailable(first, "Cancel Walker Garrison"));
        scn.LSPlayCard(first, "Cancel Walker Garrison");
        if (scn.LSHasCardChoiceAvailable(garrison))
            scn.LSChooseCard(garrison);
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, first.getZone()); //test2
        assertTrue(scn.GetDSLostPile().contains(garrison));
        scn.MoveCardsToDSSideOfTable(later);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(second, "Cancel Walker Garrison")); //test3
    }
}
