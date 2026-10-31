package com.gempukku.swccgo.cards.set1109.dark;

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

public class Card_1109_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("bay", "1_129");
                }},
                new HashMap<>() {{
                    put("bright", "1109_26");
                    put("vader", "1_168");
                    put("stormtrooper", "1_194");
                }},
                10, 10,
                StartingSetup.LSStartingLocation("3_056"),
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ABrightCenterToTheUniverseStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bright").getBlueprint();

        assertEquals("A Bright Center To The Universe", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void ABrightCenterToTheUniverseDeploysOnTable() {
        var scn = GetScenario();
        var bright = scn.GetDSCard("bright");

        scn.StartGame();
        scn.MoveCardsToDSHand(bright);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(bright));
        scn.DSPlayCard(bright);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, bright);
    }

    @Test
    public void ABrightCenterToTheUniverseOffersRetrieveWhenLosingForceToAForceDrainWhileControllingMoreBattlegrounds() {
        var scn = GetScenario();
        var bright = scn.GetDSCard("bright");
        var vader = scn.GetDSCard("vader");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var luke = scn.GetLSCard("luke");
        var lost = scn.GetDSFiller(1);

        scn.StartGame();
        var dsSite = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();
        var bay = scn.GetLSCard("bay");
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToDSSideOfTable(bright);
        scn.MoveCardsToLocation(dsSite, vader);
        scn.MoveCardsToLocation(bay, stormtrooper);
        scn.MoveCardsToLocation(lsSite, luke);
        scn.MoveCardsToTopOfDSLostPile(lost);
        int lostBefore = scn.GetDSLostPileCount();
        int usedBefore = scn.GetDSUsedPileCount();

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(lsSite));
        scn.LSForceDrainAt(lsSite);
        scn.PassForceDrainStartResponses();

        boolean offered = false;
        for (int i = 0; i < 24; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(bright, "Retrieve")) {
                scn.DSUseCardAction(bright, "Retrieve");
                offered = true;
                break;
            }
            if (scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null) {
                var actions = scn.DSGetADParamAsList("actionText");
                if (actions != null) {
                    for (String a : actions) {
                        if (a != null && a.toLowerCase().contains("retrieve")) {
                            scn.DSChooseAction(a);
                            offered = true;
                            break;
                        }
                    }
                }
            }
            if (offered) {
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(offered);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPileCount() <= lostBefore);
        assertTrue(scn.GetDSUsedPileCount() >= usedBefore);
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }
}
