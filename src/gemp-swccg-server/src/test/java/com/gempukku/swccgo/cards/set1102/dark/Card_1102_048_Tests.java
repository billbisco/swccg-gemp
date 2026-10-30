package com.gempukku.swccgo.cards.set1102.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_048_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("empires", "1102_48");
                    put("motti", "1210_3");
                    put("tagge", "1210_10");
                    put("tarkin", "1210_23");
                    put("vader", "1210_7");
                    put("moseisley", "1_295");
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
    public void TheEmpiresBackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("empires").getBlueprint();

        assertEquals("The Empire's Back", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheEmpiresBackDeploysMottiTaggeTarkinOrVaderFromReserveDeck() {
        var scn = GetScenario();
        var empires = scn.GetDSCard("empires");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(empires);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(motti);
        scn.EnsureDSForcePile(1);

        assertTrue(scn.DSCardPlayAvailable(empires, "Deploy from Reserve Deck"));
        scn.DSPlayCard(empires, "Deploy from Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.DSHasCardChoiceAvailable(motti));
        scn.DSChooseCard(motti);
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
            scn.PassAllResponses();
        }

        assertAtLocation(site, motti);
        assertInZone(Zone.LOST_PILE, empires);
    }

    @Test
    public void TheEmpiresBackRetrievesMottiTaggeTarkinOrVaderFromLostPile() {
        var scn = GetScenario();
        var empires = scn.GetDSCard("empires");
        var tarkin = scn.GetDSCard("tarkin");

        scn.StartGame();
        scn.MoveCardsToDSHand(empires);
        scn.MoveCardsToTopOfDSLostPile(tarkin);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(1);

        assertTrue(scn.DSCardPlayAvailable(empires, "Retrieve from Lost Pile"));
        scn.DSPlayCard(empires, "Retrieve from Lost Pile");
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(tarkin)) {
            scn.DSChooseCard(tarkin);
        }
        scn.PassAllResponses();

        assertTrue(tarkin.getZone() == Zone.USED_PILE || tarkin.getZone() == Zone.TOP_OF_USED_PILE
                || tarkin.getZone() == Zone.HAND);
        assertInZone(Zone.LOST_PILE, empires);
    }

    @Test
    public void TheEmpiresBackMakesOpponentLoseOneForcePerBattlegroundThoseUniquesControl() {
        var scn = GetScenario();
        var empires = scn.GetDSCard("empires");
        var motti = scn.GetDSCard("motti");
        var vader = scn.GetDSCard("vader");
        var moseisley = scn.GetDSCard("moseisley");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(moseisley);
        scn.MoveCardsToLocation(site, motti);
        scn.MoveCardsToLocation(moseisley, vader);
        scn.MoveCardsToDSHand(empires);
        scn.SkipToDSTurn(Phase.CONTROL);

        int lsBefore = scn.GetLSLifeForceRemaining();
        assertTrue(scn.DSCardPlayAvailable(empires, "Make opponent lose Force"));
        scn.DSPlayCard(empires, "Make opponent lose Force");
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose Force to lose")) {
            scn.LSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }

        assertEquals(lsBefore - 2, scn.GetLSLifeForceRemaining());
        assertInZone(Zone.LOST_PILE, empires);
    }
}
