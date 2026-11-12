package com.gempukku.swccgo.cards.set2;

import com.gempukku.swccgo.cards.set2.dark.Card2_085;
import com.gempukku.swccgo.cards.set2.dark.Card2_085o;
import com.gempukku.swccgo.cards.set2.dark.Card2_088;
import com.gempukku.swccgo.cards.set2.dark.Card2_088o;
import com.gempukku.swccgo.cards.set2.dark.Card2_098;
import com.gempukku.swccgo.cards.set2.dark.Card2_098o;
import com.gempukku.swccgo.cards.set2.dark.Card2_103;
import com.gempukku.swccgo.cards.set2.dark.Card2_103o;
import com.gempukku.swccgo.cards.set2.dark.Card2_104;
import com.gempukku.swccgo.cards.set2.dark.Card2_104o;
import com.gempukku.swccgo.cards.set2.dark.Card2_125;
import com.gempukku.swccgo.cards.set2.dark.Card2_125o;
import com.gempukku.swccgo.cards.set2.dark.Card2_149;
import com.gempukku.swccgo.cards.set2.dark.Card2_149o;
import com.gempukku.swccgo.cards.set2.light.Card2_003;
import com.gempukku.swccgo.cards.set2.light.Card2_003o;
import com.gempukku.swccgo.cards.set2.light.Card2_008;
import com.gempukku.swccgo.cards.set2.light.Card2_008o;
import com.gempukku.swccgo.cards.set2.light.Card2_021;
import com.gempukku.swccgo.cards.set2.light.Card2_021o;
import com.gempukku.swccgo.cards.set2.light.Card2_023;
import com.gempukku.swccgo.cards.set2.light.Card2_023o;
import com.gempukku.swccgo.cards.set2.light.Card2_039;
import com.gempukku.swccgo.cards.set2.light.Card2_039o;
import com.gempukku.swccgo.cards.set2.light.Card2_060;
import com.gempukku.swccgo.cards.set2.light.Card2_060o;
import com.gempukku.swccgo.cards.set2.light.Card2_061;
import com.gempukku.swccgo.cards.set2.light.Card2_061o;
import com.gempukku.swccgo.cards.set2.light.Card2_066;
import com.gempukku.swccgo.cards.set2.light.Card2_066o;
import com.gempukku.swccgo.cards.set2.light.Card2_070;
import com.gempukku.swccgo.cards.set2.light.Card2_070o;
import com.gempukku.swccgo.cards.set2.light.Card2_071;
import com.gempukku.swccgo.cards.set2.light.Card2_071o;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A New Hope printed text states, part 2 (docs/legacy-versions.md, "Text states"): Decipher-era locations and cards
 * whose printed wording plays as the bare card, and pilots whose printed text adds power only to "any starship he
 * pilots" (the errata says "anything he pilots", so vehicles too). Texts from the wiki.swccg.com plain articles
 * (Decipher archive scans).
 */
public class Card_2_ANH_TextStates2_Tests {
    private static void assertSameCard(SwccgCardBlueprint bare, SwccgCardBlueprint state) {
        assertEquals(bare.getTitle(), state.getTitle());
        assertEquals(bare.getUniqueness(), state.getUniqueness());
        assertEquals(bare.getSide(), state.getSide());
        assertEquals(bare.getCardCategory(), state.getCardCategory());
        assertEquals(bare.getExpansionSet(), state.getExpansionSet());
        assertEquals(bare.getRarity(), state.getRarity());
        assertEquals(bare.getLore(), state.getLore());
    }

    private static void assertPrintedState(SwccgCardBlueprint printed, String currentId) {
        TextState state = printed.getTextState();
        assertEquals(currentId, state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
    }

    /** Power of the vehicle or starship piloted by the pilot, and its printed power. */
    private static int[] pilotedPower(String pilotId, boolean light, boolean onStarship) {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    if (light) put("pilot", pilotId);
                    put("speeder", "3_69");  // Snowspeeder (combat vehicle, piloted)
                    put("ywing", "1_144");   // Red 1 (no permanent pilot)
                }},
                new HashMap<>() {{
                    if (!light) put("pilot", pilotId);
                    put("ubrikkian", "5_178"); // Cloud Car (combat vehicle, piloted)
                    put("tie", "2_151");       // Black 4 (no permanent pilot)
                    put("kessel", "1_288");    // Kessel (system)
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
        var pilot = light ? scn.GetLSCard("pilot") : scn.GetDSCard("pilot");
        var vehicle = light ? scn.GetLSCard("speeder") : scn.GetDSCard("ubrikkian");
        var starship = light ? scn.GetLSCard("ywing") : scn.GetDSCard("tie");
        var kessel = scn.GetDSCard("kessel");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(site, vehicle);
        scn.MoveCardsToLocation(kessel, starship);
        var piloted = onStarship ? starship : vehicle;
        scn.BoardAsPilot(piloted, pilot);
        return new int[] {scn.GetPower(piloted), piloted.getBlueprint().getPower().intValue()};
    }

    /** Power of a vehicle piloted by the pilot, of a starship piloted by the pilot, and the starship's printed power. */
    private static int[] pilotedPowers(String pilotId, boolean light) {
        int[] vehicle = pilotedPower(pilotId, light, false);
        int[] starship = pilotedPower(pilotId, light, true);
        return new int[] {vehicle[0], starship[0], starship[1]};
    }

    @Test
    public void ClakdorVIIPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_060o();
        SwccgCardBlueprint bare = new Card2_060();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_060);
        assertPrintedState(printed, "2_60");
        assertNull(bare.getTextState());
        assertEquals("If you occupy, all Bith characters are destiny +2.", printed.getLocationLightSideGameText());
        assertEquals("If you control, all Bith characters are destiny -1 and Ghhhk is power +2 in dejarik battles.", printed.getLocationDarkSideGameText());
    }

    @Test
    public void CorelliaPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_061o();
        SwccgCardBlueprint bare = new Card2_061();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_061);
        assertPrintedState(printed, "2_61");
        assertNull(bare.getTextState());
        assertEquals("Your Corellian Corvettes and Millennium Falcon may deploy here as a 'react.'", printed.getLocationLightSideGameText());
        assertEquals("Your starships are hyperspeed +1 when moving from here.", printed.getLocationDarkSideGameText());
    }

    @Test
    public void SandcrawlerLoadingBayPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_066o();
        SwccgCardBlueprint bare = new Card2_066();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_066);
        assertPrintedState(printed, "2_66");
        assertNull(bare.getTextState());
        assertEquals("Deploy on your Sandcrawler. Jawas forfeit +2 here. \"Nighttime conditions\" here.", printed.getLocationLightSideGameText());
        assertEquals("Your characters may enter/exit here for 1 Force each. \"Nighttime conditions\" here.", printed.getLocationDarkSideGameText());
    }

    @Test
    public void SandcrawlerDroidJunkheapPrintedHasThePrintedTextAndPlaysAsTheBareCard() {
        SwccgCardBlueprint printed = new Card2_149o();
        SwccgCardBlueprint bare = new Card2_149();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_149);
        assertPrintedState(printed, "2_149");
        assertNull(bare.getTextState());
        assertEquals("Your characters may enter/exit here for 1 Force each. \"Nighttime conditions\" here.", printed.getLocationLightSideGameText());
        assertEquals("Deploy on your Sandcrawler. Jawas forfeit +2 here. \"Nighttime conditions\" here.", printed.getLocationDarkSideGameText());
    }

    @Test
    public void TheyreOnDantooinePrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_039o();
        SwccgCardBlueprint bare = new Card2_039();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_039);
        assertPrintedState(printed, "2_39");
        assertNull(bare.getTextState());
        assertEquals("Once per game, use 2 Force to deploy on Dantooine. Target Death Star. Opponent may not play Commence Primary Ignition until target reaches Dantooine, canceling Utinni Effect.", printed.getGameText());
    }

    @Test
    public void Red2PrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_070o();
        SwccgCardBlueprint bare = new Card2_070();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_070);
        assertPrintedState(printed, "2_70");
        assertNull(bare.getTextState());
        assertEquals("May add 1 pilot and 1 astromech. Immune to attrition < 3 if Wedge piloting. Must have pilot aboard to use power, maneuver or hyperspeed.", printed.getGameText());
    }

    @Test
    public void Red5PrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_071o();
        SwccgCardBlueprint bare = new Card2_071();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_071);
        assertPrintedState(printed, "2_71");
        assertNull(bare.getTextState());
        assertEquals("May add 1 pilot and 1 astromech. Immune to attrition < 4 if Luke piloting. Must have pilot aboard to use power, maneuver or hyperspeed. When firing in an Attack Run, adds 1 to total.", printed.getGameText());
    }

    @Test
    public void SpiceMinesOfKesselPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_125o();
        SwccgCardBlueprint bare = new Card2_125();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_125);
        assertPrintedState(printed, "2_125");
        assertNull(bare.getTextState());
        assertEquals("Deploy on Kessel (may not be moved). Target one captive and one trooper to escort captive. When targets reach Utinni Effect, retrieve lost Force equal to captive’s forfeit (captive and Utinni Effect lost). If trooper lost en route, captive released.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void ChewbaccaPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_003o();
        SwccgCardBlueprint bare = new Card2_003();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_003);
        assertPrintedState(printed, "2_3");
        assertNull(bare.getTextState());
        assertEquals("Power +1 at same location as Han. Adds 2 to power of any starship he pilots. On Millennium Falcon, also adds 1 to maneuver. Your vehicles, starships and droids at same site go to Used Pile (rather than Lost Pile) when they are 'hit.'", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void ChewbaccaPrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_3", true);
        int[] printed = pilotedPowers("2_3o", true);
        assertEquals("vehicle: errata text adds 2, printed text adds nothing", bare[0] - 2, printed[0]);
        assertEquals("starship: both texts add 2", bare[1], printed[1]);
        assertEquals(bare[2] + 2, printed[1]);
    }

    @Test
    public void GaroufLafoePrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_008o();
        SwccgCardBlueprint bare = new Card2_008();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_008);
        assertPrintedState(printed, "2_8");
        assertNull(bare.getTextState());
        assertEquals("Adds 1 to power of any starship he pilots. All your characters at same Tatooine site are forfeit +1. All opponent's characters at same Hoth site are forfeit -1. Game text canceled if at same site as a tax collector.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void GaroufLafoePrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_8", true);
        int[] printed = pilotedPowers("2_8o", true);
        assertEquals("vehicle: errata text adds 1, printed text adds nothing", bare[0] - 1, printed[0]);
        assertEquals("starship: both texts add 1", bare[1], printed[1]);
        assertEquals(bare[2] + 1, printed[1]);
    }

    @Test
    public void TireePrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_021o();
        SwccgCardBlueprint bare = new Card2_021();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_021);
        assertPrintedState(printed, "2_21");
        assertNull(bare.getTextState());
        assertEquals("Adds 2 to power of any starship he pilots. On Gold 2, also adds 1 to maneuver and (when in Death Star: Trench) adds 1 to total of Attack Run.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void TireePrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_21", true);
        int[] printed = pilotedPowers("2_21o", true);
        assertEquals("vehicle: errata text adds 2, printed text adds nothing", bare[0] - 2, printed[0]);
        assertEquals("starship: both texts add 2", bare[1], printed[1]);
        assertEquals(bare[2] + 2, printed[1]);
    }

    @Test
    public void WedgeAntillesPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_023o();
        SwccgCardBlueprint bare = new Card2_023();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_023);
        assertPrintedState(printed, "2_23");
        assertNull(bare.getTextState());
        assertEquals("Adds 3 to power of any starship he pilots. On Red 2, also adds 2 to maneuver and draws one battle destiny if not able to otherwise. May use 1 Force to search your Reserve Deck and take one Corellian Slip into hand. Shuffle, cut and replace.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void WedgeAntillesPrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_23", true);
        int[] printed = pilotedPowers("2_23o", true);
        assertEquals("vehicle: errata text adds 3, printed text adds nothing", bare[0] - 3, printed[0]);
        assertEquals("starship: both texts add 3", bare[1], printed[1]);
        assertEquals(bare[2] + 3, printed[1]);
    }

    @Test
    public void DS614PrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_088o();
        SwccgCardBlueprint bare = new Card2_088();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_088);
        assertPrintedState(printed, "2_88");
        assertNull(bare.getTextState());
        assertEquals("Adds 3 to power of any starship he pilots. On Black 4, also draws one battle destiny if not able to otherwise. May use 1 Force to search through your Reserve Deck and take one Lone Pilot into your hand. Shuffle, cut and replace.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void DS614PrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_88", false);
        int[] printed = pilotedPowers("2_88o", false);
        assertEquals("vehicle: errata text adds 3, printed text adds nothing", bare[0] - 3, printed[0]);
        assertEquals("starship: both texts add 3", bare[1], printed[1]);
        assertEquals(bare[2] + 3, printed[1]);
    }

    @Test
    public void OfficerEvaxPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_098o();
        SwccgCardBlueprint bare = new Card2_098();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_098);
        assertPrintedState(printed, "2_98");
        assertNull(bare.getTextState());
        assertEquals("Adds 2 to power of any starship he pilots. When at a Death Star site, Imperial starships may move to the Death Star system as a 'react.'", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void OfficerEvaxPrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_98", false);
        int[] printed = pilotedPowers("2_98o", false);
        assertEquals("vehicle: errata text adds 2, printed text adds nothing", bare[0] - 2, printed[0]);
        assertEquals("starship: both texts add 2", bare[1], printed[1]);
        assertEquals(bare[2] + 2, printed[1]);
    }

    @Test
    public void ReservePilotPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_103o();
        SwccgCardBlueprint bare = new Card2_103();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_103);
        assertPrintedState(printed, "2_103");
        assertNull(bare.getTextState());
        assertEquals("Adds 1 to power of any starship he pilots. On Black 2, Black 3 or Black 4, also adds 1 to maneuver and draws one battle destiny if not able to otherwise.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void ReservePilotPrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_103", false);
        int[] printed = pilotedPowers("2_103o", false);
        assertEquals("vehicle: errata text adds 1, printed text adds nothing", bare[0] - 1, printed[0]);
        assertEquals("starship: both texts add 1", bare[1], printed[1]);
        assertEquals(bare[2] + 1, printed[1]);
    }

    @Test
    public void RodianPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_104o();
        SwccgCardBlueprint bare = new Card2_104();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_104);
        assertPrintedState(printed, "2_104");
        assertNull(bare.getTextState());
        assertEquals("Your Bounty Hunters and Smugglers are power +1 (but Greedo is power -1) at same site. Adds 1 to power of any starship he pilots.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void RodianPrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_104", false);
        int[] printed = pilotedPowers("2_104o", false);
        assertEquals("vehicle: errata text adds 1, printed text adds nothing", bare[0] - 1, printed[0]);
        assertEquals("starship: both texts add 1", bare[1], printed[1]);
        assertEquals(bare[2] + 1, printed[1]);
    }

    @Test
    public void DanzBorinPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_085o();
        SwccgCardBlueprint bare = new Card2_085();
        assertSameCard(bare, printed);
        assertTrue(printed instanceof Card2_085);
        assertPrintedState(printed, "2_85");
        assertNull(bare.getTextState());
        assertEquals("Adds 3 to power of any starship he pilots. OR If aboard a starship as a passenger, adds 1 to starship's weapon destiny draws.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
    }

    @Test
    public void DanzBorinPrintedAddsPowerOnlyToAStarshipHePilots() {
        int[] bare = pilotedPowers("2_85", false);
        int[] printed = pilotedPowers("2_85o", false);
        assertEquals("vehicle: errata text adds 3, printed text adds nothing", bare[0] - 3, printed[0]);
        assertEquals("starship: both texts add 3", bare[1], printed[1]);
        assertEquals(bare[2] + 3, printed[1]);
    }
}
