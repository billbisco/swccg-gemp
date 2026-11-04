package com.gempukku.swccgo.cards.set1292.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_1292_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dexter", "1292_8");
                    put("obiwan", "1_21");
                    put("coruscant", "12_74");
                }},
                new HashMap<>() {{
                    put("jango", "201_25");
                    put("trooper", "1_194");
                    put("keder", "12_109");
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
    public void DexterJettsterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dexter").getBlueprint();

        assertEquals("Dexter Jettster", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(Species.BESALISK, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.INFORMATION_BROKER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private int forceUsedToDeployTo(VirtualTableScenario scn, PhysicalCardImpl site) {
        var dexter = scn.GetLSCard("dexter");
        scn.MoveCardsToLSHand(dexter);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(dexter));
        scn.LSDeployCard(dexter);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertSame(site, dexter.getAtLocation());
        return force - scn.GetLSForcePileCount();
    }

    @Test
    public void DexterJettsterDeploysMinusOneToCoruscant() {
        var scn = GetScenario();
        var coruscant = scn.GetLSCard("coruscant");
        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        assertEquals(2, forceUsedToDeployTo(scn, coruscant));
    }

    @Test
    public void DexterJettsterDeploysMinusOneToSameLocationAsObiWan() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"));
        assertEquals(2, forceUsedToDeployTo(scn, site));
    }

    @Test
    public void DexterJettsterDeploysAtNormalCostElsewhere() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        assertEquals(3, forceUsedToDeployTo(scn, site));
    }

    private int lsBattleDestinyCountAgainst(VirtualTableScenario scn, PhysicalCardImpl opponentsCharacter) {
        var dexter = scn.GetLSCard("dexter");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, dexter, opponentsCharacter);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(2);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        return scn.GetLSBattleDestinyCount();
    }

    @Test
    public void DexterJettsterAddsOneBattleDestinyWithJango() {
        var scn = GetScenario();
        int withJango = lsBattleDestinyCountAgainst(scn, scn.GetDSCard("jango"));
        var scn2 = GetScenario();
        int withTrooper = lsBattleDestinyCountAgainst(scn2, scn2.GetDSCard("trooper"));
        assertEquals(withTrooper + 1, withJango);
    }

    @Test
    public void DexterJettsterBreaksCoverOfUndercoverSpyForPrintedDeployCostMinusTwo() {
        var scn = GetScenario();
        var dexter = scn.GetLSCard("dexter");
        var keder = scn.GetDSCard("keder");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dexter, keder);
        scn.MakeCardGoUndercover(keder);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(keder.isUndercover());
        assertFalse(scn.LSCardActionAvailable(dexter, "Break cover"));

        scn.SkipToPhase(Phase.MOVE);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(dexter, "Break cover"));
        scn.LSUseCardAction(dexter, "Break cover");
        if (scn.LSDecisionAvailable("Undercover spy")) {
            scn.LSChooseCard(keder);
        }
        scn.PassAllResponses();

        assertFalse(keder.isUndercover());
        // Keder The Black: printed deploy cost 4, so X = 2
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }
}
