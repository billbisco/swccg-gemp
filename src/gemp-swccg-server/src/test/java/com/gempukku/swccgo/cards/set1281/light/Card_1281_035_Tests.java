package com.gempukku.swccgo.cards.set1281.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_035_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("charm", "1281_35");
                    put("lando", "601_50");
                    put("peek1", "1_11");
                    put("peek2", "1_19");
                    put("peek3", "1_27");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void ScoundrelsCharmStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("charm").getBlueprint();

        assertEquals("Scoundrel's Charm", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SCOUNDREL);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScoundrelsCharmPeeksAtTopThreeOfForcePileAndTakesOneIntoHand() {
        var scn = GetScenario();
        var charm = scn.GetLSCard("charm");
        var lando = scn.GetLSCard("lando");
        var peek1 = scn.GetLSCard("peek1");
        var peek2 = scn.GetLSCard("peek2");
        var peek3 = scn.GetLSCard("peek3");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.AttachCardsTo(lando, charm);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSForcePile(peek3, peek2, peek1);

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(charm, "Peek at top 3 cards of Force Pile"));
        scn.LSUseCardAction(charm, "Peek at top 3 cards of Force Pile");
        scn.LSDecided("    0");
        scn.PassAllResponses();

        assertEquals(Zone.HAND, peek1.getZone());
    }
}
