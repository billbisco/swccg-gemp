package com.gempukku.swccgo.cards.set1283.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("edian", "1283_17");
                    put("boshek", "1_4");
                    put("han", "1_11");
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
    public void SergeantEdianVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("edian").getBlueprint();

        assertEquals("Sergeant Edian", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CLOUD_CITY_TROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EdianDeploysFreeToCloudCity() {
        var scn = GetScenario();
        var edian = scn.GetLSCard("edian");
        var cloudCity = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(edian);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(edian);
        if (scn.LSHasCardChoiceAvailable(cloudCity)) {
            scn.LSChooseCard(cloudCity);
        }
        scn.PassAllResponses();
        assertInZone(Zone.AT_LOCATION, edian);
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void EdianDoesNotDeployFreeOutsideCloudCity() {
        var scn = GetScenario();
        var edian = scn.GetLSCard("edian");
        var trooper = scn.GetDSCard("trooper");
        var cloudCity = scn.GetLSStartingLocation();
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(marketplace, scn.GetLSCard("boshek"));
        scn.MoveCardsToLSHand(edian);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(edian);
        scn.LSChooseCard(marketplace);
        scn.PassAllResponses();
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void EdianIsPowerPlusTwoInBattlesYouInitiateOnly() {
        var scn = GetScenario();
        var edian = scn.GetLSCard("edian");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, edian, trooper);
        assertEquals(2, scn.GetPower(edian));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(4, scn.GetPower(edian));
    }

    @Test
    public void EdianIsNotPowerPlusTwoInBattlesOpponentInitiates() {
        var scn = GetScenario();
        var edian = scn.GetLSCard("edian");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, edian, trooper);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(2, scn.GetPower(edian));
    }

    @Test
    public void EdianMayTakeJustDrawnAlienDestintyIntoHandToRedrawOncePerTurn() {
        var scn = GetScenario();
        var edian = scn.GetLSCard("edian");
        var boshek = scn.GetLSCard("boshek");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, edian, scn.GetLSCard("han"), trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(3);
        scn.MoveCardsToTopOfLSReserveDeck(boshek);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        // Bo Shek (an alien) was just drawn for battle destiny
        assertTrue(edianRedrawOffered(scn, edian));
        scn.LSUseCardAction(edian, "Take destiny card into hand");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, boshek);
    }

    @Test
    public void EdianDoesNotRedrawANonAlienDestiny() {
        var scn = GetScenario();
        var edian = scn.GetLSCard("edian");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, edian, scn.GetLSCard("han"), trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(3);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        assertFalse(edianRedrawOffered(scn, edian));
    }

    /**
     * Passes responses after a battle destiny draw until Sergeant Edian's action is offered to Light Side, or the
     * destiny-draw responses are over.
     */
    private static boolean edianRedrawOffered(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl edian) {
        for (int i = 0; i < 12; i++) {
            if (scn.LSAnyDecisionsAvailable() && scn.LSCardActionAvailable(edian, "Take destiny card into hand")) {
                return true;
            }
            if (!scn.IsActiveBattle() || scn.IsReachedDamageSegment()) {
                return false;
            }
            if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            } else if (scn.LSAnyDecisionsAvailable()) {
                if (scn.LSDecisionAvailable("battle destiny?")) {
                    return false;
                }
                scn.LSPass();
            } else {
                return false;
            }
        }
        return false;
    }
}
