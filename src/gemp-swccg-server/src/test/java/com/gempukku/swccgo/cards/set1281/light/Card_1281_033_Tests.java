package com.gempukku.swccgo.cards.set1281.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("redeemed", "1281_33");
                    put("jawa", "1_12");
                    put("nabrun", "1_97");
                }},
                new HashMap<>() {{
                    put("bast", "1_165");
                    put("elis", "1_243");
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
    public void RedeemedApprenticeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("redeemed").getBlueprint();

        assertEquals("Redeemed Apprentice", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RedeemedApprenticeDeploysAfterYouForfeitACharacter() {
        var scn = GetScenario();
        var redeemed = scn.GetLSCard("redeemed");
        var jawa = scn.GetLSCard("jawa");
        var bast = scn.GetDSCard("bast");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, bast);
        scn.MoveCardsToLSHand(redeemed);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);

        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertTrue(scn.LSHasCardChoiceAvailable(jawa));
        scn.LSChooseCard(jawa);
        scn.DSPass();

        assertTrue(scn.GetLSAvailableActions().toString().contains("Deploy due to"));
        scn.LSChooseAction("Deploy due to");
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, redeemed);
    }

    @Test
    public void RedeemedApprenticeCancelsElisHelrotAndNabrunLeids() {
        var scn = GetScenario();
        var redeemed = scn.GetLSCard("redeemed");
        var nabrun = scn.GetLSCard("nabrun");
        var elis = scn.GetDSCard("elis");
        var site = scn.GetLSStartingLocation();

        var jawa = scn.GetLSCard("jawa");
        var bast = scn.GetDSCard("bast");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa);
        scn.MoveCardsToLocation(dsSite, bast);
        scn.MoveCardsToLSHand(nabrun);
        scn.MoveCardsToDSHand(elis);
        scn.LSActivateForceCheat(8);
        scn.DSActivateForceCheat(8);

        // Without Redeemed Apprentice on table, both Interrupts are playable.
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardPlayAvailable(nabrun));
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.DSCardPlayAvailable(elis));

        scn.MoveCardsToLSSideOfTable(redeemed);
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.LSCardPlayAvailable(nabrun));
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(scn.DSCardPlayAvailable(elis));
    }
}
