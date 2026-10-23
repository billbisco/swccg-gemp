package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("alderaan", "1_121");
                }},
                new HashMap<>() {{
                    put("motti", "1210_3");
                    put("tarkin", "1_179");
                    put("doctrine", "200_113");
                    put("devastator", "1_301");
                    put("deathstar", "2_143");
                    put("detention", "1_284");
                    put("docking", "1_283");
                    put("tatooine", "1_289");
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
    public void AdmiralMottiStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("motti").getBlueprint();

        assertEquals("Admiral Motti, Battlestation Coordinator", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
            add(Keyword.ADMIRAL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.PREMIUM);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AdmiralMottiAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, motti);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void AdmiralMottiSetsDeathStarHyperspeedToTwo() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var deathstar = scn.GetDSCard("deathstar");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        assertEquals(1, scn.GetHyperspeed(deathstar));
        scn.MoveCardsToLocation(site, motti);
        assertEquals(2, scn.GetHyperspeed(deathstar));
    }

    @Test
    public void AdmiralMottiAddsOneTotalPowerAtDeathStarPerDeathStarSite() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var deathstar = scn.GetDSCard("deathstar");
        var detention = scn.GetDSCard("detention");
        var docking = scn.GetDSCard("docking");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveCardsToLocation(deathstar, motti);
        float p0 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), deathstar, scn.DS, false, false);
        scn.MoveLocationToTable(detention);
        float p1 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), deathstar, scn.DS, false, false);
        scn.MoveLocationToTable(docking);
        float p2 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), deathstar, scn.DS, false, false);
        assertEquals(Math.round(p0) + 1, Math.round(p1));
        assertEquals(Math.round(p0) + 2, Math.round(p2));
    }

    @Test
    public void AdmiralMottiAddsOneTotalPowerAtSystemDeathStarOrbitsPerDeathStarSite() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var deathstar = scn.GetDSCard("deathstar");
        var alderaan = scn.GetLSCard("alderaan");
        var detention = scn.GetDSCard("detention");
        var devastator = scn.GetDSCard("devastator");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(alderaan);
        scn.MoveCardsToLocation(site, motti);
        scn.MoveCardsToLocation(alderaan, devastator);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.MOVE);
        scn.DSPlayCard(deathstar);
        scn.DSChoose("2");
        scn.DSChoose("Orbit");
        scn.PassAllResponses();
        assertEquals("Alderaan", deathstar.getSystemOrbited());
        float p0 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), alderaan, scn.DS, false, false);
        scn.MoveLocationToTable(detention);
        float p1 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), alderaan, scn.DS, false, false);
        assertEquals(Math.round(p0) + 1, Math.round(p1));
    }

    @Test
    public void AdmiralMottiDoesNotAddTotalPowerAtUnrelatedLocation() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var deathstar = scn.GetDSCard("deathstar");
        var detention = scn.GetDSCard("detention");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(site, motti);
        float atSite = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), site, scn.DS, false, false);
        assertEquals(3, Math.round(atSite));
    }

    @Test
    public void AdmiralMottiMakesTarkinDoctrineIgnoreParsecLimitWhileWithTarkin() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var tarkin = scn.GetDSCard("tarkin");
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");
        var tatooine = scn.GetDSCard("tatooine");
        var devastator = scn.GetDSCard("devastator");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(site, motti, tarkin);
        scn.MoveCardsToDSHand(doctrine);
        scn.EnsureDSForcePile(4);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(doctrine));
        scn.DSPlayCard(doctrine);
        scn.DSChooseCard(deathstar);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue("Tarkin Doctrine parsec-ignore action missing. decision=" + scn.DSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(doctrine, "Make opponent lose"));
        int lsForceBefore = scn.GetLSForcePileCount();
        scn.DSUseCardAction(doctrine, "Make opponent lose");
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSForcePile());
        scn.PassAllResponses();
        assertEquals(lsForceBefore - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void AdmiralMottiDoesNotMakeTarkinDoctrineIgnoreParsecLimitWithoutTarkin() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");
        var tatooine = scn.GetDSCard("tatooine");
        var devastator = scn.GetDSCard("devastator");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(tatooine);
        scn.AttachCardsTo(deathstar, doctrine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.MoveCardsToLocation(site, motti);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(doctrine, "Make opponent lose"));
    }

    @Test
    public void AdmiralMottiMakesTarkinDoctrineImmuneToAlterWhileWithTarkin() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var tarkin = scn.GetDSCard("tarkin");
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.AttachCardsTo(deathstar, doctrine);
        scn.MoveCardsToLocation(site, motti, tarkin);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), doctrine, Title.Alter));
    }

    @Test
    public void AdmiralMottiDoesNotMakeTarkinDoctrineImmuneToAlterWithoutTarkin() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.AttachCardsTo(deathstar, doctrine);
        scn.MoveCardsToLocation(site, motti);
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), doctrine, Title.Alter));
    }

    @Test
    public void AdmiralMottiImmuneToAttritionLessThanThree() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, motti);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), motti), scn.epsilon);
    }
}
