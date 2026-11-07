package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_045_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11"); }},
                new HashMap<>() {{ put("spot", "1261_45"); put("galen", "601_81"); put("juno", "601_89"); put("officer", "1_166"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void OneBrightSpotPrintedMetadataMatchesSource() {
        var scn = scenario(); var b = scn.GetDSCard("spot").getBlueprint();
        assertEquals("One Bright Spot", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(4, b.getDestiny(), scn.epsilon); assertEquals(CardSubtype.USED, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.DAGOBAH); add(Icon.LEGACY_BLOCK_6); }});
    }

    private boolean playable(VirtualTableScenario scn) {
        // A live mandatory damage/forfeit selector is not a card-action menu.
        return scn.GetADParam(scn.DS, "actionText") != null && scn.GetADParam(scn.DS, "cardId") != null
                && scn.DSCardPlayAvailable(scn.GetDSCard("spot"));
    }

    private VirtualTableScenario lossSetup(String lost, String counterpart, boolean onTable) {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("spot"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard(lost));
        if (onTable) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard(counterpart));
        scn.SkipToDSTurn(Phase.DEPLOY); return scn;
    }

    private void lose(VirtualTableScenario scn, String name) {
        var card = scn.GetDSCard(name);
        // Use the engine's complete loss pipeline and its real just-lost event, not a direct zone swap.
        scn.DSExecuteAdHocEffect(card, new LoseCardFromTableEffect(new TopLevelGameTextAction(card, card.getCardId()), card));
    }

    private void recover(String lost, String counterpart) {
        var scn = lossSetup(lost, counterpart, true); var card = scn.GetDSCard(lost); var spot = scn.GetDSCard("spot");
        int force = scn.GetDSForcePileCount(); lose(scn, lost);
        assertTrue("Just-lost response must offer the Interrupt", playable(scn)); scn.DSPlayCard(spot); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, card);
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, spot);
        assertEquals(force, scn.GetDSForcePileCount()); assertEquals(Zone.AT_LOCATION, scn.GetDSCard(counterpart).getZone());
    }

    @Test public void OneBrightSpotActuallyPlacesJustLostGalenInUsedWhenJunoIsAtRemoteSite() { recover("galen", "juno"); }
    @Test public void OneBrightSpotActuallyPlacesJustLostJunoInUsedWhenGalenIsAtRemoteSite() { recover("juno", "galen"); }

    @Test public void OneBrightSpotActuallyRecoversGalenAfterForfeitWithJunoOnTable() {
        var scn = lossSetup("galen", "juno", true); var site = scn.GetDSStartingLocation(); var galen = scn.GetDSCard("galen");
        scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"), scn.GetLSCard("han")); scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(0); scn.PrepareLSDestiny(6); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses(); assertTrue(scn.AwaitingDSAttritionPayment());
        scn.DSChooseCard(galen); assertTrue("Forfeit must preserve just-lost recovery window", playable(scn));
        scn.DSPlayCard(scn.GetDSCard("spot")); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, galen);
    }

    @Test public void OneBrightSpotCannotRecoverWithoutCounterpartOnTable() {
        var scn = lossSetup("galen", "juno", false); lose(scn, "galen"); scn.PassAllResponses();
        assertFalse(playable(scn)); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("galen"));
    }

    @Test public void OneBrightSpotCounterpartInHandDoesNotSatisfyOnTableCondition() {
        var scn = lossSetup("galen", "juno", false); scn.MoveCardsToDSHand(scn.GetDSCard("juno")); lose(scn, "galen"); scn.PassAllResponses();
        assertFalse(playable(scn)); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("galen"));
    }

    @Test public void OneBrightSpotCannotRecoverAnotherJustLostCharacter() {
        var scn = lossSetup("galen", "juno", true); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("officer"));
        lose(scn, "officer"); scn.PassAllResponses(); assertFalse(playable(scn));
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("officer"));
    }

    @Test public void OneBrightSpotDoesNotRetrieveAnOldLostGalen() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("spot"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("juno")); scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("galen"));
        scn.SkipToDSTurn(Phase.DEPLOY); assertFalse(playable(scn));
    }

    private VirtualTableScenario damage(boolean junoInBattle, boolean galenInHand, boolean galenOnTable) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(scn.GetDSCard("spot"));
        if (galenInHand) scn.MoveCardsToDSHand(scn.GetDSCard("galen"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"), scn.GetLSCard("han"));
        if (junoInBattle) scn.MoveCardsToLocation(site, scn.GetDSCard("juno"));
        else { scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("juno")); scn.MoveCardsToLocation(site, scn.GetDSCard("officer")); }
        if (galenOnTable) scn.MoveCardsToLocation(site, scn.GetDSCard("galen"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.PrepareDSDestiny(0); scn.PrepareLSDestiny(6);
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); return scn;
    }

    @Test public void OneBrightSpotLosesGalenFromHandAndActuallySatisfiesBothDamageAndAttrition() {
        var scn = damage(true, true, false); var galen = scn.GetDSCard("galen"); var spot = scn.GetDSCard("spot");
        assertTrue(scn.GetUnpaidDSBattleDamage() > 0); assertTrue(scn.GetUnpaidDSAttrition() > 0);
        assertTrue(playable(scn)); int force = scn.GetDSForcePileCount(); scn.DSPlayCard(spot);
        if (scn.DSHasCardChoiceAvailable(galen)) scn.DSChooseCard(galen); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, galen);
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, spot);
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("juno").getZone()); assertEquals(force, scn.GetDSForcePileCount());
        if (scn.gameState().isDuringBattle()) { assertEquals(0, scn.GetUnpaidDSBattleDamage()); assertEquals(0, scn.GetUnpaidDSAttrition()); }
        assertFalse(scn.AwaitingDSBattleDamagePayment()); assertFalse(scn.AwaitingDSAttritionPayment());
    }

    @Test public void OneBrightSpotRequiresGalenActuallyInHandForDamagePayment() {
        var scn = damage(true, false, false); assertTrue(scn.GetUnpaidDSBattleDamage() > 0); assertTrue(scn.GetUnpaidDSAttrition() > 0);
        assertFalse(playable(scn)); assertEquals(Zone.AT_LOCATION, scn.GetDSCard("juno").getZone());
    }

    @Test public void OneBrightSpotGalenOnTableCannotPayHandCardCost() {
        var scn = damage(true, false, true); assertTrue(scn.GetUnpaidDSBattleDamage() > 0); assertTrue(scn.GetUnpaidDSAttrition() > 0);
        assertFalse(playable(scn)); assertEquals(Zone.AT_LOCATION, scn.GetDSCard("galen").getZone());
    }

    @Test public void OneBrightSpotJunoAtAnotherSiteDoesNotEnableDamageMode() {
        var scn = damage(false, true, false); assertTrue(scn.GetUnpaidDSBattleDamage() > 0); assertTrue(scn.GetUnpaidDSAttrition() > 0); assertFalse(playable(scn));
        assertEquals(Zone.HAND, scn.GetDSCard("galen").getZone());
    }

    @Test public void OneBrightSpotCannotPayAwayDamageBeforeDamageResolution() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("spot"), scn.GetDSCard("galen"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("juno"), scn.GetLSCard("obiwan"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation()); assertFalse(playable(scn));
    }
}
