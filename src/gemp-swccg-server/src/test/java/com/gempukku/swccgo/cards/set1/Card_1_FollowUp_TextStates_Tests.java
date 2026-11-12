package com.gempukku.swccgo.cards.set1;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Premiere text states that needed gameplay tests (docs/legacy-versions.md, "Text states"): the printed (o) texts are
 * the plain wiki.swccg.com articles, checked against their Decipher archive scans; the Decipher (d) texts are the
 * (Errata) articles and the Decipher Glossary Version 2.0 / Supplement entries. Each state shows its text and the
 * behaviour its text gives; the bare cards are unchanged.
 */
public class Card_1_FollowUp_TextStates_Tests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void load() {
        library = new SwccgCardBlueprintLibrary();
    }

    private static VirtualTableScenario scenario(Map<String, String> light, Map<String, String> dark) {
        return new VirtualTableScenario(new HashMap<>(light), new HashMap<>(dark), 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    /** Checks the state's text, text-state data, and that it is the same card as the bare id. */
    private static SwccgCardBlueprint assertState(String id, String text, String label, LocalDate printedOn, LocalDate supersededOn) {
        SwccgCardBlueprint state = library.getSwccgoCardBlueprint(id);
        String bareId = TextState.currentIdOf(id);
        SwccgCardBlueprint bare = library.getSwccgoCardBlueprint(bareId);
        assertEquals(text, state.getGameText());
        assertNotEquals(bare.getGameText(), state.getGameText());
        TextState ts = state.getTextState();
        assertEquals(bareId, ts.currentId());
        assertEquals(label, ts.label());
        assertEquals(printedOn, ts.printedOn());
        assertEquals(supersededOn, ts.supersededOn());
        assertNull(bare.getTextState());
        assertEquals(bare.getTitle(), state.getTitle());
        assertEquals(bare.getUniqueness(), state.getUniqueness());
        assertEquals(bare.getSide(), state.getSide());
        assertEquals(bare.getCardTypes(), state.getCardTypes());
        assertEquals(bare.getRarity(), state.getRarity());
        assertEquals(bare.getLore(), state.getLore());
        assertEquals(bare.getDestiny(), state.getDestiny());
        assertEquals(bare.getTitle() + " [" + label + "]", GameUtils.getFullName(state));
        assertTrue(bare.getClass().isInstance(state));
        return state;
    }

    private static final LocalDate PREMIERE = LocalDate.of(1995, 12, 1);
    private static final LocalDate GLOSSARY_2 = LocalDate.of(1998, 11, 1);
    private static final LocalDate SUPPLEMENT = LocalDate.of(2002, 1, 29);
    private static final LocalDate AR_2023 = LocalDate.of(2023, 7, 30);

    // ---------------------------------------------------------------- Narrow Escape (1_98o, 1_98d)

    @Test
    public void NarrowEscapeStatesHaveTheirTexts() {
        assertState("1_98o", "Cancel a battle just initiated at a site where any Rebel of ability > 2 is present, by moving all your characters there away (at normal use of the Force).",
                "Printed", PREMIERE, SUPPLEMENT);
        assertState("1_98d", "If opponent just initiated battle at a site where you have a Rebel of ability > 2 present, move all of your cards with ability there away (using their landspeed at normal use of Force) and cancel battle.",
                "D-E1", SUPPLEMENT, null);
    }

    /**
     * Dark Side initiates a battle against Luke at Chasm Walkway; Light Side plays Narrow Escape. Returns whether the
     * battle was canceled (seen as canceled while it is still being resolved).
     */
    private static boolean narrowEscapeCancels(String id) {
        var scn = scenario(Map.of("card", id, "luke", "1_19"), Map.of("trooper", "1_194", "corridor", "5_173"));
        scn.StartGame();
        var walkway = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetDSCard("corridor"));
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(walkway, luke, scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("card"));
        scn.EnsureLSForcePile(6);
        scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(walkway);
        assertTrue(id, scn.LSCardPlayAvailable(scn.GetLSCard("card"), "Move cards with ability away"));
        scn.LSPlayCard(scn.GetLSCard("card"), "Move cards with ability away");
        boolean canceled = false;
        for (int i = 0; i < 20 && scn.GetCurrentPhase() == Phase.BATTLE; i++) {
            var battle = scn.gameState().getBattleState();
            canceled |= battle != null && battle.isCanceled();
            if (scn.LSHasCardChoiceAvailable(scn.GetDSCard("corridor")))
                scn.LSChooseCard(scn.GetDSCard("corridor"));
            else if (scn.DSAnyDecisionsAvailable() && !scn.DSDecisionAvailable("Choose Battle action"))
                scn.DSPass();
            else if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else
                break;
        }
        assertEquals(id, scn.GetDSCard("corridor"), luke.getAtLocation());
        return canceled;
    }

    @Test
    public void NarrowEscapePrintedAndDecipherCancelTheBattleAndTheCurrentCardDoesNot() {
        assertFalse(narrowEscapeCancels("1_98"));
        assertTrue(narrowEscapeCancels("1_98o"));
        assertTrue(narrowEscapeCancels("1_98d"));
    }

    // ---------------------------------------------------------------- A Disturbance / A Tremor In The Force (no once per game)

    @Test
    public void DisturbanceAndTremorPrintedHaveTheirTexts() {
        assertState("1_208o", "During your deploy phase, 'insert' (face down) into opponent's Reserve Deck. Shuffle. Opponent cuts deck without looking. When Effect reaches top it is immediately lost, but opponent may not activate any more Force that turn. (Immune to Alter.)",
                "Printed", PREMIERE, SUPPLEMENT);
        assertState("1_42o", "During your deploy phase, 'insert' (face down) in opponent's Reserve Deck. Shuffle. Opponent cuts deck without looking. When Effect reaches top it is immediately lost, but opponent may not activate any more Force that turn. (Immune to Alter.)",
                "Printed", PREMIERE, SUPPLEMENT);
    }

    // ---------------------------------------------------------------- Kal'Falnl C'ndros (capital starships only)

    @Test
    public void KalFalnlPrintedHasItsText() {
        assertState("1_15o", "If both players draw one battle destiny involving Kal, and yours is higher, the opponent's destiny is reduced to zero. Landspeed = 3. Adds 2 to power of capital starships she pilots. Legs too long to board starfighters or enclosed vehicles.",
                "Printed", PREMIERE, AR_2023);
    }

    /** Power of a Corellian Corvette at Kessel, or of a Speeder Bike (open vehicle) at Tatooine: Marketplace, piloted by Kal. */
    private static int kalPilotedPower(String id, boolean vehicle) {
        var scn = scenario(Map.of("kal", id, "craft", vehicle ? "8_83" : "1_140"), Map.of("kessel", "1_288"));
        scn.StartGame();
        var craft = scn.GetLSCard("craft");
        if (vehicle) {
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), craft);
        } else {
            scn.MoveLocationToTable(scn.GetDSCard("kessel"));
            scn.MoveCardsToLocation(scn.GetDSCard("kessel"), craft);
        }
        scn.BoardAsPilot(craft, scn.GetLSCard("kal"));
        return scn.GetPower(craft);
    }

    @Test
    public void KalFalnlPrintedAddsPowerOnlyToCapitalStarships() {
        assertEquals(kalPilotedPower("1_15", false), kalPilotedPower("1_15o", false));
        assertEquals(kalPilotedPower("1_15", true) - 2, kalPilotedPower("1_15o", true));
    }

    // ---------------------------------------------------------------- Tusken Raider (1_196o, 1_196d)

    @Test
    public void TuskenRaiderStatesHaveTheirTexts() {
        assertState("1_196o", "*Total power of a group of Tusken Raiders depends on how many are present. One has a total power of 1, two = 3, three = 6, four = 10. Limit of four at one site. Deploy only to Tatooine, but may move elsewhere.",
                "Printed", PREMIERE, GLOSSARY_2);
        assertState("1_196d", "Deploys only on Tatooine. Power = 1, +1/2 for each other non-unique Tusken Raider present (limit three other Tusken Raiders that may add to power).",
                "D-E1", GLOSSARY_2, AR_2023);
    }

    private static VirtualTableScenario tuskens(String id) {
        var dark = new HashMap<String, String>();
        for (int i = 1; i <= 5; i++)
            dark.put("tusken" + i, id);
        var scn = scenario(Map.of("filler", "1_28"), dark);
        scn.StartGame();
        return scn;
    }

    /** Total of the printed powers of n Tusken Raiders present together at Tatooine: Marketplace. */
    private static float groupPower(String id, int n) {
        var scn = tuskens(id);
        float total = 0;
        for (int i = 1; i <= n; i++)
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tusken" + i));
        for (int i = 1; i <= n; i++)
            total += scn.game().getModifiersQuerying().getPower(scn.gameState(), scn.GetDSCard("tusken" + i));
        return total;
    }

    @Test
    public void TuskenRaiderPrintedAndDecipherGroupsHavePower1_3_6_10() {
        for (String id : new String[]{"1_196o", "1_196d"}) {
            assertEquals(id, 1f, groupPower(id, 1), 0.01f);
            assertEquals(id, 3f, groupPower(id, 2), 0.01f);
            assertEquals(id, 6f, groupPower(id, 3), 0.01f);
            assertEquals(id, 10f, groupPower(id, 4), 0.01f);
        }
        // The current card: power +1 if another is present (total power +2 with four is only in battle).
        assertEquals(4f, groupPower("1_196", 2), 0.01f);
        assertEquals(8f, groupPower("1_196", 4), 0.01f);
    }

    /** Whether a fifth Tusken Raider may deploy to Tatooine: Marketplace where four are present. */
    private static boolean fifthMayDeploy(String id) {
        var scn = tuskens(id);
        for (int i = 1; i <= 4; i++)
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tusken" + i));
        scn.MoveCardsToDSHand(scn.GetDSCard("tusken5"));
        scn.EnsureDSForcePile(5);
        scn.SkipToPhase(Phase.DEPLOY);
        return scn.DSDeployAvailable(scn.GetDSCard("tusken5"));
    }

    @Test
    public void TuskenRaiderPrintedHasALimitOfFourAtOneSite() {
        assertTrue(fifthMayDeploy("1_196"));
        assertTrue(fifthMayDeploy("1_196d"));
        assertFalse(fifthMayDeploy("1_196o"));
    }

    // ---------------------------------------------------------------- Fear Will Keep Them In Line (system only)

    @Test
    public void FearWillKeepThemInLinePrintedHasItsText() {
        assertState("1_216o", "Deploy on any capital starship. When that starship is at a system you control, your total power is +1 in battles at related sites.",
                "Printed", PREMIERE, GLOSSARY_2);
    }

    /** Dark Side's total power in a battle at Chasm Walkway with Fear on a Star Destroyer at Bespin or at Clouds. */
    private static int fearTotalPower(String id, boolean sector) {
        var scn = scenario(Map.of("rebel", "1_28"),
                Map.of("fear", id, "isd", "1_302", "trooper", "1_194", "bespin", "5_164", "clouds", "5_174"));
        scn.StartGame();
        var walkway = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("clouds"));
        var isd = scn.GetDSCard("isd");
        scn.MoveCardsToLocation(scn.GetDSCard(sector ? "clouds" : "bespin"), isd);
        scn.AttachCardsTo(isd, scn.GetDSCard("fear"));
        scn.MoveCardsToLocation(walkway, scn.GetDSCard("trooper"), scn.GetLSCard("rebel"));
        scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(walkway);
        scn.PassBattleStartResponses();
        return scn.GetDSTotalPower();
    }

    @Test
    public void FearWillKeepThemInLinePrintedWorksOnlyFromASystem() {
        assertEquals(fearTotalPower("1_216", false), fearTotalPower("1_216o", false));
        assertEquals(fearTotalPower("1_216", true) - 1, fearTotalPower("1_216o", true));
    }

    // ---------------------------------------------------------------- Rebel Planners (related system only)

    @Test
    public void RebelPlannersPrintedHasItsText() {
        assertState("1_60o", "Use 1 Force to deploy at Massassi War Room or any docking bay. Adds X to total power of your starships at the related system, where X = the number of your starships present.",
                "Printed", PREMIERE, GLOSSARY_2);
    }

    /** Light Side's total power in a battle at Bespin or at Clouds, with Rebel Planners at Cloud City: East Platform. */
    private static int plannersTotalPower(String id, boolean sector) {
        var scn = scenario(Map.of("planners", id, "xwing", "1_146", "pilot", "1_27"),
                Map.of("isd", "1_302", "bespin", "5_164", "clouds", "5_174", "platform", "5_169"));
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("clouds"));
        scn.MoveLocationToTable(scn.GetDSCard("platform"));
        scn.AttachCardsTo(scn.GetDSCard("platform"), scn.GetLSCard("planners"));
        var where = scn.GetDSCard(sector ? "clouds" : "bespin");
        var xwing = scn.GetLSCard("xwing");
        scn.MoveCardsToLocation(where, xwing, scn.GetDSCard("isd"));
        scn.BoardAsPilot(xwing, scn.GetLSCard("pilot"));
        scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(where);
        scn.PassBattleStartResponses();
        return scn.GetLSTotalPower();
    }

    @Test
    public void RebelPlannersPrintedAddsOnlyAtTheRelatedSystem() {
        assertEquals(plannersTotalPower("1_60", false), plannersTotalPower("1_60o", false));
        assertEquals(plannersTotalPower("1_60", true) - 1, plannersTotalPower("1_60o", true));
    }

    // ---------------------------------------------------------------- I've Got A Problem Here (system only)

    @Test
    public void IveGotAProblemHerePrintedHasItsText() {
        assertState("1_253o", "Use 1 Force to target an opponent's starfighter at a system where a battle just finished. Draw destiny. Starfighter immediately lost if destiny > maneuver.",
                "Printed", PREMIERE, GLOSSARY_2);
    }

    /** Whether Dark Side may play the card once a battle against a piloted X-wing finishes at Bespin or at Clouds. */
    private static boolean problemPlayableAfterBattle(String id, boolean sector) {
        var scn = scenario(Map.of("xwing", "1_146", "pilot", "1_27"),
                Map.of("card", id, "isd", "1_302", "bespin", "5_164", "clouds", "5_174"));
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("clouds"));
        var where = scn.GetDSCard(sector ? "clouds" : "bespin");
        var xwing = scn.GetLSCard("xwing");
        scn.MoveCardsToLocation(where, xwing, scn.GetDSCard("isd"));
        scn.BoardAsPilot(xwing, scn.GetLSCard("pilot"));
        scn.MoveCardsToDSHand(scn.GetDSCard("card"));
        scn.EnsureDSForcePile(4);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(where);
        scn.PassBattleStartResponses();
        for (int i = 0; i < 40; i++) {
            if (scn.DSDecisionAvailable("just ended"))
                return scn.DSCardPlayAvailable(scn.GetDSCard("card"), "Target opponent's starfighter");
            if (!scn.IsActiveBattle() && scn.GetCurrentPhase() != Phase.BATTLE)
                return false;
            if (scn.AwaitingLSBattleDamagePayment()) {
                scn.LSPayRemainingBattleDamageFromReserveDeck();
                continue;
            }
            if (scn.AwaitingLSAttritionPayment()) {
                scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("pilot"));
                continue;
            }
            if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
        }
        throw new AssertionError("battle did not end");
    }

    @Test
    public void IveGotAProblemHerePrintedPlaysOnlyAfterABattleAtASystem() {
        assertTrue(problemPlayableAfterBattle("1_253", true));
        assertFalse(problemPlayableAfterBattle("1_253o", true));
        assertTrue(problemPlayableAfterBattle("1_253", false));
        assertTrue(problemPlayableAfterBattle("1_253o", false));
    }
}
