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

    private boolean selectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.DS, "blueprintId"); var ids = scn.GetADParam(scn.DS, "cardId");
        var flags = scn.GetADParam(scn.DS, "selectable"); if (blueprints == null && ids == null) return false;
        int size = blueprints != null ? blueprints.length : ids.length;
        for (int i = 0; i < size; i++) {
            boolean same = blueprints != null ? blueprints[i].equals(card.getBlueprintId(true)) : ids[i].equals(String.valueOf(card.getCardId()));
            if (same) return flags == null || Boolean.parseBoolean(flags[i]);
        }
        return false;
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
        scn.PassResponses("ABOUT_TO_BE_LOST_FROM_TABLE");
        if (scn.GetDecidingPlayer().equals(scn.LS) && scn.GetCurrentDecision().getText().contains("LOST_FROM_TABLE")) scn.LSPass();
    }

    private void recover(String lost, String counterpart) {
        var scn = lossSetup(lost, counterpart, true); var card = scn.GetDSCard(lost); var spot = scn.GetDSCard("spot");
        int force = scn.GetDSForcePileCount(); lose(scn, lost);
        assertTrue("Just-lost response must offer the Interrupt; current=" + scn.GetCurrentDecision().getText() + " decider=" + scn.GetDecidingPlayer() + " lostZone=" + card.getZone(), playable(scn)); scn.DSPlayCard(spot); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, card);
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, spot);
        assertEquals(force, scn.GetDSForcePileCount()); assertEquals(Zone.AT_LOCATION, scn.GetDSCard(counterpart).getZone());
    }

    @Test public void OneBrightSpotActuallyPlacesJustLostGalenInUsedWhenJunoIsAtRemoteSite() { recover("galen", "juno"); }
    @Test public void OneBrightSpotActuallyPlacesJustLostJunoInUsedWhenGalenIsAtRemoteSite() { recover("juno", "galen"); }

    @Test public void OneBrightSpotActuallyRecoversGalenAfterForfeitWithJunoOnTable() {
        var scn = lossSetup("galen", "juno", true); var site = scn.GetDSStartingLocation(); var galen = scn.GetDSCard("galen");
        scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan")); scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(0); scn.PrepareLSDestiny(6); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses(); assertTrue(scn.AwaitingDSAttritionPayment());
        scn.DSChooseCard(galen); scn.PassResponses("ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE");
        if (scn.GetDecidingPlayer().equals(scn.LS) && scn.GetCurrentDecision().getText().contains("FORFEITED_TO_LOST_PILE_FROM_TABLE")) scn.LSPass();
        assertTrue("Forfeit must preserve just-lost recovery window; current=" + scn.GetCurrentDecision().getText(), playable(scn));
        scn.DSPlayCard(scn.GetDSCard("spot")); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, galen);
    }

    @Test public void OneBrightSpotCannotRecoverWithoutCounterpartOnTable() {
        var scn = lossSetup("galen", "juno", false); lose(scn, "galen"); assertFalse("No eligible response in the live loss window", playable(scn)); scn.PassAllResponses();
        assertFalse(playable(scn)); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("galen"));
    }

    @Test public void OneBrightSpotCannotRecoverJunoWithoutGalenOnTable() {
        var scn = lossSetup("juno", "galen", false); lose(scn, "juno");
        assertFalse("Vice versa still requires Galen on table", playable(scn)); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("juno"));
    }

    @Test public void OneBrightSpotCounterpartInHandDoesNotSatisfyOnTableCondition() {
        var scn = lossSetup("galen", "juno", false); scn.MoveCardsToDSHand(scn.GetDSCard("juno")); lose(scn, "galen"); assertFalse("No eligible response in the live loss window", playable(scn)); scn.PassAllResponses();
        assertFalse(playable(scn)); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("galen"));
    }

    @Test public void OneBrightSpotCannotRecoverAnotherJustLostCharacter() {
        var scn = lossSetup("galen", "juno", true); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("officer"));
        lose(scn, "officer"); assertFalse("Wrong just-lost character must not offer this response", playable(scn)); scn.PassAllResponses(); assertFalse(playable(scn));
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("officer"));
    }

    @Test public void OneBrightSpotDoesNotRetrieveAnOldLostGalen() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("spot"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("juno")); scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("galen"));
        scn.SkipToDSTurn(Phase.DEPLOY); assertFalse(playable(scn));
    }

    private VirtualTableScenario damage(boolean junoInBattle, boolean galenInHand, boolean galenOnTable) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(scn.GetDSCard("spot"), scn.GetDSCard("officer"));
        if (galenInHand) scn.MoveCardsToDSHand(scn.GetDSCard("galen"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"));
        if (junoInBattle) scn.MoveCardsToLocation(site, scn.GetDSCard("juno"));
        else { scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("juno")); scn.MoveCardsToLocation(site, scn.GetDSCard("officer")); }
        if (galenOnTable) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("galen"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.PrepareDSDestiny(0); scn.PrepareLSDestiny(6);
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); return scn;
    }

    @Test public void OneBrightSpotLosesGalenFromHandAndActuallySatisfiesBothDamageAndAttrition() {
        var scn = damage(true, true, false); var galen = scn.GetDSCard("galen"); var spot = scn.GetDSCard("spot");
        assertTrue("Expected real damage; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSBattleDamage() > 0); assertTrue("Expected real attrition; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSAttrition() > 0);
        assertTrue(playable(scn)); int force = scn.GetDSForcePileCount(); scn.DSPlayCard(spot);
        if (scn.DSHasCardChoiceAvailable(galen)) { assertFalse("Only Galen may pay the hand cost", selectable(scn, scn.GetDSCard("officer"))); scn.DSChooseCard(galen); } scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, galen);
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, spot);
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("juno").getZone()); assertEquals(force, scn.GetDSForcePileCount());
        if (scn.gameState().isDuringBattle()) { assertEquals(0, scn.GetUnpaidDSBattleDamage()); assertEquals(0, scn.GetUnpaidDSAttrition()); }
        assertFalse(scn.AwaitingDSBattleDamagePayment()); assertFalse(scn.AwaitingDSAttritionPayment());
    }

    @Test public void OneBrightSpotRequiresGalenActuallyInHandForDamagePayment() {
        var scn = damage(true, false, false); assertTrue("Expected real damage; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSBattleDamage() > 0); assertTrue("Expected real attrition; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSAttrition() > 0);
        assertFalse(playable(scn)); assertEquals(Zone.AT_LOCATION, scn.GetDSCard("juno").getZone());
    }

    @Test public void OneBrightSpotGalenOnTableCannotPayHandCardCost() {
        var scn = damage(true, false, true); assertTrue("Expected real damage; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSBattleDamage() > 0); assertTrue("Expected real attrition; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSAttrition() > 0);
        assertFalse(playable(scn)); assertEquals(Zone.AT_LOCATION, scn.GetDSCard("galen").getZone());
    }

    @Test public void OneBrightSpotJunoAtAnotherSiteDoesNotEnableDamageMode() {
        var scn = damage(false, true, false); assertTrue("Expected real damage; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSBattleDamage() > 0); assertTrue("Expected real attrition; current=" + scn.GetCurrentDecision().getText(), scn.GetUnpaidDSAttrition() > 0); assertFalse(playable(scn));
        assertEquals(Zone.HAND, scn.GetDSCard("galen").getZone());
    }

    @Test public void OneBrightSpotCannotPayAwayDamageBeforeDamageResolution() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("spot"), scn.GetDSCard("galen"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("juno"), scn.GetLSCard("obiwan"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation()); assertFalse(playable(scn));
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // Virtual Cards Master List (PC-E1, 2013-04-27) is the latest text; GEMP's "you may lose" wording was a PC data error.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_45")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("If Galen was just lost and Juno Eclipse is on table (or vice versa), place the just-lost character on your Used Pile instead. OR If Juno Eclipse is participating in a battle, may lose Galen from hand to satisfy all battle damage and attrition against you.", text);
        org.junit.Assert.assertFalse(text.contains("you may lose Galen"));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
