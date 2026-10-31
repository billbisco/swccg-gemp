package com.gempukku.swccgo.cards.set1103.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1103_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tech", "1103_17");
                    put("ruins", "2_068");
                    put("docking", "1_136");
                    put("attackrun", "2_42");
                    put("advance", "2_43");
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
    public void RebelTechStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tech").getBlueprint();

        assertEquals("Rebel Tech", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelTechCancelsDarkSideTextOnYourNonDockingBayYavin4Sites() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("tech");
        var ruins = scn.GetLSCard("ruins");
        var docking = scn.GetLSCard("docking");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(ruins);
        scn.MoveLocationToTable(docking);
        scn.MoveCardsToLocation(site, tech);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.game().getModifiersQuerying().isLocationGameTextCanceledForPlayer(scn.gameState(), ruins, scn.DS));
        assertFalse(scn.game().getModifiersQuerying().isLocationGameTextCanceledForPlayer(scn.gameState(), docking, scn.DS));
    }

    @Test
    public void RebelTechAddsOneToAttackRunTotalWhileNotOnYavin4() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("tech");
        var attackrun = scn.GetLSCard("attackrun");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tech);
        scn.MoveCardsToLSSideOfTable(attackrun);
        assertEquals(1, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), attackrun, 0), scn.epsilon);
    }

    @Test
    public void RebelTechPlacesSelfInUsedPileToTakeAdvancePreparationDuringYourTurn() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("tech");
        var advance = scn.GetLSCard("advance");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tech);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(advance);
        assertTrue(scn.LSCardActionAvailable(tech, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(tech, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(advance);
        scn.PassAllResponses();
        assertTrue(tech.getZone() == Zone.USED_PILE || tech.getZone() == Zone.TOP_OF_USED_PILE);
        assertEquals(Zone.HAND, advance.getZone());
    }
}
