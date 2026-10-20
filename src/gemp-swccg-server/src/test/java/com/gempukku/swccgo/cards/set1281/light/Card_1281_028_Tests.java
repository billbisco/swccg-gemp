package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("kyle", "1281_28");
                    put("rifle", "1281_47");
                    put("bravado", "1281_34");
                    put("lando", "601_50");
                    put("rest", "1281_39");
                    put("street", "1281_40");
                    put("narshaddaa", "200_58");
                    put("cantina", "1_128"); // Tatooine: Cantina
                    put("db94", "1_129"); // Tatooine: Docking Bay 94 (adjacent to the Cantina here)
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("zuckuss", "4_107"); // may move for free as a 'react'
                    put("saber", "1_324"); // Vader's Lightsaber: deploys only on Vader
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void KyleKatarnStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("kyle").getBlueprint();

        assertEquals("Kyle Katarn", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(5, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUNDREL);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.KYLE_KATARN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KyleKatarnIsImmuneToAttritionLessThanFourAndFiveIfArmed() {
        var scn = GetScenario();
        var kyle = scn.GetLSCard("kyle");
        var rifle = scn.GetLSCard("rifle");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kyle), scn.epsilon);

        scn.AttachCardsTo(kyle, rifle);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kyle), scn.epsilon);
    }

    @Test
    public void KyleKatarnAddsOneTotalBattleDestinyHerePerScoundrelEffect() {
        var scn = GetScenario();
        var kyle = scn.GetLSCard("kyle");
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, lando, trooper);
        scn.AttachCardsTo(lando, bravado);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(1);
        scn.SkipToPowerSegment();

        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(2, scn.GetLSTotalDestiny());
    }

    @Test
    public void KyleKatarnMakesARegularMoveDuringControlPhaseOncePerGame() {
        var scn = GetScenario();
        var kyle = scn.GetLSCard("kyle");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var narshaddaa = scn.GetLSCard("narshaddaa");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.MoveCardsToLocation(rest, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(kyle, "Make a regular move"));
        scn.LSUseCardAction(kyle, "Make a regular move");
        scn.PassAllResponses();

        assertEquals(street, kyle.getAtLocation());
        assertEquals(Zone.AT_LOCATION, kyle.getZone());

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(kyle, "Make a regular move"));
    }

    private void KyleOrControlUsesStolenWeapon(String userKey, boolean expectFire) {
        var scn = GetScenario();
        var user = scn.GetLSCard(userKey);
        var saber = scn.GetDSCard("saber");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, user, trooper);
        scn.AttachCardsTo(user, saber);
        saber.setOwner(scn.LS); // 'stolen' by the Light Side
        assertTrue(saber.isStolen());
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.IsAttachedTo(user, saber));
        assertEquals(expectFire, scn.LSCardActionAvailable(saber, "Fire"));
    }

    @Test
    public void KyleKatarnMayUseAnyStolenWeapon() {
        KyleOrControlUsesStolenWeapon("kyle", true);
    }

    @Test
    public void OtherCharactersMayNotUseThatStolenWeapon() {
        KyleOrControlUsesStolenWeapon("lando", false);
    }

    private void OpponentReactsToForceDrainAtSite(String drainerKey, boolean expectReact) {
        var scn = GetScenario();
        var drainer = scn.GetLSCard(drainerKey);
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        var zuckuss = scn.GetDSCard("zuckuss");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(cantina, drainer);
        scn.MoveCardsToLocation(db94, zuckuss);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSForceDrainAvailable(cantina));
        scn.LSForceDrainAt(cantina);
        // DS's response window to the Force drain (worded differently when DS has no actions).
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertEquals(expectReact, scn.DSCardActionAvailable(zuckuss, "react"));
    }

    @Test
    public void OpponentMayReactToAForceDrainAtASiteWithoutKyle() {
        OpponentReactsToForceDrainAtSite("lando", true);
    }

    @Test
    public void KyleKatarnOpponentMayNotReactToSameSite() {
        OpponentReactsToForceDrainAtSite("kyle", false);
    }

    /** Battle initiated at Docking Bay 94; Zuckuss (may move as a 'react') waits at the adjacent Cantina. */
    private VirtualTableScenario BattleAdjacentToZuckuss(String bystanderKey) {
        var scn = GetScenario();
        var bystander = bystanderKey == null ? scn.GetLSFiller(1) : scn.GetLSCard(bystanderKey);
        var lando = scn.GetLSCard("lando");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        var zuckuss = scn.GetDSCard("zuckuss");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(cantina, bystander, zuckuss);
        scn.MoveCardsToLocation(db94, lando, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(db94);
        assertTrue(scn.IsBattleStarted());
        return scn;
    }

    @Test
    public void OpponentMayReactFromASiteWithoutKyle() {
        var scn = BattleAdjacentToZuckuss(null);
        assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("zuckuss"), "react"));
    }

    @Test
    public void KyleKatarnOpponentMayNotReactFromSameSite() {
        var scn = BattleAdjacentToZuckuss("kyle");
        // DS has no 'react' (its only response), so the battle goes straight on to the weapons segment.
        assertFalse(scn.DSDecisionAvailable("Battle just initiated"));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertEquals(scn.GetLSCard("cantina"), scn.GetDSCard("zuckuss").getAtLocation());
    }
}
