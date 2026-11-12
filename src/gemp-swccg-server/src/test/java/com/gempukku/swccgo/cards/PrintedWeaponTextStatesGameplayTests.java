package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Gameplay of printed (o) weapon text states that play differently from the bare card (docs/legacy-versions.md,
 * "Text states"), and of the FireWeaponActionBuilder options they use:
 * <ul>
 *     <li>characterDefenseValueIsAbility: "Character hit if destiny &gt; ability" (printed) vs "Target hit if destiny &gt;
 *     defense value" (errata; a character's defense value is its ability or armor, whichever is higher), and no
 *     creature targets;</li>
 *     <li>ionHitLosesAllWeaponsAboard: "all weapons aboard target are lost" (printed) vs "all starship weapons deployed
 *     on target" (errata);</li>
 *     <li>hitsCardsAtSiteNotOnlyPresent: Concussion Grenade "at that site" (printed) vs "present at that site".</li>
 * </ul>
 * Each case also runs the bare card to show the builder default is unchanged.
 */
public class PrintedWeaponTextStatesGameplayTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");          // Luke Skywalker, warrior, ability 4
                    put("blaster", "1_152");      // Blaster (errata: destiny > defense value)
                    put("blastero", "1_152o");    // Blaster as printed (destiny > ability)
                    put("rebel", "1_28");         // Rebel Trooper
                    put("rebelblaster", "1_152");
                    put("corvette", "1_140");     // Corellian Corvette, armor 4
                    put("ishi", "6_19");          // Ishi Tib, ability 1, armor 3
                    put("grenade", "3_73");       // Concussion Grenade (errata: "present at that site")
                    put("grenadeo", "3_73o");     // Concussion Grenade as printed ("at that site")
                }},
                new HashMap<>() {{
                    put("boba", "105_4");         // Boba Fett (First Anthology), ability 3, armor 5
                    put("trooper", "1_194");      // Stormtrooper, warrior
                    put("impblaster", "1_317");   // Imperial Blaster (errata)
                    put("impblastero", "1_317o"); // Imperial Blaster as printed
                    put("devastator", "1_301");   // Star Destroyer
                    put("ion", "1_318");          // Ion Cannon (errata)
                    put("iono", "1_318o");        // Ion Cannon as printed
                    put("tatooine", "1_289");     // Tatooine system
                    put("ubrikkian", "1_310");    // Ubrikkian 9000 Z001, transport vehicle
                    put("passenger", "1_194");    // Stormtrooper, destiny 1, aboard the vehicle
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void fireBlasterAtBoba(VirtualTableScenario scn, String blasterName, int destiny) {
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard(blasterName);
        var boba = scn.GetDSCard("boba");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, boba);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(3);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(destiny);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(boba);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
    }

    @Test
    public void PrintedBlasterHitsAnArmoredCharacterWhenDestinyBeatsItsAbility() {
        var scn = GetScenario();
        fireBlasterAtBoba(scn, "blastero", 4); // 4 > ability 3

        assertTrue(scn.GetDSCard("boba").isHit());
    }

    @Test
    public void BareBlasterMissesTheSameArmoredCharacterBecauseItComparesDefenseValue() {
        var scn = GetScenario();
        fireBlasterAtBoba(scn, "blaster", 4); // 4 is not > defense value 5 (armor)

        assertFalse(scn.GetDSCard("boba").isHit());
    }

    private void fireImperialBlasterAtIshiTib(VirtualTableScenario scn, String blasterName) {
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard(blasterName);
        var ishi = scn.GetLSCard("ishi");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, ishi);
        scn.AttachCardsTo(trooper, blaster);
        scn.DSActivateForceCheat(3);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(2);

        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(ishi);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
    }

    @Test
    public void PrintedImperialBlasterHitsByAbility() {
        var scn = GetScenario();
        fireImperialBlasterAtIshiTib(scn, "impblastero"); // 2 > ability 1

        assertTrue(scn.GetLSCard("ishi").isHit());
    }

    @Test
    public void BareImperialBlasterComparesDefenseValue() {
        var scn = GetScenario();
        fireImperialBlasterAtIshiTib(scn, "impblaster"); // 2 is not > defense value 3 (armor)

        assertFalse(scn.GetLSCard("ishi").isHit());
    }

    private void fireIonCannonAtCorvette(VirtualTableScenario scn, String ionName) {
        var devastator = scn.GetDSCard("devastator");
        var ion = scn.GetDSCard(ionName);
        var tatooine = scn.GetDSCard("tatooine");
        var corvette = scn.GetLSCard("corvette");
        var rebel = scn.GetLSCard("rebel");
        var rebelBlaster = scn.GetLSCard("rebelblaster");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator, corvette);
        scn.AttachCardsTo(devastator, ion);
        scn.BoardAsPassenger(corvette, rebel);
        scn.AttachCardsTo(rebel, rebelBlaster);
        scn.DSActivateForceCheat(3);
        scn.DSStartBattleAndSkipToWeaponsSegment(tatooine);
        scn.PrepareDSDestiny(5);

        assertTrue(scn.DSCardActionAvailable(ion, "Fire"));
        scn.DSUseCardAction(ion, "Fire");
        scn.DSChooseCard(corvette);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
    }

    @Test
    public void PrintedIonCannonMakesAllWeaponsAboardTheTargetLost() {
        var scn = GetScenario();
        fireIonCannonAtCorvette(scn, "iono"); // 5 + 2 > armor 4

        Zone zone = scn.GetLSCard("rebelblaster").getZone();
        assertTrue(zone == Zone.LOST_PILE || zone == Zone.TOP_OF_LOST_PILE);
        assertEquals(0, scn.GetArmor(scn.GetLSCard("corvette")), scn.epsilon);
    }

    @Test
    public void BareIonCannonKeepsCharacterWeaponsAboard() {
        var scn = GetScenario();
        fireIonCannonAtCorvette(scn, "ion");

        assertEquals(0, scn.GetArmor(scn.GetLSCard("corvette")), scn.epsilon);
        assertEquals(Zone.ATTACHED, scn.GetLSCard("rebelblaster").getZone());
    }

    @Test
    public void PrintedWeaponsHaveNoTargetOutsideABattle() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blastero");
        var boba = scn.GetDSCard("boba");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, boba);
        scn.AttachCardsTo(luke, blaster);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardActionAvailable(blaster, "Fire"));
    }

    private void throwGrenadeAtVehiclePassenger(VirtualTableScenario scn, String grenadeName) {
        var luke = scn.GetLSCard("luke");
        var grenade = scn.GetLSCard(grenadeName);
        var ubrikkian = scn.GetDSCard("ubrikkian");
        var passenger = scn.GetDSCard("passenger");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, ubrikkian);
        scn.BoardAsPassenger(ubrikkian, passenger);
        scn.AttachCardsTo(luke, grenade);
        scn.LSActivateForceCheat(3);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(1);

        assertTrue(scn.LSCardActionAvailable(grenade));
        scn.LSUseCardAction(grenade);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassWeaponFireWithDestinyDraw();
    }

    private void loseChosenCards(VirtualTableScenario scn) {
        for (int i = 0; i < 5 && scn.LSDecisionAvailable("Choose card to be lost"); i++) {
            if (scn.LSHasCardChoiceAvailable(scn.GetDSCard("passenger")))
                scn.LSChooseCard(scn.GetDSCard("passenger"));
            else
                scn.LSChooseCard(scn.GetLSCard("luke"));
        }
        scn.PassAllResponses();
    }

    @Test
    public void PrintedConcussionGrenadeAlsoHitsCharactersAboardVehiclesAtThatSite() {
        var scn = GetScenario();
        throwGrenadeAtVehiclePassenger(scn, "grenadeo");
        // Luke and the Stormtrooper aboard the Ubrikkian both have destiny 1 and are at the site.
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("passenger")));
        loseChosenCards(scn);

        Zone zone = scn.GetDSCard("passenger").getZone();
        assertTrue(zone.name(), zone == Zone.LOST_PILE || zone == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void BareConcussionGrenadeSparesCharactersAboardVehicles() {
        var scn = GetScenario();
        throwGrenadeAtVehiclePassenger(scn, "grenade");
        assertFalse(scn.LSDecisionAvailable("Choose card to be lost") && scn.LSHasCardChoiceAvailable(scn.GetDSCard("passenger")));
        loseChosenCards(scn);

        Zone zone = scn.GetDSCard("passenger").getZone();
        assertFalse(zone.name(), zone == Zone.LOST_PILE || zone == Zone.TOP_OF_LOST_PILE);
    }
}
