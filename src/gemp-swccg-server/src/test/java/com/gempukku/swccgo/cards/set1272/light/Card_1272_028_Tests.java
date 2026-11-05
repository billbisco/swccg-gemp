package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("roche", "1272_28");
                }},
                new HashMap<>() {{
                    put("cpi", "2_130");
                    put("isd", "1_302");
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
    public void RocheVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("roche").getBlueprint();

        assertEquals(Title.Roche, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(7, card.getParsec());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.PLANET);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RocheVCommencePrimaryIgnitionMayNotTargetItUnlessOpponentControlsIt() {
        var scn = GetScenario();
        var roche = scn.GetLSCard("roche");
        var cpi = scn.GetDSCard("cpi");
        var isd = scn.GetDSCard("isd");

        //test1: Commence Primary Ignition may not target Roche while opponent does not control it
        //test2: Commence Primary Ignition may target Roche once opponent controls it
        scn.StartGame();
        scn.MoveLocationToTable(roche);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), roche, cpi)); //test1
        scn.MoveCardsToLocation(roche, isd);
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), roche, cpi)); //test2
    }

    @Test
    public void RocheVDarkSideForceDrainIsMinusOneHere() {
        var scn = GetScenario();
        var roche = scn.GetLSCard("roche");
        var isd = scn.GetDSCard("isd");

        //test1: Dark Side Force drains for 2 Light Side icons - 1 = 1
        scn.StartGame();
        scn.MoveLocationToTable(roche);
        scn.MoveCardsToLocation(roche, isd);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(roche));
        scn.DSForceDrainAt(roche);
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal()); //test1
    }
}
