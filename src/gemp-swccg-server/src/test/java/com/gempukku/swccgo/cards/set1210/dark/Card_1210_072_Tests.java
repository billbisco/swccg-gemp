package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1210_072_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("allwings", "7_82");
                    put("organized", "7_97");
                    put("xwing", "1_146");
                    put("biggs", "1_3");
                }},
                new HashMap<>() {{
                    put("black2", "1210_72");
                    put("ds612", "1_173");
                    put("pride", "7_236");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void Black2StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("black2").getBlueprint();

        assertEquals("Black 2", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertNull(card.getArmor());
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertNull(card.getHyperspeed());
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.TIE_LN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.NO_HYPERDRIVE);
            add(Keyword.BLACK_SQUADRON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Black2TakesPrideOfTheEmpireFromReserveDeckDuringDeploy() {
        var scn = GetScenario();
        var black2 = scn.GetDSCard("black2");
        var pride = scn.GetDSCard("pride");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, black2);
        scn.BoardAsPilot(black2, scn.GetDSCard("ds612"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(1);
        scn.MoveCardsToTopOfDSReserveDeck(pride);
        assertTrue(scn.DSCardActionAvailable(black2, "Take card into hand from Reserve Deck"));
        scn.DSUseCardAction(black2, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(pride);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, pride.getZone());
    }

    @Test
    public void Black2CancelsAllWingsReportInBeingPlayed() {
        var scn = GetScenario();
        var black2 = scn.GetDSCard("black2");
        var allwings = scn.GetLSCard("allwings");
        var xwing = scn.GetLSCard("xwing");
        var dsSystem = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dsSystem, black2);
        scn.BoardAsPilot(black2, scn.GetDSCard("ds612"));
        scn.MoveCardsToLSHand(allwings);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(xwing);
        assertTrue(scn.LSCardPlayAvailable(allwings, "Take card into hand from Reserve Deck"));
        scn.LSPlayCard(allwings, "Take card into hand from Reserve Deck");
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertTrue(allwings.getZone() == Zone.LOST_PILE || allwings.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(xwing.getZone() == Zone.RESERVE_DECK || xwing.getZone() == Zone.TOP_OF_RESERVE_DECK);
    }

    @Test
    public void Black2CancelsOrganizedAttackBeingPlayed() {
        var scn = GetScenario();
        var black2 = scn.GetDSCard("black2");
        var organized = scn.GetLSCard("organized");
        var xwing = scn.GetLSCard("xwing");
        var biggs = scn.GetLSCard("biggs");
        var dsSystem = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dsSystem, black2, xwing);
        scn.BoardAsPilot(black2, scn.GetDSCard("ds612"));
        scn.BoardAsPilot(xwing, biggs);
        scn.MoveCardsToLSHand(organized);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(dsSystem);
        scn.DSPass();
        assertTrue(scn.LSPlayUsedInterruptAvailable(organized));
        scn.LSPlayUsedInterrupt(organized);
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertTrue(organized.getZone() == Zone.LOST_PILE || organized.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void Black2IsImmuneToAttritionLessThanFiveWhileDS612Piloting() {
        var scn = GetScenario();
        var black2 = scn.GetDSCard("black2");
        var ds612 = scn.GetDSCard("ds612");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, black2);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black2), scn.epsilon);
        scn.BoardAsPilot(black2, ds612);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black2), scn.epsilon);
    }
}
