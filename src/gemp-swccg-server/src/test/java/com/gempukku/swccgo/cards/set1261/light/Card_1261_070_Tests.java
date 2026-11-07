package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1261_070_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "obi", "1261_70", "obiSaber", "1_157", "luke", "9_24", "lukeSaber", "9_90")),
                new HashMap<>(Map.of("maul", "14_77", "vader", "1_168", "vaderSaber", "1_324", "trooper", "1_194")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DSStartingLocation("1_295"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void crazyWizardUsesTheExactFinalPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("obi").getBlueprint();
        assertEquals("Obi-Wan, Crazy Wizard", b.getTitle()); assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(5, b.getDeployCost(), scn.epsilon); assertEquals(5, b.getPower(), scn.epsilon);
        assertEquals(6, b.getAbility(), scn.epsilon); assertEquals(9, b.getForfeit(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Rarity.V, b.getRarity()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(List.of(Persona.OBIWAN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REBEL, Icon.WARRIOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private boolean maulCanSwingAgainAfterActualFirstShot(boolean sameSite) {
        var scn = scenario(); var obi = scn.GetLSCard("obi"); var luke = scn.GetLSCard("luke");
        var maul = scn.GetDSCard("maul"); var vader = scn.GetDSCard("vader");
        var battle = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(sameSite ? battle : scn.GetLSStartingLocation(), obi);
        scn.MoveCardsToLocation(battle, luke, maul, vader);
        scn.AttachCardsTo(luke, scn.GetLSCard("lukeSaber"));
        scn.AttachCardsTo(vader, scn.GetDSCard("vaderSaber"));
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(battle);
        var modifiers = scn.game().getModifiersQuerying();
        assertTrue(scn.IsBattleStarted());
        assertEquals(sameSite, modifiers.mayFireOnlyOncePerBattle(scn.gameState(), scn.GetDSCard("vaderSaber")));
        assertFalse(modifiers.mayFireOnlyOncePerBattle(scn.gameState(), scn.GetLSCard("lukeSaber")));
        assertTrue(scn.DSCardActionAvailable(maul, "Fire"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(0), scn.GetDSDestiny(1));
        scn.DSUseCardAction(maul, "Fire");
        if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
        scn.PassAllResponses(); scn.PassWeaponFireWithDestinyDraw(2);
        assertFalse(luke.isHit());
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        return scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Double-Bladed Lightsaber"));
    }

    @Test public void crazyWizardAllowsMaulsFirstActualSwingButStopsTheSecondHere() {
        assertFalse(maulCanSwingAgainAfterActualFirstShot(true));
    }

    @Test public void maulCanStillSwingTwiceInAnActualBattleAtAnotherSite() {
        assertTrue(maulCanSwingAgainAfterActualFirstShot(false));
    }

    private void attrition(int amount, boolean immune) {
        var scn = scenario(); var obi = scn.GetLSCard("obi"); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, obi, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("lukeSaber"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(amount - 1);
        for (int i = 0; i < 4; i++) { scn.SkipToEndOfPowerSegment(true); scn.PassDestinyDrawResponses(); }
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED"); scn.PassDamageSegmentActions();
        assertEquals(amount, scn.GetUnpaidLSAttrition()); assertEquals(0, scn.GetUnpaidLSBattleDamage());
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) { scn.LSChooseCards(); assertAtLocation(site, obi); }
        else { scn.LSPayAttritionFromCardInPlay(obi); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, obi); }
    }

    @Test public void crazyWizardSurvivesActualAttritionFour() { attrition(4, true); }
    @Test public void crazyWizardMustForfeitAtActualAttritionFive() { attrition(5, false); }
}
