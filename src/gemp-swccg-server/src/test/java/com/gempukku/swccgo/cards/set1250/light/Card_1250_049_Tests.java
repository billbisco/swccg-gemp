package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_049_Tests {
    private static final String DOWNLOAD = "Deploy a weapon here from Reserve Deck";
    private static final String REDUCE = "Place your weapon in Used Pile to reduce non-vehicle weapon destiny";

    private VirtualTableScenario scenario() {
        StartingSetup objectiveOutsideDeck = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>() {{ put("objective", "13_46"); }};
            }
            @Override public void Setup(VirtualTableScenario s) { }
        };
        return new VirtualTableScenario(new HashMap<>() {{
            put("plo", "1250_49"); put("yoda", "4_2"); put("ship", "1_140"); put("system", "1_135");
            put("speeder", "3_69"); put("cost", "1_153"); put("rifle", "1_153");
            put("warrior", "1_28"); put("madakor", "12_1"); put("otherWarrior", "1_28");
            put("otherWeapon", "1_153"); put("nonWeapon", "1_5");
        }}, new HashMap<>() {{
            put("trooper", "1_194"); put("yularen", "1_166"); put("rifle", "1_312");
            put("scout", "8_175"); put("cannon", "8_178");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                objectiveOutsideDeck, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void PloKoonMetadataMatchesPrintedJediMaster() {
        var s = scenario(); var bp = s.GetLSCard("plo").getBlueprint();
        assertEquals("Plo Koon", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(2, bp.getDestiny(), s.epsilon);
        assertEquals(6, bp.getDeployCost(), s.epsilon); assertEquals(5, bp.getPower(), s.epsilon);
        assertEquals(7, bp.getAbility(), s.epsilon); assertEquals(5, bp.getForfeit(), s.epsilon);
        assertEquals(Species.KEL_DOR, bp.getSpecies());
        assertTrue(bp.getPersonas().contains(Persona.PLO));
        assertEquals("Kel Dor Jedi Council member descended from a long line of Jedi. Known for exceptional sensory skills, Plo Koon's control of the Force is unmatched by most.", bp.getLore());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.JEDI_MASTER)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.JEDI_MASTER, Icon.CORUSCANT,
                Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.JEDI_COUNCIL_MEMBER)));
    }

    @Test
    public void PloKoonAddsTwoPowerWhilePilotingShipButNotAsPassenger() {
        var s = scenario(); s.StartGame(); var system = s.GetLSCard("system");
        var plo = s.GetLSCard("plo"); var ship = s.GetLSCard("ship");
        s.MoveLocationToTable(system); s.MoveCardsToLocation(system, ship);
        int normal = s.GetPower(ship);
        s.BoardAsPassenger(ship, plo); assertEquals(normal, s.GetPower(ship), s.epsilon);
        s.MoveCardsToLocation(system, plo);
        s.BoardAsPilot(ship, plo); assertEquals(normal + 2, s.GetPower(ship), s.epsilon);
    }

    @Test
    public void PloKoonAddsTwoPowerWhileDrivingVehicle() {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation();
        var speeder = s.GetLSCard("speeder"); var plo = s.GetLSCard("plo");
        s.MoveCardsToLocation(site, speeder, plo);
        int normal = s.GetPower(speeder);
        s.BoardAsPilot(speeder, plo); assertEquals(normal + 2, s.GetPower(speeder), s.epsilon);
        s.MoveCardsToLocation(site, plo); assertEquals(normal, s.GetPower(speeder), s.epsilon);
    }

    private boolean action(VirtualTableScenario s, String text) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("plo"), text);
    }

    private List<String> messages(VirtualTableScenario s) {
        List<String> recorded = new ArrayList<>();
        GameStateListener listener = (GameStateListener) Proxy.newProxyInstance(GameStateListener.class.getClassLoader(),
                new Class<?>[]{GameStateListener.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "sendMessage": recorded.add((String) args[0]); return null;
                        case "hashCode": return System.identityHashCode(proxy);
                        case "equals": return proxy == args[0];
                        case "toString": return "Plo Koon message recorder";
                    }
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                });
        s.game().addGameStateListener(s.LS, listener); recorded.clear();
        return recorded;
    }

    private VirtualTableScenario download(boolean darkTurn) {
        var s = scenario(); s.StartGame();
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("plo"), s.GetLSCard("warrior"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("otherWarrior"));
        if (darkTurn) s.SkipToDSTurn(Phase.CONTROL); else s.SkipToLSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("rifle"), s.GetLSCard("nonWeapon"));
        if (darkTurn) {
            s.LSActivateForceCheat(4);
            if (s.DSAnyDecisionsAvailable()) s.DSPass();
        } else if (s.GetLSForcePileCount() < 2) {
            s.LSActivateForceCheat(2);
        }
        return s;
    }

    private void deployWeapon(VirtualTableScenario s, boolean assertChoices) {
        var rifle = s.GetLSCard("rifle"); var plo = s.GetLSCard("plo");
        int force = s.GetLSForcePileCount();
        assertTrue(action(s, DOWNLOAD)); s.LSUseCardAction(plo, DOWNLOAD); s.PassAllResponses();
        assertTrue(s.LSDecisionAvailable("to deploy from Reserve Deck"));
        if (assertChoices) {
            int non = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard("nonWeapon").getBlueprintId(true));
            assertTrue(non >= 0); assertEquals("false", s.LSGetADParam("selectable")[non]);
        }
        s.LSChooseCard(rifle); s.PassAllResponses();
        assertTrue(s.LSHasCardChoiceAvailable(plo));
        assertTrue(s.LSHasCardChoiceAvailable(s.GetLSCard("warrior")));
        assertFalse(s.LSHasCardChoiceAvailable(s.GetLSCard("otherWarrior")));
        s.LSChooseCard(plo); s.PassAllResponses();
        assertEquals(plo, rifle.getAttachedTo());
        assertEquals(force - 2, s.GetLSForcePileCount());
    }

    @Test
    public void PloKoonDownloadsOnlyWeaponHerePaysNormalCostAndReshuffles() {
        var s = download(false); var log = messages(s);
        deployWeapon(s, true);
        assertEquals(1, log.stream().filter(m -> m.startsWith(s.LS + " shuffles Reserve Deck")).count());
    }

    @Test
    public void PloKoonCanDownloadDuringOpponentsControlPhase() {
        deployWeapon(download(true), false);
    }

    @Test
    public void PloKoonWeaponDownloadDoesNotRefreshOnLaterTurn() {
        var s = download(false); deployWeapon(s, false);
        int old = s.gameState().getPlayersLatestTurnNumber(s.LS);
        for (int i = 0; i < 200; i++) {
            if (s.GetCurrentPlayer().equals(s.LS) && s.gameState().getPlayersLatestTurnNumber(s.LS) == old + 1
                    && s.GetCurrentPhase() == Phase.CONTROL) break;
            String prompt = s.GetCurrentDecision().getText();
            if (prompt.equals("You have not activated Force. Do you want to Pass?")) {
                if (s.DSDecisionAvailable(prompt)) s.DSChooseYes(); else s.LSChooseYes();
            } else {
                assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                        || prompt.contains("RECIRCULATED"));
                if (s.DSDecisionAvailable(prompt)) s.DSPass(); else s.LSPass();
            }
        }
        assertEquals(old + 1, s.gameState().getPlayersLatestTurnNumber(s.LS));
        assertEquals(Phase.CONTROL, s.GetCurrentPhase());
        assertTrue(s.AwaitingLSControlPhaseActions());
        assertFalse(action(s, DOWNLOAD));
    }

    private VirtualTableScenario restrictedBattle(boolean piloting, boolean yodaRestriction) {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation();
        var plo = s.GetLSCard("plo"); var speeder = s.GetLSCard("speeder");
        s.MoveCardsToLocation(site, plo, speeder, s.GetLSCard("warrior"), s.GetDSCard("trooper"));
        if (piloting) s.BoardAsPilot(speeder, plo); else s.BoardAsPassenger(speeder, plo);
        var objective = s.GetLSCard("objective"); s.MoveCardsToLSSideOfTable(objective);
        objective.setObjectiveDeploymentComplete(true); s.gameState().flipCard(s.game(), objective, true);
        assertEquals("Duel Of The Fates", objective.getBlueprint().getTitle());
        if (yodaRestriction) s.MoveCardsToLocation(site, s.GetLSCard("yoda"));
        s.SkipToLSTurn(Phase.BATTLE);
        return s;
    }

    @Test
    public void PloKoonPilotingCanActuallyInitiateBattleAgainstObjectiveRestriction() {
        var s = restrictedBattle(true, false); var site = s.GetLSStartingLocation();
        assertTrue(s.LSCanInitiateBattle(site)); s.LSInitiateBattle(site); s.PassAllResponses();
        assertTrue(s.IsActiveBattle()); assertEquals(site, s.GetBattleLocation());
    }

    @Test
    public void PloKoonPassengerCannotIgnoreObjectiveBattleRestriction() {
        var s = restrictedBattle(false, false); assertFalse(s.LSCanInitiateBattle(s.GetLSStartingLocation()));
    }

    @Test
    public void PloKoonPilotingCannotIgnoreYodasNonObjectiveRestriction() {
        var s = restrictedBattle(true, true); assertFalse(s.LSCanInitiateBattle(s.GetLSStartingLocation()));
    }

    private VirtualTableScenario weapons(boolean darkTurn, boolean costHere, boolean ploHere, boolean vehicle) {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation();
        var other = s.GetDSStartingLocation();
        s.MoveCardsToLocation(ploHere ? site : other, s.GetLSCard("plo"));
        s.MoveCardsToLocation(costHere ? site : other, s.GetLSCard("warrior"));
        s.MoveCardsToLocation(site, s.GetLSCard("madakor"), s.GetDSCard("trooper"), s.GetDSCard("yularen"));
        s.AttachCardsTo(s.GetLSCard("warrior"), s.GetLSCard("cost"));
        if (ploHere) s.AttachCardsTo(s.GetLSCard("plo"), s.GetLSCard("rifle"));
        s.AttachCardsTo(s.GetDSCard("trooper"), s.GetDSCard("rifle"));
        if (vehicle) {
            s.MoveCardsToLocation(site, s.GetDSCard("scout"));
            s.AttachCardsTo(s.GetDSCard("scout"), s.GetDSCard("cannon"));
        }
        if (darkTurn) {
            s.SkipToDSTurn(Phase.BATTLE); s.DSActivateForceCheat(4); s.DSInitiateBattle(site);
        } else {
            s.SkipToLSTurn(Phase.BATTLE); s.LSActivateForceCheat(4); s.LSInitiateBattle(site);
        }
        return s;
    }

    private boolean offer(VirtualTableScenario s) {
        for (int i = 0; i < 24; i++) {
            if (action(s, REDUCE)) return true;
            if (s.DSDecisionAvailable("optional")) s.DSPass();
            else if (s.LSDecisionAvailable("optional")) s.LSPass();
            else return false;
        }
        return false;
    }

    private void payWeapon(VirtualTableScenario s) {
        assertTrue(offer(s)); s.LSUseCardAction(s.GetLSCard("plo"), REDUCE);
        if (s.LSDecisionAvailable("Choose your weapon here")) {
            assertTrue(s.LSHasCardChoiceAvailable(s.GetLSCard("cost")));
            assertFalse(s.LSHasCardChoiceAvailable(s.GetDSCard("rifle")));
            s.LSChooseCard(s.GetLSCard("cost"));
        }
        s.PassAllResponses(); assertTrue(s.GetLSUsedPile().contains(s.GetLSCard("cost")));
        assertFalse(s.GetLSLostPile().contains(s.GetLSCard("cost")));
    }

    private void lightShot(VirtualTableScenario s, boolean expectHit) {
        var rifle = s.GetLSCard("rifle"); var target = s.GetDSCard("yularen");
        s.PrepareLSDestiny(2);
        assertTrue(s.LSCardActionAvailable(rifle, "Fire")); s.LSUseCardAction(rifle, "Fire");
        s.LSChooseCard(target); s.PassWeaponFireWithDestinyDraw(); s.PassAllResponses();
        assertEquals(expectHit, target.isHit());
    }

    private void darkShot(VirtualTableScenario s, boolean vehicle, boolean expectHit) {
        var weapon = s.GetDSCard(vehicle ? "cannon" : "rifle"); var target = s.GetLSCard("madakor");
        s.PrepareDSDestiny(2);
        if (s.AwaitingLSWeaponsSegmentActions()) s.LSPass();
        assertTrue(s.DSCardActionAvailable(weapon, "Fire")); s.DSUseCardAction(weapon, "Fire");
        s.DSChooseCard(target); s.PassWeaponFireWithDestinyDraw(); s.PassAllResponses();
        assertEquals(expectHit, target.isHit());
    }

    @Test
    public void PloKoonSacrificeReducesLightNonVehicleWeaponAtStrictHitBoundary() {
        var s = weapons(false, true, true, false); payWeapon(s); lightShot(s, false);
    }

    @Test
    public void PloKoonMayDeclineAndLightWeaponUsesUnmodifiedDestiny() {
        var s = weapons(false, true, true, false); assertTrue(offer(s));
        s.LSPass(); s.PassAllResponses();
        assertFalse(s.GetLSUsedPile().contains(s.GetLSCard("cost"))); lightShot(s, true);
    }

    @Test
    public void PloKoonCanSacrificeDuringOpponentsBattleAndReducesDarkWeaponToo() {
        var s = weapons(true, true, true, false); payWeapon(s); darkShot(s, false, false);
    }

    @Test
    public void PloKoonLeavesVehicleWeaponDestinyUnchanged() {
        var s = weapons(true, true, true, true); payWeapon(s); darkShot(s, true, true);
    }

    @Test
    public void PloKoonCannotSacrificeWhenBattleIsAtAnotherSite() {
        var s = weapons(false, true, false, false); assertFalse(offer(s));
        assertFalse(s.GetLSUsedPile().contains(s.GetLSCard("cost")));
    }
}
