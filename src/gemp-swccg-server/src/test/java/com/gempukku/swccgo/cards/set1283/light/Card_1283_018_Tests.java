package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1283_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("utris", "1283_18");
                    put("lando", "5_5");
                    put("lobot", "5_6");
                    put("boshek", "1_4");
                    put("cctrooper", "5_3");
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
    public void TrooperUtrisMTocVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("utris").getBlueprint();

        assertEquals("Trooper Utris M'Toc", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(Species.IMZIG, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CLOUD_CITY_TROOPER);
            add(Keyword.LEADER);
            add(Keyword.GUARD);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void deployAndCheckCost(String partner, int expectedCost, boolean partnerHere) {
        var scn = GetScenario();
        var utris = scn.GetLSCard("utris");
        var site = scn.GetLSStartingLocation();
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(partnerHere ? site : marketplace, scn.GetLSCard(partner));
        scn.MoveCardsToLSHand(utris);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(utris);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(site, utris));
        assertEquals(force - expectedCost, scn.GetLSForcePileCount());
    }

    @Test
    public void UtrisDeploysFreeToSameSiteAsLando() {
        deployAndCheckCost("lando", 0, true);
    }

    @Test
    public void UtrisDeploysFreeToSameSiteAsLobot() {
        deployAndCheckCost("lobot", 0, true);
    }

    @Test
    public void UtrisPaysFullCostWithoutLandoOrLobotThere() {
        deployAndCheckCost("lando", 2, false);
    }

    @Test
    public void UtrisIsPowerPlusTwoWhileDefendingABattle() {
        var scn = GetScenario();
        var utris = scn.GetLSCard("utris");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, utris, trooper);
        assertEquals(2, scn.GetPower(utris));
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(4, scn.GetPower(utris));
    }

    @Test
    public void UtrisIsNotPowerPlusTwoWhenHisSideInitiates() {
        var scn = GetScenario();
        var utris = scn.GetLSCard("utris");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, utris, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(2, scn.GetPower(utris));
    }

    @Test
    public void UtrisMakesYourAliensForfeitPlusOneAndTroopersPlusTwoWhilePresentAtABattleground() {
        var scn = GetScenario();
        var utris = scn.GetLSCard("utris");
        var boshek = scn.GetLSCard("boshek");
        var cctrooper = scn.GetLSCard("cctrooper");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boshek, cctrooper, han);
        int boshekBase = scn.GetForfeit(boshek);
        int trooperBase = scn.GetForfeit(cctrooper);
        int hanBase = scn.GetForfeit(han);
        scn.MoveCardsToLocation(site, utris);
        assertEquals(boshekBase + 1, scn.GetForfeit(boshek));
        assertEquals(trooperBase + 2, scn.GetForfeit(cctrooper));
        assertEquals(hanBase, scn.GetForfeit(han));
        assertEquals(4 + 2, scn.GetForfeit(utris));
    }
}
