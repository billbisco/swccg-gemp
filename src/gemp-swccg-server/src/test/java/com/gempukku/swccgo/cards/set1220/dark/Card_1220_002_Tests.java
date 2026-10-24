package com.gempukku.swccgo.cards.set1220.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("boba", "1220_2");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("bounty", "5_113");
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
    public void BobaFettStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("boba").getBlueprint();

        assertEquals("Boba Fett", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BOBA_FETT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BobaFettDeploysToStartingSite() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(boba);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(boba));
        scn.DSPlayCard(boba);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, boba.getAtLocation());
    }

    @Test
    public void BobaFettAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, boba);
        assertEquals(12, scn.GetPower(devastator));
    }

    @Test
    public void BobaFettFliesWithLandspeedThree() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba);
        assertEquals(3, scn.GetLandspeed(boba));
    }

    @Test
    public void BobaFettIsImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), boba), scn.epsilon);
    }

    @Test
    public void BobaFettMakesBountyCardsImmuneToAimHigh() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var bounty = scn.GetDSCard("bounty");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba);
        scn.AttachCardsTo(scn.GetLSCard("luke"), bounty);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), bounty, Title.Aim_High));
    }

    @Test
    public void BobaFettTakesABountyCardIntoHandDuringControlPhase() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var bounty = scn.GetDSCard("bounty");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(bounty);
        assertTrue(scn.DSCardActionAvailable(boba, "Take a 'Bounty' card into hand from Reserve Deck"));
        scn.DSUseCardAction(boba, "Take a 'Bounty' card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(bounty);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, bounty.getZone());
    }
}
