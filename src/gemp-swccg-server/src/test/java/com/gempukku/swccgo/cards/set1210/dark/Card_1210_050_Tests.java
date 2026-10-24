package com.gempukku.swccgo.cards.set1210.dark;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_050_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127");
                    put("darkhours", "9_23");
                }},
                new HashMap<>() {{
                    put("sunsdown", "1210_50");
                    put("garindan", "1_177");
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
    public void SunsdownStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sunsdown").getBlueprint();

        assertEquals("Sunsdown", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SunsdownDeploysOnTable() {
        var scn = GetScenario();
        var sunsdown = scn.GetDSCard("sunsdown");

        scn.StartGame();
        scn.MoveCardsToDSHand(sunsdown);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(3);

        assertTrue(scn.DSCardPlayAvailable(sunsdown));
        scn.DSPlayCard(sunsdown);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, sunsdown);
    }

    @Test
    public void SunsdownMakesRelatedSitesNighttimeAfterTargetingASystem() {
        var scn = GetScenario();
        var sunsdown = scn.GetDSCard("sunsdown");
        var tatooine = scn.GetLSCard("tatooine");
        var marketplace = scn.GetDSStartingLocation();
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToDSSideOfTable(sunsdown);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.IsNighttimeAt(marketplace));
        assertTrue(scn.DSCardActionAvailable(sunsdown, "Target a system"));
        scn.DSUseCardAction(sunsdown, "Target a system");
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();

        assertTrue(scn.IsNighttimeAt(marketplace));
        assertFalse(scn.IsNighttimeAt(walkway));
    }
}
