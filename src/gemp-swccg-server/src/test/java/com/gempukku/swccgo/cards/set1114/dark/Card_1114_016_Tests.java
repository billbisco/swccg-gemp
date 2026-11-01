package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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

public class Card_1114_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("accuser", "1114_16");
                    put("tatooine", "1_289");
                    put("endor", "8_157");
                    put("ds2", "9_142");
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
    public void AccuserStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("accuser").getBlueprint();
        assertEquals("Accuser", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(8, card.getDeployCost(), scn.epsilon);
        assertEquals(7, card.getPower(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(9, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.STARSHIP); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.DEATH_STAR_II);
            add(Icon.STARSHIP);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_14, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AccuserDeploysMinusThreeIfDeathStarIIOnTable() {
        var scn = GetScenario();
        var accuser = scn.GetDSCard("accuser");
        var endor = scn.GetDSCard("endor");
        var ds2 = scn.GetDSCard("ds2");
        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveLocationToTable(ds2);
        assertEquals(5, scn.GetDeployCost(accuser), scn.epsilon);
    }
}
