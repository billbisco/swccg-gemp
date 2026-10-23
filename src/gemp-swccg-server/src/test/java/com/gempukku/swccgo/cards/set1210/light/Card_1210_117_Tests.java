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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_117_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hydro", "1210_117");
                    put("farm", "1_132");
                    put("luke", "1_19");
                    put("crop", "1_072");
                }},
                new HashMap<>() {{
                    put("trample", "3_138");
                    put("scout1", "3_156");
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
    public void LarsHydroponicsStationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hydro").getBlueprint();

        assertEquals("Lars' Hydroponics Station", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LarsHydroponicsStationDeploysOnAFarm() {
        var scn = GetScenario();
        var hydro = scn.GetLSCard("hydro");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLSHand(hydro);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(hydro));
        scn.LSPlayCard(hydro);
        scn.LSChooseCard(farm);
        scn.PassAllResponses();
        assertEquals(farm, hydro.getAttachedTo());
    }

    @Test
    public void LarsHydroponicsStationMayNotDeployOnMarketplace() {
        var scn = GetScenario();
        var hydro = scn.GetLSCard("hydro");
        var farm = scn.GetLSCard("farm");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLSHand(hydro);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(hydro));
        scn.LSPlayCard(hydro);
        assertFalse(scn.LSHasCardChoiceAvailable(marketplace));
        assertTrue(scn.LSHasCardChoiceAvailable(farm));
    }

    @Test
    public void LarsHydroponicsStationStacksACropFromLostPileDuringDrawPhase() {
        var scn = GetScenario();
        var hydro = scn.GetLSCard("hydro");
        var crop = scn.GetLSCard("crop");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, hydro);
        scn.MoveCardsToTopOfLSLostPile(crop);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(1);
        scn.SkipToPhase(Phase.DRAW);
        if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("optional")) {
            scn.DSPass();
        }
        assertEquals(Phase.DRAW, scn.gameState().getCurrentPhase());
        if (scn.GetLSAvailableActions().isEmpty() && scn.LSGetDecision() != null
                && scn.LSGetDecision().getText() != null
                && scn.LSGetDecision().getText().toLowerCase().contains("draw")) {
            scn.LSChooseNo();
        }
        assertTrue(scn.GetLSAvailableActions().stream().anyMatch(a -> a != null && a.contains("crop")));
        scn.LSUseCardAction(hydro, "Stack a 'crop' from Lost Pile");
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(crop);
        scn.PassAllResponses();
        assertEquals(hydro, crop.getStackedOn());
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(a -> a != null && a.contains("Stack a 'crop'")));
    }

    @Test
    public void LarsHydroponicsStationPlacesMatchingCropInUsedPileWhileOccupying() {
        var scn = GetScenario();
        var hydro = scn.GetLSCard("hydro");
        var crop = scn.GetLSCard("crop");
        var luke = scn.GetLSCard("luke");
        var trample = scn.GetDSCard("trample");
        var scout1 = scn.GetDSCard("scout1");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, hydro);
        scn.StackCardsOn(hydro, crop);
        scn.MoveCardsToLocation(farm, luke, scout1);
        scn.MoveCardsToDSHand(trample);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(4);
        scn.DSPlayCard(trample);
        scn.DSChooseCard(luke);
        boolean placed = false;
        for (int i = 0; i < 12; i++) {
            if (scn.LSGetDecision() != null) {
                try {
                    var actions = scn.LSGetADParamAsList("actionText");
                    if (actions != null) {
                        for (String a : actions) {
                            if (a != null && a.toLowerCase().contains("crop")) {
                                scn.LSChooseAction("Place 'crop' in Used Pile");
                                placed = true;
                                break;
                            }
                        }
                    }
                } catch (RuntimeException ignored) {
                }
                if (placed) {
                    break;
                }
                if (scn.LSDecisionAvailable("optional")) {
                    scn.LSPass();
                    continue;
                }
            }
            if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("optional")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        scn.PassAllResponses();
        assertTrue(placed);
        assertTrue(crop.getZone() == Zone.USED_PILE || crop.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
