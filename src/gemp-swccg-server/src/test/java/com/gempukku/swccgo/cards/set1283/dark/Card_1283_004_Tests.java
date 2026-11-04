package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("saber", "9_90"); // Luke's Lightsaber
                }},
                new HashMap<>() {{
                    put("luuke", "1283_4");
                    put("tie", "1_304"); // TIE Fighter, power 1
                    put("tatooine", "1_289"); // Tatooine system
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
    public void LuukeStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("luuke").getBlueprint();
        assertEquals("Luuke", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(5, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.SITH);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ASSASSIN);
            add(Keyword.CLONE_TROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SITH);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LuukeAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var luuke = scn.GetDSCard("luuke");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetDSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, luuke);
        assertEquals(3, scn.GetPower(tie));
    }

    private void battleWithLuukeAndLuke(VirtualTableScenario scn) {
        var luuke = scn.GetDSCard("luuke");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, luuke, luke);
        scn.MoveCardsToTopOfLSLostPile(saber);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
    }

    private void stealSaber(VirtualTableScenario scn) {
        var luuke = scn.GetDSCard("luuke");
        var saber = scn.GetLSCard("saber");
        assertTrue(scn.DSCardActionAvailable(luuke, "Steal lightsaber from Lost Pile"));
        scn.DSUseCardAction(luuke, "Steal lightsaber from Lost Pile");
        scn.DSChooseCard(saber);
        scn.PassAllResponses();
    }

    @Test
    public void LuukeMayStealALightsaberFromOpponentsLostPileDuringBattleAndUseIt() {
        var scn = GetScenario();
        var luuke = scn.GetDSCard("luuke");
        var saber = scn.GetLSCard("saber");
        battleWithLuukeAndLuke(scn);
        stealSaber(scn);
        assertTrue(scn.IsAttachedTo(luuke, saber));
        assertTrue(saber.isStolen());
        assertTrue(scn.game().getModifiersQuerying().grantedToUseWeapon(scn.game().getGameState(), luuke, saber));
    }

    @Test
    public void LuukeMayNotStealALightsaberOutsideBattle() {
        var scn = GetScenario();
        var luuke = scn.GetDSCard("luuke");
        var saber = scn.GetLSCard("saber");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), luuke);
        scn.MoveCardsToTopOfLSLostPile(saber);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(luuke, "Steal lightsaber from Lost Pile"));
    }

    @Test
    public void LuukeReturnsStolenLightsaberToOpponentsLostPileAtEndOfBattle() {
        var scn = GetScenario();
        var luuke = scn.GetDSCard("luuke");
        var saber = scn.GetLSCard("saber");
        battleWithLuukeAndLuke(scn);
        stealSaber(scn);
        assertTrue(scn.IsAttachedTo(luuke, saber));
        scn.SkipToDamageSegment();
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassAllResponses();
        while (scn.LSAnyDecisionsAvailable() && scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
            scn.PassAllResponses();
        }
        assertFalse(scn.IsActiveBattle());
        assertEquals(saber, scn.GetTopOfLSLostPile());
        assertFalse(scn.IsAttachedTo(luuke, saber));
        assertInZone(Zone.AT_LOCATION, luuke);
    }

    @Test
    public void LuukeReturnsStolenLightsaberToOpponentsLostPileIfItIsAboutToLeaveTable() {
        var scn = GetScenario();
        var luuke = scn.GetDSCard("luuke");
        var saber = scn.GetLSCard("saber");
        battleWithLuukeAndLuke(scn);
        stealSaber(scn);
        assertTrue(scn.IsAttachedTo(luuke, saber));
        if (!scn.DSAnyDecisionsAvailable()) {
            scn.LSPass();
        }
        scn.DSExecuteAdHocEffect(luuke, new LoseCardFromTableEffect(new TopLevelGameTextAction(luuke, luuke.getCardId()), luuke));
        scn.PassAllResponses();
        assertEquals(saber, scn.GetTopOfLSLostPile());
        assertFalse(scn.GetDSLostPile().contains(saber));
    }

    @Test
    public void LuukeMayUseTwoForceToCancelLukesGameTextHere() {
        var scn = GetScenario();
        var luuke = scn.GetDSCard("luuke");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), luuke, luke);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forcePile = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(luuke, "Cancel Luke's game text"));
        scn.DSUseCardAction(luuke, "Cancel Luke's game text");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();

        assertTrue(luke.isGameTextCanceled());
        assertEquals(forcePile - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void LuukeMayNotCancelLukesGameTextElsewhere() {
        var scn = GetScenario();
        var luuke = scn.GetDSCard("luuke");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), luuke);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(luuke, "Cancel Luke's game text"));
    }
}
