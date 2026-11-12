package com.gempukku.swccgo.cards.set2;

import com.gempukku.swccgo.cards.set2.dark.Card2_093;
import com.gempukku.swccgo.cards.set2.dark.Card2_093o;
import com.gempukku.swccgo.cards.set2.dark.Card2_094;
import com.gempukku.swccgo.cards.set2.dark.Card2_094o;
import com.gempukku.swccgo.cards.set2.dark.Card2_127;
import com.gempukku.swccgo.cards.set2.dark.Card2_127o;
import com.gempukku.swccgo.cards.set2.dark.Card2_128;
import com.gempukku.swccgo.cards.set2.dark.Card2_128o;
import com.gempukku.swccgo.cards.set2.dark.Card2_129;
import com.gempukku.swccgo.cards.set2.dark.Card2_129o;
import com.gempukku.swccgo.cards.set2.dark.Card2_135;
import com.gempukku.swccgo.cards.set2.dark.Card2_135o;
import com.gempukku.swccgo.cards.set2.dark.Card2_140;
import com.gempukku.swccgo.cards.set2.dark.Card2_140o;
import com.gempukku.swccgo.cards.set2.light.Card2_033;
import com.gempukku.swccgo.cards.set2.light.Card2_033o;
import com.gempukku.swccgo.cards.set2.light.Card2_035;
import com.gempukku.swccgo.cards.set2.light.Card2_035d;
import com.gempukku.swccgo.cards.set2.light.Card2_035o;
import com.gempukku.swccgo.cards.set2.light.Card2_040;
import com.gempukku.swccgo.cards.set2.light.Card2_040o;
import com.gempukku.swccgo.cards.set2.light.Card2_042;
import com.gempukku.swccgo.cards.set2.light.Card2_042d;
import com.gempukku.swccgo.cards.set2.light.Card2_073;
import com.gempukku.swccgo.cards.set2.light.Card2_073o;
import com.gempukku.swccgo.cards.set2.dark.Card2_109;
import com.gempukku.swccgo.cards.set2.dark.Card2_109o;
import com.gempukku.swccgo.cards.set2.light.Card2_076;
import com.gempukku.swccgo.cards.set2.light.Card2_076o;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A New Hope text states, part 3 (docs/legacy-versions.md, "Text states"): printed Grabbers without the Grabber icon,
 * printed Undercover without "(Immune to Alter.)", printed Stunning Leader (no lasting exclusion, as Card2_045o),
 * wording-only printed texts, and the Decipher errata (d) texts of Luke's Cape and Attack Run. Texts from the
 * wiki.swccg.com plain articles (Decipher archive scans) and the (Errata) articles.
 */
public class Card_2_ANH_TextStates3_Tests {
    private static void assertSameCard(SwccgCardBlueprint bare, SwccgCardBlueprint state) {
        assertEquals(bare.getTitle(), state.getTitle());
        assertEquals(bare.getUniqueness(), state.getUniqueness());
        assertEquals(bare.getSide(), state.getSide());
        assertEquals(bare.getCardCategory(), state.getCardCategory());
        assertEquals(bare.getExpansionSet(), state.getExpansionSet());
        assertEquals(bare.getRarity(), state.getRarity());
        assertEquals(bare.getLore(), state.getLore());
        for (Icon icon : Icon.values())
            if (icon != Icon.GRABBER)
                assertEquals(icon.name(), bare.getIconCount(icon), state.getIconCount(icon));
    }

    @Test
    public void GrapplingHookPrintedHasItsTextAndPlaysAsTheBareCardExceptAsNoted_2_033o() {
        SwccgCardBlueprint printed = new Card2_033o();
        SwccgCardBlueprint bare = new Card2_033();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_033);
        assertEquals("Use 1 Force to deploy on table just after opponent has played any Interrupt. That Interrupt is still played out but is then 'grappled' (placed here but is out of play). Any new Interrupts of the same name are now unique (•). (Immune to Control.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_33", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertEquals(0, printed.getIconCount(Icon.GRABBER));
        assertEquals(1, bare.getIconCount(Icon.GRABBER));
    }

    @Test
    public void TentaclePrintedHasItsTextAndPlaysAsTheBareCardExceptAsNoted_2_127o() {
        SwccgCardBlueprint printed = new Card2_127o();
        SwccgCardBlueprint bare = new Card2_127();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_127);
        assertEquals("Use 1 Force to deploy on table just after opponent has played any Interrupt. That Interrupt is still played out but is then ‘grabbed’ (placed here but is out of play). Any new Interrupts of the same name are now unique (•). (Immune to Control.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_127", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertEquals(0, printed.getIconCount(Icon.GRABBER));
        assertEquals(1, bare.getIconCount(Icon.GRABBER));
    }

    @Test
    public void TherellBeHellToPayPrintedHasItsTextAndPlaysAsTheBareCardExceptAsNoted_2_128o() {
        SwccgCardBlueprint printed = new Card2_128o();
        SwccgCardBlueprint bare = new Card2_128();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_128);
        assertEquals("Use 3 Force to deploy on table and stack one just-played Interrupt here. To play any new Interrupt of the same name, player must first stack it here and use +1 Force for each Interrupt in stack, even if Interrupt is normally free. (Immune to Control.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_128", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertEquals(0, printed.getIconCount(Icon.GRABBER));
        assertEquals(1, bare.getIconCount(Icon.GRABBER));
    }

    @Test
    public void UndercoverPrintedHasItsTextAndPlaysAsTheBareCardExceptAsNoted_2_040o() {
        SwccgCardBlueprint printed = new Card2_040o();
        SwccgCardBlueprint bare = new Card2_040();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_040);
        assertEquals("Deploy on your spy at a site and cross spy to opponent's side. Spy is now Undercover. If at a site during your deploy phase, may voluntarily \"break cover\" (lose Effect).", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_40", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertFalse(printed.isImmuneToCardTitle(Title.Alter));
        assertTrue(bare.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void UndercoverPrintedHasItsTextAndPlaysAsTheBareCardExceptAsNoted_2_129o() {
        SwccgCardBlueprint printed = new Card2_129o();
        SwccgCardBlueprint bare = new Card2_129();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_129);
        assertEquals("Deploy on your spy at a site and cross spy to opponent's side. Spy is now Undercover. If at a site during your deploy phase, may voluntarily \"break cover\" (lose Effect).", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_129", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
        assertFalse(printed.isImmuneToCardTitle(Title.Alter));
        assertTrue(bare.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void MonnokPrintedHasItsTextAndPlaysAsTheBareCard_2_135o() {
        SwccgCardBlueprint printed = new Card2_135o();
        SwccgCardBlueprint bare = new Card2_135();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_135);
        assertEquals("USED: If opponent has 13 or more cards in hand, place all but 8 (random selection) in Used Pile. LOST: Use 4 Force to cause opponent to reveal hand. All cards opponent has two or more of are lost.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_135", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void ITOPrintedHasItsTextAndPlaysAsTheBareCard_2_093o() {
        SwccgCardBlueprint printed = new Card2_093o();
        SwccgCardBlueprint bare = new Card2_093();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_093);
        assertEquals("When at Detention Block Corridor during the control phase, your Force drains there are +X where X = the number of captives present. Immune to Restraining Bolt.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_93", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void LirinCarnPrintedHasItsTextAndPlaysAsTheBareCard_2_094o() {
        SwccgCardBlueprint printed = new Card2_094o();
        SwccgCardBlueprint bare = new Card2_094();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_094);
        assertEquals("When at same site as other musicians, Lirin collects a \"cover charge.\" For each other musician present, add 1 to the 'cost' for any character to move or deploy to same site.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_94", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void TantiveIVPrintedHasItsTextAndPlaysAsTheBareCard_2_073o() {
        SwccgCardBlueprint printed = new Card2_073o();
        SwccgCardBlueprint bare = new Card2_073();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_073);
        assertEquals("May add 3 pilots and 4 passengers. Has ship-docking capability. Permanent pilot aboard provides ability of 2. May be deployed to any system (even without presence or Force icons).", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_73", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void StunningLeaderPrintedHasItsText_2_140o() {
        SwccgCardBlueprint printed = new Card2_140o();
        SwccgCardBlueprint bare = new Card2_140();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_140);
        assertEquals("After a battle at an interior site is initiated, use 1 Force to cause all leaders and all characters with ability > 2 (on both sides) to be completely excluded from that battle.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_140", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void LukesCapePrintedHasItsTextAndPlaysAsTheBareCard_2_035o() {
        SwccgCardBlueprint printed = new Card2_035o();
        SwccgCardBlueprint bare = new Card2_035();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_035);
        assertEquals("Use 2 Force to deploy on a Rebel or alien. Immune to attrition < 3. Also, you may activate 1 extra Force each turn if Luke's Cape is present at any non-Tatooine location.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_35", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void LukesCapeDecipherErrataHasItsTextAndPlaysAsTheBareCard_2_035d() {
        SwccgCardBlueprint printed = new Card2_035d();
        SwccgCardBlueprint bare = new Card2_035();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_035);
        assertEquals("Use 2 Force to deploy on a Rebel or alien. You may activate 1 extra Force each turn if Luke's Cape is not at a Tatooine location. That character is immune to attrition < 3.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_35", state.currentId());
        assertEquals("D-E1", state.label());
        assertEquals(LocalDate.of(1998, 11, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void AttackRunDecipherErrataHasItsTextAndPlaysAsTheBareCard_2_042d() {
        SwccgCardBlueprint printed = new Card2_042d();
        SwccgCardBlueprint bare = new Card2_042();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_042);
        assertEquals("Deploy on Death Star: Trench. During your move phase, you may make an Attack Run as follows: Enter Trench: Move up to 3 of your starfighters into trench (for free). Dark Side may immediately follow with up to 3 TIEs (for free). Provide Cover: Identify your lead starfighter (Proton Torpedoes* required) and wingmen (if any). Turbolaser Batteries and TIEs with weapons may now target your starfighters (wingmen first, then lead if no wingmen remaining). Hit starfighters are immediately lost. It's Away!: Draw two destiny. Pull Up!: All starfighters now move to Death Star system (for free). If (total destiny + X + Y - Z) > 15, Death Star is \"blown away.\" X = ability of lead pilot or 3 if Targeting Computer is present. Y = total sites at largest Rebel Base (Yavin 4 or Hoth). Z = highest ability of TIE pilots in trench. * Your Proton Torpedoes are immune to Overload.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_42", state.currentId());
        assertEquals("D-E1", state.label());
        assertEquals(LocalDate.of(1998, 11, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void RogueBanthaPrintedHasItsTextAndPlaysAsTheBareCard_2_076o() {
        SwccgCardBlueprint printed = new Card2_076o();
        SwccgCardBlueprint bare = new Card2_076();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_076);
        assertEquals("May carry 2 passengers. Bantha ability = ½. May move as a ‘react.’ At start of battle at same exterior site, one Tusken Raider present (your choice) is ‘trampled’ (immediately lost).", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_76", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    @Test
    public void WED15l7SeptoidDroidPrintedHasItsTextAndPlaysAsTheBareCard_2_109o() {
        SwccgCardBlueprint printed = new Card2_109o();
        SwccgCardBlueprint bare = new Card2_109();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_109);
        assertEquals("* Forfeit value begins at 7. When 'forfeited,' droid remains in play, but forfeit value is reduced by the amount of attrition or battle damage absorbed. Droid lost when forfeit value reaches zero.", printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("2_109", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertNull(bare.getTextState());
    }

    // Stunning Leader: Dark Side plays it after Light Side initiates a battle at the interior site.

    private static VirtualTableScenario stunningLeaderScenario(String stunId) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");     // Luke Skywalker, ability 4
                    put("dodonna", "1_10");  // General Dodonna, leader, ability 2
                    put("rebel", "1_28");    // Rebel Trooper, ability 1
                    put("dutch", "1_8");     // Dutch, leader, arrives after the Interrupt
                }},
                new HashMap<>() {{
                    put("stun", stunId);
                    put("trooper", "1_194"); // Stormtrooper, ability 1
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

    private static void playStunningLeaderAfterBattleInitiated(VirtualTableScenario scn) {
        var stun = scn.GetDSCard("stun");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("luke"), scn.GetLSCard("dodonna"), scn.GetLSCard("rebel"));
        scn.MoveCardsToDSHand(stun);
        scn.EnsureDSForcePile(3);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        if (scn.LSGetDecision() != null && !scn.DSCardPlayAvailable(stun))
            scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(stun));
        scn.DSPlayCard(stun);
        scn.PassAllResponses();
        assertTrue(stun.getZone() == Zone.LOST_PILE || stun.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(excluded(scn, scn.GetLSCard("luke")));
        assertTrue(excluded(scn, scn.GetLSCard("dodonna")));
        assertFalse(excluded(scn, scn.GetLSCard("rebel")));
        assertFalse(excluded(scn, scn.GetDSCard("trooper")));
    }

    private static PhysicalCardImpl leaderArrivesLater(VirtualTableScenario scn) {
        var dutch = scn.GetLSCard("dutch");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), dutch);
        scn.gameState().getBattleState().updateParticipants(scn.game());
        return dutch;
    }

    private static boolean excluded(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), card);
    }

    @Test
    public void StunningLeaderPrintedExcludesOnlyTheLeadersAndCharactersInTheBattleWhenItResolves() {
        var scn = stunningLeaderScenario("2_140o");
        playStunningLeaderAfterBattleInitiated(scn);
        var dutch = leaderArrivesLater(scn);
        assertTrue(scn.gameState().isParticipatingInBattle(dutch));
        assertFalse(excluded(scn, dutch));
    }

    @Test
    public void StunningLeaderBareCardStillExcludesForTheRestOfTheBattle() {
        var scn = stunningLeaderScenario("2_140");
        playStunningLeaderAfterBattleInitiated(scn);
        assertTrue(excluded(scn, leaderArrivesLater(scn)));
    }
}
