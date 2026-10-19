package com.gempukku.swccgo.cards.set1210.dark;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_082_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("bantha", "1210_82");
                    put("urorru", "1210_25");
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
    public void URoRRuRRRsBanthaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bantha").getBlueprint();

        assertEquals("URoRRuR'R'R's Bantha", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getManeuver(), scn.epsilon);
        assertEquals(1, card.getLandspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(CardSubtype.CREATURE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BANTHA);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void URoRRuRRRsBanthaDeploysOnTatooine() {
        var scn = GetScenario();
        var bantha = scn.GetDSCard("bantha");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(bantha);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSDeployAvailable(bantha));
        scn.DSDeployCard(bantha);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertAtLocation(site, bantha);
        assertEquals(Zone.AT_LOCATION, bantha.getZone());
    }

    @Test
    public void URoRRuRRRsBanthaDeploysURoRRuFromReserveOncePerPhase() {
        var scn = GetScenario();
        var bantha = scn.GetDSCard("bantha");
        var urorru = scn.GetDSCard("urorru");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bantha);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(urorru);
        assertTrue(scn.DSCardActionAvailable(bantha, "Deploy URoRRu"));
        scn.DSUseCardAction(bantha, "Deploy URoRRu");
        assertTrue(scn.DSDecisionAvailable("Choose card"));
        scn.DSChooseCard(urorru);
        assertTrue(scn.DSDecisionAvailable("Choose"));
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertAtLocation(site, urorru);
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        if (scn.AwaitingDSDeployPhaseActions()) {
            assertFalse(scn.DSCardActionAvailable(bantha, "Deploy URoRRu"));
        }
    }

    @Test
    public void URoRRuRRRsBanthaHasAbilityOneHalf() {
        var scn = GetScenario();
        var bantha = scn.GetDSCard("bantha");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bantha);

        assertEquals(0.5f, scn.game().getModifiersQuerying().getAbility(scn.gameState(), bantha), 0.01f);
    }

    @Test
    public void URoRRuRRRsBanthaAddsOneBattleDestinyWithURoRRuAboard() {
        var scn = GetScenario();
        var bantha = scn.GetDSCard("bantha");
        var urorru = scn.GetDSCard("urorru");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bantha, luke);
        scn.BoardAsPassenger(bantha, urorru);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertEquals(1, scn.GetDSBattleDestinyCount());
    }
}
