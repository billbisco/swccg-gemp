package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1262_022_Tests {
    private static final String TRAINING = "Use 1 Force to protect training destiny";
    private static final String PREVENT = "Place out of play to prevent Luke";

    private VirtualTableScenario fixture() {
        var ls = new HashMap<>(Map.of("peace", "1262_22", "hut", "4_89", "jungle", "4_86",
                "testOne", "4_77", "testFive", "4_78", "luke", "9_24", "han", "1_11"));
        var ds = new HashMap<>(Map.of("broken", "4_118", "vader", "1_168", "saber", "1_324"));
        var scn = new VirtualTableScenario(ls, ds, 40, 40, StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void atPeaceMatchesTheFinalLegacyPrintingWithoutEarlierAlterImmunity() {
        var scn = fixture(); var b = scn.GetLSCard("peace").getBlueprint();
        assertEquals(Title.At_Peace, b.getTitle()); assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertFalse(b.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.DEPLOYS_ON_LOCATION)));
    }

    @Test public void onlyYodasHutIsALegalDeployTarget() {
        var scn = fixture(); var peace = scn.GetLSCard("peace"); var hut = scn.GetLSCard("hut");
        scn.MoveLocationToTable(scn.GetLSCard("jungle"));
        scn.MoveCardsToLSHand(peace); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(peace));
        scn.MoveLocationToTable(hut);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(peace);
        if (scn.LSHasCardChoiceAvailable(hut)) scn.LSChooseCard(hut);
        scn.PassAllResponses();
        assertEquals(hut, peace.getAttachedTo());
    }

    @Test public void paidProtectionSuppressesOnlyBrokenConcentrationsTrainingModifierForOneTurn() {
        var scn = fixture(); var peace = scn.GetLSCard("peace"); var hut = scn.GetLSCard("hut");
        var luke = scn.GetLSCard("luke"); var test = scn.GetLSCard("testOne"); var broken = scn.GetDSCard("broken");
        scn.MoveLocationToTable(hut); scn.AttachCardsTo(hut, peace, test);
        scn.MoveCardsToLocation(hut, luke);
        test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        scn.MoveCardsToDSSideOfTable(broken);
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(2);
        var query = scn.game().getModifiersQuerying();
        float unprotected = query.getTotalTrainingDestiny(scn.gameState(), test, 6);
        assertTrue(unprotected < 6);
        int before = scn.GetLSForcePileCount();
        scn.LSUseCardAction(peace, TRAINING);
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertEquals(before - 1, scn.GetLSForcePileCount());
        assertEquals(6, query.getTotalTrainingDestiny(scn.gameState(), test, 6), scn.epsilon);
        assertTrue(scn.IsCardActive(broken));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(unprotected, query.getTotalTrainingDestiny(scn.gameState(), test, 6), scn.epsilon);
    }

    private void swing(boolean completed, boolean accept) {
        var scn = fixture(); var peace = scn.GetLSCard("peace"); var hut = scn.GetLSCard("hut");
        var luke = scn.GetLSCard("luke"); var test = scn.GetLSCard("testFive");
        var vader = scn.GetDSCard("vader"); var saber = scn.GetDSCard("saber"); var site = scn.GetDSStartingLocation();
        scn.MoveLocationToTable(hut); scn.AttachCardsTo(hut, peace);
        scn.MoveCardsToLocation(site, luke, vader); scn.AttachCardsTo(vader, saber);
        if (completed) {
            scn.AttachCardsTo(luke, test);
            test.setJediTestStatus(JediTestStatus.COMPLETED);
            test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
            scn.game().getModifiersQuerying().completedJediTest(test, luke);
        }
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(5);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(6), scn.GetDSDestiny(7));
        scn.DSUseCardAction(saber, "Fire");
        if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
        for (int i = 0; i < 2; i++) scn.PassDestinyDrawResponses();
        assertEquals(completed, scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(PREVENT)));
        if (accept) scn.LSUseCardAction(peace, PREVENT);
        scn.PassAllResponses();
        assertEquals(!accept, luke.isHit());
        assertEquals(accept ? Zone.OUT_OF_PLAY : Zone.ATTACHED, peace.getZone());
    }

    @Test public void atPeacePreventsARealLightsaberHitAfterTestFive() { swing(true, true); }
    @Test public void theSameHitOccursIfProtectionIsDeclined() { swing(true, false); }
    @Test public void atPeaceCannotProtectLukeBeforeTestFive() { swing(false, false); }
}
