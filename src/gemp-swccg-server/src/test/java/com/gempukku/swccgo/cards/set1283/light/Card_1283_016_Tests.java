package com.gempukku.swccgo.cards.set1283.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("raider", "1283_16");
                    put("oldraider", "112_5");
                    put("falcon", "1_143");
                    put("lifttube", "1_148");
                    put("tatooine", "1_127");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("wipe", "13_98");
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
    public void PalaceRaiderVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("raider").getBlueprint();

        assertEquals("Palace Raider", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PalaceRaiderAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var raider = scn.GetLSCard("raider");
        var falcon = scn.GetLSCard("falcon");

        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, falcon);
        scn.BoardAsPilot(falcon, raider);
        assertEquals(3 + 2, scn.GetPower(falcon));
    }

    @Test
    public void PalaceRaiderAddsThreeToPowerOfTransportHeDrives() {
        var scn = GetScenario();
        var raider = scn.GetLSCard("raider");
        var lifttube = scn.GetLSCard("lifttube");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lifttube);
        int before = scn.GetPower(lifttube);
        scn.BoardAsPilot(lifttube, raider);
        assertEquals(before + 3, scn.GetPower(lifttube));
    }

    @Test
    public void PalaceRaiderAddsOneBattleDestinyWhenDrivingATransportVehicle() {
        var scn = GetScenario();
        var raider = scn.GetLSCard("raider");
        var han = scn.GetLSCard("han");
        var lifttube = scn.GetLSCard("lifttube");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lifttube, trooper);
        scn.BoardAsPilot(lifttube, han);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        int withoutRaider = scn.GetLSBattleDestinyCount();

        var scn2 = GetScenario();
        var raider2 = scn2.GetLSCard("raider");
        var lifttube2 = scn2.GetLSCard("lifttube");
        var trooper2 = scn2.GetDSCard("trooper");
        var site2 = scn2.GetLSStartingLocation();
        scn2.StartGame();
        scn2.MoveCardsToLocation(site2, lifttube2, trooper2);
        scn2.BoardAsPilot(lifttube2, raider2);
        scn2.SkipToLSTurn(Phase.BATTLE);
        scn2.LSInitiateBattle(site2);
        scn2.PassWeaponsSegmentActions();
        assertEquals(withoutRaider + 1, scn2.GetLSBattleDestinyCount());
    }

    @Test
    public void PalaceRaiderMakesTheTransportHeDrivesMoveForFree() {
        var scn = GetScenario();
        var raider = scn.GetLSCard("raider");
        var lifttube = scn.GetLSCard("lifttube");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lifttube);
        scn.BoardAsPilot(lifttube, raider);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.MOVES_FREE, lifttube).isEmpty());
    }

    @Test
    public void WipeThemOutLimitsBattleDestinyWithYourNonUniqueAlienWithoutPalaceRaiderV() {
        var scn = GetScenario();
        var oldraider = scn.GetLSCard("oldraider");
        var trooper = scn.GetDSCard("trooper");
        var wipe = scn.GetDSCard("wipe");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(wipe);
        scn.MoveCardsToLocation(site, oldraider, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(2, scn.GetLSBattleDestinyLimit());
    }

    @Test
    public void WipeThemOutIgnoresYourNonUniqueAliensWhilePalaceRaiderVOnTable() {
        var scn = GetScenario();
        var raider = scn.GetLSCard("raider");
        var oldraider = scn.GetLSCard("oldraider");
        var trooper = scn.GetDSCard("trooper");
        var wipe = scn.GetDSCard("wipe");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(wipe);
        scn.MoveCardsToLocation(site, raider, oldraider, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertTrue(scn.GetLSBattleDestinyLimit() > 2);
    }
}
