package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_119_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("encampment", "1220_119");
                    put("luke", "1_19");
                    put("dune", "1_130");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void EncampmentStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("encampment").getBlueprint();

        assertEquals("Encampment", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EncampmentDeploysOnOccupiedNonDockingBayExteriorPlanetSite() {
        var scn = GetScenario();
        var encampment = scn.GetLSCard("encampment");
        var luke = scn.GetLSCard("luke");
        var dune = scn.GetLSCard("dune");

        scn.StartGame();
        scn.MoveLocationToTable(dune);
        scn.MoveCardsToLocation(dune, luke);
        scn.MoveCardsToLSHand(encampment);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(encampment));
        scn.LSPlayCard(encampment);
        scn.LSChooseCard(dune);
        scn.PassAllResponses();
        assertEquals(dune, encampment.getAttachedTo());
    }

    @Test
    public void EncampmentReducesCharacterDeployCostHereByThreeOncePerGame() {
        var scn = GetScenario();
        var encampment = scn.GetLSCard("encampment");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var dune = scn.GetLSCard("dune");

        scn.StartGame();
        scn.MoveLocationToTable(dune);
        scn.MoveCardsToLocation(dune, luke);
        scn.AttachCardsTo(dune, encampment);
        float printed = leia.getBlueprint().getDeployCost();
        assertEquals(printed - 3, deployCostTo(scn, leia, dune), scn.epsilon);
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
