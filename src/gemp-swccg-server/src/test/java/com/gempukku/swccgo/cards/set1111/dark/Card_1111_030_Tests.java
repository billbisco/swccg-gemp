package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class Card_1111_030_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("special", "1111_30");
                    put("devastator", "1_301");
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
    public void SomethingSpecialPlannedForThemStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("special").getBlueprint();
        assertEquals("Something Special Planned For Them", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void SomethingSpecialPlannedForThemPlacesInLostPileToRetrieveAStarship() {
        var scn = GetScenario();
        var special = scn.GetDSCard("special");
        var devastator = scn.GetDSCard("devastator");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(special);
        scn.MoveCardsToTopOfDSLostPile(devastator);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(special, "Place in Lost Pile"));
        scn.DSUseCardAction(special, "Place in Lost Pile");
        scn.PassAllResponses();
        if (devastator.getZone() == Zone.LOST_PILE || devastator.getZone() == Zone.TOP_OF_LOST_PILE) {
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(devastator)) {
                scn.DSChooseCard(devastator);
                scn.PassAllResponses();
            }
        }
        assertTrue(special.getZone() == Zone.LOST_PILE || special.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(devastator.getZone() != Zone.LOST_PILE && devastator.getZone() != Zone.TOP_OF_LOST_PILE);
    }
}
