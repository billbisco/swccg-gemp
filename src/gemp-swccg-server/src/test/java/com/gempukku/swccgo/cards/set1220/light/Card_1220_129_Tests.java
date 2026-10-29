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
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_129_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("nick", "1220_129");
                    put("dack", "3_4");
                    put("speeder", "3_69");
                    put("delta", "3_40");
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
    public void NickOfTimeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("nick").getBlueprint();

        assertEquals("Nick Of Time", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NickOfTimeDeploysOnTable() {
        var scn = GetScenario();
        var nick = scn.GetLSCard("nick");

        scn.StartGame();
        scn.MoveCardsToLSHand(nick);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(nick));
        scn.LSDeployCard(nick);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, nick);
    }

    @Test
    public void NickOfTimeTakesAttackPatternDeltaFromReserveDeckDuringControlPhase() {
        var scn = GetScenario();
        var nick = scn.GetLSCard("nick");
        var delta = scn.GetLSCard("delta");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(nick);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(delta);
        assertTrue(scn.LSCardActionAvailable(nick, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(nick, "Take card into hand from Reserve Deck");
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(delta)) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
            else {
                break;
            }
        }
        scn.LSChooseCard(delta);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, delta.getZone());
    }
}
