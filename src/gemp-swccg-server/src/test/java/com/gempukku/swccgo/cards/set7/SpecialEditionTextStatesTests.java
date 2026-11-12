package com.gempukku.swccgo.cards.set7;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Special Edition text states (docs/legacy-versions.md, "Text states"): the as-printed state ({@code o}) of each Special
 * Edition card whose current GEMP text is an erratum. Printed texts are the plain wiki.swccg.com articles (checked
 * against the Decipher scans, Nov 2026); each bare card keeps its current text. Grondorn Muse, Sandspeeder and Sienar
 * Fleet Systems have their own Card_7_NNN_TextStates_Tests.
 */
public class SpecialEditionTextStatesTests {

    /** id -> printed game text (for a location: "DARK|LIGHT", an empty part where that side keeps the bare text). */
    private static final Map<String, String> PRINTED = new HashMap<>() {{
        put("7_4o", "Deploys only on Tatooine. When in a duel, adds 2 to your total. Once per turn, if a battle just ended, may 'revive' (place here from Lost Pile) one character forfeited from same site this turn. Immune to attrition < 5.");
        put("7_12o", "Adds 3 to power of anything he pilots. When piloting Rogue 3, also adds 2 to maneuver and draws one battle destiny if not able to otherwise. May use 2 Force to take one One More Pass into hand from Reserve Deck; reshuffle.");
        put("7_15o", "Cumulatively activates 1 Force each time you deploy a droid (or retrieves 1 Force when you deploy an astromech to a starfighter). Spaceport Speeders may be played at same site (place droid in Used Pile instead of Lost Pile).");
        put("7_37o", "While aboard a capital starship, adds 1 to power and 2 to hyperspeed, and that starship is immune to attrition < 4. While at Central Core, Death Star requires +2 Force to move.");
        put("7_68o", "If you just won a battle in which opponent lost a starship, deploy on your participating starfighter. Once during each of opponent's move phases, opponent loses 1 Force (2 if starfighter is Falcon or Red 5). Also, that starfighter is power +2. (Immune to Control.)");
        put("7_104o", "During your control phase, fire one of your starship weapons (for free). If Han or any gunner is aboard that starship, may add 2 to destiny draw. 'Hit' target is lost. OR If you just fired a weapon in battle, add that weapon's destiny number to your total power.");
        put("7_110o", "|Your spies deploy -1 here and at related sites. If you control, Undercover is immune to Hutt Smooch.");
        put("7_123o", "|Your starships deploy -2 here and are hyperspeed +2 when moving to or from here. Opponent's Force drains here are canceled. You may not Force drain here.");
        put("7_216o", "Deploy on a cloud sector (limit one per sector). Force you activate may be drawn into hand (one per turn for each of your Floating Refineries on table). Each cloud sector or gas miner drawn in this way may be revealed to retrieve 2 Force.");
        put("7_221o", "Deploy on an exterior Tatooine site. Specify starting direction. During your control phase, moves to next adjacent exterior site (reversing direction as necessary). During battle, if your Tusken Raider or bantha here, may add one battle destiny.");
        put("7_236o", "If you just won a battle in which opponent lost a starship, deploy on your participating starfighter. Once during each of opponent's move phases, opponent loses 1 Force (2 if starfighter is a TIE/ln). Also, that starfighter is power +2. (Immune to Control.)");
        put("7_243o", "Deploy on opponent's spy or Rebel leader. If subsequently captured by a bounty hunter or Imperial and then transferred to Detention Block Corridor, retrieve Force equal to character's forfeit (+4 if Leia) and lose Effect. (Immune to Alter.)");
        put("7_254o", "If you have a Star Destroyer in a battle, during the weapons segment use its tractor beam for free. Add 2 to tractor beam destiny if targeting a unique (•) starship. If not captured, target is power and maneuver -3 for remainder of battle.");
        put("7_278o", "Emperor Palpatine deploys free here. If your moff here, all Imperials are deploy -1 at sites.|Force drain +1 here. If you control, Emperor Palpatine may not deploy to Coruscant.");
        put("7_279o", "If you occupy, opponent's Force drains at related locations are canceled.|Opponent's Force drains here are canceled. You may not Force drain here.");
    }};

    private static VirtualTableScenario scenario(Map<String, String> ls, Map<String, String> ds) {
        return new VirtualTableScenario(new HashMap<>(ls), new HashMap<>(ds), 30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes");
    }

    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();

    private static SwccgCardBlueprint blueprint(String id) {
        SwccgCardBlueprint blueprint = LIBRARY.getSwccgoCardBlueprint(id);
        assertNotNull(id, blueprint);
        return blueprint;
    }

    /** Passes pending responses until Light Side has a decision again (diagnostic text otherwise). */
    private static void awaitLS(VirtualTableScenario scn) {
        for (int i = 0; i < 6 && scn.LSGetDecision() == null; i++) {
            if (scn.DSGetDecision() == null)
                break;
            scn.DSPass();
        }
        assertNotNull("Light Side has no decision; Dark Side: " + (scn.DSGetDecision() == null ? null : scn.DSGetDecision().getText()), scn.LSGetDecision());
    }

    @Test
    public void EveryPrintedStateHasThePrintedTextAndTheBareCardsIdentity() {
        for (var entry : PRINTED.entrySet()) {
            String id = entry.getKey();
            String bareId = id.substring(0, id.length() - 1);
            SwccgCardBlueprint card = blueprint(id);
            SwccgCardBlueprint bare = blueprint(bareId);
            String text = entry.getValue();
            if (text.contains("|")) {
                String[] sides = text.split("\\|", -1);
                assertEquals(id, sides[0].isEmpty() ? bare.getLocationDarkSideGameText() : sides[0], card.getLocationDarkSideGameText());
                assertEquals(id, sides[1].isEmpty() ? bare.getLocationLightSideGameText() : sides[1], card.getLocationLightSideGameText());
            } else {
                assertEquals(id, text, card.getGameText());
                assertFalse(id + " differs from the bare text", text.equals(bare.getGameText()));
            }
            assertEquals(id, bare.getTitle(), card.getTitle());
            assertEquals(id, bare.getUniqueness(), card.getUniqueness());
            assertEquals(id, bare.getSide(), card.getSide());
            assertEquals(id, bare.getCardCategory(), card.getCardCategory());
            assertEquals(id, bare.getExpansionSet(), card.getExpansionSet());
            assertEquals(id, bare.getRarity(), card.getRarity());
            assertEquals(id, bare.getLore(), card.getLore());
            TextState state = card.getTextState();
            assertNotNull(id, state);
            assertEquals(id, bareId, state.currentId());
            assertEquals(id, "Printed", state.label());
            assertEquals(id, LocalDate.of(1999, 11, 1), state.printedOn());
            assertEquals(id, bare.getTitle() + " [Printed]", GameUtils.getFullName(card));
            assertNull(bareId, bare.getTextState());
            assertEquals(bareId, bare.getTitle(), GameUtils.getFullName(bare));
        }
    }

    // ---- Sienar Fleet Systems: printed retrieves 1 Force for a TIE squadron too (bare: 3) ----

    private static int sienarRetrievalForSquadron(String sienarId) {
        var scn = scenario(Map.of("trooper", "1_28"), Map.of("sienar", sienarId, "wakeelmui", "7_295", "squadron", "9_172"));
        var sienar = scn.GetDSCard("sienar");
        var wakeelmui = scn.GetDSCard("wakeelmui");
        var squadron = scn.GetDSCard("squadron");
        scn.StartGame();
        scn.MoveLocationToTable(wakeelmui);
        scn.AttachCardsTo(wakeelmui, sienar);
        scn.MoveCardsToDSHand(squadron);
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1), scn.GetDSFiller(2), scn.GetDSFiller(3), scn.GetDSFiller(4), scn.GetDSFiller(5));
        scn.EnsureDSForcePile(10);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int lost = scn.GetDSLostPileCount();
        assertTrue(scn.DSDeployAvailable(squadron));
        scn.DSDeployCard(squadron);
        if (scn.DSHasCardChoiceAvailable(wakeelmui))
            scn.DSChooseCard(wakeelmui);
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, squadron.getZone());
        return lost - scn.GetDSLostPileCount();
    }

    @Test
    public void SienarFleetSystemsPrintedRetrievesOneForceForATIESquadron() {
        assertEquals(1, sienarRetrievalForSquadron("7_241o"));
        assertEquals(3, sienarRetrievalForSquadron("7_241"));
    }

    // ---- Floating Refinery: printed retrieves 2 Force for a revealed cloud sector (bare: 1) ----

    private static int refineryRetrieval(String refineryId) {
        var scn = scenario(Map.of("trooper", "1_28"), Map.of("refinery", refineryId, "clouds", "5_174", "drawn", "5_174", "bespin", "5_164"));
        var refinery = scn.GetDSCard("refinery");
        var clouds = scn.GetDSCard("clouds");
        var drawn = scn.GetDSCard("drawn");
        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(clouds);
        scn.AttachCardsTo(clouds, refinery);
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1), scn.GetDSFiller(2), scn.GetDSFiller(3), scn.GetDSFiller(4));
        scn.MoveCardsToTopOfDSReserveDeck(drawn);
        int lost = scn.GetDSLostPileCount();
        assertTrue(scn.DSActionAvailable("Activate Force"));
        scn.DSChooseAction("Activate Force");
        scn.DSDecided(1);
        assertTrue(scn.DSCardActionAvailable(refinery, "Draw activated Force"));
        scn.DSUseCardAction(refinery, "Draw activated Force");
        scn.PassAllResponses();
        assertEquals(Zone.HAND, drawn.getZone());
        assertTrue(scn.DSDecisionAvailable("reveal"));
        scn.DSChooseYes();
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("revealed"))
            scn.LSDismissRevealedCards();
        scn.PassAllResponses();
        return lost - scn.GetDSLostPileCount();
    }

    @Test
    public void FloatingRefineryPrintedRetrievesTwoForce() {
        assertEquals(2, refineryRetrieval("7_216o"));
        assertEquals(1, refineryRetrieval("7_216"));
    }

    // ---- Rendezvous Point / Dagobah: printed lets opponent initiate a Force drain there and cancels it ----

    private static void assertDrainCanceledOnlyWhenPrinted(String lsLocation, String dsLocation, String printedId) {
        for (boolean printed : new boolean[]{true, false}) {
            Map<String, String> ls = new HashMap<>(Map.of("trooper", "1_28"));
            Map<String, String> ds = new HashMap<>(Map.of("black3", "7_305"));
            String id = printed ? printedId : printedId.substring(0, printedId.length() - 1);
            if (lsLocation != null) ls.put("loc", id); else ds.put("loc", id);
            var scn = scenario(ls, ds);
            var loc = lsLocation != null ? scn.GetLSCard("loc") : scn.GetDSCard("loc");
            var black3 = scn.GetDSCard("black3");
            scn.StartGame();
            scn.MoveLocationToTable(loc);
            scn.MoveCardsToLocation(loc, black3);
            scn.SkipToLSTurn(Phase.DEPLOY);
            scn.SkipToDSTurn(Phase.CONTROL);
            if (!printed) {
                assertFalse(id, scn.DSForceDrainAvailable(loc));
                continue;
            }
            assertTrue(id, scn.DSForceDrainAvailable(loc));
            int lsLife = scn.GetLSLifeForceRemaining();
            scn.DSForceDrainAt(loc);
            scn.PassAllResponses();
            assertFalse(id, scn.IsActiveForceDrain());
            assertEquals(id, lsLife, scn.GetLSLifeForceRemaining());
        }
    }

    @Test
    public void RendezvousPointPrintedCancelsOpponentsForceDrainHere() {
        assertDrainCanceledOnlyWhenPrinted("ls", null, "7_123o");
    }

    @Test
    public void DagobahPrintedCancelsOpponentsForceDrainHere() {
        assertDrainCanceledOnlyWhenPrinted(null, "ds", "7_279o");
    }

    // ---- Ben Kenobi: printed may 'revive' after a battle anywhere ends (bare: only a battle here) ----

    private static boolean benOffersReviveAfterBattleElsewhere(String benId) {
        var scn = scenario(Map.of("ben", benId, "rebel", "1_28"), Map.of("trooper", "1_194"));
        var ben = scn.GetLSCard("ben");
        var rebel = scn.GetLSCard("rebel");
        var trooper = scn.GetDSCard("trooper");
        var benSite = scn.GetLSStartingLocation();
        var battleSite = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(benSite, ben);
        scn.MoveCardsToLocation(battleSite, rebel, trooper);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(battleSite);
        scn.SkipToDamageSegment(false);
        for (int i = 0; i < 20 && scn.IsActiveBattle(); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable()) scn.DSPass();
        }
        for (int i = 0; i < 5 && !scn.LSAnyDecisionsAvailable() && scn.DSAnyDecisionsAvailable(); i++)
            scn.DSPass();
        assertFalse(scn.IsActiveBattle());
        return scn.LSCardActionAvailable(ben, "Revive");
    }

    @Test
    public void BenKenobiPrintedMayReviveAfterABattleElsewhere() {
        assertTrue(benOffersReviveAfterBattleElsewhere("7_4o"));
        assertFalse(benOffersReviveAfterBattleElsewhere("7_4"));
    }

    // ---- Droid Merchant: printed activates 1 Force for every droid, for each Droid Merchant; Spaceport Speeders sends the droid to Used Pile ----

    @Test
    public void DroidMerchantPrintedActivatesForEveryDroidCumulatively() {
        var scn = scenario(Map.of("merchant1", "7_15o", "merchant2", "7_15o", "r5a", "2_15", "r5b", "2_15"), Map.of("trooper", "1_194"));
        var site = scn.GetLSStartingLocation();
        var r5a = scn.GetLSCard("r5a");
        var r5b = scn.GetLSCard("r5b");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("merchant1"), scn.GetLSCard("merchant2"));
        scn.MoveCardsToLSHand(r5a, r5b);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        for (PhysicalCardImpl droid : new PhysicalCardImpl[]{r5a, r5b}) {
            awaitLS(scn);
            int reserve = scn.GetLSReserveDeckCount();
            scn.LSDeployCard(droid);
            if (scn.LSHasCardChoiceAvailable(site))
                scn.LSChooseCard(site);
            scn.PassAllResponses();
            // Two required triggers at once: Light Side picks the order
            for (int i = 0; i < 4 && scn.LSGetDecision() != null && scn.LSGetDecision().getText().contains("Required"); i++) {
                scn.LSDecided("0");
                scn.PassAllResponses();
            }
            assertEquals(Zone.AT_LOCATION, droid.getZone());
            assertEquals("two Droid Merchants each activate 1 Force", reserve - 2, scn.GetLSReserveDeckCount());
        }
    }

    private static Zone droidZoneAfterSpaceportSpeeders(String merchantId) {
        var scn = scenario(Map.of("merchant", merchantId, "r5", "2_15", "speeders", "1_112", "moseisley", "1_133"), Map.of("trooper", "1_194"));
        var site = scn.GetLSCard("moseisley"); // Spaceport Speeders may be played at Mos Eisley with or without Droid Merchant
        var r5 = scn.GetLSCard("r5");
        var speeders = scn.GetLSCard("speeders");
        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetLSCard("merchant"), r5);
        scn.MoveCardsToLSHand(speeders);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue("Spaceport Speeders playable at Mos Eisley", scn.LSCardPlayAvailable(speeders));
        scn.LSPlayCard(speeders);
        if (scn.LSHasCardChoiceAvailable(r5))
            scn.LSChooseCard(r5);
        scn.PassAllResponses();
        return r5.getZone();
    }

    @Test
    public void DroidMerchantPrintedSendsADroidSoldWithSpaceportSpeedersToUsedPile() {
        Zone printed = droidZoneAfterSpaceportSpeeders("7_15o");
        assertTrue(String.valueOf(printed), printed == Zone.USED_PILE || printed == Zone.TOP_OF_USED_PILE);
        Zone bare = droidZoneAfterSpaceportSpeeders("7_15");
        assertTrue(String.valueOf(bare), bare == Zone.LOST_PILE || bare == Zone.TOP_OF_LOST_PILE);
    }

    // ---- Hidden Base: printed (o) and Decipher Glossary Supplement (d) ----

    private static final String HB_D_FRONT = "Deploy Rendezvous Point. Place a planet system (with a parsec number from 1 to 8) from Reserve Deck face down on your side of table (not in play); that card indicates the planet where your \"Hidden Base\" is located. While this side up, once during each of your deploy phases, may deploy one system from Reserve Deck; reshuffle. Opponent loses no more than 1 Force from each of your Force drains at systems and sectors. Flip this card any time after you have deployed five battleground systems and your \"Hidden Base\" system.";
    private static final String HB_O_FRONT = "Deploy Rendezvous Point. Place a planet system (with a parsec number from 1 to 8) from Reserve Deck face down on your side of table (not in play); that card indicates the planet where your \"Hidden Base\" is located. While this side up, once during each of your deploy phases, may deploy one system from Reserve Deck; reshuffle. Opponent loses no more than 1 Force from each of your Force drains at systems and sectors. Flip this card any time after the system matching your \"Hidden Base\" indicator has been deployed.";
    private static final String HB_D_BACK = "While this side up, to draw a card from Force Pile, opponent must first use 1 Force. For each battleground system you control, you may cancel one opponent's Force drain (limit twice per turn). You may not deploy any systems. At each system opponent occupies during any deploy phase, opponent may 'probe' there by placing one card from hand face down beneath that system. Place out of play if \"Hidden Base\" system is 'probed;' Dark Side places 'probe' cards in Used Pile (and may retrieve 1 Force for each Probe Droid used to 'probe').";
    private static final String HB_O_BACK = HB_D_BACK.replace("during any deploy phase", "during any draw phase");

    @Test
    public void HiddenBaseStatesHaveTheirTextsOnBothSides() {
        assertEquals(HB_O_FRONT, blueprint("7_136o").getGameText());
        assertEquals(HB_D_FRONT, blueprint("7_136d").getGameText());
        assertEquals(HB_O_BACK, LIBRARY.getSwccgoCardBlueprintBack("7_136o").getGameText());
        assertEquals(HB_D_BACK, LIBRARY.getSwccgoCardBlueprintBack("7_136d").getGameText());
        assertEquals("Printed", blueprint("7_136o").getTextState().label());
        assertEquals("D-E1", blueprint("7_136d").getTextState().label());
        assertEquals("7_136", blueprint("7_136d").getTextState().currentId());
        assertEquals("7_136_BACK", LIBRARY.getSwccgoCardBlueprintBack("7_136o").getTextState().currentId());
        assertEquals(LocalDate.of(2002, 1, 29), blueprint("7_136d").getTextState().printedOn());
        assertEquals("Hidden Base [D-E1]", GameUtils.getFullName(blueprint("7_136d")));
        assertNull(blueprint("7_136").getTextState());
        assertNull(LIBRARY.getSwccgoCardBlueprintBack("7_136").getTextState());
        assertEquals(blueprint("7_136").getTitle(), blueprint("7_136o").getTitle());
    }

    /** Light Side starts with Hidden Base (state id) and picks Dantooine from the Reserve Deck as its indicator. */
    private static VirtualTableScenario hiddenBaseScenario(String hiddenBaseId) {
        StartingSetup objective = new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("hb", hiddenBaseId);
                    put("rp", "7_123");      // Rendezvous Point
                    put("indicator", "1_122"); // Dantooine (parsec 5), in the Reserve Deck
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
                for (int i = 0; i < 6; i++) {
                    if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("rp")))
                        scn.LSChooseCard(scn.GetLSCard("rp"));
                    else if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("indicator")))
                        scn.LSChooseCard(scn.GetLSCard("indicator"));
                    else if (scn.LSDecisionAvailable("Rendezvous Point"))
                        scn.LSChooseCardIDFromSelection(scn.GetLSCard("rp"));
                    else if (scn.LSDecisionAvailable("Hidden Base"))
                        scn.LSChooseCardIDFromSelection(scn.GetLSCard("indicator"));
                    else
                        break;
                }
            }
        };
        return new VirtualTableScenario(
                new HashMap<>() {{ put("dantooine", "1_122"); put("alderaan", "1_121"); }},
                new HashMap<>() {{ put("black3", "7_305"); }},
                30, 30,
                objective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes");
    }

    @Test
    public void HiddenBaseDecipherErrataTakesItsIndicatorFromReserveDeckAndFlipsAsTheBareCard() {
        var scn = hiddenBaseScenario("7_136d");
        var hb = scn.GetLSCard("hb");
        var indicator = scn.GetLSCard("indicator");
        scn.StartGame();
        // Both Dantooines start in the Reserve Deck; the one chosen is stacked face down as the indicator
        var stacked = scn.gameState().getStackedCards(hb);
        assertEquals(1, stacked.size());
        assertEquals("Dantooine", stacked.get(0).getBlueprint().getTitle());
        var dantooine = stacked.get(0) == indicator ? scn.GetLSCard("dantooine") : indicator;
        scn.MoveCardsToLSHand(dantooine);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        awaitLS(scn);
        scn.LSDeployCard(dantooine);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, dantooine.getZone());
        awaitLS(scn);
        assertFalse("five battleground systems are still needed", scn.LSCardActionAvailable(hb, "Flip"));
    }

    @Test
    public void HiddenBasePrintedFlipsAfterItsSystemIsDeployedAndOpponentProbesInTheDrawPhase() {
        var scn = hiddenBaseScenario("7_136o");
        var hb = scn.GetLSCard("hb");
        var indicator = scn.GetLSCard("indicator");
        scn.StartGame();
        // Both Dantooines start in the Reserve Deck; the one chosen is stacked face down as the indicator
        var stacked = scn.gameState().getStackedCards(hb);
        assertEquals(1, stacked.size());
        assertEquals("Dantooine", stacked.get(0).getBlueprint().getTitle());
        var dantooine = stacked.get(0) == indicator ? scn.GetLSCard("dantooine") : indicator;
        var alderaan = scn.GetLSCard("alderaan");
        scn.MoveCardsToLSHand(dantooine);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        awaitLS(scn);
        assertFalse(scn.LSCardActionAvailable(hb, "Flip"));
        scn.LSDeployCard(dantooine);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, dantooine.getZone());
        awaitLS(scn);
        assertTrue(scn.LSCardActionAvailable(hb, "Flip"));
        scn.LSUseCardAction(hb, "Flip");
        scn.PassAllResponses();
        assertEquals(HB_O_BACK, hb.getBlueprint().getGameText());

        scn.MoveLocationToTable(alderaan);
        scn.MoveCardsToLocation(alderaan, scn.GetDSCard("black3"));
        scn.MoveCardsToDSHand(scn.GetDSFiller(1));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(hb, "'Probe'"));
        scn.SkipToPhase(Phase.DRAW);
        assertTrue(scn.DSCardActionAvailable(hb, "'Probe'"));
    }
}
