package com.gempukku.swccgo.cards.textstates;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Printed text states (docs/legacy-versions.md, "Text states") of Reflections II, Tatooine, Coruscant, Reflections III,
 * Theed Palace, Official Tournament Sealed Deck and Enhanced Jabba's Palace cards whose wiki.swccg.com family has an
 * (Errata) or (PC Errata) page. The printed texts are the plain wiki articles (scan-verified); the bare ids keep the
 * errata text. Arleil Schous, Red Squadron X-wing, Black Squadron TIE and Chall Bekan are in Card_106_TextStates_Tests.
 */
public class LateSetsTextStatesTests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    /** id -> {title, printed text (null when the game text is unchanged), printedOn, supersededOn}. */
    private static final Map<String, Object[]> PRINTED = new HashMap<>() {{
        put("106_13", new Object[]{"Dreadnaught-Class Heavy Cruiser", "May add 3 pilots, 6 passengers and 4 TIEs. Has ship-docking capability. Permanent pilots aboard provide total ability of 2. Turbolaser Battery may deploy here. Your TIEs present are each power +1.", LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)});
        put("106_15", new Object[]{"Obsidian Squadron TIE", "Deploy -1 at any cloud sector. Permanent pilot aboard provides ability of 2 and adds 2 to power. Power +2 at non-unique cloud sectors. Boosted TIE Cannon may deploy here and fires free here.", LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)});
        put("110_7", new Object[]{"Dengar With Blaster Carbine", "Adds 2 to power of anything he pilots. Permanent weapon is •Dengar's Blaster Carbine (may target a character, creature or vehicle for free; target hit, and its forfeit = 0, if destiny +1 > defense value; may be fired twice per battle).", LocalDate.of(2000, 1, 1), LocalDate.of(2002, 1, 29)});
        put("11_71", new Object[]{"I Will Find Them Quickly, Master", "Deploy on table. Opponent may deploy Amidala from Reserve Deck; reshuffle. If Maul present with Amidala and opponent has no Jedi here, she is captured. Once per game may take If The Trace Was Correct into hand from Reserve Deck; reshuffle. (Immune to Alter.)", LocalDate.of(2001, 5, 1), LocalDate.of(2017, 6, 1)});
        put("11_88", new Object[]{"There Is No Conflict", "USED: If your Imperial of ability > 2 just won a battle, randomly take into hand one card stacked on I Can Feel The Conflict. (Immune to Sense.) LOST: If Vader in battle, subtract 2 from each of opponent's battle destiny draws.", LocalDate.of(2001, 5, 1), LocalDate.of(2017, 6, 1)});
        put("11_25", new Object[]{"I Did It!", "Deploy on table if you've won a Podrace. Once per game, may place Boonta Eve Podrace out of play to retrieve 4 Force. If you occupy a battleground site and a battleground system, once during each of your control phases may take the bottom card of your Lost Pile and place it on top of your Force Pile. At the end of opponent's turn opponent must lose 2 Force or place their Force Pile onto their Used Pile.", LocalDate.of(2001, 5, 1), LocalDate.of(2017, 6, 1)});
        put("14_66", new Object[]{"Proton Torpedoes", "Use 1 Force to deploy on your N-1 starfighter. May target a starship using 1 Force. Draw destiny. Target hit if destiny > defense value. While on Bravo Fighter and you are attempting to 'blow away' Blockade Flagship, adds 2 to your total.", LocalDate.of(2001, 12, 1), LocalDate.of(2017, 6, 1)});
        put("10_15", new Object[]{"Obi-Wan's Journal", "Deploy on Luke or Obi-Wan. Your characters present armed with a unique (•) lightsaber Weapon card are immune to Disarmed, once per battle may cancel a weapon destiny just drawn, and that lightsaber's Force drain modifiers may not be canceled. Lost if stolen.", LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)});
        put("12_37", new Object[]{"A Tragedy Has Occurred", null, LocalDate.of(2001, 8, 1), LocalDate.of(2017, 6, 1)});
        put("12_128", new Object[]{"Allegations Of Corruption", null, LocalDate.of(2001, 8, 1), LocalDate.of(2017, 6, 1)});
        put("12_131", new Object[]{"Do They Have A Code Clearance?", null, LocalDate.of(2001, 8, 1), LocalDate.of(2017, 6, 1)});
        put("13_29", new Object[]{"Leia, Rebel Princess", "Twice per game, may place top card of Lost Pile on top of Reserve Deck to cancel a Force drain at a related site. Unless opponent's non-alien character present, opponent's total ability at same site = 0. Immune to attrition < 5 if with Luke, Han or Jabba.", LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)});
        put("13_62", new Object[]{"Conduct Your Search", "Deploy on table. Once per turn, you may take a non-Interrupt card with 'door' in title into hand from Reserve Deck; reshuffle. At Endor sites where you have a scout, Rebel scouts are power -1 and forfeit -3. (Immune to Alter.)", LocalDate.of(2001, 10, 31), LocalDate.of(2002, 1, 29)});
        put("13_92", new Object[]{"Thok & Thug", "Deploys -2 to a Jabba's Palace site. If opponent just initiated battle, may use X Force (limit 2) to add twice X to power. Your alien leaders present may not be targeted by weapons. End of your turn: Use 2 Force to maintain OR Lose 1 Force to place in Used Pile OR Place out of play.", LocalDate.of(2001, 10, 31), LocalDate.of(2003, 1, 27)});
    }};

    /** States whose wording change does not change play: the class only sets text and text-state information. */
    private static final String[] SAME_PLAY = {"106_13", "106_15", "110_7", "11_71", "11_88", "11_25", "14_66", "10_15", "12_37", "12_128", "12_131"};

    @Test
    public void PrintedStatesHaveThePrintedTextAndTheBareCardKeepsTheErrata() {
        for (Map.Entry<String, Object[]> entry : PRINTED.entrySet()) {
            String bareId = entry.getKey();
            Object[] data = entry.getValue();
            SwccgCardBlueprint printed = library.getSwccgoCardBlueprint(bareId + "o");
            SwccgCardBlueprint bare = library.getSwccgoCardBlueprint(bareId);
            assertEquals(bareId, printed.getClass().getSuperclass(), bare.getClass());
            assertEquals(bareId, data[0], printed.getTitle());
            if (data[1] != null) {
                assertEquals(bareId, data[1], printed.getGameText());
                assertNotEquals(bareId, data[1], bare.getGameText());
            }
            else {
                assertEquals(bareId, bare.getGameText(), printed.getGameText());
            }
            TextState state = printed.getTextState();
            assertEquals(bareId, bareId, state.currentId());
            assertEquals(bareId, "Printed", state.label());
            assertEquals(bareId, data[2], state.printedOn());
            assertEquals(bareId, data[3], state.supersededOn());
            assertEquals(bareId, GameUtils.getFullName(bare) + " [Printed]", GameUtils.getFullName(printed));
            assertNull(bareId, bare.getTextState());
            // Same stats as the bare card
            assertEquals(bareId, bare.getDestiny(), printed.getDestiny());
            assertEquals(bareId, bare.getSide(), printed.getSide());
            assertEquals(bareId, bare.getUniqueness(), printed.getUniqueness());
        }
    }

    @Test
    public void WordingOnlyStatesPlayLikeTheBareCard() {
        for (String bareId : SAME_PLAY) {
            Class<?> printedClass = library.getSwccgoCardBlueprint(bareId + "o").getClass();
            Method[] overrides = printedClass.getDeclaredMethods();
            assertEquals(bareId + " overrides " + Arrays.toString(overrides), 0, overrides.length);
        }
    }

    @Test
    public void PrintedCoruscantGrabbersHaveNoGrabberIcon() {
        for (String bareId : new String[]{"12_37", "12_128", "12_131"}) {
            assertTrue(bareId, library.getSwccgoCardBlueprint(bareId).hasIcon(Icon.GRABBER));
            assertFalse(bareId, library.getSwccgoCardBlueprint(bareId + "o").hasIcon(Icon.GRABBER));
        }
    }

    private static VirtualTableScenario scenario(Map<String, String> ls, Map<String, String> ds) {
        return new VirtualTableScenario(new HashMap<>(ls), new HashMap<>(ds), 10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes");
    }

    /** Conduct Your Search: printed takes a 'door' card into hand once per turn in any phase; the errata only deploys it during the deploy phase. */
    private static boolean conductYourSearchUsableInControlPhase(String id) {
        var scn = scenario(Map.of("rebel", "1_28"), Map.of("search", id, "door", "1_210")); // Blast Door Controls
        var search = scn.GetDSCard("search");
        var door = scn.GetDSCard("door");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(search);
        scn.MoveCardsToTopOfDSReserveDeck(door);
        scn.SkipToDSTurn(Phase.CONTROL);
        return scn.DSCardActionAvailable(search);
    }

    @Test
    public void ConductYourSearchPrintedTakesDoorCardIntoHandInAnyPhase() {
        assertTrue(conductYourSearchUsableInControlPhase("13_62o"));
    }

    @Test
    public void ConductYourSearchErrataIsNotUsableInControlPhase() {
        assertFalse(conductYourSearchUsableInControlPhase("13_62"));
    }

    /** Leia: printed resets opponent's total ability unless a non-alien is present; the errata, unless one is here (aboard an enclosed vehicle counts). */
    private static float dsAbilityAtSiteWithNonAlienAboardSandcrawler(String leiaId) {
        var scn = scenario(Map.of("leia", leiaId), Map.of("djas", "1_171", "yularen", "1_166", "sandcrawler", "1_309"));
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("leia"), scn.GetDSCard("djas"), scn.GetDSCard("sandcrawler"));
        scn.BoardAsPassenger(scn.GetDSCard("sandcrawler"), scn.GetDSCard("yularen"));
        return scn.game().getModifiersQuerying().getTotalAbilityAtLocation(scn.gameState(), scn.DS, site);
    }

    @Test
    public void LeiaPrintedNeedsANonAlienPresent() {
        assertEquals(0, dsAbilityAtSiteWithNonAlienAboardSandcrawler("13_29o"), 0.01);
    }

    @Test
    public void LeiaErrataAcceptsANonAlienHere() {
        assertTrue(dsAbilityAtSiteWithNonAlienAboardSandcrawler("13_29") > 0);
    }

    /** Thok &amp; Thug: printed may add to his power when opponent initiates a battle anywhere; the errata only at his site. */
    private static boolean thokMayRespondToBattleElsewhere(String thokId) {
        var scn = scenario(Map.of("rebel", "1_28"), Map.of("thok", thokId, "trooper", "1_194"));
        var thok = scn.GetDSCard("thok");
        var battleSite = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(battleSite, scn.GetLSCard("rebel"), scn.GetDSCard("trooper"));
        scn.EnsureDSForcePile(4);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        // Thok & Thug arrive after Dark Side's turn (so no maintenance choice) at the other site
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), thok);
        scn.LSInitiateBattle(battleSite);
        if (scn.DSGetDecision() == null && scn.LSGetDecision() != null)
            scn.LSPass();
        return scn.DSGetDecision() != null && scn.DSCardActionAvailable(thok);
    }

    @Test
    public void ThokAndThugPrintedRespondsToABattleAnywhere() {
        assertTrue(thokMayRespondToBattleElsewhere("13_92o"));
    }

    @Test
    public void ThokAndThugErrataRespondsOnlyAtItsSite() {
        assertFalse(thokMayRespondToBattleElsewhere("13_92"));
    }
}
