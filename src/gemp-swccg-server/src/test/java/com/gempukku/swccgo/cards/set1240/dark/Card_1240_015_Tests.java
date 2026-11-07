package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.GoMissingEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("merc", "1240_15");
                    put("bike", "8_169");
                    put("dewback", "7_312");
                    put("blaster", "1_317"); // Imperial Blaster (DH-17 blaster)
                    put("gall", "9_148");
                    put("trooper", "1_194");
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

    @Test
    public void ScoutMercenaryStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("merc").getBlueprint();

        assertEquals("Scout Mercenary", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScoutMercenaryAddsThreeToPowerOfSpeederBikeShePilots() {
        var scn = GetScenario();
        var merc = scn.GetDSCard("merc");
        var bike = scn.GetDSCard("bike");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bike);
        int base = scn.GetPower(bike);
        scn.BoardAsPilot(bike, merc);
        assertEquals(base + 3, scn.GetPower(bike));
    }

    @Test
    public void ScoutMercenaryMayUseVehicleDefenseValueWhilePiloting() {
        var scn = GetScenario();
        var merc = scn.GetDSCard("merc");
        var bike = scn.GetDSCard("bike");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bike);
        scn.BoardAsPilot(bike, merc);
        assertEquals(scn.GetDefense(bike), scn.GetDefense(merc));
        assertTrue(scn.GetDefense(merc) > 2);
    }

    @Test
    public void ScoutMercenaryMayNotGoMissingFromOpponentsCardWhilePilotingVehicle() {
        var scn = GetScenario();
        var merc = scn.GetDSCard("merc");
        var bike = scn.GetDSCard("bike");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bike, luke);
        scn.BoardAsPilot(bike, merc);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSExecuteAdHocEffect(luke, new GoMissingEffect(new TopLevelGameTextAction(luke, scn.LS, luke.getCardId()), merc));
        scn.PassAllResponses();
        assertFalse(merc.isMissing());
    }

    @Test
    public void ScoutMercenaryMayGoMissingFromYourOwnCardWhilePilotingVehicle() {
        var scn = GetScenario();
        var merc = scn.GetDSCard("merc");
        var bike = scn.GetDSCard("bike");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bike, trooper);
        scn.BoardAsPilot(bike, merc);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSExecuteAdHocEffect(trooper, new GoMissingEffect(new TopLevelGameTextAction(trooper, scn.DS, trooper.getCardId()), merc));
        scn.PassAllResponses();
        assertTrue(merc.isMissing());
    }

    @Test
    public void ScoutMercenaryMayGoMissingWhenNotPilotingVehicle() {
        var scn = GetScenario();
        var merc = scn.GetDSCard("merc");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, merc, luke);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSExecuteAdHocEffect(luke, new GoMissingEffect(new TopLevelGameTextAction(luke, scn.LS, luke.getCardId()), merc));
        scn.PassAllResponses();
        assertTrue(merc.isMissing());
    }

    private void startBattleToWeapons(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl site) {
        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(4);
        scn.DSInitiateBattle(site);
        for (int i = 0; i < 6 && !scn.AwaitingDSWeaponsSegmentActions(); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else scn.DSPass();
        }
    }

    @Test
    public void ScoutMercenaryMayUseOneForceToFireDH17TwiceDuringBattle() {
        var scn = GetScenario();
        var merc = scn.GetDSCard("merc");
        var blaster = scn.GetDSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, merc, luke, leia);
        scn.AttachCardsTo(merc, blaster);

        startBattleToWeapons(scn, site);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(merc, "Fire a DH-17 blaster twice"));

        int forceBefore = scn.GetDSForcePileCount();
        scn.DSUseCardAction(merc, "Fire a DH-17 blaster twice");
        assertTrue(scn.DSChoiceAvailable("Separately"));
        assertTrue(scn.DSChoiceAvailable("Combined"));
        scn.DSChoose("Separately");
        // Only the 1 Force from Scout Mercenary is used; both firings are free.
        for (int i = 0; i < 40; i++) {
            if (scn.DSHasCardChoiceAvailable(luke)) {
                scn.DSChooseCard(luke);
                continue;
            }
            if (scn.AwaitingDSWeaponsSegmentActions() && i > 2) break;
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable()) scn.DSPass();
        }
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void ScoutMercenaryFireTwiceNotAvailableIfGallOnTable() {
        var scn = GetScenario();
        var merc = scn.GetDSCard("merc");
        var blaster = scn.GetDSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var gall = scn.GetDSCard("gall");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(gall);
        scn.MoveCardsToLocation(site, merc, luke);
        scn.AttachCardsTo(merc, blaster);

        startBattleToWeapons(scn, site);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(merc, "Fire a DH-17 blaster twice"));
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
    }
}
