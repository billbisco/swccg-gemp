package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class Card_1111_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("artoo", "1111_25");
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
    public void IveLostArtooStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("artoo").getBlueprint();
        assertEquals("I've Lost Artoo!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_11, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IveLostArtooDeploysOnTableForTwoForce() {
        var scn = GetScenario();
        var artoo = scn.GetDSCard("artoo");
        scn.StartGame();
        scn.MoveCardsToDSHand(artoo);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(artoo) || scn.DSDeployAvailable(artoo));
        if (scn.DSDeployAvailable(artoo)) {
            scn.DSDeployCard(artoo);
        } else {
            scn.DSPlayCard(artoo);
        }
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, artoo);
        assertEquals(force - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void IveLostArtooPlacesInLostPileToActivateOneForce() {
        var scn = GetScenario();
        var artoo = scn.GetDSCard("artoo");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(artoo);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(artoo, "Place in Lost Pile to activate 1 Force"));
        scn.DSUseCardAction(artoo, "Place in Lost Pile to activate 1 Force");
        scn.PassAllResponses();
        assertTrue(artoo.getZone() == Zone.LOST_PILE || artoo.getZone() == Zone.TOP_OF_LOST_PILE);
        assertEquals(force + 1, scn.GetDSForcePileCount());
    }
}
