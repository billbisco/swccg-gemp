package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("revolution", "1_62");
                }},
                new HashMap<>() {{
                    put("office", "1272_11");
                    put("sector", "1240_53");
                    put("agent", "1_194");
                    put("agent2", "1_194");
                    put("commander", "1272_2");
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
    public void CorulagISBLogisticsOfficeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("office").getBlueprint();

        assertEquals("Corulag: ISB Logistics Office", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.DARK_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CorulagISBLogisticsOfficeDuringMovePhaseMayUseOneForceToRelocateUpToThreeISBAgents() {
        var scn = GetScenario();
        var office = scn.GetDSCard("office");
        var sector = scn.GetDSCard("sector");
        var agent = scn.GetDSCard("agent");
        var agent2 = scn.GetDSCard("agent2");

        scn.StartGame();
        scn.MoveLocationToTable(office);
        scn.MoveLocationToTable(sector);
        scn.MoveCardsToLocation(office, agent, agent2);
        scn.ApplyAdHocModifier(new KeywordModifier(office, Filters.or(Filters.sameCardId(agent), Filters.sameCardId(agent2)), Keyword.ISB_AGENT));
        scn.DSActivateForceCheat(2);
        scn.SkipToDSTurn(Phase.MOVE);

        assertTrue(scn.DSCardActionAvailable(office, "Relocate ISB agents"));
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(office, "Relocate ISB agents");
        assertTrue(scn.DSHasCardChoiceAvailable(office));
        scn.DSChooseCard(office);
        assertTrue(scn.DSHasCardChoiceAvailable(sector));
        scn.DSChooseCard(sector);
        assertTrue(scn.DSHasCardChoiceAvailable(agent));
        assertTrue(scn.DSHasCardChoiceAvailable(agent2));
        scn.DSChooseCards(agent, agent2);
        scn.PassAllResponses();
        assertAtLocation(sector, agent);
        assertAtLocation(sector, agent2);
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void CorulagISBLogisticsOfficeISBAgentsAreDeployPlusOneHereIfLightControls() {
        var scn = GetScenario();
        var office = scn.GetDSCard("office");
        var agent = scn.GetDSCard("agent");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(office);
        scn.ApplyAdHocModifier(new KeywordModifier(office, Filters.sameCardId(agent), Keyword.ISB_AGENT));
        float withoutControl = deployCostTo(scn, agent, office);
        scn.MoveCardsToLocation(office, luke);
        float withControl = deployCostTo(scn, agent, office);
        assertEquals(withoutControl + 1, withControl, scn.epsilon);
    }

    @Test
    public void CorulagISBLogisticsOfficeIsImmuneToRevolution() {
        var scn = GetScenario();
        var office = scn.GetDSCard("office");
        var revolution = scn.GetLSCard("revolution");

        scn.StartGame();
        scn.MoveLocationToTable(office);
        scn.MoveCardsToLSHand(revolution);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(revolution));
        scn.LSPlayCard(revolution);
        assertFalse(scn.LSHasCardChoiceAvailable(office));
    }

    private float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card,
                               com.gempukku.swccgo.game.PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
