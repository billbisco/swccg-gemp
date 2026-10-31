package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1104_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("medlab", "1104_10");
                    put("tooonebee", "3_1");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void HothEchoMedLabStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("medlab").getBlueprint();

        assertEquals("Hoth: Echo Med Lab", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.UNDERGROUND);
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.HOTH);
            add(Icon.LIGHT_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HothEchoMedLabDeploysAMedicalDroidHereFromReserveDeck() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("medlab");
        var tooonebee = scn.GetLSCard("tooonebee");

        scn.StartGame();
        scn.MoveLocationToTable(medlab);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(tooonebee);

        assertTrue(scn.LSCardActionAvailable(medlab, "Deploy a medical droid from Reserve Deck"));
        scn.LSUseCardAction(medlab, "Deploy a medical droid from Reserve Deck");
        if (scn.LSHasCardChoiceAvailable(tooonebee)) {
            scn.LSChooseCard(tooonebee);
        }
        scn.PassAllResponses();

        assertAtLocation(medlab, tooonebee);
    }

    @Test
    public void HothEchoMedLabDoesNotOfferMedicalDroidSearchDuringControlPhase() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("medlab");
        var tooonebee = scn.GetLSCard("tooonebee");

        scn.StartGame();
        scn.MoveLocationToTable(medlab);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(tooonebee);

        assertFalse(scn.LSCardActionAvailable(medlab, "Deploy a medical droid from Reserve Deck"));
    }
}
