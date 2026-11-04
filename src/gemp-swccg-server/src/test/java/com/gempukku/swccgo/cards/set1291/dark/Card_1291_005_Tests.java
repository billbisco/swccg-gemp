package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("nute", "1291_5");
                    put("rally", "1291_28"); // Rally To Our Cause (the card that 'aligns' systems)
                    put("council", "1291_19"); // Geonosis: Separatist Council Room
                    put("dooku", "200_76"); // unique [Separatist] character
                    put("trooper", "1_194");
                    put("junk", "1_304");
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
    public void NuteGunrayVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("nute").getBlueprint();
        assertEquals(Title.Nute_Gunray, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.GUNRAY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.SEPARATIST);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NuteGunrayVDeploysMinus1ToCouncilRoom() {
        var scn = GetScenario();
        var nute = scn.GetDSCard("nute");
        var council = scn.GetDSCard("council");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, Title.Geonosis, 6);
        scn.MoveLocationToTable(council);
        scn.MoveCardsToHand(nute);
        var mq = scn.game().getModifiersQuerying();
        assertEquals(2, mq.getDeployCost(scn.gameState(), nute, nute, council, false, null, false, 0, null, false), scn.epsilon);
        assertEquals(3, mq.getDeployCost(scn.gameState(), nute, nute, site, false, null, false, 0, null, false), scn.epsilon);
    }

    private PhysicalCardImpl putRallyOnTable(VirtualTableScenario scn) {
        var rally = scn.GetDSCard("rally");
        var host = MissionTestSupport.putOnTable(scn, 9150, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, rally);
        return rally;
    }

    @Test
    public void NuteGunrayVAddsOneToAttemptToAlignASystem() {
        var scn = GetScenario();
        var nute = scn.GetDSCard("nute");
        var trooper = scn.GetDSCard("trooper");
        var junk = scn.GetDSCard("junk");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        putRallyOnTable(scn);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), nute, trooper);
        scn.MoveCardsToTopOfOwnLostPile(junk);
        scn.SkipToDSTurn(Phase.CONTROL);
        // destiny 6 + X 0 + Y 1 (Nute) = 7 is not > 7; Nute's +1 makes it 8
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(scn.GetDSCard("rally"), "Draw destiny to align a system");
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(tatooine).isEmpty());
    }

    @Test
    public void NuteGunrayVAddsToTotalPowerForEachUniqueSeparatistCharacterIfAnAlignedSystemOnTable() {
        var scn = GetScenario();
        var nute = scn.GetDSCard("nute");
        var dooku = scn.GetDSCard("dooku");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        var rally = putRallyOnTable(scn);
        scn.MoveLocationToTable(tatooine);
        rally.setWhileInPlayData(new WhileInPlayData());
        rally.getWhileInPlayData().getTextValues().add(String.valueOf(tatooine.getCardId()));
        scn.MoveCardsToLocation(site, nute, dooku, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        int powerBefore = scn.GetDSTotalPower();
        assertTrue(scn.DSCardActionAvailable(nute, "Lose 1 Force"));
        scn.DSUseCardAction(nute, "Lose 1 Force");
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSForceLossPayment());
        scn.DSPayForceLossFromForcePile();
        scn.PassAllResponses();
        // Nute and Dooku are the unique [Separatist] characters on table
        assertEquals(powerBefore + 2, scn.GetDSTotalPower());
    }

    @Test
    public void NuteGunrayVMayNotAddToTotalPowerWithoutAnAlignedSystem() {
        var scn = GetScenario();
        var nute = scn.GetDSCard("nute");
        var dooku = scn.GetDSCard("dooku");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        putRallyOnTable(scn);
        scn.MoveCardsToLocation(site, nute, dooku, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(nute, "Lose 1 Force"));
    }
}
