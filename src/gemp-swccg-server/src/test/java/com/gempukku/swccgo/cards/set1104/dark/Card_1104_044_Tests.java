package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_044_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("telemetry", "1104_44");
                    put("probe", "1220_15");
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
    public void ProbeTelemetryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("telemetry").getBlueprint();

        assertEquals("Probe Telemetry", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ProbeTelemetryTakesProbeDroidIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var telemetry = scn.GetDSCard("telemetry");
        var probe = scn.GetDSCard("probe");

        scn.StartGame();
        scn.MoveCardsToDSHand(telemetry);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(probe);

        assertTrue(scn.DSCardPlayAvailable(telemetry, "Take Probe Droid into hand"));
        scn.DSPlayCard(telemetry, "Take Probe Droid into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        if (scn.DSHasCardChoiceAvailable(probe)) {
            scn.DSChooseCard(probe);
        }
        scn.PassAllResponses();
        assertInHand(probe);
        assertInZone(Zone.USED_PILE, telemetry);
    }
}
