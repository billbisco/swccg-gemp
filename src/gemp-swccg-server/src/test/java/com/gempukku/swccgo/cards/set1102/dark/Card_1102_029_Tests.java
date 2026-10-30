package com.gempukku.swccgo.cards.set1102.dark;

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

public class Card_1102_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper1", "1_028");
                    put("trooper2", "1_028");
                    put("jawa1", "1_012");
                    put("jawa2", "1_012");
                    put("colo", "13_011");
                }},
                new HashMap<>() {{
                    put("sentry", "1102_29");
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
    public void DeathStarSentryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sentry").getBlueprint();

        assertEquals(Title.Death_Star_Sentry, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void DeathStarSentryChargesExtraForceEqualToCopiesOfANonUniqueCardAtTheLocation() {
        var scn = GetScenario();
        var sentry = scn.GetDSCard("sentry");
        var trooper1 = scn.GetLSCard("trooper1");
        var trooper2 = scn.GetLSCard("trooper2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(sentry);
        scn.MoveCardsToLocation(site, trooper1);

        float extra = scn.game().getModifiersQuerying().getExtraForceRequiredToDeployToTarget(
                scn.gameState(), trooper2, site, null, trooper2, false);
        assertEquals(1, extra, scn.epsilon);
    }

    @Test
    public void DeathStarSentryDoesNotChargeExtraForceForJawas() {
        var scn = GetScenario();
        var sentry = scn.GetDSCard("sentry");
        var jawa1 = scn.GetLSCard("jawa1");
        var jawa2 = scn.GetLSCard("jawa2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(sentry);
        scn.MoveCardsToLocation(site, jawa1);

        float extra = scn.game().getModifiersQuerying().getExtraForceRequiredToDeployToTarget(
                scn.gameState(), jawa2, site, null, jawa2, false);
        assertEquals(0, extra, scn.epsilon);
    }

    @Test
    public void DeathStarSentryDoesNotCancelColoClawFish() {
        var scn = GetScenario();
        var sentry = scn.GetDSCard("sentry");
        var colo = scn.GetLSCard("colo");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(sentry);
        scn.MoveCardsToLSSideOfTable(colo);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, colo);
    }
}
