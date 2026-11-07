package com.gempukku.swccgo.cards.set1105.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1105_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shoo", "1105_20");
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
    public void ShooShooStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("shoo").getBlueprint();

        assertEquals("Shoo! Shoo!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle("Sense"));
    }

    private VirtualTableScenario GetReegeskScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shoo", "1105_20");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("reegesk", "1103_43");
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
    public void ShooShooOVSCancelsReegeskStealAndReegeskIsLost() {
        var scn = GetReegeskScenario();
        var shoo = scn.GetLSCard("shoo");
        var xwing = scn.GetLSCard("xwing");
        var reegesk = scn.GetDSCard("reegesk");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, reegesk);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.MoveCardsToLSHand(shoo);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();

        assertTrue(scn.DSCardActionAvailable(reegesk, "Steal card from Lost Pile"));
        scn.DSUseCardAction(reegesk, "Steal card from Lost Pile");

        assertTrue(scn.LSCardPlayAvailable(shoo, "Cancel Reegesk steal"));
        scn.LSPlayCard(shoo, "Cancel Reegesk steal");
        scn.PassAllResponses();

        assertTrue(scn.GetLSLostPile().contains(xwing));
        assertEquals(scn.LS, xwing.getOwner());
        assertTrue(scn.GetDSLostPile().contains(reegesk));
    }

    @Test
    public void ShooShooOVSNotUsedReegeskStealSucceeds() {
        var scn = GetReegeskScenario();
        var shoo = scn.GetLSCard("shoo");
        var xwing = scn.GetLSCard("xwing");
        var reegesk = scn.GetDSCard("reegesk");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, reegesk);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.MoveCardsToLSHand(shoo);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();

        scn.DSUseCardAction(reegesk, "Steal card from Lost Pile");
        assertTrue(scn.LSCardPlayAvailable(shoo, "Cancel Reegesk steal"));
        scn.PassAllResponses();
        scn.DSChooseCard(xwing);
        scn.PassAllResponses();

        assertEquals(Zone.HAND, xwing.getZone());
        assertEquals(scn.DS, xwing.getOwner());
        assertEquals(Zone.HAND, shoo.getZone());
        assertAtLocation(site, reegesk);
    }
}
