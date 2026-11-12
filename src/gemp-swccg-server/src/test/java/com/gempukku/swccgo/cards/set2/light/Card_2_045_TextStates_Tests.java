package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Blast The Door, Kid! text states (docs/legacy-versions.md, "Text states"):
 * 2_45o as printed (A New Hope, 1997), 2_45d Decipher Glossary 2.0 (1998) with the 2002 Glossary Supplement ruling,
 * and the bare card 2_45 unchanged. Texts from wiki.swccg.com [[Blast The Door, Kid!]] and
 * [[Blast The Door, Kid! (Errata)]].
 */
public class Card_2_045_TextStates_Tests {
    private static final String PRINTED_TEXT = "After a battle at an interior site is initiated, use 1 Force to cause all leaders and all characters with ability > 2 (on both sides) to be completely excluded from that battle.";
    private static final String GLOSSARY_TEXT = "If a battle was just initiated at an interior site, use 1 Force to exclude from that battle all characters of ability > 2 and all leaders (on both sides).";
    private static final String LORE = "When Vader turned his attention to the escaping Rebels, Han immediately offered Luke some sage advice.";

    protected VirtualTableScenario GetScenario(String blastId) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blast", blastId);
                    put("rebel", "1_28"); // Rebel Trooper, ability 1
                }},
                new HashMap<>() {{
                    put("djas", "1_171");    // Djas Puhr, ability 4
                    put("yularen", "1_166"); // Colonel Wullf Yularen, leader, ability 2
                    put("trooper", "1_194"); // Stormtrooper, ability 1
                    put("tagge", "1_178");   // General Tagge, leader, arrives after the Interrupt
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation, // Cloud City: Chasm Walkway (interior)
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    private static void assertBlastTheDoorStats(VirtualTableScenario scn, SwccgCardBlueprint card) {
        assertEquals("Blast The Door, Kid!", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.C2, card.getRarity());
        assertEquals(LORE, card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
    }

    @Test
    public void BlastTheDoorKidPrintedHasThePrintedTextAndStats() {
        var scn = GetScenario("2_45o");
        var card = scn.GetLSCard("blast").getBlueprint();
        assertBlastTheDoorStats(scn, card);
        assertEquals(PRINTED_TEXT, card.getGameText());
        TextState state = card.getTextState();
        assertEquals("2_45", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertEquals("Blast The Door, Kid! [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void BlastTheDoorKidDecipherErrataHasTheGlossaryTextAndStats() {
        var scn = GetScenario("2_45d");
        var card = scn.GetLSCard("blast").getBlueprint();
        assertBlastTheDoorStats(scn, card);
        assertEquals(GLOSSARY_TEXT, card.getGameText());
        TextState state = card.getTextState();
        assertEquals("2_45", state.currentId());
        assertEquals("D-E1", state.label());
        assertEquals(LocalDate.of(1998, 11, 1), state.printedOn());
        assertEquals("Blast The Door, Kid! [D-E1]", GameUtils.getFullName(card));
    }

    @Test
    public void BlastTheDoorKidBareCardIsUnchanged() {
        var scn = GetScenario("2_45");
        var card = scn.GetLSCard("blast").getBlueprint();
        assertBlastTheDoorStats(scn, card);
        assertEquals(GLOSSARY_TEXT, card.getGameText());
        assertNull(card.getTextState());
        assertNull(card.getLegacyVersion());
        assertEquals("Blast The Door, Kid!", GameUtils.getFullName(card));
    }

    /** Plays Blast The Door, Kid! right after Dark Side initiates a battle at the interior site. */
    private static void playBlastAfterBattleInitiated(VirtualTableScenario scn) {
        var blast = scn.GetLSCard("blast");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("rebel"), scn.GetDSCard("djas"), scn.GetDSCard("yularen"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(blast);
        scn.EnsureLSForcePile(3);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        if (scn.DSGetDecision() != null && !scn.LSCardPlayAvailable(blast))
            scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(blast));
        scn.LSPlayCard(blast);
        scn.PassAllResponses();
        assertTrue(blast.getZone() == Zone.LOST_PILE || blast.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    private static boolean excluded(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), card);
    }

    /** General Tagge (a leader) arrives at the battle after the Interrupt resolved. */
    private static PhysicalCardImpl leaderArrivesLater(VirtualTableScenario scn) {
        var tagge = scn.GetDSCard("tagge");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), tagge);
        scn.gameState().getBattleState().updateParticipants(scn.game());
        return tagge;
    }

    private static void assertExcludesLeadersAndAbilityMoreThanTwo(VirtualTableScenario scn) {
        assertTrue(excluded(scn, scn.GetDSCard("djas")));     // ability 4
        assertTrue(excluded(scn, scn.GetDSCard("yularen")));  // leader
        assertFalse(excluded(scn, scn.GetDSCard("trooper"))); // ability 1
        assertFalse(excluded(scn, scn.GetLSCard("rebel")));   // ability 1
        assertTrue(scn.gameState().isParticipatingInBattle(scn.GetDSCard("trooper")));
    }

    @Test
    public void BlastTheDoorKidPrintedExcludesOnlyTheLeadersAndCharactersInTheBattleWhenItResolves() {
        var scn = GetScenario("2_45o");
        playBlastAfterBattleInitiated(scn);
        assertExcludesLeadersAndAbilityMoreThanTwo(scn);
        // As printed: no lasting exclusion (the late-arrival rule came with the 2002 Glossary Supplement)
        var tagge = leaderArrivesLater(scn);
        assertTrue(scn.gameState().isParticipatingInBattle(tagge));
        assertFalse(excluded(scn, tagge));
    }

    @Test
    public void BlastTheDoorKidDecipherErrataAlsoExcludesALeaderIntroducedLater() {
        var scn = GetScenario("2_45d");
        playBlastAfterBattleInitiated(scn);
        assertExcludesLeadersAndAbilityMoreThanTwo(scn);
        // Glossary Supplement (2002): leaders and characters of ability > 2 introduced later are excluded too
        assertTrue(excluded(scn, leaderArrivesLater(scn)));
    }

    @Test
    public void BlastTheDoorKidBareCardStillExcludesForTheRestOfTheBattle() {
        var scn = GetScenario("2_45");
        playBlastAfterBattleInitiated(scn);
        assertExcludesLeadersAndAbilityMoreThanTwo(scn);
        assertTrue(excluded(scn, leaderArrivesLater(scn)));
    }

    @Test
    public void BlastTheDoorKidTextStatesAreTheSameCardAsTheBareCard() {
        var scn = GetScenario("2_45o");
        var printed = scn.GetLSCard("blast").getBlueprint();
        var bare = GetScenario("2_45").GetLSCard("blast").getBlueprint();
        var errata = GetScenario("2_45d").GetLSCard("blast").getBlueprint();
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getTitle(), errata.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getUniqueness(), errata.getUniqueness());
        assertEquals(bare.getPersonas(), printed.getPersonas());
        assertEquals(bare.getPersonas(), errata.getPersonas());
    }
}
