package com.gempukku.swccgo.cards.set1105.light;

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

public class Card_1105_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("levitation", "1105_22");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
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
    public void StarshipLevitationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("levitation").getBlueprint();

        assertEquals("Starship Levitation", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void StarshipLevitationLostRetrievesNonMaintenanceStarshipIntoHand() {
        var scn = GetScenario();
        var levitation = scn.GetLSCard("levitation");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveCardsToLSHand(levitation);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(levitation, "Retrieve a non-[Maintenance] starship into hand"));
        scn.LSPlayCard(levitation, "Retrieve a non-[Maintenance] starship into hand");
        for (int i = 0; i < 20; i++) {
            if (xwing.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSDecisionAvailable("Viewing") || scn.LSDecisionAvailable("prior to choosing")
                    || scn.LSDecisionAvailable("Top card")) {
                scn.LSDecided("");
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(xwing)) {
                scn.LSChooseCard(xwing);
                continue;
            }
            scn.PassAllResponses();
            scn.PassCardAndForceUseResponses();
        }
        assertInHand(xwing);
        assertInZone(Zone.LOST_PILE, levitation);
    }
}
