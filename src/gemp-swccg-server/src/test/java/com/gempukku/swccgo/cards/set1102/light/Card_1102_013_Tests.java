package com.gempukku.swccgo.cards.set1102.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leiasback", "1102_13");
                    put("leia", "1_17");
                    put("rifle", "7_161");
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
    public void LeiasBackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("leiasback").getBlueprint();

        assertEquals("Leia's Back", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LeiasBackDeploysLeiaFromReserveDeck() {
        var scn = GetScenario();
        var leiasback = scn.GetLSCard("leiasback");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(leiasback);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        scn.MoveCardsToTopOfLSReserveDeck(leia);

        assertTrue(scn.LSCardPlayAvailable(leiasback, "Deploy from Reserve Deck"));
        scn.LSPlayCard(leiasback, "Deploy from Reserve Deck");
        settleDeployFromReserve(scn, leiasback, leia, site);

        assertAtLocation(site, leia);
        assertInZone(Zone.LOST_PILE, leiasback);
    }

    @Test
    public void LeiasBackRetrievesLeiaFromLostPile() {
        var scn = GetScenario();
        var leiasback = scn.GetLSCard("leiasback");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveCardsToLSHand(leiasback);
        scn.MoveCardsToTopOfLSLostPile(leia);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(1);

        assertTrue(scn.LSCardPlayAvailable(leiasback, "Retrieve from Lost Pile"));
        scn.LSPlayCard(leiasback, "Retrieve from Lost Pile");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(leia)) {
            scn.LSChooseCard(leia);
        }
        scn.PassAllResponses();

        assertTrue(leia.getZone() == Zone.USED_PILE || leia.getZone() == Zone.TOP_OF_USED_PILE
                || leia.getZone() == Zone.HAND);
        assertInZone(Zone.LOST_PILE, leiasback);
    }

    @Test
    public void LeiasBackAddsTwoToForceDrainWhereLeiaIsPresent() {
        var scn = GetScenario();
        var leiasback = scn.GetLSCard("leiasback");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.MoveCardsToLSHand(leiasback);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        if (scn.DSGetDecision() != null) {
            scn.DSPass();
        }
        assertTrue("LS decision=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : "null")
                        + " DS decision=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : "null"),
                scn.LSCardPlayAvailable(leiasback, "Add 2"));
        scn.LSPlayCard(leiasback, "Add 2");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, leiasback);
        if (scn.IsActiveForceDrain() && scn.GetForceDrainTotal() == 0) {
            scn.PassForceDrainStartResponses();
        }
        assertTrue("active=" + scn.IsActiveForceDrain()
                        + " LS=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : "null")
                        + " DS=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : "null"),
                scn.IsActiveForceDrain());
        assertEquals(3, scn.GetForceDrainTotal());
    }

    private void settleDeployFromReserve(VirtualTableScenario scn,
                                         com.gempukku.swccgo.game.PhysicalCardImpl interrupt,
                                         com.gempukku.swccgo.game.PhysicalCardImpl card,
                                         com.gempukku.swccgo.game.PhysicalCardImpl site) {
        for (int i = 0; i < 24; i++) {
            if (card.getAtLocation() == site && (interrupt.getZone() == Zone.LOST_PILE
                    || interrupt.getZone() == Zone.TOP_OF_LOST_PILE)) {
                return;
            }
            if (chooseIfSelectable(scn, false, card) || chooseIfSelectable(scn, false, site)) {
                continue;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            scn.PassAllResponses();
        }
    }

    private boolean chooseIfSelectable(VirtualTableScenario scn, boolean dark,
                                       com.gempukku.swccgo.game.PhysicalCardImpl card) {
        if (!(dark ? scn.DSHasCardChoiceAvailable(card) : scn.LSHasCardChoiceAvailable(card))) {
            return false;
        }
        if (dark) {
            scn.DSChooseCard(card);
        } else {
            scn.LSChooseCard(card);
        }
        return true;
    }

    private boolean payForce(VirtualTableScenario scn, boolean dark) {
        var decision = dark ? scn.DSGetDecision() : scn.LSGetDecision();
        if (decision == null || decision.getText() == null) {
            return false;
        }
        String text = decision.getText().toLowerCase();
        if (!text.contains("force") || text.contains("retriev") || text.contains("forfeit")) {
            return false;
        }
        var force = dark ? scn.GetTopOfDSForcePile() : scn.GetTopOfLSForcePile();
        if (force != null && (dark ? scn.DSHasCardChoiceAvailable(force) : scn.LSHasCardChoiceAvailable(force))) {
            if (dark) {
                scn.DSChooseCard(force);
            } else {
                scn.LSChooseCard(force);
            }
            return true;
        }
        if (text.contains("optional")) {
            scn.PassForceUseResponses();
            return true;
        }
        String[] minParam = dark ? scn.DSGetADParam("min") : scn.LSGetADParam("min");
        if (minParam != null && minParam.length > 0) {
            int min = Integer.parseInt(minParam[0]);
            if (dark) {
                scn.DSDecided(min);
            } else {
                scn.LSDecided(min);
            }
            return true;
        }
        return false;
    }

    private boolean resolveDecision(VirtualTableScenario scn, boolean dark) {
        var decision = dark ? scn.DSGetDecision() : scn.LSGetDecision();
        if (decision == null) {
            return false;
        }
        String text = decision.getText() == null ? "" : decision.getText().toLowerCase();
        if (text.contains("optional") || text.contains("or pass")) {
            if (dark) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
            return true;
        }
        return false;
    }
}
