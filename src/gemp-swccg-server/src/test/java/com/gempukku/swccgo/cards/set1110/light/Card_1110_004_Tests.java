package com.gempukku.swccgo.cards.set1110.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1110_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1110_4");
                    put("blaster", "1_154");
                    put("falcon", "1_143");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("boba", "5_91");
                    put("stormtrooper", "1_194");
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
    public void HanSoloStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("han").getBlueprint();

        assertEquals("Han Solo", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
            add(Keyword.GAMBLER);
            add(Keyword.PIRATE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HanSoloAddsFourToPowerOfFalconHePilots() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, falcon);
        scn.BoardAsPilot(falcon, han);
        assertEquals(7, scn.GetPower(falcon));
    }

    @Test
    public void HanSoloMakesCardsWithHanInTitleDestinyPlusThree() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.MoveCardsToLSHand(blaster);
        assertEquals(5, scn.GetDestiny(blaster));
        assertEquals(4, scn.GetDestiny(han));
    }

    @Test
    public void HanSoloCancelsBobaFettsGameTextForRemainderOfTurn() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var boba = scn.GetDSCard("boba");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, boba);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(han, "Cancel Boba Fett or Jabba's game text"));
        scn.LSUseCardAction(han, "Cancel Boba Fett or Jabba's game text");
        scn.LSChooseCard(boba);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), boba));
    }

    @Test
    public void HanSoloTakesFirstWeaponsSegmentActionWhenOpponentInitiatesBattleHere() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, stormtrooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        assertTrue(scn.LSCardActionAvailable(han, "Take first weapons segment action"));
        scn.LSUseCardAction(han, "Take first weapons segment action");
        scn.PassForceUseResponses();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }
}
