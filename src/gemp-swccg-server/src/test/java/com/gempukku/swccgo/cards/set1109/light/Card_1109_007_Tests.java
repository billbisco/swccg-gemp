package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("celebration", "1109_7");
                }},
                new HashMap<>() {{
                    put("occupation", "7_223");
                    put("trooper", "1_194");
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
    public void EndorCelebrationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("celebration").getBlueprint();
        assertEquals("Endor Celebration", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.ENDOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EndorCelebrationCancelsEffectWithOccupationInTitle() {
        var scn = GetScenario();
        var celebration = scn.GetLSCard("celebration");
        var occupation = scn.GetDSCard("occupation");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.AttachCardsTo(site, occupation);
        scn.MoveCardsToLSHand(celebration);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(celebration, "Cancel Effect with occupation in title"));
        scn.LSPlayCard(celebration, "Cancel Effect with occupation in title");
        if (scn.LSHasCardChoiceAvailable(occupation)) {
            scn.LSChooseCard(occupation);
        }
        scn.PassAllResponses();
        assertTrue(occupation.getZone() == Zone.LOST_PILE || occupation.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
