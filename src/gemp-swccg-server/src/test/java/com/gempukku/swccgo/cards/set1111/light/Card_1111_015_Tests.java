package com.gempukku.swccgo.cards.set1111.light;

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
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1111_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("strikeforce", "1111_15");
                    put("falcon", "1_143");
                }},
                new HashMap<>() {{
                    put("overload", "1_263");
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
    public void StrikeforceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("strikeforce").getBlueprint();

        assertEquals("Strikeforce", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.GRABBER);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_11, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void StrikeforcePlacesInLostPileToRetrieveAStarship() {
        var scn = GetScenario();
        var strikeforce = scn.GetLSCard("strikeforce");
        var falcon = scn.GetLSCard("falcon");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(strikeforce);
        scn.MoveCardsToTopOfLSLostPile(falcon);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(strikeforce, "Place in Lost Pile"));
        scn.LSUseCardAction(strikeforce, "Place in Lost Pile");
        scn.PassAllResponses();
        if (falcon.getZone() == Zone.LOST_PILE || falcon.getZone() == Zone.TOP_OF_LOST_PILE) {
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(falcon)) {
                scn.LSChooseCard(falcon);
                scn.PassAllResponses();
            }
        }
        assertTrue(strikeforce.getZone() == Zone.LOST_PILE || strikeforce.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(falcon.getZone() != Zone.LOST_PILE && falcon.getZone() != Zone.TOP_OF_LOST_PILE);
    }
}
