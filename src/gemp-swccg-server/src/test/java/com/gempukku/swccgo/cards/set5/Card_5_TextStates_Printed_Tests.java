package com.gempukku.swccgo.cards.set5;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Cloud City printed text states (docs/legacy-versions.md, "Text states"): each 5_&lt;n&gt;o plays its printed text
 * (wiki.swccg.com plain article, Decipher archive scan), and the bare card is unchanged.
 */
public class Card_5_TextStates_Printed_Tests {

    private static VirtualTableScenario cardScenario(String stateId, String bareId, boolean light) {
        var mine = new HashMap<String, String>() {{
            put("state", stateId);
            put("bare", bareId);
        }};
        var other = new HashMap<String, String>() {{
            put("filler", light ? "1_194" : "1_28");
        }};
        return new VirtualTableScenario(light ? mine : other, light ? other : mine, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    private static void assertSameCard(SwccgCardBlueprint state, SwccgCardBlueprint bare) {
        assertEquals(bare.getTitle(), state.getTitle());
        assertEquals(bare.getUniqueness(), state.getUniqueness());
        assertEquals(bare.getSide(), state.getSide());
        assertEquals(bare.getCardTypes(), state.getCardTypes());
        assertEquals(bare.getExpansionSet(), state.getExpansionSet());
        assertEquals(bare.getRarity(), state.getRarity());
        assertEquals(bare.getDestiny(), state.getDestiny());
        assertEquals(bare.getLore(), state.getLore());
    }

    /**
     * Power of a starship (X-wing / TIE Fighter at Kessel) or vehicle (Snowspeeder / Blizzard Walker at Tatooine:
     * Marketplace) piloted by the given pilot.
     */
    private static int pilotedPower(String pilotId, boolean light, boolean vehicle) {
        var mine = new HashMap<String, String>() {{
            put("pilot", pilotId);
            put("craft", vehicle ? (light ? "3_69" : "3_157") : (light ? "1_146" : "1_304"));
        }};
        var other = new HashMap<String, String>() {{
            put("filler", light ? "1_194" : "1_28");
        }};
        var dsCards = light ? other : mine;
        dsCards.put("kessel", "1_288");
        var scn = new VirtualTableScenario(light ? mine : other, dsCards, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        var pilot = light ? scn.GetLSCard("pilot") : scn.GetDSCard("pilot");
        var craft = light ? scn.GetLSCard("craft") : scn.GetDSCard("craft");
        var kessel = scn.GetDSCard("kessel");
        scn.StartGame();
        if (vehicle) {
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), craft);
        } else {
            scn.MoveLocationToTable(kessel);
            scn.MoveCardsToLocation(kessel, craft);
        }
        scn.BoardAsPilot(craft, pilot);
        return scn.GetPower(craft);
    }

    @Test
    public void AdvantagePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_16o", "5_16", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("If you just won a battle or duel where an opponent's character of ability > 3 was lost, deploy on one of your warriors involved. Warrior is power +2. During each of opponent's move phases, opponent loses 1 Force (2 if character was a Dark Jedi). (Immune to Control.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_16", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void HumanShieldPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_145o", "5_145", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("USED: If an opponent's weapon just 'hit' an Imperial present with a captive being escorted, the captive is 'hit' instead. LOST: During a battle at a site, you may forfeit any or all captives present.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_145", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void PunchItPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_64o", "5_64", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("If Han or your Lando is piloting a starfighter which is defending a battle at a system, add one battle destiny (add two if starfighter is Millennium Falcon). Also, starfighter is immune to attrition for remainder of turn.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_64", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void ImperialTrooperGuardDainsomPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_98o", "5_98", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploys only aboard Executor or on any Death Star site. Cannot move. May cancel opponent's Force drains at adjacent sites.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_98", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void TheEmperorSPrizePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_124o", "5_124", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("If Luke was just 'frozen,' deploy on Emperor Palpatine or Detention Block Corridor. Target Luke and Vader. When reached by targets, place Utinni Effect on Luke and opponent must lose half of Life Force (round down). If Luke released, lose Utinni Effect. (Immune to Alter.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_124", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void BlastedDroidPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_132o", "5_132", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("During your control phase, fire (for free) one of your blasters carried by a trooper or one of your automated weapons. Hit target is immediately lost.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_132", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void LandoCalrissianPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_5o", "5_5", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 2 to power of any starship he pilots (3 if starship is Falcon). Power +1 for every Cloud City location you control. When playing Sabacc, may add 1 to or subtract 1 from your total.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_5", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Lando Calrissian adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void LandoCalrissianPrintedAddsPowerOnlyToStarships() {
        boolean light = true;
        assertEquals(pilotedPower("5_5", light, false), pilotedPower("5_5o", light, false));
        assertEquals(pilotedPower("5_5", light, true) - 2, pilotedPower("5_5o", light, true));
    }
    @Test
    public void CommanderDesannePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_96o", "5_96", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 2 to power of any starship he pilots. Your shuttling, landing and taking off to or from same location is free. During your control phase, may search your Reserve Deck, take any one Lambda shuttle or Landing Craft into hand and reshuffle.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_96", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Commander Desanne adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void CommanderDesannePrintedAddsPowerOnlyToStarships() {
        boolean light = false;
        assertEquals(pilotedPower("5_96", light, false), pilotedPower("5_96o", light, false));
        assertEquals(pilotedPower("5_96", light, true) - 2, pilotedPower("5_96o", light, true));
    }
    @Test
    public void PrincessLeiaPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_7o", "5_7", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploys only on Hoth or Cloud City, but may move elsewhere. Adds 1 to power of any starship she pilots. May deploy (on Hoth or Cloud City) or move as a 'react' to same site as Han or Luke. Immune to attrition < 3.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_7", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Princess Leia adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void PrincessLeiaPrintedAddsPowerOnlyToStarships() {
        boolean light = true;
        assertEquals(pilotedPower("5_7", light, false), pilotedPower("5_7o", light, false));
        assertEquals(pilotedPower("5_7", light, true) - 1, pilotedPower("5_7o", light, true));
    }
    @Test
    public void LandoCalrissianDarkPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_99o", "5_99", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploys only on Cloud City, but may move elsewhere. Adds 2 to power of any starship he pilots. When playing Sabacc, may subtract 1 from or add 1 to your total. If present at a site, can be replaced by opponent with any Light Side Lando.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_99", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Lando Calrissian (Dark) adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void LandoCalrissianDarkPrintedAddsPowerOnlyToStarships() {
        boolean light = false;
        assertEquals(pilotedPower("5_99", light, false), pilotedPower("5_99o", light, false));
        assertEquals(pilotedPower("5_99", light, true) - 2, pilotedPower("5_99o", light, true));
    }
    @Test
    public void ImperialDecreePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_120o", "5_120", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploy on your side of table. Whenever you control any two Rebel Base locations, or any one planet site and two systems, the effects of Revolution and all opponent's Force drain bonuses everywhere are ignored. (Immune to Alter.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_120", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void ArmedAndDangerousPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_32o", "5_32", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("If a battle or duel was just initiated at a site, search your hand or Reserve Deck for one unique weapon matching one of your participating characters. Immediately deploy that weapon (for free) on that character and reshuffle (if from Reserve Deck).", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_32", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void TrevaHormePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_9o", "5_9", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("During your control phase, may 'sell' one non-character card from hand. Opponent must use X Force, where X = destiny of that card or entire Force Pile (opponent's choice). Place sold card on opponent's Used Pile and activate X Force.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_9", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void KebycPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_4o", "5_4", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("When at a site and opponent is losing Force from Force drains at cloud sectors on same planet, lost Force must come from Reserve Deck if possible.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_4", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void ShatteredHopePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_152o", "5_152", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("If you just won a battle at an interior site where you have a character of ability > 3 present, after all forfeiting has occurred, relocate one opponent's character present to an adjacent site. (If on Cloud City, may relocate that character to Weather Vane instead.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_152", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void CarboniteChamberConsolePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_107o", "5_107", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploy on Carbonite Chamber. Adds 3 to Carbon-Freezing destiny. Also, during your turn, you may use 1 Force to search your Reserve Deck; take one Ugnaught, Prepare The Chamber or Carbon-Freezing into hand and reshuffle.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_107", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void BountyPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_113o", "5_113", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploy on a non-droid character. If subsequently captured by a bounty hunter and then transferred to a prison, retrieve Force equal to character's forfeit -2 and place Effect in Used Pile. (Immune to Alter.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_113", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void VaderSBountyPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("5_125o", "5_125", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploy on a Rebel of ability > 2. If subsequently captured by a bounty hunter and then transferred to a prison where Vader is present, retrieve Force equal to character's forfeit (+4 if Luke) and lose Effect. (Immune to Alter.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("5_125", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 11, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Bounty deploys on any non-droid character, so Dark Side may put it on its own Stormtrooper. */
    @Test
    public void BountyPrintedMayDeployOnYourOwnCharacter() {
        assertTrue(mayDeployOnOwnCharacter("5_113o", "1_194"));
        assertFalse(mayDeployOnOwnCharacter("5_113", "1_194"));
    }

    /** Whether Dark Side may deploy the Effect from hand when only one of its own characters is on table. */
    private static boolean mayDeployOnOwnCharacter(String effectId, String ownCharacterId) {
        var scn = new VirtualTableScenario(
                new HashMap<String, String>() {{
                    put("filler", "1_28");
                }},
                new HashMap<String, String>() {{
                    put("effect", effectId);
                    put("mine", ownCharacterId);
                }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        var effect = scn.GetDSCard("effect");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("mine"));
        scn.MoveCardsToDSHand(effect);
        scn.EnsureDSForcePile(10);
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn.DSCardPlayAvailable(effect);
    }

    /** As printed, Carbonite Chamber Console may search the Reserve Deck again in the same turn; the current card once per turn. */
    @Test
    public void CarboniteChamberConsolePrintedHasNoOncePerTurnLimit() {
        assertTrue(consoleSearchAvailableAgain("5_107o"));
        assertFalse(consoleSearchAvailableAgain("5_107"));
    }

    private static boolean consoleSearchAvailableAgain(String consoleId) {
        var scn = new VirtualTableScenario(
                new HashMap<String, String>() {{
                    put("filler", "1_28");
                }},
                new HashMap<String, String>() {{
                    put("console", consoleId);
                    put("chamber", "5_166"); // Cloud City: Carbonite Chamber
                    put("ugnaught1", "5_105");
                    put("ugnaught2", "5_105");
                }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        var console = scn.GetDSCard("console");
        var chamber = scn.GetDSCard("chamber");
        var ugnaught1 = scn.GetDSCard("ugnaught1");
        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.AttachCardsTo(chamber, console);
        scn.MoveCardsToTopOfDSReserveDeck(ugnaught1, scn.GetDSCard("ugnaught2"));
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSUseCardAction(console, "Take card into hand");
        scn.DSChooseCard(ugnaught1);
        assertTrue(scn.GetDSHandCount() > 0);
        return scn.DSCardActionAvailable(console, "Take card into hand");
    }

    /** As printed, Treva Horme may 'sell' a non-character card (here an Effect); the current card sells only Interrupts. */
    @Test
    public void TrevaHormePrintedMaySellANonCharacterCard() {
        assertTrue(trevaMaySellAnEffect("5_9o"));
        assertFalse(trevaMaySellAnEffect("5_9"));
    }

    private static boolean trevaMaySellAnEffect(String trevaId) {
        var scn = new VirtualTableScenario(
                new HashMap<String, String>() {{
                    put("treva", trevaId);
                    put("effect", "1_43"); // Affect Mind, an Effect (not an Interrupt)
                }},
                new HashMap<String, String>() {{
                    put("filler", "1_194");
                }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        var treva = scn.GetLSCard("treva");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), treva);
        scn.MoveCardsToLSHand(scn.GetLSCard("effect"));
        scn.SkipToLSTurn(Phase.CONTROL);
        return scn.LSCardActionAvailable(treva, "Sell");
    }

}
