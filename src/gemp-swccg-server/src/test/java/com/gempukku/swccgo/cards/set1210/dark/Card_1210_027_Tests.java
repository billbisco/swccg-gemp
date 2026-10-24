package com.gempukku.swccgo.cards.set1210.dark;

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

public class Card_1210_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21");
                    put("luke", "1_19");
                    put("farm", "1_132");
                }},
                new HashMap<>() {{
                    put("vader", "1210_27");
                    put("interrupt", "1_244");
                    put("high", "1_241");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
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

    private static void passResponse(VirtualTableScenario scn, String player, String text) {
        assertTrue(player + " should be deciding on '" + text + "' but is deciding on: " + scn.GetAwaitingDecision(player),
                scn.DecisionAvailable(player, text));
        scn.PlayerPass(player);
    }

    @Test
    public void VaderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("vader").getBlueprint();

        assertEquals("Vader", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void VaderAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, vader);
        assertEquals(12, scn.GetPower(devastator));
    }

    @Test
    public void VaderMakesJediDefenseValueMinusOneAndForfeitMinusTwoAtSameSite() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obiwan);
        assertEquals(6, scn.GetDefense(obiwan));
        assertEquals(9, scn.GetForfeit(obiwan));
        scn.MoveCardsToLocation(site, vader);
        assertEquals(5, scn.GetDefense(obiwan));
        assertEquals(7, scn.GetForfeit(obiwan));
    }

    @Test
    public void VaderMakesJediDefenseValueMinusOneAndForfeitMinusTwoAtARelatedSite() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var obiwan = scn.GetLSCard("obiwan");
        var farm = scn.GetLSCard("farm");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(site, vader);
        scn.MoveCardsToLocation(farm, obiwan);
        assertEquals(5, scn.GetDefense(obiwan));
        assertEquals(7, scn.GetForfeit(obiwan));
    }

    @Test
    public void VaderDoesNotReduceJediAtAnUnrelatedSite() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var obiwan = scn.GetLSCard("obiwan");
        var dsSite = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dsSite, vader);
        scn.MoveCardsToLocation(lsSite, obiwan);
        assertEquals(6, scn.GetDefense(obiwan));
        assertEquals(9, scn.GetForfeit(obiwan));
    }

    @Test
    public void VaderImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
    }

    @Test
    public void VaderUsesAnInterruptOfDestinyLessThanFourFromHandInsteadOfBattleDestiny() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var interrupt = scn.GetDSCard("interrupt");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToDSHand(interrupt);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareDSDestiny(1);
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue("Vader substitute missing. decision=" + scn.DSGetDecision(),
                scn.DSCardActionAvailable(vader, "Substitute destiny"));
        scn.DSUseCardAction(vader, "Substitute destiny");
        scn.DSChooseCard(interrupt);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, interrupt);
        assertEquals(3, scn.GetDSTotalDestiny());
    }

    @Test
    public void VaderDoesNotOfferADestinyFourOrHigherInterruptFromHand() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var high = scn.GetDSCard("high");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToDSHand(high);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareDSDestiny(1);
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        passResponse(scn, scn.LS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.DS, "COST_TO_DRAW_DESTINY_CARD");
        passResponse(scn, scn.LS, "ABOUT_TO_DRAW_DESTINY_CARD");
        assertFalse("High-destiny Interrupt was offered. decision=" + scn.DSGetDecision(),
                scn.DSCardActionAvailable(vader, "Substitute destiny"));
    }
}
