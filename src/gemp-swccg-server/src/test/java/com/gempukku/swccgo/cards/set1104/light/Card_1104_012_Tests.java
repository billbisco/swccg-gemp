package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("flaps", "1104_12");
                    put("luke", "1104_3");
                    put("rogue1", "3_66");
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
    public void ManeuveringFlapsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("flaps").getBlueprint();

        assertEquals("Maneuvering Flaps", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ManeuveringFlapsDeploysOnTable() {
        var scn = GetScenario();
        var flaps = scn.GetLSCard("flaps");

        scn.StartGame();
        scn.MoveCardsToLSHand(flaps);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(flaps) || scn.LSDeployAvailable(flaps));
        scn.LSPlayCard(flaps);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, flaps);
    }

    @Test
    public void ManeuveringFlapsDeploysMatchingPilotWithUnpilotedCombatVehicleFromHand() {
        var scn = GetScenario();
        var flaps = scn.GetLSCard("flaps");
        var luke = scn.GetLSCard("luke");
        var rogue1 = scn.GetLSCard("rogue1");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(flaps);
        scn.MoveCardsToLSHand(rogue1);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        scn.MoveCardsToTopOfLSReserveDeck(luke);
        assertTrue(scn.LSCardActionAvailable(flaps, "Reveal pilot or unpiloted combat vehicle from hand"));
        scn.LSUseCardAction(flaps, "Reveal pilot or unpiloted combat vehicle from hand");
        if (scn.LSHasCardChoiceAvailable(rogue1)) {
            scn.LSChooseCard(rogue1);
        }
        chooseFromSearch(scn, luke);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertTrue("rogue1 zone=" + rogue1.getZone() + " luke zone=" + luke.getZone()
                        + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null),
                rogue1.getZone().isInPlay());
        assertTrue(luke.getZone().isInPlay());
    }

    private void chooseFromSearch(VirtualTableScenario scn, PhysicalCardImpl card) {
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(card)) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
        if (scn.LSHasCardChoiceAvailable(card)) {
            scn.LSChooseCard(card);
        }
    }
}
