package com.gempukku.swccgo.cards.set1109.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1109_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("planet", "1109_21");
                    put("luke", "1_19");
                    put("bay", "1_129");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("stormtrooper", "1_194");
                }},
                10, 10,
                StartingSetup.LSStartingLocation("3_056"),
                StartingSetup.DSStartingLocation("1_293"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ThePlanetThatItsFarthestFromStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("planet").getBlueprint();

        assertEquals("The Planet That It's Farthest From", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ThePlanetThatItsFarthestFromDeploysOnTable() {
        var scn = GetScenario();
        var planet = scn.GetLSCard("planet");

        scn.StartGame();
        scn.MoveCardsToLSHand(planet);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(planet) || scn.LSDeployAvailable(planet));
        scn.LSPlayCard(planet);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, planet);
    }

    @Test
    public void ThePlanetThatItsFarthestFromOffersRetrieveWhenLosingForceToAForceDrainWhileControllingMoreBattlegrounds() {
        var scn = GetScenario();
        var planet = scn.GetLSCard("planet");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var lost = scn.GetLSFiller(1);

        scn.StartGame();
        var dsSite = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();
        var bay = scn.GetLSCard("bay");
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLSSideOfTable(planet);
        scn.MoveCardsToLocation(lsSite, luke);
        scn.MoveCardsToLocation(bay, scn.GetLSFiller(2));
        scn.MoveCardsToLocation(dsSite, vader);
        scn.MoveCardsToTopOfLSLostPile(lost);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(dsSite));
        scn.DSForceDrainAt(dsSite);
        scn.PassForceDrainStartResponses();

        boolean offered = false;
        for (int i = 0; i < 24; i++) {
            if (lsHasCardActions(scn) && scn.LSCardActionAvailable(planet, "Retrieve")) {
                scn.LSUseCardAction(planet, "Retrieve");
                offered = true;
                break;
            }
            if (scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null) {
                var actions = scn.LSGetADParamAsList("actionText");
                if (actions != null) {
                    for (String a : actions) {
                        if (a != null && a.toLowerCase().contains("retrieve")) {
                            scn.LSChooseAction(a);
                            offered = true;
                            break;
                        }
                    }
                }
            }
            if (offered) {
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(offered);
        scn.PassAllResponses();
    }

    private boolean lsHasCardActions(VirtualTableScenario scn) {
        return scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null;
    }
}
