package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sentry", "1102_25");
                }},
                new HashMap<>() {{
                    put("storm1", "1_194");
                    put("storm2", "1_194");
                    put("jawa1", "1_182");
                    put("jawa2", "1_182");
                    put("tusken1", "1_196");
                    put("tusken2", "1_196");
                    put("colo", "13_060");
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
    public void YavinSentryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("sentry").getBlueprint();

        assertEquals(Title.Yavin_Sentry, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void YavinSentryChargesExtraForceEqualToCopiesOfANonUniqueCardAtTheLocation() {
        var scn = GetScenario();
        var sentry = scn.GetLSCard("sentry");
        var storm1 = scn.GetDSCard("storm1");
        var storm2 = scn.GetDSCard("storm2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(sentry);
        scn.MoveCardsToLocation(site, storm1);

        float extra = scn.game().getModifiersQuerying().getExtraForceRequiredToDeployToTarget(
                scn.gameState(), storm2, site, null, storm2, false);
        assertEquals(1, extra, scn.epsilon);
    }

    @Test
    public void YavinSentryDoesNotChargeExtraForceForJawasOrTuskenRaiders() {
        var scn = GetScenario();
        var sentry = scn.GetLSCard("sentry");
        var jawa1 = scn.GetDSCard("jawa1");
        var jawa2 = scn.GetDSCard("jawa2");
        var tusken1 = scn.GetDSCard("tusken1");
        var tusken2 = scn.GetDSCard("tusken2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(sentry);
        scn.MoveCardsToLocation(site, jawa1, tusken1);

        float jawaExtra = scn.game().getModifiersQuerying().getExtraForceRequiredToDeployToTarget(
                scn.gameState(), jawa2, site, null, jawa2, false);
        float tuskenExtra = scn.game().getModifiersQuerying().getExtraForceRequiredToDeployToTarget(
                scn.gameState(), tusken2, site, null, tusken2, false);
        assertEquals(0, jawaExtra, scn.epsilon);
        assertEquals(0, tuskenExtra, scn.epsilon);
    }

    @Test
    public void YavinSentryDoesNotCancelColoClawFish() {
        var scn = GetScenario();
        var sentry = scn.GetLSCard("sentry");
        var colo = scn.GetDSCard("colo");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(sentry);
        scn.MoveCardsToDSSideOfTable(colo);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, colo);
    }
}
