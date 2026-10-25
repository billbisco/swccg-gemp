package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static org.junit.Assert.assertTrue;

public class Card_1220_093_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("kebyc", "1220_93");
                    put("trooper", "5_3");
                }},
                new HashMap<>() {{
                    put("boba", "5_91");
                    put("trooperDS", "1_194");
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
    public void KebycStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("kebyc").getBlueprint();

        assertEquals("Kebyc", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KebycDeploysFreeToCloudCity() {
        var scn = GetScenario();
        var kebyc = scn.GetLSCard("kebyc");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        assertEquals(0, deployCostTo(scn, kebyc, site), scn.epsilon);
    }

    @Test
    public void KebycAddsOneForceGenerationForEachCloudCitySiteYouOccupy() {
        var scn = GetScenario();
        var kebyc = scn.GetLSCard("kebyc");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        float before = scn.game().getModifiersQuerying().getTotalForceGeneration(scn.gameState(), scn.LS);
        scn.MoveCardsToLocation(site, kebyc);
        float after = scn.game().getModifiersQuerying().getTotalForceGeneration(scn.gameState(), scn.LS);
        assertEquals(before + 1, after, scn.epsilon);
    }

    @Test
    public void KebycMakesYourAliensDeployMinusOneHereAndOpponentsAliensDeployPlusOneHere() {
        var scn = GetScenario();
        var kebyc = scn.GetLSCard("kebyc");
        var trooper = scn.GetLSCard("trooper");
        var boba = scn.GetDSCard("boba");
        var trooperDS = scn.GetDSCard("trooperDS");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kebyc);
        assertEquals(1, deployCostTo(scn, trooper, site), scn.epsilon);
        assertEquals(boba.getBlueprint().getDeployCost().intValue() + 1, deployCostTo(scn, boba, site), scn.epsilon);
        assertEquals(trooperDS.getBlueprint().getDeployCost().intValue(), deployCostTo(scn, trooperDS, site), scn.epsilon);
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
