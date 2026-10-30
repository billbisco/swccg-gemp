package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dantooine", "1103_22");
                    put("ops", "601_257");
                    put("luke", "1_19");
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
    public void TheyreOnDantooineStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dantooine").getBlueprint();

        assertEquals("They're On Dantooine", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheyreOnDantooineDeploysOnTable() {
        var scn = GetScenario();
        var dantooine = scn.GetLSCard("dantooine");

        scn.StartGame();
        scn.MoveCardsToLSHand(dantooine);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(dantooine) || scn.LSDeployAvailable(dantooine));
        scn.LSPlayCard(dantooine);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, dantooine);
    }

    @Test
    public void TheyreOnDantooineMakesDantooineSitesImmuneToNoEscapeAndAddsForceGeneration() {
        var scn = GetScenario();
        var dantooine = scn.GetLSCard("dantooine");
        var ops = scn.GetLSCard("ops");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(ops);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), ops, scn.LS);
        scn.MoveCardsToLSSideOfTable(dantooine);
        scn.MoveCardsToLocation(ops, luke);

        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), ops, Title.No_Escape));
        assertEquals(before + 1, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), ops, scn.LS), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), dantooine));
    }
}
