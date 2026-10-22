package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Card_1291_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("muunilinst", "1291_20");
                    put("harnaidan", "1291_22");
                    put("vader", "1_168");
                    put("tie", "1_304");
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
    public void MuunilinstStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("muunilinst").getBlueprint();

        assertEquals("Muunilinst", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(7, card.getParsec());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.EPISODE_I);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MuunilinstAttritionAgainstOpponentIsPlus1WhenYouControl() {
        var scn = GetScenario();
        var muunilinst = scn.GetDSCard("muunilinst");
        var harnaidan = scn.GetDSCard("harnaidan");
        var tie = scn.GetDSCard("tie");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveLocationToTable(muunilinst);
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(muunilinst, tie);
        scn.MoveCardsToLocation(harnaidan, vader, luke, leia);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(harnaidan);
        scn.PrepareDSDestiny(0);
        scn.SkipToDamageSegment(true);

        assertEquals(2, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void MuunilinstLightAttritionAgainstOpponentIsPlus1WhenLightControls() {
        var scn = GetScenario();
        var muunilinst = scn.GetDSCard("muunilinst");
        var harnaidan = scn.GetDSCard("harnaidan");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(muunilinst);
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(muunilinst, xwing);
        scn.BoardAsPilot(xwing, leia);
        scn.MoveCardsToLocation(harnaidan, luke, vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(harnaidan);
        scn.PrepareLSDestiny(1);
        scn.SkipToDamageSegment(true);

        assertEquals(2, scn.GetUnpaidDSAttrition());
    }
}
