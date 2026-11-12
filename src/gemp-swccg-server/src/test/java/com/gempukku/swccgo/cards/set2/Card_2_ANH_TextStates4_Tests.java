package com.gempukku.swccgo.cards.set2;

import com.gempukku.swccgo.cards.set2.dark.Card2_083;
import com.gempukku.swccgo.cards.set2.dark.Card2_083o;
import com.gempukku.swccgo.cards.set2.dark.Card2_114;
import com.gempukku.swccgo.cards.set2.dark.Card2_114o;
import com.gempukku.swccgo.cards.set2.dark.Card2_126;
import com.gempukku.swccgo.cards.set2.dark.Card2_126o;
import com.gempukku.swccgo.cards.set2.light.Card2_019;
import com.gempukku.swccgo.cards.set2.light.Card2_019o;
import com.gempukku.swccgo.cards.set2.light.Card2_026;
import com.gempukku.swccgo.cards.set2.light.Card2_026o;
import com.gempukku.swccgo.cards.set2.light.Card2_028;
import com.gempukku.swccgo.cards.set2.light.Card2_028o;
import com.gempukku.swccgo.cards.set2.light.Card2_036;
import com.gempukku.swccgo.cards.set2.light.Card2_036o;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A New Hope printed text states, part 4 (docs/legacy-versions.md, "Text states"): cards printed "During your control
 * phase, may ...". Bill ruled (12 Nov 2026) that the printed wording had no per-phase limit (nothing in the Premiere /
 * A New Hope rules limited it; the limit came only with the Decipher Glossary 2.0 errata, "Once during each of your
 * control phases"). Each printed state extends the bare card and overrides only that limit, so each test shows the
 * printed state using its action twice in one control phase where the bare card allows it once. Texts from the
 * wiki.swccg.com plain articles (Decipher archive scans).
 */
public class Card_2_ANH_TextStates4_Tests {
    private static final LocalDate GLOSSARY_2_0 = LocalDate.of(1998, 11, 1);
    private static final LocalDate PC_ERRATA_UPPER_BOUND = LocalDate.of(2017, 3, 1);

    private static void assertPrintedState(SwccgCardBlueprint printed, SwccgCardBlueprint bare, String currentId, LocalDate supersededOn) {
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getCardCategory(), printed.getCardCategory());
        assertEquals(bare.getLore(), printed.getLore());
        assertNull(bare.getTextState());
        TextState state = printed.getTextState();
        assertEquals(currentId, state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1996, 7, 1), state.printedOn());
        assertEquals(supersededOn, state.supersededOn());
        assertTrue(bare.getGameText().contains("Once during each of your control phases"));
        assertFalse(printed.getGameText().contains("Once during"));
    }

    private static VirtualTableScenario scenario(Map<String, String> ls, Map<String, String> ds) {
        return new VirtualTableScenario(
                new HashMap<>(ls),
                new HashMap<>(ds),
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    /** Finishes the current action and returns to the given player's control-phase action decision. */
    private static void backToControl(VirtualTableScenario scn, String player) {
        String opponent = player.equals(scn.LS) ? scn.DS : scn.LS;
        for (int i = 0; i < 10 && !scn.DecisionAvailable(player, "Choose Control action or Pass"); i++) {
            if (scn.DecisionAvailable(opponent, "Choose Control action or Pass"))
                scn.PlayerPass(opponent);
            else
                scn.PassAllResponses();
        }
        assertTrue(scn.DecisionAvailable(player, "Choose Control action or Pass"));
        assertEquals(Phase.CONTROL, scn.gameState().getCurrentPhase());
    }


    // Rebel Tech (2_19)

    @Test
    public void RebelTechPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_019o();
        SwccgCardBlueprint bare = new Card2_019();
        assertTrue(printed instanceof Card2_019);
        assertPrintedState(printed, bare, "2_19", PC_ERRATA_UPPER_BOUND);
        assertEquals("When at one of your war rooms: Cumulatively adds 1 to total of Attack Run. OR During your control phase, may send this tech to your Used Pile; search your Reserve Deck and take Death Star: Trench into hand. Shuffle, cut and replace.", printed.getGameText());
    }

    /**
     * Uses the tech once; while its search resolves (the tech is already in the Used Pile as the cost), the same tech is
     * put back on table, and the result says whether it may be used again this control phase.
     */
    private static boolean rebelTechSecondUseAvailable(String techId) {
        var scn = scenario(Map.of("tech", techId, "trench", "2_62"), Map.of());
        var tech = scn.GetLSCard("tech");
        var trench = scn.GetLSCard("trench");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, tech);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(trench);
        scn.LSUseCardAction(tech, "Take card");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, tech);
        scn.MoveCardsToLocation(site, tech);
        scn.ChooseCardBPFromSelection(scn.LS, trench);
        backToControl(scn, scn.LS);
        assertInZone(Zone.HAND, trench);
        assertEquals(site, tech.getAtLocation());
        return scn.LSCardActionAvailable(tech, "Take card");
    }

    /**
     * The printed Rebel Tech has no per-phase limit, but its cost sends the tech to the Used Pile, and a tech that comes
     * back to table is a new card for the bare card's limit too. So the printed and bare texts play the same in GEMP:
     * the printed tech is used, and used again once it is back on table, in one control phase.
     */
    @Test
    public void RebelTechPrintedMayBeUsedAgainInOneControlPhase() {
        assertTrue(rebelTechSecondUseAvailable("2_19o"));
    }

    /** As above for the bare card: the once-per-phase limit cannot stop a second use, because the tech left the table. */
    @Test
    public void RebelTechBarePlaysTheSameBecauseTheCostRemovesTheTech() {
        assertTrue(rebelTechSecondUseAvailable("2_19"));
    }

    // Magnetic Suction Tube (2_26, Light) and (2_114, Dark)

    @Test
    public void MagneticSuctionTubeLightPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_026o();
        SwccgCardBlueprint bare = new Card2_026();
        assertTrue(printed instanceof Card2_026);
        assertPrintedState(printed, bare, "2_26", GLOSSARY_2_0);
        assertEquals("Deploy on your Sandcrawler. During your control phase, may target one character present. Draw destiny. If destiny > character's ability, \"suck up\" character (relocate to related interior Sandcrawler site or owner's Used Pile).", printed.getGameText());
    }

    @Test
    public void MagneticSuctionTubeDarkPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_114o();
        SwccgCardBlueprint bare = new Card2_114();
        assertTrue(printed instanceof Card2_114);
        assertPrintedState(printed, bare, "2_114", PC_ERRATA_UPPER_BOUND);
        assertEquals("Deploy on your Sandcrawler. During your control phase, may target one character present. Draw destiny. If destiny > character's ability, \"suck up\" character (relocate to related interior Sandcrawler site or owner's Used Pile).", printed.getGameText());
    }

    /**
     * Uses the tube on a character (destiny 0, so the character stays) and says whether it may be used again this control
     * phase; when it may, uses it the second time.
     */
    private static boolean suctionTubeSecondUse(String tubeId, boolean light) {
        Map<String, String> mine = Map.of("tube", tubeId, "sandcrawler", light ? "1_150" : "1_309",
                "pilot", light ? "1_3" : "2_103"); // Biggs Darklighter / Reserve Pilot (a device is used by a piloted vehicle)
        Map<String, String> theirs = Map.of("target", light ? "1_168" : "1_19"); // Darth Vader / Luke Skywalker
        var scn = light ? scenario(mine, theirs) : scenario(theirs, mine);
        String me = light ? scn.LS : scn.DS;
        var tube = light ? scn.GetLSCard("tube") : scn.GetDSCard("tube");
        var sandcrawler = light ? scn.GetLSCard("sandcrawler") : scn.GetDSCard("sandcrawler");
        var target = light ? scn.GetDSCard("target") : scn.GetLSCard("target");
        var site = light ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, sandcrawler, target);
        scn.BoardAsPilot(sandcrawler, light ? scn.GetLSCard("pilot") : scn.GetDSCard("pilot"));
        scn.AttachCardsTo(sandcrawler, tube);
        if (light) scn.SkipToLSTurn(Phase.CONTROL); else scn.SkipToDSTurn(Phase.CONTROL);
        for (int use = 1; use <= 2; use++) {
            if (use == 2 && !scn.ActionAvailable(me, tube, "'Suck up' character"))
                return false;
            if (light) scn.PrepareLSDestiny(0); else scn.PrepareDSDestiny(0);
            assertTrue(scn.ActionAvailable(me, tube, "'Suck up' character"));
            if (light) scn.LSUseCardAction(tube, "'Suck up' character"); else scn.DSUseCardAction(tube, "'Suck up' character");
            if (scn.HasCardChoiceAvailable(me, target))
                scn.ChooseCards(me, target);
            backToControl(scn, me);
            assertEquals(site, target.getAtLocation());
        }
        return true;
    }

    @Test
    public void MagneticSuctionTubeLightPrintedMayBeUsedTwiceInOneControlPhase() {
        assertTrue(suctionTubeSecondUse("2_26o", true));
    }

    @Test
    public void MagneticSuctionTubeLightBareMayBeUsedOnceInOneControlPhase() {
        assertFalse(suctionTubeSecondUse("2_26", true));
    }

    @Test
    public void MagneticSuctionTubeDarkPrintedMayBeUsedTwiceInOneControlPhase() {
        assertTrue(suctionTubeSecondUse("2_114o", false));
    }

    @Test
    public void MagneticSuctionTubeDarkBareMayBeUsedOnceInOneControlPhase() {
        assertFalse(suctionTubeSecondUse("2_114", false));
    }

    // Remote (2_28)

    @Test
    public void RemotePrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_028o();
        SwccgCardBlueprint bare = new Card2_028();
        assertTrue(printed instanceof Card2_028);
        assertPrintedState(printed, bare, "2_28", PC_ERRATA_UPPER_BOUND);
        assertEquals("Use 1 Force to deploy at any site. Moves like a character at normal use of the Force. During your control phase, may: Select one character present to be power or forfeit +1 for remainder of turn. OR Use 2 Force to cancel any seeker present.", printed.getGameText());
    }

    /**
     * Makes one character present power +1 and says whether the Remote may be used again this control phase; when it
     * may, makes a second character present power +1. (Two characters, because GEMP does not stack the same card's
     * power modifier on one character.)
     */
    private static boolean remoteSecondUse(String remoteId) {
        var scn = scenario(Map.of("remote", remoteId, "trooper1", "1_28", "trooper2", "1_28"), Map.of());
        var remote = scn.GetLSCard("remote");
        var troopers = new com.gempukku.swccgo.game.PhysicalCardImpl[] {scn.GetLSCard("trooper1"), scn.GetLSCard("trooper2")};
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, remote, troopers[0], troopers[1]);
        scn.SkipToLSTurn(Phase.CONTROL);
        int power = scn.GetPower(troopers[0]);
        for (int use = 1; use <= 2; use++) {
            if (use == 2 && !scn.LSCardActionAvailable(remote, "Make a character power +1"))
                return false;
            var trooper = troopers[use - 1];
            scn.LSUseCardAction(remote, "Make a character power +1");
            scn.LSChooseCard(trooper);
            backToControl(scn, scn.LS);
            assertEquals(power + 1, scn.GetPower(trooper));
        }
        return true;
    }

    @Test
    public void RemotePrintedMayBeUsedTwiceInOneControlPhase() {
        assertTrue(remoteSecondUse("2_28o"));
    }

    @Test
    public void RemoteBareMayBeUsedOnceInOneControlPhase() {
        assertFalse(remoteSecondUse("2_28"));
    }

    // Merc Sunlet (2_36) and Swilla Corey (2_126)

    @Test
    public void MercSunletPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_036o();
        SwccgCardBlueprint bare = new Card2_036();
        assertTrue(printed instanceof Card2_036);
        assertPrintedState(printed, bare, "2_36", GLOSSARY_2_0);
        assertEquals("Deploy on any non-thief to give that character thief skill. During your control phase, may target one device at same site. Draw destiny. If destiny < target's destiny number, it is 'stolen.' OR Deploy on a weapon to prevent theft. (Immune to Alter.)", printed.getGameText());
    }

    @Test
    public void SwillaCoreyPrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_126o();
        SwccgCardBlueprint bare = new Card2_126();
        assertTrue(printed instanceof Card2_126);
        assertPrintedState(printed, bare, "2_126", PC_ERRATA_UPPER_BOUND);
        assertEquals("Deploy on any non-thief to give that character thief skill. During your control phase, may target one device at same site. Draw destiny. If destiny < target's destiny number, it is 'stolen.' OR Deploy on a weapon to prevent theft. (Immune to Alter.)", printed.getGameText());
    }

    /**
     * The thief Effect on your non-thief targets a device on an opponent's character at the same site (destiny 7, so the
     * theft fails) and says whether it may be used again this control phase; when it may, uses it the second time.
     */
    private static boolean thiefSecondUse(String effectId, boolean light) {
        // Light: Merc Sunlet on a Rebel Trooper, opponent's Comlink on a Stormtrooper.
        // Dark: Swilla Corey on a Stormtrooper, opponent's Electrobinoculars on a Rebel Trooper (both warriors).
        Map<String, String> mine = Map.of("effect", effectId, "character", light ? "1_28" : "1_194");
        Map<String, String> theirs = Map.of("device", light ? "1_201" : "1_35", "bearer", light ? "1_194" : "1_28");
        var scn = light ? scenario(mine, theirs) : scenario(theirs, mine);
        String me = light ? scn.LS : scn.DS;
        var effect = light ? scn.GetLSCard("effect") : scn.GetDSCard("effect");
        var character = light ? scn.GetLSCard("character") : scn.GetDSCard("character");
        var device = light ? scn.GetDSCard("device") : scn.GetLSCard("device");
        var bearer = light ? scn.GetDSCard("bearer") : scn.GetLSCard("bearer");
        var site = light ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, character, bearer);
        scn.AttachCardsTo(bearer, device);
        if (light) scn.EnsureLSForcePile(6); else scn.EnsureDSForcePile(6);
        if (light) scn.MoveCardsToLSHand(effect); else scn.MoveCardsToDSHand(effect);
        if (light) scn.SkipToLSTurn(Phase.DEPLOY); else scn.SkipToDSTurn(Phase.DEPLOY);
        if (light) scn.LSPlayCard(effect, "non-thief"); else scn.DSPlayCard(effect, "non-thief");
        scn.ChooseCards(me, character);
        scn.PassAllResponses();
        assertEquals(character, effect.getAttachedTo());
        // Control comes before deploy: use the Effect in the next control phase of the same player.
        if (light) scn.SkipToLSTurn(Phase.CONTROL); else scn.SkipToDSTurn(Phase.CONTROL);
        for (int use = 1; use <= 2; use++) {
            if (use == 2 && !scn.ActionAvailable(me, effect, "Steal device"))
                return false;
            if (light) scn.PrepareLSDestiny(7); else scn.PrepareDSDestiny(7);
            if (light) scn.LSUseCardAction(effect, "Steal device"); else scn.DSUseCardAction(effect, "Steal device");
            if (scn.HasCardChoiceAvailable(me, device))
                scn.ChooseCards(me, device);
            backToControl(scn, me);
            assertEquals(bearer, device.getAttachedTo());
        }
        return true;
    }

    @Test
    public void MercSunletPrintedMayBeUsedTwiceInOneControlPhase() {
        assertTrue(thiefSecondUse("2_36o", true));
    }

    @Test
    public void MercSunletBareMayBeUsedOnceInOneControlPhase() {
        assertFalse(thiefSecondUse("2_36", true));
    }

    @Test
    public void SwillaCoreyPrintedMayBeUsedTwiceInOneControlPhase() {
        assertTrue(thiefSecondUse("2_126o", false));
    }

    @Test
    public void SwillaCoreyBareMayBeUsedOnceInOneControlPhase() {
        assertFalse(thiefSecondUse("2_126", false));
    }

    // Captain Khurgee (2_83)

    @Test
    public void CaptainKhurgeePrintedHasThePrintedText() {
        SwccgCardBlueprint printed = new Card2_083o();
        SwccgCardBlueprint bare = new Card2_083();
        assertTrue(printed instanceof Card2_083);
        assertPrintedState(printed, bare, "2_83", GLOSSARY_2_0);
        assertEquals("During your control phase, you may use 2 Force to search through your Reserve Deck and take one Scanning Crew into your hand. Shuffle deck, cut and replace.", printed.getGameText());
    }

    /** Takes a Scanning Crew for 2 Force and says whether Khurgee may be used again this control phase; when he may, does. */
    private static boolean khurgeeSecondUse(String khurgeeId) {
        var scn = scenario(Map.of(), Map.of("khurgee", khurgeeId, "crew1", "1_266", "crew2", "1_266"));
        var khurgee = scn.GetDSCard("khurgee");
        var crew1 = scn.GetDSCard("crew1");
        var crew2 = scn.GetDSCard("crew2");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, khurgee);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(6);
        scn.MoveCardsToTopOfDSReserveDeck(crew1, crew2);
        int force = scn.GetDSForcePileCount();
        for (int use = 1; use <= 2; use++) {
            if (use == 2 && !scn.DSCardActionAvailable(khurgee, "Take card"))
                return false;
            scn.DSUseCardAction(khurgee, "Take card");
            scn.PassAllResponses();
            scn.ChooseCardBPFromSelection(scn.DS, crew1);
            backToControl(scn, scn.DS);
            int inHand = (crew1.getZone() == Zone.HAND ? 1 : 0) + (crew2.getZone() == Zone.HAND ? 1 : 0);
            assertEquals(use, inHand);
            assertEquals(force - 2 * use, scn.GetDSForcePileCount());
        }
        return true;
    }

    @Test
    public void CaptainKhurgeePrintedMayBeUsedTwiceInOneControlPhase() {
        assertTrue(khurgeeSecondUse("2_83o"));
    }

    @Test
    public void CaptainKhurgeeBareMayBeUsedOnceInOneControlPhase() {
        assertFalse(khurgeeSecondUse("2_83"));
    }
}
