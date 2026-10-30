package com.gempukku.swccgo.cards.set1103.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("prep", "1103_2");
                    put("junk", "1_3");
                }},
                new HashMap<>() {{
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
    public void AdvancedPreparationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("prep").getBlueprint();

        assertEquals("Advanced Preparation", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AdvancedPreparationUsedDeploysProtonTorpedoesFromUnderStartingEffect() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("prep", "1103_2");
                    put("starting", "13_5");
                    put("torps", "1_158");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var prep = scn.GetLSCard("prep");
        var starting = scn.GetLSCard("starting");
        var torps = scn.GetLSCard("torps");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(starting);
        scn.StackCardsOn(starting, torps);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), xwing);
        scn.MoveCardsToLSHand(prep);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(prep, "Deploy cards from under Starting Effect"));
        scn.LSPlayCard(prep, "Deploy cards from under Starting Effect");
        for (int i = 0; i < 24; i++) {
            if (torps.getAttachedTo() == xwing) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(torps)) {
                scn.LSChooseCard(torps);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(xwing)) {
                scn.LSChooseCard(xwing);
                continue;
            }
            if (scn.LSDecisionAvailable("Choose card") && !scn.LSHasCardChoiceAvailable(torps)
                    && !scn.LSHasCardChoiceAvailable(xwing)) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertEquals(xwing, torps.getAttachedTo());
        assertTrue(prep.getZone() == Zone.USED_PILE || prep.getZone() == Zone.VOID);
    }

    @Test
    public void AdvancedPreparationLostRetrievesOneForce() {
        var scn = GetScenario();
        var prep = scn.GetLSCard("prep");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveCardsToLSHand(prep);
        scn.MoveCardsToTopOfLSLostPile(junk);
        scn.SkipToLSTurn(Phase.CONTROL);
        int lifeBefore = scn.GetLSLifeForceRemaining();

        assertTrue(scn.LSCardPlayAvailable(prep, "Retrieve 1 Force"));
        scn.LSPlayCard(prep, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(lifeBefore + 1, scn.GetLSLifeForceRemaining());
        assertTrue(prep.getZone() == Zone.LOST_PILE || prep.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
