package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("atst", "1109_2");
                    put("chewie", "2_3");
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
    public void ChewiesATSTStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("atst").getBlueprint();
        assertEquals("Chewie's AT-ST", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getLandspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotCapacity());
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.AT_ST);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.ENDOR);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ChewiesATSTMaySitAtASiteWithChewieAboard() {
        var scn = GetScenario();
        var atst = scn.GetLSCard("atst");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, atst);
        scn.BoardAsPilot(atst, chewie);
        assertAtLocation(site, atst);
        assertEquals(atst, chewie.getAttachedTo());
    }

    @Test
    public void ChewiesATSTMayTargetDuringBattleIfChewieAboard() {
        var scn = GetScenario();
        var atst = scn.GetLSCard("atst");
        var chewie = scn.GetLSCard("chewie");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, atst, trooper);
        scn.BoardAsPilot(atst, chewie);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(atst, "Target character or AT-ST"));
    }
}
