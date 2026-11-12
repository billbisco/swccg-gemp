package com.gempukku.swccgo.cards.set2;

import com.gempukku.swccgo.cards.set2.dark.Card2_095o;
import com.gempukku.swccgo.cards.set2.dark.Card2_095;
import com.gempukku.swccgo.cards.set2.dark.Card2_099o;
import com.gempukku.swccgo.cards.set2.dark.Card2_099;
import com.gempukku.swccgo.cards.set2.dark.Card2_113o;
import com.gempukku.swccgo.cards.set2.dark.Card2_113;
import com.gempukku.swccgo.cards.set2.dark.Card2_136o;
import com.gempukku.swccgo.cards.set2.dark.Card2_136;
import com.gempukku.swccgo.cards.set2.dark.Card2_152o;
import com.gempukku.swccgo.cards.set2.dark.Card2_152;
import com.gempukku.swccgo.cards.set2.dark.Card2_156o;
import com.gempukku.swccgo.cards.set2.dark.Card2_156;
import com.gempukku.swccgo.cards.set2.light.Card2_005o;
import com.gempukku.swccgo.cards.set2.light.Card2_005;
import com.gempukku.swccgo.cards.set2.light.Card2_009o;
import com.gempukku.swccgo.cards.set2.light.Card2_009;
import com.gempukku.swccgo.cards.set2.light.Card2_041o;
import com.gempukku.swccgo.cards.set2.light.Card2_041;
import com.gempukku.swccgo.cards.set2.light.Card2_058o;
import com.gempukku.swccgo.cards.set2.light.Card2_058;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A New Hope printed text states (docs/legacy-versions.md, "Text states"). Each printed state has the printed game
 * text (wiki.swccg.com plain article, Decipher archive scan) and plays as the bare card: the printed wording and the
 * errata wording give the same behaviour, so each state extends the bare class and overrides only its text.
 */
public class Card_2_ANH_TextStates_Tests {
    private static void assertSameCard(SwccgCardBlueprint bare, SwccgCardBlueprint state) {
        assertEquals(bare.getTitle(), state.getTitle());
        assertEquals(bare.getUniqueness(), state.getUniqueness());
        assertEquals(bare.getSide(), state.getSide());
        assertEquals(bare.getCardCategory(), state.getCardCategory());
        assertEquals(bare.getExpansionSet(), state.getExpansionSet());
        assertEquals(bare.getRarity(), state.getRarity());
        assertEquals(bare.getLore(), state.getLore());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), bare.getIconCount(icon), state.getIconCount(icon));
    }

    @Test
    public void CommanderVandenWillardPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_005o();
        SwccgCardBlueprint bare = new Card2_005();
        assertEquals("When at a war room you control, Rebel starships at related system are power +1. May use 1 Force to cancel Astromech Shortage.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_005);
        TextState state = printed.getTextState();
        assertEquals("2_5", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void HetNkikPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_009o();
        SwccgCardBlueprint bare = new Card2_009();
        assertEquals("Het is power +1 for each Stormtrooper at same site, unless Reegesk is present. *To deploy, requires 2 Force from each player's Force Pile. Cannot deploy otherwise. Deploy only on Tatooine, but may move elsewhere.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_009);
        TextState state = printed.getTextState();
        assertEquals("2_9", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void WhatreYouTryinToPushOnUsPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_041o();
        SwccgCardBlueprint bare = new Card2_041();
        assertEquals("Use 3 Force to deploy on table and stack one just-played Interrupt here. To play any new Interrupt of the same name, player must first stack it here and use +1 Force for each Interrupt in stack, even if Interrupt is normally free. (Immune to Control.)", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_041);
        TextState state = printed.getTextState();
        assertEquals("2_41", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void WookieeRoarPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_058o();
        SwccgCardBlueprint bare = new Card2_058();
        assertEquals("If Chewbacca is defending a battle alone at a site, add two battle destiny. OR Use 3 Force to search through your Reserve Deck and take Chewbacca into your hand. Shuffle deck, cut and replace. OR Scare off (lose) one 'mouse' droid on table.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_058);
        TextState state = printed.getTextState();
        assertEquals("2_58", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void LtPolTreidumPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_095o();
        SwccgCardBlueprint bare = new Card2_095();
        assertEquals("While at Docking Control Room 327, adds 2 to your total power at Docking Bay 327. Once per turn during your control phase, if at same site as an Undercover spy, may draw destiny. Spy's \"cover is broken\" if destiny = spy's ability.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_095);
        TextState state = printed.getTextState();
        assertEquals("2_95", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void R2Q2PrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_099o();
        SwccgCardBlueprint bare = new Card2_099();
        assertEquals("While aboard any starfighter, adds 1 to power, maneuver and hyperspeed. When at a Scomp link during your draw phase, may use 1 Force to look at top three cards of your Reserve Pile.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_099);
        TextState state = printed.getTextState();
        assertEquals("2_99", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void LaserGatePrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_113o();
        SwccgCardBlueprint bare = new Card2_113();
        assertEquals("Deploy between any two interior mobile sites. To pass, a character must have (power + ability) > 4 or use a Lift Tube (all other vehicles are blocked). Laser Gate armor = 3; may be targeted by a character weapon from either site.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_113);
        TextState state = printed.getTextState();
        assertEquals("2_113", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void NgokPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_136o();
        SwccgCardBlueprint bare = new Card2_136();
        assertEquals("If opponent just deployed four or more characters at same location this turn, prevent all of those characters from battling this turn. OR If opponent just 'reacted' to a battle, cancel the battle.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_136);
        TextState state = printed.getTextState();
        assertEquals("2_136", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void ConquestPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_152o();
        SwccgCardBlueprint bare = new Card2_152();
        assertEquals("May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Has ship-docking capability. Permanent pilot aboard provides ability of 1. After initiating battle against Falcon, may look at Rebel hand.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_152);
        TextState state = printed.getTextState();
        assertEquals("2_152", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void BespinMotorsVoidSpiderTHX1138PrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_156o();
        SwccgCardBlueprint bare = new Card2_156();
        assertEquals("May carry 3 passengers. Permanent pilot aboard provides ability of 1. May move as a 'react.' *Landspeed = 3. OR Up to 3 characters may shuttle to or from same site for free.", printed.getGameText());
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_156);
        TextState state = printed.getTextState();
        assertEquals("2_156", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }
}
