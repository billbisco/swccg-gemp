package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_1292_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("boushh", "1292_6");
                    put("leia", "1_17");
                    put("lando", "601_50");
                }},
                new HashMap<>() {{
                    put("jabba", "6_109");
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
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
    public void BoushhVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("boushh").getBlueprint();

        assertEquals("Boushh", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
            add(Keyword.FEMALE);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LEIA);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.PREMIUM);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void deployBoushhTo(VirtualTableScenario scn, PhysicalCardImpl site) {
        var boushh = scn.GetLSCard("boushh");
        scn.MoveCardsToLSHand(boushh);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(boushh));
        scn.LSDeployCard(boushh);
        assertTrue(scn.LSHasCardChoiceAvailable(site));
        scn.LSChooseCard(site);
    }

    @Test
    public void BoushhVMayDeployAsUndercoverSpyToTatooine() {
        var scn = GetScenario();
        var boushh = scn.GetLSCard("boushh");
        var tatooineSite = scn.GetDSStartingLocation();

        scn.StartGame();
                deployBoushhTo(scn, tatooineSite);
        assertTrue(scn.LSDecisionAvailable("Undercover spy"));
        scn.LSChooseYes();
        scn.PassAllResponses();

        assertSame(tatooineSite, boushh.getAtLocation());
        assertTrue(boushh.isUndercover());
    }

    @Test
    public void BoushhVMayDeployAsUndercoverSpyToSameSiteAsGangster() {
        var scn = GetScenario();
        var boushh = scn.GetLSCard("boushh");
        var cloudCitySite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(cloudCitySite, scn.GetDSCard("jabba"));
        deployBoushhTo(scn, cloudCitySite);
        assertTrue(scn.LSDecisionAvailable("Undercover spy"));
        scn.LSChooseYes();
        scn.PassAllResponses();

        assertTrue(boushh.isUndercover());
    }

    @Test
    public void BoushhVMayNotDeployAsUndercoverSpyElsewhere() {
        var scn = GetScenario();
        var boushh = scn.GetLSCard("boushh");
        var cloudCitySite = scn.GetLSStartingLocation();

        scn.StartGame();
        deployBoushhTo(scn, cloudCitySite);
        assertFalse(scn.LSDecisionAvailable("Undercover spy"));
        scn.PassAllResponses();

        assertSame(cloudCitySite, boushh.getAtLocation());
        assertFalse(boushh.isUndercover());
    }

    @Test
    public void BoushhVPowerPlusTwoWhileAloneOrWithScoundrel() {
        var scn = GetScenario();
        var boushh = scn.GetLSCard("boushh");
        var leia = scn.GetLSCard("leia");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();
        var otherSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boushh);
        assertEquals(5, scn.GetPower(boushh), scn.epsilon);

        scn.MoveCardsToLocation(site, leia);
        assertEquals(3, scn.GetPower(boushh), scn.epsilon);

        scn.MoveCardsToLocation(site, lando);
        assertTrue(scn.HasKeyword(lando, Keyword.SCOUNDREL));
        assertEquals(5, scn.GetPower(boushh), scn.epsilon);

        scn.MoveCardsToLocation(otherSite, lando);
        assertEquals(3, scn.GetPower(boushh), scn.epsilon);
    }

    @Test
    public void BoushhVImmuneToAttritionLessThanThree() {
        var scn = GetScenario();
        var boushh = scn.GetLSCard("boushh");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), boushh);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), boushh), scn.epsilon);
    }

    @Test
    public void BoushhVDuringBattleMayPeekAtTopOfReserveDeckAndPlaceItUnder() {
        var scn = GetScenario();
        var boushh = scn.GetLSCard("boushh");
        var trooper = scn.GetDSCard("trooper");
        var top = scn.GetDSCard("trooper2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boushh, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(2);
        assertFalse(scn.LSCardActionAvailable(boushh, "Peek"));
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.MoveCardsToTopOfDSReserveDeck(top);

        assertTrue(scn.LSCardActionAvailable(boushh, "Peek"));
        scn.LSUseCardAction(boushh, "Peek");
        assertTrue(scn.LSDecisionAvailable("Choose Reserve Deck"));
        scn.LSChooseCard(top);
        for (int i = 0; i < 6 && !scn.LSDecisionAvailable("under that Reserve Deck"); i++) {
            scn.PassResponses();
        }
        assertTrue(scn.LSDecisionAvailable("under that Reserve Deck"));
        scn.LSChooseYes();

        var dsReserve = scn.GetDSReserveDeck();
        assertSame(top, dsReserve.get(dsReserve.size() - 1));
    }
}
