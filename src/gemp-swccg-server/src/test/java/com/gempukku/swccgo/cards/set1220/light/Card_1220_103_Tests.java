package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_103_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("handler", "1220_103");
                    put("tauntaun", "3_70");
                    put("bones", "3_38");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
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
    public void TauntaunHandlerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("handler").getBlueprint();

        assertEquals("Tauntaun Handler", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TauntaunHandlerMakesCreatureVehiclesHeRidesPowerPlusTwo() {
        var scn = GetScenario();
        var handler = scn.GetLSCard("handler");
        var tauntaun = scn.GetLSCard("tauntaun");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tauntaun);
        scn.BoardAsPassenger(tauntaun, handler);
        assertEquals(tauntaun.getBlueprint().getPower().intValue() + 2, scn.GetPower(tauntaun));
    }

    @Test
    public void TauntaunHandlerAddsOneBattleDestinyWhileRidingATauntaun() {
        var scn = GetScenario();
        var handler = scn.GetLSCard("handler");
        var tauntaun = scn.GetLSCard("tauntaun");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tauntaun, trooper);
        scn.BoardAsPassenger(tauntaun, handler);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void TauntaunHandlerCancelsAnyResultOfTauntaunBones() {
        var scn = GetScenario();
        var handler = scn.GetLSCard("handler");
        var bones = scn.GetLSCard("bones");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, handler, vader, trooper);
        scn.MoveCardsToLSSideOfTable(bones);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareDSDestiny(1);
        scn.DSChooseYes();
        int safety = 0;
        while (safety++ < 16
                && !scn.LSDecisionAvailable("Cancel result of Tauntaun Bones")
                && !scn.GetLSAvailableActions().toString().contains("Cancel result of Tauntaun Bones")) {
            if (scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional")
                    || (scn.LSGetDecision() != null && scn.GetLSAvailableActions().isEmpty())) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertTrue(scn.LSDecisionAvailable("Cancel result of Tauntaun Bones")
                || scn.GetLSAvailableActions().toString().contains("Cancel result of Tauntaun Bones"));
        scn.LSChooseAction("Cancel result of Tauntaun Bones");
        scn.PassAllResponses();
        assertTrue(bones.getZone().isInPlay());
    }
}
