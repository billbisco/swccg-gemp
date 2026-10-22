package com.gempukku.swccgo.cards.set1210.dark;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("jarjar", "11_4");
                }},
                new HashMap<>() {{
                    put("tarkin", "1210_23");
                    put("vader", "1_168");
                    put("scan", "2_120");
                    put("detention", "1_284");
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
    public void TarkinStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("tarkin").getBlueprint();

        assertEquals("Tarkin", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
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
    public void TarkinAddsOneBattleDestinyIfWithVader() {
        var scn = GetScenario();
        var tarkin = scn.GetDSCard("tarkin");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarkin, vader, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(2, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void TarkinAddsOneBattleDestinyIfWithRebelAtMobileSite() {
        var scn = GetScenario();
        var tarkin = scn.GetDSCard("tarkin");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarkin, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void TarkinPreventsJarJarAndOpponentSpiesFromDeployingToDeathStar() {
        var scn = GetScenario();
        var tarkin = scn.GetDSCard("tarkin");
        var detention = scn.GetDSCard("detention");
        var leia = scn.GetLSCard("leia");
        var jarjar = scn.GetLSCard("jarjar");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(site, tarkin);
        scn.MoveCardsToLocation(detention, luke);
        scn.MoveCardsToLSHand(leia, jarjar);
        scn.EnsureLSForcePile(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(jarjar));
        scn.LSPlayCard(jarjar);
        assertFalse(scn.LSHasCardChoiceAvailable(detention));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(leia));
        scn.LSPlayCard(leia);
        assertFalse(scn.LSHasCardChoiceAvailable(detention));
    }

    @Test
    public void TarkinTakesHyperwaveScanOncePerGame() {
        var scn = GetScenario();
        var tarkin = scn.GetDSCard("tarkin");
        var scan = scn.GetDSCard("scan");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarkin);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(scan);
        assertTrue(scn.DSCardActionAvailable(tarkin, "Take card into hand from Reserve Deck"));
        scn.DSUseCardAction(tarkin, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(scan);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scan.getZone());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Take card into hand from Reserve Deck"));
    }
}
