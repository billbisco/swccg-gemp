package com.gempukku.swccgo.cards.set1210.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_154_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("harvest", "1210_154");
                    put("hydro", "1210_117");
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

    protected VirtualTableScenario GetStartingScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("harvest", "1210_154");
                    put("farm", "1_132");
                    put("hydro", "1210_117");
                    put("vaporator", "1210_118");
                    put("aimhigh", "8_34");
                    put("battleplan", "8_35");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.LSStartingLocation("1_128"),
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void HarvestStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("harvest").getBlueprint();

        assertEquals("Harvest", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HarvestUsedTakesADeviceIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var harvest = scn.GetLSCard("harvest");
        var hydro = scn.GetLSCard("hydro");

        scn.StartGame();
        scn.MoveCardsToLSHand(harvest);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(hydro);

        assertTrue(scn.LSCardPlayAvailable(harvest, "Take a device"));
        scn.LSPlayCard(harvest, "Take a device");
        for (int i = 0; i < 20; i++) {
            if (hydro.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(hydro)) {
                scn.LSChooseCard(hydro);
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
        finishUsedInterrupt(scn, harvest);

        assertInHand(hydro);
        assertInZone(Zone.USED_PILE, harvest);
    }

    @Test
    public void HarvestUsedRetrievesADeviceFromLostPile() {
        var scn = GetScenario();
        var harvest = scn.GetLSCard("harvest");
        var hydro = scn.GetLSCard("hydro");

        scn.StartGame();
        scn.MoveCardsToLSHand(harvest);
        scn.MoveCardsToTopOfLSLostPile(hydro);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(harvest, "Retrieve a device"));
        scn.LSPlayCard(harvest, "Retrieve a device");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(hydro)) {
            scn.LSChooseCard(hydro);
        }
        scn.PassAllResponses();
        finishUsedInterrupt(scn, harvest);

        assertTrue(hydro.getZone() == Zone.USED_PILE || hydro.getZone() == Zone.TOP_OF_USED_PILE
                || hydro.getZone() == Zone.HAND);
        assertInZone(Zone.USED_PILE, harvest);
    }

    @Test
    public void HarvestStartingDeploysFarmDevicesAndAlwaysImmuneEffectsThenGoesToReserve() {
        var scn = GetStartingScenario();
        var harvest = scn.GetLSCard("harvest");
        var farm = scn.GetLSCard("farm");
        var hydro = scn.GetLSCard("hydro");
        var vaporator = scn.GetLSCard("vaporator");
        var aimhigh = scn.GetLSCard("aimhigh");
        var battleplan = scn.GetLSCard("battleplan");

        scn.StartGame();
        for (int i = 0; i < 8; i++) {
            if (scn.LSDecisionAvailable("Choose starting interrupt")) {
                break;
            }
            if (scn.LSDecisionAvailable("On which side") || scn.LSDecisionAvailable("which side")) {
                scn.LSChoose("Left");
                continue;
            }
            if (scn.DSDecisionAvailable("Choose starting interrupt")) {
                scn.DSPass();
                continue;
            }
            if (scn.DSDecisionAvailable("On which side") || scn.DSDecisionAvailable("which side")) {
                scn.DSChoose("Left");
                continue;
            }
            break;
        }
        assertTrue(scn.LSDecisionAvailable("Choose starting interrupt"));
        scn.LSChooseCard(harvest);
        scn.PassAllResponses();
        for (int i = 0; i < 36; i++) {
            if ((harvest.getZone() == Zone.RESERVE_DECK || harvest.getZone() == Zone.TOP_OF_RESERVE_DECK)
                    && farm.getZone() == Zone.LOCATIONS
                    && aimhigh.getZone() == Zone.SIDE_OF_TABLE) {
                break;
            }
            if (scn.LSDecisionAvailable("Viewing") || scn.LSDecisionAvailable("prior to choosing")) {
                scn.LSDismissRevealedCards();
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(farm)) {
                scn.LSChooseCard(farm);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(hydro)) {
                scn.LSChooseCard(hydro);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(vaporator)) {
                scn.LSChooseCard(vaporator);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(aimhigh)) {
                scn.LSChooseCard(aimhigh);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(battleplan)) {
                scn.LSChooseCard(battleplan);
                continue;
            }
            if (scn.LSDecisionAvailable("On which side") || scn.LSDecisionAvailable("which side")) {
                scn.LSChoose("Left");
                continue;
            }
            if (scn.DSDecisionAvailable("On which side") || scn.DSDecisionAvailable("which side")) {
                scn.DSChoose("Left");
                continue;
            }
            if (scn.LSDecisionAvailable("Playing") || scn.DSDecisionAvailable("Playing")) {
                scn.PassCardPlayResponses();
                continue;
            }
            var dsChoices = scn.DSGetMultipleChoices();
            if (dsChoices != null && !dsChoices.isEmpty()) {
                String pick = dsChoices.get(0);
                for (String option : dsChoices) {
                    if (option.toLowerCase().contains("left")) {
                        pick = option;
                        break;
                    }
                }
                scn.DSChoose(pick);
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            break;
        }

        assertEquals(Zone.LOCATIONS, farm.getZone());
        assertEquals(farm, hydro.getAttachedTo());
        assertEquals(farm, vaporator.getAttachedTo());
        assertInZone(Zone.SIDE_OF_TABLE, aimhigh);
        assertInZone(Zone.SIDE_OF_TABLE, battleplan);
        Zone leftover = harvest.getZone();
        assertTrue("harvest=" + leftover,
                leftover == Zone.RESERVE_DECK
                        || leftover == Zone.TOP_OF_RESERVE_DECK
                        || leftover == Zone.FORCE_PILE
                        || leftover == Zone.HAND);
    }

    @Test
    public void HarvestStartingIsNotOfferedWithoutATatooineStartingLocation() {
        var scn = GetScenario();
        var harvest = scn.GetLSCard("harvest");

        scn.StartGame();
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            assertFalse(scn.LSHasCardChoiceAvailable(harvest));
            scn.LSPass();
        }
    }

    private void finishUsedInterrupt(VirtualTableScenario scn, PhysicalCardImpl interrupt) {
        for (int i = 0; i < 10; i++) {
            if (interrupt.getZone() == Zone.USED_PILE) {
                return;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            return;
        }
    }
}
