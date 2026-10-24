package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertTrue;

public class Card_1220_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("probe", "1220_15");
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                    put("trooper3", "1_194");
                    put("trooper4", "1_194");
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
    public void ProbeDroidStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("probe").getBlueprint();

        assertEquals("Probe Droid", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertTrue(card.getModelTypes().contains(ModelType.PROBE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ProbeDroidDeploysToStartingSite() {
        var scn = GetScenario();
        var probe = scn.GetDSCard("probe");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(probe);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(probe));
        scn.DSPlayCard(probe);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, probe.getAtLocation());
    }

    @Test
    public void ProbeDroidProvidesPresenceAndMayInitiateBattle() {
        var scn = GetScenario();
        var probe = scn.GetDSCard("probe");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, probe, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.gameState().isDuringBattle());
    }

    @Test
    public void ProbeDroidReducesImperialDeployCostAtSameSiteThricePerTurn() {
        var scn = GetScenario();
        var probe = scn.GetDSCard("probe");
        var trooper1 = scn.GetDSCard("trooper1");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, probe);
        assertEquals(0, deployCostTo(scn, trooper1, site), scn.epsilon);
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
