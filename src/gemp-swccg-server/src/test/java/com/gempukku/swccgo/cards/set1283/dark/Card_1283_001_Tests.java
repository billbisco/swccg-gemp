package com.gempukku.swccgo.cards.set1283.dark;

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

public class Card_1283_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("underattack", "3_50");
                    put("trooper", "3_6"); // Echo Base Trooper (warrior)
                    put("projection", "6_56");
                    put("tatooine", "1_127"); // Tatooine system
                }},
                new HashMap<>() {{
                    put("commander", "1283_1");
                    put("blizzard2", "3_155"); // AT-AT
                    put("tie", "1_304"); // TIE Fighter, power 1
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
    public void ATATCommanderStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("commander").getBlueprint();
        assertEquals("AT-AT Commander", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ATATCommanderAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var commander = scn.GetDSCard("commander");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, commander);
        assertEquals(3, scn.GetPower(tie));
    }

    @Test
    public void ATATCommanderMayPeekAtTopOfForcePileOnceDuringEachOfYourTurns() {
        var scn = GetScenario();
        var commander = scn.GetDSCard("commander");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), commander);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(commander, "Peek at top card of Force Pile"));
        scn.DSUseCardAction(commander, "Peek at top card of Force Pile");
        scn.DSDismissRevealedCards();
        scn.PassResponses("LOOKED_AT_CARDS_IN_CARD_PILE");
        scn.PassAllResponses();
        assertFalse(scn.GetDSAvailableActions().contains("Peek at top card of Force Pile"));
    }

    @Test
    public void ATATCommanderSuspendsProjectionOfASkywalkerHere() {
        var scn = GetScenario();
        var commander = scn.GetDSCard("commander");
        var projection = scn.GetLSCard("projection");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.AttachCardsTo(site, projection);
        assertTrue(scn.IsCardActive(projection));
        scn.MoveCardsToLocation(site, commander);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(projection.isSuspended());
    }

    @Test
    public void ATATCommanderDoesNotSuspendProjectionElsewhere() {
        var scn = GetScenario();
        var commander = scn.GetDSCard("commander");
        var projection = scn.GetLSCard("projection");
        scn.StartGame();
        scn.AttachCardsTo(scn.GetLSStartingLocation(), projection);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), commander);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(projection.isSuspended());
    }

    @Test
    public void ATATCommanderMayGoToBottomOfUsedPileToCancelUnderAttackOnHisVehicle() {
        var scn = GetScenario();
        var commander = scn.GetDSCard("commander");
        var blizzard2 = scn.GetDSCard("blizzard2");
        var underattack = scn.GetLSCard("underattack");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard2, trooper);
        scn.BoardAsPilot(blizzard2, commander);
        scn.MoveCardsToHand(underattack);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(underattack));
        scn.LSPlayCard(underattack);
        scn.LSChooseCard(blizzard2);
        scn.LSChooseCard(trooper);
        if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
            scn.LSPass();
        }
        assertTrue(scn.DSCardActionAvailable(commander, "Cancel Under Attack"));
        scn.DSUseCardAction(commander, "Cancel Under Attack");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, commander);
        assertEquals(commander, scn.GetDSUsedPile().getLast());
        assertInZone(Zone.LOST_PILE, underattack);
        assertInZone(Zone.AT_LOCATION, blizzard2);
    }
}
