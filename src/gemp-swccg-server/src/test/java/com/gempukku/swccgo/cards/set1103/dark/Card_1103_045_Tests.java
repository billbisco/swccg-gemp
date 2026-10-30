package com.gempukku.swccgo.cards.set1103.dark;

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

public class Card_1103_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("threepio", "1_5");
                    put("menace", "9_38");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("rodian1", "1103_45");
                    put("rodian2", "1103_45");
                    put("boba", "5_91");
                    put("greedo", "2_89");
                    put("farm", "1_294");
                    put("cantina", "1_290");
                    put("tatooine", "1_289");
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
    public void RodianStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("rodian1").getBlueprint();

        assertEquals("Rodian", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ASSASSIN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RodianDeploysBountyHunterExceptGreedoToSameSiteOncePerGame() {
        var scn = GetScenario();
        var rodian1 = scn.GetDSCard("rodian1");
        var boba = scn.GetDSCard("boba");
        var greedo = scn.GetDSCard("greedo");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rodian1);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(6);
        scn.MoveCardsToTopOfDSReserveDeck(boba, greedo);

        assertTrue(scn.DSCardActionAvailable(rodian1, "Deploy bounty hunter from Reserve Deck"));
        scn.DSUseCardAction(rodian1, "Deploy bounty hunter from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(boba);
        scn.PassAllResponses();
        assertEquals(site, boba.getAtLocation());
        assertFalse(greedo.getZone().isInPlay());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Deploy bounty hunter from Reserve Deck"));
    }

    @Test
    public void RodianForceDrainBonusesAtSamePlanetSiteMayNotBeCanceled() {
        var scn = GetScenario();
        var rodian1 = scn.GetDSCard("rodian1");
        var farm = scn.GetDSCard("farm");
        var menace = scn.GetLSCard("menace");
        var yavin = scn.GetLSCard("yavin");
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(farm, rodian1);
        scn.MoveCardsToLocation(yavin, luke);
        scn.MoveCardsToLocation(lsSite, han);
        scn.MoveCardsToLSSideOfTable(menace);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm));
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void RodianForceDrainBonusesMayBeCanceledWhenAnotherRestrictedThreeRodianIsAtSameSite() {
        var scn = GetScenario();
        var rodian1 = scn.GetDSCard("rodian1");
        var rodian2 = scn.GetDSCard("rodian2");
        var farm = scn.GetDSCard("farm");
        var menace = scn.GetLSCard("menace");
        var yavin = scn.GetLSCard("yavin");
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(farm, rodian1, rodian2);
        assertEquals(farm, rodian1.getAtLocation());
        assertEquals(farm, rodian2.getAtLocation());
        scn.MoveCardsToLocation(yavin, luke);
        scn.MoveCardsToLocation(lsSite, han);
        scn.MoveCardsToLSSideOfTable(menace);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm));
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void RodianForceDrainBonusesMayBeCanceledWhenAnotherRestrictedThreeRodianIsAtRelatedSite() {
        var scn = GetScenario();
        var rodian1 = scn.GetDSCard("rodian1");
        var rodian2 = scn.GetDSCard("rodian2");
        var farm = scn.GetDSCard("farm");
        var cantina = scn.GetDSCard("cantina");
        var menace = scn.GetLSCard("menace");
        var yavin = scn.GetLSCard("yavin");
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("tatooine"));
        scn.MoveLocationToTable(farm);
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(farm, rodian1);
        scn.MoveCardsToLocation(cantina, rodian2);
        assertEquals(farm, rodian1.getAtLocation());
        assertEquals(cantina, rodian2.getAtLocation());
        scn.MoveCardsToLocation(yavin, luke);
        scn.MoveCardsToLocation(lsSite, han);
        scn.MoveCardsToLSSideOfTable(menace);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm));
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }
}
