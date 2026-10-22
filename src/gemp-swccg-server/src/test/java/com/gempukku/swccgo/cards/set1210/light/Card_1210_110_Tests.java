package com.gempukku.swccgo.cards.set1210.light;

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

public class Card_1210_110_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tech", "1210_110");
                    put("warroom", "1_139");
                    put("ruins", "2_068");
                    put("attackrun", "2_42");
                    put("advance", "2_43");
                    put("gospic", "1210_95");
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
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelTechDoesNotCancelYavinDrainBonusesWhenNotOnYavin4() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("tech");
        var ruins = scn.GetLSCard("ruins");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(ruins);
        scn.MoveCardsToLocation(site, tech);
        scn.MoveCardsToLocation(ruins, vader);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(ruins));
        scn.DSForceDrainAt(ruins);
        scn.LSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(3, scn.GetForceDrainTotal());
    }

    @Test
    public void RebelTechOnYavin4CancelsForceDrainBonusesFromYavinSites() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("tech");
        var warroom = scn.GetLSCard("warroom");
        var ruins = scn.GetLSCard("ruins");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(warroom);
        scn.MoveLocationToTable(ruins);
        scn.MoveCardsToLocation(warroom, tech);
        scn.MoveCardsToLocation(ruins, vader);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(ruins));
        scn.DSForceDrainAt(ruins);
        scn.LSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void RebelTechOnYavin4AddsOneToAttackRunTotal() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("tech");
        var warroom = scn.GetLSCard("warroom");
        var attackrun = scn.GetLSCard("attackrun");

        scn.StartGame();
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToLocation(warroom, tech);
        scn.MoveCardsToLSSideOfTable(attackrun);
        assertEquals(1, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), attackrun, 0), scn.epsilon);
    }

    @Test
    public void RebelTechPlacesSelfInUsedPileToTakeAdvancePreparation() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("tech");
        var warroom = scn.GetLSCard("warroom");
        var advance = scn.GetLSCard("advance");

        scn.StartGame();
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToLocation(warroom, tech);
        scn.SkipToLSTurn(Phase.DEPLOY);
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
