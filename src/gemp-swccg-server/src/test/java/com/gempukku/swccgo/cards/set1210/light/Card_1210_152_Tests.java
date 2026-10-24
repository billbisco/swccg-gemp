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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_152_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hansback", "1210_152");
                    put("han", "1_11");
                    put("pistol", "1_154");
                    put("falcon", "1_143");
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
    public void HansBackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hansback").getBlueprint();

        assertEquals("Han's Back", card.getTitle());
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
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HansBackDeploysHanFromReserveDeck() {
        var scn = GetScenario();
        var hansback = scn.GetLSCard("hansback");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(hansback);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        scn.MoveCardsToTopOfLSReserveDeck(han);

        assertTrue(scn.LSCardPlayAvailable(hansback, "Deploy from Reserve Deck"));
        scn.LSPlayCard(hansback, "Deploy from Reserve Deck");
        settleDeployFromReserve(scn, hansback, han, site);

        assertAtLocation(site, han);
        assertInZone(Zone.LOST_PILE, hansback);
    }

    @Test
    public void HansBackRetrievesHanFromLostPile() {
        var scn = GetScenario();
        var hansback = scn.GetLSCard("hansback");
        var han = scn.GetLSCard("han");

        scn.StartGame();
        scn.MoveCardsToLSHand(hansback);
        scn.MoveCardsToTopOfLSLostPile(han);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(1);

        assertTrue(scn.LSCardPlayAvailable(hansback, "Retrieve from Lost Pile"));
        scn.LSPlayCard(hansback, "Retrieve from Lost Pile");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(han)) {
            scn.LSChooseCard(han);
        }
        scn.PassAllResponses();

        assertTrue(han.getZone() == Zone.USED_PILE || han.getZone() == Zone.TOP_OF_USED_PILE
                || han.getZone() == Zone.HAND);
        assertInZone(Zone.LOST_PILE, hansback);
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
