package com.gempukku.swccgo.cards.set6;

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
 * Jabba's Palace printed text states (docs/legacy-versions.md, "Text states"): each 6_&lt;n&gt;o plays its printed text
 * (wiki.swccg.com plain article, Decipher archive scan), and the bare card is unchanged.
 */
public class Card_6_TextStates_Printed_Tests {

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
    public void FozecPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_103o", "6_103", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 2 to power of any starship he pilots. While at a site you control, Imperials are immune to Ke Chu Ke Kukuta? at that site.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_103", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Fozec adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void FozecPrintedAddsPowerOnlyToStarships() {
        boolean light = false;
        assertEquals(pilotedPower("6_103", light, false), pilotedPower("6_103o", light, false));
        assertEquals(pilotedPower("6_103", light, true) - 2, pilotedPower("6_103o", light, true));
    }
    @Test
    public void GailidPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_104o", "6_104", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploys free to same site as Mosep. Adds 2 to power of any starship he pilots. While at Audience Chamber, adds 1 to your Force drains at Jabba's Palace sites.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_104", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Gailid adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void GailidPrintedAddsPowerOnlyToStarships() {
        boolean light = false;
        assertEquals(pilotedPower("6_104", light, false), pilotedPower("6_104o", light, false));
        assertEquals(pilotedPower("6_104", light, true) - 2, pilotedPower("6_104o", light, true));
    }
    @Test
    public void HNemthePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_18o", "6_18", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 2 to power of any starship she pilots. Adds 2 to deploy cost of each opponent's male Imperial when that Imperial is deploying to same or adjacent site.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_18", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, H'nemthe adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void HNemthePrintedAddsPowerOnlyToStarships() {
        boolean light = true;
        assertEquals(pilotedPower("6_18", light, false), pilotedPower("6_18o", light, false));
        assertEquals(pilotedPower("6_18", light, true) - 2, pilotedPower("6_18o", light, true));
    }
    @Test
    public void LeslomyTacemaPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_26o", "6_26", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 3 to power of any starship she pilots. While at Audience Chamber, adds 2 to the power bonus provided by Ellorrs Madak.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_26", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Leslomy Tacema adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void LeslomyTacemaPrintedAddsPowerOnlyToStarships() {
        boolean light = true;
        assertEquals(pilotedPower("6_26", light, false), pilotedPower("6_26o", light, false));
        assertEquals(pilotedPower("6_26", light, true) - 3, pilotedPower("6_26o", light, true));
    }
    @Test
    public void PalejoReshadPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_31o", "6_31", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 2 to power of any starship he pilots. While at Audience Chamber, all your Corellians are power and forfeit +1 (+2 if non-unique) and your Force generation at the Corellia system is +2.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_31", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Palejo Reshad adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void PalejoReshadPrintedAddsPowerOnlyToStarships() {
        boolean light = true;
        assertEquals(pilotedPower("6_31", light, false), pilotedPower("6_31o", light, false));
        assertEquals(pilotedPower("6_31", light, true) - 2, pilotedPower("6_31o", light, true));
    }
    @Test
    public void VulTazaenePrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_45o", "6_45", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 2 to power of any starship he pilots. Twice during battle at same system, may use 2 Force to add 2 to any destiny of 2. If present with Tonnika Sisters, Vul and Tonnika Sisters are lost.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_45", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Vul Tazaene adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void VulTazaenePrintedAddsPowerOnlyToStarships() {
        boolean light = true;
        assertEquals(pilotedPower("6_45", light, false), pilotedPower("6_45o", light, false));
        assertEquals(pilotedPower("6_45", light, true) - 2, pilotedPower("6_45o", light, true));
    }
    @Test
    public void AllWrappedUpPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_141o", "6_141", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploy on your side of table. We Have A Prisoner and Oo-ta Goo-ta, Solo? play for free and are immune to Sense. Also, whenever opponent forfeits a character, your bounty hunter present may capture that character. (Immune to Alter.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_141", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void BaradaPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_96o", "6_96", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploys only to a Jabba's Palace site. Power = 0 at any location other than a Tatooine site. Power +2 at any Jabba's Palace site, while Jabba on table. Your transport vehicles lost from same site may go to your Used Pile rather than your Lost Pile.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_96", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void RaycRyjerdPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_34o", "6_34", true);
        var printed = scn.GetLSCard("state").getBlueprint();
        var bare = scn.GetLSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Adds 2 to power of any starship he pilots (or 4 if trained by Rycar Ryjerd). Any starfighter Rayc pilots is immune to Tallon Roll and is not lost if an asteroid sector is drawn for asteroid destiny.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_34", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Rayc Ryjerd adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void RaycRyjerdPrintedAddsPowerOnlyToStarships() {
        boolean light = true;
        assertEquals(pilotedPower("6_34", light, false), pilotedPower("6_34o", light, false));
        assertEquals(pilotedPower("6_34", light, true) - 2, pilotedPower("6_34o", light, true));
    }
    @Test
    public void KithabaPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_111o", "6_111", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("* Power = 1 + ability of opponent's highest-ability character present. Deploys only on Tatooine. Adds 1 to power of any starship he pilots.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_111", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Kithaba adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void KithabaPrintedAddsPowerOnlyToStarships() {
        boolean light = false;
        assertEquals(pilotedPower("6_111", light, false), pilotedPower("6_111o", light, false));
        assertEquals(pilotedPower("6_111", light, true) - 1, pilotedPower("6_111o", light, true));
    }
    @Test
    public void WooofPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_136o", "6_136", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploys only at Tatooine. Power +2 when defending a battle at a Jabba's Palace site. Adds 2 to power of any starship he pilots (3 if piloting an [Independent] starship or if Jabba is aboard).", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_136", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Wooof adds to the power of a starship it pilots but not of a vehicle; the current card adds to both. */
    @Test
    public void WooofPrintedAddsPowerOnlyToStarships() {
        boolean light = false;
        assertEquals(pilotedPower("6_136", light, false), pilotedPower("6_136o", light, false));
        assertEquals(pilotedPower("6_136", light, true) - 2, pilotedPower("6_136o", light, true));
    }
    @Test
    public void BeedoPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_97o", "6_97", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("* Replaces any male Rodian for free (Rodian goes to Used Pile) or deploys for 3 Force. While at Audience Chamber, all your other Rodians are power +2 and whenever Greedo 'threatens' a smuggler you may add 2 to your destiny draw.", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_97", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    @Test
    public void HuttBountyPrintedHasThePrintedTextAndStats() {
        var scn = cardScenario("6_144o", "6_144", false);
        var printed = scn.GetDSCard("state").getBlueprint();
        var bare = scn.GetDSCard("bare").getBlueprint();
        assertSameCard(printed, bare);
        assertEquals("Deploy on a smuggler, gambler or thief. If subsequently captured by a bounty hunter and then transferred to Jabba's Palace: Dungeon, retrieve Force equal to character's forfeit (+6 if Han) and lose Effect. (Immune to Alter.)", printed.getGameText());
        assertNotEquals(printed.getGameText(), bare.getGameText());
        TextState state = printed.getTextState();
        assertEquals("6_144", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 5, 1), state.printedOn());
        assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        assertNull(bare.getTextState());
        assertTrue(bare.getClass().isInstance(printed));
    }
    /** As printed, Hutt Bounty deploys on any smuggler, gambler or thief, so Dark Side may put it on its own Lando (a gambler). */
    @Test
    public void HuttBountyPrintedMayDeployOnYourOwnGambler() {
        assertTrue(mayDeployOnOwnCharacter("6_144o", "5_99"));
        assertFalse(mayDeployOnOwnCharacter("6_144", "5_99"));
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

    /** As printed, Beedo's power bonus is for your other Rodians only; the current card also adds it to Beedo himself. */
    @Test
    public void BeedoPrintedDoesNotAddPowerToHimself() {
        assertEquals(beedoPowerAtAudienceChamber("6_97") - 2, beedoPowerAtAudienceChamber("6_97o"));
    }

    private static int beedoPowerAtAudienceChamber(String beedoId) {
        var scn = new VirtualTableScenario(
                new HashMap<String, String>() {{
                    put("filler", "1_28");
                }},
                new HashMap<String, String>() {{
                    put("beedo", beedoId);
                    put("audience", "6_162"); // Jabba's Palace: Audience Chamber
                }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        var beedo = scn.GetDSCard("beedo");
        var audience = scn.GetDSCard("audience");
        scn.StartGame();
        scn.MoveLocationToTable(audience);
        scn.MoveCardsToLocation(audience, beedo);
        return scn.GetPower(beedo);
    }

}
