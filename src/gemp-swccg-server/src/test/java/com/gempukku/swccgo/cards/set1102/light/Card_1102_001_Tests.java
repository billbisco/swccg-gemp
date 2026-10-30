package com.gempukku.swccgo.cards.set1102.light;

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

public class Card_1102_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("affect", "1102_1");
                }},
                new HashMap<>() {{
                    put("naboo", "12_170");
                    put("vader", "1_168");
                    put("emperor", "9_109");
                    put("saber", "1_324");
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
    public void AffectMindStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("affect").getBlueprint();

        assertEquals("Affect Mind", card.getTitle());
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
    public void AffectMindCapsForceLossFromDrainsAtOpponentsNabooSitesWhenTwoDarkJediAreOnNaboo() {
        var scn = GetScenario();
        var affect = scn.GetLSCard("affect");
        var naboo = scn.GetDSCard("naboo");
        var vader = scn.GetDSCard("vader");
        var emperor = scn.GetDSCard("emperor");
        var saber = scn.GetDSCard("saber");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(affect);
        scn.MoveLocationToTable(naboo);
        scn.MoveCardsToLocation(naboo, vader, emperor);
        scn.AttachCardsTo(vader, saber);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(8);

        assertEquals(2, scn.game().getModifiersQuerying().getForceToLoseFromForceDrainLimit(scn.gameState(), scn.LS, naboo), scn.epsilon);

        int lostBefore = scn.GetLSLostPileCount();
        assertTrue(scn.DSForceDrainAvailable(naboo));
        scn.DSForceDrainAt(naboo);
        if (scn.LSGetDecision() != null) {
            scn.LSPass();
        }
        assertTrue(scn.DSCardActionAvailable(saber, "Add 1 to Force drain"));
        scn.DSUseCardAction(saber, "Add 1 to Force drain");
        scn.PassForceUseResponses();
        scn.PassAllResponses();
        if (scn.GetForceDrainTotal() == 0) {
            scn.PassForceDrainStartResponses();
        }
        assertTrue(scn.IsActiveForceDrain());
        assertEquals(3, scn.GetForceDrainTotal());

        int paid = 0;
        while (scn.LSDecisionAvailable("Choose Force to lose") && paid < 5) {
            scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
            scn.PassForceDrainPendingResponses();
            paid++;
        }
        assertEquals(2, paid);
        assertEquals(2, scn.GetLSLostPileCount() - lostBefore);
        scn.PassForceDrainEndResponses();
    }
}
