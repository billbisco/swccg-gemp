package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_008_Tests {
    private static final String PLACE = "Place a card from hand in Used Pile";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("lando", "112_3", "ben", "1_21", "alter", "1_71")),
                new HashMap<>(Map.of("drop", "1262_8", "aplek", "222_2", "trooper", "1_194", "spare", "1_324")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void DropPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("drop").getBlueprint();
        assertEquals("Drop!", b.getTitle());
        assertEquals("Anakin had to heed Qui-Gon's advice to avoid the rapidly approaching storm.", b.getLore());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void DropActuallyDeploysIsImmuneToAlterAndPlacesOnlyOneCardInUsedPerTurn() {
        var scn = scenario();
        var drop = scn.GetDSCard("drop");
        var trooper = scn.GetDSCard("trooper");
        var spare = scn.GetDSCard("spare");
        var alter = scn.GetLSCard("alter");
        scn.MoveCardsToDSHand(drop, trooper, spare);
        scn.MoveCardsToLSHand(alter);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("ben"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(drop);
        settleDeploy(scn);
        assertInZone(Zone.SIDE_OF_TABLE, drop);
        scn.DSUseCardAction(drop, PLACE);
        assertTrue(scn.DSHasCardChoiceAvailable(trooper));
        scn.DSChooseCard(trooper);
        settleDeploy(scn);
        assertInZone(Zone.USED_PILE, trooper);
        assertInZone(Zone.HAND, spare);
        assertFalse(scn.DSCardActionAvailable(drop, PLACE));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(alter));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(drop, PLACE));
    }

    @Test public void DropSuppressesActualExtraBattleDestinyFromPermanentWeaponsAndMaintenanceOnBothSides() {
        for (boolean dropOnTable : List.of(false, true)) {
            var scn = scenario();
            var drop = scn.GetDSCard("drop");
            var site = scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(site, scn.GetDSCard("aplek"), scn.GetLSCard("lando"));
            if (dropOnTable) scn.MoveCardsToDSSideOfTable(drop);
            scn.EnsureDSForcePile(2);
            scn.DSStartBattleAndSkipToWeaponsSegment(site);
            scn.PrepareDSDestiny(2);
            scn.PrepareDSDestiny(3);
            scn.PrepareLSDestiny(2);
            scn.PrepareLSDestiny(3);
            scn.SkipToPowerSegment();
            assertTrue(scn.DSDecisionAvailable("battle destiny?"));
            scn.DSChooseYes();
            scn.PassDestinyDrawResponses();
            if (!dropOnTable) scn.PassDestinyDrawResponses();
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
            assertTrue(scn.LSDecisionAvailable("battle destiny?"));
            scn.LSChooseYes();
            scn.PassDestinyDrawResponses();
            if (!dropOnTable) scn.PassDestinyDrawResponses();
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
            assertEquals(dropOnTable ? 3 : 5, scn.GetDSTotalDestiny());
            assertEquals(dropOnTable ? 3 : 5, scn.GetLSTotalDestiny());
        }
    }

    private void settleDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 35 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected Drop! action decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }
}
