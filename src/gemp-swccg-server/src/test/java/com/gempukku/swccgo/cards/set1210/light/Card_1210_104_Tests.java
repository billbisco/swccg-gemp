package com.gempukku.swccgo.cards.set1210.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_104_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1210_104");
                    put("biggs", "1_3");
                    put("t16", "2_75");
                    put("canyon", "7_130");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
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
    public void LukeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luke").getBlueprint();

        assertEquals("Luke", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LukeAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, luke);
        assertEquals(6, scn.GetPower(xwing));
    }

    @Test
    public void LukeAddsOneBattleDestinyWithBiggs() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, biggs, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void LukeAddsOneBattleDestinyWhenPilotingA_T16() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var t16 = scn.GetLSCard("t16");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var canyon = scn.GetLSCard("canyon");

        scn.StartGame();
        scn.MoveLocationToTable(canyon);
        scn.MoveCardsToLocation(canyon, t16, stormtrooper);
        scn.BoardAsPilot(t16, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(canyon);
        scn.PassWeaponsSegmentActions();
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void LukeDeploysAT16HereFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var t16 = scn.GetLSCard("t16");
        var canyon = scn.GetLSCard("canyon");

        scn.StartGame();
        scn.MoveLocationToTable(canyon);
        scn.MoveCardsToLocation(canyon, luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(t16);
        assertTrue(scn.LSCardActionAvailable(luke, "Deploy a T-16 here from Reserve Deck"));
        scn.LSUseCardAction(luke, "Deploy a T-16 here from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(t16);
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, t16.getZone());
        assertEquals(canyon, t16.getAtLocation());
    }

    @Test
    public void LukeMakesOpponentUseOneForceToDrawFromForcePileWhilePilotingAT16AloneAtBeggarsCanyon() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var t16 = scn.GetLSCard("t16");
        var canyon = scn.GetLSCard("canyon");

        scn.StartGame();
        scn.MoveLocationToTable(canyon);
        scn.MoveCardsToLocation(canyon, t16);
        scn.BoardAsPilot(t16, luke);
        scn.SkipToDSTurn(Phase.DRAW);
        scn.EnsureDSForcePile(4);
        int force = scn.GetDSForcePileCount();
        int used = scn.GetDSUsedPileCount();
        int hand = scn.GetDSHandCount();
        assertTrue(scn.DSActionAvailable("Draw card into hand from Force Pile"));
        scn.DSChooseAction("Draw card into hand from Force Pile");
        scn.PassAllResponses();
        assertEquals(force - 2, scn.GetDSForcePileCount());
        assertEquals(used + 1, scn.GetDSUsedPileCount());
        assertEquals(hand + 1, scn.GetDSHandCount());
    }
}
